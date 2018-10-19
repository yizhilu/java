//package com.hecheng.wechat.openplatform.configuration;
//
//import java.util.UUID;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.integration.annotation.IntegrationComponentScan;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.core.MessageProducer;
//import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
//import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
//import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
//import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.messaging.MessagingException;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.hecheng.wechat.openplatform.service.observer.AnalysisMqttDataSubject;
//
///**
// * mqtt 消费者配置
// * 
// * @author hc
// *
// */
//@Configuration
//@Lazy
//@IntegrationComponentScan
//public class MqttCustomerConfig {
//  private static final Logger LOG = LoggerFactory.getLogger(MqttCustomerConfig.class);
//
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
//   * mqtt clientId客户端ID，生产端和消费端的客户端ID需不同，不然服务器会认为是同一个客户端，会接收不到信息
//   */
//  @Value("${spring.mqtt.customer.clientId}")
//  private String clientId;
//  /**
//   * topic 订阅的主题
//   */
//  @Value("${spring.mqtt.customer.topics}")
//  private String[] topics;
//  /**
//   *
//   * <pre>
//   *  服务质量等级 
//   * Qos0:发送者只发送一次消息，不进行重试，Broker不会返回确认消息。在Qos0情况下，Broker可能没有接受到消息
//   * Qos1:发送者最少发送一次消息，确保消息到达Broker，Broker需要返回确认消息PUBACK。在Qos1情况下，Broker可能接受到重复消息
//   * Qos2:使用两阶段确认来保证消息的不丢失和不重复。在Qos2情况下，Broker肯定会收到消息，且只收到一次
//   * </pre>
//   */
//  @Value("${spring.mqtt.customer.qos}")
//  private Integer qos;
//  /**
//   * 异步操作的完成超时
//   */
//  @Value("${spring.mqtt.customer.completionTimeout}")
//  private Integer completionTimeout;
//
//  /**
//   * 消息通道
//   * 
//   * @return
//   */
//  @Bean
//  public MessageChannel mqttInputChannel() {
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
//    // false，表示如果订阅的客户机断线了，要保存为其要推送的消息（QoS为1和QoS为2），若其重新连接时，需将这些消息推送（若客户端长时间不连接，需要设置一个过期值）。
//    // true，断线服务器即清理相关信息，重新连接上来之后，会再次订阅。
//    clientFactory.setCleanSession(false);
//    return clientFactory;
//  }
//
//  /**
//   * 通道适配器
//   * 
//   * @param clientFactory
//   * @param mqttInputChannel
//   * @return
//   */
//  @Bean
//  public MessageProducer inbound(MqttPahoClientFactory clientFactory,
//      MessageChannel mqttInputChannel) {
//    // clientId 客户端ID，生产端和消费端的客户端ID需不同，不然服务器会认为是同一个客户端，会接收不到信息
//    // topic 订阅的主题
//    MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
//        clientId + UUID.randomUUID(), clientFactory, topics);
//    // 超时时间
//    adapter.setCompletionTimeout(completionTimeout);
//    // 转换器
//    adapter.setConverter(new DefaultPahoMessageConverter());
//    adapter.setQos(qos);
//    adapter.setOutputChannel(mqttInputChannel);
//    return adapter;
//  }
//
//  @Autowired
//  private AnalysisMqttDataSubject analysisMqttDataSubject;
//
//  /**
//   * 消息处理
//   * 
//   * @return
//   */
//  @Bean
//  @ServiceActivator(inputChannel = "mqttInputChannel")
//  public MessageHandler handler() {
//    return new MessageHandler() {
//
//      @Override
//      public void handleMessage(Message<?> message) throws MessagingException {
//        LOG.info("接收到数据:" + JSON.toJSONString(message));
//        analysisMqttDataSubject.notifyObservers((JSONObject) JSON.toJSON(message));
//      }
//    };
//  }
//}
