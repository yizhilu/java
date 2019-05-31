package com.hc.security.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hc.security.controller.model.ResponseModel;
import com.hc.security.service.OperatorService;
import com.hc.security.service.SecurityService;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 和权限安全相关的接口，在这里暴露<br>
 */
@RestController
@RequestMapping("/v1/security")
public class SecurityController extends BaseController {

  @Autowired
  private SecurityService securityService;

  @Autowired
  private OperatorService operatorService;

  /**
   * 当登陆成功后，默认跳转到这个URL，并且返回登录成功后的用户基本信息
   * 
   * @return
   */
  @ApiOperation(value = "当登陆成功后，默认跳转到这个URL，并且返回登录成功后的用户基本信息" + "一旦登录成功，服务端将会向客户端返回两个重要属性：<br>"
      + "1、SESSION属性：该属性可保证从最后一次服务器请求开始的30分钟内登录信息有效(前提是服务节点没有重启)<br>"
      + "2、persistence属性：该属性可保证从最后一次登录操作开始的100天内登录信息有效(前提是服务节点没有重启)<br>"
      + "客户端应该至少保证在进行HTTP请求时，向服务器传递persistence属性。但为了保证服务端业务处理性能，应该两个属性都进行传递。<br>"
      + "<b>请注意：正常情况下SESSION属性可能发生变化，一旦变化客户端应该向服务端传递最新的SESSION属性</b>" + "</p>"
      + "一旦登录成功，服务端将通过以下三种方式向客户端返回SESSION属性和persistence属性：<br>"
      + "1、http response的Content-Type信息中，将携带。类似于：application/json; persistence=YWRtaW46MTUxNDUxNzA4MjYzNjplYzI0OTFlYWEyNDhkZmIyZWIyNjNjODc3YzM2M2Q0MA; session=54fd02c7-4067-43c9-94f8-5f6e474cd858;charset=UTF-8<br>"
      + "2、http response的header信息中，将携带。类似于：persistence →YWRtaW46MTUxNDUxNzA4MjYzNjplYzI0OTFlYWEyNDhkZmIyZWIyNjNjODc3YzM2M2Q0MA 和 SESSION →54fd02c7-4067-43c9-94f8-5f6e474cd858<br>"
      + "3、http response的cookies信息中，将携带。（此种方式是推荐使用的方式）<br>"
      + "<b>注意：以上描述只限于登录成功后的返回信息，并不是说每一次业务请求操作，服务端都会向客户端这样返回SESSION属性和persistence属性</b>" + "</p>"
      + "为了保证服务端能够正确识别到客户端已登录的用户权限信息，一旦登录成功后，每一次客户端的请求都需要向服务端发送SESSION属性（非必要，但推荐）和persistence属性<br>"
      + "客户端可以使用以下两种方式，向服务端发送SESSION属性和persistence属性：<br>"
      + "1、使用http request的header发送，类似于：persistence →YWRtaW46MTUxNDUxNzA4MjYzNjplYzI0OTFlYWEyNDhkZmIyZWIyNjNjODc3YzM2M2Q0MA 和 SESSION →54fd02c7-4067-43c9-94f8-5f6e474cd858<br>"
      + "2、使用http request的cookies发送。（此种方式是推荐使用的方式）<br>")
  @RequestMapping(value = "/loginSuccess", method = RequestMethod.POST)
  public @ApiIgnore ResponseModel loginSuccess(HttpServletRequest request,
      HttpServletResponse response, Principal logUser) {
    // String account = logUser.getName();
    // if (StringUtils.isEmpty(account)) {
    // return this.buildHttpReslutForException(new AccessDeniedException("not found op user!"));
    // }
    //
    // // 查询用户基本信息
    // OperatorEntity currentUser = this.verifyOperatorLogin(logUser);
    // // 如果条件成立，说明这个用户存在数据问题。抛出异常
    // if (currentUser == null) {
    // return this.buildHttpReslutForException(new AccessDeniedException("not found op user!"));
    // }
    //
    // // 隔断用户关联信息后返回
    // this.operatorService.updateLoginTime(logUser.getName(), new Date());
    // return this.buildHttpReslut(currentUser, "roles", "modifyUser", "createUser");
    return this.buildHttpReslut();

  }

