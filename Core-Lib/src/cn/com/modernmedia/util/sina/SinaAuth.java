package cn.com.modernmedia.util.sina;

import android.content.Context;
import android.os.Bundle;
import cn.com.modernmediaslate.unit.DateFormatTool;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * 新浪微博认证类
 * 
 * @author jiancong
 * 
 */
public class SinaAuth {

	private WeiboAuth mWeiboAuth;
	private Context mContext;
	private Oauth2AccessToken mAccessToken;
	private UserModelAuthListener mAuthListener;

	public void setAuthListener(UserModelAuthListener mAuthListener) {
		this.mAuthListener = mAuthListener;
	}

	public SinaAuth(Context context) {
		this.mContext = context;
		mWeiboAuth = new WeiboAuth(context, SinaConstants.APP_KEY,
				SinaConstants.REDIRECT_URL, SinaConstants.SCOPE);
	}

	/**
	 * 弹出窗口进行微博认证
	 * 
	 */
	public void oAuth() {
		mWeiboAuth.anthorize(new AuthListener());
	}

	/**
	 * 确认当前应用是否已经授权，若授权则检查token是否有效
	 * 
	 * @return
	 */
	public boolean checkIsOAuthed() {
		mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
		return mAccessToken.isSessionValid();
	}

	/**
	 * 清空token信息
	 */
	public void clear() {
		AccessTokenKeeper.clear(mContext);
	}

	/**
	 * 微博认证授权回调类。 1.SSO 授权时,需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} * 后,该回调才会被执行。 2. 非 SSO
	 * 授权时,当授权结束后,该回调就会被执行。 当授权成功后,请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				String date = DateFormatTool.format(
						mAccessToken.getExpiresTime(), "yyyy/MM/dd HH:mm:ss");
				String text = String.format("Token：%1$s \n有效期：%2$s",
						mAccessToken.getToken(), date);
				System.out.println(text);
				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
				if (mAuthListener != null) {
					mAuthListener.onCallBack(true);
				}
			} else {
				// 当您注册的应用程序签名不正确时,就会收到 Code,请确保签名正确
				String code = values.getString("code");
				System.out.println("error code:" + code);
				if (mAuthListener != null) {
					mAuthListener.onCallBack(false);
				}
			}
		}

		@Override
		public void onCancel() {
			if (mAuthListener != null) {
				mAuthListener.onCallBack(false);
			}
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			System.out.println("auth exception:" + arg0.getMessage());
			if (!"未发现网址".equals(arg0.getMessage()) && mAuthListener != null) {
				mAuthListener.onCallBack(false);
			}
		}
	}

}
