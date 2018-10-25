package com.hecheng.wechat.openplatform.common.weixin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * 授权事件接收URL的处理 解析完成的推送 component_verify_ticket协议 或 取消授权通知 XML 数据
 * 
 * @author hc
 *
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentReceiveXML {

  @XmlElement(name = "AppId")
  private String appid;

  @XmlElement(name = "CreateTime")
  private Integer createTime;

  @XmlElement(name = "InfoType")
  private String infoType;

  @XmlElement(name = "ComponentVerifyTicket")
  private String componentVerifyTicket;

  @XmlElement(name = "AuthorizerAppid")
  private String authorizerAppid;

  public String getAppid() {
    return appid;
  }

  public void setAppid(String appid) {
    this.appid = appid;
  }

  public Integer getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Integer createTime) {
    this.createTime = createTime;
  }

  public String getInfoType() {
    return infoType;
  }

  public void setInfoType(String infoType) {
    this.infoType = infoType;
  }

  public String getComponentVerifyTicket() {
    return componentVerifyTicket;
  }

  public void setComponentVerifyTicket(String componentVerifyTicket) {
    this.componentVerifyTicket = componentVerifyTicket;
  }

  public String getAuthorizerAppid() {
    return authorizerAppid;
  }

  public void setAuthorizerAppid(String authorizerAppid) {
    this.authorizerAppid = authorizerAppid;
  }

  @Override
  public String toString() {
    return "ComponentReceiveXML [appid=" + appid + ", createTime=" + createTime + ", infoType="
        + infoType + ", componentVerifyTicket=" + componentVerifyTicket + ", authorizerAppid="
        + authorizerAppid + "]";
  }
}
