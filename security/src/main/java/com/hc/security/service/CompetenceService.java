package com.hc.security.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.security.entity.CompetenceEntity;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.enums.StatusType;


public interface CompetenceService {
  /**
   * 查询符合权限状态的信息.
   * 
   * @param statusType 状态（正常/禁用）
   * @return List<CompetenceEntity>
   */
  List<CompetenceEntity> findByStatus(StatusType statusType);

  /**
   * 查询为当前URL设置的功能信息，注意，这里没有通过method进行过滤.
   * 
   * @param resource 权限串
   * @return List<CompetenceEntity>
   */
  List<CompetenceEntity> findByResource(String resource);

  /**
   * 查询指定的角色已绑定的功能信息，无论这些功能是否可用（状态是否正常）.
   * 
   * @param roleId 角色id
   * @return List<CompetenceEntity>
   */
  List<CompetenceEntity> findByRoleId(String roleId);

  /**
   * 查询指定的角色已绑定的功能信息，且这些功能状态符合查询的要求.
   * 
   * @param roleId 角色id
   * @param status 状态（正常/禁用）
   * @return List<CompetenceEntity>
   */
  List<CompetenceEntity> findByRoleId(String roleId, StatusType status);

  /**
   * 查询当前用户所绑定的角色下，存在于当前功能下的所有功能，并按照orderIndex进行排序<br>
   * 请注意，如果拥有ADMIN超级管理员权限，则显示当前父级功能下所有（可用）子级功能
   * 
   * @param opUserAccount 指定的用户
   * @param parentCompetenceId 指定的父级功能编号
   */
  List<CompetenceEntity> findChildCompetences(String opUserAccount, String parentCompetenceId);

  /**
   * 查询当前用户所绑定的角色下，存在主目录上的所有根功能，并按照orderIndex进行排序<br>
   * 请注意，如果拥有ADMIN超级管理员权限，则显示当前所有（可用）主目录根级功能
   * 
   * @param opUserAccount 指定的用户
   */
  List<CompetenceEntity> findRootCompetences(String opUserAccount);

  /**
   * 基于功能的中文说明信息，确定当前登录者针对这个功能有没有被授权
   * 
   * @param opUserAccount 当前登录的用户账户信息
   * @param comment 功能中文名
   * @return 如果被授权则返回true；其它情况返回false
   */
  Boolean findCompetencePermissionByComment(String opUserAccount, String comment);

  /**
   * 基于功能的URL信息，确定当前登录者针对这个功能有没有被授权
   * 
   * @param opUserAccount
   * @param url 功能的url信息，权限URL串。注意如果URL信息中有参数信息。则使用“{param}”代替。例如：<br>
   *        /vz/param4/{param}
   * @param methods POST或者POST|GET|DELETE|PATCH等，传入的大小写没有关系，反正都会转为大写
   * @return 如果被授权则返回true；其它情况返回false
   */
  Boolean findCompetencePermissionByUrl(String opUserAccount, String url, String methods);

  /**
   * 基于功能的唯一编号信息，确定当前登录者针对这个功能有没有被授权
   * 
   * @param opUserAccount 当前登录的用户账户信息
   * @param competenceId 指定的功能编号信息
   * @return 如果被授权则返回true；其它情况返回false
   */
  Boolean findCompetencePermissionById(String opUserAccount, String competenceId);

  /**
   * 根据id获取权限详情（单条）.
   * 
   * @param id 权限id
   * @return CompetenceEntity
   */
  CompetenceEntity getById(String id);

  /**
   * 条件分页查询.
   * 
   * @param params 条件
   * @param pageable 分页
   * @return Page<CompetenceEntity>
   */
  Page<CompetenceEntity> getByCondition(Map<String, Object> params, Pageable pageable);

  /**
   * 根据状态查询该状态下的所有权限信息.
   * 
   * @param status 状态
   * @return List<CompetenceEntity>
   */
  List<CompetenceEntity> getAll(StatusType status);

  /**
   * 根据methods和resource查找是否存在权限对象.
   * 
   * @param method 方法
   * @param resource 权限串
   * @param id 权限id
   * @return boolean
   */
  boolean isExit(String methods, String resource, String id);

  /**
   * 新增权限.
   * 
   * @param comp 权限对象
   * @return CompetenceEntity
   */
  CompetenceEntity addCompetence(CompetenceEntity comp);

  /**
   * 修改权限.
   * 
   * @param comp 权限对象
   * @return CompetenceEntity
   */
  CompetenceEntity updateCompetence(CompetenceEntity comp);

  /**
   * 更新指定功能为主目录“根”级功能
   * 
   * @param competenceId 指定的功能编号
   * @param operator 当前操作者信息
   * @return
   */
  CompetenceEntity updateRootCompetence(String competenceId, OperatorEntity operator);

  /**
   * 启用/禁用.
   * 
   * @param id 权限id
   * @param flag 标识（true：启用操作；false：禁用操作）
   * @param operator 操作者
   * @return CompetenceEntity
   */
  CompetenceEntity diableOrUndisable(String id, Boolean flag, OperatorEntity operator);

  /**
   * 该服务将若干个子级功能绑定到父级功能下，如果某个子级功能已经绑定了父级功能，则之前的绑定被去掉
   * 
   * @param parentCompetenceId 父级功能
   * @param childCompetenceIds 若干个子级功能
   * @return
   */
  void bindChild(String parentCompetenceId, String... childCompetenceIds);

  /**
   * 该服务将若干个子级功能从他们当前的父级功能下解绑。
   * 
   * @param childCompetenceIds 若干个将要解绑的子级功能
   */
  void unBindChild(String... childCompetenceIds);
}
