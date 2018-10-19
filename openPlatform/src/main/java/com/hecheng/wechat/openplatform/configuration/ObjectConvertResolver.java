package com.hecheng.wechat.openplatform.configuration;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.hecheng.wechat.openplatform.annotation.ObjectConvertAnno;
import com.hecheng.wechat.openplatform.common.utils.CookieUtils;
import com.hecheng.wechat.openplatform.common.utils.SHACoder;
import com.hecheng.wechat.openplatform.entity.ThirdPartUserEntity;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.service.ThirdPartUserService;
import com.hecheng.wechat.openplatform.service.UserService;

/**
 * 把 ObjectConvertAnno注解标注的UserEntity或者ThirdPartUserEntity 转换为controller参数
 * 
 * @author 作者 hc
 * @version 创建时间：2017年10月18日 下午2:29:09 类说明
 */
@Configuration
public class ObjectConvertResolver
    implements
      HandlerMethodArgumentResolver,
      ApplicationContextAware {
  private static final Logger LOG = LoggerFactory.getLogger(ObjectConvertResolver.class);
  /**
   * cookie使用的分隔符
   */
  private static final String SPLIT = "&";
  /**
   * thirdPartUser会话cookie key
   */
  public static final String THIRD_PRINCIPALCOOKIE_NAME = "thirdPartUser_principal";
  /**
   * user会话cookie key
   */
  public static final String USER_PRINCIPALCOOKIE_NAME = "user_principal";
  private static boolean isReloaded = false;
  private static ApplicationContext applicationContext;

  private UserService userService;
  private ThirdPartUserService thirdPartUserService;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {

    return parameter.getParameterAnnotation(ObjectConvertAnno.class) != null;
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    Object[] paramAnnos = parameter.getParameterAnnotations();
    if (paramAnnos == null || paramAnnos.length == 0) {
      return new Object();
    }
    for (Object anno : paramAnnos) {
      if (!ObjectConvertAnno.class.isInstance(anno)) {
        continue;
      }
      ObjectConvertAnno objectConvertAnno = (ObjectConvertAnno) anno;
      Object object = execute(parameter, webRequest);
      if (objectConvertAnno.required() && object == null) {
        return null;
      }
      return object;
    }
    return new Object();
  }

  private Object execute(MethodParameter methodParameter, NativeWebRequest webRequest) {
    Class<?> paramType = methodParameter.getParameterType();
    if (paramType.equals(UserEntity.class)) {

      return getCurrentUser((HttpServletRequest) webRequest.getNativeRequest());
    } else if (paramType.equals(ThirdPartUserEntity.class)) {
      return getCurrentThirdPartUser((HttpServletRequest) webRequest.getNativeRequest());
    }
    return null;
  }

  /**
   * 获取当前用户
   * 
   * @return
   */
  private UserEntity getCurrentUser(HttpServletRequest request) {
    synchronized (ObjectConvertResolver.class) {
      while (!isReloaded) {
        try {
          ObjectConvertResolver.class.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // TODO 这里写业务
      userService = ObjectConvertResolver.applicationContext
          .getBean(com.hecheng.wechat.openplatform.service.UserService.class);
      return getWeChatCurrentUser(request);
    }
  }

  private UserEntity getWeChatCurrentUser(HttpServletRequest request) {
    UserEntity user = null;
    Cookie cookie = CookieUtils.getCookie(request, USER_PRINCIPALCOOKIE_NAME);
    if (cookie == null) {
      return null;
    }
    String cookieValue = cookie.getValue();
    try {
      cookieValue = URLDecoder.decode(cookieValue, "utf-8");
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
    }
    LOG.info("cookie=" + cookie.getName() + "=" + cookieValue);
    if (cookieValue == null) {
      return null;
    }
    String[] values = cookieValue.split(SPLIT);
    String id = values[0];
    LOG.info("userid:" + id);
    String cookieSHAValue = values[1];
    user = userService.findByIdFetch(id);
    if (user == null) {
      return null;
    }
    String summary = null;
    try {
      summary = SHACoder.encryptShaB64(user.getId(), user.getId());
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return null;
    }
    if (summary == null || !summary.equals(cookieSHAValue)) {
      return null;
    } else {
      return user;
    }
  }

  /**
   * 获取当前登陆的第三方用户
   * 
   * @return
   */
  private ThirdPartUserEntity getCurrentThirdPartUser(HttpServletRequest request) {
    synchronized (ObjectConvertResolver.class) {
      while (!isReloaded) {
        try {
          ObjectConvertResolver.class.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // TODO 这里写业务
      thirdPartUserService = ObjectConvertResolver.applicationContext
          .getBean(com.hecheng.wechat.openplatform.service.ThirdPartUserService.class);
      return getWeChatCurrentThirdPartUser(request);
    }
  }

  private ThirdPartUserEntity getWeChatCurrentThirdPartUser(HttpServletRequest request) {
    ThirdPartUserEntity user = null;
    Cookie cookie = CookieUtils.getCookie(request, THIRD_PRINCIPALCOOKIE_NAME);
    if (cookie == null) {
      return null;
    }
    String cookieValue = cookie.getValue();
    try {
      cookieValue = URLDecoder.decode(cookieValue, "utf-8");
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
    }
    if (cookieValue == null) {
      return null;
    }
    String[] values = cookieValue.split(SPLIT);
    String id = values[0];
    String cookieSHAValue = values[1];
    user = thirdPartUserService.findById(id);
    if (user == null) {
      return null;
    }
    String summary = null;
    try {
      summary = SHACoder.encryptShaB64(user.getId(), user.getId());
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return null;
    }
    if (summary == null || !summary.equals(cookieSHAValue)) {
      return null;
    } else {
      return user;
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    synchronized (ObjectConvertResolver.class) {
      ObjectConvertResolver.applicationContext = applicationContext;
      isReloaded = true;
      ObjectConvertResolver.class.notifyAll();
    }
  }
}
