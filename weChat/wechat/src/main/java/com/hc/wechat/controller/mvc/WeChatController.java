package com.hc.wechat.controller.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hc.wechat.annotation.ObjectConvertAnno;
import com.hc.wechat.common.utils.CookieUtils;
import com.hc.wechat.common.utils.SHACoder;
import com.hc.wechat.controller.BaseController;
import com.hc.wechat.entity.ThirdPartUserEntity;
import com.hc.wechat.entity.UserEntity;
import com.hc.wechat.pojo.FileUploadPojo;
import com.hc.wechat.service.ThirdPartUserService;
import com.hc.wechat.service.UserService;
import com.hc.wechat.service.WeChatService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author 作者 hc
 */
@Api(value = "微信sso controller")
@Controller
@RequestMapping("/wx/wxsso")
public class WeChatController extends BaseController {
  private static final Logger LOG = LoggerFactory.getLogger(WeChatController.class);

  @Autowired
  private WeChatService weChatService;
  @Autowired
  private UserService userService;
  @Autowired
  private ThirdPartUserService thirdPartUserService;
  /** 微信appid */
  @Value("${weChat.appId}")
  private String WECHAT_APPID;
  /** 微信appsecret */
  @Value("${weChat.secret}")
  private String WECHAT_SECRET;
  @Value("${weChat.appDns}")
  private String WECHAT_APPDNS;
  @Value("${weChat.appToke}")
  private String WECHAT_TOKE;

  /**
   * 引导用户进入授权页面同意授权，获取code 1 menu_callback 同意授权后执行 2 gotoUrl menu_callback 参数
   * 
   * @param gotoUrl
   * @return
   */
  private ModelAndView weChatAuthorize(String gotoUrl) {
    String appId = WECHAT_APPID;
    // 获取code
    String url = "https://open.weixin.qq.com/connect/oauth2/authorize" + "?appid=" + appId
        + "&redirect_uri=http%3a%2f%2f" + WECHAT_APPDNS
        + "%2fwx%2fwxsso%2fmenu_callback%3fgotoUrl%3d" + gotoUrl
        + "&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect";
    return new ModelAndView("redirect:" + url);
  }

  /**
   * 判断是否是微信扫码
   * 
   * @param request
   * @return
   */
  protected boolean isWeChat(HttpServletRequest request) {
    String userAgent = request.getHeader("user-agent");
    // 微信扫码
    if (userAgent.contains("MicroMessenger")) {
      return true;
    }
    return false;

  }

