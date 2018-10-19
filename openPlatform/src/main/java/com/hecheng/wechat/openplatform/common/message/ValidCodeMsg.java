package com.hecheng.wechat.openplatform.common.message;


import java.util.Date;

import com.hecheng.wechat.openplatform.common.utils.DateUtils;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.entity.ValidCodeEntity;


/**
 * 验证码消息
 * 
 * @author cxj
 *
 */
public class ValidCodeMsg extends MsgTemplate {
  public static final String META_VALUE = "ValidCode";
  private UserEntity sender;
  private UserEntity receiver;
  private String content;
  private String createTime;
  private Date sendTime;
  private String code;


  public ValidCodeMsg(ValidCodeEntity validCode) {
    receiver = validCode.getReceiver();
    content = validCode.getMsg();
    createTime = DateUtils.format(validCode.getCreateDate(), "yyyy-MM-dd HH:mm:ss");
    sendTime = validCode.getCreateDate();
    code = validCode.getParameter();
  }


  @Override
  public UserEntity getSender() {
    return sender;
  }

  @Override
  public UserEntity getReceiver() {
    return receiver;
  }

  @Override
  public String getTitle() {
    return null;
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public String getCreateTime() {
    return createTime;
  }

  @Override
  public String getId() {
    return "0";
  }

  @Override
  public String getMetaValue() {
    return META_VALUE;
  }

  @Override
  public Date getSendTime() {
    return sendTime;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
