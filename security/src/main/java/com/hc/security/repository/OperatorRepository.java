package com.hc.security.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.enums.StatusType;


@Repository("operatorRepostory")
public interface OperatorRepository extends
      JpaRepository<OperatorEntity, String>,
      JpaSpecificationExecutor<OperatorEntity> {
  /**
   * 该方法用于按照用户名查询指定的平台操作者
   * @param username 用户名账户
   * @param statusType 用户状态
   */
  OperatorEntity findByAccountAndStatus(String username, StatusType statusType);
  
  @Query("from OperatorEntity o "
    +" left join fetch o.createUser "
    +" left join fetch o.modifyUser "
    +" left join fetch o.roles "
    +" where o.id=:id "
    +" order by o.createDate desc")
  OperatorEntity findByIdFetch(@Param("id") String id);
  
  /**
   * 更新某个用户的登录时间，到指定时间
   * @param account 指定的用户账号
   * @param loginTime 指定的最新的登录时间
   */
  @Modifying
  @Query(value="update operator set last_login_time = :loginTime where account = :account " , nativeQuery = true)
  void updateLoginTime(@Param("account") String account ,@Param("loginTime") Date loginTime);
}
