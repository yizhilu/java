package com.hecheng.wechat.openplatform.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Spring中匹配controller中参数的注解
 * 
 * @author 作者 hc
 * @version 创建时间：2017年10月18日 下午2:23:10 类说明
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ObjectConvertAnno {

  /**
   * 指定该参数的值是否允许为 null 默认是不允许 Whether the parameter is required.
   * <p>
   * Default is <code>true</code>, leading to an exception thrown in case of the parameter missing
   * in the request. Switch this to <code>false</code> if you prefer a <code>null</value> in case of
   * the parameter missing.
   */
  boolean required() default true;
}
