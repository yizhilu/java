package com.hc.wechat.service.internal;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.wechat.entity.WeChatCacheEntity;
import com.hc.wechat.repository.WeChatCacheRepository;
import com.hc.wechat.service.WeChatCacheService;


/**
 * 微信全局 令牌
 * 
 * @author 作者 hc
 */
@Service("weChatCacheService")
public class WeChatCacheServiceImpl implements WeChatCacheService {
  @Autowired
  private WeChatCacheRepository weChatCacheRepository;

  @Override
  public WeChatCacheEntity findAccessToken() {
    return weChatCacheRepository.findByName(WeChatCacheEntity.ACCESSTOKEN);
  }

  @Override
  public WeChatCacheEntity findJsApiTicket() {

    return weChatCacheRepository.findByName(WeChatCacheEntity.JSAPITICKET);
  }

  @Override
  @Transactional
  public WeChatCacheEntity updateAccessToken(WeChatCacheEntity weChatCache) {
    Validate.notNull(weChatCache, "微信accessToken不能为空");
    Validate.notBlank(weChatCache.getName(), "微信weChatCache,Name为accessToken");
    String accessToken = (String) weChatCache.getJsonObject("access_token");
    Validate.notBlank(accessToken, "微信accessToken不能为空");
    Validate.isTrue(weChatCache.getExpires() > 0, "微信accessToken,Expires异常");
    Validate.notNull(weChatCache.getCreateTime(), "微信accessToken创建时间不能为空");
    // 1. 检查是否已存在
    WeChatCacheEntity accessTokenOld = findAccessToken();
    if (accessTokenOld == null) {
      return weChatCacheRepository.save(weChatCache);
    } else {
      // 2. 否则 更新历史token
      accessTokenOld.setJson(weChatCache.getJson());
      accessTokenOld.setExpires(weChatCache.getExpires());
      accessTokenOld.setCreateTime(weChatCache.getCreateTime());
      return weChatCacheRepository.saveAndFlush(accessTokenOld);
    }
  }

  @Override
  @Transactional
  public WeChatCacheEntity updateJsApiTicket(WeChatCacheEntity weChatCache) {
    Validate.notNull(weChatCache, "微信ticket不能为空");
    Validate.notBlank(weChatCache.getName(), "微信weChatCache,Name为jsApiTicket");
    String ticket = (String) weChatCache.getJsonObject("ticket");
    Validate.notBlank(ticket, "微信ticket不能为空");
    Validate.isTrue(weChatCache.getExpires() > 0, "微信ticket,Expires异常");
    Validate.notNull(weChatCache.getCreateTime(), "微信ticket 创建时间不能为空");
    // 1. 检查是否已存在
    WeChatCacheEntity ticketOld = findJsApiTicket();
    if (ticketOld == null) {
      return weChatCacheRepository.save(weChatCache);
    } else {
      // 2. 否则 更新历史ticketOld
      ticketOld.setJson(weChatCache.getJson());
      ticketOld.setExpires(weChatCache.getExpires());
      ticketOld.setCreateTime(weChatCache.getCreateTime());
      return weChatCacheRepository.saveAndFlush(ticketOld);
    }
  }

}
