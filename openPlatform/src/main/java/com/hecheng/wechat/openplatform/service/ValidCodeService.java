package com.hecheng.wechat.openplatform.service;

import com.hecheng.wechat.openplatform.entity.ValidCodeEntity;

/**
 * 验证码service
 * 
 * @author hzh
 *
 */
public interface ValidCodeService {

  /**
   * 发送验证码
   * 
   * @param phoneNumber
   * @return
   */
  ValidCodeEntity sendValidCodeValue(String phoneNumber);

  /**
   * 获取phoneNumber最新的一条验证码 用于和sendValidCodeValue发送的验证码对比
   * 
   * @param phoneNumber
   * @return
   */
  ValidCodeEntity getTheLatestValidCode(String phoneNumber);
}
