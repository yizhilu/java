package com.hc.security.service.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.security.entity.UserEntity;
import com.hc.security.repository.UserRepository;
import com.hc.security.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public UserEntity findByUserName(String username) {
    return userRepository.findByUserName(username);
  }

}
