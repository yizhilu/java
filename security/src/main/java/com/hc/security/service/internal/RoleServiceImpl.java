package com.hc.security.service.internal;

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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.RoleEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.repository.RoleRepository;
import com.hc.security.repository.internal.RoleDao;
import com.hc.security.service.RoleService;


@Service("roleService")
public class RoleServiceImpl implements RoleService {
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private RoleDao roleDao;
  /**
   * 配置的那些不允许被删除被作废的角色
   */
  @Value("${roles.deleteDeny}")
  private String[] deleteDenys;

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.operation.service.RoleService#findByOperatorId(java.lang.String)
   */
  @Override
  @Cacheable(value = "role", key = "'findByOperatorId.' + {#operatorId}")
  public Set<RoleEntity> findByOperatorId(String operatorId) {
    if (StringUtils.isEmpty(operatorId)) {
      return null;
    }
    return this.roleRepository.findByOperatorId(operatorId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.operation.service.RoleService#findByCompetenceId(java.lang.String)
   */
  @Override
  @Cacheable(value = "role", key = "'findByCompetenceId.' + {#competenceId}")
  public Set<RoleEntity> findByCompetenceId(String competenceId) {
    if (StringUtils.isEmpty(competenceId)) {
      return null;
    }
    return this.roleRepository.findByCompetenceId(competenceId);
  }

  @Override
  @Cacheable(value = "role", key = "'findRoleById.' + {#roleId}")
  public RoleEntity findRoleById(String roleId) {
    Validate.notNull(roleId, "角色id不能为空!");
    RoleEntity currentRole = this.roleRepository.findOne(roleId);
    if (currentRole == null) {
      throw new IllegalArgumentException("没有找到角色!");
    }

    return currentRole;
  }

  @Override
  @Cacheable(value = "role", key = "'findByStatus.' + {#statusType}")
  public List<RoleEntity> findByStatus(StatusType statusType) {
    Validate.notNull(statusType, "角色状态不能为空!");
    List<RoleEntity> rolesList = this.roleRepository.findByStatus(statusType);
    if (rolesList == null || rolesList.size() == 0) {
      return Collections.emptyList();
    }

    return rolesList;
  }

  @Override
  @Cacheable(value = "role", key = "'findAll'")
  public List<RoleEntity> findAll() {
    List<RoleEntity> rolesList = this.roleRepository.findAllWithModifer();
    if (rolesList == null || rolesList.size() == 0) {
      return Collections.emptyList();
    }
    return rolesList;
  }

  @Override
  @Transactional
  @CacheEvict(value = "role", allEntries = true)
  public RoleEntity addRole(RoleEntity role) {
    // 进入传入信息的检查
    this.validateRoleBeforeAdd(role);
    // 开始插入
    RoleEntity currentRole = this.roleRepository.save(role);
    return currentRole;
  }

  @Override
  @Transactional
  @CacheEvict(value = "role", allEntries = true)
  public RoleEntity updateRole(RoleEntity role) {
    /*
     * 修改角色的过程如下： 1、首先确定当前role是可以进行修改的role 2、只进行comment的修改，其它即使传递了也不进行修改
     */
    Validate.notNull(role, "角色不能为空");
    String roleId = role.getId();
    String updateComment = role.getComment();
    Validate.notEmpty(roleId, "修改role时，角色Id不能为空!");
    Validate.notEmpty(updateComment, "角色中文说明一定要填写(comment)!");
    RoleEntity currentRole = this.roleRepository.getOne(roleId);
    Validate.notNull(currentRole, "角色不存在");

    // 1、========
    String currentName = currentRole.getName();
    // 如果条件成立，说明这个角色信息不能被修改
    for (String deleteDeny : deleteDenys) {
      if (StringUtils.equals(currentName, deleteDeny)) {
        throw new AccessDeniedException(deleteDeny + "不能被修改！");
      }
    }

    // 2、========
    currentRole.setModifier(role.getModifier());
    currentRole.setModifyDate(new Date());
    currentRole.setComment(updateComment);
    this.roleRepository.save(currentRole);
    return currentRole;
  }

  @Override
  @Transactional
  @CacheEvict(value = "role", allEntries = true)
  public RoleEntity disableRole(String roleId, OperatorEntity operator) {
    /*
     * 注意：在配置文件中已经设定的那些不能操作的角色信息，是不允许禁用的
     */
    Validate.notEmpty(roleId, "禁用role时，角色Id不能为空!");
    RoleEntity currentRole = this.roleRepository.findOne(roleId);

    // 如果条件成立，说明这个角色信息不能被删除（或者说作废）
    for (String deleteDeny : deleteDenys) {
      if (StringUtils.equals(currentRole.getName(), deleteDeny)) {
        throw new AccessDeniedException(deleteDeny + "不能被禁用！");
      }
    }

    // 禁用角色
    currentRole.setStatus(StatusType.STATUS_DISABLE);
    currentRole.setModifyDate(new Date());
    currentRole.setModifier(operator);
    this.roleRepository.save(currentRole);
    return currentRole;
  }

  @Override
  @Transactional
  @CacheEvict(value = "role", allEntries = true)
  public RoleEntity enableRole(String roleId, OperatorEntity operator) {
    Validate.notEmpty(roleId, "重新启用role时，角色Id不能为空!");
    RoleEntity currentRole = this.roleRepository.findOne(roleId);
    // 启用角色
    currentRole.setStatus(StatusType.STATUS_NORMAL);
    currentRole.setModifyDate(new Date());
    currentRole.setModifier(operator);
    this.roleRepository.save(currentRole);
    return currentRole;
  }

  @Override
  public Page<RoleEntity> getByCondition(Map<String, Object> params, Pageable pageable) {
    Map<String, Object> map = new HashMap<String, Object>();
    if (params.get("roleName") != null) {
      map.put("roleName", String.valueOf(params.get("roleName")));
    }
    if (params.get("statusType") != null) {
      map.put("statusType",
          StatusType.get(Integer.valueOf(String.valueOf(params.get("statusType")))));
    }
    Page<RoleEntity> list = roleDao.getByCondition(map, pageable);
    return list;
  }

  /**
   * 该私有方法在新增一个role信息前，检查传入信息的证确定
   * 
   * @param role
   */
  private void validateRoleBeforeAdd(RoleEntity role) {
    Validate.notNull(role, "角色不能为空!");
    Validate.isTrue(StringUtils.isEmpty(role.getId()), "新增role时，其中的id属性不能设定任何值！");
    // 开始验证
    Validate.notEmpty(role.getName(), "角色名不能为空!");
    // 必须是大写
    role.setName(role.getName().toUpperCase());
    RoleEntity oldRole = this.roleRepository.findByName(role.getName());
    Validate.isTrue(oldRole == null, "当前设定的角色名称（role name）已经被使用，请更换!");
    // 当前的创建时间和修改时间要一起写入
    Date currentTime = new Date();
    role.setCreateDate(currentTime);
    role.setModifyDate(currentTime);
    // 说明性信息
    Validate.notEmpty(role.getComment(), "角色中文说明一定要填写(comment)");
    // 当前角色必须是状态正常的
    role.setStatus(StatusType.STATUS_NORMAL);
  }

}
