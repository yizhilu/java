package com.hc.proxyPool.common.utils;

import java.util.HashMap;
import java.util.Map;

public class URLAnalysis {
  private Map<String, Object> paramMap = new HashMap<String, Object>();

  public void analysis(String url) {
    paramMap.clear();
    if (!"".equals(url)) {// 如果URL不是空字符串
      url = url.substring(url.indexOf('?') + 1);
      String paramaters[] = url.split("&");
      for (String param : paramaters) {
        String values[] = param.split("=",2);
        if (values.length == 1) {
          paramMap.put(values[0], null);
        } else {
          paramMap.put(values[0], values[1]);
        }
      }
    }
  }

  public Object getParam(String name) {
    return paramMap.get(name);
  }

  public Map<String, Object> getParams() {
    return paramMap;
  }

  public static void main(String[] args) {
//    String test =
//        "http://www.creditchina.gov.cn/api/credit_info_search?keyword=%E5%9B%9B%E5%B7%9D%E7%A7%91%E5%88%9B%E5%8C%BB%E8%8D%AF%E9%9B%86%E5%9B%A2%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&templateId=&page=1&pageSize=10";
    String test1 =
        "http://www.creditchina.gov.cn/api/record_param?encryStr=fWJld259d0Z9cXk=&creditType=8&dataSource=0&pageNum=2&pageSize=10";
    URLAnalysis urlAnalysis = new URLAnalysis();
    urlAnalysis.analysis(test1);
    System.out.println(urlAnalysis.getParams().toString());
  }
}
