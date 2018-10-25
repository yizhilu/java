package com.hecheng.wechat.openplatform.service;

import com.hecheng.wechat.openplatform.entity.WeChatCacheEntity;

/**
 * @author 作者 hc
 * @version 创建时间：2017年10月19日 下午5:41:01 类说明
 */
public interface WeChatCacheService {
  /**
   * 按照appid 和name查询
   * 
   * @param appId
   * @param name
   * @return
   */
  WeChatCacheEntity findByAppIdAndName(String appId, String name);

  /**
   * 如果不存在 则创建 存在则更新
   * 
   * @param WeChatCache
   * @return
   */
  WeChatCacheEntity update(WeChatCacheEntity weChatCache);
}
