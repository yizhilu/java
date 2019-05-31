package com.hc.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.AccountUserEntity;
import com.hc.security.entity.UserEntity;


@Repository("accountUserRepository")
public interface AccountUserRepository
    extends
      JpaRepository<AccountUserEntity, String>,
      JpaSpecificationExecutor<AccountUserEntity> {
  /**
   * 查询账号id的所属用户
   * 
   * @param accountId
   * @return
   */
  @Query(value = "select au.user from AccountUserEntity au where au.account.id=:accountId")
  UserEntity findByAccountId(@Param("accountId") String accountId);

  /**
   * 查询账号名的用户（状态为正常的）
   * 
   * @param userName
   * @return
   */
  @Query(value = "select au.user from AccountUserEntity au where au.account.userName=:userName "
      + "and au.account.status=1")
  UserEntity findByUserName(@Param("userName") String userName);

}
