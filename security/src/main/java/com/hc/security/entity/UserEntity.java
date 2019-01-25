package com.hc.security.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;

/**
 * 终端用户，注意：目前的终端用户都是通过第三方平台author2方式登录，所以这里不需要记录密码
 * 
 * @author hc
 */
@Entity
@Table(name = "users")
@ApiModel(value = "UserEntity")
public class UserEntity extends UuidEntity {
  /**
  * 
  */
  private static final long serialVersionUID = 1L;
  public static final String META_VALUE = "user";
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
  /**
   * 昵称
   */
  @Column(name = "nick_name", length = 64)
  private String nickName = "";
  /**
   * 头像
   */
  @Column(name = "cover", length = 256)
  private String cover = "";
  /**
   * 性别，值为1时是男性，值为2时是女性，值为0时是未知
   */
  @Column(name = "sex")
  private int sex = 1;
  /**
   * 城市 未关注的用户为空
   */
  @Column(name = "city", length = 64)
  private String city = "";
  /**
   * 上次登录时间
   */
  @Column(name = "last_login_date")
  private Date lastLoginDate;
  /**
   * 创建时间
   */
  @Column(name = "create_date", nullable = false)
  private Date createDate;
  /**
   * 最后修改时间
   */
  @Column(name = "modify_date")
  private Date modifyDate;

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public int getSex() {
    return sex;
  }

  public void setSex(int sex) {
    this.sex = sex;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Date getLastLoginDate() {
    return lastLoginDate;
  }

  public void setLastLoginDate(Date lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
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

}
