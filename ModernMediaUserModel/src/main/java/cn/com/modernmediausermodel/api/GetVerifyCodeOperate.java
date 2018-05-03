package cn.com.modernmediausermodel.api;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 获取验证码
 * 
 * @author lusiyuan
 *
 */
public class GetVerifyCodeOperate extends SlateBaseOperate {
	private ErrorMsg error;
	private String phone;
	private ArrayList<NameValuePair> nameValuePairs; // post参数

	protected GetVerifyCodeOperate(String phone) {
		this.error = new ErrorMsg();
		this.phone = phone;
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			addPostParams(postObject, "phone", phone);
//			addPostParams(postObject, "uid", "");
			addPostParams(postObject, "appid", String.valueOf(UserConstData.getInitialAppId()));
			params.add(new BasicNameValuePair("data", postObject.toString()));
			setPostParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return nameValuePairs;
	}

	protected void setPostParams(ArrayList<NameValuePair> params) {
		this.nameValuePairs = params;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getVerifyCode();
	}

	//{"error":{"no":1000,"desc":"发送验证码失败，请稍后再试"},"appid":1,"phone":"15910725520"}
	@Override
	protected void handler(JSONObject jsonObject) {
		if(!isNull(jsonObject)){
			JSONObject object = jsonObject.optJSONObject("error");
			if (!isNull(object)) {
				error.setNo(object.optInt("no", -1));
				error.setDesc(object.optString("desc", ""));
			}
		}

	}

	public ErrorMsg getCode() {
		return this.error;
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

}
