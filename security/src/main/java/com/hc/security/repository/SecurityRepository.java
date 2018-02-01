package com.hc.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hc.security.entity.CompetenceEntity;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.RoleEntity;

public interface SecurityRepository extends JpaRepository<RoleEntity, String> {

  /**
   * 形成角色和运营平台操作者的绑定关系（注意，不能重复绑定）
   */
  @Modifying
  @Query(value = "insert into role_operator (operator_id,role_id) values (:operatorId , :roleId)", nativeQuery = true)
  public void bindRoleForOperator(@Param("roleId") String roleId,
      @Param("operatorId") String operatorId);

  /**
   * 形成角色和功能url的绑定关系
   */
  @Modifying
  @Query(value = "insert into role_competence (competence_id,role_id) values (:competenceId , :roleId)", nativeQuery = true)
  public void bindRoleAndCompetence(@Param("roleId") String roleId,
      @Param("competenceId") String competenceId);

  /**
   * 该方法可以确定指定的角色和指定的系统功能是否已有绑定关系
   * 
   * @param roleId 指定的角色
   * @param competenceId 指定的系统功能
   */
  @Query("from CompetenceEntity c left join fetch c.roles r where c.id = :competenceId and r.id = :roleId")
  public CompetenceEntity findCompetenceByRoleIdAndCompetenceId(@Param("roleId") String roleId,
      @Param("competenceId") String competenceId);

  /**
   * 该方法可以确定指定的角色和指定的代理商是否已有绑定关系
   * 
   * @param roleId 指定的角色
   * @param competenceId 指定的代理商
   */
  @Query("from OperatorEntity a left join fetch a.roles r where a.id = :operatorId and r.id = :roleId")
  public OperatorEntity findOperatorByRoleIdAndOperatorId(@Param("roleId") String roleId,
      @Param("operatorId") String operatorId);

  /**
   * 解除角色和代理商的绑定关系
   */
  @Modifying
  @Query(value = "delete from role_operator where role_id = :roleId and operator_id = :operatorId", nativeQuery = true)
  public void unbindRoleForOperator(@Param("roleId") String roleId,
      @Param("operatorId") String operatorId);

  /**
   * 解除角色和功能url的绑定关系
   */
  @Modifying
  @Query(value = "delete from role_competence where role_id = :roleId and competence_id = :competenceId", nativeQuery = true)
  public void unbindRoleAndCompetence(@Param("roleId") String roleId,
      @Param("competenceId") String competenceId);
}
