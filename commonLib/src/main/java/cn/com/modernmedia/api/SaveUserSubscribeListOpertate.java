package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.ErrorMsg;

/**
 * 存用户订阅栏目列表
 * 
 * @author jiancong
 * 
 */
public class SaveUserSubscribeListOpertate extends BaseOperate {
	private String uid; // 用户userId
	private ArrayList<NameValuePair> nameValuePairs; // post参数
	private ErrorMsg error;

	public SaveUserSubscribeListOpertate(String uid, String token,
			List<SubscribeColumn> list) {
		this.uid = uid;
		error = new ErrorMsg();
		nameValuePairs = new ArrayList<NameValuePair>();
		JSONObject postObject = new JSONObject();
		try {
			postObject.put("uid", uid);
			postObject.put("appid", ConstData.getInitialAppId());
			postObject.put("token", token);
			JSONArray columnArray = new JSONArray();
			String log = "";
			for (SubscribeColumn column : list) {
				JSONObject columnObj = new JSONObject();
				columnObj.put("name", column.getName());
				columnObj.put("parent", column.getParent());
				columnObj.put("isdelete", column.getIsDelete());
				columnArray.put(columnObj);
				log = log + "**" + column.getName() + "^"
						+ column.getIsDelete() + "**";
			}
			if (!isNull(columnArray)) {
				postObject.put("col", columnArray);
				Log.e("保存订阅", log);
			}
			nameValuePairs.add(new BasicNameValuePair("data", postObject
					.toString()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getAddUserSubscribeList(uid);
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		if (jsonObject != null) {
			Log.e("SaveUserSubscribeListOpertate result", jsonObject.toString());
			error.setNo(jsonObject.optInt("no"));
			error.setDesc(jsonObject.optString("msg"));
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
