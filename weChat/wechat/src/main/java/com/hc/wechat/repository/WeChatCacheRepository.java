package com.hc.wechat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hc.wechat.entity.WeChatCacheEntity;

/**
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
