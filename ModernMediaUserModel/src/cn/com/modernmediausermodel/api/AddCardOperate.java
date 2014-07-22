package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 添加卡片operate
 * 
 * @author JianCong
 * 
 */
public class AddCardOperate extends SlateBaseOperate {
	private Error error;
	private ArrayList<NameValuePair> nameValuePairs; // post参数

	public Error getError() {
		return error;
	}

	protected AddCardOperate(CardItem cardItem) {
		this.error = new Error();
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			addPostParams(postObject, "uid", cardItem.getUid());
			addPostParams(postObject, "appid", UserConstData.getInitialAppId()
					+ "");
			addPostParams(postObject, "time", cardItem.getTime());
			addPostParams(postObject, "contents", cardItem.getContents());
			params.add(new BasicNameValuePair("data", postObject.toString()));
			setPostParams(params);
			SlatePrintHelper.print("post values:" + postObject.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getAddCard();
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONObject object = jsonObject.optJSONObject("error");
		if (object != null) {
			error.setNo(object.optInt("code", 0));
			error.setDesc(object.optString("msg", ""));
			if (UserApplication.infoChangeListener != null
					&& error.getNo() == 0) {
				UserApplication.infoChangeListener.addCard(1);
			}
		}
	}

	@Override
	protected void fetchLocalDataInBadNet(FetchDataListener mFetchDataListener) {
		SlatePrintHelper.print("net error:" + getUrl());
		mFetchDataListener.fetchData(false, null);
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
}
