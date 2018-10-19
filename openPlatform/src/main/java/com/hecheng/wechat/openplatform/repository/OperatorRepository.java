package com.hecheng.wechat.openplatform.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.entity.OperatorEntity;


/**
 * 管理员实体的数据层服务
 * 
 * @author yinwenjie
 * @date 2017年8月10日
 * @version V1.0
 */
@Repository("operatorRepository")
public interface OperatorRepository
    extends
      JpaRepository<OperatorEntity, String>,
      JpaSpecificationExecutor<OperatorEntity> {

  /**
   * 根据账号查找代理商——状态满足条件的。注意，只有基本信息
   * 
   * @param account 账号
   * @param status 账号状态，请参见枚举
   */
  OperatorEntity findByAccountAndStatus(String account, UseStatus status);

  /**
   * 根据账号查找代理商。注意，只有基本信息
   * 
   * @param account 账号
   */
  OperatorEntity findByAccount(String account);

  /**
   * 为指定的后台用户绑定绑定固定的角色,TODO 具体绑定什么角色还未知，先用admin代替
   * 
   * @param operatorId 管理员数据层编号
   */
  @Modifying
  @Query(value = "insert into role_operator(operator_id , role_id) value (:operatorId , '1')", nativeQuery = true)
  void operatorBindRole(@Param("operatorId") String operatorId);

  /**
   * 更新某个后台用户的登录时间，到指定时间
   * 
   * @param account 指定的代理商账户
   * @param loginTime 指定的最新的登录时间
   * @author yinwenjie
   */
  @Modifying
  @Query(value = "update operator set last_login_time = :loginTime where account = :account ", nativeQuery = true)
  void updateLoginTime(@Param("account") String account, @Param("loginTime") Date loginTime);
}