  /**
   * 由于后端提供的都是restful接口，并没有直接跳转的页面<br>
   * 所以只要访问的url没有通过权限认证，就跳到这个请求上，并直接排除权限异常
   */
  @ApiOperation(value = "由于后端提供的都是restful接口，并没有直接跳转的页面<br>"
      + "所以只要访问的url没有通过权限认证，就跳到这个请求上，并直接排除权限异常")
  @RequestMapping(value = "/loginFail", method = {RequestMethod.GET, RequestMethod.POST})
  public ResponseModel loginFail(HttpServletRequest request) throws IllegalAccessException {
    Object attribute = request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    String message = "用户名或密码错误!";
    if (null != attribute && attribute instanceof UsernameNotFoundException) {
      UsernameNotFoundException exception = (UsernameNotFoundException) attribute;
      message = exception.getMessage();
    }
    return this.buildHttpReslutForException(new IllegalAccessException(message));
  }

  /**
   * 成功登出
   */
  @ApiOperation(value = "成功登出")
  @RequestMapping(value = "/logoutSuccess", method = RequestMethod.GET)
  public ResponseModel logoutSuccess() {
    return this.buildHttpReslut();
  }

  /**
   * 形成角色和运营平台操作者的绑定关系（注意，不能重复绑定）
   */
  @ApiOperation(value = "形成角色和运营平台操作者的绑定关系（注意，不能重复绑定）")
  @RequestMapping(value = "/bindRoleForOperator", method = RequestMethod.POST)
  public ResponseModel bindRoleForOperator(String roleId, String operatorId) {
    try {
      this.securityService.bindRoleForOperator(roleId, operatorId);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 形成角色和功能url的绑定关系
   */
  @ApiOperation(value = "形成角色和功能url的绑定关系")
  @RequestMapping(value = "/bindRoleForCompetence", method = RequestMethod.POST)
  public ResponseModel bindRoleAndCompetence(String roleId, String competenceId) {
    try {
      this.securityService.bindRoleForCompetences(roleId, new String[] {competenceId});
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 形成角色和功能url的绑定关系<br>
   * （可以将指定的一个角色一次绑定多个功能）
   * 
   * @param roleId
   * @param competenceIds
   */
  @ApiOperation(value = "形成角色和功能url的绑定关系（可以将指定的一个角色一次绑定多个功能）")
  @RequestMapping(value = "/bindRoleForCompetences", method = RequestMethod.POST)
  public ResponseModel bindRoleAndCompetences(String roleId, String[] competenceIds) {
    try {
      this.securityService.bindRoleForCompetences(roleId, competenceIds);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 解除角色和代理商的绑定关系
   */
  @ApiOperation(value = "解除角色和运营商操作者的绑定关系")
  @RequestMapping(value = "/unbindRoleForOperator", method = RequestMethod.POST)
  public ResponseModel unbindRoleForOperator(String roleId, String agentId) {
    try {
      this.securityService.unbindRoleForOperator(roleId, agentId);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 解除角色和功能url的绑定关系
   */
  @ApiOperation(value = "解除角色和功能url的绑定关系")
  @RequestMapping(value = "/unbindRoleForCompetence", method = RequestMethod.POST)
  public ResponseModel unbindRoleAndCompetence(String roleId, String competenceId) {
    try {
      this.securityService.unbindRoleForCompetences(roleId, new String[] {competenceId});
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }

  /**
   * 解除角色和功能url的绑定关系<br>
   * （可以将指定的一个角色一次解绑多个功能）
   */
  @ApiOperation(value = "解除角色和功能url的绑定关系,（可以将指定的一个角色一次解绑多个功能）")
  @RequestMapping(value = "/unbindRoleForCompetences", method = RequestMethod.POST)
  public ResponseModel unbindRoleAndCompetences(String roleId, String[] competenceIds) {
    try {
      this.securityService.unbindRoleForCompetences(roleId, competenceIds);
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }
}
