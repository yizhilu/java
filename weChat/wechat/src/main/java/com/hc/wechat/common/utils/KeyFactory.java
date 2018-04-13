package com.hc.wechat.common.utils;

/**
 * 密钥种子工厂类
 */
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyFactory extends BASE64Coder {
  /**
   * MAC算法可选以下多种算法
   * 
   * <pre>
   * AES
   * DES
   * HmacMD5 
   * HmacSHA1 
   * HmacSHA256 
   * HmacSHA384 
   * HmacSHA512
   * </pre>
   */
  public static final String DEFAULTALGORITHM = "HmacMD5";

  /**
   * 初始化密钥
   * 
   * @param seed 密钥种子
   * @return
   * @throws Exception
   */
  public static String initKey(String algorithm, String seed) throws Exception {
    SecureRandom secureRandom = null;
    if (seed != null) {
      secureRandom = new SecureRandom(decryptBASE64(seed));
    } else {
      secureRandom = new SecureRandom();
    }
    KeyGenerator kg = KeyGenerator.getInstance(algorithm);
    kg.init(secureRandom);
    SecretKey secretKey = kg.generateKey();
    return encryptBASE64(secretKey.getEncoded());
  }
}
