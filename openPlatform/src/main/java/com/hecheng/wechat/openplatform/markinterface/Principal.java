package com.hecheng.wechat.openplatform.markinterface;

import java.io.Serializable;
import java.util.Date;

import com.hecheng.wechat.openplatform.common.enums.UseStatus;

/**
 * 此接口表示需要认证主体的抽象概念，它可以用来表示任何实体
 * 
 * @author hc
 *
 */
public interface Principal {

  /**
   * 获得标识
   * 
   * @return
   */
  String getMetaValue();

  /**
   * 得到用户标识
   * 
   * @return 用户标识
   */
  Serializable getIdentity();

  /**
   * 获得昵称名字
   * 
   * @return
   */
  String getNickName();

  /**
   * 获得真实名字
   * 
   * @return
   */
  String getRealName();

  /**
   * 获得用户状态
   * 
   * @return
   */
  UseStatus getStatus();

  /**
   * 得到用户最后登录时间
   * 
   * @return 用户最后登录时间 （返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此日期表示的毫秒数）
   */
  Date getLastLoginTime();

  /**
   * 获得用户头像
   * 
   * @return
   */
  String getHeadImgPath();
}

