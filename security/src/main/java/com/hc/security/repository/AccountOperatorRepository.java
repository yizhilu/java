package com.hc.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.AccountOperatorEntity;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.enums.StatusType;


@Repository("accountOperatorRepository")
public interface AccountOperatorRepository
    extends
      JpaRepository<AccountOperatorEntity, String>,
      JpaSpecificationExecutor<AccountOperatorEntity> {
  /**
   * 查询账号id的所属用户
   * 
   * @param accountId
   * @return
   */
  @Query(value = "select ao.operator from AccountOperatorEntity ao where ao.account.id=:accountId")
  OperatorEntity findByAccountId(@Param("accountId") String accountId);

  /**
   * 按账号和状态查询所属后台操作者
   * 
   * @param account
   * @param status
   * @return
   */
  @Query(value = "select ao.operator from AccountOperatorEntity ao "
      + "where ao.account.userName=:account " + "and ao.account.status=1 "
      + "and ao.operator.status=:status ")
  OperatorEntity findByAccountAndStatus(@Param("account") String account,
      @Param("status") StatusType status);

}
