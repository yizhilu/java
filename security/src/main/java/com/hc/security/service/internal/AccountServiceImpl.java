package com.hc.security.service.internal;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hc.security.entity.AccountEntity;
import com.hc.security.entity.AccountOperatorEntity;
import com.hc.security.entity.AccountUserEntity;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.UserEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.repository.AccountOperatorRepository;
import com.hc.security.repository.AccountRepository;
import com.hc.security.repository.AccountUserRepository;
import com.hc.security.service.AccountService;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private AccountOperatorRepository accountOperatorRepository;
  @Autowired
  private AccountUserRepository accountUserRepository;
  @Autowired
  private Md5PasswordEncoder passwordEncoder;

  @Override
  public AccountEntity findByUserName(String userName) {
    return accountRepository.findByUserName(userName);
  }

  @Override
  public OperatorEntity findOperatorByAccountAndStatus(String account, StatusType status) {
    return accountOperatorRepository.findByAccountAndStatus(account, status);
  }

  @Override
  public UserEntity findUserByAccount(String username) {
    return accountUserRepository.findByUserName(username);
  }

  @Override
  public UserEntity findUserByAccountId(String accountId) {
    return accountUserRepository.findByAccountId(accountId);
  }

  @Override
  public OperatorEntity findOperatorByAccountId(String accountId) {
    return accountOperatorRepository.findByAccountId(accountId);
  }

  @Override
  @Transactional
  public void create(OperatorEntity operator, AccountEntity account) {
    Validate.notNull(operator, "操作者信息不能为空");
    account = this.create(account);

    AccountOperatorEntity accountOperator = new AccountOperatorEntity();
    accountOperator.setOperator(operator);
    accountOperator.setAccount(account);
    accountOperatorRepository.save(accountOperator);
  }

  @Override
  @Transactional
  public void create(UserEntity User, AccountEntity account) {
    Validate.notNull(User, "用户信息不能为空");
    account = this.create(account);
    AccountUserEntity accountUser = new AccountUserEntity();
    accountUser.setUser(User);
    accountUser.setAccount(account);
    accountUserRepository.save(accountUser);
  }

  private AccountEntity create(AccountEntity account) {
    Validate.notNull(account, "账号信息不能为空");
    Validate.notBlank(account.getUserName(), "账号名不能为空");
    Validate.notBlank(account.getPassword(), "账号密码不能为空");
    // 加密密码
    String encodePassword = passwordEncoder.encodePassword(account.getPassword(), null);
    account.setPassword(encodePassword);
    return accountRepository.save(account);
  }
}
