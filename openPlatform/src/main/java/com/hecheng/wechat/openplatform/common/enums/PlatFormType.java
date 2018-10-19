/**
 * 第三方用户类型.
 */
package com.hecheng.wechat.openplatform.common.enums;

/**
 * 第三方平台类型
 * 
 * @author hc
 */
public enum PlatFormType implements EnumTypeInterface {
  /** 微信 */
  PLAT_FORM_TYPE_WEIXIN(1, "微信"),
  /** QQ */
  PLAT_FORM_TYPE_QQ(2, "QQ"),
  /** 新浪微博 */
  PLAT_FORM_TYPE_WEIBO(3, "新浪微博"),
  /** 人人 */
  PLAT_FORM_TYPE_RR(4, "人人网");

  private int value;
  private String desc;

  PlatFormType(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public int getValue() {
    return this.value;
  }

  public String getDesc() {
    return this.desc;
  }

  public static PlatFormType get(int value) {
    for (PlatFormType status : PlatFormType.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }

}
