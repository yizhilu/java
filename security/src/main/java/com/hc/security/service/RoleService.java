package com.hc.security.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.RoleEntity;
import com.hc.security.entity.enums.StatusType;


public interface RoleService {
  /**
   * 查询指定用户下所涉及的角色信息
   * 
   * @param operatorId 指定的操作者id
   * @return
   */
  Set<RoleEntity> findByOperatorId(String operatorId);

  /**
   * 查询指定功能下所涉及的角色信息
   * 
   * @param operatorId 指定的功能id
   * @return
   */
  Set<RoleEntity> findByCompetenceId(String competenceId);

  /**
   * 查询指定的角色信息，按照角色的数据层编号查询
   * 
   * @param roleId 角色编号
   * @return
   */
  RoleEntity findRoleById(String roleId);

  /**
   * 查询符合角色状态的信息
   * 
   * @param statusType 目前只有两种状态的枚举，一种是正常另外一种是禁用
   * @return
   */
  List<RoleEntity> findByStatus(StatusType statusType);

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
   * 条件分页查询.
   * 
   * @param params 条件
   * @param pageable 分页
   * @return age<RoleEntity>
   */
  Page<RoleEntity> getByCondition(Map<String, Object> params, Pageable pageable);
}
