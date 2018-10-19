package com.hecheng.wechat.openplatform.repository.internal;

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

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.entity.CompetenceEntity;
import com.hecheng.wechat.openplatform.repository.CompetenceRepository;


/**
 * 权限dao实现.
 * 
 * @author ly
 * @date 2017年8月21日 下午6:04:33
 * @version V1.0
 */
@Repository(value = "competenceDao")
public class CompetenceDaoImpl implements CompetenceDao {

  /**
   * 权限repository自动注入.
   */
  @Autowired
  private CompetenceRepository competenceRepository;

  @Override
  public Page<CompetenceEntity> getByConditions(Map<String, Object> params, Pageable pageable) {
    Page<CompetenceEntity> list =
        competenceRepository.findAll(new Specification<CompetenceEntity>() {

          @Override
          public Predicate toPredicate(Root<CompetenceEntity> root, CriteriaQuery<?> query,
              CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<Predicate>();
            // 方法描述 例如：POST或者POST|GET|DELETE
            if (params.get("methods") != null) {
              predicates.add(cb.like(root.get("methods").as(String.class),
                  "%" + String.valueOf(params.get("methods")) + "%"));
            }
            // 备注（模糊查询）
            if (params.get("comment") != null) {
              predicates.add(cb.like(root.get("comment").as(String.class),
                  "%" + String.valueOf(params.get("comment")) + "%"));
            }
            // 状态查询
            if (params.get("status") != null) {
              predicates.add(cb.equal(root.get("status").as(UseStatus.class),
                  (UseStatus) params.get("status")));
            }
            // 权限串查询（模糊查询）
            if (params.get("resource") != null) {
              predicates.add(cb.like(root.get("resource").as(String.class),
                  "%" + params.get("resource") + "%"));
            }
            // 遍历查询条件，查询语句
            query.where(predicates.toArray(new Predicate[predicates.size()]));
            return null;
          }
        }, pageable);
    return list;
  }

}
