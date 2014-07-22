package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmediausermodel.util.UserConstData;

public class UserLoginOperate extends UserModelBaseOperate {

	protected UserLoginOperate(String userName, String password) {
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			object.put("username", userName);
			object.put("password", password);
			object.put("appid", UserConstData.getAppId() + "");
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getLoginUrl();
	}

}
