package cn.com.modernmediausermodel.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmediausermodel.model.Active;

/**
 * 获取活动列表
 * 
 * @author lusiyuan
 *
 */
public class GetActiveListOperate extends UserBaseOperate {
	private String uid;
	private String token;
	private Active active = new Active();

	public GetActiveListOperate(String uid, String token) {
		this.uid = uid;
		this.token = token;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getActiveList(uid, token);
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		try {
			JSONObject error = jsonObject.getJSONObject("error");
			if (error != null && error.optInt("code") == 200) {
				JSONArray array = jsonObject.getJSONArray("active");
				if (array != null && array.length() > 0) {
					active.setId(array.getJSONObject(0).optInt("id"));
					active.setStatus(array.getJSONObject(0).optInt("status"));
					active.setName(array.getJSONObject(0).optString("name"));
					active.setUrl(array.getJSONObject(0).optString("url"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void saveData(String data) {

	}

	public Active getError() {
		return active;
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

}
