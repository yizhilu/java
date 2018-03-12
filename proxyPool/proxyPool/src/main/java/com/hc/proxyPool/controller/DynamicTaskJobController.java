package com.hc.proxyPool.controller;

import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hc.proxyPool.configuration.SpringUtils;
import com.hc.proxyPool.controller.model.ResponseModel;
import com.hc.proxyPool.entity.DynameicTaskJobEntity;
import com.hc.proxyPool.service.DynamicTaskJobService;

@RestController
public class DynamicTaskJobController extends BaseController {
  @Autowired
  private ThreadPoolTaskScheduler threadPoolTaskScheduler;
  @Autowired
  private ConcurrentMapCache cache;
  @Autowired
  private DynamicTaskJobService dynamicTaskJobService;

  /**
   * 创建定时任务
   * 
   * @return
   */
  @RequestMapping(value = "/addTaskJob", method = {RequestMethod.POST})
  public ResponseModel addTaskJob(@RequestBody DynameicTaskJobEntity dynameicTaskJob) {
    try {
      // 创建新任务
      String jobName = dynameicTaskJob.getJobName();
      Validate.notBlank(jobName, "jobName不能为空");
      String cron = dynameicTaskJob.getCron();
      Validate.notBlank(cron, "cron不能为空");
      Validate.isTrue(cache.get(jobName) == null, "jobName为%s的任务已存在", jobName);
      DynameicTaskJobEntity newJob = dynamicTaskJobService.create(dynameicTaskJob);
      return this.buildHttpReslut(newJob);

    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 更新定时任务(如果当前任务是stop==true，则只更新任务状态不再加入任务中，否则检查任务如果已存在 则更新任务的cron，如果不存在则直接加入任务中)
   * 
   * @return
   */
  @RequestMapping(value = "/updateTaskJob", method = {RequestMethod.POST})
  public ResponseModel updateTaskJob(@RequestBody DynameicTaskJobEntity dynameicTaskJob) {
    try {
      // 更新任务
      String jobName = dynameicTaskJob.getJobName();
      Validate.notBlank(jobName, "jobName不能为空");
      String cron = dynameicTaskJob.getCron();
      Validate.notBlank(cron, "cron不能为空");
      Validate.isTrue(cache.get(jobName) == null, "jobName为%s的任务已存在", jobName);
      DynameicTaskJobEntity newJob = dynamicTaskJobService.update(dynameicTaskJob);
      return this.buildHttpReslut(newJob);

    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 停止定时任务
   * 
   * @return
   */
  @RequestMapping(value = "/stopTaskJob", method = {RequestMethod.POST})
  public ResponseModel stopTaskJob(String dynameicTaskJobId) {
    try {

      DynameicTaskJobEntity dynameicTaskJob = dynamicTaskJobService.findById(dynameicTaskJobId);
      String jobName = dynameicTaskJob.getJobName();

      ScheduledFuture<?> taskFuture =  cache.get(jobName,ScheduledFuture.class);
      if (taskFuture != null) {
        // 停止任务
        taskFuture.cancel(true);
      }
      dynamicTaskJobService.stop(dynameicTaskJob);

      return this.buildHttpReslut();

    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 开始定时任务
   * 
   * @return
   */
  @RequestMapping(value = "/startTaskJob", method = {RequestMethod.POST})
  public ResponseModel startTaskJob(String dynameicTaskJobId) {
    try {

      DynameicTaskJobEntity dynameicTaskJob = dynamicTaskJobService.findById(dynameicTaskJobId);
      String jobName = dynameicTaskJob.getJobName();

      ScheduledFuture<?> taskFuture =  cache.get(jobName,ScheduledFuture.class);

      if (taskFuture != null) {
        // 停止任务
        taskFuture.cancel(true);
        // 把任务加入缓存中
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(
            (Runnable) SpringUtils.getBean(dynameicTaskJob.getJobName()),
            new CronTrigger(dynameicTaskJob.getCron()));
        cache.put(dynameicTaskJob.getJobName(), future);
      } else {
        // 把任务加入缓存中
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(
            (Runnable) SpringUtils.getBean(dynameicTaskJob.getJobName()),
            new CronTrigger(dynameicTaskJob.getCron()));
        cache.put(dynameicTaskJob.getJobName(), future);
      }

      return this.buildHttpReslut();

    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }
}
