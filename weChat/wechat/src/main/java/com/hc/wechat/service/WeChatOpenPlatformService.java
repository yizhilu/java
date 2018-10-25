package com.hc.wechat.service;

import java.util.Map;

/**
 * 获取微信开放平台相关参数接口,对有次数限制的接口做缓存（数据库/redis）
 * 
 * @author hc
 *
 */
public interface WeChatOpenPlatformService {
  /**
   * 获取component_access_token,第三方平台component_access_token是第三方平台的下文中接口的调用凭据，也叫做令牌（component_access_token）。
   * 每个令牌是存在有效期（2小时）的，且令牌的调用不是无限制的，请第三方平台做好令牌的管理，在令牌快过期时（比如1小时50分）再进行刷新
   * 
   * @param componentAppid
   * @param componentAppsecret
   * @param componentVerifyTicket
   * @return
   */
  Map<String, Object> getComponentAccessToken(String componentAppid, String componentAppsecret,
      String componentVerifyTicket);

  /**
   * 获取pre_auth_code,该API用于获取预授权码。预授权码用于公众号或小程序授权时的第三方平台方安全验证
   * 
   * @param componentAppid
   * @param componentAccessToken
   * @return
   */
  Map<String, Object> getPreAuthCode(String componentAppid, String componentAccessToken);
}
