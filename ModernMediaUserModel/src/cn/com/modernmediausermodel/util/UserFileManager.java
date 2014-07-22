package cn.com.modernmediausermodel.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

/**
 * 文件存储
 * 
 * @author ZhuQiao
 * 
 */
public class UserFileManager {
	private static final int DEFAULT_COMPRESS_QUALITY = 100;//

	public static void saveImage(Bitmap bitmap, String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		File picPath = new File(path);
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
