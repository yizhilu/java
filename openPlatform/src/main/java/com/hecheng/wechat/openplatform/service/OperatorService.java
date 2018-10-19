package com.hecheng.wechat.openplatform.service;

import java.util.Date;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.entity.OperatorEntity;


/**
 * 管理员信息的服务层.
 * 
 * @author yinwenjie
 */
public interface OperatorService {
  /**
   * 根据id获取管理员详情.
   * 
   * @param id 逻辑键
   * @return AgentEntity
   */
  OperatorEntity findById(String id);

  /**
   * 根据账号查找管理员.
   * 
   * @param account 账号
   */
  OperatorEntity findByAccount(String account);

  /**
   * 根据账号查找管理员(未禁用的).
   * 
   * @param account 账号
   * @param status 账号状态，请参见枚举
   */
  OperatorEntity findByAccountAndStatus(String account, UseStatus status);

  /**
   * 条件分页查询.
   * 
   * @param params 条件
   * @param pageable 分页
   * @return Page<AgentEntity>
   */
  Page<OperatorEntity> getByConditions(Map<String, Object> params, Pageable pageable);

  /**
   * 注册一个新的管理员.
   * 
   * @param operator
   * @return
   */
  OperatorEntity create(OperatorEntity operator);

  /**
   * 创建管理员并设置角色
   * 
   * @param operator
   * @param roleNames
   * @return
   */
  OperatorEntity create(OperatorEntity operator, String[] roleNames);

  /**
   * 修改 管理员信息.
   * 
   * @param agent 对象
   * @return OperatorEntity
   */
  OperatorEntity modify(OperatorEntity operator);

  /**
   * 启用/禁用管理员.
   * 
   * @param id 逻辑键
   * @param flag 操作类型（true：启用；false：禁用）
   * @return AgentEntity
   */
  OperatorEntity diableOrUndisable(String id, Boolean flag, OperatorEntity operator);

  /**
   * 更新某个管理员的登录时间，到指定时间
   * 
   * @param account 指定的管理员账户
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
   * 把指定用戶的密碼重置
   * 
   * @param id
   */
  void resetPassword(String id);

}
