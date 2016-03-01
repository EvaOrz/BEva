package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.model.VerifyCode;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 获取验证码
 * 
 * @author lusiyuan
 *
 */
public class GetVerifyCodeOperate extends SlateBaseOperate {
	private ErrorMsg error;
	private VerifyCode code;// 验证码
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
			addPostParams(postObject, "uid", "");
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

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONObject object = jsonObject.optJSONObject("error");
		if (object != null) {
			error.setNo(object.optInt("no", 0));
			error.setDesc(object.optString("desc", ""));
		} else
			code = new VerifyCode(jsonObject);
	}

	public VerifyCode getCode() {
		return this.code;
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

}