  /**
   * 微信菜单
   * 
   * @param request
   * @param response
   * @return
   */
  @ApiOperation(value = "微信菜单跳转", notes = "微信菜单跳转：gotoUrl 要跳转的位置")
  @RequestMapping(value = "/go_menu", method = RequestMethod.GET)
  public ModelAndView goMenu(HttpServletRequest request, HttpServletResponse response,
      @ApiIgnore @ObjectConvertAnno UserEntity user) {
    String gotoUrl = request.getParameter("gotoUrl");
    // 1.如果用户未单点登录，或者需要先微信单点登录的接口 ，就进行单点登陆
    // TODO 占时以中文代替 以后设置后替换
    if (user == null || "share".equals(gotoUrl) || "xxxxx".equals(gotoUrl)
        || "xxxxx".equals(gotoUrl)) {
      // 去单点登陆
      return weChatAuthorize(gotoUrl);
    } else {
      try {
        // 如果是登陆用户直接去菜单
        packageMenuView(response, request, gotoUrl);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }
  }

  /**
   * 封装菜单调整视图对象
   *
   * @return 视图对象
   * @throws IOException
   */
  // TODO 各自负责的人 自己填写
  private void packageMenuView(HttpServletResponse response, HttpServletRequest request,
      String gotoUrl) throws IOException {
    // 初始页面
    LOG.info("gotoUrl============:" + gotoUrl);
    String redirect_url = "/invalid.html";//
    if (StringUtils.isBlank(gotoUrl)) {
      redirect_url = "/invalid.html";
    }
    if ("share".equals(gotoUrl)) {// 分享
      redirect_url = "/wx/wxsso/share";
    } else if ("xxxxx".equals(gotoUrl)) {//
      redirect_url = "";
    } else if ("xxxxx".equals(gotoUrl)) {//
      redirect_url = "";
    }
    // 回到请求登录的方法
    StringBuilder strUrl = new StringBuilder();
    strUrl.append("<script>location.replace('" + redirect_url + "?callback=true");
    strUrl.append("')</script>");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    response.setContentType("text/html;charset=UTF-8");
    out.println(strUrl.toString());
  }

  /**
   * 点击菜单回调页面
   * 
   * @param request
   * @param response
   * @return
   * @throws IOException
   */
  @ApiOperation(value = "点击菜单回调页面", notes = "点击菜单回调页面：gotoUrl 要跳转的位置")
  @RequestMapping("menu_callback")
  public ModelAndView menuCallback(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String gotoUrl = request.getParameter("gotoUrl");
    Validate.notBlank(gotoUrl, "跳转页面参数无效");
    // 微信API返回的code,获得当前登录者第三方账号对象
    String code = request.getParameter("code");
    Map<String, Object> wxssomap = wxSso(request, response, code);
    ThirdPartUserEntity thirdPartUser = (ThirdPartUserEntity) wxssomap.get("thirdPartUser");
    Validate.notNull(thirdPartUser, "微信登录失败");
    packageMenuView(response, request, gotoUrl);
    return null;
  }


  private Map<String, Object> wxSso(HttpServletRequest request, HttpServletResponse response,
      String code) {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    // 1.获取网页授权 openid和access_token
    LOG.info("====wxSso====获取网页授权 openid和access_token==code:" + code);
    Map<String, Object> tokenMap =
        weChatService.getWebPageAccessToken(WECHAT_APPID, WECHAT_SECRET, code);
    LOG.info("====wxSso====token:" + tokenMap);
    String openId = (String) tokenMap.get("openid");
    String access_token = (String) tokenMap.get("access_token");
    ThirdPartUserEntity thirdPartUser = null;
    thirdPartUser = weChatService.getThirdPartUserEntity(access_token, openId);
    UserEntity user = (UserEntity) thirdPartUser.getUser();
    if (user != null) {
      // ThirdPartUser对象写入会话cookies
      CookieUtils.weChatWriteThirdCookie(request, response, thirdPartUser);
      // user对象写入会话cookies
      CookieUtils.weChatWriteUserCookie(request, response, user);
    }
    returnMap.put("thirdPartUser", thirdPartUser);
    return returnMap;
  }

  /**
   * 确认请求来自微信服务器
   */
  @RequestMapping(value = "weixinevent", method = RequestMethod.GET)
  public ModelAndView doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // 微信加密签名
    String signature = request.getParameter("signature");
    // 时间戳
    String timestamp = request.getParameter("timestamp");
    // 随机数
    String nonce = request.getParameter("nonce");
    // 随机字符串
    String echostr = request.getParameter("echostr");
    String token = WECHAT_TOKE;
    String signature2 = null;
    try {
      String signatureValue = nonce + timestamp + token;
      signature2 = SHACoder.encryptShaHex(signatureValue, null);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
    PrintWriter out = response.getWriter();
    // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
    if (signature != null && signature2 != null) {
      out.print(echostr);
    }
    out.close();
    out = null;
    return null;
  }

  /**
   * 处理微信服务器发来的消息
   */
  @RequestMapping(value = "weixinevent", method = RequestMethod.POST)
  public ModelAndView doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    // 调用核心业务类接收消息、处理消息
    String respMessage = processRequest(request, response);
    // 响应消息
    PrintWriter out = response.getWriter();
    out.print(respMessage);
    out.close();
    return null;
  }

