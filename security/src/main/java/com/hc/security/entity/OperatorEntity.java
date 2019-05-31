package com.hc.security.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.hc.security.entity.enums.EnumType;
import com.hc.security.entity.enums.StatusType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统用户基本信息.
 * 
 * @version V1.0
 */
@ApiModel(value = "OperatorEntity")
@Entity
@Table(name = "operator")
@TypeDefs({@TypeDef(name = "status", typeClass = EnumType.class, parameters = {
    @Parameter(name = "class", value = "com.hc.security.entity.enums.StatusType")})})
public class OperatorEntity extends UuidEntity {

  private static final long serialVersionUID = -5744634769875803775L;

  /** 姓名. **/
  @Column(name = "name", length = 64, nullable = false)
  private String name = "";

  /*** 真实名字 */
  @Column(name = "realname", length = 64, nullable = false)
  private String realName = "";

  /** 头像图片的相对路径 */
  @Column(name = "imagepath", length = 256)
  private String imagePath = "";

  /*** 电子邮件 */
  @Column(name = "email", length = 64, nullable = true)
  private String email = "";

  /** 状态 1正常, 2禁用(枚举). **/
  @Type(type = "status")
  @Column(name = "status", nullable = false)
  private StatusType status = StatusType.STATUS_NORMAL;

  /** 修改人 **/
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modify_user")
  @ApiModelProperty(hidden = true)
  private OperatorEntity modifyUser;

  /** 创建人 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "create_user")
  @ApiModelProperty(hidden = true)
  private OperatorEntity createUser;

  /** 最后登录时间. **/
  @Column(name = "last_login_time")
  private Date lastLoginTime;

  /** 创建时间. **/
  @Column(name = "create_date", nullable = false)
  private Date createDate = new Date();

  /** 修改时间. **/
  @Column(name = "modify_date")
  private Date modifyDate;

  /** 角色和用户的对应关系相关的. **/
  @ApiModelProperty(hidden = true)
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "operators")
  private Set<RoleEntity> roles;

  /** 逻辑删除字段，是否删除. **/
  @Column(name = "is_del")
  private boolean isDel = false;

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Set<RoleEntity> getRoles() {
    return roles;
  }

  public void setRoles(Set<RoleEntity> roles) {
    this.roles = roles;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  public OperatorEntity getModifyUser() {
    return modifyUser;
  }

  public void setModifyUser(OperatorEntity modifyUser) {
    this.modifyUser = modifyUser;
  }

  public OperatorEntity getCreateUser() {
    return createUser;
  }

  public void setCreateUser(OperatorEntity createUser) {
    this.createUser = createUser;
  }

  public boolean isDel() {
    return isDel;
  }

  public void setDel(boolean isDel) {
    this.isDel = isDel;
  }

}
