package com.hc.security.service;

import org.springframework.web.multipart.MultipartFile;

import com.hc.security.pojo.FileUploadPojo;

public interface FileUpdateService {

  /**
   * 文件上传服务
   * 
   * @param subsystem 指代进行文件上传的子系统信息，子系统将单独生成一个文件夹。利于管理
   * @param file 文件系统
   * @return
   * @throws IllegalArgumentException
   */
  public FileUploadPojo fileUpload(String subsystem, MultipartFile file)
      throws IllegalArgumentException;
}
