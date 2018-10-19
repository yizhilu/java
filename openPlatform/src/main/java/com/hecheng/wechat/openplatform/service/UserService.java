package com.hecheng.wechat.openplatform.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.entity.UserEntity;

/**
 * 
 * @author 作者 hc:
 * 
 */
public interface UserService {

  /**
   * 通过id获取用户
   * 
   * @param id
   * @return
   */
  UserEntity findById(String id);

  /**
   * 通过电话获取用户
   * 
   * @param id
   * @return
   */
  UserEntity findByPhone(String phone);

  /**
   * 通过id查询用户并left join fetch查出第三方用户和所属供应商.
   * 
   * @param id
   * @return
   */
  UserEntity findByIdFetch(String id);

  /**
   * 创建用户
   * 
   * @param user
   * @return
   */
  UserEntity create(UserEntity user);

  /**
   * 修改用户信息
   * 
   * @param user
   */
  UserEntity modify(UserEntity user);

  /**
   * 按条件分页查询
   * 
   * @param nickName
   * @param status
   * @param telephone
   * @param pageable
   * @return
   */

  Page<UserEntity> getByConditions(String nickName, UseStatus status, String telephone,
      Pageable pageable);
}
