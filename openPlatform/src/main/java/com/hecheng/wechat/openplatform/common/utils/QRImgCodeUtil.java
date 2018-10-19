package com.hecheng.wechat.openplatform.common.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class QRImgCodeUtil {
  private static final Logger LOG = LoggerFactory.getLogger(QRImgCodeUtil.class);

  private static final int BLACK = 0xFF000000;
  private static final int WHITE = 0xFFFFFFFF;
  // logo默认边框宽度
  public static final int DEFAULT_BORDER = 2;

  public static TwoCodeImageBuilder newBuilder() {
    return new TwoCodeImageBuilder();
  }

  /** 源类型 文件路径 、文件 、流 */
  public static enum FileSourceType {
    FILEPATH, FILE, STREAM;
  }
  public static class TwoCodeImageBuilder {
    /** 二维码默认边框宽度 */
    private Integer twoCodeWidth = 430;
    /** 二维码默认边框高度 */
    private Integer twoCodeHeight = 430;
    /** 二维码内容 */
    private String content = null;
    /** 二维码图片默认格式 */
    private String imageSuffix = "jpg";

    public TwoCodeImageBuilder setTwoCodeWidth(Integer twoCodeWidth) {
      this.twoCodeWidth = twoCodeWidth;
      return this;
    }

    public TwoCodeImageBuilder setTwoCodeHeight(Integer twoCodeHeight) {
      this.twoCodeHeight = twoCodeHeight;
      return this;
    }

    public TwoCodeImageBuilder setContent(String content) {
      this.content = content;
      return this;
    }

    public TwoCodeImageBuilder setImageSuffix(String imageSuffix) {
      this.imageSuffix = imageSuffix;
      return this;
    }

  }

  private static BufferedImage toBufferedImage(BitMatrix matrix) {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
      }
    }
    return image;
  }

  public static byte[] get2CodeImageByte(TwoCodeImageBuilder builder) {
    Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
    hints.put(EncodeHintType.MARGIN, 1);
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
    BitMatrix bitMatrix = null;
    String content = builder.content;
    Validate.notBlank(content, "二维码内容不能为空");
    Integer twoCodeWidth = builder.twoCodeWidth;
    Validate.notNull(twoCodeWidth, "二维码宽度不能为空");
    Validate.isTrue(twoCodeWidth > 0, "二维码宽度不能小于0");
    Integer twoCodeHeight = builder.twoCodeHeight;
    Validate.notNull(twoCodeHeight, "二维码高度不能为空");
    Validate.isTrue(twoCodeHeight > 0, "二维码高度不能小于0");
    // 设置二维码的内容和宽高
    try {
      bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, twoCodeWidth,
          twoCodeHeight, hints);
    } catch (WriterException e) {
      e.printStackTrace();
      LOG.error(e.getMessage());
    }
    try {
      // 输出图像
      return QRImgCodeUtil.writeToFileByte(bitMatrix, builder);
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getMessage());
    }
    return null;

  }

  private static byte[] writeToFileByte(BitMatrix bitMatrix, TwoCodeImageBuilder builder)
      throws FileNotFoundException {
    String imageSuffix = builder.imageSuffix;
    Validate.notBlank(imageSuffix, "二维码图片格式不能为空");
    BufferedImage image = toBufferedImage(bitMatrix);
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageIO.write(image, imageSuffix, out);
      byte[] imageBytes = out.toByteArray();
      return imageBytes;
    } catch (IOException e) {
      e.printStackTrace(System.out);
      LOG.error(e.getMessage());
    }
    return null;
  }
}
