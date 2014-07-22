package cn.com.modernmediausermodel.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;

/**
 * 文件存储
 * 
 * @author ZhuQiao
 * 
 */
public class UserFileManager {
	private static final int DEFAULT_COMPRESS_QUALITY = 100;//

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
			bitmap.compress(CompressFormat.JPEG, DEFAULT_COMPRESS_QUALITY, bos);
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
