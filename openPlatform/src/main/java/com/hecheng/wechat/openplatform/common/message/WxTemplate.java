package com.hecheng.wechat.openplatform.common.message;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.hecheng.wechat.openplatform.common.enums.PlatFormType;
import com.hecheng.wechat.openplatform.entity.ThirdPartUserEntity;
import com.hecheng.wechat.openplatform.entity.UserEntity;


public abstract class WxTemplate extends MsgTemplate {

  /**
   * 微信消息模版id
   */
  public abstract String getWxTemplateId();

  /**
   * 微信消息模版类型
   */
  public abstract WxTemplateType getWxTemplateType();

  /**
   * 微信消息模版回显页面
   */
  public abstract String getCallbackUrl();

  /**
   * 获取json格式的微信消息内容
   * 
   * @return 微信消息内容
   */
  public final String getWxContent() {
    Map<String, MsgData> data = null;
    WxTemplateType type = getWxTemplateType();
    if (WxTemplateType.THRESHOLD_ALARM_OR_STATISTICS_NOTICE.equals(type)) {
      data = null;
    } else if (WxTemplateType.BIND_PHONE_NOTICE.equals(type)) {
      data = null;
    } else if (WxTemplateType.CHANGE_PHONE_NOTICE.equals(type)) {
      data = null;
    } else if (WxTemplateType.UNBIND_GATEWAY_NOTICE.equals(type)) {
      data = null;
    } else if (WxTemplateType.REMOVE_SOCKET_OR_SYSTEM_NOTICE.equals(type)) {
      data = null;
    }
    ThirdPartUserEntity tUser =
        ((UserEntity) getReceiver()).getNowThirdPartUser(PlatFormType.PLAT_FORM_TYPE_WEIXIN);
    WxContent wxContent =
        new WxContent(tUser.getOpenId(), getWxTemplateId(), getCallbackUrl(), data);
    return wxContent.toJson();
  }



  class WxContent {
    /**
     * 接收者，wx是openid
     */
    private String touser;
    /**
     * 微信消息模版id
     */
    private String template_id;
    /**
     * 点击微信消息回显页面
     */
    private String url;
    /**
     * 消息模板详细内容
     */
    private Map<String, MsgData> data;

    public WxContent(String touser, String template_id, String url, Map<String, MsgData> data) {
      this.touser = touser;
      this.template_id = template_id;
      this.url = url;
      this.data = data;
    }

    public String getTouser() {
      return touser;
    }

    public void setTouser(String touser) {
      this.touser = touser;
    }

    public String getTemplate_id() {
      return template_id;
    }

    public void setTemplate_id(String template_id) {
      this.template_id = template_id;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public Map<String, MsgData> getData() {
      return data;
    }

    public void setData(Map<String, MsgData> data) {
      this.data = data;
    }

    public String toJson() {
      return JSON.toJSONString(this);
    }
  }

}
