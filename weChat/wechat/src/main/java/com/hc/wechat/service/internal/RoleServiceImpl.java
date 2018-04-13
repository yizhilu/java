package com.hc.wechat.service.internal;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.common.utils.StringValidateUtils;
import com.hc.wechat.entity.OperatorEntity;
import com.hc.wechat.entity.RoleEntity;
import com.hc.wechat.repository.RoleRepository;
import com.hc.wechat.repository.internal.RoleDao;
import com.hc.wechat.service.OperatorService;
import com.hc.wechat.service.RoleService;
import com.hc.wechat.service.SecurityService;


@Service("roleService")
public class RoleServiceImpl implements RoleService {

  /**
   * 角色repository自动注入.
   */
  @Autowired
  private RoleRepository roleRepository;
  /**
   * 角色dao自动注入.
   */
  @Autowired
  private RoleDao roleDao;

  @Autowired
  private OperatorService operatorService;

  @Autowired
  private SecurityService securityService;

  /**
   * 配置的那些不允许被删除被作废的角色
   */
  @Value("${roles.deleteDeny}")
  private String deleteDeny;

  @Override
  public List<RoleEntity> findByOperatorId(String operatorId) {
    Validate.notEmpty(operatorId, "operatorId must not be empty!");
    Set<RoleEntity> roles = this.roleRepository.findByOperatorId(operatorId);
    if (roles == null || roles.isEmpty()) {
      return Collections.emptyList();
    }

    List<RoleEntity> rolesList = new ArrayList<>(roles);
    return rolesList;
  }

  @Override
  public List<RoleEntity> findByCompetenceId(String competenceId) {
    Validate.notEmpty(competenceId, "competenceId must not be empty!");
    Set<RoleEntity> roles = this.roleRepository.findByCompetenceId(competenceId);
    if (roles == null || roles.isEmpty()) {
      return Collections.emptyList();
    }

    List<RoleEntity> rolesList = new ArrayList<RoleEntity>(roles);
    return rolesList;
  }

  @Override
  public List<RoleEntity> findByStatus(UseStatus useStatus) {
    Validate.notNull(useStatus, "useStatus must not null!");
    List<RoleEntity> rolesList = this.roleRepository.findByStatus(useStatus);
    if (rolesList == null || rolesList.size() == 0) {
      return Collections.emptyList();
    }

    return rolesList;
  }

  @Override
  public RoleEntity findRoleById(String roleId) {
    Validate.notNull(roleId, "role Id must not null!");
    RoleEntity currentRole = this.roleRepository.findOne(roleId);
    Validate.notNull(currentRole, "角色id为%s的角色信息不存在", roleId);

    return currentRole;
  }

  @Override
  public List<RoleEntity> findAll() {
    List<RoleEntity> rolesList = this.roleRepository.findAllWithModifer();
    // 如果条件成立说明系统该数据异常，这是直接抛出错误信息
    if (rolesList == null || rolesList.size() == 0) {
      throw new IllegalArgumentException("role infos error!!");
    }
    return rolesList;
  }

  @Override
  public Page<RoleEntity> getByConditions(String name, UseStatus status, Pageable pageable) {
    Map<String, Object> map = new HashMap<String, Object>();
    if (StringUtils.isNotBlank(name)) {
      map.put("name", name);
    }
    if (status != null) {
      map.put("status", status);
    }
    Page<RoleEntity> list = roleDao.getByConditions(map, pageable);
    return list;
  }

  @Override
  public List<RoleEntity> findByConditions(String name, UseStatus status) {
    Map<String, Object> map = new HashMap<String, Object>();
    if (StringUtils.isNotBlank(name)) {
      map.put("name", name);
    }
    if (status != null) {
      map.put("status", status);
    }
    List<RoleEntity> list = roleDao.findByConditions(map);
    return list;
  }

  @Override
  @Transactional
  public RoleEntity addRole(RoleEntity role) {
    // 进入传入信息的检查
    this.validateRoleBeforeAdd(role);
    // 开始插入
    RoleEntity newRole = roleRepository.saveAndFlush(role);
    // 查询系统内的默认管理员账号，并将添加的角色绑定到其身上
    OperatorEntity operator =
        operatorService.findByAccountAndStatus("admin", UseStatus.STATUS_NORMAL);
    if (null != operator) {
      securityService.bindRoleForOperator(newRole.getId(), operator.getId());
    }
    return newRole;
  }


