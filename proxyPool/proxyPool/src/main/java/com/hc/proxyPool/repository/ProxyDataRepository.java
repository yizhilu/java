package com.hc.proxyPool.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.proxyPool.entity.ProxyDataEntity;



@Repository("proxyDataRepository")
public interface ProxyDataRepository
    extends
      JpaRepository<ProxyDataEntity, String>,
      JpaSpecificationExecutor<ProxyDataEntity> {
  /**
   * 
   * @param dataSource
   * @return
   */
  @Query(value = "from ProxyDataEntity where dataSource=:dataSource and crawleDate<:date")
  List<ProxyDataEntity> findByDataSource(@Param("dataSource") String dataSource,
      @Param("date") Date date);

  /**
   * 按代理ip和 端口查询
   * 
   * @param proxyIp
   * @param proxyPort
   * @return
   */
  ProxyDataEntity findByProxyIpAndProxyPort(String proxyIp, String proxyPort);

  /**
   * 从数据库中随机获取一个代理
   * 
   * @return
   */
  @Query(value = "SELECT * FROM proxy_data  ORDER BY  RAND() LIMIT 1", nativeQuery = true)
  ProxyDataEntity getRandomProxy();

  /**
   * 从数据库中随机获取指定的limit条代理
   * 
   * @param limit
   * @return
   */
  @Query(value = "SELECT * FROM proxy_data  ORDER BY  RAND() LIMIT :limit", nativeQuery = true)
  List<ProxyDataEntity> findRandomProxy(@Param("limit") int limit);
}
