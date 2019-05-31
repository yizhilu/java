package com.hc.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.security.entity.OperatorEntity;


@Repository("operatorRepostory")
public interface OperatorRepository
    extends
      JpaRepository<OperatorEntity, String>,
      JpaSpecificationExecutor<OperatorEntity> {

  @Query("from OperatorEntity o " + " left join fetch o.createUser "
      + " left join fetch o.modifyUser " + " left join fetch o.roles " + " where o.id=:id "
      + " order by o.createDate desc")
  OperatorEntity findByIdFetch(@Param("id") String id);
}
