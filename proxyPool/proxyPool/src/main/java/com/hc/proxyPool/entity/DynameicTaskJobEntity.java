package com.hc.proxyPool.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 任务日志
 * 
 * @author hc
 *
 */
@Entity
@Table(name = "dynameic_task_job")
public class DynameicTaskJobEntity extends UuidEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 549889799906787274L;
  /***
   * 任务名称
   */
  @Column(name = "job_name", length = 64, nullable = false, unique = true)
  private String jobName;
  /**
   * 计划任务执行的规则
   */
  @Column(name = "cron", length = 64, nullable = false)
  private String cron;
  /**
   * 是否删除
   */
  @Column(name = "del", length = 64, nullable = false)
  private boolean del;
  /**
   * 是否停止任务
   */
  @Column(name = "is_stop", length = 64, nullable = false)
  private boolean stop;
  /**
   * 任务描述
   */
  @Column(name = "desc_", length = 64, nullable = false)
  private String desc;
  /**
   * 任务描述
   */
  @Column(name = "last_execution_date")
  private Date lastExecutionDate;

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getCron() {
    return cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }

  public boolean isDel() {
    return del;
  }

  public void setDel(boolean del) {
    this.del = del;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public boolean isStop() {
    return stop;
  }

  public void setStop(boolean stop) {
    this.stop = stop;
  }

  public Date getLastExecutionDate() {
    return lastExecutionDate;
  }

  public void setLastExecutionDate(Date lastExecutionDate) {
    this.lastExecutionDate = lastExecutionDate;
  }

}
