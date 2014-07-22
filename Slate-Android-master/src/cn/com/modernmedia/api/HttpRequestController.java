package cn.com.modernmedia.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.listener.FetchDataListener;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.PrintHelper;

/**
 * Http请求管理器
 * 
 * @author ZhuQiao
 * 
 */
public class HttpRequestController {
	private static HttpRequestController instance = null;

	private HttpRequestController() {
	}

	protected static HttpRequestController getInstance() {
		if (instance == null) {
			instance = new HttpRequestController();
		}
		return instance;
	}

	protected void fetchHttpData(RequestThread requestThread) {
		requestThread.start();
	}

	/**
	 * 读取网络接口子线程
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class RequestThread extends Thread {
		private Context context;
		private URL mUrl = null;
		private FetchDataListener mFetchDataListener;
		private String fileName = "";// 本地文件名称
		private boolean useLocalFirst = true;// 是否优先使用本地数据
		private String url = "";

		protected RequestThread(Context context, String url) {
			this.context = context;
			this.url = url == null ? "" : url;
			if (!TextUtils.isEmpty(url)) {
				try {
					mUrl = new URL(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		protected void setmFetchDataListener(
				FetchDataListener mFetchDataListener) {
			this.mFetchDataListener = mFetchDataListener;
		}

		protected void setFileName(String fileName) {
			this.fileName = fileName;
		}

		protected void setUseLocalFirst(boolean useLocalFirst) {
			this.useLocalFirst = useLocalFirst;
		}

		@Override
		public void run() {
			// 如果存在本地数据，直接返回
			if (useLocalFirst && fecthLocalData()) {
				return;
			}
			if (mUrl == null) {
				return;
			}
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) mUrl.openConnection();
				conn.setUseCaches(true);
				conn.setConnectTimeout(ConstData.CONNECT_TIMEOUT);
				conn.setReadTimeout(ConstData.READ_TIMEOUT);
				int status = conn.getResponseCode();
				if (status == 200) {
					InputStream is = conn.getInputStream();
					if (is == null) {
						fetchLocalDataInBadNet();
						return;
					}
					String data = receiveData(is);
					if (TextUtils.isEmpty(data)) {
						fetchLocalDataInBadNet();
						return;
					}
					showToast("from http:" + url);
					mFetchDataListener.fetchData(true, data);
					reSetUpdateTime();
					PrintHelper.print("from http:" + url);
				} else {
					fetchLocalDataInBadNet();
				}
			} catch (IOException e) {
				e.printStackTrace();
				fetchLocalDataInBadNet();
			} finally {
				conn.disconnect();
			}
		}

		private String receiveData(InputStream is) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byte[] buff = new byte[ConstData.BUFFERSIZE];
				int readed = -1;
				while ((readed = is.read(buff)) != -1) {
					baos.write(buff, 0, readed);
				}
				byte[] result = baos.toByteArray();
				if (result == null)
					return null;
				return new String(result);
			} finally {
				if (is != null)
					is.close();
				if (baos != null)
					baos.close();
			}
		}

		/**
		 * 网络不好的情况下获取本地数据
		 */
		private void fetchLocalDataInBadNet() {
			String localData = getLocalData(fileName);
			if (!TextUtils.isEmpty(localData)) {
				showToast("from sdcard by bad net:" + url);
				mFetchDataListener.fetchData(true, localData);
				PrintHelper.print("from sdcard by bad net:" + url);
			} else {
				PrintHelper.print("net error:" + url);
				mFetchDataListener.fetchData(false, null);
			}
		}

		/**
		 * 返回本地数据
		 */
		private boolean fecthLocalData() {
			String localData = getLocalData(fileName);
			if (!TextUtils.isEmpty(localData)) {
				showToast("from sdcard:" + url);
				mFetchDataListener.fetchData(true, localData);
				PrintHelper.print("from sdcard:" + url);
				return true;
			}
			return false;
		}

		/**
		 * 获取本地数据
		 * 
		 * @param name
		 * @return
		 */
		private String getLocalData(String name) {
			if (TextUtils.isEmpty(name) || !FileManager.containFile(name))
				return null;
			String data = FileManager.getApiData(name);
			if (TextUtils.isEmpty(data))
				return null;
			try {
				new JSONObject(data);
			} catch (JSONException e) {
				e.printStackTrace();
				PrintHelper.print(name + "文件被异常修改，无法封装成json数据！！");
				FileManager.deleteFile(name);
				return null;
			}
			return data;
		}

		/**
		 * 开始判定的更新时间变化了,如果从HTTP请求成功的，那么设置为不变化
		 */
		private void reSetUpdateTime() {
			if (TextUtils.isEmpty(fileName))
				return;
			// column时间只比较首页，栏目首页数据在判定为不相同时已经删除
			if (fileName.equals(ConstData.getIndexFileName())
					&& !CommonApplication.columnUpdateTimeSame) {
				CommonApplication.columnUpdateTimeSame = true;
			} else if (fileName.equals(ConstData.getArticleListFileName())
					&& !CommonApplication.articleUpdateTimeSame) {
				CommonApplication.articleUpdateTimeSame = true;
			}
		}

		private void showToast(String str) {
			// if (ConstData.IS_DEBUG == 0)
			// return;
			// if (context != null && context instanceof BaseActivity) {
			// // ((BaseActivity) context).showToast(str);
			// }
		}
	}

}
