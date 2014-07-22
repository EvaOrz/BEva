package cn.com.modernmediausermodel.api;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import cn.com.modernmedia.api.BaseOperate;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.RecommendUserActivity;
import cn.com.modernmediausermodel.model.Users;
import cn.com.modernmediausermodel.model.Users.UserCardInfo;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserTools;

public class GetRecommendUsersOperate extends BaseOperate {

	private Users users;
	private String uid;
	private int pageType;

	public Users getUsers() {
		return users;
	}

	public GetRecommendUsersOperate(String uid, int pageType) {
		this.users = new Users();
		this.uid = uid;
		this.pageType = pageType;
	}

	@Override
	protected String getUrl() {
		String url = "";
		switch (pageType) {
		case RecommendUserActivity.PAGE_RECOMMEND_FRIEND:
			url = UrlMaker.getReccommendUsers();
			break;
		case RecommendUserActivity.PAGE_FRIEND:
			url = UrlMaker.getFriends() + "/uid/" + uid;
			break;
		case RecommendUserActivity.PAGE_FANS:
			url = UrlMaker.getFans() + "/uid/" + uid;
			break;
		default:
			break;
		}
		if (!TextUtils.isEmpty(url)) {
			url += "/customer_uid/" + UserTools.getUid(getmContext());
		}
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		users.setUid(jsonObject.optString("uid", ""));
		JSONArray array;
		if (pageType == RecommendUserActivity.PAGE_RECOMMEND_FRIEND) {
			array = jsonObject.optJSONArray("user");
		} else {
			array = jsonObject.optJSONArray("auid");
		}
		if (array != null) {
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject object = array.optJSONObject(i);
				if (object != null) {
					String uid = object.optString("uid", "");
					UserCardInfo userCardInfo = new UserCardInfo();
					userCardInfo.setFollowNum(object.optInt("follow", 0));
					userCardInfo.setFansNum(object.optInt("follower", 0));
					userCardInfo.setCardNum(object.optInt("cardnum", 0));
					userCardInfo.setIsFollowed(object.optInt("isfollow", 0));
					users.getUserCardInfoMap().put(uid, userCardInfo);
				}
			}
		}
		JSONObject error = jsonObject.optJSONObject("error");
		if (error != null) {
			users.getError().setNo(error.optInt("code", 0));
			users.getError().setDesc(error.optString("msg"));
		}
	}

	@Override
	protected void saveData(String data) {
		String fileName = "";
		if (pageType == RecommendUserActivity.PAGE_FRIEND) { // 好友
			fileName = UserConstData.getFrindsUidFileName(uid);
		} else if (pageType == RecommendUserActivity.PAGE_FANS) { // 粉丝
			fileName = UserConstData.getFansUidFileName(uid);
		}
		FileManager.saveApiData(fileName, data);
	}

	@Override
	protected String getDefaultFileName() {
		String fileName = "";
		if (pageType == RecommendUserActivity.PAGE_FRIEND) {
			fileName = UserConstData.getFrindsUidFileName(uid);
		} else if (pageType == RecommendUserActivity.PAGE_FANS) {
			fileName = UserConstData.getFansUidFileName(uid);
		}
		return fileName;
	}

	@Override
	protected void fetchLocalDataInBadNet(FetchDataListener mFetchDataListener) {
		if (pageType != RecommendUserActivity.PAGE_RECOMMEND_FRIEND) {
			super.fetchLocalDataInBadNet(mFetchDataListener);
		} else {
			SlatePrintHelper.print("net error:" + getUrl());
			mFetchDataListener.fetchData(false, null);
		}
	}
}
