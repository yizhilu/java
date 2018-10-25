package com.hecheng.wechat.openplatform.controller.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hecheng.wechat.openplatform.common.utils.SHACoder;
import com.hecheng.wechat.openplatform.common.weixin.AesException;
import com.hecheng.wechat.openplatform.common.weixin.ComponentReceiveXML;
import com.hecheng.wechat.openplatform.common.weixin.WXBizMsgCrypt;
import com.hecheng.wechat.openplatform.common.weixin.XMLConverUtil;
import com.hecheng.wechat.openplatform.controller.BaseController;
import com.hecheng.wechat.openplatform.entity.WeChatCacheEntity;
import com.hecheng.wechat.openplatform.service.WeChatCacheService;
import com.hecheng.wechat.openplatform.service.WeChatOpenPlatformService;

import io.swagger.annotations.Api;

/**
 * 微信开放平台
 * 
 * @author 作者 hc
 */
@Api(value = "微信sso controller")
@Controller
@RequestMapping("/wx/open")
public class WeChatOpenPlatformController extends BaseController {
  private static final Logger LOG = LoggerFactory.getLogger(WeChatOpenPlatformController.class);
  // 第三方平台 APPID
  private final static String COMPONENT_APPID = "wxf853922397c48434";
  // 第三方平台 秘钥
  private final static String COMPONENT_APPSECRET = "b77150ef939bc849a4a78c0276efe915";
  // 开发者 设置的 key
  private final static String COMPONENT_ENCODINGAESKEY =
      "3XpENOes3wOWYk0cLrjefHuEF3RLB6IIj2nidm3VGtU";
  // 开发者 设置的 token
  private final static String COMPONENT_TOKEN = "vanda-tec";
  private final static String WECHAT_APPDNS = "open.91-ec.com";
  @Autowired
  private WeChatCacheService weChatCacheService;
  @Autowired
  private WeChatOpenPlatformService weChatOpenPlatformService;

