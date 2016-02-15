package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.util.UserConstData;

public class UserLoginOutOperate extends UserModelBaseOperate {
	private Context context;

	protected UserLoginOutOperate(Context context, String uid, String token) {
		super();
		this.context = context;
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			object.put("uid", uid);
			object.put("token", token);
			object.put("appid", UserConstData.getInitialAppId() + "");
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getLoginOutUrl();
	}

	/**
	 * 退出登陆，重置支付权限 未付费：“0”
	 */
	@Override
	protected void getIssueLevel() {
		SlateDataHelper.setIssueLevel(context, "0");
	}

}
