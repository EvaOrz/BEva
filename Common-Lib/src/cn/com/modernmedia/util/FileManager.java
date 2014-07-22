package cn.com.modernmedia.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.Environment;
import android.text.TextUtils;

/**
 * 文件存储
 * 
 * @author ZhuQiao
 * 
 */
public class FileManager {
	private static final String CAT_INDEX_FOLDER = "catindex";
	private static final String ARTICLE_FOLDER = "article";
	private static final String SHARE_FOLDER = "share";
	private static final String CHARSET = "utf-8";
	private static String defaultPath = "";

	private static String getDefaultPath() {
		if (TextUtils.isEmpty(defaultPath)) {
			defaultPath = Environment.getExternalStorageDirectory().getPath();
		}
		return defaultPath;
	}

	public static void createNoMediaFile() {
		String path = getDefaultPath() + "/" + ConstData.getAppName() + "/"
				+ ".nomedia";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static void saveImage(String url, byte[] data) {
		String path = getDefaultPath() + ConstData.DEFAULT_IMAGE_PATH;
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

	public static byte[] getImage(String url) {
		String path = getDefaultPath() + ConstData.DEFAULT_IMAGE_PATH;
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
		return getDefaultPath() + ConstData.DEFAULT_IMAGE_PATH
				+ MD5.MD5Encode(name) + ".img";
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
		boolean isCrash = name.equals(ConstData.CRASH_NAME);
		String expand = getexpandFolder(name);
		String dataPath = "";
		if (!isCrash) {
			name = MD5.MD5Encode(name);
			dataPath = getDefaultPath() + ConstData.getCurrentIssueFold()
					+ expand;
		} else {
			dataPath = getDefaultPath() + ConstData.DEFAULT_DATA_PATH;
		}
		File file = new File(dataPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		String path = dataPath + name + ".txt";
		File saveFile = new File(path);
		FileOutputStream oStream = null;
		OutputStreamWriter writer = null;
		try {
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			oStream = new FileOutputStream(saveFile, isCrash);// false:更新文件；true:追加文件
			writer = new OutputStreamWriter(oStream, CHARSET);
			// if (isCrash || !needEncrpyt) {
			writer.write(data);
			// } else {
			// writer.write(EncrptUtil.encrpyt2(data));
			// }
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
		String expand = getexpandFolder(name);
		name = MD5.MD5Encode(name);
		String data_path = getDefaultPath() + ConstData.getCurrentIssueFold()
				+ expand + name + ".txt";
		FileInputStream in = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			File file = new File(data_path);
			in = new FileInputStream(file);
			byte[] buff = new byte[ConstData.BUFFERSIZE];
			int line = -1;
			while ((line = in.read(buff)) != -1) {
				baos.write(buff, 0, line);
			}
			byte[] result = baos.toByteArray();
			if (result == null)
				return null;
			// if (needEncrpyt) {
			// return EncrptUtil.decrypt2(new String(result));
			// } else {
			return new String(result);
			// }
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
	 * 删除文件
	 * 
	 * @param name
	 */
	public static void deleteFile(String name) {
		String expand = getexpandFolder(name);
		String delete_name = MD5.MD5Encode(name);// 需要删除的文件名
		String dataPath = getDefaultPath() + ConstData.getCurrentIssueFold()
				+ expand;
		File deleteFile = new File(dataPath + delete_name + ".txt");
		if (deleteFile.exists()) {
			deleteFile.delete();
		}
	}

	/**
	 * 是否存在此文件(解析完数据查看，如果用户手动删除此文件，但服务器的逻辑又不需要更新此文件，那么咱们也得生产一份新的文件,ok)
	 * 
	 * @param name
	 * @return
	 */
	public static boolean containFile(String name) {
		String expand = getexpandFolder(name);
		name = MD5.MD5Encode(name);
		String dataPath = getDefaultPath() + ConstData.getCurrentIssueFold()
				+ expand;
		File file = new File(dataPath + name + ".txt");
		return file.exists();
	}

	/**
	 * 由于保存的catindex不唯一，所以批量删除
	 */
	public static void deleteCatIndexFile() {
		String dataPath = getDefaultPath() + ConstData.getCurrentIssueFold()
				+ CAT_INDEX_FOLDER + "/";
		File file = new File(dataPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				continue;
			} else {
				files[i].delete();
			}
		}
	}

	/**
	 * 更新了文章，把图集文章删除
	 */
	public static void deleteArticleFile() {
		String dataPath = getDefaultPath() + ConstData.getCurrentIssueFold()
				+ ARTICLE_FOLDER + "/";
		File file = new File(dataPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				continue;
			} else {
				files[i].delete();
			}
		}
	}

	/**
	 * 更新了文章，把分享信息删除
	 */
	public static void deleteShareFile() {
		String dataPath = getDefaultPath() + ConstData.getCurrentIssueFold()
				+ SHARE_FOLDER + "/";
		File file = new File(dataPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				continue;
			} else {
				files[i].delete();
			}
		}
	}

	/**
	 * 当用户更新了一期，把上一期的删除
	 * 
	 * @param folder_name
	 */
	public static void deleteIssueFolder(String folder_name) {
		String dataPath = getDefaultPath() + ConstData.getCurrentIssueFold()
				+ folder_name + "/";
		File file = new File(dataPath);
		if (!file.exists())
			return;
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				continue;
			} else {
				files[i].delete();
			}
		}
	}

