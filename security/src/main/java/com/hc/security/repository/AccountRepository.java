package com.hc.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.AccountEntity;


@Repository("accountRepository")
public interface AccountRepository
    extends
      JpaRepository<AccountEntity, String>,
      JpaSpecificationExecutor<AccountEntity> {

  /**
   * 查询指定账号名称的账号
   * 
   * @param userName
   * @return
   */
  @Query(value = "from AccountEntity where userName=:userName")
  AccountEntity findByUserName(@Param("userName") String userName);

}
