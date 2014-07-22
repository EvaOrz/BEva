package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 新浪微博账号登录
 * 
 * @author jiancong
 * 
 */
public class SinaLoginOperate extends UserModelBaseOperate {

	protected SinaLoginOperate(Context context, User user, String avatar) {
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			addPostParams(object, "username", user.getUserName());
			addPostParams(object, "nickname", user.getNickName());
			addPostParams(object, "avatar", avatar);
			object.put("sinaid", user.getSinaId());
			object.put("deviceid", UserTools.getDeviceId(context));
			object.put("appid", UserConstData.getAppId() + "");
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getSinaLoginUrl();
	}

}
