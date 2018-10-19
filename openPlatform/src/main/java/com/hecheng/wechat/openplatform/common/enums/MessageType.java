package com.hecheng.wechat.openplatform.common.enums;

/**
 * 用户选择的消息通知类型
 * 
 * @author hc
 *
 */
public enum MessageType implements EnumTypeInterface {
  /** 微信 */
  MSGTYPE_WEIXIN(1, "微信"),
  /** 短信 */
  MSGTYPE_SMS(2, "短信"),
  /** 语音 */
  MSGTYPE_VOICE(3, "语音"),
  /** 站内信 */
  MSGTYPE_LETTER(4, "站内信"),
  /** 推送消息 */
  MSGTYPE_WEBSOCKET(5, "推送消息");

  private int value;
  private String desc;

  MessageType(int value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public int getValue() {
    return value;
  }

  public String getDesc() {
    return desc;
  }

  public static MessageType get(int value) {
    for (MessageType status : MessageType.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }
}
