package com.hecheng.wechat.openplatform.service.internal;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.hecheng.wechat.openplatform.common.message.MsgTemplate;
import com.hecheng.wechat.openplatform.common.message.ValidCodeMsg;
import com.hecheng.wechat.openplatform.common.utils.StringTools;
import com.hecheng.wechat.openplatform.entity.ValidCodeEntity;
import com.hecheng.wechat.openplatform.repository.ValidCodeRepository;
import com.hecheng.wechat.openplatform.service.MsgService;
import com.hecheng.wechat.openplatform.service.ValidCodeService;

@Service("validcodeService")
public class ValidCodeServiceImpl implements ValidCodeService {
  @Autowired
  private ValidCodeRepository validCodeRepository;
  @Resource(name = "smsService")
  private MsgService msgService;

  private static final Logger LOG = Logger.getLogger(ValidCodeServiceImpl.class);

  /** 签名 */
  @Value("${sms.validCodePrefix}")
  private String SMS_SIGNATURE;

  @Override
  public ValidCodeEntity getTheLatestValidCode(String phoneNumber) {
    List<ValidCodeEntity> validCodes =
        validCodeRepository.findByReceiveCodeOrderByCreateDateDesc(phoneNumber);
    if (validCodes == null || validCodes.isEmpty()) {
      return null;
    } else {
      return validCodes.get(0);
    }
  }


  @Transactional
  @CacheEvict(value = "validCode", allEntries = true)
  private ValidCodeEntity add(ValidCodeEntity validCode) {
    return validCodeRepository.saveAndFlush(validCode);
  }

  @Override
  @Transactional
  public ValidCodeEntity sendValidCodeValue(String phoneNumber) {
    String phoneRegex = "^1[34578]\\d{9}$";
    Validate.notBlank(phoneNumber, "手机号不能为空");
    Validate.isTrue(phoneNumber.matches(phoneRegex), "手机号码格式不正确");
    ValidCodeEntity validCode = null;
    boolean createFlag = false;
    ValidCodeEntity lastValidCode = getTheLatestValidCode(phoneNumber);
    if (lastValidCode == null) {
      createFlag = true;
    } else {
      Long interval = lastValidCode.getIntervalTime();
      if (interval > 120) {
        createFlag = true;
      }
    }
    if (createFlag) {
      String code = StringTools.generateRandom(6);
      // 签名
      String signature = SMS_SIGNATURE;
      validCode = new ValidCodeEntity();
      validCode.setParameter(code);
      validCode.setReceiveCode(phoneNumber);
      validCode.setMsg("【" + signature + "】您的验证码为：" + code + "，请在2分钟内输入。");
      validCode.setSendDate(null);
      validCode.setCreateDate(new Date());
      MsgTemplate vcm = new ValidCodeMsg(validCode);
      LOG.info("创建好游离态验证码并准备发送短信时的时间:" + new Date());
      if (msgService.sendMsg(vcm)) {
        LOG.info("调用发送短信接口成功后的时间:" + new Date());
        // 持久化
        add(validCode);
        LOG.info("验证码成功持久化后的时间:" + new Date());
      } else {
        Validate.isTrue(false, "手机发送验证码失败");
      }
    }
    return validCode;
  }

}
