package com.hc.proxyPool.crawler;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.proxyPool.common.utils.DateUtils;
import com.hc.proxyPool.entity.DynameicTaskJobEntity;
import com.hc.proxyPool.entity.ProxyDataEntity;
import com.hc.proxyPool.repository.ProxyDataRepository;
import com.hc.proxyPool.service.DynamicTaskJobService;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

@Service("Ip181CrawleService")
public class Ip181CrawleService extends BreadthCrawler implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(Ip181CrawleService.class);
  @Autowired
  private ProxyDataRepository proxyDataRepository;
  @Autowired
  private DynamicTaskJobService dynamicTaskJobService;
  public Ip181CrawleService() {
    super("crawl", true);

  }

  public Ip181CrawleService(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    this.setConf(
        this.getConf().setExecuteInterval(5000).setConnectTimeout(20000).setMaxExecuteCount(5));

  }


  @Override
  public void visit(Page page, CrawlDatums next) {
    String pageUrl = page.url();
    // 搜索
    if (Pattern.matches("^http://www.ip181.com.*$", pageUrl)) {
      analysis(page, next);
    }

  }

  private void analysis(Page page, CrawlDatums next) {

    HtmlUnitDriver driver = new HtmlUnitDriver();
    driver.get(page.url());
    List<WebElement> trs = driver.findElements(
        By.xpath("//table[@class='table table-hover panel-default panel ctable']/tbody/tr"));
    if (trs.size() > 0) {
      List<ProxyDataEntity> proxyDatas = proxyDataRepository
          .findByDataSource("http://www.ip181.com", DateUtils.getEarlyInTheDay(new Date()));
      proxyDataRepository.delete(proxyDatas);
    }
    int i = 0;
    for (WebElement tr : trs) {
      if (i++ == 0) {
        continue;
      }
      List<WebElement> tds = tr.findElements(By.tagName("td"));
      String ip = tds.get(0).getText();
      String port = tds.get(1).getText();
      String anonymousType = tds.get(2).getText();
      String type = tds.get(3).getText();
      String address = tds.get(5).getText();
      if (StringUtils.isBlank(ip) || StringUtils.isBlank(port)) {
        continue;
      }
      ProxyDataEntity proxy = proxyDataRepository.findByProxyIpAndProxyPort(ip, port);
      if (proxy != null) {
        continue;
      }
      if ("HTTP".equals(type) || "HTTPS".equals(type) || type.contains("HTTP")
          || type.contains("HTTPS")) {
        ProxyDataEntity proxyData = new ProxyDataEntity();
        proxyData.setProxyIp(ip);
        proxyData.setProxyPort(port);
        proxyData.setProxyAddress(address);
        proxyData.setAnonymousType(anonymousType);
        proxyData.setDataSource("http://www.ip181.com");
        proxyData.setProxyType(type);
        proxyData.setCrawleDate(new Date());
        LOGGER.info(proxyData.toString());
        proxyDataRepository.save(proxyData);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Ip181CrawleService crawleService = new Ip181CrawleService();

    crawleService.addSeed("http://www.ip181.com");

    crawleService.addRegex("-.*\\.(jpg|png|gif).*");

    crawleService.addRegex("-.*#.*");
    try {
      // 设置爬虫的总线程数量
      crawleService.setThreads(5);
      crawleService.start(10);
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }

  @Override
  public void run() {
    DynameicTaskJobEntity dynameicTaskJob =
        dynamicTaskJobService.findByJobName("CoderbusyCrawleService");
    if(dynameicTaskJob!=null) {
      dynameicTaskJob.setLastExecutionDate(new Date());
      dynamicTaskJobService.update(dynameicTaskJob);
    }
    this.addSeed("http://www.ip181.com");

    this.addRegex("-.*\\.(jpg|png|gif).*");

    this.addRegex("-.*#.*");
    try {
      // 设置爬虫的总线程数量
      this.setThreads(5);
      this.start(10);
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }

  }
}

