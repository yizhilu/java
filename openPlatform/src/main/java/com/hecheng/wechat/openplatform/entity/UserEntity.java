package com.hecheng.wechat.openplatform.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.hecheng.wechat.openplatform.common.enums.EnumType;
import com.hecheng.wechat.openplatform.common.enums.PlatFormType;
import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.markinterface.Principal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 终端用户，注意：目前的终端用户都是通过第三方平台author2方式登录，所以这里不需要记录密码
 * 
 * @author hc
 */
@Entity
@Table(name = "users")
@ApiModel(value = "UserEntity")
@TypeDefs({@TypeDef(name = "status", typeClass = EnumType.class, parameters = {
    @Parameter(name = "class", value = "com.hecheng.wechat.openplatform.common.enums.UseStatus")})})
public class UserEntity extends UuidEntity implements Principal {
  /**
  * 
  */
  private static final long serialVersionUID = 1L;
  public static final String META_VALUE = "user";

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
  /**
   * 用户状态
   */
  @Type(type = "status")
  @Column(name = "status", nullable = false)
  private UseStatus status = UseStatus.STATUS_NORMAL;
  /**
   * 第三方平台账号
   */
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  @ApiModelProperty(hidden = true)
  private List<ThirdPartUserEntity> thirdPartUsers;

  /**
   * 用户联系方式
   */
  @ApiModelProperty(hidden = true)
  @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
  private UserContactEntity userContact;
  /**
   * 联系电话
   */
  @Transient
  private String telephone;

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

  public UseStatus getStatus() {
    return status;
  }

  public void setStatus(UseStatus status) {
    this.status = status;
  }

  public List<ThirdPartUserEntity> getThirdPartUsers() {
    return thirdPartUsers;
  }

  public void setThirdPartUsers(List<ThirdPartUserEntity> thirdPartUsers) {
    this.thirdPartUsers = thirdPartUsers;
  }

  public UserContactEntity getUserContact() {
    return userContact;
  }

  public void setUserContact(UserContactEntity userContact) {
    this.userContact = userContact;
  }

  public String getTelephone() {
    if (this.telephone != null) {
      return telephone;
    } else if (this.userContact != null) {
      return this.userContact.getPhone();
    }
    return null;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
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


  @Override
  @Transient
  public String getMetaValue() {
    return META_VALUE;
  }

  @Override
  @Transient
  public Serializable getIdentity() {
    return this.getId();
  }

  @Override
  @Transient
  public String getRealName() {
    return null;
  }

  @Override
  @Transient
  public Date getLastLoginTime() {
    return this.getLastLoginDate();
  }

  @Override
  @Transient
  public String getHeadImgPath() {
    return this.getCover();
  }

}
