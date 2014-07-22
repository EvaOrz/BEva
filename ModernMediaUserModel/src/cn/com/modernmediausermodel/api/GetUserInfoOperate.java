package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class GetUserInfoOperate extends UserModelBaseOperate {

	protected GetUserInfoOperate(String uid, String token) {
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			object.put("uid", uid);
			object.put("token", token);
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getUserInfoUrlByUidAndToken();
	}

}
