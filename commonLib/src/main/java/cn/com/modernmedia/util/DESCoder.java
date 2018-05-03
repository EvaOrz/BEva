package cn.com.modernmedia.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 支付订单加密处理类
 * 
 * @author Eva.
 * 
 */
public class DESCoder {
	// 加解密统一使用的编码方式
	// private final static String encoding = "utf-8";

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

	/**
	 * 秘钥处理
	 * 
	 * @return
	 */
	private byte[] getKey(String initKey) {
		byte[] seed = initKey.getBytes();
		KeyGenerator kg;
		try {
			kg = KeyGenerator.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		SecureRandom sr;
		try {

			sr = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		sr.setSeed(seed);
		kg.init(128, sr);
		SecretKey sk = kg.generateKey();

		byte[] key = sk.getEncoded();
		return key;
	}
}
