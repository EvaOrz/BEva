package cn.com.modernmediausermodel.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;

/**
 * 取消关注operate
 * 
 * @author JianCong
 * 
 */
public class DeleteFollowOperate extends SlateBaseOperate {
	private Error error;
	private ArrayList<NameValuePair> nameValuePairs; // post参数
	private int num;
	private boolean refreshList;// 是否需要刷新朋友页

	public Error getError() {
		return error;
	}

	protected DeleteFollowOperate(String uid, List<User> unfollowedUsers,
			boolean refreshList) {
		this.refreshList = refreshList;
		this.error = new Error();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			postObject.put("uid", uid);
			JSONArray array = new JSONArray();
			for (User user : unfollowedUsers) {
				JSONObject object = new JSONObject();
				object.put("uid", user.getUid());
				array.put(object);
			}
			postObject.put("auid", array);
			params.add(new BasicNameValuePair("data", postObject.toString()));
			setPostParams(params);
			if (ParseUtil.listNotNull(unfollowedUsers)) {
				num = unfollowedUsers.size();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getDelFollow();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONObject object = jsonObject.optJSONObject("error");
		if (object != null) {
			error.setNo(object.optInt("code", 0));
			error.setDesc(object.optString("msg", ""));
			if (UserApplication.infoChangeListener != null
					&& error.getNo() == 0) {
				UserApplication.infoChangeListener.deleteFollow(num);
			}
			if (refreshList && UserApplication.recommInfoChangeListener != null
					&& error.getNo() == 0) {
				UserApplication.recommInfoChangeListener.deleteFollow(num);
			}
		}
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
}
