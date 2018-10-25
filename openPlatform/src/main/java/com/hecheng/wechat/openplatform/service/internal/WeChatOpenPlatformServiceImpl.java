package com.hecheng.wechat.openplatform.service.internal;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hecheng.wechat.openplatform.entity.WeChatCacheEntity;
import com.hecheng.wechat.openplatform.service.WeChatCacheService;
import com.hecheng.wechat.openplatform.service.WeChatOpenPlatformService;
import com.hecheng.wechat.openplatform.service.feign.WeChatOpenPlatformApiService;

@Service("weChatOpenPlatformService")
@SuppressWarnings("unchecked")
public class WeChatOpenPlatformServiceImpl implements WeChatOpenPlatformService {
  private static final Logger LOG = LoggerFactory.getLogger(WeChatOpenPlatformServiceImpl.class);

  @Autowired
  private WeChatOpenPlatformApiService weChatOpenPlatformApiService;
  @Autowired
  private WeChatCacheService weChatCacheService;

  @Override
  public String getComponentVerifyTicket(String componentAppid) {
    Validate.notBlank(componentAppid, "获取第三方平台component_verify_ticket时:componentAppid不能为空");
    WeChatCacheEntity componentVerifyTicket = weChatCacheService.findByAppIdAndName(componentAppid,
        WeChatCacheEntity.COMPONENT_VERIFY_TICKET);
    if (componentVerifyTicket == null) {
      return null;
    }
    Object ticket = componentVerifyTicket.getJsonObject("componentVerifyTicket");
    return ticket == null ? null : ticket.toString();
  }

  @Override
  public synchronized String getComponentAccessToken(String componentAppid,
      String componentAppsecret) {
    // 先去数据库查询是否已存在，如果存在那么检查时间是否过期 ，如果过期则去重新获取
    Validate.notBlank(componentAppid, "获取第三方平台component_access_token时:componentAppid不能为空");
    Validate.notBlank(componentAppsecret, "获取第三方平台component_access_token时:componentAppsecret不能为空");
    WeChatCacheEntity weChatCache = weChatCacheService.findByAppIdAndName(componentAppid,
        WeChatCacheEntity.COMPONENT_ACCESSTOKEN);
    if (!isNeedGet(weChatCache, WeChatCacheEntity.COMPONENT_ACCESSTOKEN, 200)) {
      return (String) weChatCache.getJsonObject(WeChatCacheEntity.COMPONENT_ACCESSTOKEN);
    } else {
      String componentVerifyTicket = getComponentVerifyTicket(componentAppid);
      Validate.notBlank(componentVerifyTicket,
          "获取第三方平台component_access_token时:componentVerifyTicket不能为空");
      JSONObject postData = new JSONObject();
      postData.put("component_appid", componentAppid);
      postData.put("component_appsecret", componentAppsecret);
      postData.put("component_verify_ticket", componentVerifyTicket);
      String component_access_token =
          weChatOpenPlatformApiService.getComponentAccessToken(postData);
      LOG.info("getComponentAccessToken=:" + component_access_token);

      Map<String, Object> json =
          (Map<String, Object>) JSON.parseObject(component_access_token, Map.class);
      Validate.isTrue(json.get("errcode") == null, component_access_token);
      String token = (String) json.get("component_access_token");
      int expires_in = (int) json.get("expires_in");
      weChatCache = new WeChatCacheEntity();
      weChatCache.setAppId(componentAppid);
      weChatCache.setName(WeChatCacheEntity.COMPONENT_ACCESSTOKEN);
      weChatCache.setCreateTime(new Date());
      weChatCache.setExpires(expires_in);
      weChatCache.setJson(component_access_token);
      weChatCacheService.update(weChatCache);
      return token;
    }
  }



  @Override
  public String getPreAuthCode(String componentAppid, String componentAppsecret) {
    Validate.notBlank(componentAppid, "获取第三方平台pre_auth_code时:componentAppid不能为空");
    Validate.notBlank(componentAppsecret, "获取第三方平台pre_auth_code时:componentAppsecret不能为空");
    String componentAccessToken = getComponentAccessToken(componentAppid, componentAppsecret);
    JSONObject postData = new JSONObject();
    postData.put("component_appid", componentAppid);
    String jsonStr = weChatOpenPlatformApiService.getPreAuthCode(postData, componentAccessToken);
    LOG.info("getPreAuthCode=:" + jsonStr);
    Map<String, Object> json = (Map<String, Object>) JSON.parseObject(jsonStr, Map.class);
    Validate.isTrue(json.get("errcode") == null, jsonStr);
    return json.get("pre_auth_code").toString();
  }

