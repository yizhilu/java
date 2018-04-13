/**
 * 
 */
package com.hc.wechat.repository.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.entity.RoleEntity;
import com.hc.wechat.repository.RoleRepository;


/**
 * 角色dao实现.
 * 
 * @author ly
 * @date 2017年8月21日 下午3:30:04
 * @version V1.0
 */
@Repository(value = "roleDao")
public class RoleDaoImpl implements RoleDao {

  /**
   * 角色repository自动注入.
   */
  @Autowired
  private RoleRepository roleRepository;


  @Override
  public Page<RoleEntity> getByConditions(Map<String, Object> params, Pageable pageable) {
    Page<RoleEntity> list = roleRepository.findAll(new Specification<RoleEntity>() {

      @Override
      public Predicate toPredicate(Root<RoleEntity> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        // 查询角色名称
        if (params.get("name") != null) {
          predicates.add(cb.like(root.get("name").as(String.class),
              "%" + String.valueOf(params.get("name") + "%")));
        }
        // 查询状态
        if (params.get("status") != null) {
          predicates.add(
              cb.equal(root.get("status").as(UseStatus.class), (UseStatus) params.get("status")));
        }
        // 遍历查询条件，查询语句
        query.where(predicates.toArray(new Predicate[predicates.size()]));
        return null;
      }
    }, pageable);
    return list;
  }


  @Override
  public List<RoleEntity> findByConditions(Map<String, Object> params) {
    List<RoleEntity> list = roleRepository.findAll(new Specification<RoleEntity>() {

      @Override
      public Predicate toPredicate(Root<RoleEntity> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        // 查询角色名称
        if (params.get("name") != null) {
          predicates.add(cb.like(root.get("name").as(String.class),
              "%" + String.valueOf(params.get("name") + "%")));
        }
        // 查询状态
        if (params.get("status") != null) {
          predicates.add(
              cb.equal(root.get("status").as(UseStatus.class), (UseStatus) params.get("status")));
        }
        // 遍历查询条件，查询语句
        query.where(predicates.toArray(new Predicate[predicates.size()]));
        return null;
      }
    });
    return list;
  }

}
