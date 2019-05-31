package com.hc.security.service;

import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.enums.StatusType;

public interface AccountOperatorService {
  /**
   * 按账号id查询所属后台操作者
   * 
   * @param accountId
   * @return
   */
  OperatorEntity findByAccountId(String accountId);

  /**
   * 按账号和状态查询所属后台操作者
   * 
   * @param account
   * @param status
   * @return
   */
  OperatorEntity findByAccountAndStatus(String account, StatusType status);
}
