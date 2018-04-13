/**
 * 权限.
 */
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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.hc.wechat.common.enums.EnumType;
import com.hc.wechat.common.enums.UseStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 权限
 * 
 * @author ly yinwenjie
 * @date 2017年8月8日 下午2:22:32
 * @version V1.0
 */
@ApiModel(value = "CompetenceEntity")
@Entity
@Table(name = "competence")
@TypeDefs({
    @TypeDef(name = "useStatus", typeClass = EnumType.class, parameters = {
        @Parameter(name = "class", value = "com.hc.wechat.common.enums.UseStatus")}),
    @TypeDef(name = "competenceType", typeClass = EnumType.class, parameters = {
        @Parameter(name = "class", value = "com.hc.wechat.common.enums.CompetenceType")})})
public class CompetenceEntity extends UuidEntity {

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = -7742962048681654604L;

  /** 权限URL串. **/
  @Column(name = "resource", nullable = false)
  private String resource = "";

  /**
   * 涉及的方法描述<br>
   * 例如：POST或者POST|GET|DELETE
   */
  @Column(name = "methods", nullable = false)
  private String methods = "";

  /** 创建时间. **/
  @Column(name = "create_date", nullable = false)
  private Date createDate = new Date();

  /** 修改时间. **/
  @Column(name = "modify_date")
  private Date modifyDate;

  /** 状态 1正常, 0禁用（枚举）. **/
  @Type(type = "useStatus")
  @Column(name = "status", nullable = false)
  private UseStatus status = UseStatus.STATUS_NORMAL;

  /** 最后一次操作人. **/
  @ApiModelProperty(hidden = true)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private OperatorEntity creator;

  /** 修改人. **/
  @ApiModelProperty(hidden = true)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modifier_id")
  private OperatorEntity modifier;

  /**
   * 权限对应的角色信息
   */
  @ApiModelProperty(hidden = true)
  @ManyToMany(mappedBy = "competences")
  private Set<RoleEntity> roles;

  /** 备注. **/
  @Column(name = "comment", nullable = false)
  private String comment = "";

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public UseStatus getStatus() {
    return status;
  }

  public void setStatus(UseStatus status) {
    this.status = status;
  }

  public OperatorEntity getCreator() {
    return creator;
  }

  public void setCreator(OperatorEntity creator) {
    this.creator = creator;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getMethods() {
    return methods;
  }

  public void setMethods(String methods) {
    this.methods = methods;
  }

  public Set<RoleEntity> getRoles() {
    return roles;
  }

  public void setRoles(Set<RoleEntity> roles) {
    this.roles = roles;
  }

  public Date getModifyDate() {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate) {
    this.modifyDate = modifyDate;
  }

  public OperatorEntity getModifier() {
    return modifier;
  }

  public void setModifier(OperatorEntity modifier) {
    this.modifier = modifier;
  }
}
