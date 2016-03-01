package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 注册api
 * 
 * @author lusiyuan
 * 
 */
public class UserRegisterOperate extends UserModelBaseOperate {

	protected UserRegisterOperate(String userName, String password,
			String code, String phone, String nick) {
		super();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			object.put("username", userName);
			object.put("password", password);
			object.put("appid", UserConstData.getInitialAppId() + "");
			
			if (code != null && code.length() > 0)
				object.put("code", code);// 验证码
			if (phone != null && phone.length() > 0)
				object.put("phone", phone);// 电话号码
			if (nick != null && nick.length() > 0) {
				object.put("nickname", nick);// nick name
			}
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getRegisterUrl();
	}

	@Override
	protected void getIssueLevel() {

	}
}
