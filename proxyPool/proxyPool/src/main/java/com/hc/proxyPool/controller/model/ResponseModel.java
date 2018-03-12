package com.hc.proxyPool.controller.model;

/**
 * 统一返回数据对象（对本次请求执行的操作结果是否成功 进行描述 flag=1时执行成功）
 * 
 * 
 */
public class ResponseModel {
  /** 时间 */
  private Long timestemp;
  /**
   * 正常情况下返回的数据在这里进行记录和描述
   */
  private Object data;
  /**
   * 响应标记，正常情况下是200
   */
  private ResponseCode responseCode = ResponseCode._200;
  /** 异常信息描述 */
  private String errorMsg;

  public ResponseModel(Long timestemp, Object data, ResponseCode responseCode, String errorMsg) {
    this.timestemp = timestemp;
    this.data = data;
    this.responseCode = responseCode;
    this.errorMsg = errorMsg;
  }

  public Long getTimestemp() {
    return timestemp;
  }

  public void setTimestemp(Long timestemp) {
    this.timestemp = timestemp;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public ResponseCode getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(ResponseCode responseCode) {
    this.responseCode = responseCode;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }
}
