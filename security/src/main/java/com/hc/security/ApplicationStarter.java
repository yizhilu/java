package com.hc.security;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.hc.crypto.annotation.EnableCryptoStarter;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

// 启动spring boot
@SpringBootApplication
// 启动swagger配置
@EnableSwagger2
// 启动基于http的spring Securit权限管理
@EnableWebSecurity
// 启动spring Securit基于方法的权限管理方式
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableAsync
@EnableCryptoStarter
public class ApplicationStarter {
  public static void main(String[] args) throws Exception {
    new SpringApplicationBuilder(ApplicationStarter.class).web(true).run(args);
  }
}
