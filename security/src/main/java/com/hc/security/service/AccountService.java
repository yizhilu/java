package com.hc.security.service;

import com.hc.security.entity.AccountEntity;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.UserEntity;
import com.hc.security.entity.enums.StatusType;

public interface AccountService {
  /**
   * 按账号名查询账号
   * 
   * @param userName
   * @return
   */
  AccountEntity findByUserName(String userName);

  /**
   * 按账号查询正常的操作者
   * 
   * @param account
   * @param status
   * @return
   */
  OperatorEntity findOperatorByAccountAndStatus(String account, StatusType status);

  /**
   * 按账号查询用户
   * 
   * @param username
   * @return
   */
  UserEntity findUserByAccount(String username);

  /**
   * 根据账号id查询账号所属的用户
   * 
   * @param accountId
   * @return
   */
  UserEntity findUserByAccountId(String accountId);

  /**
   * 根据账号id查询账号所属的操作者
   * 
   * @param accountId
   * @return
   */
  OperatorEntity findOperatorByAccountId(String id);

  /***
   * 创建操作者账号
   * 
   * @param operator
   * @param account
   */
  void create(OperatorEntity operator, AccountEntity account);

  /**
   * 创建用户账号
   * 
   * @param User
   * @param account
   */
  void create(UserEntity User, AccountEntity account);

}
