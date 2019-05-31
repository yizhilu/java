package com.hc.security.repository.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.repository.OperatorRepository;


@Repository(value = "operatorDao")
public class OperatorDaoImpl implements OperatorDao {

  /**
   * 管理员repository.
   */
  @Autowired
  private OperatorRepository operatorRepository;

  @Override
  public OperatorEntity saveAndFlush(OperatorEntity operator) {
    return operatorRepository.saveAndFlush(operator);
  }

  @Override
  public void deleteOperator(OperatorEntity operator) {
    operatorRepository.delete(operator);
  }

  @Override
  public void updateLoginTime(String account, Date loginTime) {
    // operatorRepository.updateLoginTime(account, loginTime);
  }

  @Override
  public OperatorEntity getById(String id) {
    return operatorRepository.findByIdFetch(id);
  }

  @Override
  public Page<OperatorEntity> findOperatorByParmas(Map<String, Object> map, Pageable pageable) {
    String account = (String) map.get("account");
    String name = (String) map.get("name");
    StatusType status = (StatusType) map.get("status");
    Page<OperatorEntity> operatorList = null;
    Specification<OperatorEntity> querySpecifi = new Specification<OperatorEntity>() {
      @Override
      public Predicate toPredicate(Root<OperatorEntity> root, CriteriaQuery<?> criteriaQuery,
          CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (StringUtils.isNotBlank(account)) {
          predicates.add(criteriaBuilder.like(root.get("account"), account));
        }
        if (StringUtils.isNotBlank(name)) {
          predicates.add(criteriaBuilder.like(root.get("name"), name));
        }
        if (null != status) {
          predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }
        predicates.add(criteriaBuilder.equal(root.get("isDel"), false));
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
    operatorList = this.operatorRepository.findAll(querySpecifi, pageable);
    return operatorList;
  }
}
