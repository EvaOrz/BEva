package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cn.com.modernmediausermodel.util.UserConstData;

public class UserLoginOperate extends UserModelBaseOperate {

	protected UserLoginOperate(String userName, String password) {
		super();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			// username有邮箱check，可以不编码
			object.put("username", userName);
			// 密码在输入上已经做了限制，也可以不编码
			object.put("password", password);
			object.put("appid", UserConstData.getInitialAppId() + "");
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getLoginUrl();
	}

}
