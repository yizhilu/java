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

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.CharsetDetector;

@Service("AgriCrawleService")
public class AgriCrawleService extends BreadthCrawler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AgriCrawleService.class);

  public AgriCrawleService() {
    super("crawl", true);

  }

  public AgriCrawleService(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    this.setConf(
        this.getConf().setExecuteInterval(5000).setConnectTimeout(20000).setMaxExecuteCount(5));

  }


  @Override
  public void visit(Page page, CrawlDatums next) {
    String pageUrl = page.url();
    // 搜索
    if (Pattern.matches("^http://www.agri.cn/V20/SC/scjghq/gnhq/index.*$", pageUrl)) {
      analysis(page, next);
    } else if (Pattern.matches("^http://www.agri.cn/V20/SC/scjghq/gnhq/.*$", pageUrl)) {
      analysisDetails(page, next);
    }
  }

  private void analysisDetails(Page page, CrawlDatums next) {
    HtmlUnitDriver driver = new HtmlUnitDriver();
    driver.setJavascriptEnabled(true);
    driver.get(page.url());
   WebElement title= driver.findElement(By.className("hui_15_cu"));
   System.out.println(title.getText());
   WebElement details=driver.findElement(By.id("TRS_AUTOADD"));
   System.out.println(details.getText());
  }

  private void analysis(Page page, CrawlDatums next) {

    HtmlUnitDriver driver = new HtmlUnitDriver();
    driver.setJavascriptEnabled(true);
    driver.get(page.url());
    // analysisJsoup(page, next);
    List<WebElement> as = driver.findElements(By.className("link03"));
    for (WebElement a : as) {
      String href = a.getAttribute("href");
      next.add(new CrawlDatum(href));
    }
    WebElement totalPage = driver.findElement(By.id("pageBar1"));
    String totalPageStr = totalPage.getText();
    if (StringUtils.isNotBlank(totalPageStr)) {
      String pageStr = totalPageStr.substring(1, totalPageStr.indexOf("页"));
      for (int i = 1; i < Integer.valueOf(pageStr); i++) {
        next.add(new CrawlDatum("http://www.agri.cn/V20/SC/scjghq/gnhq/index_" + i + ".htm"));
      }
    }

    // analysisJsoup(page,next);
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
      Elements elements = doc.select("a[class='link03']");
      for (Element element : elements) {
        String relativelyHref = element.attr("href");
        String href = relativelyHref.substring(1, relativelyHref.length());
        next.add(new CrawlDatum("http://www.agri.cn/V20/SC/scjghq/gnhq" + href));
      }
      String totalPageStr = doc.select("#last").attr("href");
      if (StringUtils.isNotBlank(totalPageStr)) {
        String pageStr = totalPageStr.substring(1, 2);

      }

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  public static void main(String[] args) throws Exception {
    AgriCrawleService crawleService = new AgriCrawleService();

    crawleService.addSeed("http://www.agri.cn/V20/SC/scjghq/gnhq/index.htm");

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
}

