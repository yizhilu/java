package com.hc.security.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.hc.security.entity.enums.AccountTypeEnum;
import com.hc.security.entity.enums.EnumType;
import com.hc.security.entity.enums.StatusType;

/**
 * 账号实体
 * 
 * @author hc
 *
 */
@Entity
@Table(name = "b_account")
@TypeDefs({
    @TypeDef(name = "accountType", typeClass = EnumType.class, parameters = {
        @Parameter(name = "class", value = "com.hc.security.entity.enums.AccountTypeEnum")}),
    @TypeDef(name = "status", typeClass = EnumType.class, parameters = {
        @Parameter(name = "class", value = "com.hc.security.entity.enums.StatusType")})})
public class AccountEntity extends UuidEntity {

  /**
   * 
   */
  private static final long serialVersionUID = -4273228735302763667L;
  /**
   * 账户
   */
  @Column(name = "user_name")
  private String userName;
  /**
   * 密码
   */
  @Column(name = "password")
  private String password;

  /** 账号类型. **/
  @Type(type = "accountType")
  @Column(name = "account_type", nullable = false)
  private AccountTypeEnum accountType = AccountTypeEnum.ACCOUNT_PASSWORD;
  /** 状态 1正常, 2禁用(枚举). **/
  @Type(type = "status")
  @Column(name = "status", nullable = false)
  private StatusType status = StatusType.STATUS_NORMAL;
  /** 创建时间. **/
  @CreatedDate
  @Column(name = "create_date", nullable = false)
  private Date createDate;

  /** 修改时间. **/
  @LastModifiedDate
  @Column(name = "modify_date")
  private Date modifyDate;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AccountTypeEnum getAccountType() {
    return accountType;
  }

  public void setAccountType(AccountTypeEnum accountType) {
    this.accountType = accountType;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate) {
    this.modifyDate = modifyDate;
  }

  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

}