	/**
	 * 是否是catindex，单独创建文件夹
	 * 
	 * @param name
	 * @return
	 */
	private static String isCatIndex(String name) {
		return name.contains(CAT_INDEX_FOLDER + "_") ? CAT_INDEX_FOLDER + "/"
				: "";
	}

	/**
	 * 是否是图集文章，单独创建文件夹
	 * 
	 * @param name
	 * @return
	 */
	private static String isArticle(String name) {
		return name.contains(ARTICLE_FOLDER + "_") ? ARTICLE_FOLDER + "/" : "";
	}

	/**
	 * 是否是分享文章信息，单独创建文件夹
	 * 
	 * @param name
	 * @return
	 */
	private static String isShareArticle(String name) {
		return name.contains(SHARE_FOLDER + "_") ? SHARE_FOLDER + "/" : "";
	}

	private static String getexpandFolder(String name) {
		if (!TextUtils.isEmpty(isCatIndex(name))) {
			return isCatIndex(name);
		} else if (!TextUtils.isEmpty(isArticle(name))) {
			return isArticle(name);
		} else if (!TextUtils.isEmpty(isShareArticle(name))) {
			return isShareArticle(name);
		}
		return "";
	}

	/**
	 * 更新的apk
	 * 
	 * @return
	 */
	public static File getApkByName(String name) {
		String path = getDefaultPath() + ConstData.DEFAULT_APK_PATH;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return new File(path + name);
	}

	/**
	 * fullpackage file
	 * 
	 * @param name
	 * @return
	 */
	public static File getPackageByName(String name) {
		String path = getDefaultPath() + ConstData.DEFAULT_PACKAGE_PATH;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return new File(path + ModernMediaTools.getPackageFileName(name));
	}

	/**
	 * 删除fullpackage zip
	 * 
	 * @param name
	 */
	public static void deletePackageByName(String name) {
		String path = getDefaultPath() + ConstData.DEFAULT_PACKAGE_PATH;
		name = ModernMediaTools.getPackageFileName(name);
		File packageFile = new File(path + name);
		if (packageFile.exists()) {
			packageFile.delete();
		}
		// if (name.endsWith(".zip")) {
		// name = name.substring(0, name.lastIndexOf(".zip"));
		// }
		// File file = new File(path + name);
		// if (file.exists()) {
		// deletePackageFold(file);
		// }
	}

	/**
	 * 删除fullpackage zip解压后的文件夹
	 * 
	 * @param path
	 */
	public static void deletePackageFold(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				deletePackageFold(childFiles[i]);
			}
			file.delete();
		}
	}

	/**
	 * 获取package路径
	 * 
	 * @param url
	 * @return
	 */
	public static String getPackageNameByUrl(String url) {
		return getDefaultPath() + ConstData.DEFAULT_PACKAGE_PATH + url;
	}

	/**
	 * 是否存在当前zip包
	 * 
	 * @param name
	 * @return
	 */
	public static boolean containThisPackage(String name) {
		String path = getDefaultPath() + ConstData.DEFAULT_PACKAGE_PATH;
		File file = new File(path + ModernMediaTools.getPackageFileName(name));
		// TODO 解压完的文件夹暂时不考虑了，因为如果暂停了再进来是没有文件夹的，如果判断了，会把压缩包也删了
		// File flod = new File(path +
		// ModernMediaTools.getPackageFolderName(name));
		return file.exists();
	}

	public static boolean containThisPackageFolder(String name) {
		String path = getDefaultPath() + ConstData.DEFAULT_PACKAGE_PATH;
		// File file = new File(path +
		// ModernMediaTools.getPackageFileName(name));
		// TODO 解压完的文件夹暂时不考虑了，因为如果暂停了再进来是没有文件夹的，如果判断了，会把压缩包也删了
		File flod = new File(path + ModernMediaTools.getPackageFolderName(name));
		return flod.exists();
	}
}
