package com.hc.crypto.advice;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.crypto.annotation.ApiEncryptAnno;
import com.hc.crypto.common.utils.crypto.aesrsa.AESCoder;
import com.hc.crypto.common.utils.crypto.aesrsa.EncryUtil;
import com.hc.crypto.common.utils.crypto.aesrsa.RSA;
import com.hc.crypto.common.utils.crypto.aesrsa.SecureRandomUtil;
import com.hc.crypto.configuration.CryptoProperties;

/**
 * 请求响应处理类<br>
 * 
 * 对加了@ApiEncryptAnno的方法的数据进行加密操作
 * <pre> 
 *  1. sever生成AES密钥(aesKey)
 *  2. sever使用client的RSA公钥对aesKey进行加密得到(encryptkey)
 *  3. sever使用自己的RSA私钥对请求明文数据(json)进行数字签名
 *  4. sever将签名加入到请求参数中，然后转换为jsonStr格式
 *  4. sever使用aesKey对json数据进行加密得到密文
 *  5. encryptkey和密文返回给客户端
 *  =============================================
 *  1. 对sever返回的数据，进行验签encryptkey 为sever用client公钥加密后的AES密钥 
 *  2. 使用client私钥对encryptkey进行解密，得到aeskey 
 *  3. 使用aesKey对json数据进行解密得到明文(data) 
 * <pre>
 * @author hc
 * 
 *
 */
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  private Logger logger = LoggerFactory.getLogger(EncryptResponseBodyAdvice.class);

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private CryptoProperties cryptoProperties;

  private static ThreadLocal<Boolean> encryptLocal = new ThreadLocal<Boolean>();

  public static void setEncryptStatus(boolean status) {
    encryptLocal.set(status);
  }

  private String getMethodUrl(Method method) {
    StringBuilder uriSb = new StringBuilder();
    // 获取类上的url路径
    Class<?> clazz = method.getDeclaringClass();
    RequestMapping classRequestMapping = AnnotationUtils.getAnnotation(clazz, RequestMapping.class);
    if (classRequestMapping != null) {
      String classUrl = classRequestMapping.value()[0];
      uriSb.append(classUrl);
    }
    String methodType = "";
    GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
    PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
    RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
    PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
    DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
    if (getMapping != null) {
      methodType = RequestMethod.GET.name();
      uriSb.append(getMapping.value()[0]);

    } else if (postMapping != null) {
      methodType = RequestMethod.POST.name();
      uriSb.append(postMapping.value()[0]);

    } else if (putMapping != null) {
      methodType = RequestMethod.PUT.name();
      uriSb.append(putMapping.value()[0]);

    } else if (deleteMapping != null) {
      methodType = RequestMethod.DELETE.name();
      uriSb.append(deleteMapping.value()[0]);

    } else if (requestMapping != null) {
      RequestMethod m = requestMapping.method()[0];
      methodType = m.name() + ":";
      uriSb.append(requestMapping.value()[0]);
    }
    return methodType + uriSb.toString();
  }

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    if (returnType.getMethod().isAnnotationPresent(ApiEncryptAnno.class)
        && !cryptoProperties.isDebug()) {
      List<String> uriIgnoreList = cryptoProperties.getApiEncryptUriIgnoreList();
      if (uriIgnoreList != null && !uriIgnoreList.isEmpty()) {
        Method method = returnType.getMethod();
        // 获取方法上ApiEncryptAnno中的url 最高优先级
        ApiEncryptAnno apiEncryptAnno =
            AnnotationUtils.findAnnotation(method, ApiEncryptAnno.class);
        String value = apiEncryptAnno.value();
        // ApiEncryptAnno中的url 为空那么 去寻找RequestMapping中的url作为判断依据
        if (StringUtils.isBlank(value)) {
          value = getMethodUrl(method);
        }
        if (uriIgnoreList.contains(value)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
    // 可以通过调用EncryptResponseBodyAdvice.setEncryptStatus(false);来动态设置不加密操作
    Boolean status = encryptLocal.get();
    if (status != null && status == false) {
      encryptLocal.remove();
      return body;
    }
    long startTime = System.currentTimeMillis();
    boolean encrypt = true;
    if (encrypt) {
      try {
        String selectedContent = selectedContentType.toString();
        if (selectedContent.contains(MediaType.APPLICATION_JSON_VALUE)) {
          String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
          JSONObject json = JSONObject.parseObject(content);
          JSONObject data = json.getJSONObject("data");
          if (data == null) {
            return body;
          }
          // String dataStr =
          // objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
          // 1. sever生成AES密钥(aesKey)
          String aesKey = SecureRandomUtil.getRandom(16);
          // 2. sever使用client的RSA公钥对aesKey进行加密(encryptkey)
          String encryptkey = RSA.encrypt(aesKey, cryptoProperties.getClientPublicKey());
          // 3. sever使用自己的RSA私钥对请求明文数据(params)进行数字签名
          String sign = EncryUtil.handleRSA(data, cryptoProperties.getServerPrivateKey());
          data.put("sign", sign);
          // 4. 将签名加入到请求参数中，然后转换为json格式
          String jsonParams = JSON.toJSONString(data);
          // 5.对明文进行加密
          String result = AESCoder.encryptToBase64(jsonParams, aesKey);
          // // 3.对明文进行加密
          // String result = AESCoder.encryptToBase64(dataStr, aesKey);
          long endTime = System.currentTimeMillis();
          logger.debug("Encrypt Time:" + (endTime - startTime));
          HttpHeaders httpHeaders = response.getHeaders();
          httpHeaders.add("encryptkey", encryptkey);
          json.put("data", result);
          return json;
        }
      } catch (Exception e) {
        logger.error("加密数据异常", e);
      }
    }

    return body;
  }

}
