package com.hc.wechat.common.utils;

public class Utils {
  /**
   * 合并两个数组
   * 
   * @param byte_1 数组1
   * @param byte_2 数组2
   * @return
   */
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
    byte[] byte_3 = new byte[byte_1.length + byte_2.length];
    System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
    return byte_3;
  }

  /**
   * 把长整形转成byte，8个字节
   * 
   * @param args
   */
  public static byte[] longToByteBuf(long l) {
    byte[] targets = new byte[8];
    for (int i = 0; i < 8; i++) {
      int offset = (targets.length - 1 - i) * 8;
      targets[i] = (byte) ((l >>> offset) & 0xff);
    }
    return targets;
  }

  /**
   * 把整形转成byte，4个字节
   * 
   * @param args
   */
  public static byte[] intToByteBuf(int i) {
    byte[] result = new byte[4];
    result[0] = (byte) ((i >> 24) & 0xFF);
    result[1] = (byte) ((i >> 16) & 0xFF);
    result[2] = (byte) ((i >> 8) & 0xFF);
    result[3] = (byte) (i & 0xFF);
    return result;
  }

  /**
   * 把短整形转成byte，2个字节
   * 
   * @param args
   */
  public static byte[] shortToByte(short i) {
    byte[] result = new byte[2];
    result[0] = (byte) ((i >> 8) & 0xFF);
    result[1] = (byte) (i & 0xFF);
    return result;
  }

  /**
   * 把布尔转成byte，1个字节
   * 
   * @param args
   */
  public static byte[] booleanToByte(boolean f) {
    byte[] result = new byte[1];
    result[0] = (byte) (f ? 1 : 0 & 0xFF);
    return result;
  }

  /**
   * 将byte数组转换成16进制字符串
   * 
   * @param b byte数组
   * @return hex string
   */
  public static String bytesToHexString(byte[] b) {
    StringBuilder stringBuilder = new StringBuilder("");
    if (b == null || b.length <= 0) {
      return null;
    }
    for (int i = 0; i < b.length; i++) {
      int v = b[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hv);
    }
    return stringBuilder.toString();
  }

  /**
   * 将16进制字符串转换成byte数组数组
   * 
   * @param hexString 16进制字符串
   * @return byte[]
   */
  public static byte[] hexStringToBytes(String hexString) {
    if (hexString == null || hexString.equals("")) {
      return null;
    }
    hexString = hexString.toUpperCase();
    int length = hexString.length() / 2;
    char[] hexChars = hexString.toCharArray();
    byte[] d = new byte[length];
    for (int i = 0; i < length; i++) {
      int pos = i * 2;
      d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
    }
    return d;
  }

  private static byte charToByte(char c) {
    return (byte) "0123456789ABCDEF".indexOf(c);
  }
}
