package com.hc.security.common.utils.crypto.aesrsa;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <pre>
 * 对称加密算法: 
 * 较传统的加密体制，通信双方在加/解密过程中使用他们共享的单一密钥，鉴于其算法简单和加密速度快的优点，目前仍然是主流的密码体制之一。
 * 最常用的对称密码算法是数据加密标准（DES）算法，但是由于DES密钥长度较短，已经不适合当今分布式开放网络对数据加密安全性的要求。
 * 最后，一种新的基于Rijndael算法对称高级数据加密标准AES取代了数据加密标准DES。
 * 
 * ===================================================
 * 1，算法: DES, DESede, AES,...RC4各自密钥长度不同
 * DES          key密钥大小必须等于56 
 * DESede(TripleDES) key 密钥大小必须等于112或168 
 * AES          key 密钥大小必须等于128,192或256，但192和256位可能不可用 
 * Blowfish     key 密钥大小必须是8的倍数，并且只能是32到448（含）
 * RC2          key 密钥大小必须介于40和1024位之间
 * RC4(ARCFOUR) key 密钥大小必须介于40和1024位之间
 * 2.加密模式: ECB、CBC、CTR、OCF、CFB
 * ECB(电码本模式):     
 * 将整个明文分成若干段相同的小段，然后对每一小段进行加密.
 * (优：操作简单，易于实现；分组独立，易于并行；误差不会被传送。——简单，可并行，不传送误差,
 * 缺：掩盖不了明文结构信息，难以抵抗统计分析攻击。——可对明文进行主动攻击)
 * CBC(密码分组链模式):
 * 先将明文切分成若干小段，然后每一小段与初始块或者上一段的密文段进行异或运算后，再与密钥进行加密
 * (优点：能掩盖明文结构信息，保证相同密文可得不同明文，所以不容易主动攻击，安全性好于ECB，适合传输长度长的报文，是SSL和IPSec的标准。
 * 缺点：（1）不利于并行计算；（2）传递误差——前一个出错则后续全错；（3）第一个明文块需要与一个初始化向量IV进行抑或，初始化向量IV的选取比较复杂。)
 * CTR（计数器模式）：
 * 完全的流模式。将瞬时值与计数器连接起来，然后对此进行加密产生密钥流的一个密钥块，再进行XOR操作 。
 * (优点：不泄露明文；仅需实现加密函数；无需填充；可并行计算。
 * 缺点：需要瞬时值IV，难以保证IV的唯一性。)
 * 3，补码方式:  NoPadding，PKCS5Padding，ISO10126Padding
 * 参照：http://blog.sina.com.cn/s/blog_679daa6b0100zmpp.html
 * PKCS5Padding：填充的原则是，如果长度少于16个字节，需要补满16个字节，补(16-len)个(16-len)
 * 例如: 123这个节符串是3个字节，16-3= 13,补满后如：123+13个十进制的13，
 * ===================================================
 * </pre> 对称加密算法: 适用AES,DES
 *
 * @author hc
 * @date 2019/6/20
 */
public class AESCoder {

  /** 算法/模式/补码方式 */
  public static final String AES_ALGORITHM = "AES";
  // public static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
  // public static final String AES_ALGORITHM = "DESede/CBC/PKCS5Padding";
  public static final String CHAR_ENCODING = "UTF-8";

  /**
   * 加密
   *
   * @param data 需要加密的内容
   * @param key 加密密码
   * @return
   */
  public static byte[] encrypt(byte[] data, byte[] key) {
    notEmpty(data, "data");
    notEmpty(key, "key");
    // 判断Key是否为16位(CBC模式需要)
    if (key.length != 16) {
      throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
    }
    try {
      SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
      byte[] enCodeFormat = secretKey.getEncoded();
      SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
      // 创建密码器
      Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
      if (AES_ALGORITHM.contains("CBC")) {
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(key);
        // 初始化
        cipher.init(Cipher.ENCRYPT_MODE, seckey, iv);
      } else {
        // 初始化
        cipher.init(Cipher.ENCRYPT_MODE, seckey);
      }
      // 加密
      byte[] result = cipher.doFinal(data);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("encrypt fail!", e);
    }
  }

