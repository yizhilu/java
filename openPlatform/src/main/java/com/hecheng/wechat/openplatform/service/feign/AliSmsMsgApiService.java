package com.hecheng.wechat.openplatform.service.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient(url = "http://ali-sms.showapi.com", name = "aliManagerMsg")
public interface AliSmsMsgApiService {

  /**
   * 发送短信
   * 
   * @param content 可选,对应模板中短信的参数,json格式,如果短信内容中,没有需要替换的参数,此处可以不传值
   * @param mobile 必选,接收短信发送的电话号码,11位的电话号码
   * @param tNum 必选,模板编号,创建模板时返回的tNum,注意只有审核通过的模板才可以发(阿里短信接口内置7个通用模板)
   * @return
   */
  @RequestMapping(value = "/sendSms", method = RequestMethod.GET)
  public String sendSmsMsg(@RequestHeader("Authorization") String authorization,
      @RequestParam("content") String content, @RequestParam("mobile") String mobile,
      @RequestParam("tNum") String tNum);

}
