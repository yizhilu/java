package com.hc.wechat.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.entity.OperatorEntity;
import com.hc.wechat.entity.RoleEntity;

/**
 * 角色
 * 
 * @author hzh
 *
 */
public interface RoleService {
  /**
   * 查询指定的后台用户所绑定的角色信息(只包括角色基本信息)
   * 
   * @param operatorId 后台用户编号（数据层唯一编号）
   * @return
   */
  List<RoleEntity> findByOperatorId(String operatorId);

  /**
   * 查询指定的角色信息，按照角色的数据层编号查询
   * 
   * @param roleId 角色编号
   * @return
   */
  RoleEntity findRoleById(String roleId);

  /**
   * 查询指定的功能描述所绑定的角色信息(只包括角色基本信息)
   * 
   * @param competenceId 功能描述信息
   * @return
   */
  List<RoleEntity> findByCompetenceId(String competenceId);

  /**
   * 查询符合角色状态的信息
   * 
   * @param useStatus 目前只有两种状态的枚举，一种是正常另外一种是作废
   * @return
   */
  List<RoleEntity> findByStatus(UseStatus useStatus);

  /**
   * 查询目前系统中所有的角色信息，无论这些角色信息是否可用（但是只包括角色的基本信息）
   * 
   * @return
   */
  List<RoleEntity> findAll();

  /**
   * 该方法用于添加一个角色信息，这个角色信息的名字必须是唯一的
   * 
   * @param role 新的角色信息
   * @return
   */
  RoleEntity addRole(RoleEntity role);

  /**
   * 修改一个指定的角色信息，注意配置在roles.deleteDeny属性的信息不能进行修改操作<br>
   * 且指定的一个角色只能修改角色的comment信息
   * 
   * @param role 指定的修改信息
   * @return
   */
  RoleEntity updateRole(RoleEntity role);

  /**
   * 禁用某一个指定的角色信息（相当于删除）<br>
   * 只是系统中不能真正的删除某一个角色，只能是将这个角色作废掉或者恢复正常状态
   * 
   * @param roleId
   * @return
   */
  RoleEntity disableRole(String roleId, OperatorEntity operator);

  /**
   * 重新启用某一个指定的角色信息
   * 
   * @param roleId
   * @return
   */
  RoleEntity enableRole(String roleId, OperatorEntity operator);

  /**
   * 条件分页查询
   * 
   * @param name
   * @param status
   * @param pageable
   * @return
   */
  Page<RoleEntity> getByConditions(String name, UseStatus status, Pageable pageable);

  /**
   * 条件查询
   * 
   * @param name
   * @param status
   * @return
   */
  List<RoleEntity> findByConditions(String name, UseStatus status);
}
