package com.hecheng.wechat.openplatform.service.observer;

import com.alibaba.fastjson.JSONObject;

/**
 * topic观察者
 * 
 * @author hc
 *
 */
public interface TopicObserver {
  /**
   * 是否匹配topic的标识
   * 
   * @return
   */
  public boolean isMatch(String topicSymbol);

  /**
   * 解析数据
   * 
   * @param obj
   */
  public void analysis(JSONObject obj);
}
