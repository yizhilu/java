package com.hc.solr;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.Test;

import com.hc.solr.entity.GoodsEntity;
import com.hc.solr.repository.primary.GoodsRepository;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes = ApplicationStarter.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTest3 extends AbstractTestNGSpringContextTests {
  @LocalServerPort
  private int port;
  @Autowired
  private TestRestTemplate testRestTemplate;
  @Autowired
  private GoodsRepository goodsRepository;

  @Test
  public void test1() throws InterruptedException {
    GoodsEntity goods = goodsRepository.findOne("7da93cc3-2db8-4918-9465-6c53c13ff472");
    goods.setStack(goods.getStack() - 1);
    new Thread(new Runnable() {
      @Override
      public void run() {
        GoodsEntity goods2 = goodsRepository.findOne("7da93cc3-2db8-4918-9465-6c53c13ff472");
        goods2.setStack(goods2.getStack() - 1);
        goods2 = goodsRepository.saveAndFlush(goods2);
        System.out.println("goods2=" + goods2.getStack() + "version=" + goods2.getVersion());
      }
    }).start();
    Thread.sleep(5000);
    goods = goodsRepository.saveAndFlush(goods);
    System.out.println("goods=" + goods.getStack() + "version=" + goods.getVersion());
    Thread.sleep(20000);
  }

  // @Test(threadPoolSize = 10, invocationCount = 5)
  @Test
  public void test2() {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("goodsId", "7da93cc3-2db8-4918-9465-6c53c13ff472");
    testRestTemplate.postForObject("http://localhost:" + port + "/goods/subStack", map,
        String.class);
  }
}
