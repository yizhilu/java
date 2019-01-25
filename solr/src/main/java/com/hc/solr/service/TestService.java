package com.hc.solr.service;

import com.hc.solr.entity.GoodsEntity;
import com.hc.solr.entity.TestEntity;

public interface TestService {
  TestEntity add(TestEntity test);

  TestEntity addGoodsAndTest(GoodsEntity goods, TestEntity test);

  void addAfterFind();

}
