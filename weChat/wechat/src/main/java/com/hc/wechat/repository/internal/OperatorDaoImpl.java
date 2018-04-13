package com.hc.wechat.repository.internal;

import java.util.ArrayList;
import java.util.Date;
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
import com.hc.wechat.entity.OperatorEntity;
import com.hc.wechat.repository.OperatorRepository;

/**
 * 后台用户dao实现
 * 
 * @author hc
 *
 */
@Repository("operatorDao")
public class OperatorDaoImpl implements OperatorDao {

  /**
   * 后台用户repository自动注入.
   */
  @Autowired
  private OperatorRepository operatorRepository;

  @Override
  public Page<OperatorEntity> getByConditions(Map<String, Object> params, Pageable pageable) {
    Page<OperatorEntity> list = operatorRepository.findAll(new Specification<OperatorEntity>() {

      @Override
      public Predicate toPredicate(Root<OperatorEntity> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        // 创建时间区间
        if (params.get("startCreateDate") != null) {
          predicates.add(cb.greaterThan(root.get("createDate"), (Date) params.get("startDate")));
        }
        if (params.get("endCreateDate") != null) {
          predicates.add(cb.lessThan(root.get("createDate"), (Date) params.get("endDate")));
        }
        // 查询条件-账号
        if (params.get("account") != null) {
          predicates.add(cb.equal(root.get("account").as(String.class),
              String.valueOf(params.get("account"))));
        }
        // 查询条件模糊-姓名
        if (params.get("name") != null) {
          predicates.add(cb.like(root.get("name").as(String.class),
              "%" + String.valueOf(params.get("name")) + "%"));
        }
        // 查询条件-状态
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

}
