package com.hc.wechat.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.controller.model.ResponseModel;
import com.hc.wechat.entity.OperatorEntity;
import com.hc.wechat.entity.RoleEntity;
import com.hc.wechat.service.RoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "RoleController角色restful接口")
@RestController
@RequestMapping("/v1/roles")
public class RoleController extends BaseController {
  @Autowired
  private RoleService roleService;

  /**
   * 该接口方法用于查询符合指定状态的角色信息，只返回角色的基本信息，没有任何的关联信息但是包括了可能存在的修改者信息
   * 
   * @param status
   * @return
   */
  @ApiOperation(value = "该接口方法用于查询符合指定状态的角色信息，只返回角色的基本信息，没有任何的关联信息但是包括了可能存在的修改者信息。")
  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseModel findRoles(Integer status) {
    try {
      UseStatus statusType = null;
      if (status == 1) {
        statusType = UseStatus.STATUS_NORMAL;
      } else {
        statusType = UseStatus.STATUS_DISABLE;
      }
      List<RoleEntity> roles = this.roleService.findByStatus(statusType);
      if (roles == null || roles.isEmpty()) {
        roles = Collections.emptyList();
        return this.buildHttpReslut(roles);
      }
      return this.buildHttpReslut(roles, "competences", "operators", "modifier.creator",
          "modifier.modifier", "modifier.roles", "creator.creator", "creator.modifier",
          "creator.roles");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 该接口方法用于查询所有角色信息，无论它们的状态如何。<br>
   * 只返回角色的基本信息，没有任何的关联信息但是包括了可能的修改者信息"
   * 
   * @param status
   * @return
   */
  @ApiOperation(value = "该接口方法用于查询所有角色信息，无论它们的状态如何。<br>" + "只返回角色的基本信息，没有任何的关联信息但是包括了可能的修改者信息")
  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public ResponseModel findAllRoles() {
    try {
      List<RoleEntity> roles = this.roleService.findAll();
      if (roles == null || roles.isEmpty()) {
        roles = Collections.emptyList();
        return this.buildHttpReslut(roles);
      }
      return this.buildHttpReslut(roles, "competences", "operators", "modifier.creator",
          "modifier.modifier", "modifier.roles", "creator.creator", "creator.modifier",
          "creator.roles");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 查询一个指定的角色信息，只查询这个角色的基本信息
   */
  @ApiOperation(value = "查询一个指定的角色信息，只查询这个角色的基本信息")
  @RequestMapping(value = "/getById", method = RequestMethod.GET)
  public ResponseModel findById(String roleId) {
    try {
      Validate.notBlank(roleId, "角色roleId不能为空");
      RoleEntity currentRole = this.roleService.findRoleById(roleId);
      if (currentRole == null) {
        throw new IllegalArgumentException(roleId + "的角色不存在!");
      }

      return this.buildHttpReslut(currentRole, "competences", "operators", "modifier", "creator");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 条件分页查询角色列表信息，只查询基本信息。
   * 
   * @param roleName 角色名称
   * @param status 状态
   * @param pageable 分页
   * @return Page<RoleEntity>
   */
  @ApiOperation(value = "条件分页查询角色列表", notes = "根据查询条件分页查询角色列表信息")
  @RequestMapping(value = "/getByConditions", method = {RequestMethod.GET})
  public ResponseModel getByConditions(
      @ApiParam(value = "角色名称（模糊查询）") @RequestParam(required = false) String roleName,
      @ApiParam(value = "状态（正常/禁用）") @RequestParam(required = false) UseStatus status,
      @ApiParam(value = "自动注入分页对象") @PageableDefault(value = 15, sort = {
          "createDate"}, direction = Sort.Direction.DESC) Pageable pageable) {

    Page<RoleEntity> list = roleService.getByConditions(roleName, status, pageable);
    // 去除关联关系
    list.map(new Converter<RoleEntity, RoleEntity>() {
      @Override
      public RoleEntity convert(RoleEntity source) {
        levelOneAssociation(source);
        return source;
      }
    });
    return this.buildHttpReslut(list, "competences", "operators", "modifier.creator",
        "modifier.modifier", "modifier.roles", "creator.creator", "creator.modifier",
        "creator.roles");
  }

  /**
   * 添加一个角色信息
   * 
   * @param role
   * @return 创建后的角色基本信息将会被返回
   */
  @ApiOperation(value = "添加一个角色信息")
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseModel addRole(@RequestBody RoleEntity role, Principal opUser) {
    try {
      // 求得当前操作的操作者
      OperatorEntity operator = this.verifyOperatorLogin(opUser);
      if (role == null) {
        throw new IllegalArgumentException("新增角色时，角色信息不能为空!");
      }
      if (operator != null) {
        role.setCreator(operator);
      }

      RoleEntity currentRole = roleService.addRole(role);

      return this.buildHttpReslut(currentRole, "competences", "operators", "modifier", "creator");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 修改一个指定的角色信息，注意配置在roles.deleteDeny属性的信息不能进行修改操作<br>
   * 且指定的一个角色只能修改角色的comment信息
   * 
   * @param role 指定的修改信息
   */
  @ApiOperation(value = "修改一个指定的角色信息，注意配置在roles.deleteDeny属性的信息不能进行修改操作。且指定的一个角色只能修改角色的comment信息")
  @RequestMapping(value = "/", method = RequestMethod.PATCH)
  public ResponseModel updateRole(@RequestBody RoleEntity role, Principal opUser) {
    try {
      // 求得当前操作的操作者
      OperatorEntity operator = this.verifyOperatorLogin(opUser);
      if (role == null) {
        throw new IllegalArgumentException("更新角色时，角色信息不能为空!");
      }
      if (operator != null) {
        role.setModifier(operator);
      }

      RoleEntity currentRole = this.roleService.updateRole(role);
      if (currentRole == null) {
        throw new IllegalArgumentException("角色不存在!");
      }
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 禁用某一个指定的角色信息（相当于删除）<br>
   * 只是系统中不能真正的删除某一个角色，只能是将这个角色作废掉或者恢复正常状态
   * 
   * @param roleId
   * @return
   */
  @ApiOperation(value = "禁用某一个指定的角色信息（相当于删除）<br>" + "只是系统中不能真正的删除某一个角色，只能是将这个角色作废掉或者恢复正常状态")
  @RequestMapping(value = "/disable", method = RequestMethod.POST)
  public ResponseModel disableRole(@ApiParam(value = "角色id") String roleId, Principal opUser) {
    try {
      // 求得当前操作的操作者
      OperatorEntity operator = this.verifyOperatorLogin(opUser);
      if (operator == null) {
        throw new IllegalArgumentException("当前登录的操作者不存在！");
      }
      this.roleService.disableRole(roleId, operator);

      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 重新启用某一个指定的角色信息
   * 
   * @param roleId
   * @return
   */
  @ApiOperation(value = "重新启用某一个指定的角色信息")
  @RequestMapping(value = "/enable", method = RequestMethod.POST)
  public ResponseModel enableRole(@ApiParam(value = "角色id") String roleId, Principal opUser) {
    try {
      // 求得当前操作的操作者
      OperatorEntity operator = this.verifyOperatorLogin(opUser);
      if (operator == null) {
        throw new IllegalArgumentException("当前登录的操作者不存在！");
      }
      roleService.enableRole(roleId, operator);

      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 根据后台用户id获取该后台用户绑定的角色列表信息 .
   * 
   * @param operatorId 后台用户id
   * @return
   */
  @ApiOperation(value = "获取后台用户绑定的角色列表", notes = "根据后台用户Operatorid获取该后台用户的角色列表信息")
  @RequestMapping(value = "/getRolesByOperator", method = {RequestMethod.GET})
  public ResponseModel getRolesByOperator(@ApiParam(value = "后台用户operatorId") String operatorId,
      Principal opUser) {
    try {
      OperatorEntity operator = this.verifyOperatorLogin(opUser);
      if (StringUtils.isBlank(operatorId)) {
        operatorId = operator.getId();
      }
      List<RoleEntity> nowList = roleService.findByOperatorId(operatorId);
      return this.buildHttpReslut(nowList, "competences", "operators", "modifier", "creator");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 该私有方法用于获取角色信息的同时得到其一级关联信息<br>
   * 创建人和修改人<br>
   * 仍然需要排除一级关联信息中的关联关系.
   * 
   * 
   */
  private void levelOneAssociation(RoleEntity roleEntity) {
    /*
     * 获取角色信息的创建人基本信息, 同时排除创建人中的关联关系
     */
    OperatorEntity mCreator = roleEntity.getCreator();
    OperatorEntity creator = new OperatorEntity();
    if (mCreator != null) {
      creator.setId(mCreator.getId());
      creator.setAccount(mCreator.getAccount());
      creator.setCreateDate(mCreator.getCreateDate());
      creator.setImagePath(mCreator.getImagePath());
      creator.setLastLoginTime(mCreator.getLastLoginTime());
      creator.setModifyDate(mCreator.getModifyDate());
      creator.setName(mCreator.getName());
      creator.setPassword(mCreator.getPassword());
      creator.setStatus(mCreator.getStatus());
      roleEntity.setCreator(creator);
    } else {
      roleEntity.setCreator(null);
    }
    /*
     * 获取角色信息的修改人基本信息, 同时排除修改人中的关联关系
     */
    OperatorEntity mModifier = roleEntity.getModifier();
    OperatorEntity modifier = new OperatorEntity();
    if (mModifier != null) {
      modifier.setAccount(mModifier.getAccount());
      modifier.setCreateDate(mModifier.getCreateDate());
      modifier.setImagePath(mModifier.getImagePath());
      modifier.setId(mModifier.getId());
      modifier.setLastLoginTime(mModifier.getLastLoginTime());
      modifier.setModifyDate(mModifier.getModifyDate());
      modifier.setName(mModifier.getName());
      modifier.setPassword(mModifier.getPassword());
      modifier.setStatus(mModifier.getStatus());
      roleEntity.setModifier(modifier);
    } else {
      roleEntity.setModifier(null);
    }
  }
}
