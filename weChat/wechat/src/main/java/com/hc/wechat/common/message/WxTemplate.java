package com.hc.wechat.common.message;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.hc.wechat.common.enums.PlatFormType;
import com.hc.wechat.entity.ThirdPartUserEntity;
import com.hc.wechat.entity.UserEntity;


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
    if (WxTemplateType.WXTEMPLATE_NOTICE.equals(type)) {
      data = wxTransactNotice();
    } else
      return null;

    ThirdPartUserEntity tUser =
        ((UserEntity) getReceiver()).getNowThirdPartUser(PlatFormType.PLAT_FORM_TYPE_WEIXIN);
    WxContent wxContent =
        new WxContent(tUser.getOpenId(), getWxTemplateId(), getCallbackUrl(), data);
    return wxContent.toJson();// wxContent.toJson()
  }


  /**
   * 用户名称：{{keyword1.DATA}} 开通功能：{{keyword2.DATA}} 开通时间：{{keyword3.DATA}} 封装开通成功消息模板
   * 
   * @return
   */
  private Map<String, MsgData> wxTransactNotice() {
    WxTransactNotice notice = null;
    if (this instanceof WxTransactNotice) {
      notice = (WxTransactNotice) this;
    }
    Map<String, MsgData> data = new HashMap<String, MsgData>();
    data.put("first", new MsgData(getTitle()));
    // 昵称
    data.put("nickName", new MsgData(notice.getNickName()));
    // 电话
    data.put("telphone", new MsgData(notice.getTelphone()));
    // 服务类型
    data.put("serviceType", new MsgData(notice.getServiceType()));
    // 时间
    data.put("noticeDate", new MsgData(notice.getNoticeDate()));
    data.put("remark", new MsgData(notice.getRemark()));
    return data;
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
