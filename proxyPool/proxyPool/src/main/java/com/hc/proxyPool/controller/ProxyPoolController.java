package com.hc.proxyPool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hc.proxyPool.controller.model.ResponseModel;
import com.hc.proxyPool.entity.ProxyDataEntity;
import com.hc.proxyPool.service.ProxyDataService;

@RestController
public class ProxyPoolController extends BaseController {
  @Autowired
  private ProxyDataService proxyDataService;

  /**
   * 获取一个随机代理
   * 
   * @return
   */
  @RequestMapping(value = "/randomProxy", method = {RequestMethod.GET})
  public ResponseModel randomProxy() {
    try {
      ProxyDataEntity proxyData = proxyDataService.getRandomProxy();
      return this.buildHttpReslut(proxyData);
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);

    }
  }

  /**
   * 获取一个随机代理
   * 
   * @return
   */
  @RequestMapping(value = "/findRandomProxy", method = {RequestMethod.GET})
  public ResponseModel findRandomProxy(int limit) {
    try {
      List<ProxyDataEntity> proxyDatas = proxyDataService.findRandomProxy(limit);
      return this.buildHttpReslut(proxyDatas);
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);

    }
  }
}
