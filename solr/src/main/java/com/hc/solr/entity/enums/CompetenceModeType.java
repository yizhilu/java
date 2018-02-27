package com.hc.solr.entity.enums;

/**
 * 功能呈现的类型，1表示是一个URL链接，2表示是一个button形式的按钮
 * 
 * @version V1.0
 */
public enum CompetenceModeType implements EnumTypeInterface {
  /** URL链接 **/
  MODETYPE_URL(1, "URL"),
  /** button **/
  MODETYPE_BUTTON(2, "BUTTON");

  private int value;
  private String desc;

  CompetenceModeType(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public int getValue() {
    return this.value;
  }

  public String getDesc() {
    return this.desc;
  }

  public static CompetenceModeType get(int value) {
    for (CompetenceModeType status : CompetenceModeType.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }

}
