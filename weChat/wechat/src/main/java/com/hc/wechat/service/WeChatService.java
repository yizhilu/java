package com.hc.wechat.service;

import java.util.Map;

import com.hc.wechat.entity.ThirdPartUserEntity;
import com.hc.wechat.pojo.FileUploadPojo;

/**
 * @author 作者 hc
 * @version 创建时间：2017年10月18日 下午1:16:27 类说明
 */
public interface WeChatService {
  /**
   * 通过code值获取网页授权access_token
   * 
   * @param appid 公众号的唯一标识
   * @param secret 公众号的appsecret
   * @param code 登录用户每次授权所携带的code
   * @return
   */
  Map<String, Object> getWebPageAccessToken(String appid, String secret, String code);

  /**
   * 如果网页授权作用域为snsapi_userinfo，则此时开发者可以通过access_token和openid拉取用户信息了。
   * 
   * @param access_token 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
   * @param openid 用户的唯一标识
   * @param lang 返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
   * @return <br>
   *         正确时返回的JSON数据包如下： { "openid":" OPENID", " nickname": NICKNAME, "sex":"1",
   *         "province":"PROVINCE" "city":"CITY", "country":"COUNTRY", "headimgurl":
   *         "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ
   *         4eMsv84eavHiaiceqxibJxCfHe/46", "privilege":[ "PRIVILEGE1" "PRIVILEGE2" ], "unionid":
   *         "o6_bmasdasdsad6_2sgVt7hMZOPfL" }
   */
  Map<String, Object> getUserInfo(String access_token, String openid, String lang);

  /**
   * 开发者可通过OpenID来获取用户基本信息
   * 
   * @param access_token 调用接口凭证 全局token
   * @param openid 普通用户的标识，对当前公众号唯一
   * @param lang 返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
   * @return <br>
   *         返回下述JSON数据包给公众号： { "subscribe": 1, "openid": "o6_bmjrPTlm6_2sgVt7hMZOPfL2M",
   *         "nickname": "Band", "sex": 1, "language": "zh_CN", "city": "广州", "province": "广东",
   *         "country": "中国", "headimgurl":
   *         "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4
   *         eMsv84eavHiaiceqxibJxCfHe/0", "subscribe_time": 1382694957, "unionid": "
   *         o6_bmasdasdsad6_2sgVt7hMZOPfL" "remark": "", "groupid": 0, "tagid_list":[128,2] }
   */
  Map<String, Object> getInfo(String access_token, String openid);

  /**
   * 获取jsapi_ticket是公众号用于调用微信JS接口的临时票据
   * 
   * @param access_token 全局token
   * @return
   */
  Map<String, Object> getTicket(String access_token);

  /**
   * 获取全局token
   * 
   * @param appid 第三方用户唯一凭证
   * @param secret 第三方用户唯一凭证密钥，即appsecret
   * @return
   */
  Map<String, Object> getToken(String appid, String secret);

  /**
   * 生成JS-SDK权限验证的签名了
   * 
   * @param currentUrl 签名用的url必须是调用JS接口页面的完整URL。
   * @return
   */
  Map<String, Object> getJsSignature(String currentUrl);

  /**
   * 获取jsApiTicket 如果数据库中 全局jsApiTicket未过期则直接从数据库获取或者从 redis中获取， 如果过期则从微信获取并存储在数据库中或redis中获取
   * 
   * @return
   */
  String getTicket();


  /**
   * 获取微信第三方用户
   * 
   * @param access_token
   * @param openid
   * @return
   */
  ThirdPartUserEntity getThirdPartUserEntity(String access_token, String openid);

  /**
   * 获取全局token 如果数据库中 全局token未过期则直接从数据库获取， 如果过期则从微信获取并存储在数据库中
   * 
   * @param appid
   * @param secret
   * @return
   */
  String getAccessToken(String appid, String secret);

  /**
   * 下载微信图片
   * 
   * @param serverId
   * @return
   */
  FileUploadPojo downloadWeChatImage(String serverId);
}
