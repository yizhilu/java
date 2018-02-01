/**
 * 权限.
 */
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

import org.hibernate.annotations.ListIndexBase;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.hc.security.entity.enums.CompetenceModeType;
import com.hc.security.entity.enums.EnumType;
import com.hc.security.entity.enums.StatusType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 需要进行权限管理的功能
 * 
 * @version V1.0
 */
@Entity
@Table(name = "competence")
@ApiModel(value = "CompetenceEntity")
@TypeDefs({
    @TypeDef(name = "status", typeClass = EnumType.class, parameters = {
        @Parameter(name = "class", value = "com.hc.security.entity.enums.StatusType")}),
    @TypeDef(name = "loadingMode", typeClass = EnumType.class, parameters = {
        @Parameter(name = "class", value = "com.hc.security.entity.enums.CompetenceModeType")})})
public class CompetenceEntity extends UuidEntity {

  private static final long serialVersionUID = -7742962048681654604L;

  /** 权限URL串. **/
  @Column(name = "resource", length = 256, nullable = false)
  private String resource = "";

  /**
   * 涉及的方法描述<br>
   * 例如：POST或者POST|GET|DELETE|PATCH等
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
  @Type(type = "status")
  @Column(name = "status", nullable = false)
  private StatusType status = StatusType.STATUS_NORMAL;

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
  @ManyToMany(mappedBy = "competences", fetch = FetchType.LAZY)
  private Set<RoleEntity> roles;

  /** 功能中文名，只能是唯一的. **/
  @Column(name = "comment", length = 128, nullable = false, unique = true)
  private String comment = "";

  /**
   * 功能在主目录树上的层级，如果没有表示功能并不在主目录树的层级上<br>
   * 0表示根层级
   */
  @Column(name = "level", nullable = true)
  private int level = Integer.MAX_VALUE;

  /**
   * 描述当前功能在主目录树上的排序，越小排序越靠前
   */
  @ListIndexBase(0)
  @Column(name = "order_index", nullable = true)
  private long orderIndex = Long.MAX_VALUE;

  /**
   * 上一级功能——在主目录上的
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", nullable = true)
  private CompetenceEntity parentCompetence;

  /**
   * 功能在页面上呈现的形式（url还是按钮）
   */
  @Column(name = "loading_mode", nullable = true)
  private CompetenceModeType loadingMode = CompetenceModeType.MODETYPE_URL;

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public CompetenceEntity getParentCompetence() {
    return parentCompetence;
  }

  public void setParentCompetence(CompetenceEntity parentCompetence) {
    this.parentCompetence = parentCompetence;
  }

  public CompetenceModeType getLoadingMode() {
    return loadingMode;
  }

  public void setLoadingMode(CompetenceModeType loadingMode) {
    this.loadingMode = loadingMode;
  }

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

  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
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

  public OperatorEntity getCreator() {
    return creator;
  }

  public void setCreator(OperatorEntity creator) {
    this.creator = creator;
  }

  public OperatorEntity getModifier() {
    return modifier;
  }

  public long getOrderIndex() {
    return orderIndex;
  }

  public void setOrderIndex(long orderIndex) {
    this.orderIndex = orderIndex;
  }

  public void setModifier(OperatorEntity modifier) {
    this.modifier = modifier;
  }
}
