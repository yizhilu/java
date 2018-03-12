package com.hc.proxyPool.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 代理
 * 
 * @author hc
 *
 */
@Entity
@Table(name = "proxy_data")
public class ProxyDataEntity extends UuidEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 549889799906787274L;
  /***
   * 代理类型
   */
  @Column(name = "proxy_type")
  private String proxyType;
  /**
   * 代理地址
   */
  @Column(name = "proxy_ip")
  private String proxyIp;
  /**
   * 代理端口
   */
  @Column(name = "proxy_port")
  private String proxyPort;
  /**
   * 代理地址
   */
  @Column(name = "proxy_address")
  private String proxyAddress;
  /**
   * 代理运营商
   */
  @Column(name = "proxy_operator")
  private String proxyOperator;
  /**
   * 匿名类型
   */
  @Column(name = "anonymous_type")
  private String anonymousType;
  /**
   * 最后成功时间
   */
  @Column(name = "last_successful_date")
  private Date lastSuccessfulDate;
  /**
   * 数据来源
   */
  @Column(name = "data_source")
  private String dataSource;
  /**
   * 爬取时间
   */
  @Column(name = "crawle_date")
  private Date crawleDate;

  public String getProxyType() {
    return proxyType;
  }

  public void setProxyType(String proxyType) {
    this.proxyType = proxyType;
  }



  public String getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(String proxyPort) {
    this.proxyPort = proxyPort;
  }

  public Date getLastSuccessfulDate() {
    return lastSuccessfulDate;
  }

  public void setLastSuccessfulDate(Date lastSuccessfulDate) {
    this.lastSuccessfulDate = lastSuccessfulDate;
  }

  public String getProxyIp() {
    return proxyIp;
  }

  public void setProxyIp(String proxyIp) {
    this.proxyIp = proxyIp;
  }

  public String getProxyAddress() {
    return proxyAddress;
  }

  public void setProxyAddress(String proxyAddress) {
    this.proxyAddress = proxyAddress;
  }

  public String getProxyOperator() {
    return proxyOperator;
  }

  public void setProxyOperator(String proxyOperator) {
    this.proxyOperator = proxyOperator;
  }

  public String getAnonymousType() {
    return anonymousType;
  }

  public void setAnonymousType(String anonymousType) {
    this.anonymousType = anonymousType;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource(String dataSource) {
    this.dataSource = dataSource;
  }

  public Date getCrawleDate() {
    return crawleDate;
  }

  public void setCrawleDate(Date crawleDate) {
    this.crawleDate = crawleDate;
  }

  @Transient
  public String getProxyStr() {
    return proxyType + "://" + proxyIp + ":" + proxyPort;
  }

  @Override
  public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode())
        + "ProxyDataEntity [proxyType=" + proxyType + ", proxyIp=" + proxyIp + ", proxyPort="
        + proxyPort + ", proxyAddress=" + proxyAddress + ", proxyOperator=" + proxyOperator
        + ", anonymousType=" + anonymousType + ", lastSuccessfulDate=" + lastSuccessfulDate + "]";
  }

}