  /**
   * 解密
   *
   * @param data 待解密内容
   * @param key 解密密钥
   * @return
   */
  public static byte[] decrypt(byte[] data, byte[] key) {
    notEmpty(data, "data");
    notEmpty(key, "key");
    if (key.length != 16) {
      throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
    }
    try {
      SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
      byte[] enCodeFormat = secretKey.getEncoded();
      SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
      // 创建密码器
      Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
      if (AES_ALGORITHM.contains("CBC")) {
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec(key);
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, seckey, iv);
      } else {
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, seckey);
      }
      // 解密
      byte[] result = cipher.doFinal(data);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("decrypt fail!", e);
    }
  }

  /**
   * 加密并返回base64的编码
   *
   * @param data 需要加密的内容
   * @param key 加密密码
   * @return
   */
  public static String encryptToBase64(String data, String key) {
    try {
      byte[] valueByte = encrypt(data.getBytes(CHAR_ENCODING), key.getBytes(CHAR_ENCODING));
      return new String(Base64.getEncoder().encode(valueByte));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("encrypt fail!", e);
    }

  }

  /**
   * 从base64的编码解密并返回base64de编码
   *
   * @param data 需要加密的内容base64
   * @param key 加密密码
   * @return
   */
  public static String decryptFromBase64(String data, String key) {
    try {
      byte[] originalData = Base64.getDecoder().decode(data.getBytes());
      byte[] valueByte = decrypt(originalData, key.getBytes(CHAR_ENCODING));
      return new String(valueByte, CHAR_ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("decrypt fail!", e);
    }
  }

  /**
   * 加密并返回base64的编码
   * 
   * @param data
   * @param key base64
   * @return
   */
  public static String encryptWithKeyBase64(String data, String key) {
    try {
      byte[] valueByte =
          encrypt(data.getBytes(CHAR_ENCODING), Base64.getDecoder().decode(key.getBytes()));
      return new String(Base64.getEncoder().encode(valueByte));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("encrypt fail!", e);
    }
  }

  public static String decryptWithKeyBase64(String data, String key) {
    try {
      byte[] originalData = Base64.getDecoder().decode(data.getBytes());
      byte[] valueByte = decrypt(originalData, Base64.getDecoder().decode(key.getBytes()));
      return new String(valueByte, CHAR_ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("decrypt fail!", e);
    }
  }

  public static byte[] genarateRandomKey() {
    KeyGenerator keygen = null;
    try {
      keygen = KeyGenerator.getInstance("AES");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(" genarateRandomKey fail!", e);
    }
    SecureRandom random = new SecureRandom();
    keygen.init(random);
    Key key = keygen.generateKey();
    return key.getEncoded();
  }



  public static String genarateRandomKeyWithBase64() {
    return new String(Base64.getEncoder().encode(genarateRandomKey()));
  }


  /**
   * 验证对象是否为NULL,空字符串，空数组，空的Collection或Map(只有空格的字符串也认为是空串)
   * 
   * @param obj 被验证的对象
   * @param message 异常信息
   */
  @SuppressWarnings("rawtypes")
  public static void notEmpty(Object obj, String message) {
    if (obj == null) {
      throw new IllegalArgumentException(message + " must be specified");
    }
    if (obj instanceof String && obj.toString().trim().length() == 0) {
      throw new IllegalArgumentException(message + " must be specified");
    }
    if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
      throw new IllegalArgumentException(message + " must be specified");
    }
    if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
      throw new IllegalArgumentException(message + " must be specified");
    }
    if (obj instanceof Map && ((Map) obj).isEmpty()) {
      throw new IllegalArgumentException(message + " must be specified");
    }
  }

  public static void main(String[] args) {
    System.out.println(Arrays.toString(genarateRandomKey()));
  }
}
