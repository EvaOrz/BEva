package cn.com.modernmediausermodel.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import cn.com.modernmediausermodel.model.User;

/**
 * 保存数据
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class UserDataHelper {
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String UID = "uid";
	public static final String TOKEN = "token";
	public static final String NICKNAME = "nickname";
	private static SharedPreferences mPref;

	private static SharedPreferences getPref(Context context) {
		if (mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return mPref;
	}

	/**
	 * 存储登录或者注册后得到的uid、token以及用户名
	 * 
	 * @param context
	 * @param userName
	 * @param uid
	 * @param token
	 */
	public static void saveUserLoginInfo(Context context, User user) {
		Editor editor = getPref(context).edit();
		editor.putString(USER_NAME, user.getUserName());
		editor.putString(PASSWORD, user.getPassword());
		editor.putString(UID, user.getUid());
		editor.putString(TOKEN, user.getToken());
		editor.putString(NICKNAME, user.getNickName());
		editor.commit();
	}

	/**
	 * 取得登录或者注册后得到的uid、token以及用户名
	 * 
	 * @param context
	 * @return
	 */
	public static User getUserLoginInfo(Context context) {
		User user = new User();
		user.setUserName(getPref(context).getString(USER_NAME, ""));
		user.setPassword(mPref.getString(PASSWORD, ""));
		user.setUid(mPref.getString(UID, ""));
		user.setToken(mPref.getString(TOKEN, ""));
		user.setNickName(mPref.getString(NICKNAME, ""));
		if (TextUtils.isEmpty(user.getUid()))
			return null;
		return user;
	}

	/**
	 * 清除登录或者注册后得到的数据
	 * 
	 * @param context
	 */
	public static void clearLoginInfo(Context context) {
		Editor editor = getPref(context).edit();
		editor.putString(USER_NAME, "");
		editor.putString(PASSWORD, "");
		editor.putString(UID, "");
		editor.putString(TOKEN, "");
		editor.putString(NICKNAME, "");
		editor.commit();
	}

	/**
	 * 存储头像下载路径
	 * 
	 * @param url
	 */
	public static void saveAvatarUrl(Context context, String userName,
			String url) {
		Editor editor = getPref(context).edit();
		editor.putString(userName, url);
		editor.commit();
	}

	/**
	 * 取得头像的下载路径
	 * 
	 * @param context
	 * @param userName
	 * @return
	 */
	public static String getAvatarUrl(Context context, String userName) {
		return getPref(context).getString(userName, "");
	}

}
