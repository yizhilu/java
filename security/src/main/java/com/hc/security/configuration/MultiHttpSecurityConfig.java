package com.hc.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hc.security.service.security.UserSecurityDetailsService;
import com.hc.security.service.security.jwt.JwtAuthenticationEntryPoint;
import com.hc.security.service.security.jwt.JwtAuthenticationFilter;
import com.hc.security.service.security.jwt.JwtUserDetailsService;
import com.hc.security.service.security.jwt.JwtUsernamePasswordAuthenticationFilter;
import com.hc.security.service.security.jwt.utils.JwtTokenUtil;

@Configuration
public class MultiHttpSecurityConfig {

  /**
   * 用户密码的加密方式为MD5加密
   * 
   * @return
   */
  @Bean
  public Md5PasswordEncoder passwordEncoder() {
    return new Md5PasswordEncoder();

  }

  @Bean
  public BCryptPasswordEncoder bcryptPasswordEncoder() {
    return new BCryptPasswordEncoder();

  }

  /**
   * 
   * @author hc
   *
   */
  @Configuration
  @Order(1)
  public static class RestfulApiWebSecurityConfigurationAdapter
      extends WebSecurityConfigurerAdapter {

    /**
     * 忽略权限判断的url
     */
    @Value("${author.app.ignoreUrls}")
    private String[] appignoreUrls;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    // @Autowired
    // private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.header}")
    private String tokenHeader;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      http.csrf().disable()
          // 不需要session
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
          .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
          .addFilterBefore(
              new JwtAuthenticationFilter(userDetailsService, jwtTokenUtil, tokenHeader),
              JwtUsernamePasswordAuthenticationFilter.class)
          .addFilterAt(new JwtUsernamePasswordAuthenticationFilter(authenticationManagerBean(),
              jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)
          // http.antMatcher表明HttpSecurity这只适用于以。开头的网址/api/
          .antMatcher("/api/**")
          // http.authorizeRequests()方法有多个子节点，每个macher按照他们的声明顺序执行
          .authorizeRequests()
          // 忽略ignoreUrls的权限验证
          .antMatchers(appignoreUrls).permitAll().antMatchers("/api/**").authenticated();
    }
  }

  /**
   * 
   * @author hc
   *
   */
  @Configuration
  public static class CookieWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    /**
     * 忽略权限判断的url
     */
    @Value("${author.web.ignoreUrls}")
    private String[] ignoreUrls;
    @Autowired
    private Md5PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      // 允许所有用户访问"/"和"/home"等地址
      http.authorizeRequests()
          // 系统中的“登录页面”在被访问时，不进行权限控制
          .antMatchers(ignoreUrls).permitAll()
          // //尚未匹配的任何URL都要求用户进行身份验证
          .anyRequest().authenticated().and()
          // ==================== 设定登录页的url地址，它不进行权限控制
          .formLogin()
          // 由于后端提供的都是restful接口，并没有直接跳转的页面
          // 所以只要访问的url没有通过权限认证，就跳到这个请求上，并直接排除权限异常
          .loginPage("/web/security/loginFail")
          // 登录请求点
          .loginProcessingUrl("/web/login")
          // 登录失败，返回到这里
          .failureForwardUrl("/web/security/loginFail")
          // 登录成功后，默认到这个URL，返回登录成功后的信息
          .successForwardUrl("/web/security/loginSuccess").permitAll().and()
          // ===================== 设定登出后的url地址
          .logout()
          // 登出页面
          .logoutUrl("/web/security/logout")
          // 登录成功后
          .logoutSuccessUrl("/web/security/logoutSuccess").permitAll().and().csrf().disable()
          // .csrfTokenRepository(tokenRepository()).and()
          // ===================== 关闭csrf.csrf().disable()
          .rememberMe()
          // 持续化登录，登录时间为100天
          .tokenValiditySeconds(100 * 24 * 60 * 60).rememberMeCookieName("persistence")
          .alwaysRemember(true);
    }

    // @Autowired
    // public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    // auth.userDetailsService(operatorUserSecurityDetailsService())
    // // 设置密码加密模式
    // .passwordEncoder(passwordEncoder());
    // }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(daoAuthenticationProvider());
    }

    // @Bean
    // public CookieCsrfTokenRepository tokenRepository() {
    // CookieCsrfTokenRepository tokenRepository = new CookieCsrfTokenRepository();
    // tokenRepository.setCookieHttpOnly(false);
    // tokenRepository.setCookieName("X-XSRF-TOKEN");
    // tokenRepository.setHeaderName("X-XSRF-TOKEN");
    // return tokenRepository;
    // }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
      provider.setHideUserNotFoundExceptions(false);
      provider.setUserDetailsService(userDetailsService());
      provider.setPasswordEncoder(passwordEncoder);
      return provider;
    }



    /**
     * 自定义UserDetailsService，从数据库中读取用户信息
     * 
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService() {
      return new UserSecurityDetailsService();
    }
  }
}
