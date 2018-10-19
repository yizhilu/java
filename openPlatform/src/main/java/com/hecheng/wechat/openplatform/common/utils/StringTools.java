package com.hecheng.wechat.openplatform.common.utils;

import java.math.BigDecimal;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class StringTools {
  /**
   * 返回单个字符串，若匹配到多个的话就返回第一个
   * 
   * @param source
   * @param regex
   * @return
   */
  public static String getFirstMatches(String source, String regex) {
    Pattern pattern = Pattern.compile(regex);// 匹配的模式
    Matcher m = pattern.matcher(source);
    while (m.find()) {
      return m.group(1);
    }
    return "";
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

  /**
   * 将指定的target十六进制字符串 转化为十进制整数（不能转化0x11这类型的）
   * 
   * @param target
   * @return 十进制
   */
  public static int hexToInt(String target) {
    Validate.notBlank(target, "target不能为空");
    Validate.matchesPattern(target, "^[0-9a-fA-F]+$", "target只能是16进制的字符串");
    int x = Integer.parseInt(target, 16);
    return x;
  }

  /**
   * 将指定的target十六进制字符串 的[beginIndex,endIndex) 指定区间字符 转化为十进制 整数（不能转化0x11这类型的）
   * 
   * @param target
   * @param beginIndex 开始(包含,从0开始)
   * @param endIndex 结束（不包含）
   * @return
   */
  public static int hexToInt(String target, int beginIndex, int endIndex) {
    Validate.notBlank(target, "target不能为空");
    Validate.matchesPattern(target, "^[0-9a-fA-F]+$", "target只能是16进制的字符串");
    target = target.substring(beginIndex, endIndex);
    int x = Integer.parseInt(target, 16);
    return x;
  }

  /**
   * 将指定的target十六进制字符串 指定区间字符 转化为BigDecimal（不能转化0x11这类型的）
   * 
   * @param target
   * @param integerBitBeginIndex 整数位开始下标 （从0开始）
   * @param integerBitEndIndex 整数位结束下标（不包含）
   * @param decimalBitBeginIndex 小数位开始下标 （从0开始）如果decimalBitBeginIndex/decimalBitEndIndex其中一个为-1
   *        那么小数位为0
   * @param decimalBitEndIndex 小数位结束下标（不包含）
   * @param scale 小数位
   * @return
   */
  public static BigDecimal hexToBigDecimal(String target, int integerBitBeginIndex,
      int integerBitEndIndex, int decimalBitBeginIndex, int decimalBitEndIndex, int scale) {
    Validate.notBlank(target, "target不能为空");
    // Validate.matchesPattern(target, "^[0-9a-fA-F]+$", "target只能是16进制的字符串");
    String integerBitStr = target.substring(integerBitBeginIndex, integerBitEndIndex);
    int integerBit = Integer.parseInt(integerBitStr, 16);
    BigDecimal integerBitDec = new BigDecimal(integerBit).setScale(scale);
    if (decimalBitBeginIndex == -1 || decimalBitEndIndex == -1) {
      return integerBitDec;
    }
    String decimalBitStr = target.substring(decimalBitBeginIndex, decimalBitEndIndex);
    int decimalBit = Integer.parseInt(decimalBitStr, 16);
    BigDecimal decimalBitDec =
        new BigDecimal(decimalBit).setScale(scale).divide(new BigDecimal(Math.pow(10, scale)));
    return integerBitDec.add(decimalBitDec);
  }
}
