package com.hecheng.wechat.openplatform.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户的联系方式
 * 
 * @author hq
 *
 */

@ApiModel(value = "UserContactEntity")
@Table
@Entity(name = "user_contact")
public class UserContactEntity extends UuidEntity {

  private static final long serialVersionUID = 1L;
  /**
   * 联系人电话
   */
  @Column(name = "phone", nullable = false, length = 20)
  private String phone;
  /**
   * 创建一个指向user的外键关联
   */
  @ApiModelProperty(hidden = true)
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }
}