  /**
   * <pre>
   *  授权事件接收URL的处理
    *   1、微信服务器发送给服务自身的事件推送（如取消授权通知，Ticket推送等）
    *   此时，消息XML体中没有ToUserName字段，而是AppId字段，即公众号服务的AppId。
    *   这种系统事件推送通知（现在包括推送component_verify_ticket协议和推送取消
    *   授权通知），服务开发者收到后也需进行解密，接收到后只需直接返回字符串“success”。
    * 授权事件接受url 每隔10分钟 获取微信服务器推送ticket 接收后需要解密 接收到后 必须返回字符串success
    * </pre>
   * 
   * @param request
   * @param response
   * @throws Exception
   */
  @RequestMapping("/sysMsg")
  public void sysMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
    // 微信加密签名
    String signature = request.getParameter("signature");
    // 时间戳
    String timestamp = request.getParameter("timestamp");
    // 随机数
    String nonce = request.getParameter("nonce");
    // 微信消息加密后的内容
    String msgSignature = request.getParameter("msg_signature");
    LOG.info(String.format("COMPONENT_TOKEN=%s,signature=%s,timestamp=%s,nonce=%s,msgSignature=%s",
        COMPONENT_TOKEN, signature, timestamp, nonce, msgSignature));
    // 判断消息是否空 （微信推送给第三方开放平台的消息一定是加过密的，无消息加密无法解密消息）
    if (StringUtils.isBlank(msgSignature)) {
      return;
    }
    boolean isValid = checkSignature(COMPONENT_TOKEN, signature, timestamp, nonce);
    LOG.info(String.format("isValid=%s", isValid));
    // 如果通过验证 储存
    if (isValid) {
      StringBuilder sb = new StringBuilder();
      BufferedReader in = request.getReader();
      String line;
      while ((line = in.readLine()) != null) {
        sb.append(line);
      }
      String xml = sb.toString();
      LOG.info("/wx/open/sysMsg加密的Xml=" + xml);
      WXBizMsgCrypt pc =
          new WXBizMsgCrypt(COMPONENT_TOKEN, COMPONENT_ENCODINGAESKEY, COMPONENT_APPID);
      xml = pc.decryptMsg(msgSignature, timestamp, nonce, xml);
      LOG.info("/wx/open/sysMsg解密后的 Xml=" + xml);
      ComponentReceiveXML com = XMLConverUtil.convertToObject(ComponentReceiveXML.class, xml);
      LOG.info("ComponentReceiveXML=" + com.toString());
      WeChatCacheEntity componentVerifyTicket = new WeChatCacheEntity();
      componentVerifyTicket.setAppId(com.getAppid());
      componentVerifyTicket.setCreateTime(new Date());
      componentVerifyTicket.setExpires(10 * 60 * 60);
      componentVerifyTicket.setName(WeChatCacheEntity.COMPONENT_VERIFY_TICKET);
      componentVerifyTicket.setJson(JSON.toJSONString(com));
      weChatCacheService.update(componentVerifyTicket);
    }
    PrintWriter pw = response.getWriter();
    pw.write("success");
    pw.flush();
  }



  /**
   * 消息与事件接收URL(第三方平台处理微信发送的消息)
   * 
   * @param request
   * @param response
   * @param appId
   * @throws IOException
   * @throws AesException
   * @throws DocumentException
   */
  @ResponseBody
  @RequestMapping(value = "{appid}/event", method = RequestMethod.POST)
  public void event(HttpServletRequest request, HttpServletResponse response,
      @PathVariable(value = "appid") String appId)// springMVC 获取地址里面的参数信息
      throws IOException, AesException, DocumentException {
    String nonce = request.getParameter("nonce");
    String timestamp = request.getParameter("timestamp");
    String msgSignature = request.getParameter("msg_signature");
    LOG.info(String.format("COMPONENT_TOKEN=%s,timestamp=%s,nonce=%s,msgSignature=%s",
        COMPONENT_TOKEN, timestamp, nonce, msgSignature));
    // 微信推送给第三方开放平台的消息一定是加过密的，无消息加密无法解密消息
    if (StringUtils.isBlank(msgSignature)) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    BufferedReader in = request.getReader();
    String line;
    while ((line = in.readLine()) != null) {
      sb.append(line);
    }
    in.close();
    String xml = sb.toString();
    LOG.info("读取加密的XML为：" + xml);
    // 微信自动化测试的专用测试公众账号
    if (appId.equals("wx570bc396a51b8ff8")) {
      WXBizMsgCrypt pc =
          new WXBizMsgCrypt(COMPONENT_TOKEN, COMPONENT_ENCODINGAESKEY, COMPONENT_APPID);
      try {
        xml = pc.decryptMsg(msgSignature, timestamp, nonce, xml);// 将xml进行加密后，和sign签名码进行对比，如果正确则返回xml
        LOG.info("解密后:" + xml);
        Document doc = DocumentHelper.parseText(xml);
        Element rootElt = doc.getRootElement();
        String msgType = rootElt.elementText("MsgType");
        String toUserName = rootElt.elementText("ToUserName");
        String fromUserName = rootElt.elementText("FromUserName");
        // 返回类型值，做一下区分
        if (msgType.equals("event")) {
          String event = rootElt.elementText("Event");
          // 返回时， 将发送人和接收人 调换一下即可
          replyEventMessage(request, response, event, fromUserName, toUserName);
        }

        if (msgType.equals("text")) { // 标示文本消息，
          String content = rootElt.elementText("Content");
          // 返回时， 将发送人和接收人 调换一下即可
          processTextMessage(request, response, content, fromUserName, toUserName);// 用文本消息去拼接字符串。微信规定
        }
      } catch (AesException e) {
        // 应该做容错处理
        LOG.error("错误码为: " + e.getCode() + "错误信息为: " + e.getMessage(), e);
      }
    } else {
      LOG.info("appid=" + appId + ",正确的值为：wx570bc396a51b8ff8");
      LOG.info("检测不是微信开放平台测试账号,发布程序终止.");
    }

  }



  /**
   * 授权注册页面扫码授权
   * 
   * @param preAuthCode 预授权码
   * @param redirectUri 回调URI
   * @return
   */
  @RequestMapping(value = "componentLoginPage")
  public ModelAndView componentLoginPage() {
    String preAuthCode =
        weChatOpenPlatformService.getPreAuthCode(COMPONENT_APPID, COMPONENT_APPSECRET);
    String redirectUri = "http%3a%2f%2f" + WECHAT_APPDNS + "%2fwx%2fopen%2fqueryAuth";
    // 获取code
    String url = String.format(
        "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=%s&pre_auth_code=%s&redirect_uri=%s&auth_type=%s",
        COMPONENT_APPID, preAuthCode, redirectUri, 1);
    ModelAndView model = new ModelAndView();
    model.setViewName("/weChat/views/componentLoginPage/componentLoginPage");
    model.addObject("url", url);
    return model;
  }

  /**
   * 根据auth_code查询授权信息
   * 
   * @param authCode 授权成功时获得的授权码
   * @param expiresIn 存活时间
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/queryAuth")
  public String queryAuth(@RequestParam("auth_code") String authCode,
      @RequestParam("expires_in") String expiresIn) {
    LOG.info("auth_code={},expires_in={}", authCode, expiresIn);
    weChatOpenPlatformService.getApiQueryAuth(COMPONENT_APPID, COMPONENT_APPSECRET, authCode);
    return "success";
  }

  /**
   * 验证
   * 
   * @param componentToken
   * @param signature
   * @param timestamp
   * @param nonce
   * @return
   */
  private boolean checkSignature(String componentToken, String signature, String timestamp,
      String nonce) {
    boolean flag = false;
    if (StringUtils.isNotBlank(signature) && StringUtils.isNotBlank(timestamp)
        && StringUtils.isNotBlank(nonce)) {
      String signatureValue = "";
      String[] arr = new String[] {componentToken, timestamp, nonce};
      Arrays.sort(arr);
      for (String temp : arr) {
        signatureValue += temp;
      }
      String signatureVerify = null;
      try {
        signatureVerify = SHACoder.encryptShaHex(signatureValue, null);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      if (signature.equals(signatureVerify)) {
        flag = true;
      }
    }
    return flag;
  }

  /**
   * 方法描述: 类型为event的时候，拼接
   * 
   * @param request
   * @param response
   * @param event
   * @param toUserName 发送接收人
   * @param fromUserName 发送人
   */
  private void replyEventMessage(HttpServletRequest request, HttpServletResponse response,
      String event, String toUserName, String fromUserName) throws DocumentException, IOException {
    String content = event + "from_callback";
    replyTextMessage(request, response, content, toUserName, fromUserName);
  }


  /**
   * 微信模推送给第三方平台方：文本消息，其中Content字段的内容固定为：TESTCOMPONENT_MSG_TYPE_TEXT，
   * 第三方平台方立马回应文本消息并最终触达粉丝：Content必须固定为：TESTCOMPONENT_MSG_TYPE_TEXT_callback
   * 
   * @param content 文本
   * @param toUserName 发送接收人
   * @param fromUserName 发送人
   */
  private void processTextMessage(HttpServletRequest request, HttpServletResponse response,
      String content, String toUserName, String fromUserName)
      throws IOException, DocumentException {
    if ("TESTCOMPONENT_MSG_TYPE_TEXT".equals(content)) {
      String returnContent = content + "_callback";
      replyTextMessage(request, response, returnContent, toUserName, fromUserName);
    } else if (StringUtils.startsWithIgnoreCase(content, "QUERY_AUTH_CODE")) {
      // 需在5秒内返回空串表明暂时不回复，然后再立即使用客服消息接口发送消息回复粉丝
      response.getWriter().print("");
      LOG.info("content:" + content + " content[1]:" + content.split(":")[1] + " fromUserName:"
          + fromUserName + " toUserName:" + toUserName);
      // 接下来客服API再回复一次消息
      // 此时 content字符的内容为是 QUERY_AUTH_CODE:adsg5qe4q35
      replyApiTextMessage(content.split(":")[1], toUserName);
    }
  }



  /**
   * 方法描述: 直接返回给微信开放平台
   * 
   * @param request
   * @param response
   * @param content 文本
   * @param toUserName 发送接收人
   * @param fromUserName 发送人
   */
  private void replyTextMessage(HttpServletRequest request, HttpServletResponse response,
      String content, String toUserName, String fromUserName)
      throws DocumentException, IOException {
    Long createTime = System.currentTimeMillis() / 1000;
    StringBuffer sb = new StringBuffer(512);
    sb.append("<xml>");
    sb.append("<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>");
    sb.append("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
    sb.append("<CreateTime>" + createTime.toString() + "</CreateTime>");
    sb.append("<MsgType><![CDATA[text]]></MsgType>");
    sb.append("<Content><![CDATA[" + content + "]]></Content>");
    sb.append("</xml>");
    String replyMsg = sb.toString();
    // 千万别加密
    LOG.info("确定发送的XML为：" + replyMsg);
    returnJSON(replyMsg, response);
  }

  /**
   * 方法描述: 调用客服回复消息给粉丝
   * 
   * @param auth_code
   * @param fromUserName
   * @throws DocumentException
   * @throws IOException
   * @return void
   */
  private void replyApiTextMessage(String auth_code, String fromUserName)
      throws DocumentException, IOException {
    // 得到微信授权成功的消息后，应该立刻进行处理！！相关信息只会在首次授权的时候推送过来
    String authorizerAccessToken =
        weChatOpenPlatformService.getApiQueryAuth(COMPONENT_APPID, COMPONENT_APPSECRET, auth_code);
    JSONObject message = new JSONObject();
    message.put("touser", fromUserName);
    message.put("msgtype", "text");
    JSONObject text = new JSONObject();
    text.put("content", auth_code + "_from_api");
    message.put("text", text);
    String result = weChatOpenPlatformService.customSendMessage(message, authorizerAccessToken);
    LOG.info("客服发送接口返回值:" + result);
  }


  /**
   * 方法描述: 返回数据到请求方
   * 
   * @param data 数据
   * @param response
   */
  private void returnJSON(Object data, HttpServletResponse response) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonEncoding encoding = JsonEncoding.UTF8;
      response.setContentType("application/json");
      org.codehaus.jackson.JsonGenerator generator =
          objectMapper.getJsonFactory().createJsonGenerator(response.getOutputStream(), encoding);
      objectMapper.writeValue(generator, data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
