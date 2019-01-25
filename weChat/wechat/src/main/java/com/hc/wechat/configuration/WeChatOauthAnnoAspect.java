package com.hc.wechat.configuration;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.hc.wechat.annotation.WeChatOauthAnno;
import com.hc.wechat.common.utils.CookieUtils;
import com.hc.wechat.entity.ThirdPartUserEntity;
import com.hc.wechat.entity.UserEntity;
import com.hc.wechat.service.WeChatService;

/**
 * 
 * @author hc
 *
 */
@Aspect
@Component
public class WeChatOauthAnnoAspect {

  private static final Logger logger = LoggerFactory.getLogger(WeChatOauthAnnoAspect.class);
  /** 微信appid */
  @Value("${weChat.appId}")
  private String WECHAT_APPID;
  /** 微信appsecret */
  @Value("${weChat.secret}")
  private String WECHAT_SECRET;
  @Autowired
  private WeChatService weChatService;

  @Pointcut(value = "@annotation(com.vanda.tqrs.annotation.WeChatOauthAnno)") // 自定义的注解作为切点
  public void weChatOauthAnno() {}

  @Around(value = "weChatOauthAnno()") // around注解可以在 目标方法 之前执行 也可以在目标方法之后
  public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
    logger.info("微信Oauth登录方法名:" + pjp.getSignature().getName());
    try {
      // 1.检查weChatOauthAnno注解方法的 @ObjectConvertAnno UserEntity user 是否存在
      MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
      Method targetMethod = methodSignature.getMethod();
      Method realMethod = pjp.getTarget().getClass().getDeclaredMethod(pjp.getSignature().getName(),
          targetMethod.getParameterTypes());
      if (realMethod.isAnnotationPresent(WeChatOauthAnno.class)) {
        WeChatOauthAnno weChatOauthAnno =
            (WeChatOauthAnno) realMethod.getAnnotation(WeChatOauthAnno.class);
        Object[] args = pjp.getArgs();
        Class<?>[] argsTypes = realMethod.getParameterTypes();
        ModelAndView model = checkOauth(args, argsTypes, weChatOauthAnno);
        if (model != null) {
          return model;
        }
        return pjp.proceed(args);
      }
      Object obj = pjp.proceed();// 调用执行目标方法
      return obj;
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 检查
   * 
   * @ObjectConvertAnno UserEntity user 是否存在
   * @param args
   * @param argsTypes
   * @param weChatOauthAnno
   * @throws UnsupportedEncodingException
   */
  private ModelAndView checkOauth(Object[] args, Class<?>[] argsTypes,
      WeChatOauthAnno weChatOauthAnno) throws UnsupportedEncodingException {
    UserEntity currentUser = null;
    HttpServletRequest request = null;
    HttpServletResponse response = null;
    int userIndex = -1;
    // int requestIndex = -1;
    // int responseIndex = -1;
    for (int i = 0; i < argsTypes.length; i++) {
      if (argsTypes[i].getName().equals(UserEntity.class.getName())) {
        userIndex = i;
      }
    }
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof UserEntity) {
        currentUser = (UserEntity) args[i];
        userIndex = i;
      }
      if (args[i] instanceof HttpServletRequest) {
        request = (HttpServletRequest) args[i];
        // requestIndex = i;
      }
      if (args[i] instanceof HttpServletResponse) {
        response = (HttpServletResponse) args[i];
        // responseIndex = i;
      }
    }
    if (currentUser == null && request != null && response != null) {
      String code = request.getParameter("code");
      if (StringUtils.isBlank(code)) {
        // 当前链接
        String currentUrl = request.getRequestURL() + "";
        if (StringUtils.isNotBlank(request.getQueryString())) {
          currentUrl += "?" + request.getQueryString();
        }
        String currentUrlEncode = URLEncoder.encode(currentUrl, "utf-8");
        StringBuffer sb =
            new StringBuffer("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s");
        sb.append("&redirect_uri=%s");
        sb.append("&response_type=code&scope=%s&state=1#wechat_redirect");
        String redirect =
            String.format(sb.toString(), WECHAT_APPID, currentUrlEncode, weChatOauthAnno.scope());
        return new ModelAndView("redirect:" + redirect);
      } else {
        currentUser = wxSso(request, response, code);
        if (weChatOauthAnno.requiredPhone()
            && StringUtils.isNotBlank(weChatOauthAnno.bindPhoneUrl())
            && StringUtils.isBlank(currentUser.getTelephone())) {
          return new ModelAndView("redirect:" + weChatOauthAnno.bindPhoneUrl());
        }
        if (userIndex != -1) {
          args[userIndex] = currentUser;
        }
      }
    }
    return null;
  }

  private UserEntity wxSso(HttpServletRequest request, HttpServletResponse response, String code) {
    // 1.获取网页授权 openid和access_token
    logger.info("====wxSso====获取网页授权 openid和access_token==code:" + code);
    Map<String, Object> tokenMap =
        weChatService.getWebPageAccessToken(WECHAT_APPID, WECHAT_SECRET, code);
    logger.info("====wxSso====token:" + tokenMap);
    String openId = (String) tokenMap.get("openid");
    String access_token = (String) tokenMap.get("access_token");
    ThirdPartUserEntity thirdPartUser = null;
    thirdPartUser = weChatService.getThirdPartUserEntity(access_token, openId);
    UserEntity currentUser = (UserEntity) thirdPartUser.getUser();
    if (currentUser != null) {
      // ThirdPartUser对象写入会话cookies
      CookieUtils.weChatWriteThirdCookie(request, response, thirdPartUser);
      // user对象写入会话cookies
      CookieUtils.weChatWriteUserCookie(request, response, currentUser);
    }
    return currentUser;
  }
}

