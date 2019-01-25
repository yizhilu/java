package com.hc.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.UserEntity;


@Repository("userRepository")
public interface UserRepository
    extends
      JpaRepository<UserEntity, String>,
      JpaSpecificationExecutor<UserEntity> {
  /**
   * 该方法用于按照用户名查询指定的平台操作者
   * 
   * @param username 用户名账户
   * @param statusType 用户状态
   */
  UserEntity findByUserName(String username);

}
