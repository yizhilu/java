package com.hecheng.wechat.openplatform.service.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import com.hecheng.wechat.openplatform.entity.CompetenceEntity;
import com.hecheng.wechat.openplatform.entity.RoleEntity;
import com.hecheng.wechat.openplatform.service.CompetenceService;
import com.hecheng.wechat.openplatform.service.RoleService;


/**
 * @author yinwenjie
 */
@Service("CustomFilterInvocationSecurityMetadataSource")
public class CustomFilterInvocationSecurityMetadataSource
    implements
      FilterInvocationSecurityMetadataSource {
  @SuppressWarnings("unused")
  private final static Logger LOGGER =
      LoggerFactory.getLogger(CustomFilterInvocationSecurityMetadataSource.class);

  @Autowired
  private CompetenceService competenceService;

  @Autowired
  private RoleService roleService;

  /**
   * 忽略权限判断的url
   */
  @Value("${author.ignoreUrls}")
  private String[] ignoreUrls;

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.security.access.SecurityMetadataSource#getAttributes(java.lang.Object)
   */
  @Override
  public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
    /*
     * 确定功能路径绑定的角色过程如下： 1、如果当前的路径符合配置的“排除权限”路径，那么直接返回“匿名”角色和“管理员角色”
     * 2、然后才开始正式的判断，因为很可能存在“{}”传参的形式，所以需要进行递归排除、 3、比较Method，返回method设置匹配的功能权限绑定信息
     */

    FilterInvocation filterInvocation = (FilterInvocation) object;
    String url = filterInvocation.getHttpRequest().getRequestURI();
    HttpServletRequest request = filterInvocation.getHttpRequest();
    List<ConfigAttribute> configs = new ArrayList<>();
    String method = request.getMethod();

    // 1、====================
    for (String ignoreUrl : ignoreUrls) {
      AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(ignoreUrl);
      if (requestMatcher.matches(request)) {
        return this.fillAnonymous(configs);
      }
    }

    // 2、===================
    int index = 0;
    String matchUrl = "";
    List<CompetenceEntity> currentCompetences = null;
    try {
      URI currentUri = new URI(url);
      do {
        // 拼凑对比url用的表达式
        matchUrl = currentUri.toString();
        for (int sum = 0; sum < index; sum++) {
          matchUrl += "/{param}";
        }
        // 查询数据库，找到可能存在的满足url格式的功能信息
        currentCompetences = this.queryByRequestUrl(matchUrl);
        // 如果条件成立，说明找到了那个要找的权限功能绑定信息
        if (currentCompetences != null && !currentCompetences.isEmpty()) {
          break;
        }

        currentUri = currentUri.resolve(".");
        // 去掉最后的“/”
        String currentUriValue = currentUri.toString();
        currentUriValue = currentUriValue.substring(0, currentUriValue.length() - 1);
        if (StringUtils.equals("", currentUriValue)) {
          break;
        }
        // 这是上一级uri
        currentUri = new URI(currentUriValue);
        index++;
      } while (true);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e.getMessage());
    }

    // 3、===================
    // 这里在进行request method的过滤，只有满足method方式的角色名，才能写入到configs集合中
    for (CompetenceEntity competence : currentCompetences) {
      String methods = competence.getMethods();
      if (methods.indexOf(method) != -1) {
        // 取得当前权限和角色的绑定关系
        List<RoleEntity> roles = roleService.findByCompetenceId(competence.getId());
        if (roles != null && !roles.isEmpty()) {
          for (RoleEntity role : roles) {
            SecurityConfig securityConfig = new SecurityConfig(role.getName());
            configs.add(securityConfig);
          }
        }
      }
    }

    if (configs.isEmpty()) {
      return this.fillAnonymous(configs);
    } else {
      return configs;
    }
  }

  /**
   * 该私有方法，将在角色集合为empty的时候，默认填充两个角色“管理员角色”和“匿名角色”
   * 
   * @param configs
   * @return
   */
  private Collection<ConfigAttribute> fillAnonymous(Collection<ConfigAttribute> configs) {
    // 如果条件成立，说明并没有设定相关url的权限
    // 那么就只有两类角色可以访问，一类是ADMIN，另一类是匿名用户ROLE_ANONYMOUS
    if (configs.isEmpty()) {
      configs.add(new SecurityConfig("ADMIN"));
      configs.add(new SecurityConfig("ROLE_ANONYMOUS"));
    }

    return configs;
  }

  /**
   * 查询当前权限设置中，符合当前http访问路径的角色信息。（注意没有通过method过滤）
   * 
   * @param requestUrl
   */
  private List<CompetenceEntity> queryByRequestUrl(String requestUrl) {
    List<CompetenceEntity> competences = this.competenceService.findByResource(requestUrl);
    if (competences == null || competences.size() == 0) {
      return Collections.emptyList();
    }
    return competences;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.security.access.SecurityMetadataSource#getAllConfigAttributes()
   */
  @Override
  public Collection<ConfigAttribute> getAllConfigAttributes() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.security.access.SecurityMetadataSource#supports(java.lang.Class)
   */
  @Override
  public boolean supports(Class<?> clazz) {
    return true;
  }
}
