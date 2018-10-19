package com.hecheng.wechat.openplatform.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.controller.model.ResponseModel;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(value = "UserController restful接口")
@RestController
@RequestMapping("/v1/user")
public class UserController extends BaseController {
  @Autowired
  private UserService userService;

  @ApiOperation(value = "条件分页查询用户", notes = "条件分页查询用户")
  @RequestMapping(value = "/getByConditions", method = {RequestMethod.GET})
  public ResponseModel getByConditions(@ApiParam(value = "昵称") String nickName,
      @ApiParam(value = "状态") UseStatus status, @ApiParam(value = "电话号") String telephone,
      @ApiParam(value = "自动注入分页对象") @PageableDefault(value = 15, sort = {
          "createDate"}, direction = Sort.Direction.DESC) Pageable pageable,
      Principal logUser) {
    try {
      verifyOperatorLogin(logUser);

      Page<UserEntity> users = userService.getByConditions(nickName, status, telephone, pageable);
      // 去关联
      return this.buildHttpReslut(users, "thirdPartUsers", "userContact.user");
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }
}
