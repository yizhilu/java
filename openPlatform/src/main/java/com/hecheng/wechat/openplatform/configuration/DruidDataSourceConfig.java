package com.hecheng.wechat.openplatform.configuration;

import java.util.Collections;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DruidDataSourceConfig {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.driverClassName}")
  private String driverClassName;


  @Bean // 声明其为Bean实例
  @Primary // 在同样的DataSource中，首先使用被标注的DataSource
  public DataSource dataSource() {
    DruidDataSource datasource = new DruidDataSource();

    datasource.setUrl(this.dbUrl);
    datasource.setUsername(username);
    datasource.setPassword(password);
    datasource.setDriverClassName(driverClassName);

    // configuration
    String connectionInitSqls = "SET NAMES utf8mb4";
    StringTokenizer tokenizer = new StringTokenizer(connectionInitSqls, ";");
    datasource.setConnectionInitSqls(Collections.list(tokenizer));// 重点设置该参数
    // datasource.setInitialSize(initialSize);
    // datasource.setMinIdle(minIdle);
    // datasource.setMaxActive(maxActive);
    // datasource.setMaxWait(maxWait);
    // datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    // datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    // datasource.setValidationQuery(validationQuery);
    // datasource.setTestWhileIdle(testWhileIdle);
    // datasource.setTestOnBorrow(testOnBorrow);
    // datasource.setTestOnReturn(testOnReturn);
    // datasource.setPoolPreparedStatements(poolPreparedStatements);
    // datasource
    // .setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
    // try {
    // datasource.setFilters(filters);
    // } catch (SQLException e) {
    // logger.error("druid configuration initialization filter", e);
    // }
    // datasource.setConnectionProperties(connectionProperties);
    return datasource;
  }
}
