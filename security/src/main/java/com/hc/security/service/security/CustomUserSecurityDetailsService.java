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
import org.springframework.stereotype.Service;

import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.RoleEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.service.OperatorService;
import com.hc.security.service.RoleService;

@Service("customUserSecurityDetailsService")
public class CustomUserSecurityDetailsService implements UserDetailsService {

  @Autowired
  private OperatorService operatorService;

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
     * 查找用户的处理过程描述如下： 1、首先查找代理商信息，如果没有找到就差早商户信息，如果还没有找到，就抛出异常
     * 2、查找这个账号对应的角色信息（当然是目前还可以用的角色信息）
     * 3、构造UserDetails对象，并返回
     */
    // 查询用户基本信息
    OperatorEntity currentUser =
        this.operatorService.findByAccountAndStatus(username, StatusType.STATUS_NORMAL);
    if (currentUser == null) {
      throw new UsernameNotFoundException("没有发现指定的账号，或者账号状态不正确！");
    }

    // 查询用户角色信息
    Set<RoleEntity> roles = null;
    roles = this.roleService.findByOperatorId(currentUser.getId());
    if (roles == null || roles.isEmpty()) {
      throw new UsernameNotFoundException("用户权限状态错误，请联系客服人员！");
    }
    List<SimpleGrantedAuthority> authorities = new LinkedList<>();
    for (RoleEntity role : roles) {
      SimpleGrantedAuthority authoritie = new SimpleGrantedAuthority(role.getName());
      authorities.add(authoritie);
    }

    // 角色信息形成authorities集合对象
    UserDetails securityDetails = new User(username, currentUser.getPassword(), authorities);
    return securityDetails;
  }
}
