package cn.com.modernmediausermodel.api;

import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserFileManager;

public class GetUserCardInfoOperate extends UserBaseOperate {
	private UserCardInfo userCardInfo;
	private String uid = ""; // 要查看的用户的uid
	private String customerUid = ""; // 当前登录用户的uid

	public UserCardInfo getUserCardInfo() {
		return userCardInfo;
	}

	public GetUserCardInfoOperate(String uid, String customerUid) {
		this.userCardInfo = new UserCardInfo();
		this.uid = uid;
		this.customerUid = customerUid;
	}

	@Override
	protected String getUrl() {
		String url = UrlMaker.getUserCardInfo() + "/uid/" + uid;
		if (!TextUtils.isEmpty(customerUid)) {
			url += "/customer_uid/" + customerUid;
		}
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		userCardInfo.setUid(jsonObject.optString("uid", ""));
		userCardInfo.setFollowNum(jsonObject.optInt("follow", 0));
		userCardInfo.setFansNum(jsonObject.optInt("follower", 0));
		userCardInfo.setCardNum(jsonObject.optInt("cardnum", 0));
		userCardInfo.setNickName(jsonObject.optString("nickname", ""));
		userCardInfo.setAvatar(jsonObject.optString("avatar", ""));
		// 当前登录用户与查看用户是否为收藏关系，若查看自己，则无用
		userCardInfo.setIsFollowed(jsonObject.optInt("isfollow", 0));
	}

	@Override
	protected void saveData(String data) {
		String fileName = UserConstData.getUserCardInfoFileName(uid);
		UserFileManager.saveApiData(fileName, data);
	}

	@Override
	protected String getDefaultFileName() {
		return UserConstData.getUserCardInfoFileName(uid);
	}
}
