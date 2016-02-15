package cn.com.modernmedia.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DESCoder;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 获取用户支付权限接口
 * 
 * @author lusiyuan
 *
 */
public class GetUserPermissionOperate extends BaseOperate {
	private String token;
	private Context context;
	private User user;
	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

	public GetUserPermissionOperate(Context context, String token) {
		this.context = context;
		user = new User();
		// post 参数设置
		JSONObject object = new JSONObject();
		try {
			String uid = SlateDataHelper.getUid(context);
			object.put("usertoken", token);
			object.put("appid", ConstData.getInitialAppId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params.add(new BasicNameValuePair("data", object.toString()));
	}

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return params;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getUserPermisson(token);
	}

	// {"level":1,"endTime":2397283484}
	@Override
	protected void handler(JSONObject jsonObject) {
		if (jsonObject != null) {

			String token = SlateDataHelper.getToken(context);
			String key = token.substring(token.length() - 8, token.length());// 解析的key
			try {
				String json = DESCoder
						.decode(key, jsonObject.optString("data"));
				JSONObject j = new JSONObject(json);
				if (j != null) {// 解析服务器返回的权限值，刷新本地存储
					SlateDataHelper
							.setIssueLevel(context, j.getString("level"));
					SlateDataHelper.setEndTime(context, j.getLong("endTime"));
					Log.e("getUserPermisson", j.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void saveData(String data) {

	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	/**
	 * 传递是否获取服务器返回数据
	 * 
	 * @return
	 */
	public User getPermission() {
		return user;
	}

}
