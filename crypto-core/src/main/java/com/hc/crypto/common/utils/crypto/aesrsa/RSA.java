package com.hc.crypto.common.utils.crypto.aesrsa;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA算法
 *
 * @author hc
 * @date 2019/6/20
 */
public class RSA {

  /**
   * CBC模式要求加密字符串长度为16倍数
   */
  public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
  /**
   * 字符编码
   */
  public static final String CHAR_ENCODING = "UTF-8";

  /**
   * RSA生成密钥长度
   */
  private static int KEYSIZE = 2048;

  /**
   * RSA 签名算法
   */
  private static String SIGNATURE_ALGORITHM = "SHA256WithRSA";

  /**
   * 生成密钥对
   */
  public static Map<String, String> generateKeyPair() throws Exception {
    /** RSA算法要求有一个可信任的随机数源 */
    SecureRandom sr = new SecureRandom();
    /** 为RSA算法创建一个KeyPairGenerator对象 */
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
    kpg.initialize(KEYSIZE, sr);
    /** 生成密匙对 */
    KeyPair kp = kpg.generateKeyPair();
    /** 得到公钥 */
    Key publicKey = kp.getPublic();
    byte[] publicKeyBytes = publicKey.getEncoded();
    String pub = new String(Base64.getEncoder().encode(publicKeyBytes), CHAR_ENCODING);
    /** 得到私钥 */
    Key privateKey = kp.getPrivate();
    byte[] privateKeyBytes = privateKey.getEncoded();
    String pri = new String(Base64.getEncoder().encode(privateKeyBytes), CHAR_ENCODING);

    Map<String, String> map = new HashMap<String, String>();
    map.put("publicKey", pub);
    map.put("privateKey", pri);
    RSAPublicKey rsp = (RSAPublicKey) kp.getPublic();
    BigInteger bint = rsp.getModulus();
    byte[] b = bint.toByteArray();
    byte[] deBase64Value = Base64.getEncoder().encode(b);
    String retValue = new String(deBase64Value);
    map.put("modulus", retValue);
    return map;
  }

  /**
   * RSA加密
   *
   * @param source 源数据
   * @param publicKey server公钥
   * @return 加密数据
   * @throws Exception 异常
   */
  public static String encrypt(String source, String publicKey) throws Exception {
    Key key = getPublicKey(publicKey);
    /** 得到Cipher对象来实现对源数据的RSA加密 */
    Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] b = source.getBytes();
    /** 执行加密操作 */
    byte[] b1 = cipher.doFinal(b);
    return new String(Base64.getEncoder().encode(b1), CHAR_ENCODING);
  }

  /**
   * RSA解密算法
   *
   * @param cryptograph 密文
   * @param privateKey server私钥
   * @return 明文
   * @throws Exception 异常
   */
  public static String decrypt(String cryptograph, String privateKey) throws Exception {
    Key key = getPrivateKey(privateKey);
    /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
    Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] b1 = Base64.getDecoder().decode(cryptograph.getBytes());
    /** 执行解密操作 */
    byte[] b = cipher.doFinal(b1);
    return new String(b);
  }

  /**
   * 得到公钥
   *
   * @param key 密钥字符串（经过base64编码）
   * @throws Exception 异常
   */
  public static PublicKey getPublicKey(String key) throws Exception {
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PublicKey publicKey = keyFactory.generatePublic(keySpec);
    return publicKey;
  }

  /**
   * 得到私钥
   *
   * @param key 密钥字符串（经过base64编码）
   * @throws Exception 异常
   */
  public static PrivateKey getPrivateKey(String key) throws Exception {
    PKCS8EncodedKeySpec keySpec =
        new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key.getBytes()));
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
    return privateKey;
  }

  /**
   * RSA 签名
   * 
   * @param content 传递内容
   * @param privateKey client私钥
   * @return 数字签名
   */
  public static String sign(String content, String privateKey) {
    String charset = CHAR_ENCODING;
    try {
      PKCS8EncodedKeySpec priPKCS8 =
          new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey.getBytes()));
      KeyFactory keyf = KeyFactory.getInstance("RSA");
      PrivateKey priKey = keyf.generatePrivate(priPKCS8);

      Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

      signature.initSign(priKey);
      signature.update(content.getBytes(charset));

      byte[] signed = signature.sign();

      return new String(Base64.getEncoder().encode(signed));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 验证签名
   *
   * @param content client传递的签名数据
   * @param sign client传递的数字签名
   * @param publicKey server公钥
   * @return
   */
  public static boolean checkSign(String content, String sign, String publicKey) {
    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      byte[] encodedKey = Base64.getDecoder().decode(publicKey);
      PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

      java.security.Signature signature = java.security.Signature.getInstance(SIGNATURE_ALGORITHM);

      signature.initVerify(pubKey);
      signature.update(content.getBytes("utf-8"));

      boolean bverify = signature.verify(Base64.getDecoder().decode(sign));
      return bverify;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public static void main(String[] args) throws Exception {
    Map<String, String> map = generateKeyPair();
    System.out.println("publicKey: " + map.get("publicKey"));
    System.out.println("privateKey: " + map.get("privateKey"));
  }
}
