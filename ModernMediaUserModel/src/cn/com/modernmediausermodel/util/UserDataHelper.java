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
	public static final String SINA_ID = "sina_id";
	public static final String TOKEN = "token";
	public static final String NICKNAME = "nickname";
	public static final String IS_FIRST_LOGIN = "login_"; // 标识用户是否第一次登录的前缀，后接用户ID
	public static final String SINA_LOGINED = "sina_logined_"; // 存储新浪用户ID对应的用户名
	public static final String LAST_ID = "last_id"; // 最后一个通知消息ID
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
	private static String getAvatarUrl(Context context, String userName) {
		return getPref(context).getString(userName, "");
	}

	/**
	 * 存储上一次取消或者添加失败的卡片的内容
	 * 
	 * @param context
	 * @param userName
	 */
	public static void saveIsFirstLogin(Context context, String userName,
			boolean isFirstLogin) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(IS_FIRST_LOGIN + userName, isFirstLogin);
		editor.commit();
	}

	/**
	 * 
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getIsFirstLogin(Context context, String userName) {
		return getPref(context).getBoolean(IS_FIRST_LOGIN + userName, true);
	}

	/**
	 * 存储本次通知最近的消息的ID
	 * 
	 * @param context
	 * @param userName
	 */
	public static void saveMessageLastId(Context context, int lastId) {
		Editor editor = getPref(context).edit();
		editor.putInt(LAST_ID, lastId);
		editor.commit();
	}

	/**
	 * 获取LASTID
	 * 
	 * @param context
	 * @return
	 */
	public static int getMessageLastId(Context context) {
		return getPref(context).getInt(LAST_ID, 0);
	}

	/**
	 * 存储新浪ID及对应的用户名
	 * 
	 * @param context
	 * @param userName
	 */
	public static void saveSinaLoginedName(Context context, String sinaId,
			String userName) {
		Editor editor = getPref(context).edit();
		editor.putString(SINA_LOGINED + sinaId, userName);
		editor.commit();
	}

	/**
	 * 获取新浪ID及对应的用户名
	 * 
	 * @param context
	 * @return
	 */
	public static String getSinaLoginedName(Context context, String sinaId) {
		return getPref(context).getString(SINA_LOGINED + sinaId, "");
	}

}
