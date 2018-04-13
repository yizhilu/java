package com.hc.wechat.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 和redis有关的配置在这里
 * @author yinwenjie
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
  @Bean(name="cacheManager")
  public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
    RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
    // 设置缓存过期时间(单位秒)
    rcm.setDefaultExpiration(30);
    return rcm;
  }
}