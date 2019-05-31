package com.hc.security.entity.enums;

/**
 * 账号类型
 * 
 * @author hc
 *
 */
public enum AccountTypeEnum implements EnumTypeInterface {
  /** 账号密码 **/
  ACCOUNT_PASSWORD(0, "账号密码"),;

  private int value;
  private String desc;

  AccountTypeEnum(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public int getValue() {
    return this.value;
  }

  public String getDesc() {
    return this.desc;
  }

  public static AccountTypeEnum get(int value) {
    for (AccountTypeEnum status : AccountTypeEnum.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }

}
