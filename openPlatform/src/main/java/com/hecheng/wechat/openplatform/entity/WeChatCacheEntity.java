package com.hecheng.wechat.openplatform.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
  /** 公众平台的全局ACCESSTOKEN */
  public static final String ACCESSTOKEN = "access_token";
  /** 公众平台的全局JSAPITICKET */
  public static final String JSAPITICKET = "jsapi_ticket";
  /** 第三方平台的全局COMPONENT_VERIFY_TICKET */
  public static final String COMPONENT_VERIFY_TICKET = "component_verify_ticket";
  /** 第三方平台的全局COMPONENT_ACCESSTOKEN */
  public static final String COMPONENT_ACCESSTOKEN = "component_access_token";
  /** 授权方接口调用凭据（在授权的公众号或小程序具备API权限时，才有此返回值），也简称为令牌 */
  public static final String AUTHORIZER_ACCESS_TOKEN = "authorizer_access_token";
  /**
   * 在namecomponent_verify_ticket，component_access_token，时为第三方平台的COMPONENT_APPID,<br>
   * 当name为 ACCESSTOKEN，JSAPITICKET时为某个公众平台的appid<br>
   * 当name为authorizer_access_token为授权方appid<br>
   */
  @Column(name = "app_id")
  private String appId;

  @Column(name = "name")
  private String name;
  /** json **/
  @Column(name = "json")
  private String json;
  /** expires_in 凭证有效时间，单位：秒 **/
  @Column(name = "expires_in")
  private int expires;
  /** 创建时间 **/
  @Column(name = "create_time")
  private Date createTime = new Date();

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

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

  /**
   * 获取json的JSONObject 对象
   * 
   * @return
   */
  @Transient
  public JSONObject getJSONObject() {
    if (StringUtils.isBlank(this.json)) {
      return null;
    }
    return JSON.parseObject(this.json);
  }
}
