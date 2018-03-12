package com.hc.solr.configuration;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author hc
 *
 */
@Configuration
public class SolrConfig {
  /** 连接超时时间Timeout in milliseconds. **/
  public static final int CONNECTION_TIME_OUT = 5000;
  /** 读取超时. **/
  public static final int SO_TIME_OUT = 5000;
  /** 设置最大连接数. **/
  public static final int MAX_CONNECTIONS_PERHOST = 100;

  /** solr host. **/
  @Value("${solr.host}")
  private String HTTP_SOLR_CLIENT;

  /** solr coreName. **/
  @Value("${solr.coreName}")
  private String CORE_NAME;

  @Bean
  public HttpSolrClient getHttpSolrClientServer() {
    HttpSolrClient server = new HttpSolrClient(HTTP_SOLR_CLIENT + CORE_NAME);
    server.setConnectionTimeout(CONNECTION_TIME_OUT);
    server.setSoTimeout(SO_TIME_OUT);
    server.setMaxTotalConnections(MAX_CONNECTIONS_PERHOST);
    // 设置在任何给定时间可以向单个主机打开的最大连接数。 如果http客户端在操作之外被创建是不允许的。
    server.setDefaultMaxConnectionsPerHost(MAX_CONNECTIONS_PERHOST);
    server.setParser(new XMLResponseParser());
    server.setFollowRedirects(false);
    // 允许服务器 - >客户端通信被压缩。 目前支持gzip和deflate。 如果服务器支持压缩，则响应将被压缩。
    // 只有当http客户端类型为DefatulHttpClient时，才允许使用此方法。
    server.setAllowCompression(true);
    return server;
  }
}
