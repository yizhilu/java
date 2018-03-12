package com.hc.solr.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hc.solr.controller.model.ResponseCode;
import com.hc.solr.controller.model.ResponseModel;
import com.hc.solr.entity.GoodsEntity;
import com.hc.solr.service.SolrService;

import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

  private Logger logger = Logger.getLogger(TestController.class);
  @Autowired
  private SolrService solrService;

  @RequestMapping(value = "/add", method = {RequestMethod.POST})
  public ResponseModel add() {
    try {
      GoodsEntity goods1 = new GoodsEntity();
      goods1.setName("苹果手机");
      goods1.setId("1");
      goods1.setImagePath("xxxxxx");
      goods1.setDesc("苹果手机最新款");
      goods1.setPrice(new BigDecimal(4999));
      goods1.setCategory("手机");
      goods1.setBrand("苹果");
      GoodsEntity goods2 = new GoodsEntity();
      goods2.setName("小米手机7");
      goods2.setId("2");
      goods2.setImagePath("xxxxxx");
      goods2.setDesc("小米手机7最新款");
      goods2.setPrice(new BigDecimal(3999));
      goods2.setCategory("手机");
      goods2.setBrand("小米");
      GoodsEntity goods3 = new GoodsEntity();
      goods3.setName("小米手机6");
      goods3.setId("3");
      goods3.setImagePath("xxxxxx");
      goods3.setDesc("小米手机6最新款");
      goods3.setPrice(new BigDecimal(2999));
      goods3.setCategory("手机");
      goods3.setBrand("小米");
      GoodsEntity goods4 = new GoodsEntity();
      goods4.setName("华为手机");
      goods4.setId("4");
      goods4.setImagePath("xxxxxx");
      goods4.setDesc("华为手机2018");
      goods4.setPrice(new BigDecimal(5999));
      goods4.setCategory("手机");
      goods4.setBrand("华为");
      GoodsEntity goods5 = new GoodsEntity();
      goods5.setName("华为平板电脑");
      goods5.setId("5");
      goods5.setImagePath("xxxxxx");
      goods5.setDesc("华为平板电脑2018");
      goods5.setPrice(new BigDecimal(5999));
      goods5.setCategory("平板电脑");
      goods5.setBrand("华为");
      solrService.addDocument(goods1);
      solrService.addDocument(goods2);
      solrService.addDocument(goods3);
      solrService.addDocument(goods4);
      solrService.addDocument(goods5);
      return null;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RequestMapping(value = "/del", method = {RequestMethod.POST})
  public ResponseModel del() {
    try {
      solrService.delDocumentById("1");
      return null;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RequestMapping(value = "/delByQuery", method = {RequestMethod.POST})
  public ResponseModel delByQuery() {
    try {
      solrService.delDocumentByQuery("id:2");
      return null;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return this.buildHttpReslutForException(e);
    }
  }

  @RequestMapping(value = "/listPage", method = RequestMethod.GET)
  public ResponseModel listPage(@ApiParam("搜索关键字") String keyWord, String category,
      @ApiParam("排序字段，该参数可不用") String orderByFiled,
      @ApiParam("排序类型：ASC升序，DESC降序，该参数可不用") String ascOrDesc,@PageableDefault Pageable pageable) {
    try {
      // 存储其它搜索（固定）字段：如企业类型
      Map<String, Object> map = new HashMap<String, Object>();
      if (StringUtils.isNotBlank(category)) {
        map.put("category", category);
      }
      Map<String, Object> lisCompanyPojos =
          solrService.query(keyWord, map, orderByFiled, ascOrDesc, pageable);
      return new ResponseModel(new Date().getTime(), lisCompanyPojos, ResponseCode._200, null);
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }
}
