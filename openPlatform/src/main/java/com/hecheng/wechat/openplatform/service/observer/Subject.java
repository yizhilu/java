package com.hecheng.wechat.openplatform.service.observer;

import com.alibaba.fastjson.JSONObject;

/**
 * 主题
 * 
 * @author hc
 *
 */
public interface Subject {
  /**
   * 添加观察者
   * 
   * @param o
   */
  public void addObserver(TopicObserver o);

  /**
   * 删除观察者
   * 
   * @param o
   */
  public void deleteObserver(TopicObserver o);

  /**
   * 通知观察者
   */
  public void notifyObservers(JSONObject obj);
}
