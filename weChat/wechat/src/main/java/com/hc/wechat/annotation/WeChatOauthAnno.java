package com.hc.wechat.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 发起微信单点登录 接口,如果需要手机号,则跳转到绑定手机号页面 只对controller 返回值 为ModelAndView 的方法有效
 * 
 * @author hc
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WeChatOauthAnno {

  /**
   * 指定去微信单点登录scope域默认为snsapi_userinfo <pre>
   * 1、以snsapi_base为scope发起的网页授权，是用来获取进入页面的用户的openid的，并且是静默授权并自动跳转到回调页的。用户感知的就是直接进入了回调页（往往是业务页面）
   * 2、以snsapi_userinfo为scope发起的网页授权，是用来获取用户的基本信息的。但这种授权需要用户手动同意，并且由于用户同意过，所以无须关注，就可在授权后获取该用户的基本信息。
   * </pre>
   * 
   * @return
   */
  String scope() default "snsapi_userinfo";

  /**
   * 是否学要绑定手机号 与 bindPhoneUrl 配合使用
   * 
   * @return
   */
  boolean requiredPhone() default false;

  /**
   * 绑定手机号的接口地址
   * 
   * @return
   */
  String bindPhoneUrl() default "";
}
