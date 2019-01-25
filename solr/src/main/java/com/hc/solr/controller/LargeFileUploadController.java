package com.hc.solr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hc.solr.common.utlis.SnowflakeIdWorker;
import com.hc.solr.controller.model.ResponseCode;
import com.hc.solr.controller.model.ResponseModel;

@RestController
@RequestMapping("/largeFileUpload")
public class LargeFileUploadController extends BaseController {

  private Logger logger = Logger.getLogger(LargeFileUploadController.class);
  @Value("${upload.tempFileRoot}")
  private String tempFileRoot;
  @Value("${upload.fileRoot}")
  private String fileRoot;

  @GetMapping("getFileCode")
  public ModelAndView getFileCode() {
    ModelAndView model = new ModelAndView("/upload/upload");
    SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
    model.addObject("md5File", idWorker.nextId());
    return model;
  }

  /**
   * 
   * <p>
   * Title: checkFileExist
   * </p>
   * Description:<pre>检查文件存在与否 </pre>
   * 
   * @author hecheng
   * @date 2019年1月18日
   * @param md5File
   * @return
   */
  @PostMapping("checkFileExist")
  @ResponseBody
  public ResponseModel checkFileExist(@RequestParam(value = "md5File") String md5File) {
    Boolean exist = false;
    // 实际项目中，这个md5File唯一值，应该保存到数据库或者缓存中，通过判断唯一值存不存在，来判断文件存不存在，这里我就不演示了
    /*
     * if(true) { exist = true; }
     */
    return new ResponseModel(System.currentTimeMillis(), exist, ResponseCode._200, null);
  }

  /**
   * 
   * <p>
   * Title: checkChunkExist
   * </p>
   * Description:<pre>检查指定 md5File文件的指定chunk分片存不存在 </pre>
   * 
   * @author hecheng
   * @date 2019年1月18日
   * @param md5File
   * @param chunk
   * @return
   */
  @PostMapping("checkChunkExist")
  @ResponseBody
  public ResponseModel checkChunkExist(@RequestParam(value = "md5File") String md5File,
      @RequestParam(value = "chunk") Integer chunk) {
    Boolean exist = false;
    String path = tempFileRoot + "/" + md5File + "/";// 分片存放目录
    String chunkName = chunk + ".tmp";// 分片名
    File file = new File(path + chunkName);
    if (file.exists()) {
      exist = true;
    }
    return new ResponseModel(System.currentTimeMillis(), exist, ResponseCode._200, null);
  }

  /**
   * 
   * <p>
   * Title: uploadFileChunk
   * </p>
   * Description:<pre> 上传文件的每个分片</pre>
   * 
   * @author hecheng
   * @date 2019年1月18日
   * @param file
   * @param md5File
   * @param chunk
   * @return
   */
  @PostMapping("uploadFileChunk")
  @ResponseBody
  public ResponseModel uploadFileChunk(@RequestParam(value = "file") MultipartFile file,
      @RequestParam(value = "md5File") String md5File,
      @RequestParam(value = "chunk", required = false) Integer chunk) { // 第几片，从0开始
    String path = tempFileRoot + "/" + md5File + "/";
    File dirfile = new File(path);
    if (!dirfile.exists()) {// 目录不存在，创建目录
      dirfile.mkdirs();
    }
    String chunkName;
    if (chunk == null) {// 表示是小文件，还没有一片
      chunkName = "0.tmp";
    } else {
      chunkName = chunk + ".tmp";
    }
    String filePath = path + chunkName;
    File savefile = new File(filePath);

    try {
      if (!savefile.exists()) {
        savefile.createNewFile();// 文件不存在，则创建
      }
      file.transferTo(savefile);// 将文件保存
    } catch (IOException e) {
      return new ResponseModel(System.currentTimeMillis(), false, ResponseCode._200, null);
    }
    return new ResponseModel(System.currentTimeMillis(), true, ResponseCode._200, null);
  }

  /**
   * 
   * <p>
   * Title: merge
   * </p>
   * Description:<pre> 分片合成</pre>
   * 
   * @author hecheng
   * @date 2019年1月18日
   * @param chunks
   * @param md5File
   * @param name
   * @return
   * @throws Exception
   */
  @PostMapping("merge")
  @ResponseBody
  public ResponseModel merge(@RequestParam(value = "chunks", required = false) Integer chunks,
      @RequestParam(value = "md5File") String md5File, @RequestParam(value = "name") String name,
      boolean delTempFile,String filePath) throws Exception {
    String path = fileRoot;
    String tempPath = tempFileRoot;
    FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + name); // 合成后的文件
    try {
      byte[] buf = new byte[1024];
      for (long i = 0; i < chunks; i++) {
        String chunkFile = i + ".tmp";
        File file = new File(tempPath + "/" + md5File + "/" + chunkFile);
        InputStream inputStream = new FileInputStream(file);
        int len = 0;
        while ((len = inputStream.read(buf)) != -1) {
          fileOutputStream.write(buf, 0, len);
        }
        inputStream.close();
      }
      // 删除md5目录，及临时文件
      if (delTempFile) {
        FileUtils.deleteDirectory(new File(tempPath + "/" + md5File));
      }
    } catch (Exception e) {
      return new ResponseModel(System.currentTimeMillis(), false, ResponseCode._200, null);
    } finally {
      fileOutputStream.close();
    }
    return new ResponseModel(System.currentTimeMillis(), true, ResponseCode._200, null);
  }
}
