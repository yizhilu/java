package com.hc.wechat.service.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hc.wechat.common.enums.PlatFormType;
import com.hc.wechat.common.message.MsgTemplate;
import com.hc.wechat.common.message.WxTemplate;
import com.hc.wechat.common.utils.SHACoder;
import com.hc.wechat.entity.ThirdPartUserEntity;
import com.hc.wechat.entity.UserEntity;
import com.hc.wechat.entity.WeChatCacheEntity;
import com.hc.wechat.pojo.FileUploadPojo;
import com.hc.wechat.service.MessageService;
import com.hc.wechat.service.ThirdPartUserService;
import com.hc.wechat.service.UserService;
import com.hc.wechat.service.WeChatCacheService;
import com.hc.wechat.service.WeChatService;
import com.hc.wechat.service.feign.WeChatApiService;


/**
 * @author 作者 hc
 * @version 创建时间：2017年10月18日 下午1:27:42 类说明
 */
@Service("weChatService")
@SuppressWarnings("unchecked")
public class WeChatServiceImpl implements WeChatService, MessageService {
  private static final Logger LOG = LoggerFactory.getLogger(WeChatServiceImpl.class);
  @Autowired
  private WeChatApiService weChatApiService;
  @Autowired
  private ThirdPartUserService thirdPartUserService;
  @Autowired
  private UserService userService;
  @Autowired
  private WeChatCacheService weChatCacheService;
  /** 微信appid */
  @Value("${weChat.appId}")
  private String WECHAT_APPID;
  /** 微信appsecret */
  @Value("${weChat.secret}")
  private String WECHAT_SECRET;
  /** 服务域名. */
  @Value("${weChat.appDns}")
  private String WECHAT_APPDNS;
  /** 缓存微信全局token */
  @Value("${weChat.cacheTokenType}")
  private String CACHETOKENTYPE;
  /**
   * 保存文件的根路径描述
   */
  @Value("${filePath.imageRoot}")
  private String fileRoot;
  @Autowired
  private RedisTemplate<?, ?> redisTemplate;

  @Override
  public Map<String, Object> getWebPageAccessToken(String appid, String secret, String code) {
    Validate.notBlank(appid, "获取网页access_token时:appid不能为空");
    Validate.notBlank(secret, "获取网页access_token时:secret不能为空");
    Validate.notBlank(code, "获取网页access_token时:code不能为空");

    String accessToken =
        weChatApiService.getWebPageAccessToken(appid, secret, code, "authorization_code");
    LOG.info("getWebPageAccessToken=:" + accessToken);
    Map<String, Object> json = (Map<String, Object>) JSON.parseObject(accessToken, Map.class);
    Validate.isTrue(json.get("errcode") == null, accessToken);

    return json;
  }

  @Override
  public Map<String, Object> getUserInfo(String access_token, String openid, String lang) {
    Validate.notBlank(access_token, "getUserInfo:access_token不能为空");
    Validate.notBlank(openid, "getUserInfo:openid不能为空");
    Validate.notBlank(lang, "getUserInfo:lang不能为空");

    String userInfo = weChatApiService.getUserInfo(access_token, openid, lang);
    LOG.info("getUserInfo=:" + userInfo);
    Map<String, Object> json = (Map<String, Object>) JSON.parseObject(userInfo, Map.class);
    Validate.isTrue(json.get("errcode") == null, userInfo);

    return json;
  }

  @Override
  public Map<String, Object> getInfo(String access_token, String openid) {
    Validate.notBlank(access_token, "getInfo:access_token不能为空");
    Validate.notBlank(openid, "getInfo:openid不能为空");
    // 1.获取用户的普通信息
    String info = weChatApiService.getInfo(access_token, openid, "zh_CN");
    LOG.info("getInfo=:" + info);
    Map<String, Object> json = (Map<String, Object>) JSON.parseObject(info, Map.class);
    // 2.如果返回的json 中含有 errcode则error
    Validate.isTrue(json.get("errcode") == null, info);

    return json;
  }



