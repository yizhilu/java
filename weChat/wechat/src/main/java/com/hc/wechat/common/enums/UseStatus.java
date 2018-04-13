/**
 * 使用状态.
 */
package com.hc.wechat.common.enums;

/**
 * @author ly
 * @date 2017年8月8日 下午3:38:52
 * @version V1.0
 */
public enum UseStatus implements EnumTypeInterface {
  /** 正常. **/
  STATUS_NORMAL(1, "正常"),
  /** 禁用. **/
  STATUS_DISABLE(2, "禁用");

  private int value;
  private String desc;

  UseStatus(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public int getValue() {
    return this.value;
  }

  public String getDesc() {
    return this.desc;
  }

  public static UseStatus get(int value) {
    for (UseStatus status : UseStatus.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }

}
