package com.hecheng.wechat.openplatform.configuration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author 作者 hc
 * @version 创建时间：2017年10月18日 下午5:48:44 类说明
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(WebConfig.class);

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new ObjectConvertResolver());
  }

  @Bean
  public ApplicationListener<?> eventListener() {
    return new ApplicationListener<MqttConnectionFailedEvent>() {

      @Override
      public void onApplicationEvent(MqttConnectionFailedEvent event) {
        LOG.info(event.getCause().getMessage(), event.getCause());
        event.getCause().printStackTrace();
      }

    };
  }
}
