package cn.com.modernmediaslate.unit;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.text.TextUtils;
import cn.com.modernmediaslate.SlateApplication;

/**
 * 图片磁盘缓存管理器
 * 
 * @author zhuqiao
 * 
 */
public class ImgFileManager {
	private static String defaultPath = "";

	private static String getDefaultPath() {
		if (TextUtils.isEmpty(defaultPath)) {
			defaultPath = Environment.getExternalStorageDirectory().getPath();
		}
		return defaultPath;
	}

	public static void createNoMediaFile() {
		String path = getDefaultPath() + "/"
				+ SlateApplication.mConfig.getCache_file_name() + "/"
				+ ".nomedia";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param url
	 * @param data
	 */
	public static void saveImage(String url, byte[] data) {
		String path = getDefaultPath() + SlateApplication.DEFAULT_IMAGE_PATH;
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdir();
		}
		File file = new File(path + MD5.MD5Encode(url) + ".img");
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取图片
	 * 
	 * @param url
	 * @return
	 */
	public static byte[] getImage(String url) {
		String path = getDefaultPath() + SlateApplication.DEFAULT_IMAGE_PATH;
		path += MD5.MD5Encode(url) + ".img";
		byte[] buffer = null;
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				return null;
			}
			fis = new FileInputStream(file);
			bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			buffer = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer;
	}

	/**
	 * 获取图片在SD卡中的路径
	 * 
	 * @param name
	 * @return
	 */
	public static String getBitmapPath(String name) {
		return getDefaultPath() + SlateApplication.DEFAULT_IMAGE_PATH
				+ MD5.MD5Encode(name) + ".img";
	}

	public static void saveImage(Bitmap bitmap, String path) {
		if (TextUtils.isEmpty(path) || !path.contains(File.separator)) {
			return;
		}
		String dirPath = path.substring(0, path.lastIndexOf(File.separator));
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File picPath = new File(path);
		if (picPath.exists()) {
			picPath.delete();
		}
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(picPath), 1024);
			bitmap.compress(CompressFormat.JPEG, 100, bos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
