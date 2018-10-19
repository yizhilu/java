//package com.hecheng.wechat.openplatform.configuration;
//
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.Header;
//import org.springframework.integration.annotation.IntegrationComponentScan;
//import org.springframework.integration.annotation.MessagingGateway;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
//import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
//import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
//import org.springframework.integration.mqtt.support.MqttHeaders;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//
///**
// * mqtt 生产者配置
// * 
// * @author hc
// *
// */
//@SuppressWarnings("deprecation")
//@Configuration
//@IntegrationComponentScan
//public class MqttProudcerConfig {
//  /**
//   * mqtt 服务地址
//   */
//  @Value("${spring.mqtt.url}")
//  private String serviceUri;
//  /**
//   * mqtt 服务用户名
//   */
//  @Value("${spring.mqtt.username}")
//  private String username;
//  /**
//   * mqtt 服务密碼
//   */
//  @Value("${spring.mqtt.password}")
//  private String password;
//  /**
//   * mqtt clientId 客户端ID，生产端和消费端的客户端ID需不同，不然服务器会认为是同一个客户端，会接收不到信息
//   */
//  @Value("${spring.mqtt.proudcer.clientId}")
//  private String clientId;
//  /**
//   * mqtt 默認topic
//   */
//  @Value("${spring.mqtt.proudcer.defaultTopic}")
//  private String defaultTopic;
//
//  /**
//   * 消息通道
//   * 
//   * @return
//   */
//  @Bean
//  public MessageChannel mqttOutboundChannel() {
//    return new DirectChannel();
//  }
//
//  /**
//   * mqtt服务器配置
//   * 
//   * @return
//   */
//  @Bean
//  public MqttPahoClientFactory clientFactory() {
//    DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
//    clientFactory.setServerURIs(serviceUri);
//    clientFactory.setUserName(username);
//    clientFactory.setPassword(password);
//    return clientFactory;
//  }
//
//  /**
//   * 通道适配器
//   * 
//   * @param clientFactory
//   * @return
//   */
//  @Bean
//  @ServiceActivator(inputChannel = "mqttOutboundChannel")
//  public MqttPahoMessageHandler mqttOutbound(MqttPahoClientFactory clientFactory) {
//    MqttPahoMessageHandler messageHandler =
//        new MqttPahoMessageHandler(clientId + UUID.randomUUID(), clientFactory);
//    messageHandler.setAsync(true);
//    messageHandler.setDefaultTopic(defaultTopic);
//    messageHandler.setDefaultQos(1);
//    // 1个Topic只有唯一的retain消息，Broker会保存每个Topic的最后一条retain消息。
//    // 每个Client订阅Topic后会立即读取到retain消息，不必要等待发送。
//    messageHandler.setDefaultRetained(false);
//    // messageHandler.setAsyncEvents(false);
//    return messageHandler;
//  }
//
//  @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
//  public interface MqttMessageGateway {
//    /**
//     * 发送消息
//     * 
//     * @param message
//     */
//    void sendMessage(Message<?> message);
//
//    /**
//     * 发送指定信息到指定topic
//     * 
//     * @param topic
//     * @param payload
//     */
//    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);
//
//    /**
//     * 发送指定信息到指定topic
//     * 
//     * @param topic
//     * @param payload
//     * @param qos
//     */
//    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos,
//        String payload);
//  }
//}
