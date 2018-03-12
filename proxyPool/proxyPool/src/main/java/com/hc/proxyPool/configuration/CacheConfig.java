package com.hc.proxyPool.configuration;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * 线程池任务调度类，能够开启线程池进行任务调度
 * ThreadPoolTaskScheduler.schedule()方法会创建一个定时计划ScheduledFuture，在这个方法需要添加两个参数，Runnable（线程接口类）
 * 和CronTrigger（定时任务触发器）, 在ScheduledFuture中有一个cancel可以停止定时任务。
 * 
 * @author hc
 *
 */
@Configuration
public class CacheConfig {


  @Bean
  public ConcurrentMapCache getConcurrentMapCache() {

    return new ConcurrentMapCache("taskCache");
  }
}
