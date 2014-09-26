package cn.com.modernmediaslate.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;

import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.FileBody;
import com.parse.entity.mime.content.StringBody;

/**
 * Http请求控制器
 * 
 * @author ZhuQiao
 * 
 */
public class HttpRequestController {
	/** 连接超时时间 **/
	public static final int CONNECT_TIMEOUT = 10 * 1000;
	/** 读取数据超时时间 **/
	public static final int READ_TIMEOUT = 10 * 1000;
	/** 读取数据buffer大小 **/
	public static final int BUFFERSIZE = 1024;
	private static HttpRequestController instance = null;

	private HttpRequestController() {
	}

	public static HttpRequestController getInstance() {
		if (instance == null) {
			instance = new HttpRequestController();
		}
		return instance;
	}

	public void fetchHttpData(RequestThread requestThread) {
		requestThread.start();
	}

	public void requestHttp(final String url) {
		requestHttp(url, null);
	}

	public void requestHttp(final String url, final String userAgent) {
		if (TextUtils.isEmpty(url))
			return;
		new Thread() {

			@Override
			public void run() {
				try {
					URL mUrl = new URL(url);
					HttpURLConnection conn = null;
					conn = (HttpURLConnection) mUrl.openConnection();
					if (!TextUtils.isEmpty(userAgent)) {
						conn.addRequestProperty("User-Agent", userAgent);
					}
					// connect()函数，实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。
					// 无论是post还是get，http请求实际上直到HttpURLConnection的getInputStream()这个函数里面才正式发送出去。
					conn.getInputStream();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

	/**
	 * 读取网络接口子线程
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class RequestThread extends Thread {
		@SuppressWarnings("unused")
		private Context context;
		private URL mUrl = null;
		private FetchDataListener mFetchDataListener;
		private String url = "";
		private boolean isPost = false; // 默认用GET方式
		private List<NameValuePair> params; // post用参数
		private String imagePath; // 要上传的图片存储路径(目前仅支持post方式)
		private SlateBaseOperate mOperate;
		private String userAgent = "";// iweekly统计广告使用

		public RequestThread(Context context, String url,
				SlateBaseOperate baseOperate) {
			this.context = context;
			this.url = url == null ? "" : url;
			mOperate = baseOperate;
			if (!TextUtils.isEmpty(url)) {
				try {
					mUrl = new URL(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		public void setmFetchDataListener(FetchDataListener mFetchDataListener) {
			this.mFetchDataListener = mFetchDataListener;
		}

		protected boolean isPost() {
			return isPost;
		}

		protected void setPost(boolean isPost) {
			this.isPost = isPost;
		}

		protected void setPostParams(ArrayList<NameValuePair> nameValuePairs) {
			this.params = nameValuePairs;
		}

		protected void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}

		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}

		@Override
		public void run() {
			if (mUrl == null) {
				return;
			}
			if (!isPost) {
				HttpURLConnection conn = null;
				try {
					conn = (HttpURLConnection) mUrl.openConnection();
					if (!TextUtils.isEmpty(userAgent)) {
						conn.addRequestProperty("User-Agent", userAgent);
					}
					conn.setConnectTimeout(CONNECT_TIMEOUT);
					conn.setReadTimeout(READ_TIMEOUT);
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
						mFetchDataListener.fetchData(true, data, true);
						reSetUpdateTime();
						SlatePrintHelper.print("from http:" + url);
					} else {
						fetchLocalDataInBadNet();
					}
				} catch (Exception e) {
					e.printStackTrace();
					fetchLocalDataInBadNet();
				} finally {
					if (conn != null)
						conn.disconnect();
				}
			} else { // post方式
				if ((params != null && !params.isEmpty())
						|| !TextUtils.isEmpty(imagePath)) {
					HttpPost httpPost = null;
					HttpClient httpClient = null;
					try {
						httpPost = new HttpPost(url);
						MultipartEntity reqEntity = new MultipartEntity();
						// 上传图片处理
						if (!TextUtils.isEmpty(imagePath)) {
							FileBody fileBody = new FileBody(
									new File(imagePath), "image/png");
							reqEntity.addPart("image", fileBody);
						}
						// 参数设置
						if (params != null) {
							for (NameValuePair param : params) {
								String value = param.getValue();
								if (!TextUtils.isEmpty(value)) {
									reqEntity.addPart(param.getName(),
											new StringBody(value));
								}
							}
						}
						httpPost.setEntity(reqEntity);
						httpClient = new DefaultHttpClient();
						// 请求超时
						httpClient.getParams().setParameter(
								CoreConnectionPNames.CONNECTION_TIMEOUT,
								CONNECT_TIMEOUT);
						// 读取超时
						httpClient.getParams().setParameter(
								CoreConnectionPNames.SO_TIMEOUT, READ_TIMEOUT);
						HttpResponse response = httpClient.execute(httpPost);
						String resData = null;
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							HttpEntity resEntity = response.getEntity();
							resData = (resEntity == null) ? null : EntityUtils
									.toString(resEntity, HTTP.UTF_8);
						}
						if (resData == null) {
							fetchLocalDataInBadNet();
							return;
						}
						showToast("from http:" + url);
						mFetchDataListener.fetchData(true, resData, true);
						reSetUpdateTime();
						SlatePrintHelper.print("from http:" + url);
					} catch (Exception e) {
						fetchLocalDataInBadNet();
						e.printStackTrace();
					} finally {
						if (httpPost != null)
							httpPost.abort();
						if (httpClient != null)
							httpClient.getConnectionManager().shutdown();
					}
				}
			}
		}

		private String receiveData(InputStream is) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byte[] buff = new byte[BUFFERSIZE];
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

		private void fetchLocalDataInBadNet() {
			if (mOperate != null)
				mOperate.fetchLocalDataInBadNet(mFetchDataListener);
		}

		private void reSetUpdateTime() {
			if (mOperate != null)
				mOperate.reSetUpdateTime();
		}

		private void showToast(String str) {
		}
	}
}
