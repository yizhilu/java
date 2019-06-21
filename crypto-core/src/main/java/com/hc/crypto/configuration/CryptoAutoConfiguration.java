package com.hc.crypto.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.hc.crypto.advice.DecryptRequestBodyAdvice;
import com.hc.crypto.advice.EncryptResponseBodyAdvice;


/**
 * 加解密自动配置
 * 
 * @author hc
 * 
 *
 */
@Configuration
@Component
@EnableAutoConfiguration
@EnableConfigurationProperties(CryptoProperties.class)
public class CryptoAutoConfiguration {

  /**
   * 配置ResponseBody请求加密
   * 
   * @return
   */
  @Bean
  public DecryptRequestBodyAdvice decryptRequestBodyAdvice() {
    return new DecryptRequestBodyAdvice();
  }

  /**
   * 配置RequestBody请求解密
   * 
   * @return
   */
  @Bean
  public EncryptResponseBodyAdvice encryptRequestBodyAdvice() {
    return new EncryptResponseBodyAdvice();
  }

}
