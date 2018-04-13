package com.hc.wechat.common.utils;

import java.security.MessageDigest;



/**
 * SHA加密
 * 
 * @author liyong
 * @version 1.0
 */
public class SHACoder {
  public static final String ALGORITHM = "SHA";
  public static final String ALGORITHM256 = "SHA-256";

  /**
   * 初始化密钥
   * 
   * @return
   * @throws Exception
   */
  public static String initKey() throws Exception {
    return KeyFactory.initKey(KeyFactory.DEFAULTALGORITHM, null);
  }

  /**
   * 初始化密钥
   * 
   * @param seed 密钥种子
   * @return
   * @throws Exception
   */
  public static String initKey(String seed) throws Exception {
    return KeyFactory.initKey(KeyFactory.DEFAULTALGORITHM, seed);
  }

  /**
   * SHA加密
   * 
   * @param distStr 待加密串
   * @param key 加密密钥
   * @return
   * @throws Exception
   */
  public static byte[] encryptSha(byte[] distStr, String key) throws Exception {
    MessageDigest sha = MessageDigest.getInstance(ALGORITHM);
    byte[] newData;
    if (key == null || "".equals(key.trim()))
      newData = distStr;
    else
      newData = Utils.byteMerger(distStr, key.getBytes());
    sha.update(newData);
    return sha.digest();
  }

  /**
   * SHA256加密
   * 
   * @param distStr 待加密串
   * @param key 加密密钥
   * @return
   * @throws Exception
   */
  public static byte[] encryptSha256(byte[] distStr, String key) throws Exception {
    MessageDigest sha = MessageDigest.getInstance(ALGORITHM256);
    byte[] newData;
    if (key == null || "".equals(key.trim()))
      newData = distStr;
    else
      newData = Utils.byteMerger(distStr, key.getBytes());
    sha.update(newData);
    return sha.digest();
  }

  /**
   * 生成SHA1 16进制摘要
   * 
   * @param distStr 待加密串
   * @param key 加密密钥
   * @return 16进制摘要
   * @throws Exception
   */
  public static String encryptShaHex(String distStr, String key) throws Exception {
    if (distStr == null || "".equals(distStr.trim())) {
      return null;
    }
    byte[] b = encryptSha(distStr.getBytes(), key);
    return Utils.bytesToHexString(b);
  }

  /**
   * 生成SHA256 16进制摘要
   * 
   * @param distStr 待加密串
   * @param key 加密密钥
   * @return 16进制摘要
   * @throws Exception
   */
  public static String encryptSha256Hex(String distStr, String key) throws Exception {
    if (distStr == null || "".equals(distStr.trim())) {
      return null;
    }
    byte[] b = encryptSha256(distStr.getBytes(), key);
    return Utils.bytesToHexString(b);
  }

  /**
   * 生成SHA1 base64编码摘要
   * 
   * @param distStr 待加密串
   * @param key 加密密钥
   * @return base64编码摘要
   * @throws Exception
   */
  public static String encryptShaB64(String distStr, String key) throws Exception {
    if (distStr == null || "".equals(distStr.trim())) {
      return null;
    }
    byte[] b = encryptSha(distStr.getBytes(), key);
    return BASE64Coder.encryptBASE64(b);
  }

  /**
   * 生成SHA256 base64编码摘要
   * 
   * @param distStr 待加密串
   * @param key 加密密钥
   * @return base64编码摘要
   * @throws Exception
   */
  public static String encryptSha256B64(String distStr, String key) throws Exception {
    if (distStr == null || "".equals(distStr.trim())) {
      return null;
    }
    byte[] b = encryptSha256(distStr.getBytes(), key);
    return BASE64Coder.encryptBASE64(b);
  }
}
