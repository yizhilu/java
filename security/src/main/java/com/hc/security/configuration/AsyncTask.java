package com.hc.security.configuration;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

/**
 * 功能描述：异步任务业务类
 */
@Component
@Async

public class AsyncTask {


  // 获取异步结果
  public Future<String> task4() throws InterruptedException {
    long begin = System.currentTimeMillis();
    // Thread.sleep(2000L);
    long end = System.currentTimeMillis();
    System.out.println(Thread.currentThread().getName() + "任务4耗时=" + (end - begin));
    return new AsyncResult<String>("任务4");
  }


  public Future<String> task5() throws InterruptedException {
    long begin = System.currentTimeMillis();
    // Thread.sleep(3000L);
    long end = System.currentTimeMillis();
    System.out.println(Thread.currentThread().getName() + "任务5耗时=" + (end - begin));
    return new AsyncResult<String>("任务5");
  }

  public Future<String> task6() throws InterruptedException {
    long begin = System.currentTimeMillis();
    // Thread.sleep(1000L);
    long end = System.currentTimeMillis();
    System.out.println(Thread.currentThread().getName() + "任务6耗时=" + (end - begin));
    return new AsyncResult<String>("任务6");
  }
}
