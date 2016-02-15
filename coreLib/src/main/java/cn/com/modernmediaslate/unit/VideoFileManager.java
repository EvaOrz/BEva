package cn.com.modernmediaslate.unit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import cn.com.modernmediaslate.SlateApplication;

/**
 * 视频磁盘缓存管理器
 * 
 * @author lusiyuan
 *
 */
public class VideoFileManager {
	private static String defaultPath = "";

	private static String getDefaultPath() {
		if (TextUtils.isEmpty(defaultPath)) {
			defaultPath = Environment.getExternalStorageDirectory().getPath();
		}
		return defaultPath;
	}

	/**
	 * 保存视频
	 * 
	 * @param url
	 * @param data
	 */
	public static void saveVideo(String url, Context context) {
		String path = getDefaultPath() + SlateApplication.DEFAULT_VIDEO_PATH;
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdir();
		}
		File file = new File(path + MD5.MD5Encode(url) + ".mp4");
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			// 执行
			HttpResponse ressponse = client.execute(httpGet);
			int code = ressponse.getStatusLine().getStatusCode();

			if (code == HttpStatus.SC_OK) {
				// 设置本地保存的文件
				// File storeFile = new File("c:/0431la.zip");
				FileOutputStream output = new FileOutputStream(file);
				// context.openFileOutput(path + MD5.MD5Encode(url) + ".mp4",
				// context.MODE_PRIVATE);
				// 得到网络资源并写入文件
				InputStream input = ressponse.getEntity().getContent();
				byte b[] = new byte[1024];
				int j = 0;
				while ((j = input.read(b)) != -1) {
					output.write(b, 0, j);
				}
				output.flush();
				output.close();
			}
		} catch (Exception e) {
		}

	}

	/**
	 * 获取视频
	 * 
	 * @param url
	 * @return
	 */
	public static byte[] getVideo(String url) {
		String path = getDefaultPath() + SlateApplication.DEFAULT_VIDEO_PATH;
		path += MD5.MD5Encode(url) + ".mp4";
		byte[] buffer = null;
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
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
	 * 获取视频在SD卡中的路径
	 * 
	 * @param name
	 * @return
	 */
	public static boolean getVideoExit(String name) {
		File file = new File(getDefaultPath()
				+ SlateApplication.DEFAULT_VIDEO_PATH + MD5.MD5Encode(name)
				+ ".mp4");
		if (file.exists())
			return true;
		return false;
	}

	/**
	 * 获取视频在SD卡中的路径
	 * 
	 * @param name
	 * @return
	 */
	public static String getVideoPath(String name) {
		return getDefaultPath() + SlateApplication.DEFAULT_VIDEO_PATH
				+ MD5.MD5Encode(name) + ".mp4";
	}

	/**
	 * 删除视频
	 * 
	 * @param name
	 */
	public static void deleteVideo(String name) {
		File file = new File(getDefaultPath()
				+ SlateApplication.DEFAULT_VIDEO_PATH + MD5.MD5Encode(name)
				+ ".mp4");
		if (file.exists())
			file.delete();
	}

}
