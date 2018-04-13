package com.hc.wechat.pojo;

import java.util.Date;

/**
 * 文件上传后的返回信息
 * @author yinwenjie
 */
public class FileUploadPojo {
  
  /**
   * 文件名
   */
  private String fileName;
  
  /**
   * 图片文件的相对路径，包括图片重命名后的信息
   */
  private String relativePath;
  /**
   * 图片的原名
   */
  private String originalFilename;
  /**
   * 创建时间
   */
  private Date createTime;

  public String getRelativePath() {
    return relativePath;
  }

  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
  }
  
}
