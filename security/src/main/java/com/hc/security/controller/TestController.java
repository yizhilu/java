package com.hc.security.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.hc.crypto.annotation.ApiDecryptAnno;
import com.hc.crypto.annotation.ApiEncryptAnno;
import com.hc.security.configuration.AsyncTask;
import com.hc.security.controller.model.ResponseModel;
import com.hc.security.service.security.jwt.JwtUserDetailsService;
import com.hc.security.service.security.jwt.model.JwtAuthenticationResponse;
import com.hc.security.service.security.jwt.model.JwtUser;
import com.hc.security.service.security.jwt.utils.JwtTokenUtil;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 和权限安全相关的接口，在这里暴露<br>
 */
@RestController
public class TestController extends BaseController {
  @Autowired
  private Md5PasswordEncoder passwordEncoder;
  @Autowired
  private JwtUserDetailsService userDetailsService;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @RequestMapping(value = "/api/refresh", method = RequestMethod.GET)
  public @ApiIgnore ResponseModel refresh(HttpServletRequest request, HttpServletResponse response,
      Principal logUser) {
    String authToken = request.getHeader("Authorization");
    final String token = authToken.substring(7);
    String username = jwtTokenUtil.getUsernameFromToken(token);
    JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

    if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
      String refreshedToken = jwtTokenUtil.refreshToken(token);
      return new ResponseModel(new JwtAuthenticationResponse(refreshedToken));
    } else {
      return this.buildHttpReslut();
    }
  }

  @RequestMapping(value = "/api/test1", method = RequestMethod.GET)
  public @ApiIgnore ResponseModel testApi(HttpServletRequest request, HttpServletResponse response,
      Principal logUser) {
    System.out.println(logUser == null ? "no" : logUser.getName());
    List<String> list = new ArrayList<>();
    return this.buildHttpReslut();
  }

  @RequestMapping(value = "/test1", method = RequestMethod.GET)
  public @ApiIgnore ResponseModel loginSuccess(HttpServletRequest request,
      HttpServletResponse response, Principal logUser) {
    System.out.println(logUser == null ? "no" : logUser.getName());
    System.out.println(passwordEncoder.encodePassword("123456", null));
    return this.buildHttpReslut();
  }

  // @ApiDecryptAnno
  @ApiEncryptAnno
  @RequestMapping(value = "/test2", method = RequestMethod.POST)
  public @ApiIgnore ResponseModel loginSuccess2(@RequestBody JSONObject json) {

    return this.buildHttpCryptoReslut(json);
  }

  @RequestMapping(value = "/testCsrf", method = RequestMethod.GET)
  public @ApiIgnore ModelAndView testCsrf(Principal logUser, String a) {
    return new ModelAndView("/test/testCsrf.html");
  }

  @Autowired
  private AsyncTask task;

  @GetMapping("async_task")
  public void exeTask() throws InterruptedException, ExecutionException {

    long begin = System.currentTimeMillis();

    Future<String> task4 = task.task4();
    Future<String> task5 = task.task5();
    Future<String> task6 = task.task6();

    // 如果都执行往就可以跳出循环,isDone方法如果此任务完成，true
    for (;;) {
      if (task4.isDone() && task5.isDone() && task6.isDone()) {
        System.out.println(task4.get());
        System.out.println(task5.get());
        System.out.println(task6.get());

        break;
      }
    }

    long end = System.currentTimeMillis();
    long total = end - begin;
    System.out.println("执行总耗时=" + total);
  }
}
