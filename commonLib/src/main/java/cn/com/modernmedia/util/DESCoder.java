package cn.com.modernmedia.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class DESCoder {
	// 密钥
	// private final static String secretKey = "liuyunqiang@lx100$#365#$";
	// 向量
	private final static String iv = "01234567";
	// 加解密统一使用的编码方式
	private final static String encoding = "utf-8";

	/**
	 * 3DES加密
	 * 
	 * @param plainText
	 *            普通文本
	 * @return
	 * @throws Exception
	 */
	public static String encode(String secretKey, String plainText)
			throws Exception {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(secretKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(plainText.getBytes());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new String(Base641.encode(crypted));

		// Key deskey = null;
		// DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
		// SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		// deskey = keyfactory.generateSecret(spec);
		//
		// Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
		// IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
		// cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
		// byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
		// return Base641.encode(encryptData);
	}

	/**
	 * 3DES解密
	 * 
	 * @param encryptText
	 *            加密文本
	 * @return
	 * @throws Exception
	 */
	public static String decode(String secretKey, String encryptText) {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(secretKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base641.decode(encryptText));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new String(output);
	}

}
