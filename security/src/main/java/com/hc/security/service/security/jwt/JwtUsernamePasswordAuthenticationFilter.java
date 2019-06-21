package com.hc.security.service.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.security.controller.model.ResponseCode;
import com.hc.security.controller.model.ResponseModel;
import com.hc.security.service.security.jwt.model.JwtAuthenticationRequest;
import com.hc.security.service.security.jwt.model.JwtAuthenticationResponse;
import com.hc.security.service.security.jwt.utils.JwtTokenUtil;

// @Component
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  // @Autowired
  private final JwtTokenUtil jwtTokenUtil;

  public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
      JwtTokenUtil jwtTokenUtil) {
    this.setAuthenticationManager(authenticationManager);
    this.jwtTokenUtil = jwtTokenUtil;
    super.setFilterProcessesUrl("/api/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    // 从输入流中获取到登录的信息
    try {
      JwtAuthenticationRequest loginUser =
          new ObjectMapper().readValue(request.getInputStream(), JwtAuthenticationRequest.class);

      return this.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
          loginUser.getUsername(), loginUser.getPassword(), new ArrayList<>()));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  // 成功验证后调用的方法
  // 如果验证成功，就生成token并返回
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");
    UserDetails jwtUser = (UserDetails) authResult.getPrincipal();

    logger.debug("jwtUser: {}", jwtUser.toString());

    String token = jwtTokenUtil.generateToken(jwtUser);
    ResponseModel model = new ResponseModel(new JwtAuthenticationResponse(token));
    response.getWriter().write(new ObjectMapper().writeValueAsString(model));
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    ResponseModel model =
        new ResponseModel(new Date().getTime(), null, ResponseCode._501, failed.getMessage());
    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");
    response.getWriter().write(new ObjectMapper().writeValueAsString(model));
  }

}
