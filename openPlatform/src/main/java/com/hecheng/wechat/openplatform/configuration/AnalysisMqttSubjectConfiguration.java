package com.hecheng.wechat.openplatform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hecheng.wechat.openplatform.service.observer.AnalysisMqttDataSubject;


@Configuration
public class AnalysisMqttSubjectConfiguration {

  @Bean
  public AnalysisMqttDataSubject getAnalysisMqttSubject() {
    AnalysisMqttDataSubject subject = new AnalysisMqttDataSubject();
    // subject.addObserver(xxxxx);
    return subject;

  }
}
