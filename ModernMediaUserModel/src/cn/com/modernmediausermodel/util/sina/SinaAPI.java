package cn.com.modernmediausermodel.util.sina;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.sina.net.RequestListener;
import cn.com.modernmediausermodel.util.sina.openapi.legacy.StatusesWriteAPI;
import cn.com.modernmediausermodel.util.sina.openapi.legacy.UsersAPI;
import cn.com.modernmediausermodel.util.sina.openapi.legacy.WeiboAPI;

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

	/**
	 * 获取新浪微博用户相关信息
	 * 
	 * @param sinaRequestListener
	 */
	public void fetchUserInfo(final SinaRequestListener sinaRequestListener) {
		if (mAccessToken == null)
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
				JSONObject object;
				try {
					object = new JSONObject(response);
					User user = new User();
					user.setSinaId(object.optString("idstr", "")); // 新浪ID
					user.setNickName(object.optString("screen_name")); // 昵称
					user.setUserName(object.optString("name"));
					user.setAvatar(object.optString("profile_image_url"));// 用户头像地址（中图），50×50像素
					user.setToken(mAccessToken.getToken());
					sinaRequestListener.onSuccess(user);
				} catch (JSONException e) {
					sinaRequestListener.onFailed("response is not a json!");
				}
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
		if (mAccessToken == null)
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
						JSONObject object;
						try {
							object = new JSONObject(response);
							String createTime = object.optString("created_at",
									"");
							String id = object.optString("idstr", "");
							String error = object.optString("error", "");
							if (!TextUtils.isEmpty(createTime)
									&& !TextUtils.isEmpty(id)
									&& TextUtils.isEmpty(error)) {
								sinaRequestListener.onSuccess(null);
							} else {
								sinaRequestListener.onFailed(error);
							}
						} catch (JSONException e) {
							sinaRequestListener
									.onFailed("response is not a json!");
						}
					}
				});
	}

}
