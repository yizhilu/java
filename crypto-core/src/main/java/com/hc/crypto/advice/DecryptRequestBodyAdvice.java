package com.hc.crypto.advice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.hc.crypto.annotation.ApiDecryptAnno;
import com.hc.crypto.common.utils.crypto.aesrsa.AESCoder;
import com.hc.crypto.common.utils.crypto.aesrsa.EncryUtil;
import com.hc.crypto.common.utils.crypto.aesrsa.RSA;
import com.hc.crypto.configuration.CryptoProperties;

/**
 * 请求数据接收处理类<br>
 * 
 * 对加了@ApiDecryptAnno的方法的数据进行解密操作<br>
 * 
 * 只对@RequestBody参数有效 <br>
 * <pre> 
 *  1. client随机生成AES密钥，用sever公钥对AES密钥进行加密得到encryptkey
 *  2. client使用自己的RSA私钥对请求明文数据(json)进行数字签名
 *  3. client将签名加入到请求参数中，然后转换为jsonStr格式
 *  4. client使用aesKey对json数据进行加密得到密文
 *  5. encryptkey和密文提交到服务端
 *  =============================================
 *  1. 对client请求的数据，进行验签encryptkey 为client用sever公钥加密后的AES密钥 
 *  2. 使用sever私钥对encryptkey进行解密，得到aeskey 
 *  3. 使用aesKey对json数据进行解密得到明文(data) 
 * <pre>
 * @author hc
 */
@ControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

  private Logger logger = LoggerFactory.getLogger(DecryptRequestBodyAdvice.class);

  @Autowired
  private CryptoProperties cryptoProperties;
  public static final String CHARSET = "utf-8";

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
  public boolean supports(MethodParameter methodParameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    // 如果方法上含有@ApiDecryptAnno 解密注解 且不是bug模式
    if (methodParameter.getMethod().isAnnotationPresent(ApiDecryptAnno.class)
        && !cryptoProperties.isDebug()) {
      List<String> uriIgnoreList = cryptoProperties.getApiDecyptUriIgnoreList();
      if (uriIgnoreList != null && !uriIgnoreList.isEmpty()) {
        Method method = methodParameter.getMethod();
        // 获取方法上ApiDecryptAnno中的url 最高优先级
        ApiDecryptAnno apiDecryptAnno =
            AnnotationUtils.findAnnotation(method, ApiDecryptAnno.class);
        String value = apiDecryptAnno.value();
        // ApiDecryptAnno中的url 为空那么 去寻找RequestMapping中的url作为判断依据
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
  public Object handleEmptyBody(Object body, HttpInputMessage inputMessage,
      MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  @Override
  public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
      Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
    try {
      return new DecryptHttpInputMessage(inputMessage, cryptoProperties.getAccesskey(),
          cryptoProperties.getClientPublicKey(), cryptoProperties.getServerPrivateKey());
    } catch (Exception e) {
      logger.error("数据解密失败", e);
    }
    return inputMessage;
  }

  @Override
  public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
      Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }
}


class DecryptHttpInputMessage implements HttpInputMessage {
  private Logger logger = LoggerFactory.getLogger(DecryptHttpInputMessage.class);
  private HttpHeaders headers;
  private InputStream body;

  /**
   * 
   * @param inputMessage
   * @param accesskey 固定AES密钥
   * @param clientPublicKey RSA客户端公钥
   * @param serverPrivateKey RSA服务器私钥
   * @throws Exception
   */
  public DecryptHttpInputMessage(HttpInputMessage inputMessage, String accesskey,
      String clientPublicKey, String serverPrivateKey) throws Exception {
    this.headers = inputMessage.getHeaders();
    String requestInfo = IOUtils.toString(inputMessage.getBody(), DecryptRequestBodyAdvice.CHARSET);
    long startTime = System.currentTimeMillis();
    // JSON 数据格式的不进行解密操作
    String decryptBody = "";
    if (requestInfo.startsWith("{")) {
      decryptBody = requestInfo;
    } else {
      List<String> encryptkeys = this.headers.getValuesAsList("encryptkey");
      if (encryptkeys != null && !encryptkeys.isEmpty()) {
        String encryptkey = encryptkeys.get(0);
        if (StringUtils.isNotBlank(encryptkey)) {
          // 1. 对客户端请求的数据，进行验签encryptkey 为客户端用服务端公钥加密后的AES密钥
          boolean passSign = EncryUtil.checkDecryptAndSign(requestInfo, encryptkey, clientPublicKey,
              serverPrivateKey);
          if (passSign) {
            // 2, 使用sever私钥对encryptkey进行解密，得到aeskey
            String aeskey = RSA.decrypt(encryptkey, serverPrivateKey);
            // 3，使用aesKey对json数据进行解密得到明文(data)
            decryptBody = AESCoder.decryptFromBase64(requestInfo, aeskey);
          }
        }
      }
    }
    long endTime = System.currentTimeMillis();
    logger.debug("Decrypt Time:" + (endTime - startTime));
    this.body = IOUtils.toInputStream(decryptBody, DecryptRequestBodyAdvice.CHARSET);
  }

  @Override
  public InputStream getBody() throws IOException {
    return body;
  }

  @Override
  public HttpHeaders getHeaders() {
    return headers;
  }
}
