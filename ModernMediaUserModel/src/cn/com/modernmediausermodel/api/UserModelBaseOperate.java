package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.model.User;

/**
 * 用户模块基础operate
 * 
 * @author ZhuQiao
 * 
 */
public abstract class UserModelBaseOperate extends SlateBaseOperate {
	private User user;
	private ArrayList<NameValuePair> nameValuePairs; // post参数

	public User getUser() {
		return user;
	}

	protected UserModelBaseOperate() {
		this.user = new User();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		user = parseUser(jsonObject);
	}

	@Override
	protected void fetchLocalDataInBadNet(FetchDataListener mFetchDataListener) {
		SlatePrintHelper.print("net error:" + getUrl());
		mFetchDataListener.fetchData(false, null);
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return nameValuePairs;
	}

	protected void setPostParams(ArrayList<NameValuePair> params) {
		this.nameValuePairs = params;
	}

	/**
	 * 解析USER模块相关请求结果
	 * 
	 * @param object
	 *            待解析的JSON对象
	 * @return User对象
	 */
	protected User parseUser(JSONObject object) {
		User user = new User();
		user.setUid(object.optString("uid", ""));
		user.setUserName(object.optString("username", ""));
		user.setPassword(object.optString("password", ""));
		user.setNickName(object.optString("nickname", ""));
		user.setAvatar(object.optString("avatar", ""));
		user.setSinaId(object.optString("sinaid", ""));
		user.setToken(object.optString("token", ""));
		user.setDeviceId(object.optString("deviceid", ""));
		user.setDeviceToken(object.optString("devicetoken", ""));
		user.setNewPassword(object.optString("newpassword", ""));
		user.setAppid(object.optString("appid", ""));
		user.setVersion(object.optString("version", ""));
		user.setDesc(object.optString("desc", ""));
		JSONObject errorObject = object.optJSONObject("error");
		if (!isNull(errorObject)) {
			user.getError().setNo(errorObject.optInt("no", -1));
			user.getError().setDesc(errorObject.optString("desc", ""));
		}
		return user;
	}
}
