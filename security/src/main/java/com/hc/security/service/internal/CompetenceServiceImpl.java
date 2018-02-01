package com.hc.security.service.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hc.security.entity.CompetenceEntity;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.RoleEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.repository.CompetenceRepository;
import com.hc.security.repository.internal.CompetenceDao;
import com.hc.security.service.CompetenceService;
import com.hc.security.service.OperatorService;
import com.hc.security.service.RoleService;


@Service("competenceService")
public class CompetenceServiceImpl implements CompetenceService {
  @Autowired
  private CompetenceRepository competenceRepository;
  /**
   * 功能dao自动注入.
   */
  @Autowired
  private CompetenceDao competenceDao;

  @Autowired
  private OperatorService operatorService;
  
  @Autowired
  private RoleService roleService; 

  @Override
  @Cacheable(value = "competence", key = "'findByStatus' + {#statusType}")
  public List<CompetenceEntity> findByStatus(StatusType statusType) {
    Validate.notNull(statusType, "功能状态不能为空!");
    return this.competenceRepository.findByStatus(statusType);
  }

  @Override
  @Cacheable(value = "competence", key = "'findByResource' + {#resource}")
  public List<CompetenceEntity> findByResource(String resource) {
    Validate.notNull(resource, "功能URL串resource不能为空!");
    return this.competenceRepository.findByResource(resource);
  }

  @Override
  @Cacheable(value = "competence", key = "'findByRoleId' + {#roleId}")
  public List<CompetenceEntity> findByRoleId(String roleId) {
    Validate.notNull(roleId, "角色roleId不能为空!");
    return this.competenceRepository.findByRoleId(roleId);
  }

  @Override
  @Cacheable(value = "competence", key = "'findByRoleId' + {#roleId} + '.' + {#status}")
  public List<CompetenceEntity> findByRoleId(String roleId, StatusType status) {
    Validate.notNull(roleId, "角色roleId不能为空!");
    Validate.notNull(status, "功能状态不能为空!");
    return this.competenceRepository.findByRoleId(roleId, status);
  }

  @Override
  @Cacheable(value = "competence", key = "'getById.' + {#id}")
  public CompetenceEntity getById(String id) {
    Validate.notEmpty(id, "参数错误，功能id不能为空！");
    CompetenceEntity competence = competenceRepository.findOne(id);
    return competence;
  }

  @Override
  @Cacheable(value = "competence", key = "'isExit.' + {#methods} + '.' + {#resource} + '.' + {#id}")
  public boolean isExit(String methods, String resource, String id) {
    int count = 0;
    if (StringUtils.isBlank(id)) {
      count = competenceRepository.findByMethodsAndResource(methods, resource);
    } else {
      count = competenceRepository.findByMethodsAndResourceAndId(methods, resource, id);
    }
    if (count > 0) {
      return true;
    }
    return false;
  }

  @Override
  public Page<CompetenceEntity> getByCondition(Map<String, Object> params, Pageable pageable) {
    Map<String, Object> map = new HashMap<String, Object>();
    if (params.get("methods") != null) {
      map.put("methods", String.valueOf(params.get("methods")));
    }
    if (params.get("comment") != null) {
      map.put("comment", String.valueOf(params.get("comment")));
    }
    if (params.get("status") != null) {
      map.put("status", StatusType.get(Integer.valueOf(String.valueOf(params.get("status")))));
    }
    if (params.get("resource") != null) {
      map.put("resource", String.valueOf(params.get("resource")));
    }
    Page<CompetenceEntity> list = competenceDao.getByCondition(map, pageable);
    return list;
  }
  
  /* (non-Javadoc)
   * @see com.cheshangma.operation.service.CompetenceService#getAll(com.cheshangma.operation.common.enums.StatusType)
   */
  @Override
  public List<CompetenceEntity> getAll(StatusType status) {
    Validate.notNull(status, "功能状态参数不能为空！");
    List<CompetenceEntity> list = competenceRepository.getAll(status);
    return list;
  }
  
  /* (non-Javadoc)
   * @see com.cheshangma.operation.service.CompetenceService#findChildCompetences(java.lang.String, String)
   */
  @Override
  public List<CompetenceEntity> findChildCompetences(String opUserAccount, String parentCompetenceId) {
    OperatorEntity currentOperator = this.operatorService.findByAccountAndStatus(opUserAccount, StatusType.STATUS_NORMAL);
    if(currentOperator == null) {
      throw new IllegalArgumentException("未找到指定用户，可能是用户状态不正确!");
    }
    // 这是当前父级功能下已绑定的子级功能
    List<CompetenceEntity> childCompetences = this.competenceRepository.findByParentCompetences(parentCompetenceId);
    if(childCompetences == null || childCompetences.isEmpty()) {
      return null;
    }
    
    return this.filterCompetence(childCompetences, currentOperator.getId());
  }

