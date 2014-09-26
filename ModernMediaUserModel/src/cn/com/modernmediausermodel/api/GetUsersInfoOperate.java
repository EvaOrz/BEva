package cn.com.modernmediausermodel.api;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediausermodel.db.UserInfoDb;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.Users;

/**
 * 获取用户信息
 * 
 * @author user
 * 
 */
public class GetUsersInfoOperate extends SlateBaseOperate {
	private Users users;
	private Set<String> uidSet;
	private String uids;
	private UserInfoDb db;

	public GetUsersInfoOperate(Context context) {
		users = new Users();
		db = UserInfoDb.getInstance(context);
	}

	public Users getUsers() {
		return users;
	}

	public void setUids(Set<String> uidSet) {
		this.uidSet = uidSet;
	}

	@Override
	protected String getUrl() {
		StringBuilder builder = new StringBuilder();
		for (String id : uidSet) {
			builder.append(id).append(",");
		}
		String str = builder.toString();
		if (str.length() > 0) {
			uids = str.substring(0, str.length() - 1);
		}
		return UrlMaker.getUsersInfo() + "&uids=" + uids;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray array = jsonObject.optJSONArray("user");
		if (isNull(array))
			return;
		int len = array.length();
		for (int i = 0; i < len; i++) {
			JSONObject object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			User user = new User();
			user.setUid(object.optString("uid", ""));
			user.setUserName(object.optString("username", ""));
			user.setNickName(object.optString("nickname", ""));
			user.setAvatar(object.optString("avatar", ""));
			users.getUserList().add(user);
			users.getUserInfoMap().put(user.getUid(), user);
		}
		db.addUsersInfo(users.getUserList());
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}
}
