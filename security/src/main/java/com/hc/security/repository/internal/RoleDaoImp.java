/**
 * 
 */
package com.hc.security.repository.internal;

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

import com.hc.security.entity.RoleEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.repository.RoleRepository;



@Repository(value = "roleDao")
public class RoleDaoImp implements RoleDao {

  /**
   * 角色repository自动注入.
   */
  @Autowired
  private RoleRepository roleRepository;
  
  /* (non-Javadoc)
   * @see com.cheshangma.merchant.repository.internal.RoleDao#getByCondition(java.util.Map, org.springframework.data.domain.Pageable)
   */
  @Override
  public Page<RoleEntity> getByCondition(Map<String, Object> params, Pageable pageable) {
    Page<RoleEntity> list = roleRepository.findAll(new Specification<RoleEntity>() {
      
      @Override
      public Predicate toPredicate(Root<RoleEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        // 查询角色名称
        if (params.get("roleName") != null) {
          predicates.add(cb.like(root.get("name").as(String.class),
              "%" + String.valueOf(params.get("roleName") + "%")));
        }
        // 查询状态
        if (params.get("statusType") != null) {
          predicates.add(cb.equal(root.get("status").as(StatusType.class), (StatusType)params.get("statusType")));
        }
        // 遍历查询条件，查询语句
        query.where(predicates.toArray(new Predicate[predicates.size()]));
        return null;
      }
    }, pageable);
    return list;
  }

}
