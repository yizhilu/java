package com.hecheng.wechat.openplatform.service.observer;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;

/**
 * 解析mqttTopic数据主题
 * 
 * @author hc
 *
 */
public class AnalysisMqttDataSubject implements Subject {
  // 存放观察者引用
  private ArrayList<TopicObserver> observers = new ArrayList<TopicObserver>();

  @Override
  public void addObserver(TopicObserver o) {
    if (!(observers.contains(o))) {
      observers.add(o);
    }

  }

  @Override
  public void deleteObserver(TopicObserver o) {
    if (observers.contains(o)) {
      observers.remove(o);
    }

  }

  @Override
  public void notifyObservers(JSONObject obj) {
    JSONObject headers = obj.getJSONObject("headers");
    String topic = headers.getString("mqtt_topic");
    for (TopicObserver observer : observers) {
      if (observer.isMatch(topic)) {
        observer.analysis(obj);
      }
    }

  }

}
