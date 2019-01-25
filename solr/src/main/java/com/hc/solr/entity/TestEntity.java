package com.hc.solr.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "test")
public class TestEntity extends UuidEntity {

  /**
   * <p>
   * Description:
   * </p>
   * 
   * @author hecheng
   * @date 2019年1月7日
   */
  private static final long serialVersionUID = 1L;

  /***
   * 名
   */
  @Column(name = "name")
  private String name;
  @Column(name = "num")
  private int num;
  @Column(name = "create_date")
  private Date createDate;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getNum() {
    return num;
  }

  public void setNum(int num) {
    this.num = num;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

}