  @Override
  public Map<String, Object> getTicket(String access_token) {
    Validate.notBlank(access_token, "getTicket:access_token不能为空");
    String ticket = weChatApiService.getTicket(access_token, "jsapi");
    LOG.info("getTicket=:" + ticket);
    Map<String, Object> json = (Map<String, Object>) JSON.parseObject(ticket, Map.class);
    if (!"ok".equals((String) json.get("errmsg"))) {
      throw new IllegalArgumentException(ticket);
    }
    return json;
  }

  @Override
  public Map<String, Object> getToken(String appid, String secret) {
    Validate.notBlank(appid, "getToken:appid不能为空");
    Validate.notBlank(secret, "getToken:secret不能为空");
    String token = weChatApiService.getToken("client_credential", appid, secret);
    LOG.info("getToken=:" + token);
    Map<String, Object> json = (Map<String, Object>) JSON.parseObject(token, Map.class);
    Validate.isTrue(json.get("errcode") == null, token);

    return json;
  }

  private boolean isNeedGetJsApiTicket(WeChatCacheEntity jsApiTicket) {
    boolean isNeedGetJsApiTicket = false;
    if (jsApiTicket == null) {
      isNeedGetJsApiTicket = true;
    } else {
      long distanceTime = new Date().getTime() - jsApiTicket.getCreateTime().getTime();
      if (distanceTime > (jsApiTicket.getExpires() - 200) * 1000) {
        isNeedGetJsApiTicket = true;
      } else {
        String ticket = (String) jsApiTicket.getJsonObject("ticket");
        if (StringUtils.isNotBlank(ticket)) {
          isNeedGetJsApiTicket = false;
        } else {
          isNeedGetJsApiTicket = true;
        }
      }
    }
    return isNeedGetJsApiTicket;
  }

  @Override
  @Transactional
  public String getTicket() {
    // 1.如果是存储在redis 则先去redis 获取 如果有 且没有过期 直接返回，否则重新获取
    // 2.如果是存储在数据库 则先去数据库 获取 如果有 且没有过期 直接返回，否则重新获取
    if ("redis".equals(CACHETOKENTYPE)) {
      ValueOperations<String, WeChatCacheEntity> ops =
          (ValueOperations<String, WeChatCacheEntity>) this.redisTemplate.opsForValue();
      WeChatCacheEntity jsApiTicket = (WeChatCacheEntity) ops.get(WeChatCacheEntity.JSAPITICKET);
      if (!isNeedGetJsApiTicket(jsApiTicket)) {
        return (String) jsApiTicket.getJsonObject("ticket");
      } else {
        Map<String, Object> jsApiTicketMap = null;
        String access_token = getAccessToken(WECHAT_APPID, WECHAT_SECRET);
        jsApiTicketMap = getTicket(access_token);
        jsApiTicket = new WeChatCacheEntity();
        String ticket = (String) jsApiTicketMap.get("ticket");
        int expires_in = (int) jsApiTicketMap.get("expires_in");
        jsApiTicket.setName(WeChatCacheEntity.JSAPITICKET);
        jsApiTicket.setJson(JSONArray.toJSONString(jsApiTicketMap).toString());
        jsApiTicket.setExpires(expires_in);
        jsApiTicket.setCreateTime(new Date());
        ops.set(WeChatCacheEntity.JSAPITICKET, jsApiTicket, 7000, TimeUnit.SECONDS);
        return ticket;
      }
    } else {
      WeChatCacheEntity jsApiTicket = weChatCacheService.findJsApiTicket();
      if (!isNeedGetJsApiTicket(jsApiTicket)) {
        return (String) jsApiTicket.getJsonObject("ticket");
      } else {
        Map<String, Object> jsApiTicketMap = null;
        String access_token = getAccessToken(WECHAT_APPID, WECHAT_SECRET);
        jsApiTicketMap = getTicket(access_token);
        jsApiTicket = new WeChatCacheEntity();
        String ticket = (String) jsApiTicketMap.get("ticket");
        int expires_in = (int) jsApiTicketMap.get("expires_in");
        jsApiTicket.setName(WeChatCacheEntity.JSAPITICKET);
        jsApiTicket.setJson(JSONArray.toJSONString(jsApiTicketMap).toString());
        jsApiTicket.setExpires(expires_in);
        jsApiTicket.setCreateTime(new Date());
        weChatCacheService.updateJsApiTicket(jsApiTicket);
        return ticket;
      }
    }
  }

