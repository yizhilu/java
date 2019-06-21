package com.hc.security.service.security.jwt;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.security.controller.model.ResponseCode;
import com.hc.security.controller.model.ResponseModel;

// 没有权限时的处理
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

  private static final long serialVersionUID = -8970718410437077606L;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    // 当用户尝试访问安全的REST资源而不提供任何资源凭据时，将调用此方法
    // 我们应该发送401 Unauthorized响应，因为没有重定向的“登录页面”
    ResponseModel model =
        new ResponseModel(new Date().getTime(), null, ResponseCode._401, "Unauthorized");
    response.setContentType("application/json");
    response.getWriter().write(new ObjectMapper().writeValueAsString(model));
  }
}
