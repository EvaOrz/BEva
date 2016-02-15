package cn.com.modernmedia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 标记日志helper
 * 
 * @author lusiyuan
 *
 */
public class SelectionHelper {
	/**
	 * “uid":1000,//用户uid “deviceid”:”xxxxxxxxx”,//设备id “devicemodel”:”iPhone
	 * 5”,// “appid”:”1", //应用id “appversion”:”2.8.2”, “os”:”IOS8.3”,
	 * “osversion”:”8.4”, “appname”:”商业周刊”, “appbuildversion”:1873
	 */
	private static JSONArray arrayevent = new JSONArray();
	private static SelectionHelper instance;
	private static String path = FileManager.getDefaultPath()
			+ ConstData.DEFAULT_DATA_PATH;

	public static synchronized SelectionHelper getInstance() {
		if (instance == null)
			instance = new SelectionHelper();
		return instance;
	}

	public void add(Context context, String event, Map<String, String> map) {
		JSONObject postObject = new JSONObject();
		try {
			addPostParams(postObject, "event", event);
			// 参数
			JSONObject param = new JSONObject();
			for (String key : map.keySet()) {
				addPostParams(param, key, map.get(key));
			}
			postObject.put("param", param);
			// DateFormat.format("yyyyMMddHH",
			// System.currentTimeMillis()).toString()

			addPostParams(postObject, "timestamp", System.currentTimeMillis()
					/ 1000 + "");

			arrayevent.put(postObject);// 添加
			if (arrayevent.length() > 39) {// post参数长度限制，50条请求一次接口
				// writeToFile(context, postObject);
				sendApi(context);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 发送日志 (参数方式)
	 * 
	 * @param context
	 */
	public void sendApi(Context context) {
		final JSONObject json = new JSONObject();
		try {
			json.put("events", arrayevent);

			addPostParams(json, "uid", Tools.getUid(context));
			addPostParams(json, "deviceid", CommonApplication.getMyUUID());
			addPostParams(json, "devicemodel", android.os.Build.MODEL);// 手机型号
			addPostParams(json, "appid", ConstData.getInitialAppId() + "");
			PackageManager packageManager = context.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			addPostParams(json, "appversion", info.versionName);

			addPostParams(json, "os", "android");
			addPostParams(json, "osversion", android.os.Build.VERSION.RELEASE
					+ android.os.Build.VERSION.SDK);
			addPostParams(json, "appbuildversion", info.versionCode + "");
			addPostParams(json, "appname",
					context.getResources().getString(R.string.app_name));

		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * post
		 */
		new Thread() {

			@Override
			public void run() {
				String resData = null;
				HttpPost httpPost = null;
				HttpClient httpClient = null;
				try {
					httpPost = new HttpPost(UrlMaker.getSelection());
					List<NameValuePair> formparams = new ArrayList<NameValuePair>();
					formparams.add(new BasicNameValuePair("data", json
							.toString()));
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
							formparams, HTTP.UTF_8);
					httpPost.setEntity(entity);
					httpPost.setHeader("Accept", "application/json");
					httpPost.setHeader("Content-type", "application/json");

					httpClient = new DefaultHttpClient();
					HttpResponse response = httpClient.execute(httpPost);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity resEntity = response.getEntity();
						resData = (resEntity == null) ? null : EntityUtils
								.toString(resEntity, HTTP.UTF_8);
					}
				} catch (Exception e) {
					if (e != null && !TextUtils.isEmpty(e.getMessage()))
						SlatePrintHelper.logE("SELECTION_LOG", e.getMessage());
				} finally {
					if (httpPost != null)
						httpPost.abort();
					if (httpClient != null)
						httpClient.getConnectionManager().shutdown();
				}
			}

		}.start();

		// OperateController controller =
		// OperateController.getInstance(context);
		// controller.addSelection(context, json, new FetchEntryListener() {
		//
		// @Override
		// public void setData(Entry entry) {
		// arrayevent = new JSONArray();
		// }
		// });
	}

	/**
	 * 发送日志（文件方式）
	 */
	public void sendApiFile(Context context) {
		try {
			zip(path + "events_log", path + "events_log_zip.zip");
			OperateController controller = OperateController
					.getInstance(context);
			controller.addSelectionByFile(context, path + "events_log_zip.zip",
					new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							arrayevent = new JSONArray();
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将Value值进行UTF-8编码
	 * 
	 * @param obj
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	private void addPostParams(JSONObject obj, String key, String value)
			throws Exception {
		if (!TextUtils.isEmpty(value)) {
			String encode = URLEncoder.encode(value, HTTP.UTF_8);
			// 数据中含换行符时，不能编码，否则服务器端在解析该json时会无法解析
			String br = URLEncoder.encode("\n", HTTP.UTF_8);
			if (encode.contains(br)) {
				encode = encode.replace(br, "\n");
			}
			obj.put(key, value);
		}
	}

	/**
	 * 写入文件
	 */
	private void writeToFile(Context c, JSONObject json) {
		try {

			String js = json.toString();

			File dirs = new File(path, "events_log");
			FileOutputStream fout = new FileOutputStream(dirs);

			byte[] bytes = js.getBytes();

			fout.write(bytes);
			fout.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 压缩文件到zip
	 * 
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void zip(String src, String dest) throws IOException {
		// 提供了一个数据项压缩成一个ZIP归档输出流
		ZipOutputStream out = null;
		try {

			File outFile = new File(dest);// 源文件或者目录
			File fileOrDirectory = new File(src);// 压缩文件路径
			out = new ZipOutputStream(new FileOutputStream(outFile));
			// 如果此文件是一个文件，否则为false。
			if (fileOrDirectory.isFile()) {
				zipFileOrDirectory(out, fileOrDirectory, "");
			} else {
				// 返回一个文件或空阵列。
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], "");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// 关闭输出流
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static void zipFileOrDirectory(ZipOutputStream out,
			File fileOrDirectory, String curPath) throws IOException {
		// 从文件中读取字节的输入流
		FileInputStream in = null;
		try {
			// 如果此文件是一个目录，否则返回false。
			if (!fileOrDirectory.isDirectory()) {
				// 压缩文件
				byte[] buffer = new byte[4096];
				int bytes_read;
				in = new FileInputStream(fileOrDirectory);
				// 实例代表一个条目内的ZIP归档
				ZipEntry entry = new ZipEntry(curPath
						+ fileOrDirectory.getName());
				// 条目的信息写入底层流
				out.putNextEntry(entry);
				while ((bytes_read = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes_read);
				}
				out.closeEntry();
			} else {
				// 压缩目录
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], curPath
							+ fileOrDirectory.getName() + "/");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			// throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

}