  private boolean isNeedGetTocken(WeChatCacheEntity accessToken) {
    boolean isNeedGetTocken = false;
    // 数据库中没有记录或者是过期时间大于7000，需要重新获取token
    if (accessToken == null) {
      isNeedGetTocken = true;
    } else {
      long distanceTime = new Date().getTime() - accessToken.getCreateTime().getTime();
      if (distanceTime > (accessToken.getExpires() - 200) * 1000) {
        isNeedGetTocken = true;
      } else {
        String token = (String) accessToken.getJsonObject("access_token");
        if (StringUtils.isNotBlank(token)) {
          isNeedGetTocken = false;
        } else {
          isNeedGetTocken = true;
        }
      }
    }
    return isNeedGetTocken;
  }

  @Override
  @Transactional
  public String getAccessToken(String appid, String secret) {
    // 1.如果是存储在redis 则先去redis 获取 如果有 且没有过期 直接返回，否则重新获取
    // 2.如果是存储在数据库 则先去数据库 获取 如果有 且没有过期 直接返回，否则重新获取
    if ("redis".equals(CACHETOKENTYPE)) {
      ValueOperations<String, WeChatCacheEntity> ops =
          (ValueOperations<String, WeChatCacheEntity>) this.redisTemplate.opsForValue();
      WeChatCacheEntity accessToken = (WeChatCacheEntity) ops.get(WeChatCacheEntity.ACCESSTOKEN);
      if (!isNeedGetTocken(accessToken)) {
        return (String) accessToken.getJsonObject("access_token");
      } else {
        // 重新获取token后存入redis
        Map<String, Object> accessTokenMap = getToken(appid, secret);
        accessToken = new WeChatCacheEntity();
        String token = (String) accessTokenMap.get("access_token");
        int expires_in = (int) accessTokenMap.get("expires_in");
        accessToken.setName(WeChatCacheEntity.ACCESSTOKEN);
        accessToken.setJson(JSONArray.toJSONString(accessTokenMap).toString());
        accessToken.setExpires(expires_in);
        accessToken.setCreateTime(new Date());
        ops.set(WeChatCacheEntity.ACCESSTOKEN, accessToken, 7000, TimeUnit.SECONDS);
        return token;
      }
    } else {
      WeChatCacheEntity accessToken = weChatCacheService.findAccessToken();
      if (!isNeedGetTocken(accessToken)) {
        return (String) accessToken.getJsonObject("access_token");
      } else {
        // 重新获取token后存入数据库
        Map<String, Object> accessTokenMap = getToken(appid, secret);
        accessToken = new WeChatCacheEntity();
        String token = (String) accessTokenMap.get("access_token");
        int expires_in = (int) accessTokenMap.get("expires_in");
        accessToken.setName(WeChatCacheEntity.ACCESSTOKEN);
        accessToken.setJson(JSONArray.toJSONString(accessTokenMap).toString());
        accessToken.setExpires(expires_in);
        accessToken.setCreateTime(new Date());
        weChatCacheService.updateAccessToken(accessToken);
        return token;
      }
    }
  }

