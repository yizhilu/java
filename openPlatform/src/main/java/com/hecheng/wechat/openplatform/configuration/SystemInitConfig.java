package com.hecheng.wechat.openplatform.configuration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.entity.OperatorEntity;
import com.hecheng.wechat.openplatform.entity.RoleEntity;
import com.hecheng.wechat.openplatform.service.OperatorService;
import com.hecheng.wechat.openplatform.service.RoleService;

/**
 * 数据初始化配置，以保证整个系统在保证最小化数据完成性的基础上能够使用
 * 
 * @author yinwenjie
 */
@Component
public class SystemInitConfig implements CommandLineRunner {

  @Autowired
  private RoleService roleService;

  @Autowired
  private OperatorService operatorService;

  /**
   * 初始化角色
   */
  private void initRole() {
    /*
     * 向系统初始化role角色信息 保证系统中至少固定持有ADMIN（管理员）、QBANK（题库管理员）OTHER（其他）、STAFF（科员以下）、
     * VICE_DEPART（副科级）、POSITIVE_DEPART（正科级）、VICE_UNIT（副处级）、POSITIVE_UNIT（正处级）、VICE_CITY（副厅级）、
     * POSITIVE_CITY（正厅级） 10个角色
     */
    if (this.roleService.findByName("ADMIN") == null) {
      RoleEntity role = new RoleEntity();
      role.setComment("管理员角色");
      role.setCreateDate(new Date());
      role.setName("ADMIN");
      role.setStatus(UseStatus.STATUS_NORMAL);
      this.roleService.addRole(role);
    }
  }

  /**
   * 初始化系统管理员
   */
  private void initAdmin() {
    /*
     * 保证系统中至少有一个系统管理员
     */
    OperatorEntity admin = this.operatorService.findByAccount("admin");
    if (admin == null) {
      admin = new OperatorEntity();
      admin.setAccount("admin");
      admin.setCreateDate(new Date());
      admin.setName("超级管理员");
      admin.setNickName("超级管理员");
      admin.setPassword("123456");
      admin.setStatus(UseStatus.STATUS_NORMAL);
      operatorService.create(admin, new String[] {"ADMIN"});
    }

  }


  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
   */
  @Override
  public void run(String... args) throws Exception {
    this.initRole();
    this.initAdmin();
  }
}
