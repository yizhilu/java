package com.hc.wechat.common.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.vdurmont.emoji.EmojiParser;

/**
 * 去掉emoji 表情符
 * 
 * @author liyong
 *
 */
public class EmojiFilterUtils {
  /**
   * 检测是否有emoji字符
   * 
   * @param source
   * @return 一旦含有就抛出
   */
  public static boolean containsEmoji(String source) {
    if (StringUtils.isBlank(source)) {
      return false;
    }

    int len = source.length();

    for (int i = 0; i < len; i++) {
      char codePoint = source.charAt(i);

      if (isEmojiCharacter(codePoint)) {
        // do nothing，判断到了这里表明，确认有表情字符
        return true;
      }
    }

    return false;
  }

  private static boolean isEmojiCharacter(char codePoint) {
    return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
        || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
        || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
        || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
  }

  /**
   * 过滤emoji 或者 其他非文字类型的字符
   * 
   * @param source
   * @return
   */
  public static String filterEmoji(String source) {

    if (!containsEmoji(source)) {
      return source;// 如果不包含，直接返回
    }
    // 到这里铁定包含
    StringBuilder buf = null;

    int len = source.length();

    for (int i = 0; i < len; i++) {
      char codePoint = source.charAt(i);

      if (isEmojiCharacter(codePoint)) {
        if (buf == null) {
          buf = new StringBuilder(source.length());
        }

        buf.append(codePoint);
      } else {}
    }

    if (buf == null) {
      return source;// 如果没有找到 emoji表情，则返回源字符串
    } else {
      if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
        buf = null;
        return source;
      } else {
        return buf.toString();
      }
    }

  }

  public static String filterEmojiTwo(String source) {
    if (source != null) {
      Pattern emoji =
          Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
              Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
      Matcher emojiMatcher = emoji.matcher(source);
      if (emojiMatcher.find()) {
        source = emojiMatcher.replaceAll("?");
        return source;
      }
      return source;
    }
    return source;
  }
  
  /**
   * 过滤emoji表情
   * @param source
   * @return
   */
  public static String filterEmojiThree(String source){
    if (source != null) {
      List<String> emojis = EmojiParser.extractEmojis(source);
      for (String string : emojis) {
        source = source.replace(string, "?");
      }
    }
    return source;
  }

//  //测试emojiJAR包的main
  // public static void main(String[] args) {
//    String aaa = "这是一个测试🤓🤓🤓";
//    //EmojiParser.extractEmojis是获取一串字符串里所有表情
//    //EmojiParser.parseToAliases是将一串字符串里的表情转换为别名
//    //EmojiParser.parseToHtmlHexadecimal师匠一串字符串里的表情转换为unicode编码
//    //EmojiParser.parseToUnicode是根据别名和unicode编码转换为表情
////    List<String> emojis = EmojiParser.extractEmojis(aaa);
////    for (String string : emojis) {
////      System.out.println(aaa.replace(string, "?"));
////    }
//    String emojiToHtml = EmojiParser.parseToHtmlHexadecimal(aaa);//这是一个测试&#x1f913;&#x1f913;&#x1f913;
////    String emojiToHtml = EmojiParser.parseToAliases(aaa);//这是一个测试:nerd::nerd::nerd:
//    System.out.println(emojiToHtml);
//    String bbb = EmojiParser.parseToUnicode(emojiToHtml);//这是一个测试🤓🤓🤓
//    System.out.println(bbb);
//  }
//  public static void main(String[] args) {
//    String str = "\\xF0\\x9F\\x92\\x8B";
//    String nstr = EmojiFilterUtils.filterEmoji(str);
//    System.out.println(nstr);
//    String nstr2 = EmojiFilterUtils.filterEmojiTwo(str);
//    System.out.println(nstr2);
//  }
}
