package com.hecheng.wechat.openplatform.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import com.hecheng.wechat.openplatform.common.enums.MessageType;
import com.hecheng.wechat.openplatform.common.message.MsgTemplate;
import com.hecheng.wechat.openplatform.service.MsgService;


/**
 * 根据通知类型不同调用不同service发送消息
 * 
 * @author cxj
 *
 */
@Configuration
public class MsgTools implements ApplicationContextAware {
  @SuppressWarnings("unused")
  private static Logger log = Logger.getLogger(MsgTools.class);
  /**
   * 如果没有查看微信通知信息，那么其他通知方式间隔时间
   */
  public static int TIME_INTERVAL = 1;
  public static int SLEEP_TIME = 10 * 1000;
  /** 存入memcached的时间 **/
  public static int MEM_TIME = (TIME_INTERVAL + 1) * 60 * 1000;
  private static Map<MessageType, Object> map = new HashMap<MessageType, Object>();

  private static MsgTools msgTools = new MsgTools();
  private static boolean isReloaded = false;
  private static ApplicationContext applicationContext;

  public static MsgTools getInstance() {
    return msgTools;
  }


  /**
   * 返回第三方数据(字符串)
   * 
   * @param msgType
   * @param msgTemplate
   * @return
   */
  public String sendMsgBackString(MessageType msgType, MsgTemplate msgTemplate) {
    synchronized (MsgTools.class) {
      while (!isReloaded) {
        try {
          MsgTools.class.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      MsgService msgService = (MsgService) map.get(msgType);
      return msgService.sendMsgBackString(msgTemplate);
    }
  }

  public boolean sendMessage(MessageType msgType, MsgTemplate msgTemplate) {
    synchronized (MsgTools.class) {
      while (!isReloaded) {
        try {
          MsgTools.class.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      MsgService msgService = (MsgService) map.get(msgType);
      return msgService.sendMsg(msgTemplate);
    }
  }

  /**
   * 发送消息格式
   * 
   * @author cxj
   *
   */
  public class SendObj {
    private MessageType msgType;
    private MsgTemplate msgTemplate;

    public SendObj(MessageType msgType, MsgTemplate msgTemplate) {
      this.msgType = msgType;
      this.msgTemplate = msgTemplate;
    }

    public MessageType getMsgType() {
      return msgType;
    }

    public void setMsgType(MessageType msgType) {
      this.msgType = msgType;
    }

    public MsgTemplate getMsgTemplate() {
      return msgTemplate;
    }

    public void setMsgTemplate(MsgTemplate msgTemplate) {
      this.msgTemplate = msgTemplate;
    }
  }


  @Override
  public void setApplicationContext(ApplicationContext arg0) throws BeansException {
    synchronized (MsgTools.class) {
      MsgTools.applicationContext = arg0;
      isReloaded = true;
      map.put(MessageType.MSGTYPE_WEIXIN, MsgTools.applicationContext
          .getBean(com.hecheng.wechat.openplatform.service.internal.WeChatServiceImpl.class));
      map.put(MessageType.MSGTYPE_SMS, MsgTools.applicationContext
          .getBean(com.hecheng.wechat.openplatform.service.internal.ValidCodeServiceImpl.class));
      MsgTools.class.notifyAll();
    }

  }

}
