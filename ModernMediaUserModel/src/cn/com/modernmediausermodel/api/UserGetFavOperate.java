package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 获取收藏列表
 * 
 * @author ZhuQiao
 * 
 */
public class UserGetFavOperate extends UserFavBaseOperate {

	public UserGetFavOperate(String uid) {
		super();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			object.put("uid", uid);
			object.put("appid", UserConstData.getAppId() + "");
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getFva();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		super.handler(jsonObject);
	}

}
