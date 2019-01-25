package com.hc.solr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.alibaba.fastjson.JSONObject;
import com.hc.solr.controller.GoodsController;
import com.hc.solr.entity.GoodsEntity;
import com.hc.solr.service.GoodsService;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@WebMvcTest(GoodsController.class)
public class ApplicationTest2 {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private GoodsService goodsService;

  @Test
  public void test1() throws Exception {
    GoodsEntity goods = new GoodsEntity();
    goods.setName("test");
    goods.setStack(0);
   //    when(goodsService.add(goods)).thenReturn(null);
    mockMvc
        .perform(post("/goods/add").contentType(MediaType.APPLICATION_JSON)
            .content(JSONObject.toJSONString(goods)))
        .andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();;
  }
}
