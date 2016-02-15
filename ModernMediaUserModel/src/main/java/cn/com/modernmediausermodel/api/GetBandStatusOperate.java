package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 获取用户绑定信息
 * 
 * @author lusiyuan
 *
 */
public class GetBandStatusOperate extends UserModelBaseOperate {
	private String uid;
	private ErrorMsg error;
	private User user;

	public GetBandStatusOperate(String uid, String token) {
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		user = new User();
		JSONObject postObject = new JSONObject();
		try {
			addPostParams(postObject, "uid", uid);
			addPostParams(postObject, "token", token);
			addPostParams(postObject, "appid", UserConstData.getInitialAppId()
					+ "");
			params.add(new BasicNameValuePair("data", postObject.toString()));
			setPostParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getBandStatus();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONObject object = jsonObject.optJSONObject("error");
		if (object != null) {
			error.setNo(object.optInt("no", 0));
			error.setDesc(object.optString("desc", ""));
		} else {
			user.setBandPhone(getBool(jsonObject.optInt("phone")));
			user.setBandEmail(getBool(jsonObject.optInt("email")));
			user.setBandWeixin(getBool(jsonObject.optInt("weixin")));
			user.setBandWeibo(getBool(jsonObject.optInt("weibo")));
			user.setBandQQ(getBool(jsonObject.optInt("qq")));
		}
	}

	private boolean getBool(int i) {
		return i == 0 ? false : true;
	}

	public User getStatus() {
		return user;
	}

	@Override
	protected void saveData(String data) {

	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	@Override
	protected void getIssueLevel() {
		// TODO Auto-generated method stub
		
	}

}
