package com.hc.security.service.internal;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hc.security.pojo.FileUploadPojo;
import com.hc.security.service.FileUpdateService;

/**
 * 文件上传服务
 * 
 * @author yinwenjie
 */
@Service("FileUpdateServiceImpl")
public class FileUpdateServiceImpl implements FileUpdateService {

  /**
   * 保存文件的根路径描述
   */
  @Value("${filePath.fileRoot}")
  private String fileRoot;

  /**
   * 最大允许的文件上传大小（单位MB）
   */
  @Value("${filePath.maxFileSize}")
  private Integer maxFileSize;

  /*
   * (non-Javadoc)
   * 
   * @see vanda.demo.buttonAlarm.service.FileUpdateService#fileUpload(java.lang.String,
   * org.springframework.web.multipart.MultipartFile)
   */
  @Override
  public FileUploadPojo fileUpload(String subSystem, MultipartFile file)
      throws IllegalArgumentException {
    /*
     * 处理过程为： 1、为了保证网络畅通，要控制文件大小在1MB以下，所以也要进行控制（当然也可以通过spring mvc的配置实现限制）
     * 
     * 2、开始保存文件，注意，文件都要重命名。 为了简单起见重命名使用java自带的UUID工具完成即可
     * 
     * 3、正式写入文件，如果以上所有步骤都成功，则向上传者返回文件存储的提示信息
     * 
     * 最后，本工程没有提供上传的测试页面，测试是使用postman等软件完成的
     */
    // 1、都在这里=======
    String originalFilename = file.getOriginalFilename();
    long fileSize = file.getSize();
    String prefix = null;
    int prefixIndex = originalFilename.lastIndexOf(".");
    if (prefixIndex != -1) {
      prefix = originalFilename.substring(prefixIndex + 1);
      prefix = prefix.toLowerCase();
    }
    // 如果条件成立，说明大于1MB了
    if (fileSize > maxFileSize * 1024 * 1024) {
      throw new IllegalArgumentException("file should be less than 1MB!");
    }

    // 2、======
    // 可以使用日期作为文件夹的名字
    Date nowDate = new Date();
    String folderName = new SimpleDateFormat("yyyyMMdd").format(nowDate);
    String renameImage = UUID.randomUUID().toString();
    String relativePath = null;
    String folderPath = null;
    if (!StringUtils.isBlank(subSystem)) {
      relativePath = subSystem + "/" + folderName + "/" + (new Random().nextInt(100) % 10);
    } else {
      relativePath = folderName + "/" + (new Random().nextInt(100) % 10);
    }
    folderPath = fileRoot + "/" + relativePath;
    File folderFile = new File(folderPath);
    // 如果不存在这个目录则进行创建。
    // 为了保证高并发时不会重复创建目录，要进行线程锁定
    // 使用悲观锁就行了
    if (!folderFile.exists()) {
      synchronized (FileUpdateService.class) {
        while (!folderFile.exists()) {
          folderFile.mkdirs();
        }
      }
    }
    // 以下就是这个即将创建的文件的完整路径了
    relativePath += "/" + renameImage + "." + prefix;
    String fullImagePath = fileRoot + "/" + relativePath;

    // 4、====
    try {
      file.transferTo(new File(fullImagePath));
    } catch (IllegalStateException | IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }

    // 构造返回
    FileUploadPojo result = new FileUploadPojo();
    result.setRelativePath(relativePath);
    result.setOriginalFilename(originalFilename);
    result.setFileName(renameImage + "." + prefix);
    result.setCreateTime(new Date());
    return result;
  }
}