  @Override
  public RoleEntity updateRole(RoleEntity role) {
    /*
     * 修改角色的过程如下： 1、首先确定当前role是可以进行修改的role 2、只进行comment的修改，其它即使传递了也不进行修改
     */
    Validate.notNull(role, "角色信息不能为空");
    String roleId = role.getId();
    String updateComment = role.getComment();
    Validate.notEmpty(roleId, "修改角色时，角色id不能为空!");
    Validate.notEmpty(updateComment, "角色中文说明一定要填写(comment)!");
    RoleEntity currentRole = this.roleRepository.getOne(roleId);
    Validate.notNull(currentRole, "角色id为%s的角色信息不存在", roleId);
    String[] deleteDenys = deleteDeny.split(",");
    // 1、========
    String currentName = currentRole.getName();
    // 如果条件成立，说明这个角色信息不能被修改
    for (String deleteDeny : deleteDenys) {
      if (StringUtils.equals(currentName, deleteDeny)) {
        try {
          throw new AccessDeniedException("the role not allow be disable！");
        } catch (AccessDeniedException e) {
          e.printStackTrace();
        }
      }
    }

    // 2、========
    currentRole.setModifier(role.getModifier());
    currentRole.setModifyDate(new Date());
    currentRole.setComment(updateComment);
    this.roleRepository.saveAndFlush(currentRole);
    return currentRole;
  }

  @Override
  @Transactional
  public RoleEntity disableRole(String roleId, OperatorEntity operator) {
    /*
     * 注意：在配置文件中已经设定的那些不能操作的角色信息，是不允许禁用的
     */
    Validate.notEmpty(roleId, "role id not be found!");
    RoleEntity currentRole = this.roleRepository.findOne(roleId);
    String[] deleteDenys = deleteDeny.split(",");
    // 如果条件成立，说明这个角色信息不能被删除（或者说作废）
    for (String deleteDeny : deleteDenys) {
      if (StringUtils.equals(currentRole.getName(), deleteDeny)) {
        Validate.isTrue(!deleteDeny.equals(currentRole.getName()),
            "the role not allow be disable！");
      }
    }

    // 禁用角色
    currentRole.setStatus(UseStatus.STATUS_DISABLE);
    currentRole.setModifyDate(new Date());
    currentRole.setModifier(operator);
    this.roleRepository.saveAndFlush(currentRole);
    return currentRole;
  }

  @Override
  @Transactional
  public RoleEntity enableRole(String roleId, OperatorEntity operator) {
    Validate.notEmpty(roleId, "role id not be found!");
    RoleEntity currentRole = this.roleRepository.findOne(roleId);

    // 启用角色
    currentRole.setStatus(UseStatus.STATUS_NORMAL);
    currentRole.setModifyDate(new Date());
    currentRole.setModifier(operator);
    this.roleRepository.saveAndFlush(currentRole);
    return currentRole;
  }

  /**
   * 该私有方法在新增一个role信息前，检查传入信息的证确定
   * 
   * @param role
   */
  private void validateRoleBeforeAdd(RoleEntity role) {
    Validate.notNull(role, "role input object not be null!");
    Validate.isTrue(StringUtils.isEmpty(role.getId()), "新增role时，其中的id属性不能设定任何值！");
    // 开始验证
    Validate.notEmpty(role.getName(), "角色名称不能为空!");
    // 必须是大写
    role.setName(role.getName().toUpperCase());
    RoleEntity oldRole = this.roleRepository.findByName(role.getName());
    Validate.isTrue(oldRole == null, "当前设定的角色名称（%s）已经被使用，请更换!", role.getName());
    // 当前的创建时间和修改时间要一起写入
    Date currentTime = new Date();
    role.setCreateDate(currentTime);
    role.setModifyDate(currentTime);
    // 说明性信息
    Validate.notBlank(role.getComment(), "角色中文说明一定要填写(comment)");
    Validate.isTrue(StringValidateUtils.validStrByLength(role.getComment(), 4, 20),
        "角色中文说明长度为4-20个字符");
    // 当前角色必须是状态正常的
    role.setStatus(UseStatus.STATUS_NORMAL);
  }
}
