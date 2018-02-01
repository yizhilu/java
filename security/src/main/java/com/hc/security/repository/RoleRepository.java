package com.hc.security.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.RoleEntity;
import com.hc.security.entity.enums.StatusType;

@Repository("roleRepostory")
public interface RoleRepository
    extends
      JpaRepository<RoleEntity, String>,
      JpaSpecificationExecutor<RoleEntity> {
  /**
   * 查询指定用户下所涉及的角色信息
   * 
   * @param operatorId 指定的操作者id
   * @return
   */
  @Query("from RoleEntity r left join fetch r.operators o where o.id = :operatorId")
  Set<RoleEntity> findByOperatorId(@Param("operatorId") String operatorId);

  /**
   * 查询指定功能下所涉及的角色信息
   * 
   * @param operatorId 指定的功能id
   * @return
   */
  @Query("from RoleEntity r left join fetch r.competences c where c.id = :competenceId")
  Set<RoleEntity> findByCompetenceId(@Param("competenceId") String competenceId);

  /**
   * 查询符合角色状态的信息
   * 
   * @param useStatus
   * @return
   */
  @Query("from RoleEntity r left join fetch r.modifier m left join fetch r.creator c where r.status = :statusType")
  List<RoleEntity> findByStatus(@Param("statusType") StatusType statusType);

  /**
   * 查询所有的角色信息，包括角色的修改者信息
   * 
   * @return
   */
  @Query("from RoleEntity r left join fetch r.modifier m left join fetch r.creator c")
  List<RoleEntity> findAllWithModifer();

  /**
   * 按照角色名，查询指定的角色信息
   * 
   * @param name
   * @return
   */
  RoleEntity findByName(String name);
}
