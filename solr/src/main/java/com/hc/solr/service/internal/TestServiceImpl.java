package com.hc.solr.service.internal;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.solr.entity.GoodsEntity;
import com.hc.solr.entity.TestEntity;
import com.hc.solr.repository.secondary.TestRepository;
import com.hc.solr.service.GoodsService;
import com.hc.solr.service.TestService;

@Service("testService")
public class TestServiceImpl implements TestService {
  @Autowired
  private TestRepository testRepository;
  @Autowired
  private GoodsService goodsService;

  @Override
  @Transactional
  public TestEntity add(TestEntity test) {
    Validate.notNull(test, "test不能为空");
    Validate.notBlank(test.getName(), "testName不能为空");
    return testRepository.save(test);
  }

  @Override
  @Transactional
  public TestEntity addGoodsAndTest(GoodsEntity goods, TestEntity test) {
    goodsService.add(goods);
    Validate.notNull(test, "test不能为空");
    Validate.notBlank(test.getName(), "testName不能为空");
    return testRepository.save(test);
  }

  @Override
  @Transactional
  public void addAfterFind() {
    TestEntity test = new TestEntity();
    test.setName("1111");
    test.setNum(10);
    Date now = new Date();
    test.setCreateDate(now);
    add(test);
    Date start = DateUtils.addDays(now, -1);
    List<TestEntity> list = testRepository.findByNumCond(1, 10);
    List<TestEntity> list1 = testRepository.findByNumCond(1, 11);
    List<TestEntity> list3 = testRepository.findByNumCond2(start, now);
    List<TestEntity> list2 = testRepository.findByNumCond(start, now);

  }
}
