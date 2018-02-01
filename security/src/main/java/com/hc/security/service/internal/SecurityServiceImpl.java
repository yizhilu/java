package com.hc.security.service.internal;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.security.entity.CompetenceEntity;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.RoleEntity;
import com.hc.security.repository.CompetenceRepository;
import com.hc.security.repository.OperatorRepository;
import com.hc.security.repository.RoleRepository;
import com.hc.security.repository.SecurityRepository;
import com.hc.security.service.SecurityService;

@Service("SecurityServiceImpl")
public class SecurityServiceImpl implements SecurityService {

  @Autowired
  private SecurityRepository securityRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private CompetenceRepository competenceRepository;

  @Autowired
  private OperatorRepository operatorRepository;

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.operation.service.SecurityService#bindRoleForOperator(java.lang.String,
   * java.lang.String)
   */
  @Override
  @Transactional
  public void bindRoleForOperator(String roleId, String operatorId) {
    Validate.notEmpty(roleId, "roleId not be empty!");
    Validate.notEmpty(operatorId, "operatorId not be empty!");
    this.checkRoleExist(roleId);
    this.checkOperatorExist(operatorId);

    // 如果已经有绑定信息，则不予许再进行绑定
    OperatorEntity oldBind =
        this.securityRepository.findOperatorByRoleIdAndOperatorId(roleId, operatorId);
    if (oldBind != null) {
      throw new IllegalArgumentException("该运营商操作者和角色已经有绑定关系，不能重复绑定!");
    }

    // 进行绑定
    this.securityRepository.bindRoleForOperator(roleId, operatorId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.operation.service.SecurityService#bindRoleForCompetences(java.lang.String,
   * java.lang.String[])
   */
  @Override
  @Transactional
  public void bindRoleForCompetences(String roleId, String[] competenceIds) {
    Validate.notEmpty(roleId, "roleId not be empty!");
    Validate.notEmpty(competenceIds, "competenceId not be empty!");
    this.checkRoleExist(roleId);

    // 依次进行角色和功能的绑定
    for (String competenceId : competenceIds) {
      this.checkCompetenceExist(competenceId);
      // 如果已经有绑定信息，则不予许再进行绑定
      CompetenceEntity oldBind =
          this.securityRepository.findCompetenceByRoleIdAndCompetenceId(roleId, competenceId);
      if (oldBind != null) {
        throw new IllegalArgumentException("该系统功能和角色已经有绑定关系，不能重复绑定!");
      }
      // 进行绑定
      this.securityRepository.bindRoleAndCompetence(roleId, competenceId);
    }
  }

  @Override
  @Transactional
  public void unbindRoleForOperator(String roleId, String operatorId) {
    Validate.notEmpty(roleId, "roleId not be empty!");
    Validate.notEmpty(operatorId, "operatorId not be empty!");
    this.checkRoleExist(roleId);
    this.checkOperatorExist(operatorId);

    // 解除绑定
    this.securityRepository.unbindRoleForOperator(roleId, operatorId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.cheshangma.operation.service.SecurityService#unbindRoleForCompetences(java.lang.String,
   * java.lang.String[])
   */
  @Override
  @Transactional
  public void unbindRoleForCompetences(String roleId, String[] competenceIds) {
    Validate.notEmpty(roleId, "roleId not be empty!");
    Validate.notEmpty(competenceIds, "competenceId not be empty!");
    this.checkRoleExist(roleId);
    // 依次解除绑定
    for (String competenceId : competenceIds) {
      this.checkCompetenceExist(competenceId);
      // 解除绑定
      this.securityRepository.unbindRoleAndCompetence(roleId, competenceId);
    }
  }

  /**
   * 确定有这样一个角色
   * 
   * @param roleId
   */
  private void checkRoleExist(String roleId) {
    RoleEntity currentRole = this.roleRepository.findOne(roleId);
    if (currentRole == null) {
      throw new IllegalArgumentException("没有发现指定的角色信息,请检查！");
    }
  }

  /**
   * 确定有这样一个运营平台操作者
   * 
   * @param merchantId
   */
  private void checkOperatorExist(String operatorId) {
    OperatorEntity operator = this.operatorRepository.findOne(operatorId);
    if (operator == null) {
      throw new IllegalArgumentException("没有发现指定的运营平台操作者信息,请检查！");
    }
  }

  /**
   * 确定有这样一个功能
   * 
   * @param competenceId
   */
  private void checkCompetenceExist(String competenceId) {
    CompetenceEntity competence = this.competenceRepository.findOne(competenceId);
    if (competence == null) {
      throw new IllegalArgumentException("没有发现指定的功能信息,请检查！");
    }
  }
}
