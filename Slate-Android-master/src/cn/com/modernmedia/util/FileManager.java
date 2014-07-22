package cn.com.modernmedia.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

/**
 * �ļ��洢
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

	/**
	 * ����ͼƬ��SD��
	 * 
	 * @param bitmap
	 * @param name
	 *            ͼƬ����(MD5����ͼƬurl)
	 */
	public static void saveImageToFile(InputStream is, String name) {
		if (TextUtils.isEmpty(name) || is == null)
			return;
		saveImage(is, name, true);
	}

	private static void saveImage(InputStream is, String name, boolean showMd5) {
		String file_name = name;
		if (showMd5) {
			file_name = MD5.MD5Encode(name) + ".img";
		}
		String imagePath = getDefaultPath() + ConstData.DEFAULT_IMAGE_PATH;
		File file = new File(imagePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File picPath = new File(imagePath + file_name);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(picPath), 1024);
			byte[] buffer = new byte[1024];
			int size = -1;
			while ((size = is.read(buffer)) != -1) {
				bos.write(buffer, 0, size);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��SD���ж�ȡͼƬ
	 * 
	 * @param name
	 * @return
	 */
	public static Bitmap getImageFromFile(String name) {
		return getImageFromFile(name, 0, 0);
	}

	public static Bitmap getImageFromFile(String name, int width, int height) {
		if (TextUtils.isEmpty(name))
			return null;
		String file_name = MD5.MD5Encode(name);
		String img_path = getDefaultPath() + ConstData.DEFAULT_IMAGE_PATH
				+ file_name + ".img";
		File file = new File(img_path);
		if (!file.exists()) {
			return null;
		}
		return BitmapUtil.getBitmapByPath(name, img_path, width, height);
	}

	/**
	 * ����api���ص�����
	 * 
	 * @param name
	 * @param data
	 */
	public static void saveApiData(String name, String data) {
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(data))
			return;
		if (data == null)
			return;
		boolean isCrash = name.equals(ConstData.CRASH_NAME)
				|| name.equals(ConstData.API_LOG);
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
			oStream = new FileOutputStream(saveFile, isCrash);// false:�����ļ���true:׷���ļ�
			writer = new OutputStreamWriter(oStream, CHARSET);
			if (isCrash) {
				writer.write(data);
			} else {
				writer.write(EncrptUtil.encrpyt2(data));
			}
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
	 * ��SD���϶�ȡapi��
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
			return EncrptUtil.decrypt2(new String(result));
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
	 * ɾ���ļ�
	 * 
	 * @param name
	 */
	public static void deleteFile(String name) {
		String expand = getexpandFolder(name);
		String delete_name = MD5.MD5Encode(name);// ��Ҫɾ�����ļ���
		String dataPath = getDefaultPath() + ConstData.getCurrentIssueFold()
				+ expand;
		File deleteFile = new File(dataPath + delete_name + ".txt");
		if (deleteFile.exists()) {
			deleteFile.delete();
		}
	}

	/**
	 * �Ƿ���ڴ��ļ�(���������ݲ鿴������û��ֶ�ɾ�����ļ��������������߼��ֲ���Ҫ���´��ļ�����ô����Ҳ������һ���µ��ļ�,ok)
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
	 * ���ڱ����catindex��Ψһ����������ɾ��
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
	 * ���������£���ͼ������ɾ��
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
	 * ���������£��ѷ�����Ϣɾ��
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
	 * ���û�������һ�ڣ�����һ�ڵ�ɾ��
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
	 * �Ƿ���catindex�����������ļ���
	 * 
	 * @param name
	 * @return
	 */
	private static String isCatIndex(String name) {
		return name.contains(CAT_INDEX_FOLDER + "_") ? CAT_INDEX_FOLDER + "/"
				: "";
	}

	/**
	 * �Ƿ���ͼ�����£����������ļ���
	 * 
	 * @param name
	 * @return
	 */
	private static String isArticle(String name) {
		return name.contains(ARTICLE_FOLDER + "_") ? ARTICLE_FOLDER + "/" : "";
	}

	/**
	 * �Ƿ��Ƿ���������Ϣ�����������ļ���
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
	 * ���µ�apk
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

}
