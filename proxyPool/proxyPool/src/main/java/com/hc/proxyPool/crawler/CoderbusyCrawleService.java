package com.hc.proxyPool.crawler;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.proxyPool.common.utils.DateUtils;
import com.hc.proxyPool.common.utils.ProxyUtils;
import com.hc.proxyPool.entity.DynameicTaskJobEntity;
import com.hc.proxyPool.entity.ProxyDataEntity;
import com.hc.proxyPool.repository.ProxyDataRepository;
import com.hc.proxyPool.service.DynamicTaskJobService;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.CharsetDetector;

@Service("CoderbusyCrawleService")
public class CoderbusyCrawleService extends BreadthCrawler implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(CoderbusyCrawleService.class);
  @Autowired
  private ProxyDataRepository proxyDataRepository;
  @Autowired
  private DynamicTaskJobService dynamicTaskJobService;

  public CoderbusyCrawleService() {
    super("crawl", true);

  }

  public CoderbusyCrawleService(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    this.setConf(
        this.getConf().setExecuteInterval(5000).setConnectTimeout(20000).setMaxExecuteCount(5));

  }


  @Override
  public void visit(Page page, CrawlDatums next) {
    String pageUrl = page.url();
    // 搜索
    if (Pattern.matches("^https://proxy.coderbusy.com/.*$", pageUrl)) {
      analysis(page, next);
    }

  }

  private void analysis(Page page, CrawlDatums next) {

    // HtmlUnitDriver driver = new HtmlUnitDriver();
    // driver.get(page.url());
    // List<WebElement> trs = driver.findElements(By.xpath("//table[@class='table']/tbody/tr"));
    // if (trs.size() > 0) {
    // List<ProxyDataEntity> proxyDatas = proxyDataRepository.findAll();
    // proxyDataRepository.delete(proxyDatas);
    // }
    // for (WebElement tr : trs) {
    // List<WebElement> tds = tr.findElements(By.tagName("td"));
    // String ip = tds.get(0).getText();
    // String port = tds.get(1).getText();
    // ProxyDataEntity proxyData = new ProxyDataEntity();
    // proxyData.setProxyIp(ip);
    // proxyData.setProxyPort(port);
    // LOGGER.info(proxyData.toString());
    // proxyDataRepository.save(proxyData);
    // }
    analysisJsoup(page, next);
  }

  private void analysisJsoup(Page page, CrawlDatums next) {
    CloseableHttpClient client = HttpClients.createDefault();
    String url = page.url();
    try {
      HttpGet get = new HttpGet(url);
      HttpResponse response = client.execute(get);
      HttpEntity entity = response.getEntity();
      /*
       * 利用HttpClient获取网页的字节数组， 通过CharsetDetector判断网页的编码
       */
      byte[] content = EntityUtils.toByteArray(entity);
      String charset = CharsetDetector.guessEncoding(content);
      String html = new String(content, charset);
      /* 利用Jsoup解析网页，并执行抽取等操作 */
      Document doc = Jsoup.parse(html, url);
      System.out.println(doc.title());
      Elements elements = doc.select("div[class='table-responsive'] table tbody tr:gt(0)");
      if (elements.size() > 0) {
        List<ProxyDataEntity> proxyDatas = proxyDataRepository.findByDataSource(
            "https://proxy.coderbusy.com", DateUtils.getEarlyInTheDay(new Date()));
        proxyDataRepository.delete(proxyDatas);
      }
      for (Element element : elements) {
        String ip = element.select("td:eq(0)").first().text();
        String port = element.select("td:eq(1)").first().text();
        String address = element.select("td:eq(2)").first().text();
        String operator = element.select("td:eq(3)").first().text();
        String type = element.select("td:eq(4)").first().text();
        String anonymousType = element.select("td:eq(6)").first().text();
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(port)) {
          continue;
        }
        ProxyDataEntity proxy = proxyDataRepository.findByProxyIpAndProxyPort(ip, port);
        if (proxy != null) {
          continue;
        }
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(port)) {
          continue;
        }
        if ("HTTP".equals(type) || "HTTPS".equals(type)) {
          ProxyDataEntity proxyData = new ProxyDataEntity();
          proxyData.setProxyIp(ip);
          proxyData.setProxyPort(port);
          proxyData.setProxyOperator(operator);
          proxyData.setProxyAddress(address);
          proxyData.setProxyType(type);
          proxyData.setAnonymousType(anonymousType);
          proxyData.setDataSource("https://proxy.coderbusy.com");
          proxyData.setCrawleDate(new Date());
          LOGGER.info(proxyData.toString());
          proxyDataRepository.save(proxyData);

        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  public static void main(String[] args) throws Exception {
    CoderbusyCrawleService crawleService = new CoderbusyCrawleService();

    crawleService.addSeed("https://proxy.coderbusy.com/");

    crawleService.addRegex("-.*\\.(jpg|png|gif).*");

    crawleService.addRegex("-.*#.*");
    try {
      // 设置爬虫的总线程数量
      crawleService.setThreads(5);
      crawleService.start(6);
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }

  @Override
  public void run() {
    DynameicTaskJobEntity dynameicTaskJob =
        dynamicTaskJobService.findByJobName("CoderbusyCrawleService");
    if (dynameicTaskJob != null) {
      dynameicTaskJob.setLastExecutionDate(new Date());
      dynamicTaskJobService.update(dynameicTaskJob);
    }
    this.addSeed("https://proxy.coderbusy.com/");

    this.addRegex("-.*\\.(jpg|png|gif).*");

    this.addRegex("-.*#.*");
    try {
      // 设置爬虫的总线程数量
      this.setThreads(5);
      this.start(6);
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }

  }
}

