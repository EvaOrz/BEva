package cn.com.modernmediausermodel.util;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import cn.com.modernmedia.util.sina.UserModelAuthListener;
import cn.com.modernmediaslate.SlateApplication;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * qq登录
 * 
 * @author jiancong
 * 
 */
public class QQLoginUtil {

	// public static String APP_ID = "222222";
	public static String APP_ID = SlateApplication.mConfig.getQq_app_id();
	private Tencent mTencent;
	private Context mContext;
	private IUiListener mUiListener;
	private static QQLoginUtil instance;
	private UserModelAuthListener mAuthListener;

	private QQLoginUtil(Context context) {
		this.mContext = context;
		mTencent = Tencent.createInstance(APP_ID, context);
		mUiListener = new BaseUiListener();
	}

	public static QQLoginUtil getInstance(Context context) {
		if (instance == null) {
			instance = new QQLoginUtil(context);
		}
		return instance;
	}

	/**
	 * 自定义登录回调接口
	 * 
	 * @param listener
	 */
	public void setLoginListener(IUiListener listener) {
		mUiListener = listener == null ? new BaseUiListener() : listener;
	}

	/**
	 * 自定义登录回调接口
	 * 
	 * @param listener
	 */
	public void setLoginListener(UserModelAuthListener mAuthListener) {
		this.mAuthListener = mAuthListener;
	}

	/**
	 * 登录
	 */
	public void login() {
		mTencent.login((Activity) mContext, "all", mUiListener);
	}

	/**
	 * 获取open id
	 * 
	 * @return
	 */
	public String getOpenId() {
		return (mTencent != null && mTencent.isSessionValid()) ? mTencent
				.getOpenId() : null;
	}

	/**
	 * 获取token
	 * 
	 * @return
	 */
	public String getToken() {
		return (mTencent != null && mTencent.isSessionValid()) ? mTencent
				.getAccessToken() : null;
	}

	/**
	 * 获得用户信息
	 * 
	 * @param listener
	 *            回调接口
	 */
	public void getUserInfo(IUiListener listener) {
		if (mTencent != null && !mTencent.isSessionValid() || listener == null)
			return;
		UserInfo mInfo = new UserInfo(mContext, mTencent.getQQToken());
		mInfo.getUserInfo(listener);
	}

	/**
	 * 登出
	 */
	public void loginOut() {
		if (mTencent != null && !mTencent.isSessionValid())
			return;
		mTencent.logout(mContext);
	}

	/**
	 * 默认的登录回调接口类
	 * 
	 * @author jiancong
	 * 
	 */
	private class BaseUiListener implements IUiListener {

		@Override
		public void onCancel() {
			if (mAuthListener != null) {
				mAuthListener.onCallBack(false);
			}
		}

		@Override
		public void onComplete(Object arg0) {
			System.out.println("result is:" + arg0.toString());
			if (arg0 != null && arg0 instanceof JSONObject
					&& mAuthListener != null) {
				int code = ((JSONObject) arg0).optInt("ret");
				if (code == 0) {
					mAuthListener.onCallBack(true);
				} else {
					mAuthListener.onCallBack(false);
				}
			}
		}

		@Override
		public void onError(UiError arg0) {
			if (mAuthListener != null) {
				mAuthListener.onCallBack(false);
			}
			if (arg0 != null)
				System.out.println("error is:" + arg0.errorCode + "_"
						+ arg0.errorDetail);
		}
	}
}
