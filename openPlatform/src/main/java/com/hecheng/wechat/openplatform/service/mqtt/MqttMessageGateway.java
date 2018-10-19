//package com.vanda.aihome.service.mqtt;
//
//import org.springframework.integration.annotation.Header;
//import org.springframework.integration.annotation.MessagingGateway;
//import org.springframework.integration.mqtt.support.MqttHeaders;
//import org.springframework.messaging.Message;
//import org.springframework.stereotype.Component;
//
///**
// *
// * 消息发送接口，不需要实现，spring会通过代理的方式实现
// *
// * @author hc
// */
//@Component
//@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
//@SuppressWarnings("deprecation")
//public interface MqttMessageGateway {
//  /**
//   * 发送消息
//   * 
//   * @param message
//   */
//  void sendMessage(Message<?> message);
//
//  /**
//   * 发送指定信息到指定topic
//   * 
//   * @param topic
//   * @param payload
//   */
//  void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);
//
//  /**
//   * 发送指定信息到指定topic
//   * 
//   * @param topic
//   * @param payload
//   * @param qos
//   */
//  void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos,
//      String payload);
//}
