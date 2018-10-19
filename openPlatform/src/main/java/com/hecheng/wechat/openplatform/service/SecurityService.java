package com.hecheng.wechat.openplatform.service;

public interface SecurityService {
  /**
   * 形成角色和运营平台操作者的绑定关系（注意，不能重复绑定）
   */
  public void bindRoleForOperator(String roleId, String operatorId);

  /**
   * 形成角色和功能url的绑定关系<br>
   * 该方法将指定的角色，一次绑定多个功能
   */
  public void bindRoleForCompetences(String roleId, String[] competenceIds);

  /**
   * 解除角色和运营平台操作者的绑定关系
   */
  public void unbindRoleForOperator(String roleId, String operatorId);

  /**
   * 解除角色和功能url的绑定关系<br>
   * 该方法将指定的角色，一次解绑多个功能
   */
  public void unbindRoleForCompetences(String roleId, String[] competenceIds);

  /**
   * 形成角色和运营平台操作者的绑定关系（注意，不能重复绑定）可以一次绑定多个
   */
  public void bindRoleForOperator(String[] roleId, String userId);

  /**
   * 解除角色和运营平台操作者的绑定关系 可以一次解绑多个
   */
  public void unbindRoleForOperator(String[] roleId, String userId);
}
