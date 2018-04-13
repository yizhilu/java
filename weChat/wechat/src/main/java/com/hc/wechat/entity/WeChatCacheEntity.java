package com.hc.wechat.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * 微信api 调用 凭证数据缓存 ，如全局token jsapi等
 * 
 * @author 作者 hc:
 */
@Entity
@Table(name = "wechat_cache")
public class WeChatCacheEntity extends UuidEntity {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public static final String ACCESSTOKEN = "accessToken";
  public static final String JSAPITICKET = "jsApiTicket";
  @Column(name = "name", unique = true)
  private String name;
  /** json **/
  @Column(name = "json")
  private String json;
  /** expires_in 凭证有效时间，单位：秒 **/
  @Column(name = "expires_in")
  private int expires;
  /** 创建时间 **/
  @Column(name = "create_time")
  private Date createTime;

  public String getJson() {
    return json;
  }

  public void setJson(String json) {
    this.json = json;
  }

  public int getExpires() {
    return expires;
  }

  public void setExpires(int expires) {
    this.expires = expires;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * 获取json中的值 如果 json为 空字符串 或者为null 则直接返回 null ，如果不存在 key 对应的value 返回 null
   * 
   * @param key
   * @return
   */
  @Transient
  public Object getJsonObject(String key) {
    if (StringUtils.isBlank(this.json)) {
      return null;
    }
    @SuppressWarnings("unchecked")
    Map<String, Object> json = (Map<String, Object>) JSON.parseObject(this.json, Map.class);
    Object value = json.get(key);
    if (value == null) {
      return null;
    }
    return value;
  }
}