  @Override
  public String getApiQueryAuth(String componentAppid, String componentAppsecret,
      String authorizationCode) {
    Validate.notBlank(componentAppid, "获取第三方平台ApiQueryAuth时:componentAppid不能为空");
    Validate.notBlank(componentAppsecret, "获取第三方平台ApiQueryAuth时:componentAppsecret不能为空");
    Validate.notBlank(authorizationCode, "获取第三方平台ApiQueryAuth时:authorizationCode不能为空");
    String componentAccessToken = getComponentAccessToken(componentAppid, componentAppsecret);
    JSONObject postData = new JSONObject();
    postData.put("component_appid", componentAppid);
    postData.put("authorization_code", authorizationCode);
    String jsonStr = weChatOpenPlatformApiService.getApiQueryAuth(postData, componentAccessToken);
    LOG.info("getApiQueryAuth=:" + jsonStr);
    JSONObject json = JSON.parseObject(jsonStr);
    Validate.isTrue(json.get("errcode") == null, jsonStr);
    JSONObject authorizationInfoJson = json.getJSONObject("authorization_info");
    // 授权方appid
    String authorizerAppid = authorizationInfoJson.getString("authorizer_appid");
    // 授权方接口调用凭据（在授权的公众号或小程序具备API权限时，才有此返回值），也简称为令牌
    String authorizerAccessToken = authorizationInfoJson.getString("authorizer_access_token");
    int expires_in = authorizationInfoJson.getIntValue("expires_in");
    WeChatCacheEntity weChatCache = new WeChatCacheEntity();
    weChatCache.setAppId(authorizerAppid);
    weChatCache.setName(WeChatCacheEntity.AUTHORIZER_ACCESS_TOKEN);
    weChatCache.setCreateTime(new Date());
    weChatCache.setExpires(expires_in);
    weChatCache.setJson(authorizationInfoJson.toJSONString());
    weChatCacheService.update(weChatCache);
    return authorizerAccessToken;
  }

  @Override
  public synchronized String getAuthorizerAccessToken(String appId, String componentAppid,
      String componentAppsecret) {
    Validate.notBlank(appId, "获取getAuthorizerAccessToken时:appId不能为空");
    Validate.notBlank(componentAppid, "获取getAuthorizerAccessToken时:componentAppid不能为空");
    WeChatCacheEntity weChatCache =
        weChatCacheService.findByAppIdAndName(appId, WeChatCacheEntity.AUTHORIZER_ACCESS_TOKEN);
    if (!isNeedGet(weChatCache, WeChatCacheEntity.AUTHORIZER_ACCESS_TOKEN, 200)) {
      return (String) weChatCache.getJsonObject(WeChatCacheEntity.AUTHORIZER_ACCESS_TOKEN);
    } else {
      Validate.notNull(weChatCache, "appId:%s的公众号还未授权", appId);
      String componentAccessToken = getComponentAccessToken(componentAppid, componentAppsecret);
      JSONObject postData = new JSONObject();
      postData.put("component_appid", componentAppid);
      postData.put("authorizer_appid", appId);
      postData.put("authorizer_refresh_token",
          (String) weChatCache.getJsonObject("authorizer_refresh_token"));
      String jsonStr =
          weChatOpenPlatformApiService.getApiAuthorizerToken(postData, componentAccessToken);
      LOG.info("getAuthorizerAccessToken=:" + jsonStr);
      JSONObject json = JSON.parseObject(jsonStr);
      Validate.isTrue(json.get("errcode") == null, jsonStr);
      String authorizer_access_token = json.getString("authorizer_access_token");
      String authorizer_refresh_token = json.getString("authorizer_refresh_token");
      int expires_in = json.getIntValue("expires_in");
      JSONObject oldJson = weChatCache.getJSONObject();
      oldJson.put("authorizer_access_token", authorizer_access_token);
      oldJson.put("authorizer_refresh_token", authorizer_refresh_token);
      oldJson.put("expires_in", expires_in);
      weChatCache.setCreateTime(new Date());
      weChatCache.setExpires(expires_in);
      weChatCache.setJson(oldJson.toJSONString());
      weChatCacheService.update(weChatCache);
      return authorizer_access_token;
    }
  }

  @Override
  public String customSendMessage(JSONObject message, String authorizerAccessToken) {
    Validate.notNull(message, "发送的消息不能为空");
    Validate.notBlank(authorizerAccessToken, "authorizerAccessToken不能为空");
    return weChatOpenPlatformApiService.customSendMessage(message, authorizerAccessToken);
  }

  /**
   * 是否需要重新获取指定的缓存
   * 
   * @param weChatCache
   * @param key 缓存中指定json的key
   * @param beforehandSecond 提前多少秒
   * @return
   */
  private boolean isNeedGet(WeChatCacheEntity weChatCache, String key, int beforehandSecond) {
    boolean isNeedGetTocken = false;
    // 数据库中没有记录或者是过期时间大于指定时间，需要重新获取
    if (weChatCache == null) {
      isNeedGetTocken = true;
    } else {
      long distanceTime = new Date().getTime() - weChatCache.getCreateTime().getTime();
      if (distanceTime > (weChatCache.getExpires() - beforehandSecond) * 1000) {
        isNeedGetTocken = true;
      } else {
        String value = (String) weChatCache.getJsonObject(key);
        if (StringUtils.isNotBlank(value)) {
          isNeedGetTocken = false;
        } else {
          isNeedGetTocken = true;
        }
      }
    }
    return isNeedGetTocken;
  }

}
