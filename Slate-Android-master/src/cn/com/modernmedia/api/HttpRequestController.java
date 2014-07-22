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
 * Http���������
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
	 * ��ȡ����ӿ����߳�
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class RequestThread extends Thread {
		private Context context;
		private URL mUrl = null;
		private FetchDataListener mFetchDataListener;
		private String fileName = "";// �����ļ�����
		private boolean useLocalFirst = true;// �Ƿ�����ʹ�ñ�������
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
			// ������ڱ������ݣ�ֱ�ӷ���
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
		 * ���粻�õ�����»�ȡ��������
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
		 * ���ر�������
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
		 * ��ȡ��������
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
				PrintHelper.print(name + "�ļ����쳣�޸ģ��޷���װ��json���ݣ���");
				FileManager.deleteFile(name);
				return null;
			}
			return data;
		}

		/**
		 * ��ʼ�ж��ĸ���ʱ��仯��,�����HTTP����ɹ��ģ���ô����Ϊ���仯
		 */
		private void reSetUpdateTime() {
			if (TextUtils.isEmpty(fileName))
				return;
			// columnʱ��ֻ�Ƚ���ҳ����Ŀ��ҳ�������ж�Ϊ����ͬʱ�Ѿ�ɾ��
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
