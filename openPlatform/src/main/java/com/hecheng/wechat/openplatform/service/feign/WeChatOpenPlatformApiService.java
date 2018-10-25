package com.hecheng.wechat.openplatform.service.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

/**
 * 获取微信开放平台相关参数接口
 * 
 * @author hc
 *
 */
@FeignClient(url = "https://api.weixin.qq.com", name = "weChatOpenPlatform")
public interface WeChatOpenPlatformApiService {
  /**
   * 获取component_access_token,第三方平台component_access_token是第三方平台的下文中接口的调用凭据，也叫做令牌（component_access_token）。
   * 每个令牌是存在有效期（2小时）的，且令牌的调用不是无限制的，请第三方平台做好令牌的管理，在令牌快过期时（比如1小时50分）再进行刷新<br>
   * <pre>
   *  第三方平台通过自己的component_appid和component_appsecret（即在微信开放平台管理中心的第三方平台详情页中的AppID和AppSecret），
   *  以及component_verify_ticket（每10分钟推送一次的安全ticket）来获取自己的接口调用凭据（component_access_token
   * </pre>
   * 
   * @param postData <br>
   *        <pre>{ "component_appid":"appid_value" , 
   *               "component_appsecret":"appsecret_value",
   *               "component_verify_ticket": "ticket_value"
   *           }
   *component_appid 第三方平台appid 
   *component_appsecret 第三方平台appsecret 
   *component_verify_ticket 微信后台推送的ticket，此ticket会定时推送</pre>
   * @return 返回结果示例
   *         {"component_access_token":"61W3mEpU66027wgNZ_MhGHNQDHnFATkDa9-2llqrMBjUwxRSNPbVsMmyD-yq8wZETSoE5NQgecigDrSHkPtIYA",
   *         "expires_in":7200}
   */
  @RequestMapping(value = "/cgi-bin/component/api_component_token", method = RequestMethod.POST)
  public String getComponentAccessToken(@RequestBody JSONObject postData);

  /**
   * 获取pre_auth_code,该API用于获取预授权码。预授权码用于公众号或小程序授权时的第三方平台方安全验证<br>
   * <pre>
   * </pre>
   * 
   * @param postData<br>
   *        <pre>{
   *               "component_appid":"appid_value" 
   *             }
   * component_appid 第三方平台方appid
   * component_access_token 
   *        </pre>
   *
   * @return 返回结果示例
   *         {"pre_auth_code":"Cx_Dk6qiBE0Dmx4EmlT3oRfArPvwSQ-oa3NL_fwHM7VI08r52wazoZX2Rhpz1dEw","expires_in":600}
   */
  @RequestMapping(value = "/cgi-bin/component/api_create_preauthcode?component_access_token={component_access_token}", method = RequestMethod.POST)
  public String getPreAuthCode(@RequestBody JSONObject postData,
      @PathVariable("component_access_token") String component_access_token);

  /**
   * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
   * 
   * @param postData<br>
   *        <pre>
   *        { "component_appid":"appid_value" } 
   *component_appid 
   *authorization_code  授权code,会在授权成功时返回给第三方平台，详见第三方平台授权流程说明 component_access_token
   *        </pre>
   * @return
   */
  @RequestMapping(value = "/cgi-bin/component/api_query_auth?component_access_token={component_access_token}", method = RequestMethod.POST)
  public String getApiQueryAuth(@RequestBody JSONObject postData,
      @PathVariable("component_access_token") String component_access_token);

  /**
   * 获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
   * 
   * @param postData { "component_appid":"appid_value", "authorizer_appid":"auth_appid_value",
   *        "authorizer_refresh_token":"refresh_token_value", }
   * @param component_access_token
   * @return
   */
  @RequestMapping(value = "/cgi-bin/component/api_authorizer_token?component_access_token={component_access_token}", method = RequestMethod.POST)
  public String getApiAuthorizerToken(@RequestBody JSONObject postData,
      @PathVariable("component_access_token") String component_access_token);

  /**
   * 客服接口-发消息
   * 
   * @param context
   * @param access_token
   * @return
   */
  @RequestMapping(value = "/cgi-bin/message/custom/send?access_token={access_token}", method = RequestMethod.POST)
  public String customSendMessage(@RequestBody JSONObject context,
      @PathVariable("access_token") String access_token);
}
