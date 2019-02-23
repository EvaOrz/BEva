package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 绑定账号： 手机、邮箱、微博、微信、QQ
 *
 * @author lusiyuan
 *
 */
public class BandAccountOperate extends UserModelBaseOperate {
	private ErrorMsg error;
	public static int PHONE = 1;
	public static int EMAIL = 2;
	public static int WEIXIN = 3;
	public static int WEIBO = 4;
	public static int QQ = 5;

	public BandAccountOperate(String uid, int bindType, String token,
			String userName, String code) {
		error = new ErrorMsg();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			addPostParams(postObject, "uid", uid);
			addPostParams(postObject, "token", token);
			addPostParams(postObject, "appid", UserConstData.getInitialAppId()
					+ "");
			if (bindType == PHONE) {
				addPostParams(postObject, "phone", userName);
				addPostParams(postObject, "bindtype", "phone");
				addPostParams(postObject, "code", code);
			} else if (bindType == EMAIL) {
				addPostParams(postObject, "username", userName);
				addPostParams(postObject, "email", userName);
				addPostParams(postObject, "bindtype", "email");
			} else if (bindType == WEIXIN) {
				addPostParams(postObject, "sinaid", userName);
				addPostParams(postObject,"unionid", code);
				addPostParams(postObject, "bindtype", "weixin");
			} else if (bindType == WEIBO) {
				addPostParams(postObject, "sinaid", userName);
				addPostParams(postObject, "bindtype", "weibo");
			} else if (bindType == QQ) {
				addPostParams(postObject, "sinaid", userName);
				addPostParams(postObject, "bindtype", "qq");
			}

			params.add(new BasicNameValuePair("data", postObject.toString()));
			setPostParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected ErrorMsg getError() {
		return error;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.bandAccount();
	}


	@Override
	protected void handler(JSONObject jsonObject) {
		JSONObject object = jsonObject.optJSONObject("error");
		if (object != null) {
			error.setNo(object.optInt("no", 0));
			error.setDesc(object.optString("desc", ""));
			if (error.getNo() == 0) {
				error.setDesc(jsonObject.optString("jsonObject"));
			}
		}
	}

	@Override
	protected void saveData(String data) {

	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}



}
