package com.hecheng.wechat.openplatform.service.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hecheng.wechat.openplatform.common.message.MsgTemplate;
import com.hecheng.wechat.openplatform.common.message.ValidCodeMsg;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.service.MsgService;
import com.hecheng.wechat.openplatform.service.feign.AliSmsMsgApiService;


/**
 * 发送短信
 * 
 * @author cxj
 *
 */
@Service("smsService")
public class SmsServiceImpl implements MsgService {
  private static Logger log = Logger.getLogger(SmsServiceImpl.class);
  @Autowired
  private AliSmsMsgApiService aliSmsMsgApiService;
  /**
   * 验证码模板
   */
  @Value("${ali.verificationCodeTemp}")
  private String ali_sms_codetnum;

  @Value("${ali.aliAppcode}")
  private String ali_appcode;

  @Override
  public boolean sendMsg(MsgTemplate msg) {
    boolean flag = false;
    UserEntity user = (UserEntity) msg.getReceiver();
    if (user == null || user.getTelephone() == null) {
      log.error("===user===" + user + "===telephone===" + user.getTelephone());
      return flag;
    }
    Map<String, Object> map = getAliSpecialPara(msg);
    // 参数1可选,对应模板中短信的参数,json格式,如果短信内容中,没有需要替换的参数,此处可以不传值
    // 参数2必选,接收短信发送的电话号码,11位的电话号码
    // 参数3必选,模板编号,创建模板时返回的tNum,注意只有审核通过的模板才可以发(阿里短信接口内置7个通用模板)
    String param = null;
    if (map.get("parameter") != null) {
      param = map.get("parameter").toString();
    }
    String result = aliSmsMsgApiService.sendSmsMsg("APPCODE " + ali_appcode, param,
        user.getTelephone(), map.get("tNum").toString());
    try {
      String flg = JSON.parseObject(result).get("showapi_res_code").toString();
      if ("0".equals(flg)) {
        JSONObject showapi_res_body =
            JSON.parseObject(JSON.parseObject(result).get("showapi_res_body").toString());
        String ret_code = showapi_res_body.getString("ret_code");
        if ("0".equals(ret_code)) {
          return true;
        }
      }
    } catch (Exception e) {
      return flag;
    }
    return flag;
  }

  /**
   * 返回第三方返回数据
   */
  @Override
  public String sendMsgBackString(MsgTemplate msg) {
    UserEntity user = (UserEntity) msg.getReceiver();
    if (user == null || user.getTelephone() == null) {
      log.error("===user===" + user + "===telephone===" + user.getTelephone());
      return "";
    }
    try {
      Map<String, Object> map = getAliSpecialPara(msg);
      // 参数1可选,对应模板中短信的参数,json格式,如果短信内容中,没有需要替换的参数,此处可以不传值
      // 参数2必选,接收短信发送的电话号码,11位的电话号码
      // 参数3必选,模板编号,创建模板时返回的tNum,注意只有审核通过的模板才可以发(阿里短信接口内置7个通用模板)
      String param = null;
      if (map.get("parameter") != null) {
        param = map.get("parameter").toString();
      }
      String result = aliSmsMsgApiService.sendSmsMsg("APPCODE " + ali_appcode, param,
          user.getTelephone(), map.get("tNum").toString());
      String flg = JSON.parseObject(result).get("showapi_res_code").toString();
      return flg;
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 获取阿里接口短信模板内容中的参数json和对应模板的编号.
   * 
   * @param msgTemplate 模板
   * @return Map<String, Object>
   */
  private Map<String, Object> getAliSpecialPara(MsgTemplate msgTemplate) {
    Map<String, Object> map = new HashMap<String, Object>();
    String metaValue = msgTemplate.getMetaValue();
    if (metaValue.equals(ValidCodeMsg.META_VALUE)) {
      ValidCodeMsg smsMsg = (ValidCodeMsg) msgTemplate;
      map = getValideCodeContent(smsMsg);
    }
    return map;
  }

  /**
   * 短信发送-验证码.
   * 
   * @param codeMsg
   * @return 模板参数json串，模板编号
   */
  private Map<String, Object> getValideCodeContent(ValidCodeMsg codeMsg) {
    Map<String, Object> map = new HashMap<String, Object>();
    String code = codeMsg.getCode();
    map.put("parameter", "{\"code\":'" + code + "'}");
    map.put("tNum", ali_sms_codetnum);
    return map;
  }
}
