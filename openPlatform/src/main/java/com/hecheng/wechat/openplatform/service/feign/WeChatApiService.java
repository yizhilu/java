package com.hecheng.wechat.openplatform.service.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;

@FeignClient(url = "https://api.weixin.qq.com", name = "weChat")
public interface WeChatApiService {

  /**
   * 通过code值获取网页授权access_token<br>
   * <pre>首先请注意，这里通过code换取的是一个特殊的网页授权access_token,
   * 与基础支持中的access_token（该access_token用于调用其他接口）不同。
   * 公众号可通过下述接口来获取网页授权access_token。
   * 如果网页授权的作用域为snsapi_base，则本步骤中获取到网页授权access_token的同时，也获取到了openid，snsapi_base式的网页授权流程即到此为止。</pre>
   * 
   * @param appid 公众号的唯一标识
   * @param secret 公众号的appsecret
   * @param code 登录用户每次授权所携带的code
   * @param grant_type 填写为authorization_code
   * @return <br>
   *         正确时返回的JSON数据包如下： { "access_token":"ACCESS_TOKEN", "expires_in":7200(s),
   *         "refresh_token":"REFRESH_TOKEN", "openid":"OPENID", "scope":"SCOPE" }
   */
  @RequestMapping(value = "/sns/oauth2/access_token", method = RequestMethod.GET)
  public String getWebPageAccessToken(@RequestParam("appid") String appid,
      @RequestParam("secret") String secret, @RequestParam("code") String code,
      @RequestParam("grant_type") String grant_type);

  /**
   * 由于access_token拥有较短的有效期， 当access_token超时后，可以使用refresh_token进行刷新，
   * refresh_token有效期为30天，当refresh_token失效之后，需要用户重新授权
   * 
   * @param appid 公众号的唯一标识
   * @param grant_type 填写为refresh_token
   * @param refresh_token 填写通过access_token获取到的refresh_token参数
   * @return <br>
   *         正确时返回的JSON数据包如下： { "access_token":"ACCESS_TOKEN", "expires_in":7200,
   *         "refresh_token":"REFRESH_TOKEN", "openid":"OPENID", "scope":"SCOPE" }
   */
  @RequestMapping(value = "/sns/oauth2/refresh_token", method = RequestMethod.GET)
  public String refreshWebPageAccessToken(@RequestParam("appid") String appid,
      @RequestParam("grant_type") String grant_type,
      @RequestParam("refresh_token") String refresh_token);

  /**
   * 如果网页授权作用域为snsapi_userinfo，则此时开发者可以通过access_token和openid拉取用户信息了。
   * 
   * @param webPageAccessToken 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
   * @param openid 用户的唯一标识
   * @param lang 返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
   * @return <br>
   *         正确时返回的JSON数据包如下： { "openid":" OPENID", " nickname": NICKNAME, "sex":"1",
   *         "province":"PROVINCE" "city":"CITY", "country":"COUNTRY", "headimgurl":
   *         "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQ
   *         Q 4eMsv84eavHiaiceqxibJxCfHe/46", "privilege":[ "PRIVILEGE1" "PRIVILEGE2" ], "unionid":
   *         "o6_bmasdasdsad6_2sgVt7hMZOPfL" }
   */
  @RequestMapping(value = "/sns/userinfo", method = RequestMethod.GET)
  public String getUserInfo(@RequestParam("access_token") String webPageAccessToken,
      @RequestParam("openid") String openid, @RequestParam("lang") String lang);

  /**
   * 获取全局token <pre>access_token是公众号的全局唯一接口调用凭据，
   * 公众号调用各接口时都需使用access_token。开发者需要进行妥善保存。
   * access_token的存储至少要保留512个字符空间。
   * access_token的有效期目前为2个小时，需定时刷新，重复获取将导致上次获取的access_token失效。</pre>
   * 
   * @param grant_type 获取access_token填写client_credential
   * @param appid 第三方用户唯一凭证
   * @param secret 第三方用户唯一凭证密钥，即appsecret
   * @return <br>
   *         返回下述JSON数据包给公众号： {"access_token":"ACCESS_TOKEN","expires_in":7200}
   */
  @RequestMapping(value = "/cgi-bin/token", method = RequestMethod.GET)
  public String getToken(@RequestParam("grant_type") String grant_type,
      @RequestParam("appid") String appid, @RequestParam("secret") String secret);

  /**
   * 开发者可通过OpenID来获取用户基本信息 <pre>在关注者与公众号产生消息交互后，
   * 公众号可获得关注者的OpenID（加密后的微信号，每个用户对每个公众号的OpenID是唯一的。对于不同公众号，同一用户的openid不同）。
   * 公众号可通过本接口来根据OpenID获取用户基本信息，包括昵称、头像、性别、所在城市、语言和关注时间。</pre>
   * 
   * @param access_token 调用接口凭证 全局token
   * @param openid 普通用户的标识，对当前公众号唯一
   * @param lang 返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
   * @return <br>
   *         返回下述JSON数据包给公众号： { "subscribe": 1, "openid": "o6_bmjrPTlm6_2sgVt7hMZOPfL2M",
   *         "nickname": "Band", "sex": 1, "language": "zh_CN", "city": "广州", "province": "广东",
   *         "country": "中国", "headimgurl":
   *         "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ
   *         4 eMsv84eavHiaiceqxibJxCfHe/0", "subscribe_time": 1382694957, "unionid": "
   *         o6_bmasdasdsad6_2sgVt7hMZOPfL" "remark": "", "groupid": 0, "tagid_list":[128,2] }
   */
  @RequestMapping(value = "/cgi-bin/user/info", method = RequestMethod.GET)
  public String getInfo(@RequestParam("access_token") String access_token,
      @RequestParam("openid") String openid, @RequestParam("lang") String lang);

  /**
   * 获取jsapi_ticket是公众号用于调用微信JS接口的临时票据
   * 
   * @param access_token
   * @param type jsapi
   * @return <br>
   *         成功返回如下JSON： { "errcode":0, "errmsg":"ok", "ticket":
   *         "bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA"
   *         , "expires_in":7200 }
   */
  @RequestMapping(value = "/cgi-bin/ticket/getticket", method = RequestMethod.GET)
  public String getTicket(@RequestParam("access_token") String access_token,
      @RequestParam("type") String type);


  /**
   * 发送微信模板消息
   * 
   * @param access_token
   * @param type
   * @return { "errcode":0, "errmsg":"ok", "msgid":200228332 }
   */
  @RequestMapping(value = "/cgi-bin/message/template/send?access_token={access_token}", method = RequestMethod.POST)
  public String sendMsg(@RequestBody JSONObject context,
      @PathVariable("access_token") String access_token);


}
