package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 开放平台(新浪微博、QQ、微信)账号登录
 * 
 * @author jiancong
 * 
 */
public class OpenLoginOperate extends UserModelBaseOperate {
	private int mType = 0; // 开放平台类型(1:新浪微博；2:QQ；3:微信)
	private Context context;

	protected OpenLoginOperate(Context context, User user, String avatar,
			int type) {
		super();
		this.context = context;
		mType = type;
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {

			addPostParams(object, "nickname", user.getNickName());
			addPostParams(object, "avatar", avatar);
			if (type == 1) { // 新浪微博
				addPostParams(object, "username", user.getSinaId());
				object.put("sinaid", user.getSinaId());
			} else if (type == 2) { // qq
				addPostParams(object, "username", user.getQqId());
				object.put("sinaid", user.getQqId());
			} else if (type == 3) { // 微信
				addPostParams(object, "username", user.getWeixinId());
				object.put("sinaid", user.getWeixinId());
			} else { // 其他默认普通登录
				addPostParams(object, "username", user.getUserName());
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
	protected void parseUser(JSONObject object) {
		super.parseUser(object);
		if (!TextUtils.isEmpty(user.getSinaId()) && mType == 2) { // QQ账号登录
			user.setQqId(user.getSinaId());
			user.setSinaId("");
		} else if (!TextUtils.isEmpty(user.getSinaId()) && mType == 3) {// 微信账号登录
			user.setWeixinId(user.getSinaId());
			user.setSinaId("");
		}
		getIssueLevel();
	}

	/**
	 * 第三方登陆之后，取用户的付费权限
	 */
	@Override
	protected void getIssueLevel() {
		OperateController.getInstance(context).getUserPermission(context,
				user.getToken(), new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {

					}
				});
	}
}
