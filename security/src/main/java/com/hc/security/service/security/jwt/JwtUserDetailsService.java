package com.hc.security.service.security.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.hc.security.entity.AccountEntity;
import com.hc.security.entity.RoleEntity;
import com.hc.security.entity.UserEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.service.AccountService;
import com.hc.security.service.security.jwt.utils.JwtUserFactory;

@Service("jwtUserDetailsService")
public class JwtUserDetailsService implements UserDetailsService {

  @Autowired
  private AccountService accountService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AccountEntity currentAccount = accountService.findByUserName(username);
    if (currentAccount == null) {
      throw new UsernameNotFoundException("没有发现指定的账号！");
    }
    if (!Objects.equal(StatusType.STATUS_NORMAL, currentAccount.getStatus())) {
      throw new UsernameNotFoundException("账号已被禁用，请联系管理员！");
    }
    UserEntity user = accountService.findUserByAccountId(currentAccount.getId());
    if (user == null) {
      throw new UsernameNotFoundException(
          String.format("No user found with username '%s'.", username));
    } else {
      List<RoleEntity> roles = new ArrayList<>();
      return JwtUserFactory.create(currentAccount, roles);
    }
  }
}
