package com.hecheng.wechat.openplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hecheng.wechat.openplatform.entity.WeChatCacheEntity;

/**
 * 存储微信全局token和jsApiTicket 数据层
 * 
 * @author hc
 *
 */
@Repository("weChatCacheRepostory")
public interface WeChatCacheRepository
    extends
      JpaRepository<WeChatCacheEntity, String>,
      JpaSpecificationExecutor<WeChatCacheEntity> {
  WeChatCacheEntity findByName(String name);
}
