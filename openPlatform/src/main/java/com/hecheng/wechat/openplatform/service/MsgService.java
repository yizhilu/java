package com.hecheng.wechat.openplatform.service;

import com.hecheng.wechat.openplatform.common.message.MsgTemplate;

public interface MsgService {
  /**
   * 发送消息
   * 
   * @param msg 消息模板
   * @return 发送成功返回true
   */
  public boolean sendMsg(MsgTemplate msg);

  /**
   * 发送消息
   * 
   * @param msg 消息模板
   * @return 返回字符串
   */
  public String sendMsgBackString(MsgTemplate msg);
}
