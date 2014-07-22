package cn.com.modernmediausermodel.api;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 修改用户信息
 * 
 * @author ZhuQiao
 * 
 */
public class ModifyUserInfoOperate extends UserModelBaseOperate {

	protected ModifyUserInfoOperate(String uid, String token, String userName,
			String nickName, String url) {
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			object.put("uid", uid);
			object.put("token", token);
			object.put("username", userName);
			if (!TextUtils.isEmpty(nickName)) {
				nickName = URLEncoder.encode(nickName, "UTF-8");
				object.put("nickname", nickName);
			}
			if (!TextUtils.isEmpty(url)) {
				object.put("avatar", url);
			}
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getModifyInfoUrl();
	}

}
