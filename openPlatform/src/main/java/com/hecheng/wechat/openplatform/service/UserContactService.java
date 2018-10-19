package com.hecheng.wechat.openplatform.service;

import com.hecheng.wechat.openplatform.entity.UserContactEntity;

/**
 * 这是一个 接口 ，用于修改用户或者绑定用户的联系电话
 * 
 * @author hq
 *
 */
public interface UserContactService {

  /**
   * 修改用户的手机号码
   * 
   * @param useOnelyId
   * @param userPhone
   * @return
   */
  UserContactEntity updateUserContact(String useOnelyId, String userPhone);

  /**
   * 用户首次绑定联系电话
   * 
   * @param useOnelyId
   * @param userPhone
   * @return
   */

  UserContactEntity bindUserContact(String useOnelyId, String userPhone);

  /**
   * 通过user Id 查找 用户绑定的联系方式，例如联系电话
   * 
   * @return
   */
  UserContactEntity findByUserId(String userId);

}
