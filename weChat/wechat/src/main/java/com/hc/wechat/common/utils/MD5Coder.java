package com.hc.wechat.common.utils;

import java.security.MessageDigest;


/**
 * MD5加密
 * 
 * @author liyong
 * @version 1.0
 */
public class MD5Coder{
	public static final String ALGORITHM = "MD5";

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String initKey() throws Exception {
		return KeyFactory.initKey(KeyFactory.DEFAULTALGORITHM, null);
	}

	/**
	 * 初始化密钥
	 * 
	 * @param seed
	 *            密钥种子
	 * @return
	 * @throws Exception
	 */
	public static String initKey(String seed) throws Exception {
		return KeyFactory.initKey(KeyFactory.DEFAULTALGORITHM, seed);
	}

	/**
	 * MD5加密
	 * 
	 * @param distStr
	 *            待加密串
	 * @param key
	 *            加密密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptMd5(byte[] distStr, String key)
			throws Exception {
		MessageDigest md5 = MessageDigest.getInstance(ALGORITHM);
		byte[] newData;
		if (key == null || "".equals(key.trim()))
			newData = distStr;
		else
			newData = Utils.byteMerger(distStr, key.getBytes());
		md5.update(newData);
		return md5.digest();
	}

	/**
	 * 生成MD5 16进制摘要
	 * 
	 * @param distStr
	 *            待加密串
	 * @param key
	 *            加密密钥
	 * @return 16进制摘要
	 * @throws Exception
	 */
	public static String encryptMd5Hex(String distStr, String key)
			throws Exception {
		if (distStr == null || "".equals(distStr.trim())) {
			return null;
		}
		byte[] b = encryptMd5(distStr.getBytes(), key);
		return Utils.bytesToHexString(b);
	}

	/**
	 * 生成MD5 base64编码摘要
	 * 
	 * @param distStr
	 *            待加密串
	 * @param key
	 *            加密密钥
	 * @return base64编码摘要
	 * @throws Exception
	 */
	public static String encryptMd5B64(String distStr, String key)
			throws Exception {
		if (distStr == null || "".equals(distStr.trim())) {
			return null;
		}
		byte[] b = encryptMd5(distStr.getBytes(), key);
		return BASE64Coder.encryptBASE64(b);
	}
}
