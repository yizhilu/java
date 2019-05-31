package com.hc.security.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hc.security.pojo.FileUploadPojo;
import com.hc.security.service.FileUpdateService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 这个controller用于接受一个新的文件，支持jpg、gif、png图片
 * 
 * @author yinwenjie
 */
@RestController
@RequestMapping("/v1/files")
public class FileUpdateController extends BaseController {

  @Autowired
  private FileUpdateService fileUpdateService;

  /**
   * 这个方法用于上传单张图片使用
   * 
   * @param file 上传的文件信息
   * @param response
   * @throws IllegalStateException
   * @throws IOException
   */
  @ApiOperation(value = "这个方法可用于一次上传多个文件", notes = ""
      + "1、只能上传大小不超过1024KB的文件，也就是说如果上传的图片文件较大，那么客户端需要自行压缩一下<br>"
      + "2、上传成功后，这个请求将返回文件在服务器端重命名后的相对存储路径，使用http://service:port/v1/files/文件相对路径  的方式就可以进行图片访问（还可以加相应特效哦）")
  @ApiParam(required = true, name = "file", value = "上传的文件对象，提交的表单信息中，请命名为file")
  @RequestMapping(path = "/{subsystem}/fileUploads", method = RequestMethod.POST)
  public List<FileUploadPojo> fileUpload(@PathVariable("subsystem") String subsystem,
      @RequestParam("file") MultipartFile[] files) throws IllegalArgumentException {
    if (files == null || files.length == 0) {
      throw new IllegalArgumentException("you must upload least one file !");
    }

    // 依次处理每个文件
    List<FileUploadPojo> results = new ArrayList<>();
    for (MultipartFile multipartFile : files) {
      FileUploadPojo result = this.fileUpdateService.fileUpload(subsystem, multipartFile);
      results.add(result);
    }
    return results;
  }

  /**
   * 这个方法用于上传单个文件使用
   * 
   * @param file 上传的文件信息
   * @param response
   * @throws IllegalStateException
   * @throws IOException
   */
  @ApiOperation(value = "这个方法用于上传单个文件使用", notes = "" + "<br>"
      + "2、只能上传大小不超过1024KB的文件，也就是说如果上传的图片文件较大，那么客户端需要自行压缩一下<br>"
      + "3、上传成功后，这个请求将返回图片在服务器端重命名后的相对存储路径，使用http://service:port/v1/files/图片相对路径  的方式就可以进行图片访问（还可以加相应特效哦）")
  @ApiParam(required = true, name = "file", value = "上传的文件对象，提交的表单信息中，请命名为file")
  @RequestMapping(path = "/fileUpload", method = RequestMethod.POST)
  public FileUploadPojo imageUpload(@RequestParam("file") MultipartFile file)
      throws IllegalArgumentException {
    if (file == null) {
      throw new IllegalArgumentException("you must upload least one file !");
    }
    return this.fileUpdateService.fileUpload(null, file);
  }
}
