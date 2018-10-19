package com.hc.wechat.common.utils.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface ExcelElement {
  /**
   * excel列名
   * 
   * @return
   */
  String name() default "##default";

  /**
   * excel下标从0开始
   * 
   * @return
   */
  int index() default 0;

  /**
   * 是否可以为空
   * 
   * @return
   */
  boolean nullable() default false;

  /**
   * 是否必须
   * 
   * @return
   */
  boolean required() default false;

  /**
   * 默认值
   * 
   * @return
   */
  String defaultValue() default "\000";

  /**
   * 类型
   * 
   * @return
   */
  Class<?> type() default DEFAULT.class;

  public static final class DEFAULT {}
}
