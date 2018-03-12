package com.hc.proxyPool.service;

import java.util.List;

import com.hc.proxyPool.entity.ProxyDataEntity;

public interface ProxyDataService {
  /**
   * 从数据库中随机获取一个代理
   * 
   * @return
   */
  ProxyDataEntity getRandomProxy();

  /**
   * 从数据库中随机获取指定的limit条代理
   * 
   * @param limit
   * @return
   */
  List<ProxyDataEntity> findRandomProxy(int limit);
}
