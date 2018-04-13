package com.hc.wechat.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.entity.RoleEntity;


/**
 * @author yinwenjie
 */
@Repository("roleRepository")
public interface RoleRepository
    extends
      JpaRepository<RoleEntity, String>,
      JpaSpecificationExecutor<RoleEntity> {

  /**
   * 查询符合角色状态的信息
   * 
   * @param useStatus
   * @return
   */
  @Query("from RoleEntity r left join fetch r.modifier m left join fetch r.creator c where r.status = :useStatus")
  List<RoleEntity> findByStatus(@Param("useStatus") UseStatus useStatus);

  /**
   * 查询所有的角色信息，包括角色的修改者信息
   * 
   * @return
   */
  @Query("from RoleEntity r left join fetch r.modifier m left join fetch r.creator c")
  List<RoleEntity> findAllWithModifer();

  /**
   * 查询指定的管理员用户所绑定的角色信息
   * 
   * @param operatorId 管理员编号（数据层唯一编号）
   * @return
   */
  @Query("from RoleEntity r left join fetch r.operators a where a.id = :operatorId")
  Set<RoleEntity> findByOperatorId(@Param("operatorId") String operatorId);

  /**
   * 查询指定的功能描述所绑定的角色信息
   * 
   * @param competenceId 功能描述信息
   * @return
   */
  @Query("from RoleEntity r left join fetch r.competences c where c.id = :competenceId")
  Set<RoleEntity> findByCompetenceId(@Param("competenceId") String competenceId);

  /**
   * 按照角色名，查询指定的角色信息
   * 
   * @param name
   * @return
   */
  RoleEntity findByName(String name);
}