  /* (non-Javadoc)
   * @see com.cheshangma.operation.service.CompetenceService#findRootCompetences(java.lang.String)
   */
  @Override
  public List<CompetenceEntity> findRootCompetences(String opUserAccount) {
    OperatorEntity currentOperator = this.operatorService.findByAccountAndStatus(opUserAccount, StatusType.STATUS_NORMAL);
    if(currentOperator == null) {
      throw new IllegalArgumentException("未找到指定用户，可能是用户状态不正确!");
    }
    // 这是所有可用的根级功能
    List<CompetenceEntity> rootCompetences = this.competenceRepository.findRootCompetence();
    if(rootCompetences == null || rootCompetences.isEmpty()) {
      return null;
    }
    
    return this.filterCompetence(rootCompetences, currentOperator.getId());
  }
  
  /**
   * 这个私有方法，用于过滤指定的用户（operatorId）针对competences集合有哪些功能是可以操作的<br>
   * 如果操作员拥有ADMIN管理员功能，就不进行过滤了
   * @param competences 功能目标集合
   * @param operatorId 用户编号
   * @return
   */
  private List<CompetenceEntity> filterCompetence(List<CompetenceEntity> targetCompetences , String operatorId) {    
    // 1、===============求当前用户是否拥有管理员功能
    boolean isAdmin = false;
    Set<RoleEntity> currentRoles = this.roleService.findByOperatorId(operatorId);
    if(currentRoles == null || currentRoles.isEmpty()) {
      return null;
    }
    for (RoleEntity role : currentRoles) {
      if(role.getName().equals("ADMIN")) {
        isAdmin = true;
        break;
      }
    }
    
    // 2、===============就两个集合的交集
    if(isAdmin) {
      return targetCompetences;
    } else {
      // 所有查询出来当前用户可以使用的功能都在这里
      Set<CompetenceEntity> allCompetences = new HashSet<>();
      currentRoles.forEach(itemRole -> {
        List<CompetenceEntity> competences = this.competenceRepository.findByRoleId(itemRole.getId());
        if(competences != null) {
          allCompetences.addAll(competences);
        }
      });
      
      List<CompetenceEntity> result = new ArrayList<>();
      targetCompetences.forEach(itemChild -> {
        for (CompetenceEntity item : allCompetences) {
          if(StringUtils.equals(itemChild.getId(), item.getId())) {
            result.add(itemChild);
          }
        }
      });
      return result;
    }
  }
  
