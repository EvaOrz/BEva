package cn.com.modernmedia.util.sina.openapi.legacy;

import android.text.TextUtils;
import cn.com.modernmedia.util.sina.net.RequestListener;

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

	/**
	 * 发送图片和文字到微博
	 * 
	 * @param token
	 * @param source
	 * @param content
	 *            要发布的微博文本内容，内容不超过140个汉字
	 * @param file
	 *            要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M
	 * @param listener
	 */
	public void writeTextAndImage(String token, String source, String content,
			String file, String lat, String lon, RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		if (mAccessToken != null) {
			params.add("access_token", token);
		} else {
			params.add("source", source);
		}
		if (content != null)
			params.add("status", content);
		if (!TextUtils.isEmpty(file))
			params.add("pic", file);
		if (!TextUtils.isEmpty(lon))
			params.add("long", lon);
		if (!TextUtils.isEmpty(lat))
			params.add("lat", lat);

		request(SERVER_URL_PRIX + "/upload.json", params, HTTPMETHOD_POST,
				listener);
	}
}
