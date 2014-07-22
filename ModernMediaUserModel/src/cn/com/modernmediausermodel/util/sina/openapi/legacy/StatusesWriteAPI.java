package cn.com.modernmediausermodel.util.sina.openapi.legacy;

import cn.com.modernmediausermodel.util.sina.net.RequestListener;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;

/**
 * 该类封装了微博写入接口，详情请参考<a href= "http://open.weibo.com/wiki/2/statuses/update"</a>
 * 
 * @author JianCong
 */
public class StatusesWriteAPI extends WeiboAPI {
	public StatusesWriteAPI(Oauth2AccessToken accessToken) {
		super(accessToken);
	}

	private static final String SERVER_URL_PRIX = API_SERVER + "/statuses";

	/**
	 * 发布一条新微博
	 * 
	 * @param token
	 *            采用OAuth授权方式为必填参数，其他授权方式不需要此参数，OAuth授权后获得。
	 * @param source
	 *            采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。
	 * @param content
	 *            要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。
	 * @param listener
	 */
	public void writeText(String token, String source, String content,
			RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if (mAccessToken != null) {
			params.add("access_token", token);
		} else {
			params.add("source", source);
		}
		params.add("status", content);
		request(SERVER_URL_PRIX + "/update.json", params, HTTPMETHOD_POST,
				listener);
	}

}
