package cn.com.modernmediaslate.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmediaslate.R;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.SlateBaseFragmentActivity;
import cn.com.modernmediaslate.api.HttpRequestController.RequestThread;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;

/**
 * 封装服务器返回数据父类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class SlateBaseOperate {
	private HttpRequestController mController = HttpRequestController
			.getInstance();
	private Context mContext;
	private boolean success = false;// 是否解析成功
	private boolean showToast = true;

	/**
	 * 由子类提供
	 * 
	 */
	protected abstract String getUrl();

	/**
	 * 由子类提供
	 * 
	 */
	protected ArrayList<NameValuePair> getPostParams() {
		return null;
	}

	/**
	 * 由子类提供
	 * 
	 */
	protected String getPostImagePath() {
		return null;
	}

	public void setShowToast(boolean showToast) {
		this.showToast = showToast;
	}

	/**
	 * 以get方式请求服务器，并解析Json数据
	 * 
	 * @param context
	 * @param useLocalFirst
	 *            是否优先使用本地数据
	 * @param callBack
	 */
	public void asyncRequest(Context context, boolean useLocalFirst,
			DataCallBack callBack) {
		mContext = context;
		if (TextUtils.isEmpty(getUrl()) || callBack == null) {
			// TODO 提示错误信息
			return;
		}
		request(useLocalFirst, false, callBack);
	}

	/**
	 * 以post方式请求服务器，并解析Json数据
	 * 
	 * @param context
	 * @param useLocalFirst
	 *            是否优先使用本地数据
	 * @param callBack
	 */
	public void asyncRequestByPost(Context context, boolean useLocalFirst,
			DataCallBack callBack) {
		mContext = context;
		if (TextUtils.isEmpty(getUrl()) || callBack == null) {
			// TODO 提示错误信息
			return;
		}
		request(useLocalFirst, true, callBack);
	}

	/**
	 * 
	 * @param useLocalFirst
	 *            是否优先用本地数据
	 * @param callBack
	 */
	private void request(boolean useLocalFirst, boolean isPost,
			final DataCallBack callBack) {
		if (useLocalFirst && fecthLocalData(getDefaultFileName(), callBack))
			return;
		String url = getUrl();
		RequestThread thread = new RequestThread(mContext, url, this);

		if (isPost) {
			thread.setPost(true);
			thread.setPostParams(getPostParams());
			thread.setImagePath(getPostImagePath());
		}

		thread.setmFetchDataListener(new FetchDataListener() {

			@Override
			public void fetchData(boolean isSuccess, String data) {
				handlerData(isSuccess, data, callBack);
			}
		});
		mController.fetchHttpData(thread);
	}

	/**
	 * 解析数据
	 * 
	 * @param isSuccess
	 * @param data
	 * @param callBack
	 */
	private void handlerData(boolean isSuccess, String data,
			DataCallBack callBack) {
		if (isSuccess) {
			if (TextUtils.isEmpty(data)) {
				showToast(R.string.net_error);
			} else {
				if (data.equals("[]")) {
					data = "{}";
				}
				try {
					JSONObject obj = new JSONObject(data);
					if (isNull(obj)) {
						showToast(R.string.net_error);
					} else {
						handler(obj);
						saveData(data);
						success = true;
					}
				} catch (JSONException e) {
					SlatePrintHelper.print(getUrl()
							+ ":can not transform to jsonobject");
					e.printStackTrace();
					showToast(R.string.net_error);
				}
			}
		} else {
			showToast(R.string.net_error);
		}
		callBack.callback(success);
	}

	private boolean fecthLocalData(String fileName, DataCallBack callBack) {
		if (TextUtils.isEmpty(fileName))
			return false;
		String localData = getLocalData(fileName);
		if (!TextUtils.isEmpty(localData)) {
			handlerData(true, localData, callBack);
			SlatePrintHelper.print("from sdcard:" + getUrl());
			return true;
		}
		return false;
	}

	/**
	 * 获取本地数据
	 * 
	 * @param fileName
	 *            文件名
	 * @return
	 */
	protected String getLocalData(String fileName) {
		return "";
	}

	/**
	 * 网络不好的情况下获取本地数据
	 */
	protected void fetchLocalDataInBadNet(FetchDataListener mFetchDataListener) {
	}

	/**
	 * 开始判定的更新时间变化了,如果从HTTP请求成功的，那么设置为不变化
	 */
	protected void reSetUpdateTime() {
	}

	/**
	 * 由子类去解析json数据
	 * 
	 * @param jsonObject
	 */
	protected abstract void handler(JSONObject jsonObject);

	/**
	 * 解析完保存数据
	 * 
	 * @param data
	 */
	protected abstract void saveData(String data);

	/**
	 * 获取默认的文件名(由于不需要getissue接口，其它接口都是在getissue之后进行的并且在getissue时保存过相应的护具，
	 * 所以所有默认的文件后缀都用sharep里面的)
	 * 
	 * @return
	 */
	protected abstract String getDefaultFileName();

	private void showToast(int resId) {
		// 升级提示和统计不提示错误信息
		if (showToast && mContext != null) {
			if (mContext instanceof SlateBaseActivity) {
				((SlateBaseActivity) mContext).showToast(resId);
			} else if (mContext instanceof SlateBaseFragmentActivity) {
				((SlateBaseFragmentActivity) mContext).showToast(resId);
			}
		}
	}

	public Context getmContext() {
		return mContext;
	}

	/**
	 * JSONObject是否为空
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isNull(JSONObject object) {
		return JSONObject.NULL.equals(object) || object == null;
	}

	/**
	 * JSONArray是否为空
	 * 
	 * @param array
	 * @return
	 */
	protected boolean isNull(JSONArray array) {
		return JSONObject.NULL.equals(array) || array == null
				|| array.length() == 0;
	}

}
