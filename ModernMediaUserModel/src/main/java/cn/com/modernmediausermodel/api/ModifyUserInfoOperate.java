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

	/**
	 * 修改用户资料 password为空时，更新用户昵称、头像；反之则更新邮箱
	 * 
	 * @param uid
	 * @param token
	 * @param userName
	 * @param nickName
	 * @param url
	 * @param password
	 * @param desc
	 */
	protected ModifyUserInfoOperate(String uid, String token, String userName,
			String nickName, String url, String password, String desc) {
		super();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			object.put("uid", uid);
			object.put("token", token);
			if (!TextUtils.isEmpty(password)) {
				object.put("password", password);
				if (!TextUtils.isEmpty(userName)) {
					userName = URLEncoder.encode(userName, "UTF-8");
					object.put("username", userName);
				}
			} else {
				if (!TextUtils.isEmpty(nickName)) {
					nickName = URLEncoder.encode(nickName, "UTF-8");
					object.put("nickname", nickName);
				}
				if (!TextUtils.isEmpty(url))
					object.put("avatar", url);
				if (!TextUtils.isEmpty(desc))
					object.put("desc", URLEncoder.encode(desc, "UTF-8"));
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

	@Override
	protected void getIssueLevel() {

	}

}
