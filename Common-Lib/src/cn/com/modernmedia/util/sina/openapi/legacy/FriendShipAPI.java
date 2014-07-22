package cn.com.modernmedia.util.sina.openapi.legacy;

import cn.com.modernmedia.util.sina.net.RequestListener;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;

/**
 * 关系接口
 * 
 * @author jiancong
 * 
 */
public class FriendShipAPI extends WeiboAPI {

	private static final String SERVER_URL_PRIX = API_SERVER + "/friendships";

	public FriendShipAPI(Oauth2AccessToken oauth2AccessToken) {
		super(oauth2AccessToken);
	}

	/**
	 * 关注用户
	 * 
	 * @param token
	 * @param source
	 * @param uid
	 * @param listener
	 */
	public void create(String token, String source, int uid,
			RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if (mAccessToken != null) {
			params.add("access_token", token);
		} else {
			params.add("source", source);
		}
		params.add("uid", uid);
		request(SERVER_URL_PRIX + "/create.json", params, HTTPMETHOD_POST,
				listener);
	}

}
