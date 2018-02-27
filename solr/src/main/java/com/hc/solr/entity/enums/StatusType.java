/**
 * 使用状态.
 */
package com.hc.solr.entity.enums;

/**
 * @author ly
 * @version V1.0
 */
public enum StatusType implements EnumTypeInterface {
  /** 正常. **/
  STATUS_NORMAL(1, "正常"),
  /** 禁用. **/
  STATUS_DISABLE(0, "禁用");

  private int value;
  private String desc;

  StatusType(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public int getValue() {
    return this.value;
  }

  public String getDesc() {
    return this.desc;
  }

  public static StatusType get(int value) {
    for (StatusType status : StatusType.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }

}
