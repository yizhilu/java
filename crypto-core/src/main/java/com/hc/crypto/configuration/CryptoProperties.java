package com.hc.crypto.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.crypto")
public class CryptoProperties {
  /**
   * 固定AES密钥
   */
  private String accesskey;
  /**
   * RSA服务器私钥
   */
  private String serverPrivateKey;
  /**
   * RSA服务器公钥
   */
  private String serverPublicKey;
  /**
   * RSA客户端公钥
   */
  private String clientPublicKey;
  /**
   * 开启调试模式，调试模式下不进行加解密操作
   */
  private boolean debug = false;
  /**
   * 忽略需要加密的接口（使@ApiEncryptAnno失效）
   */
  private List<String> apiEncryptUriIgnoreList = new ArrayList<>();
  /**
   * 忽略需要解密的接口（使@ApiDecryptAnno失效）
   */
  private List<String> apiDecyptUriIgnoreList = new ArrayList<>();
  /**
   * TODO 签名过期时间（分钟）未实现
   */
  private Long signExpireTime = 10L;

  public String getAccesskey() {
    return accesskey;
  }

  public void setAccesskey(String accesskey) {
    this.accesskey = accesskey;
  }

  public String getServerPrivateKey() {
    return serverPrivateKey;
  }

  public void setServerPrivateKey(String serverPrivateKey) {
    this.serverPrivateKey = serverPrivateKey;
  }

  public String getServerPublicKey() {
    return serverPublicKey;
  }

  public void setServerPublicKey(String serverPublicKey) {
    this.serverPublicKey = serverPublicKey;
  }

  public String getClientPublicKey() {
    return clientPublicKey;
  }

  public void setClientPublicKey(String clientPublicKey) {
    this.clientPublicKey = clientPublicKey;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public Long getSignExpireTime() {
    return signExpireTime;
  }

  public void setSignExpireTime(Long signExpireTime) {
    this.signExpireTime = signExpireTime;
  }

  public List<String> getApiEncryptUriIgnoreList() {
    return apiEncryptUriIgnoreList;
  }

  public void setApiEncryptUriIgnoreList(List<String> apiEncryptUriIgnoreList) {
    this.apiEncryptUriIgnoreList = apiEncryptUriIgnoreList;
  }

  public List<String> getApiDecyptUriIgnoreList() {
    return apiDecyptUriIgnoreList;
  }

  public void setApiDecyptUriIgnoreList(List<String> apiDecyptUriIgnoreList) {
    this.apiDecyptUriIgnoreList = apiDecyptUriIgnoreList;
  }
}
