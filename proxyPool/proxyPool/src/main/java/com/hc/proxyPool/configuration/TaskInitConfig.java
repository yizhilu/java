package com.hc.proxyPool.configuration;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.hc.proxyPool.entity.DynameicTaskJobEntity;
import com.hc.proxyPool.service.DynamicTaskJobService;

/**
 * 
 * 任务初始化
 * 
 * @author hc
 *
 */
@Configuration
public class TaskInitConfig implements ApplicationContextAware {
  @Autowired
  private ThreadPoolTaskScheduler threadPoolTaskScheduler;
  @Autowired
  private ConcurrentMapCache cache;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    DynamicTaskJobService dynamicTaskJobService =
        (DynamicTaskJobService) applicationContext.getBean("dynamicTaskJobService");

    // 系统启动时把系统内的未删除的任务和ScheduledFuture的关系加入缓存中 加入定时任务ThreadPoolTaskScheduler
    List<DynameicTaskJobEntity> jobs = dynamicTaskJobService.findAll();
    for (DynameicTaskJobEntity dynameicTaskJob : jobs) {
      if (!dynameicTaskJob.isDel() && StringUtils.isNotBlank(dynameicTaskJob.getCron())&&!dynameicTaskJob.isStop()) {
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(
            (Runnable) applicationContext.getBean(dynameicTaskJob.getJobName()),
            new CronTrigger(dynameicTaskJob.getCron()));
        cache.put(dynameicTaskJob.getJobName(), future);
      }
    }
  }
}
