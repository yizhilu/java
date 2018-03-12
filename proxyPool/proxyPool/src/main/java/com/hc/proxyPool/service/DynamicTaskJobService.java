package com.hc.proxyPool.service;

import java.util.List;

import com.hc.proxyPool.entity.DynameicTaskJobEntity;

public interface DynamicTaskJobService {
  /**
   * 创建任务
   * 
   * @param dynameicTaskJob
   * @return
   */
  DynameicTaskJobEntity create(DynameicTaskJobEntity dynameicTaskJob);

  /**
   * 更新任务
   * 
   * @param dynameicTaskJob
   * @return
   */
  DynameicTaskJobEntity update(DynameicTaskJobEntity dynameicTaskJob);

  /**
   * 停止任务
   * 
   * @param dynameicTaskJob
   * @return
   */
  DynameicTaskJobEntity stop(DynameicTaskJobEntity dynameicTaskJob);
  /**
   * 开始任务
   * @param dynameicTaskJob
   * @return
   */
  DynameicTaskJobEntity start(DynameicTaskJobEntity dynameicTaskJob);
  /**
   * 通过id查找任务
   * 
   * @param id
   * @return
   */
  DynameicTaskJobEntity findById(String id);

  /**
   * 通过任务名称查询任务
   * 
   * @param jobName
   * @return
   */
  DynameicTaskJobEntity findByJobName(String jobName);

  /**
   * 查询所有任务
   * 
   * @return
   */
  List<DynameicTaskJobEntity> findAll();
}
