package com.hecheng.wechat.openplatform.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.hecheng.wechat.openplatform.common.enums.EnumType;
import com.hecheng.wechat.openplatform.common.enums.PlatFormType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 第三方登录用户(账号)
 * 
 * @author hc
 */
@Entity
@Table(name = "third_users")
@ApiModel(value = "ThirdPartUserEntity")
@TypeDef(name = "platFormType", typeClass = EnumType.class, parameters = {
    @Parameter(name = "class", value = "com.hecheng.wechat.openplatform.common.enums.PlatFormType")})
public class ThirdPartUserEntity extends UuidEntity {

  private static final long serialVersionUID = 1L;
  /**
   * 第三方账户唯一标识符
   */
  @Column(name = "open_id", length = 128, nullable = false)
  private String openId;
  /**
   * 第三方账户统一标识
   */
  @Column(name = "union_id", length = 128)
  private String unionId = "";
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
   * 性别，0未知，1是男，2是女
   */
  @Column(name = "sex")
  private int sex = 0;
  /**
   * 城市
   */
  @Column(name = "city", length = 64)
  private String city = "";
  /**
   * 创建时间
   */
  @Column(name = "create_time", nullable = false)
  private Date createDate = new Date();
  /**
   * 最后登录时间
   */
  @Column(name = "login_time")
  private Date lastLoginDate;
  /**
   * 关联的终端用户
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @ApiModelProperty(hidden = true)
  private UserEntity user;
  /**
   * 
   * 对应平台类型
   */
  @Type(type = "platFormType")
  @Column(name = "platform_type", nullable = false)
  private PlatFormType platFormType = PlatFormType.PLAT_FORM_TYPE_WEIXIN;

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public String getUnionId() {
    return unionId;
  }

  public void setUnionId(String unionId) {
    this.unionId = unionId;
  }

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

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getLastLoginDate() {
    return lastLoginDate;
  }

  public void setLastLoginDate(Date lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public PlatFormType getPlatFormType() {
    return platFormType;
  }

  public void setPlatFormType(PlatFormType platFormType) {
    this.platFormType = platFormType;
  }
}
