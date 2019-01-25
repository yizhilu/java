package com.hc.solr.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * <p>
 * Title: RetryOnOptimisticLockingFailure.java
 * </p>
 * <p>
 * Description:乐观锁更新失败后的解决方案
 * </p>
 * 
 * @author hecheng
 * @date 2018年12月28日
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnOptimisticLockingFailure {


}
