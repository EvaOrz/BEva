package cn.com.modernmediausermodel.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.Environment;
import android.text.TextUtils;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.MD5;

/**
 * 文件存储
 * 
 * @author ZhuQiao
 * 
 */
public class UserFileManager {
	private static String defaultPath = "";

	private static String getDefaultPath() {
		if (TextUtils.isEmpty(defaultPath)) {
			defaultPath = Environment.getExternalStorageDirectory().getPath();
		}
		return defaultPath;
	}

	/**
	 * 保存api返回的数据
	 * 
	 * @param name
	 * @param data
	 */
	public static void saveApiData(String name, String data) {
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(data))
			return;
		if (data == null)
			return;
		name = MD5.MD5Encode(name);
		File file = new File(getFolder());
		if (!file.exists()) {
			file.mkdirs();
		}
		String path = getFolder() + name + ".txt";
		File saveFile = new File(path);
		FileOutputStream oStream = null;
		OutputStreamWriter writer = null;
		try {
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			oStream = new FileOutputStream(saveFile, false);// false:更新文件；true:追加文件
			writer = new OutputStreamWriter(oStream, "utf-8");
			writer.write(data);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oStream != null) {
					oStream.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从SD卡上读取api收
	 * 
	 * @param name
	 * @return
	 */
	public static String getApiData(String name) {
		name = MD5.MD5Encode(name);
		String data_path = getFolder() + name + ".txt";
		FileInputStream in = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			File file = new File(data_path);
			in = new FileInputStream(file);
			byte[] buff = new byte[1024];
			int line = -1;
			while ((line = in.read(buff)) != -1) {
				baos.write(buff, 0, line);
			}
			byte[] result = baos.toByteArray();
			if (result == null)
				return null;
			return new String(result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null)
					in.close();
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 是否存在此文件(解析完数据查看，如果用户手动删除此文件，但服务器的逻辑又不需要更新此文件，那么咱们也得生产一份新的文件,ok)
	 * 
	 * @param name
	 * @return
	 */
	public static boolean containFile(String name) {
		name = MD5.MD5Encode(name);
		File file = new File(getFolder() + name + ".txt");
		return file.exists();
	}

	/**
	 * 删除文件
	 * 
	 * @param name
	 */
	public static void deleteFile(String name) {
		String delete_name = MD5.MD5Encode(name);// 需要删除的文件名
		File deleteFile = new File(getFolder() + delete_name + ".txt");
		if (deleteFile.exists()) {
			deleteFile.delete();
		}
	}

	/**
	 * 获取用户模块文件夹
	 * 
	 * @return
	 */
	private static String getFolder() {
		return getDefaultPath() + "/"
				+ SlateApplication.mConfig.getCache_file_name() + "/user/";
	}
}
