package com.hecheng.wechat.openplatform.controller.mvc;

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
import org.springframework.web.servlet.ModelAndView;

import com.hecheng.wechat.openplatform.annotation.ObjectConvertAnno;
import com.hecheng.wechat.openplatform.common.utils.CookieUtils;
import com.hecheng.wechat.openplatform.common.utils.SHACoder;
import com.hecheng.wechat.openplatform.controller.BaseController;
import com.hecheng.wechat.openplatform.entity.ThirdPartUserEntity;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.service.ThirdPartUserService;
import com.hecheng.wechat.openplatform.service.UserService;
import com.hecheng.wechat.openplatform.service.WeChatService;

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
  @Value("${weChat.oAuthCallbackDomain}")
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

  @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
  @ApiOperation(value = "跳转到关注公众号", notes = "跳转到关注公众号")
  public ModelAndView gotoBindTelephone(@ApiIgnore @ObjectConvertAnno UserEntity user,
      String target, String gatewayMac) {

    try {
      Validate.notNull(user, "你还未登录，请登录");
      ModelAndView mav = new ModelAndView();
      // 跳转路径
      mav.setViewName("/weChat/views/subscribe/subscribe");
      return mav;
    } catch (Exception e) {
      return this.buildHttpReslutForException(e, null);
    }
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
    if (user == null) {
      // 去单点登陆
      return weChatAuthorize(gotoUrl);
    } else {
      try {
        // 如果是登陆用户直接去菜单
        packageMenuView(response, request, gotoUrl, user);
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
      String gotoUrl, UserEntity user) throws IOException {
    // 如果用户的电话号码不为空则直接去对应菜单，否则跳转到用户绑定电话号码的页面
    String telephone = user.getTelephone();

    if (StringUtils.isBlank(telephone)) {
      gotoUrl = "bindTelephone";
    }
    LOG.info("gotoUrl============:" + gotoUrl);
    // 初始页面
    String redirect_url = "/weChat/views/invalid.html";//
    if (StringUtils.isBlank(gotoUrl)) {
      redirect_url = "/weChat/views/invalid.html";
    }
    if ("bindTelephone".equals(gotoUrl)) {// 绑定电话号码
      redirect_url = "/wx/user/gotoBindTelephone";
    } else if ("home".equals(gotoUrl)) {// 首页
      redirect_url = "/wx/gateway/findAllGateway";
    } else if ("updateTelephone".equals(gotoUrl)) {// 更换手机号
      redirect_url = "/wx/user/gotoUpdateTelephone";
    } else if ("setting".equals(gotoUrl)) {
      redirect_url = "/wx/setting/gotoSetting";// 前往设置
    } else if ("demo".equals(gotoUrl)) {
      redirect_url = "/wx/wxsso/demo";
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
    packageMenuView(response, request, gotoUrl, thirdPartUser.getUser());

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
   * 本地模拟 微信登陆demo（正式环境需要去掉）
   * 
   * @param request
   * @param response
   * @param openId
   * @return
   */
  @ApiOperation(value = "本地测试  登陆User ThirdPartUser", notes = "本地测试  登陆User ThirdPartUser")
  @RequestMapping(value = "testlogin")
  public ModelAndView testlogin(HttpServletRequest request, HttpServletResponse response,
      String userId, String thirdPartUserId) {
    if (StringUtils.isNotBlank(userId)) {
      UserEntity user = userService.findById(userId);
      CookieUtils.weChatWriteUserCookie(request, response, user);
    }
    if (StringUtils.isNotBlank(thirdPartUserId)) {
      ThirdPartUserEntity user = thirdPartUserService.findById(thirdPartUserId);
      CookieUtils.weChatWriteThirdCookie(request, response, user);
    }
    return new ModelAndView("/weChat/views/loginSuccess");
  }

  @RequestMapping(value = "gotoUrl")
  public ModelAndView gotoUrl(HttpServletRequest request, HttpServletResponse response,
      String gotoUrl) {
    return new ModelAndView(gotoUrl);
  }

  /**
   * 微信jsdk demo（正式环境需要去掉）
   * 
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/demo")
  public ModelAndView share(HttpServletRequest request, HttpServletResponse response) {
    ModelAndView model = new ModelAndView();
    // 当前链接
    String currentUrl = request.getRequestURL() + "";
    if (StringUtils.isNotBlank(request.getQueryString())) {
      currentUrl += "?" + request.getQueryString();
    }
    Map<String, Object> jsSignature = weChatService.getJsSignature(currentUrl);
    model.addObject("jsSignature", jsSignature);
    model.setViewName("/weChat/views/demo/demo");
    return model;
  }
}
