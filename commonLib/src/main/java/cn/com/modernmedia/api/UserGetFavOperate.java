package cn.com.modernmedia.api;

import org.json.JSONObject;

/**
 * 获取收藏列表
 * 
 * @author ZhuQiao
 * 
 */
public class UserGetFavOperate extends UserFavBaseOperate {

	private String url;

	public UserGetFavOperate(String uid) {
		super();
		url = UrlMaker.getFav(uid);
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		super.handler(jsonObject);
	}

	@Override
	protected void saveData(String data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getDefaultFileName() {
		// TODO Auto-generated method stub
		return null;
	}
}
