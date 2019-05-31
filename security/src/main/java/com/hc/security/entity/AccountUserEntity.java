package com.hc.security.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author hc
 *
 */
@Entity
@Table(name = "b_account_user")
public class AccountUserEntity extends UuidEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 7880620829794772789L;
  /**
   * 账号
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;
  /**
   * 
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  public AccountEntity getAccount() {
    return account;
  }

  public void setAccount(AccountEntity account) {
    this.account = account;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }
}
