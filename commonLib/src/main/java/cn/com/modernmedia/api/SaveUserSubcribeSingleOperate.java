package cn.com.modernmedia.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.ErrorMsg;

/**
 * 存单个订阅
 * 
 * @author lusiyuan
 *
 */
public class SaveUserSubcribeSingleOperate extends BaseOperate {
	private String uid; // 用户userId
	private ArrayList<NameValuePair> nameValuePairs; // post参数
	private ErrorMsg error;

	public SaveUserSubcribeSingleOperate(String uid, String token,
			SubscribeColumn data) {
		this.uid = uid;
		error = new ErrorMsg();
		nameValuePairs = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			postObject.put("uid", uid);
			postObject.put("appid", ConstData.getInitialAppId());
			postObject.put("token", token);
			JSONArray columnArray = new JSONArray();
			JSONObject columnObj = new JSONObject();
			columnObj.put("name", data.getName());
			columnObj.put("parent", data.getParent());
			columnObj.put("isdelete", data.getIsDelete());
			columnArray.put(columnObj);
			if (!isNull(columnArray)) {
				postObject.put("col", columnArray);
			}
			nameValuePairs.add(new BasicNameValuePair("data", postObject
					.toString()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getAddUserSubscribeSingle(uid);
	}

	/**
	 * {"uid":"534363","appid":"1","col":[{"parent":"","name":"cat_1121"}],
	 * "error":{"msg":"success","no":0},"token":
	 * "63](]gtc,{wclxkBss,amo](]4:](]716141](]10"}
	 */
	@Override
	protected void handler(JSONObject jsonObject) {
		JSONObject j = jsonObject.optJSONObject("error");
		if (j != null) {
			error.setNo(j.optInt("no"));
			error.setDesc(j.optString("msg"));
		}

	}

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return nameValuePairs;
	}

	public ErrorMsg getError() {
		return error;
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}
}
