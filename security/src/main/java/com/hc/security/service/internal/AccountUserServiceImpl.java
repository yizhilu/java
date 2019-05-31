package com.hc.security.service.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.security.entity.UserEntity;
import com.hc.security.repository.AccountUserRepository;
import com.hc.security.service.AccountUserService;

@Service("accountUserService")
public class AccountUserServiceImpl implements AccountUserService {
  @Autowired
  private AccountUserRepository accountUserRepository;

  @Override
  public UserEntity findByAccountId(String accountId) {
    return accountUserRepository.findByAccountId(accountId);
  }

  @Override
  public UserEntity findByUserName(String username) {
    return accountUserRepository.findByUserName(username);
  }

}
