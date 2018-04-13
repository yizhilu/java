package com.hc.wechat.service;

import com.hc.wechat.entity.WeChatCacheEntity;

/**
 * 用于缓存微信全局应用的token 和 Ticket
 * 
 * @author 作者 hc
 * @version 创建时间：2017年10月19日 下午5:41:01 类说明
 */
public interface WeChatCacheService {
  /**
   * 查询系统缓存的微信的accessToken
   * 
   * @param name
   * @return
   */
  WeChatCacheEntity findAccessToken();

  /**
   * 查询系统缓存的微信的jsApiTicket
   * 
   * @param name
   * @return
   */
  WeChatCacheEntity findJsApiTicket();

  /**
   * 如果不存在 则创建 存在则更新accessToken
   * 
   * @param WeChatCache
   * @return
   */
  WeChatCacheEntity updateAccessToken(WeChatCacheEntity weChatCache);

  /**
   * 如果不存在 则创建 存在则更新jsApiTicke
   * 
   * @param WeChatCache
   * @return
   */
  WeChatCacheEntity updateJsApiTicket(WeChatCacheEntity weChatCache);
}
