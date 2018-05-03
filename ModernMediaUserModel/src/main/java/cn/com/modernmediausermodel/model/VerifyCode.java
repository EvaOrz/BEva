package cn.com.modernmediausermodel.model;

import org.json.JSONObject;

import cn.com.modernmediaslate.model.Entry;

/**
 * 手机验证码
 * 
 * @author lusiyuan
 *
 */
public class VerifyCode extends Entry {
	private String code;

	public VerifyCode(JSONObject json) {
		if (!json.isNull("phone")) {
			setCode(json.optString("phone"));
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
