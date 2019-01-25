package com.hc.solr.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hc.solr.annotation.RetryOnOptimisticLockingFailure;
import com.hc.solr.controller.model.ResponseModel;
import com.hc.solr.entity.GoodsEntity;
import com.hc.solr.entity.TestEntity;
import com.hc.solr.service.GoodsService;
import com.hc.solr.service.TestService;

@RestController
@RequestMapping("/goods")
public class GoodsController extends BaseController {

  private Logger logger = Logger.getLogger(GoodsController.class);
  @Autowired
  private GoodsService goodsService;
  @Autowired
  private TestService testService;

  @RequestMapping(value = "/add", method = {RequestMethod.POST})
  public ResponseModel add(@RequestBody GoodsEntity goods) throws Exception {
    try {
      return this.buildHttpReslut(goodsService.add(goods));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RetryOnOptimisticLockingFailure
  @RequestMapping(value = "/updateStack", method = {RequestMethod.POST})
  public ResponseModel updateStack(String goodsId, int stack) throws Exception {
    try {
      return this.buildHttpReslut(goodsService.updateStack(goodsId, stack));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RetryOnOptimisticLockingFailure
  @RequestMapping(value = "/subStack", method = {RequestMethod.POST})
  public ResponseModel subStack(String goodsId) throws Exception {
    try {
      return this.buildHttpReslut(goodsService.subStack(goodsId));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RequestMapping(value = "/addGoods", method = {RequestMethod.POST})
  public ResponseModel addGoods(@RequestBody GoodsEntity goods) throws Exception {
    try {
      return this.buildHttpReslut(goodsService.add(goods));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RequestMapping(value = "/addTest", method = {RequestMethod.POST})
  public ResponseModel addTest(@RequestBody TestEntity test) throws Exception {
    try {
      return this.buildHttpReslut(testService.add(test));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RequestMapping(value = "/addGoodsAndTest", method = {RequestMethod.POST})
  public ResponseModel addGoodsAndTest(@RequestBody TestEntity test) throws Exception {
    try {
      GoodsEntity goods = new GoodsEntity();
      goods.setName("2222");
      return this.buildHttpReslut(testService.addGoodsAndTest(goods, test));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RequestMapping(value = "/addAfterFind", method = {RequestMethod.GET})
  public ResponseModel addAfterFind() throws Exception {
    try {
      testService.addAfterFind();
      return this.buildHttpReslut();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }
}
