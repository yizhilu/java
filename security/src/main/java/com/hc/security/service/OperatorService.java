package com.hc.security.service;

import java.util.Date;

import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.enums.StatusType;

public interface OperatorService {

  /**
   * 新增平台操作者
   * 
   * @param operator
   * @return OperatorEntity
   */
  OperatorEntity addOperator(OperatorEntity operator);

  /**
   * 删除管理员(逻辑)
   * 
   * @param operatorId 管理员id
   */
  void deleteOperatorById(String operatorId, OperatorEntity operator);

  /**
   * 该方法用于按照用户名查询指定的平台操作者
   * 
   * @param username 用户名账户
   * @param statusType 用户状态
   */
  OperatorEntity findByAccountAndStatus(String username, StatusType statusType);


  /**
   * 通过id获取管理员
   * 
   * @param operatorId 管理员id
   * @return OperatorEntity
   */
  OperatorEntity getById(String operatorId);

  /**
   * 更新某个管理员的登录时间，到指定时间
   * 
   * @param account 指定的代理商账户
   * @param loginTime 指定的最新的登录时间
   * @author yinwenjie
   */
  void updateLoginTime(String account, Date loginTime);

  /**
   * 修改密码
   * 
   * @param operatorId 管理员id
   * @param oldPass 原密码
   * @param newPass 新密码
   * @param modifyUser 修改人
   */
  void updatePassword(String operatorId, String oldPass, String newPass, OperatorEntity modifyUser);

  /**
   * 修改管理员信息
   * 
   * @param operator 管理员
   * @return OperatorEntity
   */
  OperatorEntity modify(OperatorEntity operator);

  /**
   * 禁用或启用管理员账号
   * 
   * @param operatorId 管理员id
   * @param modifyUser 修改人
   * @param flag 判断该次操作是禁用还是启用。true为启用
   */
  void disableOrUsable(String operatorId, boolean flag, OperatorEntity modifyUser);

}
