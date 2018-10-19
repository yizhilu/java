package com.hecheng.wechat.openplatform.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModel;


/**
 * 验证码entity
 * 
 * @author hzh
 *
 */

@Entity
@Table(name = "vali_code")
@ApiModel(value = "ValiCodeEntity")
public class ValidCodeEntity extends UuidEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 3435661966504970609L;

  /**
   * 短信参数
   */
  @Column(name = "parameter")
  private String parameter;
  /**
   * 验证码短信内容
   */
  @Column(name = "msg")
  private String msg;
  /**
   * 接收号
   */
  @Column(name = "receive_code")
  private String receiveCode;
  /**
   * 表数据创建时间
   */
  @Column(name = "create_date")
  private Date createDate;
  /**
   * 短信发送时间
   */
  @Column(name = "send_date")
  private Date sendDate;
  /**
   * 发送结果标志
   */
  @Column(name = "send_flag")
  private int sendFlag;

  public String getParameter() {
    return parameter;
  }


  public void setParameter(String parameter) {
    this.parameter = parameter;
  }


  public String getMsg() {
    return msg;
  }


  public void setMsg(String msg) {
    this.msg = msg;
  }


  public String getReceiveCode() {
    return receiveCode;
  }


  public void setReceiveCode(String receiveCode) {
    this.receiveCode = receiveCode;
  }


  public Date getCreateDate() {
    return createDate;
  }


  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }


  public Date getSendDate() {
    return sendDate;
  }


  public void setSendDate(Date sendDate) {
    this.sendDate = sendDate;
  }


  public int getSendFlag() {
    return sendFlag;
  }


  public void setSendFlag(int sendFlag) {
    this.sendFlag = sendFlag;
  }

  @Transient
  public UserEntity getReceiver() {
    UserEntity user = new UserEntity();
    user.setTelephone(this.receiveCode);
    return user;
  }

  /**
   * 创建时间与现在时间的间隔时间s
   * 
   * @return
   */
  @Transient
  public Long getIntervalTime() {
    Date now = new Date();
    long l = now.getTime() - createDate.getTime();
    long s = (l / 1000);
    return s;
  }
}
