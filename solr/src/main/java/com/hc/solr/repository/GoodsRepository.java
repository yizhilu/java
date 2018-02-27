package com.hc.solr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hc.solr.entity.GoodsEntity;

@Repository("goodsRepostory")
public interface GoodsRepository
    extends
      JpaRepository<GoodsEntity, String>,
      JpaSpecificationExecutor<GoodsEntity> {
}
