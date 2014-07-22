package cn.com.modernmediausermodel.api;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * 提交收藏更新
 * 
 * @author ZhuQiao
 * 
 */
public class UserUpdateFavOperate extends UserFavBaseOperate {

	public UserUpdateFavOperate(String uid, String appid, String jsonStr) {
		super();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("appid", appid));
		params.add(new BasicNameValuePair("data", jsonStr));
		setPostParams(params);
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getUpdateFav();
	}

}
