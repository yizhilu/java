package com.hc.security.repository.internal;

import java.util.Date;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.enums.StatusType;


public interface OperatorDao {

  /**
   * 保存及更新管理员对象
   * 
   * @param operator 管理员对象
   * @return OperatorEntity
   */
  OperatorEntity saveAndFlush(OperatorEntity operator);

  /**
   * 物理删除管理员
   * 
   * @param operator 管理员对象
   */
  void deleteOperator(OperatorEntity operator);

  /**
   * 更新某个管理员的登录时间，到指定时间
   * 
   * @param account
   * @param loginTime
   */
  void updateLoginTime(String account, Date loginTime);

  /**
   * 根据id获取管理员信息
   * 
   * @param id 管理员id
   * @return OperatorEntity
   */
  OperatorEntity getById(String id);

  /**
   * 根据条件获取管理员列表
   * 
   * @param map 条件集
   * @param pageable 翻页对象
   * @return Page<OperatorEntity>
   */
  Page<OperatorEntity> findOperatorByParmas(Map<String, Object> map, Pageable pageable);

  /**
   * 按照登陆账号和状态查询指定的平台操作者
   * 
   * @param username 登陆账号
   * @param statusType 状态
   * @return
   */
  OperatorEntity findByAccountAndStatus(String username, StatusType statusType);
}
