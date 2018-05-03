package cn.com.modernmedia.util.sina;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.util.sina.net.RequestListener;
import cn.com.modernmedia.util.sina.openapi.legacy.FriendShipAPI;
import cn.com.modernmedia.util.sina.openapi.legacy.StatusesWriteAPI;
import cn.com.modernmedia.util.sina.openapi.legacy.UsersAPI;
import cn.com.modernmedia.util.sina.openapi.legacy.WeiboAPI;
import cn.com.modernmediaslate.unit.ParseUtil;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * 该类用于调用新浪OPEN API的相关类和方法,使用时，请确保已经授权
 * 
 * @author jiancong
 * 
 */
public class SinaAPI extends WeiboAPI {
	private static SinaAPI instance;

	public static SinaAPI getInstance(Context context) {
		if (instance == null) {
			Oauth2AccessToken accessToken = AccessTokenKeeper
					.readAccessToken(context);
			instance = new SinaAPI(context, accessToken);
		}
		return instance;
	}

	private SinaAPI(Context context, Oauth2AccessToken accessToken) {
		super(accessToken);
	}

	/**
	 * 获取授权过的SINAID
	 * 
	 * @return
	 */
	public String getSinaId() {
		return mAccessToken == null ? null : mAccessToken.getUid();
	}

	public String getToken() {
		return mAccessToken == null ? "" : mAccessToken.getToken();
	}

	/**
	 * 获取新浪微博用户相关信息
	 * 
	 * @param sinaRequestListener
	 */
	public void fetchUserInfo(final SinaRequestListener sinaRequestListener) {
		if (mAccessToken == null || sinaRequestListener == null)
			return;
		long uid = ParseUtil.stol(mAccessToken.getUid());
		new UsersAPI(mAccessToken).show(uid, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				sinaRequestListener.onFailed(e.getMessage());
			}

			@Override
			public void onError(WeiboException e) {
				sinaRequestListener.onFailed(e.getMessage());
			}

			@Override
			public void onComplete4binary(ByteArrayOutputStream responseOS) {
			}

			@Override
			public void onComplete(String response) {
				sinaRequestListener.onSuccess(response);
			}
		});
	}

	/**
	 * 发送一条微博（连续两次发布的微博不可以重复， 要发布的微博文本内容，必须做URLencode，内容不超过140个汉字）
	 * 
	 * @param content
	 * @param sinaRequestListener
	 */
	public void sendText(String content,
			final SinaRequestListener sinaRequestListener) {
		if (mAccessToken == null || sinaRequestListener == null)
			return;
		if (TextUtils.isEmpty(content))
			return;
		if (content.length() > 140) {
			content = content.substring(0, 140);
		}
		String token = mAccessToken.getToken();
		new StatusesWriteAPI(mAccessToken).writeText(token, null, content,
				new RequestListener() {

					@Override
					public void onIOException(IOException e) {
						sinaRequestListener.onFailed(e.getMessage());
					}

					@Override
					public void onError(WeiboException e) {
						sinaRequestListener.onFailed(e.getMessage());
					}

					@Override
					public void onComplete4binary(
							ByteArrayOutputStream responseOS) {
					}

					@Override
					public void onComplete(String response) {
						defAfterComplete(response, sinaRequestListener);
					}
				});
	}

	public void sendTextAndImage(String content, String file,
			final SinaRequestListener sinaRequestListener) {
		if (TextUtils.isEmpty(file))
			sendText(content, sinaRequestListener);
		else
			sendTextAndImage(content, file, "", "", sinaRequestListener);
	}

	/**
	 * 发送图片和文字到微博
	 * 
	 * @param token
	 * @param source
	 * @param content
	 *            要发布的微博文本内容，内容不超过140个汉字
	 * @param file
	 *            要上传的图片路径，仅支持JPEG、GIF、PNG格式，图片大小小于5M
	 * @param listener
	 */
	public void sendTextAndImage(String content, String file, String lat,
			String lon, final SinaRequestListener sinaRequestListener) {
		if (mAccessToken == null || sinaRequestListener == null)
			return;
		if (!TextUtils.isEmpty(content) && content.length() > 140) {
			content = content.substring(0, 140);
		}
		if (!TextUtils.isEmpty(file)) {
			File image = new File(file);
			if (!image.exists() || image.length() > 5 * 1024 * 1024) {
				sinaRequestListener
						.onFailed("image is not exist or size is more than 5M");
				return;
			}
		}
		String token = mAccessToken.getToken();
		new StatusesWriteAPI(mAccessToken).writeTextAndImage(token, "",
				content, file, lat, lon, new RequestListener() {

					@Override
					public void onIOException(IOException e) {
						sinaRequestListener.onFailed(e.getMessage());
					}

					@Override
					public void onError(WeiboException e) {
						sinaRequestListener.onFailed(e.getMessage());
					}

					@Override
					public void onComplete4binary(
							ByteArrayOutputStream responseOS) {
					}

					@Override
					public void onComplete(String response) {
						defAfterComplete(response, sinaRequestListener);
					}
				});

	}

	/**
	 * 关注用户
	 * 
	 * @param uid
	 * @param sinaRequestListener
	 */
	public void followUser(int uid,
			final SinaRequestListener sinaRequestListener) {
		if (mAccessToken == null || sinaRequestListener == null)
			return;
		if (uid == 0)
			return;
		String token = mAccessToken.getToken();
		new FriendShipAPI(mAccessToken).create(token, null, uid,
				new RequestListener() {

					@Override
					public void onIOException(IOException e) {
						sinaRequestListener.onFailed(e.getMessage());
					}

					@Override
					public void onError(WeiboException e) {
						sinaRequestListener.onFailed(e.getMessage());
					}

					@Override
					public void onComplete4binary(
							ByteArrayOutputStream responseOS) {
					}

					@Override
					public void onComplete(String response) {
						defAfterComplete(response, sinaRequestListener);
					}
				});
	}

	/**
	 * 默认的请求成功后的处理
	 * 
	 * @param response
	 * @param sinaRequestListener
	 */
	private void defAfterComplete(String response,
			SinaRequestListener sinaRequestListener) {
		if (TextUtils.isEmpty(response))
			return;
		JSONObject object;
		try {
			object = new JSONObject(response);
			String createTime = object.optString("created_at", "");
			String id = object.optString("idstr", "");
			String error = object.optString("error", "");
			if (!TextUtils.isEmpty(createTime) && !TextUtils.isEmpty(id)
					&& TextUtils.isEmpty(error)) {
				sinaRequestListener.onSuccess(null);
			} else {
				sinaRequestListener.onFailed(error);
			}
		} catch (JSONException e) {
			sinaRequestListener.onFailed("response is not a json!");
		}
	}

}
