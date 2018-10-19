package com.hecheng.wechat.openplatform.common.message;

import com.hecheng.wechat.openplatform.common.enums.EnumTypeInterface;

/**
 * 通知
 * 
 * @author hc
 *
 */
public enum WxTemplateType implements EnumTypeInterface {

  /** 阈值告警或者统计通知 */
  THRESHOLD_ALARM_OR_STATISTICS_NOTICE(0, "阈值告警或者统计通知"),
  /** 绑定手机号通知 */
  BIND_PHONE_NOTICE(1, "绑定手机号通知"),
  /** 更换手机号码通知 */
  CHANGE_PHONE_NOTICE(2, "更换手机号码通知"),
  /** 移除智能开关或者系统通知 */
  REMOVE_SOCKET_OR_SYSTEM_NOTICE(3, "移除智能开关或者系统通知"),
  /** 解绑路由器通知 */
  UNBIND_GATEWAY_NOTICE(4, "解绑路由器通知"),;

  private int value;
  private String desc;

  WxTemplateType(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public int getValue() {
    return value;
  }

  public String getDesc() {
    return desc;
  }

  public static WxTemplateType get(int value) {
    for (WxTemplateType status : WxTemplateType.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }

}