  /* (non-Javadoc)
   * @see com.cheshangma.operation.service.CompetenceService#findCompetencePermissionByComment(java.lang.String, java.lang.String)
   */
  @Override
  @Cacheable(value = "competence", key = "'competencePermissionByComment.' + {#opUserAccount} + '.' + {#comment}")
  public Boolean findCompetencePermissionByComment(String opUserAccount, String comment) {
    Validate.notBlank(opUserAccount , "opUserAccount must be input!");
    Validate.notBlank(comment , "comment must be input!");
    // 如果当前用户拥有管理员权限，则都返回true
    OperatorEntity currentOp = this.operatorService.findByAccountAndStatus(opUserAccount, StatusType.STATUS_NORMAL);
    if(currentOp == null) {
    	throw new IllegalArgumentException("用户状态不正确!"); 
    }
    Set<RoleEntity> roles = this.roleService.findByOperatorId(currentOp.getId());
    if(roles == null || roles.isEmpty()) {
      return false;
    }
    for (RoleEntity role : roles) {
      if(StringUtils.equals(role.getName(), "ADMIN")) {
        return true;
      }
    }
    
    Set<CompetenceEntity> allCompetences = this.queryAllCompetencesByAccount(opUserAccount);
    // 看看集合中是不是有这个指定的功能
    if(allCompetences.isEmpty()) {
      return false;
    }
    for (CompetenceEntity item : allCompetences) {
      if(StringUtils.equals(item.getComment(), comment)) 
        return true;
    }
    return false;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.operation.service.CompetenceService#findCompetencePermissionByUrl(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @Cacheable(value = "competence", key = "'competencePermissionByUrl.' + {#opUserAccount} + '.' + {#url} + '.' + {#methods}")
  public Boolean findCompetencePermissionByUrl(String opUserAccount, String url, String methods) {
    Validate.notBlank(opUserAccount , "opUserAccount must be input!");
    Validate.notBlank(url , "url must be input!");
    Validate.notBlank(methods , "methods must be input!");
    // 如果当前用户拥有管理员权限，则都返回true
    OperatorEntity currentOp = this.operatorService.findByAccountAndStatus(opUserAccount, StatusType.STATUS_NORMAL);
    if(currentOp == null) {
    	throw new IllegalArgumentException("用户状态不正确!"); 
    }
    Set<RoleEntity> roles = this.roleService.findByOperatorId(currentOp.getId());
    if(roles == null || roles.isEmpty()) {
      return false;
    }
    for (RoleEntity role : roles) {
      if(StringUtils.equals(role.getName(), "ADMIN")) {
        return true;
      }
    }
    
    String currentMethod = methods.toUpperCase();
    Set<CompetenceEntity> allCompetences = this.queryAllCompetencesByAccount(opUserAccount);
    // 看看集合中是不是有这个指定的功能
    if(allCompetences.isEmpty()) {
      return false;
    }
    for (CompetenceEntity item : allCompetences) {
      if(StringUtils.equals(item.getResource(), url) && StringUtils.indexOf(item.getMethods(), currentMethod) != -1) {
        return true;
      }
    }
    return false;
  }

  /* (non-Javadoc)
   * @see com.cheshangma.operation.service.CompetenceService#findCompetencePermissionById(java.lang.String, java.lang.String)
   */
  @Override
  @Cacheable(value = "competence", key = "'competencePermissionById.' + {#opUserAccount} + '.' + {#competenceId}")
  public Boolean findCompetencePermissionById(String opUserAccount, String competenceId) {
    Validate.notBlank(opUserAccount , "opUserAccount must be input!");
    Validate.notBlank(competenceId , "competenceId must be input!");
    // 如果当前用户拥有管理员权限，则都返回true
    OperatorEntity currentOp = this.operatorService.findByAccountAndStatus(opUserAccount, StatusType.STATUS_NORMAL);
    if(currentOp == null) {
    	throw new IllegalArgumentException("用户状态不正确!"); 
    }
    Set<RoleEntity> roles = this.roleService.findByOperatorId(currentOp.getId());
    if(roles == null || roles.isEmpty()) {
      return false;
    }
    for (RoleEntity role : roles) {
      if(StringUtils.equals(role.getName(), "ADMIN")) {
        return true;
      }
    }
    
    Set<CompetenceEntity> allCompetences = this.queryAllCompetencesByAccount(opUserAccount);
    // 看看集合中是不是有这个指定的功能
    if(allCompetences.isEmpty()) {
      return false;
    }
    for (CompetenceEntity item : allCompetences) {
      if(StringUtils.equals(item.getId(), competenceId)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * 这个方法用于查询该用户可使用的所有功能信息
   * @param opUserAccount
   * @return
   */
  private Set<CompetenceEntity> queryAllCompetencesByAccount(String opUserAccount) {
    OperatorEntity operator = this.operatorService.findByAccountAndStatus(opUserAccount, StatusType.STATUS_NORMAL);
    if(operator == null) {
      return Collections.emptySet();
    }
    Set<RoleEntity> roles = operator.getRoles();
    if(roles == null || roles.isEmpty()) {
      return Collections.emptySet();
    }
    
    // 集成查询当前用户所有的功能
    Set<CompetenceEntity> allCompetences = new HashSet<>();
    for (RoleEntity role : roles) {
      // 这里带有缓存
      List<CompetenceEntity> competences = this.findByRoleId(role.getId());
      if(competences == null || competences.isEmpty()) {
        return Collections.emptySet();
      }
      allCompetences.addAll(competences);
    }
    return allCompetences;
  }
  
  @Override
  @Transactional
  @CacheEvict(value = "competence", allEntries = true)
  public CompetenceEntity addCompetence(CompetenceEntity comp) {
    // 验证
    validCompetence(comp);
    // 验证是否已经存在
    Validate.isTrue(!isExit(comp.getMethods().toUpperCase(), comp.getResource(), null), "功能对象已存在！");
    comp.setMethods(comp.getMethods().toUpperCase());
    comp.setStatus(StatusType.STATUS_NORMAL);
    comp.setCreateDate(new Date());
    CompetenceEntity competenceEntity = competenceRepository.saveAndFlush(comp);
    return competenceEntity;
  }

  @Override
  @Transactional
  @CacheEvict(value = "competence", allEntries = true)
  public CompetenceEntity updateCompetence(CompetenceEntity comp) {
    CompetenceEntity currentComp = getById(comp.getId());
    // 验证对象
    Validate.notNull(currentComp, "功能id为" + comp.getId() + "的对象不存在！");
    currentComp.setResource(comp.getResource());
    currentComp.setMethods(comp.getMethods());
    currentComp.setComment(comp.getComment());
    currentComp.setModifier(comp.getModifier());
    currentComp.setModifyDate(new Date());
    // 验证
    validCompetence(currentComp);
    // 验证是否已经存在
    Validate.isTrue(!isExit(currentComp.getMethods().toUpperCase(), currentComp.getResource(), currentComp.getId()), "功能对象已存在！");
    // 验证通过后methods统一大写存储
    currentComp.setMethods(currentComp.getMethods().toUpperCase());
    competenceRepository.saveAndFlush(currentComp);
    return currentComp;
  }
  
  @Override
  @Transactional
  @CacheEvict(value = "competence", allEntries = true)
  public CompetenceEntity updateRootCompetence(String competenceId , OperatorEntity operator) {
    Validate.notBlank(competenceId , "必须传入功能编号!");
    Validate.notNull(operator , "操作者信息必须传入!");
    CompetenceEntity currentComp = getById(competenceId);
    
    // 更新成根目录
    currentComp.setModifier(operator);
    currentComp.setModifyDate(new Date());
    currentComp.setLevel(0);
    currentComp.setOrderIndex(new Date().getTime());
    competenceRepository.saveAndFlush(currentComp);
    return currentComp;
  }
  
  @Override
  @Transactional
  @CacheEvict(value = "competence", allEntries = true)
  public CompetenceEntity diableOrUndisable(String id, Boolean flag, OperatorEntity operator) {
    if (StringUtils.isEmpty(id) || flag == null) {
      throw new IllegalArgumentException("禁用功能时，功能id不能为空！");
    }
    CompetenceEntity competenceEntity = getById(id);
    Validate.notNull(competenceEntity, "id为" + id + "的功能对象不存在！");
    if (flag == true) {
      // 启用
      competenceEntity.setStatus(StatusType.STATUS_NORMAL);
    } else if (flag == false) {
      // 禁用
      competenceEntity.setStatus(StatusType.STATUS_DISABLE);
    }
    competenceEntity.setModifier(operator);
    competenceEntity.setModifyDate(new Date());
    competenceRepository.saveAndFlush(competenceEntity);
    return competenceEntity;
  }

  /**
   * 验证功能对象和字段.
   * @param comp 功能对象
   */
  private void validCompetence(CompetenceEntity comp) {
    // 验证对象是否存在
    Validate.notNull(comp, "功能对象不能为空！");
    // 功能串
    String resource = comp.getResource();
    Validate.notBlank(resource, "功能串不能为空！");
    // 涉及的方法描述（POST/GET……）
    String method = comp.getMethods();
    Validate.notBlank(method, "方法描述不能为空！");
    // 备注
    String comment = comp.getComment();
    Validate.notBlank(comment, "功能备注不能为空！");
  }

  /* (non-Javadoc)
   * @see com.cheshangma.operation.service.CompetenceService#bindChild(java.lang.String, java.lang.String[])
   */
  @Override
  @Transactional
  public void bindChild(String parentCompetenceId, String... childCompetenceIds) {
    Validate.notBlank(parentCompetenceId , "父级功能编号必须传入!");
    if(childCompetenceIds == null || childCompetenceIds.length == 0) {
      throw new IllegalArgumentException("至少需要一个已绑定的功能!");
    }
    CompetenceEntity parentCompetence = this.competenceRepository.findOne(parentCompetenceId);
    if(parentCompetence == null) {
      throw new IllegalArgumentException("未发现父级功能!");
    }
    int parentLevel = parentCompetence.getLevel();
    
    // 使用当前时间作为默认排序值
    final AtomicLong orderIndex = new AtomicLong(new Date().getTime());
    Arrays.stream(childCompetenceIds).forEach(item -> {
      this.competenceRepository.bindChild(parentCompetenceId, orderIndex.getAndIncrement(), parentLevel+1, item);
    });
  }

  /* (non-Javadoc)
   * @see com.cheshangma.operation.service.CompetenceService#unBindChild(java.lang.String[])
   */
  @Override
  @Transactional
  public void unBindChild(String... competenceIds) {
    if(competenceIds == null || competenceIds.length == 0) {
      throw new IllegalArgumentException("至少输入一个需要解绑的功能!");
    }
    
    Arrays.stream(competenceIds).forEach(item -> {
      this.competenceRepository.unBindChild(item);
    });
  }
}
