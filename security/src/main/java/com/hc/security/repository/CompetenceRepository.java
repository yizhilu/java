package com.hc.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.CompetenceEntity;
import com.hc.security.entity.enums.StatusType;


@Repository("competenceRepostory")
public interface CompetenceRepository
    extends
      JpaRepository<CompetenceEntity, String>,
      JpaSpecificationExecutor<CompetenceEntity> {
  /**
   * 查询符合权限状态的信息
   * 
   * @param useStatus
   * @return
   */
  List<CompetenceEntity> findByStatus(StatusType status);

  /**
   * 查询为当前URL设置的角色信息，注意，这里没有通过method进行过滤
   * 
   * @param resource
   * @return
   */
  List<CompetenceEntity> findByResource(String resource);

  /**
   * 查询指定的角色已绑定的功能信息，无论这些功能是否可用（状态是否正常）
   * 
   * @param roleId
   * @return
   */
  @Query("from CompetenceEntity c left join fetch c.roles r where r.id = :roleId")
  List<CompetenceEntity> findByRoleId(@Param("roleId") String roleId);

  /**
   * 查询指定的角色已绑定的功能信息，且这些功能状态符合查询的要求
   * 
   * @param roleId
   * @return
   */
  @Query("from CompetenceEntity c left join fetch c.roles r where r.id = :roleId and c.status = :status")
  List<CompetenceEntity> findByRoleId(@Param("roleId") String roleId,
      @Param("status") StatusType status);

  /**
   * 根据方法（methods）和路径（resource）查询.
   * 
   * @param methods 方法
   * @param resource 路径
   * @return int 查询的总条数
   */
  @Query(value = "SELECT count(*) FROM competence WHERE methods = :methods AND resource = :resource", nativeQuery = true)
  int findByMethodsAndResource(@Param("methods") String methods,
      @Param("resource") String resource);

  /**
   * 根据方法（methods）和路径（resource）以及权限id查询（排除自身，即不等于该id）.
   * 
   * @param methods 方法
   * @param resource 路径
   * @param id 权限id
   * @return int 查询的总条数
   */
  @Query(value = "SELECT count(*) FROM competence WHERE methods = :methods AND resource = :resource AND id != :id", nativeQuery = true)
  int findByMethodsAndResourceAndId(@Param("methods") String methods,
      @Param("resource") String resource, @Param("id") String id);

  /**
   * 查询指定功能下绑定的子级功能，并按照orderIndex进行排序返回（这些功能必须可用）
   * 
   * @param parentCompetenceId
   * @return
   */
  @Query("from CompetenceEntity c left join fetch c.parentCompetence p where c.status = 1 and p.id = :parentCompetenceId order by c.orderIndex")
  List<CompetenceEntity> findByParentCompetences(
      @Param("parentCompetenceId") String parentCompetenceId);

  /**
   * 查询目前所有level为0的功能，并按照orderIndex进行排序返回（这些功能必须可用）
   * 
   * @return
   */
  @Query("from CompetenceEntity c where c.level = 0 and c.status = 1 order by c.orderIndex")
  List<CompetenceEntity> findRootCompetence();

  /**
   * 根据状态查询该状态下的所有权限信息.
   * 
   * @param status 状态
   * @return List<CompetenceEntity>
   */
  @Query("from CompetenceEntity c where c.status = :status")
  List<CompetenceEntity> getAll(@Param("status") StatusType status);

  /**
   * 设置指定的功能为主功能目录上的根级功能
   * 
   * @param competenceId 指定的功能编号
   */
  @Modifying
  @Query(value = "update competence set level = 0 where id = :competenceId", nativeQuery = true)
  void updateRootCompetence(@Param("competenceId") String competenceId);

  /**
   * 该服务将若干个子级功能绑定到父级功能下，如果某个子级功能已经绑定了父级功能，则之前的绑定被去掉
   * 
   * @param parentCompetenceId 父级功能
   * @param childCompetenceId 子级功能
   * @param orderIndex 排序数字
   * @param level 新设置的层级
   * @return
   */
  @Modifying
  @Query(value = "update competence set parent_id = :parentCompetenceId , order_index = :orderIndex , level = :level where id = :childCompetenceId", nativeQuery = true)
  void bindChild(@Param("parentCompetenceId") String parentCompetenceId,
      @Param("orderIndex") long orderIndex, @Param("level") int level,
      @Param("childCompetenceId") String childCompetenceId);

  /**
   * 该服务将若干个子级功能从他们当前的父级功能下解绑。
   * 
   * @param childCompetenceIds 若干个将要解绑的子级功能
   */
  @Modifying
  @Query(value = "update competence set parent_id = null where id = :childCompetenceId", nativeQuery = true)
  void unBindChild(@Param("childCompetenceId") String childCompetenceId);
}
