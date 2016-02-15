package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import cn.com.modernmediausermodel.model.UserCent;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserFileManager;

/**
 * 用户积分接口operate
 * 
 * @author JianCong
 * 
 */
public class GetUserCoinNumberOperate extends UserBaseOperate {
	private ArrayList<NameValuePair> nameValuePairs; // post参数
	private String uid;
	private String token;
	private UserCent userCent;

	protected GetUserCoinNumberOperate(String uid, String token) {
		this.uid = uid;
		this.token = token;
		userCent = new UserCent();
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getUserCent(uid) + "&token=" + token;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		userCent.setUid(jsonObject.optString("uid", ""));
		userCent.setNumber(jsonObject.optInt("cent", 0));
	}

	@Override
	protected void saveData(String data) {
		UserFileManager.saveApiData(UserConstData.getUserCentFileName(uid), data);
	}

	@Override
	protected String getDefaultFileName() {
		return UserConstData.getUserCentFileName(uid);
	}

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return nameValuePairs;
	}

	protected void setPostParams(ArrayList<NameValuePair> params) {
		this.nameValuePairs = params;
	}

	protected UserCent getUserCent() {
		return userCent;
	}
}
