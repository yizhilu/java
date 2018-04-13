package com.hc.wechat.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.hc.wechat.common.enums.EnumType;
import com.hc.wechat.common.enums.PlatFormType;
import com.hc.wechat.common.enums.UseStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 终端用户，注意：目前的终端用户都是通过第三方平台author2方式登录，所以这里不需要记录密码 
 * TODO 如果存在本系统内的应用用户名和密码还是需要的
 * 
 * @author hc
 */
@Entity
@Table(name = "users")
@ApiModel(value = "UserEntity")
@TypeDefs({@TypeDef(name = "status", typeClass = EnumType.class, parameters = {
    @Parameter(name = "class", value = "com.hc.wechat.common.enums.UseStatus")})})
public class UserEntity extends UuidEntity {
  /**
  * 
  */
  private static final long serialVersionUID = 1L;
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
   * 性别，1是男，2是女
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
  /**
   * 用户状态
   */
  @Type(type = "status")
  @Column(name = "status", nullable = false)
  private UseStatus status = UseStatus.STATUS_NORMAL;
  /**
   * 电话 
   */
  @Column(name = "telephone", length = 64, unique = true)
  private String telephone = "";
  /**
   * 第三方平台账号
   */
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  @ApiModelProperty(hidden = true)
  private List<ThirdPartUserEntity> thirdPartUsers;

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

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public List<ThirdPartUserEntity> getThirdPartUsers() {
    return thirdPartUsers;
  }

  public void setThirdPartUsers(List<ThirdPartUserEntity> thirdPartUsers) {
    this.thirdPartUsers = thirdPartUsers;
  }

  public UseStatus getStatus() {
    return status;
  }

  public void setStatus(UseStatus status) {
    this.status = status;
  }

  /**
   * 根据第三方平台类型获得当前登录用户的第三方账户
   * 
   * @param platFormType 第三方平台类型
   * @return
   */
  @Transient
  public ThirdPartUserEntity getNowThirdPartUser(PlatFormType platFormType) {
    if (thirdPartUsers == null || thirdPartUsers.isEmpty()) {
      return null;
    }
    for (ThirdPartUserEntity thirdPartUser : thirdPartUsers) {
      if (thirdPartUser.getPlatFormType().equals(platFormType)) {
        return thirdPartUser;
      }
    }
    return null;
  }

}
