package com.hecheng.wechat.openplatform.common.message;

/**
 * 微信消息模板详细内容
 * 
 * @author liyong 2016-6-27
 */
public class MsgData {
  private static final String DEFAULT_COLOR = "#173177";
  /**
   * 消息属性数据
   */
  private String value;
  /**
   * 消息数据显示颜色
   */
  private String color;

  public MsgData() {}

  public MsgData(String value) {
    this(value, DEFAULT_COLOR);
  }

  public MsgData(String value, String color) {
    this.value = value;
    this.color = color;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }
}
