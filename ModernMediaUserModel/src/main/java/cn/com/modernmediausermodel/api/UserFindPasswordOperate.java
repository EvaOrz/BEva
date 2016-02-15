package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 忘记密码
 * 
 * @author lusiyuan
 *
 */
public class UserFindPasswordOperate extends UserModelBaseOperate {
	/**
	 * 邮箱
	 * 
	 * @param userName
	 */
	protected UserFindPasswordOperate(String userName, String code,
			String newPwd) {
		super();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			object.put("username", userName);
			object.put("appid", UserConstData.getInitialAppId());
			if (code != null)
				object.put("code", code);
			if (newPwd != null)
				object.put("newpassword", newPwd);
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getPasswordUrl();
	}

	@Override
	protected void getIssueLevel() {
		// TODO Auto-generated method stub
		
	}

}
