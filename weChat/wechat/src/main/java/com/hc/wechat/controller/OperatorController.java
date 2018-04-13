package com.hc.wechat.controller;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.hc.wechat.common.utils.DateUtils;
import com.hc.wechat.controller.model.ResponseModel;
import com.hc.wechat.entity.OperatorEntity;
import com.hc.wechat.service.OperatorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 管理员controller
 * 
 * @author hzh
 */
@Api(value = "管理员controller")
@RestController
@RequestMapping("/v1/operator")
public class OperatorController extends BaseController {

  @Autowired
  private OperatorService operatorService;



  @ApiOperation(value = "条件分页查询后台管理人员", notes = "根据查询条件分页查询后台管理人员")
  @RequestMapping(value = "/getByConditions", method = {RequestMethod.GET})
  public ResponseModel getByConditions(@ApiParam(value = "账号") String account,
      @ApiParam(value = "状态（正常/禁用）") UseStatus status, @ApiParam(value = "管理员名称(name)") String name,
      @ApiParam(value = "搜索开始时间") String startDate, @ApiParam(value = "搜索结束时间") String endDate,
      @ApiParam(value = "自动注入分页对象") @PageableDefault(value = 15, sort = {
          "createDate"}, direction = Sort.Direction.DESC) Pageable pageable) {
    try {
      Map<String, Object> params = new HashMap<String, Object>();
      if (StringUtils.isNotEmpty(account)) {
        params.put("account", account);
      }
      if (StringUtils.isNotBlank(name)) {
        params.put("name", name);
      }
      if (StringUtils.isNotBlank(startDate)) {
        params.put("startCreateDate",
            DateUtils.parserDate(startDate, DateUtils.SHORT_DATE_FORMAT_STR));
      }
      if (StringUtils.isNotBlank(endDate)) {
        params.put("endCreateDate", DateUtils.parserDate(endDate, DateUtils.SHORT_DATE_FORMAT_STR));
      }
      if (null != status) {
        params.put("status", status);
      }

      Page<OperatorEntity> operators = operatorService.getByConditions(params, pageable);
      // 加载懒加载属性
      loadLazyProperties(operators.getContent());
      return this.buildHttpReslut(operators, "roles");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }
  @ApiOperation(value = "创建新管理员", notes = "创建新管理员，账号，密码，名称(name)为必填")
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseModel create(@ApiParam(value = "管理员参数对象") @RequestBody OperatorEntity operator,
                              Principal logUser) {
    try {
      OperatorEntity logOperator = verifyOperatorLogin(logUser);
      if (null != operator) {
        operator.setCreator(logOperator);
        operator.setCreateDate(new Date());
      }
      operatorService.create(operator);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  @ApiOperation(value = "查询单个管理员", notes = "根据管理员id查询管理员信息")
  @RequestMapping(value = "/getById", method = RequestMethod.GET)
  public ResponseModel getById(@ApiParam(value = "管理员id") @RequestParam String id,
      Principal logUser) {
    try {
      verifyOperatorLogin(logUser);
      OperatorEntity operator = operatorService.findById(id);
      loadLazyPropertie(operator);
      return this.buildHttpReslut(operator, "modifier.modifier", "modifier.creator",
          "modifier.roles", "creator.modifier", "creator.creator", "creator.roles", "roles");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  @ApiOperation(value = "修改某个管理员的密码", notes = "登录的管理员修改某个管理员的密码")
  @RequestMapping(value = "/updatePassword", method = RequestMethod.PATCH)
  public ResponseModel updatePassword(@ApiParam(value = "管理员id") @RequestParam String operatorId,
      @ApiParam(value = "该用户的老密码") @RequestParam String oldPass,
      @ApiParam(value = "该用户的新密码") @RequestParam String newPass, Principal logUser) {
    try {
      OperatorEntity operator = verifyOperatorLogin(logUser);
      operatorService.updatePassword(operatorId, oldPass, newPass, operator);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  @ApiOperation(value = "修改某个管理员的信息", notes = "登录的管理员修改某个管理员的信息，只能修改名称和密码")
  @RequestMapping(value = "/", method = RequestMethod.PATCH)
  public ResponseModel modify(@ApiParam(value = "管理员参数对象") @RequestBody OperatorEntity operator,
      Principal logUser) {
    try {
      OperatorEntity logOperator = verifyOperatorLogin(logUser);
      if (null != operator) {
        operator.setModifier(logOperator);
        operator.setModifyDate(new Date());
      }
      operatorService.modify(operator);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  @ApiOperation(value = "修改某个管理员的状态", notes = "登陆的管理员修改某个管理员的状态，true为启用，false为禁用")
  @RequestMapping(value = "/disableOrUsable", method = RequestMethod.PATCH)
  public ResponseModel disableOrUsable(@ApiParam(value = "需要修改状态的管理员id") @RequestParam String id,
      @ApiParam(value = "判断这次操作是启用还是禁用，true为启用") @RequestParam boolean flag, Principal logUser) {
    try {
      OperatorEntity logOperator = verifyOperatorLogin(logUser);
      operatorService.diableOrUndisable(id, flag, logOperator);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 重装管理员集合的创建者和修改者(只有name和id)
   * 
   * @param List<OperatorEntity> operators 需要重装的管理员集合
   */
  private void loadLazyProperties(List<OperatorEntity> operators) {
    if (null != operators && operators.size() > 0) {
      for (OperatorEntity operatorEntity : operators) {
        loadLazyPropertie(operatorEntity);
      }
    }

  }

  /**
   * 重装管理员的创建者和修改者(只有name和id)
   * 
   * @param operatorEntity 需要重装的管理员
   */
  private void loadLazyPropertie(OperatorEntity operatorEntity) {
    if (null != operatorEntity.getCreator()) {
      OperatorEntity cOp = operatorEntity.getCreator();
      OperatorEntity newOp = new OperatorEntity();
      newOp.setId(cOp.getId());
      newOp.setName(cOp.getName());
      operatorEntity.setCreator(newOp);
    }
    if (null != operatorEntity.getModifier()) {
      OperatorEntity mOp = operatorEntity.getModifier();
      OperatorEntity newOp = new OperatorEntity();
      newOp.setId(mOp.getId());
      newOp.setName(mOp.getName());
      operatorEntity.setModifier(newOp);
    }
  }
}
