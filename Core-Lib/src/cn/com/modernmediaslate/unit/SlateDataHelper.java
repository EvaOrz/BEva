package cn.com.modernmediaslate.unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.User;

public class SlateDataHelper {
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String UID = "uid";
	public static final String SINA_ID = "sina_id";
	public static final String QQ_ID = "qq_id";
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
		editor.putString(SINA_ID, user.getSinaId());
		editor.putString(QQ_ID, user.getQqId());
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
		user.setSinaId(mPref.getString(SINA_ID, ""));
		user.setQqId(mPref.getString(QQ_ID, ""));
		user.setToken(mPref.getString(TOKEN, ""));
		user.setNickName(mPref.getString(NICKNAME, ""));
		user.setAvatar(getAvatarUrl(context, user.getUserName()));
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
		editor.putString(SINA_ID, "");
		editor.putString(QQ_ID, "");
		editor.putString(TOKEN, "");
		editor.putString(NICKNAME, "");
		editor.commit();
	}

	/**
	 * 存储新的用户名
	 * 
	 * @param context
	 * @param userName
	 */
	public static void saveUserName(Context context, String userName) {
		Editor editor = getPref(context).edit();
		editor.putString(USER_NAME, userName);
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
	private static String getAvatarUrl(Context context, String userName) {
		return getPref(context).getString(userName, "");
	}

	/**
	 * 获取uid
	 * 
	 * @param context
	 * @return
	 */
	public static String getUid(Context context) {
		String uid = getPref(context).getString(UID, "");
		return TextUtils.isEmpty(uid) ? SlateApplication.UN_UPLOAD_UID : uid;
	}

	/**
	 * 获取token
	 * 
	 * @param context
	 * @return
	 */
	public static String getToken(Context context) {
		return getPref(context).getString(TOKEN, "");
	}
}
