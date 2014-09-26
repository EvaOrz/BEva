package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.newtag.db.UserSubscribeListDb;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.model.Entry;

/**
 * 取用户订阅栏目列表
 * 
 * @author jiancong
 * 
 */
public class GetUserSubscribeListOpertate extends BaseOperate {
	private String uid; // 用户userId
	private String token;
	private SubscribeOrderList subScribeOrderList;

	public GetUserSubscribeListOpertate(String uid, String token) {
		this.uid = uid;
		this.token = TextUtils.isEmpty(token) ? "" : token;
		subScribeOrderList = new SubscribeOrderList();
		cacheIsDb = true;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getUserSubscribeList(uid, token);
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		subScribeOrderList.setUid(jsonObject.optString("uid"));
		subScribeOrderList.setAppId(jsonObject.optInt("appid"));
		subScribeOrderList.setColumnList(parseColumnList(jsonObject
				.optJSONArray("col")));
		UserSubscribeListDb.getInstance(getmContext()).clearTable(uid);
		UserSubscribeListDb.getInstance(getmContext()).addEntry(
				subScribeOrderList);
	}

	private List<SubscribeColumn> parseColumnList(JSONArray arr) {
		List<SubscribeColumn> list = new ArrayList<SubscribeColumn>();
		if (isNull(arr))
			return list;
		int len = arr.length();
		JSONObject object;
		SubscribeColumn column;
		for (int i = 0; i < len; i++) {
			object = arr.optJSONObject(i);
			if (isNull(object))
				continue;
			column = new SubscribeColumn();
			column.setName(object.optString("name"));
			column.setParent(object.optString("parent"));
			list.add(column);
		}
		return list;
	}

	@Override
	public boolean fecthLocalData(String fileName) {
		Entry entry = UserSubscribeListDb.getInstance(getmContext()).getEntry(
				this, uid);
		if (entry instanceof SubscribeOrderList) {
			subScribeOrderList = (SubscribeOrderList) entry;
			if (callBack != null) {
				PrintHelper
						.print("from db:getUserSubscribeList====" + getUrl());
				callBack.callback(true, false);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	public SubscribeOrderList getSubscribeOrderList() {
		return subScribeOrderList;
	}

}
