package com.hc.solr.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import com.hc.solr.annotation.RetryOnOptimisticLockingFailure;

@Aspect
@Component
public class RetryOnOptimisticLockingAspect {

  private static final Logger logger =
      LoggerFactory.getLogger(RetryOnOptimisticLockingFailure.class);
  public final int maxRetries = 5;// 最多重试的次数

  @Pointcut("@annotation(com.hc.solr.annotation.RetryOnOptimisticLockingFailure)") // 自定义的注解作为切点
  public void retryOnOptFailure() {}

  @Around("retryOnOptFailure()") // around注解可以在 目标方法 之前执行 也可以在目标方法之后
  public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
    int numAttempts = 0;
    do {
      numAttempts++;
      try {
        return pjp.proceed();
      } catch (Exception e) {// 此处捕获异常时，网上的大部分文章都是只有OptimisticLockingFailureException这一种异常，这是不够的，可以先e.printStackTrace();看一看都有哪些异常，在进行捕获
        if (e instanceof ObjectOptimisticLockingFailureException || e instanceof StaleStateException
            || e instanceof JpaSystemException) {
          logger.error(e.getMessage(), e);
          logger.info("更新数据---乐观锁重试中---第" + numAttempts + "次");
          if (numAttempts > maxRetries) {
            logger.info("抛出异常");
            throw e;
          }
        }
      }
    } while (numAttempts < this.maxRetries);
    return null;
  }

}

