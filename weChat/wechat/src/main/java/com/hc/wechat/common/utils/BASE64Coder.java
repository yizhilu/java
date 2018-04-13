package com.hc.wechat.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * BASE64加密组件
 * 
 * @author 梁栋
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("restriction")
public abstract class BASE64Coder {

  /**
   * BASE64解密
   * 
   * @param key
   * @return
   * @throws Exception
   */
  public static byte[] decryptBASE64(String key) throws Exception {
    return (new BASE64Decoder()).decodeBuffer(key);
  }

  /**
   * BASE64加密
   * 
   * @param key
   * @return
   * @throws Exception
   */
  public static String encryptBASE64(byte[] key) throws Exception {
    String strKey = (new BASE64Encoder()).encodeBuffer(key);
    /**
     * 替换掉base64的回车换行符
     */
    strKey = strKey.replaceAll("\\p{Cntrl}]|\\p{Space}", "");
    return strKey;
  }
}
