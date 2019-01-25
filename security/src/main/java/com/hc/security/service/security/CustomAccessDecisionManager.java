package com.hc.security.service.security;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

/**
 * AccessDecisionManager 进行最终的访问控制（授权）决定。
 * 
 * @author Administrator
 *
 */
@Service("CustomAccessDecisionManager")
public class CustomAccessDecisionManager implements AccessDecisionManager {

  /**
   * 忽略权限判断的url
   */
  @Value("${author.ignoreUrls}")
  private String[] ignoreUrls;

  /**
  * 
  */
  @Override
  public void decide(Authentication authentication, Object object,
      Collection<ConfigAttribute> configAttributes)
      throws AccessDeniedException, InsufficientAuthenticationException {
    /*
     * authentication：包含了当前的用户信息，包括拥有的权限。 这里的权限来源就是前面登录时UserDetailsService中（public UserDetails
     * loadUserByUsername(String username)）<code>设置的authorities object：就是FilterInvocation对象
     * ，可以得到request等web资源 configAttributes：是本次访问需要的权限
     * 
     * 
     * 
     * 这里就是进行当前url请求需要的权限，和当前登录操作者所具备的权限进行对比
     * 1、当前url主要的权限从CustomFilterInvocationSecurityMetadataSource的getAttributes(Object object)这个方法返回
     * 就是这个方法中的configAttributes参数 2、当前登陆者所具有的权限为authentication，
     * 就是CustomUserSecurityDetailsService中循环添加到 GrantedAuthority 对象中的权限信息集合 3、object
     * 包含客户端发起的请求的requset信息
     * 
     * 处理过程是： 1、如果当前登录者具备超级管理员ADMIN的权限，就不需要进行判断，直接通过
     * 2、如果当前configAttributes没有任何角色信息，说明当前url并不需要权限控制，也直接通过
     * 3、否则就以configAttributes为标准进行循环，依次到authentication中进行判断
     */
    // 1、=================
    Collection<? extends GrantedAuthority> currentAuthors = authentication.getAuthorities();
    if (currentAuthors == null || currentAuthors.isEmpty()) {
      throw new AccessDeniedException("not found any author from this single in user!");
    }
    for (GrantedAuthority grantedAuthority : currentAuthors) {
      // 发现超级管理员权限 或者 匿名者权限，就直接通过验证
      if (StringUtils.equals(grantedAuthority.getAuthority(), "ADMIN")
          || StringUtils.equals(grantedAuthority.getAuthority(), "ROLE_ANONYMOUS")) {
        return;
      }
    }
    // 如果当前路径是需要忽略权限的路径，则不再进行后续判断
    FilterInvocation filterInvocation = (FilterInvocation) object;
    HttpServletRequest request = filterInvocation.getHttpRequest();
    for (String ignoreUrl : ignoreUrls) {
      AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(ignoreUrl);
      if (requestMatcher.matches(request)) {
        return;
      }
    }


    // 2、=================
    if (configAttributes == null || configAttributes.isEmpty()) {
      return;
    }

    // 3、================
    for (ConfigAttribute securityConfig : configAttributes) {
      for (GrantedAuthority grantedAuthority : currentAuthors) {
        // 如果条件成立，则说明当前登陆者具备访问这个url的权限
        if (StringUtils.equals(securityConfig.getAttribute(), grantedAuthority.getAuthority())) {
          return;
        }
      }
    }

    throw new AccessDeniedException("not author!");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.security.access.AccessDecisionManager#supports(org.springframework.security
   * .access.ConfigAttribute)
   */
  @Override
  public boolean supports(ConfigAttribute attribute) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.security.access.AccessDecisionManager#supports(java.lang.Class)
   */
  @Override
  public boolean supports(Class<?> clazz) {
    return true;
  }

}
