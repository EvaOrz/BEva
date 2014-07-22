package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.unit.SlatePrintHelper;
import cn.com.modernmediausermodel.model.User.Error;

/**
 * 添加卡片operate
 * 
 * @author JianCong
 * 
 */
public class CardFavOperate extends SlateBaseOperate {
	public final static int TYPE_ADD = 0; // 收藏卡片
	public final static int TYPE_DELTE = 1; // 删除卡片
	private Error error;
	private ArrayList<NameValuePair> nameValuePairs; // post参数
	private int type = 0;

	public Error getError() {
		return error;
	}

	protected CardFavOperate(String uid, String cardId, int type) {
		this.error = new Error();
		this.type = type;
		// post 参数设置
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			postObject.put("uid", uid);
			postObject.put("id", cardId);
			if (type == TYPE_ADD) {
				postObject.put("submit", "addFav");
			} else if (type == TYPE_DELTE) {
				postObject.put("submit", "delFav");
			}
			params.add(new BasicNameValuePair("data", postObject.toString()));
			setPostParams(params);
			SlatePrintHelper.print("post values:" + postObject.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		String url = UrlMaker.getAddCardFav();
		if (type == TYPE_DELTE) {
			url = UrlMaker.getCancelCardFav();
		}
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONObject object = jsonObject.optJSONObject("error");
		if (object != null) {
			error.setNo(object.optInt("code", 0));
			error.setDesc(object.optString("msg", ""));
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
