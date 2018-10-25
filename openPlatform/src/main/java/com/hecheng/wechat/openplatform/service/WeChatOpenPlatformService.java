package com.hecheng.wechat.openplatform.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 获取微信开放平台相关参数接口,对有次数限制的接口做缓存（数据库/redis）
 * 
 * @author hc
 *
 */
public interface WeChatOpenPlatformService {
  /**
   * 获得component_verify_ticket协议
   * 
   * @param componentAppid
   * @return
   */
  String getComponentVerifyTicket(String componentAppid);

  /**
   * 获取component_access_token,第三方平台component_access_token是第三方平台的下文中接口的调用凭据，也叫做令牌（component_access_token）。
   * 每个令牌是存在有效期（2小时）的，且令牌的调用不是无限制的，请第三方平台做好令牌的管理，在令牌快过期时（比如1小时50分）再进行刷新（2000次）
   * 
   * @param componentAppid
   * @param componentAppsecret
   * @return
   */
  String getComponentAccessToken(String componentAppid, String componentAppsecret);

  /**
   * 获取pre_auth_code,该API用于获取预授权码。预授权码用于公众号或小程序授权时的第三方平台方安全验证(不可重复使用)
   * 
   * @param componentAppid
   * @param componentAccessToken
   * @return
   */
  String getPreAuthCode(String componentAppid, String componentAppsecret);

  /**
   * 使用授权码换取公众号或小程序的接口调用凭据和授权信息<br>
   * <pre>
   * 该API用于使用授权码换取授权公众号或小程序的授权信息，并换取authorizer_access_token和authorizer_refresh_token。
   *  授权码的获取，需要在用户在第三方平台授权页中完成授权流程后，在回调URI中通过URL参数提供给第三方平台方。请注意，由于现在公众号或小程
   *  序可以自定义选择部分权限授权给第三方平台，因此第三方平台开发者需要通过该接口来获取公众号或小程序具体授权了哪些权限，而不是简单地认为自
   *  声明的权限就是公众号或小程序授权的权限
   * </pre>
   * 
   * @param componentAppid
   * @param componentAppsecret
   * @param authorizationCode
   *        授权流程完成后，会进入回调URI，并在URL参数中返回授权码和过期时间(redirect_url?auth_code=xxx&expires_in=600)
   * @return
   */
  String getApiQueryAuth(String componentAppid, String componentAppsecret,
      String authorizationCode);

  /**
   * 获取指定授权方的authorizer_access_token 用于第三方平台调用授权方的接口
   * 
   * @param appId
   * @param componentAppid
   * @param componentAppsecret
   * @return
   */
  String getAuthorizerAccessToken(String appId, String componentAppid, String componentAppsecret);

  /**
   * 客服接口-发消息
   * 
   * @param message
   * @param authorizerAccessToken
   * @return
   */
  String customSendMessage(JSONObject message, String authorizerAccessToken);
}
