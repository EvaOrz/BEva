package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 添加用户积分接口operate
 * 
 * @author JianCong
 * 
 */
public class AddUserCoinNumberOperate extends SlateBaseOperate {
	private ArrayList<NameValuePair> nameValuePairs; // post参数
	private ErrorMsg error;

	protected AddUserCoinNumberOperate(String uid, String token,
			String actionRuleIds) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appid", UserConstData.getInitialAppId()
				+ ""));
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("token", token));
		// 规则id,数组或者,号分割字符串
		params.add(new BasicNameValuePair("actionruleids", actionRuleIds));
		setPostParams(params);
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getAddUserCent();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		// 添加成功返回值 为status=>1,msg=>获取积分;失败返回 status=>0,msg=>消息
		error = new ErrorMsg();
		error.setNo(jsonObject.optInt("status", 0));
		error.setDesc(jsonObject.optString("msg", ""));
	}

	@Override
	protected void fetchLocalDataInBadNet(FetchDataListener mFetchDataListener) {
		SlatePrintHelper.print("net error:" + getUrl());
		mFetchDataListener.fetchData(false, null, false);
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return nameValuePairs;
	}

	protected void setPostParams(ArrayList<NameValuePair> params) {
		this.nameValuePairs = params;
	}

	protected ErrorMsg getError() {
		return error;
	}
}
