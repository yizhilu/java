package com.hc.security.common.utils.crypto.sm;

/**
 * SM2为非对称加密，基于非对称加密算法强度最高级ECC，椭圆曲线密码编码学。该算法已公开。由于该算法基于ECC，故其签名速度与秘钥生成速度都快于RSA。ECC 256位（SM2采用的就是ECC
 * 256位的一种）安全强度比RSA 2048位高，但运算速度快于RSA。
 * 
 * @author hc
 *
 */
public class SM2Cipher {
  /**
   * ECC密钥
   */
  private byte[] c1;

  /**
   * 真正的密文
   */
  private byte[] c2;

  /**
   * 对（c1+c2）的SM3-HASH值
   */
  private byte[] c3;

  /**
   * SM2标准的密文，即（c1+c2+c3）
   */
  private byte[] cipherText;

  public byte[] getC1() {
    return c1;
  }

  public void setC1(byte[] c1) {
    this.c1 = c1;
  }

  public byte[] getC2() {
    return c2;
  }

  public void setC2(byte[] c2) {
    this.c2 = c2;
  }

  public byte[] getC3() {
    return c3;
  }

  public void setC3(byte[] c3) {
    this.c3 = c3;
  }

  public byte[] getCipherText() {
    return cipherText;
  }

  public void setCipherText(byte[] cipherText) {
    this.cipherText = cipherText;
  }
}
