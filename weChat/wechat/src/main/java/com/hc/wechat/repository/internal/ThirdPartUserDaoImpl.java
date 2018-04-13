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

import com.hc.wechat.common.enums.PlatFormType;
import com.hc.wechat.entity.ThirdPartUserEntity;
import com.hc.wechat.repository.ThirdPartUserRepository;

@Repository("thirdPartUserDao")
public class ThirdPartUserDaoImpl implements ThirdPartUserDao {
  @Autowired
  private ThirdPartUserRepository thirdPartUserRepository;

  @Override
  public Page<ThirdPartUserEntity> getByCondition(String nickName, PlatFormType platFormType,
      Pageable pageable) {
    Page<ThirdPartUserEntity> list =
        thirdPartUserRepository.findAll(new Specification<ThirdPartUserEntity>() {

          @Override
          public Predicate toPredicate(Root<ThirdPartUserEntity> root, CriteriaQuery<?> query,
              CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<Predicate>();
            // 查询昵称
            if (StringUtils.isNotBlank(nickName)) {
              predicates.add(cb.like(root.get("nickName").as(String.class),
                  "%" + String.valueOf(nickName + "%")));
            }
            // 查询平台类型
            if (platFormType != null) {
              predicates
                  .add(cb.equal(root.get("platFormType").as(PlatFormType.class), platFormType));
            }
            // 遍历查询条件，查询语句
            query.where(predicates.toArray(new Predicate[predicates.size()]));
            return null;
          }
        }, pageable);
    return list;
  }

}
