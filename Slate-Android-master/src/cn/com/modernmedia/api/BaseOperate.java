package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.BaseFragmentActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.HttpRequestController.RequestThread;
import cn.com.modernmedia.listener.DataCallBack;
import cn.com.modernmedia.listener.FetchDataListener;
import cn.com.modernmedia.model.Adv;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.PrintHelper;

/**
 * ��װ�������������ݸ���
 * 
 * @author ZhuQiao
 * 
 */
public abstract class BaseOperate {
	private HttpRequestController mController = HttpRequestController
			.getInstance();
	private Context mContext;
	private boolean success = false;// �Ƿ�����ɹ�

	/**
	 * �������ṩ
	 * 
	 * @param url
	 */
	protected abstract String getUrl();

	/**
	 * �����������������Json����
	 * 
	 * @param context
	 * @param useLocalFirst
	 *            �Ƿ�����ʹ�ñ�������
	 * @param callBack
	 */
	protected void asyncRequest(Context context, boolean useLocalFirst,
			DataCallBack callBack) {
		mContext = context;
		// if (!SystemHelper.checkNetWork(context)) {
		// // TODO ��ʾ������Ϣ
		// return;
		// }
		if (TextUtils.isEmpty(getUrl()) || callBack == null) {
			// TODO ��ʾ������Ϣ
			return;
		}
		request(useLocalFirst, callBack);
	}

	private void request(boolean useLocalFirst, final DataCallBack callBack) {
		String url = getUrl();
		RequestThread thread = new RequestThread(mContext, url);
		thread.setFileName(getDefaultFileName());
		thread.setUseLocalFirst(useLocalFirst);
		thread.setmFetchDataListener(new FetchDataListener() {

			@Override
			public void fetchData(boolean isSuccess, String data) {
				if (isSuccess) {
					if (TextUtils.isEmpty(data)) {
						showToast(R.string.net_error);
					} else {
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
							PrintHelper.print(getUrl()
									+ ":can not transform to jsonobject");
							e.printStackTrace();
							showToast(R.string.json_error);
						}
					}
				} else {
					// TODO ��ʾ������Ϣ
					showToast(R.string.net_error);
				}
				callBack.callback(success);
			}
		});
		mController.fetchHttpData(thread);
	}

	/**
	 * ������ȥ����json����
	 * 
	 * @param jsonObject
	 */
	protected abstract void handler(JSONObject jsonObject);

	/**
	 * �����걣������
	 * 
	 * @param data
	 */
	protected abstract void saveData(String data);

	/**
	 * ��ȡĬ�ϵ��ļ���(���ڲ���Ҫgetissue�ӿڣ������ӿڶ�����getissue֮����еĲ�����getissueʱ�������Ӧ�Ļ��ߣ�
	 * ��������Ĭ�ϵ��ļ���׺����sharep�����)
	 * 
	 * @return
	 */
	protected abstract String getDefaultFileName();

	private void showToast(int resId) {
		// ������ʾ��ͳ�Ʋ���ʾ������Ϣ
		if (TextUtils.isEmpty(getDefaultFileName()))
			return;
		if (mContext != null) {
			if (mContext instanceof BaseActivity) {
				((BaseActivity) mContext).showToast(resId);
			} else if (mContext instanceof BaseFragmentActivity) {
				((BaseFragmentActivity) mContext).showToast(resId);
			}
		}
	}

	public Context getmContext() {
		return mContext;
	}

	/**
	 * JSONObject�Ƿ�Ϊ��
	 * 
	 * @param object
	 * @return
	 */
	protected boolean isNull(JSONObject object) {
		return JSONObject.NULL.equals(object) || object == null;
	}

	/**
	 * JSONArray�Ƿ�Ϊ��
	 * 
	 * @param array
	 * @return
	 */
	protected boolean isNull(JSONArray array) {
		return JSONObject.NULL.equals(array) || array == null
				|| array.length() == 0;
	}

	/**
	 * �������
	 * 
	 * @param object
	 * @return
	 */
	protected Adv parseAdv(JSONObject obj) {
		Adv adv = new Adv();
		JSONObject property = obj.optJSONObject("property");
		if (isNull(property))
			return adv;
		int isadv = property.optInt("isadv", 0);
		if (isadv != 1)
			return adv;
		adv.getAdvProperty().setIsadv(isadv);
		JSONArray columnadv = obj.optJSONArray("columnadv");
		if (isNull(columnadv))
			return adv;
		JSONObject columnadvObj = columnadv.optJSONObject(0);
		if (isNull(columnadvObj))
			return adv;
		String starttime = columnadvObj.optString("starttime", "0");
		String endtime = columnadvObj.optString("endtime", "0");
		long currentTime = System.currentTimeMillis() / 1000;
		if (currentTime > ParseUtil.stol(starttime)
				&& currentTime < ParseUtil.stol(endtime)) {
			adv.getColumnAdv().setId(columnadvObj.optInt("id", 0));
			adv.getColumnAdv().setLink(columnadvObj.optString("link", ""));
			adv.getColumnAdv().setStartTime(starttime);
			adv.getColumnAdv().setEndTime(endtime);
			JSONArray picture_h = columnadvObj.optJSONArray("picture_h");
			if (isNull(picture_h))
				return adv;
			JSONObject picture_h_json = picture_h.optJSONObject(0);
			if (isNull(picture_h_json))
				return adv;
			adv.getColumnAdv().setUrl(picture_h_json.optString("url", ""));
		} else {
			adv.setExpired(true);
		}
		return adv;
	}
}
