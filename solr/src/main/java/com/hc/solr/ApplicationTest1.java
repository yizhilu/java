package com.hc.solr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.hc.solr.entity.GoodsEntity;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes = ApplicationStarter.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTest1 {
  @LocalServerPort
  private int port;
  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void test1() {
    GoodsEntity goods = new GoodsEntity();
    goods.setName("test");
    goods.setStack(0);
    testRestTemplate.postForObject("http://localhost:" + port + "/goods/add", goods,
        GoodsEntity.class);
  }

  @Test
  public void test2() {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("goodsId", "7da93cc3-2db8-4918-9465-6c53c13ff472");
    map.add("stack", 2);
    testRestTemplate.postForObject("http://localhost:" + port + "/goods/updateStack", map,
        String.class);
  }
}
