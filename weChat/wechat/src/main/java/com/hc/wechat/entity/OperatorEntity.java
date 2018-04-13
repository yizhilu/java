package com.hc.wechat.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.hc.wechat.common.enums.UseStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 后台管理员账户.
 * 
 * @author hc
 */
@ApiModel(value = "OperatorEntity")
@Entity
@Table(name = "operator")
public class OperatorEntity extends UuidEntity {

  // metaValue值.
  public static final String META_VALUE = "operator";
  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 1159352097781652775L;
  /** 账号. **/
  @Column(name = "account", unique = true, nullable = false)
  private String account = "";

  /** 密码. **/
  @Column(name = "password", nullable = false)
  private String password = "";

  /** 昵称. **/
  @Column(name = "nick_name", nullable = false)
  private String nickName = "";

  /** 姓名. **/
  @Column(name = "name", nullable = false)
  private String name = "";

  /** 状态 1正常, 2禁用(枚举). **/
  @Type(type = "useStatus")
  @Column(name = "status", nullable = false)
  private UseStatus status = UseStatus.STATUS_NORMAL;

  /** 最后登录时间. **/
  @Column(name = "last_login_time")
  private Date lastLoginTime;

  /** 创建时间. **/
  @Column(name = "create_date", nullable = false)
  private Date createDate = new Date();

  /** 修改时间. **/
  @Column(name = "modify_date")
  private Date modifyDate;

  /** 角色——和管理员相关的. **/
  @ApiModelProperty(hidden = true)
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "operators")
  private Set<RoleEntity> roles;
  /** 头像. */
  @Column(name = "header", nullable = false)
  private String imagePath = "";

  /** 创建人. **/
  @ApiModelProperty(hidden = true)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private OperatorEntity creator;

  /** 修改人. **/
  @ApiModelProperty(hidden = true)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modifier_id")
  private OperatorEntity modifier;

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public OperatorEntity getCreator() {
    return creator;
  }

  public void setCreator(OperatorEntity creator) {
    this.creator = creator;
  }

  public OperatorEntity getModifier() {
    return modifier;
  }

  public void setModifier(OperatorEntity modifier) {
    this.modifier = modifier;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UseStatus getStatus() {
    return status;
  }

  public void setStatus(UseStatus status) {
    this.status = status;
  }

  public Date getLastLoginTime() {
    return lastLoginTime;
  }

  public void setLastLoginTime(Date lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
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

  public Set<RoleEntity> getRoles() {
    return roles;
  }

  public void setRoles(Set<RoleEntity> roles) {
    this.roles = roles;
  }

  @Transient
  public String getMetaValue() {
    return META_VALUE;
  }

}
