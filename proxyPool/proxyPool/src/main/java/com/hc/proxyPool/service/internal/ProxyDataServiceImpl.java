package com.hc.proxyPool.service.internal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.proxyPool.entity.ProxyDataEntity;
import com.hc.proxyPool.repository.ProxyDataRepository;
import com.hc.proxyPool.service.ProxyDataService;

@Service("proxyDataService")
public class ProxyDataServiceImpl implements ProxyDataService {
  @Autowired
  private ProxyDataRepository proxyDataRepository;

  @Override
  public ProxyDataEntity getRandomProxy() {

    return proxyDataRepository.getRandomProxy();
  }

  @Override
  public List<ProxyDataEntity> findRandomProxy(int limit) {
    
    return proxyDataRepository.findRandomProxy(limit);
  }
  
}
