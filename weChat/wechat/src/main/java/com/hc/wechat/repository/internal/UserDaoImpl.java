package com.hc.wechat.repository.internal;

import java.util.ArrayList;
import java.util.List;

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

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.entity.UserEntity;
import com.hc.wechat.repository.UserRepository;


@Repository("UserDao")
public class UserDaoImpl implements UserDao {

  @Autowired
  private UserRepository userRepository;

  @Override
  public Page<UserEntity> getByConditions(String nickName, UseStatus status, String telephone,
      Pageable pageable) {
    Page<UserEntity> list = userRepository.findAll(new Specification<UserEntity>() {

      @Override
      public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        // 查询昵称
        if (StringUtils.isNotBlank(nickName)) {
          predicates.add(
              cb.like(root.get("nickName").as(String.class), "%" + String.valueOf(nickName + "%")));
        }
        // 查询手机
        if (StringUtils.isNotBlank(telephone)) {
          predicates.add(cb.like(root.get("telephone").as(String.class),
              "%" + String.valueOf(telephone + "%")));
        }
        // 查询状态
        if (status != null) {
          predicates.add(cb.equal(root.get("status").as(UseStatus.class), status));
        }
        // 遍历查询条件，查询语句
        query.where(predicates.toArray(new Predicate[predicates.size()]));
        return null;
      }
    }, pageable);
    return list;
  }
}
