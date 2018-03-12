/*
 * @(#)StringLengthCount.java
 *
 * Copyright 2016 Vanda Inc. All rights reserved.
 */

/**
 * 
 */
package com.hc.proxyPool.common.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * description
 * 
 * @author shiliang
 * @version 1.0,2013-5-21
 */

public class StringMatchUtil {


  /**
   * 计算GBK编码的字符串长度
   * 
   * @param str
   * @return
   */
  public static int getGBKLength(String str) {
    try {
      byte[] b = str.getBytes("GBK");
      return b.length;
    } catch (Exception ex) {
      return 0;
    }
  }

  /**
   * 还原字符串中特殊字符
   * 
   * @param strData strData
   * @return 还原后的字符串
   */
  public static String decodeString(String strData) {
    if (strData == null) {
      return "";
    }
    return strData.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&apos;", "'")
        .replaceAll("&quot;", "\"").replaceAll("&amp;", "&");
  }

  /**
   * 判断是否在允许的长度范围内
   * 
   * @param str
   * @param min
   * @param max
   * @return
   */
  public static boolean isMatch(String str, int min, int max) {
    int strLength = getGBKLength(decodeString(str));
    if (strLength < min || strLength > max) {
      return false;
    }
    return true;

  }

  /**
   * 
   * @param type 1：账号，2：姓名，3：密码,4:邮箱
   * @param str 需要验证的串
   * @param min 最小长度
   * @param max 最大长度
   * @return
   */
  public static boolean regxMatch(int type, String str, int min, int max) {
    String regx = "";
    if (type == 1 || type == 3) {
      regx = "([A-Za-z0-9]{" + min + "," + max + "})";
    } else if (type == 2) {
      regx = "(([\u4E00-\u9FA5a-zA-Z]{" + min + "," + max + "}))";
    } else if (type == 4) {
      regx =
          "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    }

    return Pattern.matches(regx, str);
  }

  public static String splitSavePath(String filePath, String reg) {
    if (StringUtils.isEmpty(filePath) || StringUtils.isEmpty(reg)) {
      return "";
    }
    int lastIndex = filePath.indexOf(reg);
    return filePath.substring(lastIndex + reg.length());
  }

  /**
   * 创建随机数
   * 
   * @param length 随机数长度
   * @return
   */
  public static String generateRandom(int length) {
    String result = "";
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      result += random.nextInt(10);
    }
    return result;
  }

  /**
   * 替换字符串中的任何不可见字符，包括空格、制表符、换页符、换行符等等
   * 
   * @param str
   * @return
   */
  public static String replaceWrap(String str) {
    String dest = "";
    if (str != null) {
      Pattern p = Pattern.compile("\\s*|\t|\r|\n");
      Matcher m = p.matcher(str);
      dest = m.replaceAll("");
    }
    return dest;
  }

  /**
   * 替换字符串中的中英文括号
   * 
   * @param str
   * @return
   */
  public static String replaceBrackets(String str) {
    String dest = "";
    if (str != null) {
      Pattern p = Pattern.compile("\\(|\\)|（|）");
      Matcher m = p.matcher(str);
      dest = m.replaceAll("");
    }
    return dest;
  }
}

