package com.hecheng.wechat.openplatform;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

// 启动spring boot
@SpringBootApplication
@EnableFeignClients
// 启动swagger配置
@EnableSwagger2
// 启动基于http的spring Securit权限管理
@EnableWebSecurity
// 启动spring Securit基于方法的权限管理方式
@EnableGlobalMethodSecurity(prePostEnabled = true)
// 启动spring session
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 5)
public class ApplicationStarter {
  public static void main(String[] args) throws Exception {

    new SpringApplicationBuilder(ApplicationStarter.class).web(true).run(args);
  }
}
