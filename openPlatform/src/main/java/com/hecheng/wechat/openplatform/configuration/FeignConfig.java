package com.hecheng.wechat.openplatform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Request;

/**
 * 独立的feign客户端的超时时间设置
 * 
 * @author yinwenjie
 */
@Configuration
public class FeignConfig {
  @Bean
  public Request.Options feignOptions() {
    return new Request.Options(4 * 5000, 2 * 5000);
  }
}
