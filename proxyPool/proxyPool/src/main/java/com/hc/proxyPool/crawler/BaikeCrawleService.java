package com.hc.proxyPool.crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
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

@Service("BaikeCrawleService")
public class BaikeCrawleService extends BreadthCrawler {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaikeCrawleService.class);

  public BaikeCrawleService() {
    super("crawl", true);

  }

  public BaikeCrawleService(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    this.setConf(
        this.getConf().setExecuteInterval(5000).setConnectTimeout(20000).setMaxExecuteCount(1));

  }


  @Override
  public void visit(Page page, CrawlDatums next) {
    // 则是公司信息页
    if (Pattern.matches("^https://baike.qixin.com/.*$", page.url())) {
      analysisCompanyResult(page, next);

    } else if (Pattern.matches("^https://baike.baidu.com/item/.*$", page.url())) {
      // 搜索结果页
      analysisSearchResult(page, next);
    }
  }

  /**
   * 分析企业信息页
   * 
   * @param page
   * @param next
   */
  private void analysisCompanyResult(Page page, CrawlDatums next) {
    ChromeDriver driver = getChromeDriver();
    driver.get(page.url());
    try {
      Thread.sleep(4000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    String companyName=driver.findElement(By.className("company-name")).getText();
    List<WebElement> leftdds=driver.findElements(By.xpath("//dl[@class='business-info-left']/dd"));
    if(leftdds!=null&&!leftdds.isEmpty()) {
      String creditCode=leftdds.get(0).getText();//统一社会信用代码:
      String organizationCode=leftdds.get(1).getText();//组织机构代码:
      String regCode=leftdds.get(2).getText();//注册号:
      String registrationType=leftdds.get(3).getText();//经营状态:
      String enterpriseType=leftdds.get(4).getText();//公司类型:
      String establishTime=leftdds.get(5).getText();//成立日期:
    }
    List<WebElement> rightdds=driver.findElements(By.xpath("//dl[@class='business-info-right']/dd"));
    if(rightdds!=null&&!rightdds.isEmpty()) {
      String legalPerson=rightdds.get(0).getText();//法人
      String operatingPeriodStrat=rightdds.get(1).getText();//营业期限
      String funds=rightdds.get(2).getText();//注册资本
      String dateOfIssue=rightdds.get(3).getText();//发证日期
      String registrationAuthority=rightdds.get(4).getText();//登记机关
      String address=rightdds.get(5).getText();//企业地址
    }
    String range=driver.findElement(By.xpath("//dl[@class='business-info-bottom']/dd")).getText();
    System.out.println(range);
    driver.quit();
  }

  /**
   * 分析搜索结果
   * 
   * @param page
   * @param next
   */
  private void analysisSearchResult(Page page, CrawlDatums next) {
    ChromeDriver driver = getChromeDriver();
    driver.get(page.url());
    try {
      Thread.sleep(4000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    List<WebElement> webElements = driver.findElements(By.id("enterprise_qixinbao"));
    if (webElements != null && !webElements.isEmpty()) {
      String src = webElements.get(0).getAttribute("src");
      if (StringUtils.isNotBlank(src)) {
        next.add(new CrawlDatum(src));
      }
    }
    driver.quit();
  }

  private ChromeDriver getChromeDriver() {
    System.setProperty("webdriver.chrome.driver",
        "D:\\environment\\tools\\chromeDriver_2.33\\chromedriver.exe");
    DesiredCapabilities dcaps = new DesiredCapabilities(BrowserType.CHROME, "", Platform.ANY);
    // ssl证书支持
    dcaps.setCapability("acceptSslCerts", true);
    // 截屏支持
    dcaps.setCapability("takesScreenshot", true);
    // css搜索支持
    dcaps.setCapability("cssSelectorsEnabled", true);
    // js支持
    dcaps.setJavascriptEnabled(true);
    ChromeOptions chromeOptions = new ChromeOptions();
    // 设置为 headless 模式 （必须）
    // chromeOptions.addArguments("--headless");
    // chromeOptions.addArguments("--disable-gpu");
    // 设置浏览器窗口打开大小 （非必须）
    chromeOptions.addArguments("--window-size=1920,1080");
    // 禁用CHROME正在进行测试的提示信息
    chromeOptions.addArguments(Arrays.asList("disable-infobars"));
    // 将页面打开动作设置为none（执行跳转以后就不管了），默认为normal（CHROME等待页面基本内容加载完毕）
    dcaps.setCapability("pageLoadStrategy", "none");
    dcaps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
    ChromeDriver driver = new ChromeDriver(dcaps);
    return driver;
  }

  public static void main(String[] args) throws Exception {
    BaikeCrawleService crawleService = new BaikeCrawleService();
    List<String> links = new ArrayList<>();
    for (int i = 0; i < 1; i++) {
//      links.add(
//          "https://baike.baidu.com/item/%E5%9B%9B%E5%B7%9D%E5%A5%87%E5%8A%9B%E5%88%B6%E8%8D%AF%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8");
      links.add(
          "https://baike.baidu.com/item/成都知乐科技有限公司");
    }
    crawleService.addSeed(links);

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

