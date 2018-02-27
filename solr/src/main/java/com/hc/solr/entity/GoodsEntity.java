package com.hc.solr.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "goods")
public class GoodsEntity extends UuidEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 549889799906787274L;
  /***
   * 商品名
   */
  @Column(name = "name", length = 64, nullable = false, unique = true)
  private String name;
  /**
   * 商品图片地址
   */
  @Column(name = "image_path")
  private String imagePath;
  /**
   * 商品描述
   */
  @Column(name = "desc")
  private String desc;
  /***
   * 商品售价
   */
  @Column(name = "price")
  private BigDecimal price;
  /**
   * 商品分类
   */
  @Column(name = "category")
  private String category;
  /**
   * 商品品牌
   */
  @Column(name = "brand")
  private String brand;
  /**
   * 商品创建时间
   */
  @Column(name = "create_date")
  private Date createDate;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

}
