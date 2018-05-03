package cn.com.modernmedia.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
* Environment: 常用方法: 1.Environment.getExternalStorageState() 获取sdcard状态
 * 2.Environment.getExternalStoragePublicDirectory(type) 获取外部存储的公共的目录
 * storage/adcard/type 
 * 3.Environment.getExternalStorageDirectory() 获取外部存储的根目录
 * storage/adcard 
 * 4.Environment.getDataDirectory():获取内部存储路径 /data
 * 5.Environment.getDownloadCacheDirectory():缓存目录 /cache
 * 
 * 
 * 
 * 注意: context.getExternalFilesDir():获取外部存储的私有目录
 * 
 * @author Administrator
 * 
 */
@SuppressLint("NewApi")
public class ExternalStroageUtil {
	public static final String TAG = "ExternalStroageUtil";

	/**
	 * 判断外部存储空间是否可用
	 */
	public static boolean isExternalStorageUse() {
		// 表示是否可用
		boolean flag = false;

		/**
		 * Environment 描述当前设备的一个类 设备的状态常量都可以通过该类调用。 Environment.MEDIA_MOUNTED
		 * 设备挂载上了 Environment.MEDIA_MOUNTED_READ_ONLY 设备挂载上了,但是只能读
		 * 
		 */

		String state = Environment.getExternalStorageState();

		// 比较
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			flag = true;
		}
		return flag;
	}

	/**
	 *  判断类型是否可用 type参数就是公共目录
	 */
	public static boolean hasExternalStoragePublicDir(String type) {
		boolean flag = false;
		File file = Environment.getExternalStoragePublicDirectory(type);
		if (file != null && file.exists()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 向公共的空间中存储数据
	 */
	public static boolean writeExternalStoragePublic(String type,
			String fileName, byte[] content) {
		boolean flag = false;
		if (isExternalStorageUse()) // 判断sdcard是否可用
		{
			Log.i("case", "isExternalStroageUse---->" + isExternalStorageUse());
			// 可以写入
			// 判断类型是否可用
			if (hasExternalStoragePublicDir(type)) {

				File fileDir = Environment
						.getExternalStoragePublicDirectory(type);
				Log.i("case", "fileDir---->" + fileDir.getPath());
				// 创建文件
				File file = new File(fileDir, fileName);
				Log.i("ExternalPublicDirectory", file.getPath());
				Log.i("case", "file---->" + file.getPath());
				// 创建流对象
				FileOutputStream outputStream;
				try {
					outputStream = new FileOutputStream(file);
					outputStream.write(content);
					// 关闭流
					outputStream.close();
					flag = true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		return flag;
	}

	/**
	 * 读取公共目录
	 */

	public static byte[] readExternalStoragePublic(String type, String fileName) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = null;
		if (isExternalStorageUse()) {
			if (hasExternalStoragePublicDir(type)) {
				File file = new File(
						Environment.getExternalStoragePublicDirectory(type),
						fileName);
				// 创建流
				try {
					FileInputStream inputStream = new FileInputStream(file);
					int len = 0;
					byte[] buf = new byte[1024];
					while ((len = inputStream.read(buf)) != -1) {
						baos.write(buf, 0, len);
						baos.flush();
					}
					// 关闭流
					inputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return baos.toByteArray();
	}

	/**
 * 向外部存储 的私有的目录存储数据 目录： 所有的文件存储在: sdcard/Android/data/应用程序名字/目录名(type)/文件
	 * 
	 * @param context
	 * @param type
	 *            目录名 可以为null sdcard/Android/data/应用程序名字/files/文件名
	 * @param fileName
	 * @param content
	 * @return 是否存储成功
	 */
	public static boolean writeExternalStoragePrivate(Context context,
			String type, String fileName, byte[] content) {
		// 
		boolean flag = false;
		// 
		if (isExternalStorageUse()) {
		
			File parentFile = context.getExternalFilesDir(type);
		
			File file = new File(parentFile, fileName);
			try {
			
				FileOutputStream fos = new FileOutputStream(file);
			
				fos.write(content);
			
				fos.close();
		
				flag = true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			Log.i(TAG, "sdcard");
		}
		return flag;
	}

	public void showToast(String file,String fileName){
		String msg = "图片保存在"+ fileName +"文件夹";
	}

	public static byte[] readExternalStoragePrivate(Context context,
			String type, String fileName) {
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (isExternalStorageUse()) {
		
			File parentFile = context.getExternalFilesDir(type);
		
			File file = new File(parentFile, fileName);
		
			try {
				FileInputStream fis = new FileInputStream(file);
				int len = 0;
				byte[] buf = new byte[1024];
				while ((len = fis.read(buf)) != -1) {
					baos.write(buf, 0, len);
					baos.flush();
				}
				
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			Log.i(TAG, "sdcard");
		}
		return baos.toByteArray();
	}

	/**
	 *向sdcard的根目录中写入内容 根目录是:storage/sdcard
	 * 
	 * @param fileName
	 * @param content
	 * @return
	 */
	public static boolean writeExternalStorageRoot(String fileName,
			byte[] content) {
		boolean flag = false;

		if (isExternalStorageUse()) {
			File parentFile = Environment.getExternalStorageDirectory();
			Log.i(TAG, parentFile.getAbsolutePath());
			File file = new File(parentFile, fileName);

			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(content);
				fos.close();
				flag = true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.i(TAG, "sdcard");
		}
		return flag;
	}

	public static byte[] readExternalStorageRoot(String fileName) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (isExternalStorageUse()) {
			File parentFile = Environment.getExternalStorageDirectory();
			File file = new File(parentFile, fileName);
			try {
				FileInputStream fis = new FileInputStream(file);
				int len = 0;
				byte[] b = new byte[1024];
				while ((len = fis.read(b)) != -1) {
					baos.write(b, 0, len);
					baos.flush();
				}
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.i(TAG, "sdcard");
		}
		return baos.toByteArray();
	}

	/**
	 * 获取sdcard总空间 StatFs类：该类主要用于描述存储设备状态。状态主要是指参数位置处的总空间，可用空间。
	 * 块block的个数:getBlockCount() 每块大小:getBlockSize() 可用的快数:getAvailableBlock()
	 * 字节---》M兆 1字节=8bit 1kb=1024字节 1M=1024kb 1G=1024M 1T=1024G
	 */

	@SuppressWarnings("deprecation")
	public static long getToatalSpace(File file) {
		long size = 0;
		if (isExternalStorageUse()) {
			StatFs statFs = new StatFs(file.getAbsolutePath());
			if (Build.VERSION.SDK_INT >= 18) {
				size = file.getTotalSpace();
			} else {
				//
				size = statFs.getBlockCount() * statFs.getBlockSize();
			}
		}
		return size;
	}

	/**
	 * 可用空间
	 */
	public static long getAvailableSize(File file) {
		long size = 0;
		if (isExternalStorageUse()) {
			StatFs statFs = new StatFs(file.getAbsolutePath());
			if (Build.VERSION.SDK_INT >= 18) {
				size = file.getUsableSpace();
			} else {
				//
				size = statFs.getFreeBlocks() * statFs.getBlockSize();
			}
		} else {
			Log.i(TAG, "");
		}
		return size;
	}

}
