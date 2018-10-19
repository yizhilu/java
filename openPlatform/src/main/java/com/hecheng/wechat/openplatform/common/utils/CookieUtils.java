
package com.hecheng.wechat.openplatform.common.utils;

import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hecheng.wechat.openplatform.entity.ThirdPartUserEntity;
import com.hecheng.wechat.openplatform.entity.UserEntity;



public class CookieUtils {
  private static final Logger LOG = LoggerFactory.getLogger(CookieUtils.class);
  public static final String VIEW_HISTORY_COOKIE_KEY = "client_view_history";
  public static final int COOKIE_AGE = 30 * 24 * 60 * 60;
  public static final String COOKIE_PATH = "/";
  public static final String WEB_SITE = "www.vanda.cn";
  public static final String WEB_DOMAIN = ".vanda.cn";
  /**
   * cookie使用的分隔符
   */
  private static final String SPLIT = "&";
  /**
   * user会话cookie key
   */
  public static final String USER_PRINCIPALCOOKIE_NAME = "user_principal";
  /**
   * thirdPartUser会话cookie key
   */
  public static final String THIRD_PRINCIPALCOOKIE_NAME = "thirdPartUser_principal";

  private CookieUtils() {}

  /**
   * 把第三方用户信息写入cookie
   * 
   * 
   * 
   * @param request
   * @param response
   * @param user
   */
  public static void weChatWriteThirdCookie(HttpServletRequest request,
      HttpServletResponse response, ThirdPartUserEntity user) {
    StringBuffer cookValue = new StringBuffer();
    String lastLoginTime =
        user.getLastLoginDate() == null ? null : user.getLastLoginDate().getTime() + "";
    try {
      cookValue.append(user.getId());
      cookValue.append(SPLIT);
      String summary = SHACoder.encryptShaB64(user.getId(), user.getId());
      cookValue.append(summary);
      cookValue.append(SPLIT);
      // 会话cookie保存最后登录时间，拦截器检查根据该时间检查1个账号只能1个终端登录
      cookValue.append(lastLoginTime);
      String cookieValue = URLEncoder.encode(cookValue.toString(), "utf-8");
      Cookie cookie = new Cookie(THIRD_PRINCIPALCOOKIE_NAME, cookieValue);
      cookie.setPath("/");
      CookieUtils.writeCookie(response, request, cookie);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }

  }

  /**
   * 把用户信息写入微信浏览器cookie
   * 
   * @param request
   * @param response
   * @param user
   */
  public static void weChatWriteUserCookie(HttpServletRequest request, HttpServletResponse response,
      UserEntity user) {
    StringBuffer cookValue = new StringBuffer();
    String lastLoginTime =
        user.getLastLoginDate() == null ? null : user.getLastLoginDate().getTime() + "";
    try {
      cookValue.append(user.getId());
      cookValue.append(SPLIT);
      String summary = SHACoder.encryptShaB64(user.getId(), user.getId());
      cookValue.append(summary);
      cookValue.append(SPLIT);
      // 会话cookie保存最后登录时间，拦截器检查根据该时间检查1个账号只能1个终端登录
      cookValue.append(lastLoginTime);
      String cookieValue = URLEncoder.encode(cookValue.toString(), "utf-8");
      Cookie cookie = new Cookie(USER_PRINCIPALCOOKIE_NAME, cookieValue);
      cookie.setPath("/");
      CookieUtils.writeCookie(response, request, cookie);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
  }

  /**
   * 得到当前request请求的所有cookie
   * 
   * 
   * @return cookie数组
   */
  public static Cookie[] getCookies(HttpServletRequest request) {

    return request == null ? null : request.getCookies();
  }

  /**
   * 获得cookie （request 由外部传入）
   * 
   * @param request
   * @param name
   * @return
   */
  public static Cookie getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    if (cookies != null && cookies.length > 0) {
      for (int i = 0; i < cookies.length; i++) {
        Cookie cookie = cookies[i];
        String cookName = cookie.getName();
        LOG.info("cookies name=：" + cookName);
        if (cookName != null && cookName.equals(name)) {
          return cookie;
        }
      }
    }
    return null;
  }

  /**
   * 将cookie写入客户端 (request,response 由外部传入)
   * 
   * @author fengyuan
   * @param cookie
   */
  public static void writeCookie(HttpServletResponse response, HttpServletRequest request,
      Cookie cookie) {
    if (cookie == null) return;
    if (request != null) {
      String host = request.getHeader("Host");
      if (WEB_SITE.equals(host)) cookie.setDomain(WEB_DOMAIN);
    }
    cookie.setPath(COOKIE_PATH);
    if (response != null) {
      response.addCookie(cookie);
    }
  }


  public static void removeCookie(HttpServletResponse response, HttpServletRequest request,
      String cookieName, String path) {
    if (request == null) return;
    Cookie[] cookies = request.getCookies();
    if (cookies == null || cookies.length == 0) return;
    for (int i = 0; i < cookies.length; i++) {
      Cookie cookie = cookies[i];
      if (cookie.getName().equals(cookieName)) {
        cookie.setMaxAge(0);
        cookie.setPath(path);
        if (request != null) {
          String host = request.getHeader("Host");
          if (WEB_SITE.equals(host)) cookie.setDomain(WEB_DOMAIN);
        }
        response.addCookie(cookie);
        break;
      }
    }

  }
}
