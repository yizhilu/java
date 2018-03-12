package com.hc.proxyPool.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 代理资源目标地址
 * 
 * @author hc
 *
 */
@Entity
@Table(name = "proxy_resource")
public class ProxyResourceEntity extends UuidEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 549889799906787274L;
  /**
   * 网站名
   */
  @Column(name = "web_name")
  private String webName;
  /**
   * 网址
   */
  @Column(name = "web_url")
  private String webUrl;
  /**
   * 爬虫类
   */
  @Column(name = "crawler_parser")
  private String crawlerParser;
  /**
   * 
   */
  @Column(name = "create_date")
  private Date createDate;
  /**
   * 
   */
  @Column(name = "modify_date")
  private Date modifyDate;

  public String getWebName() {
    return webName;
  }

  public void setWebName(String webName) {
    this.webName = webName;
  }

  public String getWebUrl() {
    return webUrl;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  public String getCrawlerParser() {
    return crawlerParser;
  }

  public void setCrawlerParser(String crawlerParser) {
    this.crawlerParser = crawlerParser;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate) {
    this.modifyDate = modifyDate;
  }

}
