package com.hc.wechat.common.message;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.Validate;

import com.hc.wechat.common.utils.DateUtils;
import com.hc.wechat.entity.UserEntity;


/**
 * {{first.DATA}}<br>
 * 姓名：{{keyword1.DATA}}<br>
 * 电话：{{keyword2.DATA}}<br>
 * 服务类型:{{keyword3.DATA}}<br>
 * 时间：{{keyword4.DATA}}<br>
 * {{remark.DATA}}<br>
 * 
 * @author hc
 *
 */
public class WxTransactNotice extends WxTemplate {
  public static final String META_VALUE = "WxTransactNotice";
  /**
   * 发送者
   */
  private UserEntity sender;
  /**
   * 接收者
   */
  private UserEntity receiver;
  /**
   * 微信回调
   */
  private String callBackUrl;
  /**
   * 
   */
  private String first;
  /**
   * 名称
   */
  private String nickName;
  /**
   * 电话
   */
  private String telphone;
  /**
   * 服务类型
   */
  private String serviceType;
  /**
   * 通知时间
   */
  private String noticeDate;
  /**
   * 
   */
  private String remark;
  /**
   * 发送时间
   */
  private Date sendTime;
  /**
   * 
   */
  private String wxTemplateId;
  /**
   * 消息内容
   */
  private String content;

  public WxTransactNotice(UserEntity sender, UserEntity receiver, String callBackUrl,
      String content, Date date, String wxTemplateId) {
    this.callBackUrl = callBackUrl;
    this.sender = sender;
    this.receiver = receiver;
    this.wxTemplateId = wxTemplateId;
    // 名称0||电话1||服务类型2||结果3
    Validate.notBlank(content, "发送的内容不能为空");
    this.content = content;
    String[] arr = content.split("\\|\\|");
    this.first = "您有一条新的服务通知。";
    this.nickName = arr[0];
    this.telphone = arr[1];
    this.serviceType = arr[2];
    this.remark = arr[3];
    this.noticeDate = DateUtils.format(date, "yyyy年MM月dd号  HH:mm");
    this.sendTime = date;
  }

  @Override
  public String getWxTemplateId() {

    return wxTemplateId;
  }

  @Override
  public WxTemplateType getWxTemplateType() {

    return WxTemplateType.WXTEMPLATE_NOTICE;
  }

  @Override
  public String getCallbackUrl() {
    return callBackUrl;
  }

  @Override
  public String getId() {

    return UUID.randomUUID().toString();
  }

  @Override
  public String getMetaValue() {
    return META_VALUE;
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
    return first;
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public Date getSendTime() {
    return sendTime;
  }

  @Override
  public String getCreateTime() {
    return noticeDate;
  }

  public String getCallBackUrl() {
    return callBackUrl;
  }

  public void setCallBackUrl(String callBackUrl) {
    this.callBackUrl = callBackUrl;
  }

  public String getFirst() {
    return first;
  }

  public void setFirst(String first) {
    this.first = first;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getTelphone() {
    return telphone;
  }

  public void setTelphone(String telphone) {
    this.telphone = telphone;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public String getNoticeDate() {
    return noticeDate;
  }

  public void setNoticeDate(String noticeDate) {
    this.noticeDate = noticeDate;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public void setSender(UserEntity sender) {
    this.sender = sender;
  }

  public void setReceiver(UserEntity receiver) {
    this.receiver = receiver;
  }

  public void setSendTime(Date sendTime) {
    this.sendTime = sendTime;
  }

  public void setWxTemplateId(String wxTemplateId) {
    this.wxTemplateId = wxTemplateId;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
