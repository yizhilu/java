package com.hc.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.hc.security.service.security.OperatorUserSecurityDetailsService;
import com.hc.security.service.security.UserSecurityDetailsService;

@Configuration
public class MultiHttpSecurityConfig {
  /**
   * 和访问权限有关的配置信息在这里
   * 
   * @author hc
   */
  @Configuration
  @Order(1)
  public static class OperatorSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 忽略权限判断的url
     */
    @Value("${author.ignoreUrls}")
    private String[] ignoreUrls;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/operator/**")
      .formLogin()
      // 由于后端提供的都是restful接口，并没有直接跳转的页面
      // 所以只要访问的url没有通过权限认证，就跳到这个请求上，并直接排除权限异常
      .loginPage("/v1/security/loginFail")
      // 登录请求点
      .loginProcessingUrl("/operator/login")
      // 登录失败，返回到这里
      .failureForwardUrl("/v1/security/operator/loginFail")
      // 登录成功后，默认到这个URL，返回登录成功后的信息
      .successForwardUrl("/v1/security/operator/loginSuccess").permitAll().and()
      // ===================== 设定登出后的url地址
      .logout()
      // 登出页面
      .logoutUrl("/v1/security/operator/logout")
      // 登录成功后
      .logoutSuccessUrl("/v1/security/operator/logoutSuccess").permitAll().and()
      .authorizeRequests()
      // 系统中的“登录页面”在被访问时，不进行权限控制
      .antMatchers(ignoreUrls).permitAll()
      // 其它访问都要验证权限
      .anyRequest().authenticated().and()
      // ===================== 关闭csrf
      .csrf().disable().rememberMe()
      // 持续化登录，登录时间为100天
      .tokenValiditySeconds(100 * 24 * 60 * 60).rememberMeCookieName("persistence")
      .alwaysRemember(true);
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(operatorUserSecurityDetailsService())
          // 设置密码加密模式
          .passwordEncoder(passwordEncoder());
    }

    /**
     * 用户密码的加密方式为MD5加密
     * 
     * @return
     */
    @Bean
    public Md5PasswordEncoder passwordEncoder() {
      return new Md5PasswordEncoder();

    }

    /**
     * 自定义UserDetailsService，从数据库中读取用户信息
     * 
     * @return
     */
    @Bean
    public OperatorUserSecurityDetailsService operatorUserSecurityDetailsService() {
      return new OperatorUserSecurityDetailsService();
    }
  }
  /**
   * 和访问权限有关的配置信息在这里
   * 
   * @author hc
   */
  @Configuration
  @Order(2)
  public static class UserSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 忽略权限判断的url
     */
    @Value("${author.ignoreUrls}")
    private String[] ignoreUrls;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
      // 允许所有用户访问"/"和"/home"等地址
      http.antMatcher("/user/**")
          .formLogin()
          // 由于后端提供的都是restful接口，并没有直接跳转的页面
          // 所以只要访问的url没有通过权限认证，就跳到这个请求上，并直接排除权限异常
          .loginPage("/v1/security/loginFail")
          // 登录请求点
          .loginProcessingUrl("/user/login")
          // 登录失败，返回到这里
          .failureForwardUrl("/v1/security/user/loginFail")
          // 登录成功后，默认到这个URL，返回登录成功后的信息
          .successForwardUrl("/v1/security/user/loginSuccess").permitAll().and()
          // ===================== 设定登出后的url地址
          .logout()
          // 登出页面
          .logoutUrl("/v1/security/user/logout")
          // 登录成功后
          .logoutSuccessUrl("/v1/security/user/logoutSuccess").permitAll().and()
          .authorizeRequests()
          // 系统中的“登录页面”在被访问时，不进行权限控制
          .antMatchers(ignoreUrls).permitAll()
          // 其它访问都要验证权限
          .anyRequest().authenticated().and()
          // ===================== 关闭csrf
          .csrf().disable().rememberMe()
          // 持续化登录，登录时间为100天
          .tokenValiditySeconds(100 * 24 * 60 * 60).rememberMeCookieName("persistence")
          .alwaysRemember(true);
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userSecurityDetailsService())
          // 设置密码加密模式
          .passwordEncoder(passwordEncoder());
    }

    /**
     * 用户密码的加密方式为MD5加密
     * 
     * @return
     */
    @Bean
    public Md5PasswordEncoder passwordEncoder() {
      return new Md5PasswordEncoder();

    }

    /**
     * 自定义UserDetailsService，从数据库中读取用户信息
     * 
     * @return
     */
    @Bean
    public UserSecurityDetailsService userSecurityDetailsService() {
      return new UserSecurityDetailsService();
    }
  }
}
