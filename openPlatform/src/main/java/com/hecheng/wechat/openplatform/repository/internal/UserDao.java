package com.hecheng.wechat.openplatform.repository.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.entity.UserEntity;

/**
 * 
 * @author hc
 *
 */
public interface UserDao {

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
