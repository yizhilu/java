package com.hc.wechat.service;

import com.hc.wechat.common.message.MsgTemplate;



public interface MessageService {
  /**
   * 发送消息
   * 
   * @param msg 消息模板
   * @return 返回字符串
   */
  public String sendMessage(MsgTemplate msg);
}
