package com.hc.proxyPool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hc.proxyPool.entity.DynameicTaskJobEntity;



@Repository("dynameicTaskJobRepository")
public interface DynameicTaskJobRepository
    extends
      JpaRepository<DynameicTaskJobEntity, String>,
      JpaSpecificationExecutor<DynameicTaskJobEntity> {
  /**
   * 通过任务名称查询任务
   * 
   * @param jobName
   * @return
   */
  DynameicTaskJobEntity findByJobName(String jobName);

}
