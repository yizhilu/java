package com.hc.security.controller;

import java.security.Principal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.hc.security.configuration.AsyncTask;
import com.hc.security.controller.model.ResponseModel;

import springfox.documentation.annotations.ApiIgnore;

/**
 * 和权限安全相关的接口，在这里暴露<br>
 */
@RestController
@RequestMapping("/v1/test")
public class TestController extends BaseController {

  @RequestMapping(value = "/test1", method = RequestMethod.GET)
  public @ApiIgnore ResponseModel loginSuccess(HttpServletRequest request,
      HttpServletResponse response, Principal logUser,
      @AuthenticationPrincipal(errorOnInvalidType = true) UserDetails userDetails) {
    return this.buildHttpReslut();
  }

  @RequestMapping(value = "/test2", method = RequestMethod.POST)
  public @ApiIgnore ResponseModel loginSuccess2(Principal logUser, String a) {
    return this.buildHttpReslut();
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
