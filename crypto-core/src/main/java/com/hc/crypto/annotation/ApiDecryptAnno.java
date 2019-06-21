package com.hc.crypto.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 解密注解 （目前只对@RequestBody 进行解密）
 * 
 * @author hc
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiDecryptAnno {
  /**
   * 接口url
   * 
   * @return
   */
  String value() default "";
}
