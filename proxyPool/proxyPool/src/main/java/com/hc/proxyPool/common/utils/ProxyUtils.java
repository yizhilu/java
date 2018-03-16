package com.hc.proxyPool.common.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 代理管理
 * 
 * @author hc
 *
 */
public class ProxyUtils {
  private static final Logger logger = LoggerFactory.getLogger(ProxyUtils.class);

  /**
   * 检验代理ip有效性
   * 
   * @param ip
   * @param port
   * @return
   */
  public static boolean verifyProxy(String ip, int port) {
    boolean isPassed = false;
    HttpURLConnection connection = null;
    try {
      URL url = new URL("https://www.baidu.com");
      InetSocketAddress addr = new InetSocketAddress(ip, port);
      Type proxyType = Proxy.Type.HTTP;
      Proxy proxy = new Proxy(proxyType, addr);
      connection = (HttpURLConnection) url.openConnection(proxy);
      connection.setConnectTimeout(20 * 1000);
      connection.setInstanceFollowRedirects(false);
      connection.setReadTimeout(6 * 1000);
      int code = connection.getResponseCode();
      isPassed = code == 200;
    } catch (IOException e) {
      logger.info(String.format("verify proxy %s:%d exception: " + e.getMessage(), ip, port));
      isPassed = false;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    logger.info(String.format("verify proxy %s:%d isPassed: " + isPassed, ip, port));
    return isPassed;
  }

  public static boolean verifyProxy2(String ip, int port) {
    boolean isPassed = false;
    try {
      HttpClient httpClient = new HttpClient();
      httpClient.getHostConfiguration().setProxy(ip, port);

      // 连接超时时间（默认10秒 10000ms） 单位毫秒（ms）
      int connectionTimeout = 10000;
      // 读取数据超时时间（默认30秒 30000ms） 单位毫秒（ms）
      int soTimeout = 30000;
      httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
      httpClient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

      HttpMethod method = new GetMethod("http://www.baidu.com");

      int code = httpClient.executeMethod(method);
      isPassed = code == 200;
    } catch (Exception e) {
      logger.info(String.format("verify proxy2 %s:%d exception: " + e.getMessage(), ip, port));
    }
    logger.info(String.format("verify proxy %s:%d isPassed: " + isPassed, ip, port));
    return isPassed;
  }
}
