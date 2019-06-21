package com.hc.crypto.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加密注解（目前只对@ResponseBody 进行加密）
 * 
 * @author hc
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiEncryptAnno {
  /**
   * 接口url
   * 
   * @return
   */
  String value() default "";
}
