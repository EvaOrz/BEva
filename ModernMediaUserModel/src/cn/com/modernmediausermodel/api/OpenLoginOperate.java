package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 开放平台(新浪微博、QQ)账号登录
 * 
 * @author jiancong
 * 
 */
public class OpenLoginOperate extends UserModelBaseOperate {
	private int mType = 0; // 开放平台类型(1:新浪微博；2:QQ)

	protected OpenLoginOperate(Context context, User user, String avatar,
			int type) {
		mType = type;
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			addPostParams(object, "username", user.getUserName());
			addPostParams(object, "nickname", user.getNickName());
			addPostParams(object, "avatar", avatar);
			if (type == 1) { // 新浪微博
				object.put("sinaid", user.getSinaId());
			} else if (type == 2) { // qq
				object.put("sinaid", user.getQqId());
			} else { // 其他默认普通登录
				type = 0;
				object.put("sinaid", user.getUid());
			}
			object.put("logintype", type + "");
			object.put("deviceid", UserTools.getDeviceId(context));
			object.put("appid", UserConstData.getInitialAppId() + "");
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getOpenLoginUrl();
	}

	@Override
	protected User parseUser(JSONObject object) {
		User tempUser = super.parseUser(object);
		if (!TextUtils.isEmpty(tempUser.getSinaId()) && mType == 2) { // QQ账号登录
			tempUser.setQqId(tempUser.getSinaId());
			tempUser.setSinaId("");
		}
		return tempUser;
	}
}