  private String processRequest(HttpServletRequest request, HttpServletResponse response) {
    // 默认返回的文本消息内容
    String respContent = "请求处理异常，请稍候尝试！";
    try {
      // xml请求解析
      Map<String, String> requestMap = parseXml(request);

      // 发送方帐号（open_id）
      String fromUserName = requestMap.get("FromUserName");
      // 公众帐号
      String toUserName = requestMap.get("ToUserName");
      // 消息类型
      String msgType = requestMap.get("MsgType");
      // 事件推送
      if (msgType.equals("event")) {
        // 事件类型
        String eventType = requestMap.get("Event");
        String eventKey = requestMap.get("EventKey");
        // 自定义客服
        if (eventType.equals("CLICK") && eventKey.equals("M1001_SERVER")) {
          respContent = "<xml><ToUserName>" + fromUserName + "</ToUserName><FromUserName>"
              + toUserName
              + "</FromUserName><CreateTime>12345678</CreateTime><MsgType>text</MsgType><Content>"
              + "您好，请点击左下角小键盘，留下你的问题。" + "</Content></xml>";
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
    return respContent;
  }

  /**
   * 解析微信发来的请求(XML)
   * 
   * @param request
   * @return
   * @throws Exception
   */
  private static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
    // 将解析结果存储在HashMap中
    Map<String, String> map = new HashMap<String, String>();

    // 从request中取得输入流
    InputStream inputStream = request.getInputStream();
    // 读取输入流
    SAXReader reader = new SAXReader();
    Document document = reader.read(inputStream);
    // 得到xml根元素
    Element root = document.getRootElement();
    // 得到根元素的所有子节点
    @SuppressWarnings("unchecked")
    List<Element> elementList = root.elements();
    // 遍历所有子节点
    for (Element e : elementList) {
      map.put(e.getName(), e.getText());
    }
    // 释放资源
    inputStream.close();
    inputStream = null;
    return map;
  }

  /**
   * 提供本地测试（正式发布时去掉）
   * 
   * @param request
   * @param response
   * @param userId
   * @param thirdPartUserId
   * @param gotoUrl
   * @return
   */
  @ApiOperation(value = "本地测试  登陆User ThirdPartUser", notes = "本地测试  登陆User ThirdPartUser")
  @RequestMapping(value = "testlogin")
  public ModelAndView testlogin(HttpServletRequest request, HttpServletResponse response,
      String userId, String thirdPartUserId, String gotoUrl) {
    if (StringUtils.isNotBlank(userId)) {
      UserEntity user = userService.findById(userId);
      CookieUtils.weChatWriteUserCookie(request, response, user);
    }
    if (StringUtils.isNotBlank(thirdPartUserId)) {
      ThirdPartUserEntity user = thirdPartUserService.findById(userId);
      CookieUtils.weChatWriteThirdCookie(request, response, user);
    }
    if (StringUtils.isNotBlank(gotoUrl)) {
      ModelAndView model = new ModelAndView(gotoUrl);
      model.addObject("twoCode", "2IVNz2");
      return model;
    }
    return new ModelAndView();
  }

  /**
   * 提供本地测试发送微信消息使用（正式发布时去掉）
   * 
   * @param request
   * @param response
   * @param openId
   * @return
   */
  @RequestMapping(value = "/testSend")
  @ResponseBody
  public String testSend(HttpServletRequest request, HttpServletResponse response, String openId) {
    // ThirdPartUserEntity thirdPartUser = thirdPartUserService.findByOpenIdAndPlatFormType(openId,
    // PlatFormType.PLAT_FORM_TYPE_WEIXIN);
    // String content = "张三||18398601252||户籍办理||审核通过";
    // String wxTemplateId = "m9j458S65WpYPFlLA_vDhg9HExP6ALEV1Po447kJ4RA";
    // WxTemplate wxTemplate = new WxTransactNotice(null, thirdPartUser.getUser(), null, content,
    // new Date(), wxTemplateId);
    // String isSuccess = MsgTools.getInstance().sendMessage(MessageType.MSGTYPE_WEIXIN,
    // wxTemplate);
    // return isSuccess;
    return null;
  }

  @ApiOperation(value = "获取微信暂存图片并上传至图片服务", notes = "获取微信暂存图片并上传至图片服务，返回数据为图片服务返回的值")
  @RequestMapping(value = "downloadWeChatImage")
  @ResponseBody
  public FileUploadPojo downloadWeChatImage(String serverId) {
    FileUploadPojo file = weChatService.downloadWeChatImage(serverId);
    return file;
  }

  /**
   * 微信js dk demo (正式发布时去掉)
   * 
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/weChatDemo")
  public ModelAndView share(HttpServletRequest request, HttpServletResponse response) {
    ModelAndView model = new ModelAndView();
    // 当前链接
    String currentUrl = request.getRequestURL() + "";
    if (StringUtils.isNotBlank(request.getQueryString())) {
      currentUrl += "?" + request.getQueryString();
    }
    Map<String, Object> jsSignature = weChatService.getJsSignature(currentUrl);
    model.addObject("jsSignature", jsSignature);
    model.setViewName("/weChat/views/weChatDemo/weChatDemo");
    return model;
  }
}
