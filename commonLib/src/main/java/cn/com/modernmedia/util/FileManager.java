package cn.com.modernmedia.util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmediaslate.unit.MD5;

/**
 * 文件存储
 *
 * @author ZhuQiao
 */
public class FileManager {
    private static final String CHARSET = "utf-8";
    private static String defaultPath = "";

    public static String getDefaultPath() {
        if (TextUtils.isEmpty(defaultPath)) {
            defaultPath = Environment.getExternalStorageDirectory().getPath();
        }
        return defaultPath;
    }

    public static void createNoMediaFile() {
        String path = getDefaultPath() + "/" + CommonApplication.mConfig.getCache_file_name() + "/" + ".nomedia";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 保存api返回的数据
     *
     * @param name
     * @param data
     */
    public static void saveApiData(String name, String data) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(data)) return;
        if (data == null) return;
        boolean isCrash = name.equals(ConstData.CRASH_NAME);
        String dataPath = "";
        if (!isCrash) {
            name = MD5.MD5Encode(name);
            dataPath = getDefaultPath() + ConstData.DEFAULT_DATA_PATH;
        } else {
            dataPath = getDefaultPath() + ConstData.DEFAULT_DATA_PATH + "crash/";
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
        String data_path = getDefaultPath() + ConstData.DEFAULT_DATA_PATH + name + ".txt";
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
            if (result == null) return null;
            return new String(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) in.close();
                if (baos != null) baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件中获取数据
     *
     * @param file
     * @return
     */
    public static String getJsonData(File file) {
        FileInputStream in = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            in = new FileInputStream(file);
            byte[] buff = new byte[ConstData.BUFFERSIZE];
            int line = -1;
            while ((line = in.read(buff)) != -1) {
                baos.write(buff, 0, line);
            }
            byte[] result = baos.toByteArray();
            if (result == null) return null;
            return new String(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) in.close();
                if (baos != null) baos.close();
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
        String delete_name = MD5.MD5Encode(name);// 需要删除的文件名
        String dataPath = getDefaultPath() + ConstData.DEFAULT_DATA_PATH;
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
        name = MD5.MD5Encode(name);
        String dataPath = getDefaultPath() + ConstData.DEFAULT_DATA_PATH;
        File file = new File(dataPath + name + ".txt");
        return file.exists();
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
     * @param file
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

    public static List<String> getFoldChildNames(String name) {
        String path = getDefaultPath() + ConstData.DEFAULT_PACKAGE_PATH;
        File flod = new File(path + ModernMediaTools.getPackageFolderName(name));
        if (!flod.exists()) return null;
        List<String> result = new ArrayList<String>();
        File list[] = flod.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile()) {
                result.add(list[i].getName());
            }
        }
        return result;
    }


    public static String getVideoName(String name) {
        String path = getDefaultPath() + ConstData.DEFAULT_PACKAGE_PATH;
        File flod = new File(path + ModernMediaTools.getPackageFolderName(name));
        if (!flod.exists()) return null;
        File list[] = flod.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile() && list[i].getName().endsWith(".mp4")) {
                return list[i].getName();
            }
        }
        return null;
    }

    /**
     * 获取存储pdf文件的路径
     *
     * @param pdfUrl
     * @return
     */
    public static String getPdfFilePath(String pdfUrl) {
        return new File(Environment.getExternalStorageDirectory().getPath() + ConstData.DEFAULT_PDF_PATH + MD5.MD5Encode(pdfUrl) + ".dzs").getAbsolutePath();
    }

    /**
     * pdf文件是否已下载
     *
     * @param pdfUrl
     * @return
     */
    public static boolean ifHasPdfFilePath(String pdfUrl) {
        File flod = new File(Environment.getExternalStorageDirectory().getPath() + ConstData.DEFAULT_PDF_PATH + MD5.MD5Encode(pdfUrl) + ".dzs");
        if (flod.exists()) {
            return true;
        }
        return false;
    }

    public static String getHtmlName(String name) {
        String path = getDefaultPath() + ConstData.DEFAULT_PACKAGE_PATH;
        File flod = new File(path + ModernMediaTools.getPackageFolderName(name));
        if (!flod.exists()) return null;
        File list[] = flod.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile() && list[i].getName().endsWith(".html")) {
                return list[i].getName();
            }
        }
        return null;
    }

}
