package com.hc.security.service.security;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hc.security.entity.UserEntity;
import com.hc.security.service.UserService;

@Service("UserSecurityDetailsService")
public class UserSecurityDetailsService implements UserDetailsService {

  @Autowired
  private UserService userService;

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
    UserEntity currentUser = this.userService.findByUserName(username);
    if (currentUser == null) {
      throw new UsernameNotFoundException("没有发现指定的账号，或者账号状态不正确！");
    }
    List<SimpleGrantedAuthority> authorities = new LinkedList<>();
    SimpleGrantedAuthority authoritie = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
    authorities.add(authoritie);
    // 角色信息形成authorities集合对象
    UserDetails securityDetails = new User(username, currentUser.getPassword(), authorities);
    return securityDetails;
  }
}
