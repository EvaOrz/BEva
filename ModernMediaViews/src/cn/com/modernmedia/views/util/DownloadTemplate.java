package cn.com.modernmedia.views.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.HttpRequestController;
import cn.com.modernmediaslate.api.HttpRequestController.RequestThread;
import cn.com.modernmediaslate.listener.FetchDataListener;

/**
 * 下载模板
 * 
 * @author zhuqiao
 * 
 */
public class DownloadTemplate {
	private String split = "android_template/";

	/**
	 * 下载模板回调函数
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static interface DownloadTemplateCallBack {
		public void afterDownload(Template template);
	}

	private Context mContext;
	private String mUrl = "";
	private DownloadTemplateCallBack mCallBack;
	private Handler handler = new Handler();

	public DownloadTemplate(Context context, TagArticleList articleList,
			DownloadTemplateCallBack callBack) {
		mContext = context;
		mUrl = articleList.getProperty().getTemplate();
		mCallBack = callBack;
	}

	/**
	 * 获取模板
	 */
	public void getTemplate() {
		if (SlateApplication.mConfig.getHas_subscribe() == 0
				|| AppValue.appInfo.getHaveSubscribe() == 0) {
			// TODO 如果不支持订阅，那么只取本地默认模板
			callback(null);
			return;
		}
		String name = getTemplateName();
		if (TextUtils.isEmpty(name)) {
			callback(null);
		} else {
			String data = getTemplateData(name);
			if (!TextUtils.isEmpty(data)) {
				try {
					new JSONObject(data);
					callback(ParseProperties.getInstance(mContext).parseIndex(
							data, getTemplateHost()));
				} catch (JSONException e) {
					e.printStackTrace();
					deleteFile(name);
					callback(null);
				}
			} else {
				download();
			}
		}
	}

	/**
	 * 下载
	 */
	private void download() {
		RequestThread thread = new RequestThread(mContext, mUrl,
				new BaseOperate() {

					@Override
					protected void saveData(String data) {
					}

					@Override
					protected void handler(JSONObject jsonObject) {
					}

					@Override
					protected String getUrl() {
						return null;
					}

					@Override
					protected String getDefaultFileName() {
						return null;
					}

					@Override
					protected void fetchLocalDataInBadNet(
							FetchDataListener mFetchDataListener) {
						mFetchDataListener.fetchData(false, null, false);
					}

				});
		thread.setmFetchDataListener(new FetchDataListener() {

			@Override
			public void fetchData(boolean isSuccess, String data,
					boolean fromHttp) {
				if (isSuccess && !TextUtils.isEmpty(data)) {
					Template template = ParseProperties.getInstance(mContext)
							.parseIndex(data, getTemplateHost());
					String supportVersions = template.getSupportVersions();
					if (!TextUtils.isEmpty(supportVersions)) {
						String[] supports = supportVersions.split(",");
						for (String support : supports) {
							if (support.equals(SlateApplication.mConfig
									.getTemplate_version() + "")) {
								// TODO 当版本不大于当前应用最大模板版本时，保存当前文件并更新临时模板文件
								saveTemplateData(data, getTemplateName());
								saveTemplateData(data, getLastestTemplateName());
								callback(template);
								return;
							}
						}
					}
					// TODO 当线上模板版本不支持当前应用所支持的最大版本时，获取上次保存的最新模板
					fecthLastestTemplate();
				} else {
					fecthLastestTemplate();
				}
			}
		});
		HttpRequestController.getInstance().fetchHttpData(thread);
	}

	/**
	 * 获取最新一次更新的模板文件
	 */
	private void fecthLastestTemplate() {
		String lastData = getTemplateData(getLastestTemplateName());
		if (!TextUtils.isEmpty(lastData)) {
			try {
				new JSONObject(lastData);
				callback(ParseProperties.getInstance(mContext).parseIndex(
						lastData, getTemplateHost()));
			} catch (JSONException e) {
				e.printStackTrace();
				deleteFile(getLastestTemplateName());
				callback(null);
			}
		} else {
			callback(null);
		}
	}

	/**
	 * 保存模板
	 * 
	 * @param name
	 * @param data
	 */
	private void saveTemplateData(String data, String name) {
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(data))
			return;
		if (data == null)
			return;
		File file = new File(getFolder());
		if (!file.exists()) {
			file.mkdirs();
		}
		String path = getFolder() + name;
		File saveFile = new File(path);
		FileOutputStream oStream = null;
		OutputStreamWriter writer = null;
		try {
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			oStream = new FileOutputStream(saveFile, false);// false:更新文件；true:追加文件
			writer = new OutputStreamWriter(oStream, "UTF-8");
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
	 * 获取模板
	 * 
	 * @param name
	 *            模板名
	 * @return
	 */
	private String getTemplateData(String name) {
		if (TextUtils.isEmpty(name))
			return null;
		String data_path = getFolder() + name;
		FileInputStream in = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			File file = new File(data_path);
			if (!file.exists())
				return null;
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
		} catch (Exception e) {
			e.printStackTrace();
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
		return null;
	}

	private void deleteFile(String name) {
		if (TextUtils.isEmpty(name))
			return;
		String data_path = getFolder() + name;
		File file = new File(data_path);
		if (file.exists())
			file.delete();
	}

	private void callback(Template template) {
		if (mCallBack != null) {
			if (template == null) {
				if (mUrl.contains("iweekly-iphone-column-index-gallery")) {
					template = ParseProperties.getInstance(mContext)
							.parseIndexGalleryNormal();
				} else {
					template = ParseProperties.getInstance(mContext)
							.parseIndexListNormal();
				}
			}
			final Template _template = template;
			handler.post(new Runnable() {

				@Override
				public void run() {
					mCallBack.afterDownload(_template);
				}
			});
		}
	}

	/**
	 * 获取模板名称
	 * 
	 * @return
	 */
	private String getTemplateName() {
		// "http://s1.cdn.bbwc.cn/issue_424/category/android_template/文件夹/bloomberg-column-index_1399605287.json",
		if (TextUtils.isEmpty(mUrl))
			return "";
		String arr[] = mUrl.split(split);
		if (arr.length == 2) {
			String[] _arr = arr[1].split("/");
			if (_arr.length == 2)
				return _arr[1];
		}
		return "";
	}

	/**
	 * 获取上一次更新的最新文件名称
	 * 
	 * @return
	 */
	private String getLastestTemplateName() {
		String templateName = getTemplateName();
		if (TextUtils.isEmpty(templateName))
			return "";
		String s = "\\d+";

		Pattern pattern = Pattern.compile(s);
		Matcher ma = pattern.matcher(templateName);

		while (ma.find()) {
			templateName = templateName.replace("_" + ma.group(), "");
		}
		return templateName;
	}

	/**
	 * 获取模板host
	 * 
	 * @eg http://s1.cdn.bbwc.cn/issue_424/category/android_template/bloomberg/
	 * @return
	 */
	private String getTemplateHost() {
		String templateName = getTemplateName();
		if (TextUtils.isEmpty(templateName))
			return "";
		return mUrl.replaceAll(templateName, "");
	}

	private String getFolder() {
		return Environment.getExternalStorageDirectory().getPath()
				+ ConstData.DEFAULT_TEMPLATE_PATH;
	}
}
