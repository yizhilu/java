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
@Table(name = "b_account_operator")
public class AccountOperatorEntity extends UuidEntity {

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
   * 后台操作者
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "operator_id", nullable = false)
  private OperatorEntity operator;

  public AccountEntity getAccount() {
    return account;
  }

  public void setAccount(AccountEntity account) {
    this.account = account;
  }

  public OperatorEntity getOperator() {
    return operator;
  }

  public void setOperator(OperatorEntity operator) {
    this.operator = operator;
  }


}