  @Override
  @Transactional
  public ThirdPartUserEntity getThirdPartUserEntity(String access_token, String openid) {
    // 1.获取用户基本信息 用openid
    Map<String, Object> userInfo = getUserInfo(access_token, openid, "zh_CN");
    // 2.从数据库获取第三方用户信息来判断是否应该创建 或者 更新第三方账号的信息
    ThirdPartUserEntity thirdPartUser = (ThirdPartUserEntity) thirdPartUserService
        .findByOpenIdAndPlatFormType(openid, PlatFormType.PLAT_FORM_TYPE_WEIXIN);
    // 3.如果 thirdPartUser==null 则表示 该openid的用户不存在于系统，则创建一个新的PlatFormType类型为微信的第三方用户
    if (thirdPartUser == null) {
      thirdPartUser = createWeChatAccount(userInfo);
    } else {
      // 5.如果已存在 则更新PlatFormType类型为微信的第三方用户的信息
      updateUserAndThirdPartUserInfo(thirdPartUser, userInfo);
    }
    return thirdPartUser;
  }

  /**
   * 创建PlatFormType.PLAT_FORM_TYPE_WEIXIN 的三方用户
   * 
   * @param unionId
   * @param weixinInfoMap 微信获取的用户信息
   * @return
   */
  private ThirdPartUserEntity createWeChatAccount(Map<?, ?> weixinInfoMap) {
    ThirdPartUserEntity thirdPartUser = null;
    String nickName = (String) weixinInfoMap.get("nickname");
    int sex = (Integer) weixinInfoMap.get("sex");
    String cover = (String) weixinInfoMap.get("headimgurl");
    String str_city = (String) weixinInfoMap.get("city");
    String unionId = (String) weixinInfoMap.get("unionid");
    String openid = (String) weixinInfoMap.get("openid");
    if (StringUtils.isNotEmpty(nickName)) {
      thirdPartUser = new ThirdPartUserEntity();
      thirdPartUser.setPlatFormType(PlatFormType.PLAT_FORM_TYPE_WEIXIN);
      thirdPartUser.setNickName(nickName);
      thirdPartUser.setSex(sex);
      thirdPartUser.setCover(cover);
      thirdPartUser.setCity(str_city);
      thirdPartUser.setCreateDate(new Date());
      thirdPartUser.setOpenId(openid);
      thirdPartUser.setUnionId(unionId);
      thirdPartUserService.weChatCreate(thirdPartUser);
    }
    return thirdPartUser;
  }

  /**
   * 更新第三方账号信息 和user信息
   * 
   * @param thirdPartUser
   * @param weixinInfoMap
   */
  private void updateUserAndThirdPartUserInfo(ThirdPartUserEntity thirdPartUser,
      Map<?, ?> weixinInfoMap) {
    String nickName = (String) weixinInfoMap.get("nickname");
    int sex = (Integer) weixinInfoMap.get("sex");
    String cover = (String) weixinInfoMap.get("headimgurl");
    String str_city = (String) weixinInfoMap.get("city");
    String openid = (String) weixinInfoMap.get("openid");
    String unionId = (String) weixinInfoMap.get("unionid");
    if (StringUtils.isNotEmpty(nickName)) {
      Date curDate = Calendar.getInstance().getTime();
      thirdPartUser.setNickName(nickName);
      thirdPartUser.setSex(sex);
      thirdPartUser.setCover(cover);
      thirdPartUser.setCity(str_city);
      thirdPartUser.setLastLoginDate(curDate);
      thirdPartUser.setOpenId(openid);
      thirdPartUser.setUnionId(unionId);
      thirdPartUserService.weChatModify(thirdPartUser);
      UserEntity user = (UserEntity) thirdPartUser.getUser();
      user.setCity(thirdPartUser.getCity());
      user.setNickName(thirdPartUser.getNickName());
      user.setCover(thirdPartUser.getCover());
      user.setSex(thirdPartUser.getSex());
      user.setLastLoginDate(curDate);
      userService.modify(user);
    }
  }

