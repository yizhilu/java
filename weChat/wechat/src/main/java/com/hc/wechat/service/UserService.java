package com.hc.wechat.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.entity.UserEntity;

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
   * 通过id查询用户并left join fetch查出第三方用户
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
   * 绑定手机
   * 
   * @param user
   * @param telephone
   */
  void bindPhone(String userId, String telephone);

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
