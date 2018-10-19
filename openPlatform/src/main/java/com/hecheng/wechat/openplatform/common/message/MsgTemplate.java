package com.hecheng.wechat.openplatform.common.message;

import java.util.Date;

import com.hecheng.wechat.openplatform.entity.UserEntity;


/**
 * 消息模板
 *
 * @author liyong 2016-6-27
 *
 */
public abstract class MsgTemplate {

  /**
   * 消息ID
   */
  public abstract String getId();

  /**
   * 消息类型
   */
  public abstract String getMetaValue();

  /**
   * 消息发送者
   */
  public abstract UserEntity getSender();

  /**
   * 消息接收者
   */
  public abstract UserEntity getReceiver();


  /**
   * 消息标题
   */
  public abstract String getTitle();

  /**
   * 消息内容
   */
  public abstract String getContent();

  /**
   * 消息发送时间
   */
  public abstract Date getSendTime();

  /**
   * 消息创建时间
   */
  public abstract String getCreateTime();
}
