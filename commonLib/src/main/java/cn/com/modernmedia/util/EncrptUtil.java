package cn.com.modernmedia.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;
import android.util.Base64;

/**
 * 文件加密解密
 * 
 * @author ZhuQiao
 * 
 */
public class EncrptUtil {

	private static final String KEY = "my_modern_media_";

	/**
	 * 加密
	 * 
	 * @param content
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String encrpyt2(String content) {
		if (TextUtils.isEmpty(content)) {
			return "";
		}
		try {
			SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptedData = cipher.doFinal(content.getBytes("utf-8"));
			return Base64.encodeToString(encryptedData, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解密
	 * 
	 * @param content
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String decrypt2(String content) {
		if (TextUtils.isEmpty(content)) {
			return "";
		}
		try {
			byte[] byteD = Base64.decode(content, Base64.DEFAULT);
			SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedData = cipher.doFinal(byteD);
			return new String(decryptedData, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
