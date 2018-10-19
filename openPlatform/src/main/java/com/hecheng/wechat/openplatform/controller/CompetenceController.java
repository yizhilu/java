package com.hecheng.wechat.openplatform.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.controller.model.ResponseCode;
import com.hecheng.wechat.openplatform.controller.model.ResponseModel;
import com.hecheng.wechat.openplatform.entity.CompetenceEntity;
import com.hecheng.wechat.openplatform.entity.OperatorEntity;
import com.hecheng.wechat.openplatform.service.CompetenceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "功能restful接口")
@RestController
@RequestMapping("/v1/competence")
public class CompetenceController extends BaseController {

  @Autowired
  private CompetenceService competenceService;

  /**
   * 新增功能.
   * 
   * @param competenceEntity 功能对象
   * @param operator 当前登录用户
   * @return ResponseModel
   */
  @ApiOperation(value = "新增功能", notes = "根据参数（功能对象）新增该条功能信息")
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseModel addCompetence(
      @ApiParam(value = "功能参数对象") @RequestBody CompetenceEntity competenceEntity,
      @ApiParam(value = "当前登录用户") Principal operator) {
    try {
      // 通过operator确定当前的登录者信息
      OperatorEntity currOperator = this.verifyOperatorLogin(operator);
      if (competenceEntity == null) {
        throw new IllegalArgumentException("功能对象不能为空！");
      }
      competenceEntity.setCreator(currOperator);
      CompetenceEntity comp = competenceService.addCompetence(competenceEntity);
      return this.buildHttpReslut(comp, "creator", "roles", "modifier");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 修改功能.
   * 
   * @param competenceEntity 功能对象
   * @param operator 当前登录用户
   * @return ResponseModel
   */
  @ApiOperation(value = "修改功能", notes = "根据参数（功能对象）修改该条功能信息")
  @RequestMapping(value = "/", method = RequestMethod.PATCH)
  public ResponseModel updateCompetence(
      @ApiParam(value = "功能参数对象") @RequestBody CompetenceEntity competenceEntity,
      @ApiParam(value = "当前登录用户") Principal operator) {
    try {
      // 通过operator确定当前的登录者信息
      OperatorEntity currOperator = this.verifyOperatorLogin(operator);
      if (competenceEntity == null) {
        throw new IllegalArgumentException("功能对象不能为空！");
      }
      competenceEntity.setModifier(currOperator);
      CompetenceEntity comp = competenceService.updateCompetence(competenceEntity);
      return this.buildHttpReslut(comp, "creator", "roles", "modifier");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }

  }

  /**
   * 启用/禁用.
   * 
   * @param id 逻辑键
   * @param flag 标识true为启用，false为禁用
   * @return
   */
  @ApiOperation(value = "启用/禁用功能", notes = "根据参数（功能id）和操作标识（flag：true为启用，false为禁用）")
  @RequestMapping(value = "/disableOrUndisable", method = RequestMethod.POST)
  public ResponseModel disableOrUndisable(@ApiParam(value = "功能id") @RequestParam String id,
      @ApiParam(value = "操作标识（flag：true为启用，false为禁用）") @RequestParam Boolean flag,
      @ApiParam(value = "当前登录用户") Principal operator) {
    try {
      // 通过operator确定当前的登录者信息
      OperatorEntity currOperator = this.verifyOperatorLogin(operator);
      competenceService.diableOrUndisable(id, flag, currOperator);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 根据id获取功能详情（单条）.
   * 
   * @param id 逻辑键
   * @return ResponseModel
   */
  @ApiOperation(value = "获取功能详情", notes = "根据参数（功能id）获取其详情信息（单条）")
  @RequestMapping(value = "/getById", method = RequestMethod.GET)
  public ResponseModel getById(@ApiParam(value = "功能id") String id) {
    try {
      CompetenceEntity competenceEntity = competenceService.getById(id);
      return this.buildHttpReslut(competenceEntity, "creator", "roles", "modifier");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 查询指定的角色已绑定的功能信息，无论这些功能是否可用（状态是否正常）.
   * 
   * @param roleId 角色id
   * @return ResponseModel
   */
  @ApiOperation(value = "查询指定的角色已绑定的功能信息，无论这些功能是否可用（状态是否正常）.")
  @RequestMapping(value = "/findByRoleId", method = RequestMethod.GET)
  public ResponseModel findByRoleId(@ApiParam(value = "角色id") String roleId) {
    try {
      List<CompetenceEntity> results = this.competenceService.findByRoleId(roleId);
      if (results == null || results.isEmpty()) {
        results = Collections.emptyList();
        return this.buildHttpReslut(results);
      }
      return this.buildHttpReslut(results, "creator", "roles", "modifier");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 条件分页查询功能列表.
   * 
   * @param method 方法描述 例如：POST或者POST|GET|DELETE
   * @param comment 备注
   * @param status 状态
   * @param pageable 分页
   * @return Page<CompetenceEntity>
   */
  @ApiOperation(value = "条件分页查询功能列表", notes = "根据查询条件分页查询功能列表信息")
  @RequestMapping(value = "/getByCondition", method = {RequestMethod.GET})
  public ResponseModel getByCondition(
      @ApiParam(value = "方法描述 例如：POST或者POST|GET|DELETE") String method,
      @ApiParam(value = "备注（模糊查询）") String comment, @ApiParam(value = "状态（正常/禁用）") String status,
      @ApiParam(value = "功能串（模糊查询）") String resource,
      @ApiParam(value = "自动注入分页对象") @PageableDefault(value = 15, sort = {
          "createDate"}, direction = Sort.Direction.DESC) Pageable pageable) {
    Map<String, Object> params = new HashMap<String, Object>();
    if (StringUtils.isNotEmpty(method)) {
      params.put("methods", method);
    }
    if (StringUtils.isNotBlank(comment)) {
      params.put("comment", comment);
    }
    if (StringUtils.isNotBlank(status)) {
      params.put("status", status);
    }
    if (StringUtils.isNotBlank(resource)) {
      params.put("resource", resource);
    }
    Page<CompetenceEntity> list = competenceService.getByCondition(params, pageable);
    // 去除关联
    list.map(new Converter<CompetenceEntity, CompetenceEntity>() {

      @Override
      public CompetenceEntity convert(CompetenceEntity source) {
        levelOneAssociation(source);
        return source;
      }
    });
    return this.buildHttpReslut(list, "roles", "modifier.creator", "modifier.modifier",
        "modifier.roles", "creator.creator", "creator.modifier", "creator.roles");
  }

  /**
   * 查询所有非禁用的功能信息（不分页）.
   * 
   * @return List<CompetenceEntity>
   */
  @ApiOperation(value = "查询功能列表(所有非禁用的功能，不分页)", notes = "查询所有状态为正常的功能列表信息，不分页")
  @RequestMapping(value = "/getByAllNoPage", method = {RequestMethod.GET})
  public ResponseModel getByAllNoPage() {
    // 查询所有非禁用的功能信息
    List<CompetenceEntity> nowList = competenceService.findByStatus(UseStatus.STATUS_NORMAL);
    if (nowList == null || nowList.isEmpty()) {
      nowList = Collections.emptyList();
      return this.buildHttpReslut(nowList);
    }
    return this.buildHttpReslut(nowList, "creator", "roles", "modifier");
  }

  /**
   * 基于功能的URL信息，确定当前登录者针对这个功能有没有被授权
   * 
   * @param opUserAccount
   * @param url 功能的url信息，权限URL串。注意如果URL信息中有参数信息。则使用“{param}”代替。例如：<br>
   *        /vz/param4/{param}
   * @param methods POST或者POST|GET|DELETE|PATCH等，传入的大小写没有关系，反正都会转为大写
   * @return 如果被授权则返回true；其它情况返回false
   */
  @ApiOperation(value = "基于功能的URL信息，确定当前登录者针对这个功能有没有被授权")
  @RequestMapping(value = "/findCompetencePermissionByUrl", method = RequestMethod.GET)
  public ResponseModel findCompetencePermissionByUrl(@ApiParam(value = "当前登录用户") Principal operator,
      @ApiParam(value = "功能url") String url, @ApiParam(value = "请求方法") String methods) {
    Boolean hasPermission = false;
    try {
      this.verifyOperatorLogin(operator);
      hasPermission =
          this.competenceService.findCompetencePermissionByUrl(operator.getName(), url, methods);
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }

    ResponseModel result =
        new ResponseModel(new Date().getTime(), hasPermission, ResponseCode._200, "");
    return result;
  }

  /**
   * 基于功能的URL信息，确定当前登录者针对这个功能有没有被授权(检查的是一个功能拥有多个权限 其中一个满足即可)
   * 
   * @param opUserAccount
   * @param urls 功能的url信息，权限URL串。注意如果URL信息中有参数信息。则使用“{param}”代替。例如：<br>
   *        /vz/param4/{param} 例如： "/v1/gateway/getByConditions&GET|/v1/socket/getByConditions&GET"
   * @return 如果被授权则返回true；其它情况返回false
   */
  @ApiOperation(value = "基于功能的URL信息，确定当前登录者针对这个功能有没有被授权(检查的是一个功能拥有多个权限 其中一个满足即可)")
  @RequestMapping(value = "/findOrPermissionByUrl", method = RequestMethod.GET)
  public ResponseModel findOrPermissionByUrl(@ApiParam(value = "当前登录用户") Principal operator,
      @ApiParam(value = "功能urls") String urls) {
    Boolean hasPermission = false;
    try {
      this.verifyOperatorLogin(operator);
      String[] urlAndMethodArr = urls.split("\\|");
      for (String urlAndMethod : urlAndMethodArr) {
        String url = urlAndMethod.split("&")[0];
        String methods = urlAndMethod.split("&")[1];
        hasPermission =
            this.competenceService.findCompetencePermissionByUrl(operator.getName(), url, methods);
        if (hasPermission) {
          break;
        }
      }
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }

    ResponseModel result =
        new ResponseModel(new Date().getTime(), hasPermission, ResponseCode._200, "");
    return result;
  }

  /**
   * 基于功能的URL信息，确定当前登录者针对这个功能有没有被授权(检查的是一个功能拥有多个权限 都满足才行)
   * 
   * @param opUserAccount
   * @param urls 功能的url信息，权限URL串。注意如果URL信息中有参数信息。则使用“{param}”代替。例如：<br>
   *        /vz/param4/{param} 例如： "/v1/gateway/getByConditions&GET|/v1/socket/getByConditions&GET"
   * @return 如果被授权则返回true；其它情况返回false
   */
  @ApiOperation(value = "基于功能的URL信息，确定当前登录者针对这个功能有没有被授权(检查的是一个功能拥有多个权限 都满足才行)")
  @RequestMapping(value = "/findAndPermissionByUrl", method = RequestMethod.GET)
  public ResponseModel findAndPermissionByUrl(@ApiParam(value = "当前登录用户") Principal operator,
      @ApiParam(value = "功能urls") String urls) {
    Boolean hasPermission = false;
    try {
      this.verifyOperatorLogin(operator);
      String[] urlAndMethodArr = urls.split("\\|");
      for (String urlAndMethod : urlAndMethodArr) {
        String url = urlAndMethod.split("&")[0];
        String methods = urlAndMethod.split("&")[1];
        hasPermission =
            this.competenceService.findCompetencePermissionByUrl(operator.getName(), url, methods);
        if (!hasPermission) {
          break;
        }
      }
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }

    ResponseModel result =
        new ResponseModel(new Date().getTime(), hasPermission, ResponseCode._200, "");
    return result;
  }

  /**
   * 基于功能的唯一编号信息，确定当前登录者针对这个功能有没有被授权
   * 
   * @param opUserAccount 当前登录的用户账户信息
   * @param competenceId 指定的功能编号信息
   * @return 如果被授权则返回true；其它情况返回false
   */
  @ApiOperation(value = "基于功能的唯一编号信息，确定当前登录者针对这个功能有没有被授权")
  @RequestMapping(value = "/findCompetencePermissionById", method = RequestMethod.GET)
  public ResponseModel findCompetencePermissionById(@ApiParam(value = "当前登录用户") Principal operator,
      @ApiParam(value = "功能id") String competenceId) {
    Boolean hasPermission = false;
    try {
      hasPermission =
          this.competenceService.findCompetencePermissionById(operator.getName(), competenceId);
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }

    ResponseModel result =
        new ResponseModel(new Date().getTime(), hasPermission, ResponseCode._200, "");
    return result;
  }

  /**
   * 该私有方法用于获取功能信息的同时得到其一级关联信息<br>
   * 创建人和修改人<br>
   * 仍然需要排除一级关联信息中的关联关系.
   * 
   * @param merchantEntity 功能对象
   */
  private void levelOneAssociation(CompetenceEntity competenceEntity) {
    /*
     * 获取功能信息的创建人基本信息, 同时排除创建人中的关联关系
     */
    OperatorEntity mCreator = competenceEntity.getCreator();
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
      competenceEntity.setCreator(creator);
    } else {
      competenceEntity.setCreator(null);
    }
    /*
     * 获取角色信息的修改人基本信息, 同时排除修改人中的关联关系
     */
    OperatorEntity mModifier = competenceEntity.getModifier();
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
      competenceEntity.setModifier(modifier);
    } else {
      competenceEntity.setModifier(null);
    }
  }
}
