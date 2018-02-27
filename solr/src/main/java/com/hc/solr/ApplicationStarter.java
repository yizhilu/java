package com.hc.solr;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

// 启动spring boot
@SpringBootApplication
//启动swagger配置
@EnableSwagger2
public class ApplicationStarter {
  public static void main(String[] args) throws Exception {
    new SpringApplicationBuilder(ApplicationStarter.class).web(true).run(args);
  } 
}
