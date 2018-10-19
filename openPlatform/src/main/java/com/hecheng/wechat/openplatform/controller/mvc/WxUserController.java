package com.hecheng.wechat.openplatform.controller.mvc;

import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hecheng.wechat.openplatform.annotation.ObjectConvertAnno;
import com.hecheng.wechat.openplatform.controller.BaseController;
import com.hecheng.wechat.openplatform.controller.model.ResponseCode;
import com.hecheng.wechat.openplatform.controller.model.ResponseModel;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.entity.ValidCodeEntity;
import com.hecheng.wechat.openplatform.service.UserContactService;
import com.hecheng.wechat.openplatform.service.ValidCodeService;
import com.hecheng.wechat.openplatform.service.WeChatService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author 作者 xy
 */
@Api(value = "微信 用户controller")
@Controller
@RequestMapping("/wx/user")
public class WxUserController extends BaseController {


  @Autowired
  private UserContactService userContactService;
  @Autowired
  private ValidCodeService validCodeService;
  @Autowired
  private WeChatService weChatService;
  /** 微信appid */
  @Value("${weChat.appId}")
  private String WECHAT_APPID;
  /** 微信appsecret */
  @Value("${weChat.secret}")
  private String WECHAT_SECRET;

  /**
   * 跳转到电话修改页面 <br>
   * <pre>
   * 这个方法 有两种途径进入
   * 1.点击菜单检查用户是否绑定电话号码，这种进入方式在绑定结束后直接跳转到首页
   * 2.扫码绑定设备时检查用户是否绑定电话号码，这种进入方式需要判断用户是否关注公众号。
   * 如果没有关注，那么跳转到关注页面，否则进行对应设备的绑定操作
   *</pre>
   * 
   * @param user
   * @param target 为null 是为第一种方式进入，为“bindGateway”时 是绑定智能网关，为“bindSocket”时 是绑定智能开关
   * @param mac 对应设备的mac地址
   * @return
   */
  @RequestMapping(value = "/gotoBindTelephone", method = RequestMethod.GET)
  @ApiOperation(value = "跳转到绑定电话页面", notes = "跳转到绑定电话页面")
  public ModelAndView gotoBindTelephone(@ApiIgnore @ObjectConvertAnno UserEntity user,
      String target, String mac) {

    try {
      Validate.notNull(user, "你还未登录，请登录");
      ModelAndView mav = new ModelAndView();
      // 跳转路径
      mav.setViewName("/weChat/views/user/bindTelephone");
      mav.addObject("user", user);
      mav.addObject("target", target);
      mav.addObject("mac", mac);
      return mav;
    } catch (Exception e) {
      return this.buildHttpReslutForException(e, null);
    }
  }


  @RequestMapping(value = "/gotoUpdateTelephone", method = RequestMethod.GET)
  @ApiOperation(value = "跳转到修改电话页面", notes = "跳转到修改电话页面")
  public ModelAndView gotoUpdateTelephone(@ApiIgnore @ObjectConvertAnno UserEntity user) {

    try {
      Validate.notNull(user, "你还未登录，请登录");
      ModelAndView mav = new ModelAndView();
      // 跳转路径
      mav.setViewName("/weChat/views/user/updateTelephone");
      mav.addObject("user", user);
      return mav;
    } catch (Exception e) {
      return this.buildHttpReslutForException(e, null);
    }
  }

  /**
   * 修改联系电话 <br>
   * 
   * @param user
   * @param phone
   * @param validCode
   * @return
   */
  @RequestMapping(value = "/updateTelephone", method = RequestMethod.POST)
  @ResponseBody
  public ResponseModel updateTelephone(@ApiIgnore @ObjectConvertAnno UserEntity user,
      @ApiParam("用户phone") @RequestParam String phone, @RequestParam String validCode) {
    try {
      Validate.notNull(phone, "请输入手机号码");
      Validate.notNull(validCode, "请输入正确的短信验证码");
      String phoneRegex = "^1[345789]\\d{9}$";
      Validate.isTrue(phone.matches(phoneRegex), "请输入正确的手机号码");
      ValidCodeEntity oldValidCode = validCodeService.getTheLatestValidCode(phone);
      Validate.notNull(oldValidCode, "请输入正确的短信验证码");
      Long interval = oldValidCode.getIntervalTime();
      Validate.isTrue(interval < 120, "请输入正确的短信验证码");
      Validate.isTrue(validCode.equals(oldValidCode.getParameter()), "请输入正确的短信验证码");
      userContactService.updateUserContact(user.getId(), phone);
      // 首页菜单路径
      String gotoUrl = "/wx/gateway/findAllGateway";
      return new ResponseModel(new Date().getTime(), gotoUrl, ResponseCode._200, null);
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }

  }


  /**
   * 发送验证码
   * 
   * @param phoneNumber
   * @return
   */
  @RequestMapping(value = "/sendValidCodeValue", method = RequestMethod.GET)
  @ResponseBody
  public ResponseModel sendValidCodeValue(String phoneNumber) {
    try {
      ValidCodeEntity validCode = validCodeService.sendValidCodeValue(phoneNumber);
      Validate.notNull(validCode, "120秒内不能重复获取");
      return this.buildHttpReslut();
    } catch (Exception e) {
      return this.buildHttpReslutForException(e);
    }
  }
}


