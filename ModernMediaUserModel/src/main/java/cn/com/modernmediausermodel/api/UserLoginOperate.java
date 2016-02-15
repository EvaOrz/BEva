package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.util.UserConstData;

public class UserLoginOperate extends UserModelBaseOperate {
	private Context context;

	protected UserLoginOperate(Context context, String userName, String password) {
		super();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject object = new JSONObject();
		try {
			// username有邮箱check，可以不编码
			object.put("username", userName);
			// 密码在输入上已经做了限制，也可以不编码
			object.put("password", password);
			object.put("appid", UserConstData.getInitialAppId() + "");
			params.add(new BasicNameValuePair("data", object.toString()));
			setPostParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String getUrl() {
		return UrlMaker.getLoginUrl();
	}

	@Override
	protected void parseUser(JSONObject object) {
		super.parseUser(object);
		getIssueLevel();
	}

	/**
	 * 登陆成功后，向server端获取用户付费信息
	 */
	protected void getIssueLevel() {
		OperateController.getInstance(context).getUserPermission(context,
				user.getToken(), new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {

					}
				});
	}
}
