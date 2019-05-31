package com.hc.security.service.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.security.entity.UserEntity;
import com.hc.security.repository.UserRepository;
import com.hc.security.service.AccountService;
import com.hc.security.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AccountService accountService;

  @Override
  public UserEntity findByUserName(String username) {
    return accountService.findUserByAccount(username);
  }

}
