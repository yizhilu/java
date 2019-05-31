package com.hc.security.service.security;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.base.Objects;
import com.hc.security.entity.AccountEntity;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.RoleEntity;
import com.hc.security.entity.UserEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.service.AccountService;
import com.hc.security.service.RoleService;

public class UserSecurityDetailsService implements UserDetailsService {

  @Autowired
  private AccountService accountService;
  @Autowired
  private RoleService roleService;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.
   * String)
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    /*
     * 查找用户的处理过程描述如下： 1、首先查找代理商信息，如果没有找到就差早商户信息，如果还没有找到，就抛出异常 2、查找这个账号对应的角色信息（当然是目前还可以用的角色信息）
     * 3、构造UserDetails对象，并返回
     */
    // 查询用户基本信息
    AccountEntity currentAccount = this.accountService.findByUserName(username);
    if (currentAccount == null) {
      throw new UsernameNotFoundException("没有发现指定的账号！");
    }
    if (!Objects.equal(StatusType.STATUS_NORMAL, currentAccount.getStatus())) {
      throw new UsernameNotFoundException("账号已被禁用，请联系管理员！");
    }
    UserEntity user = accountService.findUserByAccountId(currentAccount.getId());
    OperatorEntity operator = accountService.findOperatorByAccountId(currentAccount.getId());
    if (user == null && operator == null) {
      throw new UsernameNotFoundException("账号是一个空号！");
    }
    List<SimpleGrantedAuthority> authorities = new LinkedList<>();
    if (user != null) {
      SimpleGrantedAuthority authoritie = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
      authorities.add(authoritie);
    } else {
      // 查询用户角色信息
      Set<RoleEntity> roles = this.roleService.findByOperatorId(operator.getId());
      if (roles == null || roles.isEmpty()) {
        throw new UsernameNotFoundException("用户权限状态错误，请联系客服人员！");
      }
      for (RoleEntity role : roles) {
        SimpleGrantedAuthority authoritie = new SimpleGrantedAuthority(role.getName());
        authorities.add(authoritie);
      }
    }
    // 角色信息形成authorities集合对象
    UserDetails securityDetails = new User(username, currentAccount.getPassword(), authorities);
    return securityDetails;
  }
}