  @Override
  public Map<String, Object> getJsSignature(String currentUrl) {
    if (StringUtils.isBlank(currentUrl)) {
      return null;
    }
    // 获取js的签名相关信息
    Map<String, Object> result = null;
    // 获取ticket
    String ticket = getTicket();
    // 随机字符串
    String noncestr = UUID.randomUUID().toString();
    String timestamp = String.valueOf(new Date().getTime());
    timestamp = timestamp.substring(0, 10);
    String signatureValue = "jsapi_ticket=" + ticket + "&noncestr=" + noncestr + "&timestamp="
        + timestamp + "&url=" + currentUrl;
    LOG.info("signatureValue=" + signatureValue);
    try {
      String signature = SHACoder.encryptShaHex(signatureValue, null);
      result = new HashMap<String, Object>();
      result.put("appid", WECHAT_APPID);
      result.put("noncestr", noncestr);
      result.put("timestamp", timestamp);
      result.put("url", currentUrl);
      result.put("signature", signature);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
    return result;
  }

  @Override
  public FileUploadPojo downloadWeChatImage(String serverId) {
    Validate.notBlank(serverId, "serverId不为空");
    InputStream inputStream = getFileInputStream(serverId);
    String filePath = InputStreamConverImage(inputStream);
    Validate.notBlank(filePath, "从微信下载图片失败");
    FileUploadPojo file = new FileUploadPojo();
    file.setCreateTime(new Date());
    file.setRelativePath(filePath);
    return file;
  }

  /**
   * 从微信服务器获取照片存到本地服务器.
   * 
   * @param request
   * @return
   */
  private InputStream getFileInputStream(String serverId) {
    InputStream is = null;
    String accessToken = getAccessToken(WECHAT_APPID, WECHAT_SECRET);
    Validate.notBlank(accessToken, "无法获取token");
    String requestUrl =
        "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
    requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", serverId);
    try {
      URL urlGet = new URL(requestUrl);
      HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
      http.setRequestMethod("GET"); // 必须是get方式请求
      http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      http.setDoOutput(true);
      http.setDoInput(true);
      http.connect();
      // 获取文件转化为byte流
      is = http.getInputStream();

    } catch (Exception e) {
      LOG.error(e.getMessage());
      Validate.isTrue(false, "获取微信服务器临时素材失败");
    }
    return is;
  }

  private String InputStreamConverImage(InputStream inputStream) {
    Validate.notNull(inputStream, "inputStream 不能为空");
    Date nowDate = new Date();
    String folderName = new SimpleDateFormat("yyyyMMdd").format(nowDate);
    String renameImage = UUID.randomUUID().toString();
    String relativePath = "/" + folderName + "/" + (new Random().nextInt(100) % 10);
    File folderFile = new File(fileRoot + relativePath);
    if (!folderFile.exists()) {
      folderFile.mkdirs();
    }
    // 以下就是这个即将创建的文件的完整路径了
    relativePath = relativePath + "/" + renameImage + ".jpg";
    String fullArmPath = fileRoot + "/" + relativePath;
    byte[] data = new byte[1024 * 1024];
    int len = 0;
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(fullArmPath);
      while ((len = inputStream.read(data)) != -1) {
        fileOutputStream.write(data, 0, len);
      }
      return relativePath;
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          LOG.error(e.getMessage(), e);
        }
      }
      if (fileOutputStream != null) {
        try {
          fileOutputStream.close();
        } catch (IOException e) {
          LOG.error(e.getMessage(), e);
        }
      }
    }
    return null;
  }

  @Override
  public String sendMessage(MsgTemplate msg) {
    LOG.info("微信发送消息开始=====================");
    WxTemplate wxTemplate = (WxTemplate) msg;
    try {
      String token = null;
      token = getAccessToken(WECHAT_APPID, WECHAT_SECRET);
      String str_json = weChatApiService
          .sendMsg(JSON.parseObject(wxTemplate.getWxContent(), JSONObject.class), token);
      LOG.info("微信消息=：" + str_json);
      return str_json;
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return "";
    }
  }
}
