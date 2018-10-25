package com.hecheng.wechat.openplatform.service.internal;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hecheng.wechat.openplatform.entity.WeChatCacheEntity;
import com.hecheng.wechat.openplatform.repository.WeChatCacheRepository;
import com.hecheng.wechat.openplatform.service.WeChatCacheService;


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
  public WeChatCacheEntity findByAppIdAndName(String appId, String name) {
    Validate.notBlank(appId, "appId不能为空");
    Validate.notBlank(name, "name不能为空");
    return weChatCacheRepository.findByAppIdAndName(appId, name);
  }

  @Override
  @Transactional
  public WeChatCacheEntity update(WeChatCacheEntity weChatCache) {
    Validate.notNull(weChatCache, "weChatCache不能为空");
    Validate.notNull(weChatCache.getAppId(), "weChatCache的appId不能为空");
    Validate.notNull(weChatCache.getName(), "weChatCache的name不能为空");
    // 1. 检查是否已存在
    WeChatCacheEntity old = findByAppIdAndName(weChatCache.getAppId(), weChatCache.getName());
    if (old == null) {
      return weChatCacheRepository.save(weChatCache);
    } else {
      // 2. 否则 更新历史
      old.setJson(weChatCache.getJson());
      old.setExpires(weChatCache.getExpires());
      old.setCreateTime(weChatCache.getCreateTime());
      return weChatCacheRepository.saveAndFlush(old);
    }
  }
}
