package cn.com.modernmediaslate.unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.User;

/**
 * 用户数据存储在SharedPreferences
 * 
 * @author lusiyuan
 * 
 */
public class SlateDataHelper {
	public static final String USER_NAME = "username";
	public static final String PHONE = "phone";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String UID = "uid";
	public static final String SINA_ID = "sina_id";
	public static final String QQ_ID = "qq_id";
	public static final String WEIXIN_ID = "weixin_id";
	public static final String TOKEN = "token";
	public static final String NICKNAME = "nickname";
	public static final String ISSUELEVEL = "issuelevel";
	public static final String END_TIME = "end_time";
	public static final String DESC = "desc";

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
		editor.putString(PHONE, user.getPhone());
		editor.putString(EMAIL, user.getEmail());
		editor.putString(PASSWORD, user.getPassword());
		editor.putString(UID, user.getUid());
		editor.putString(SINA_ID, user.getSinaId());
		editor.putString(QQ_ID, user.getQqId());
		editor.putString(WEIXIN_ID, user.getWeixinId());
		editor.putString(TOKEN, user.getToken());
		editor.putString(NICKNAME, user.getNickName());
		editor.putString(DESC, user.getDesc());
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
		user.setPhone(getPref(context).getString(PHONE, ""));
		user.setEmail(getPref(context).getString(EMAIL, ""));
		user.setPassword(getPref(context).getString(PASSWORD, ""));
		user.setUid(getPref(context).getString(UID, ""));
		user.setSinaId(getPref(context).getString(SINA_ID, ""));
		user.setQqId(getPref(context).getString(QQ_ID, ""));
		user.setWeixinId(getPref(context).getString(WEIXIN_ID, ""));
		user.setToken(getPref(context).getString(TOKEN, ""));
		user.setNickName(getPref(context).getString(NICKNAME, ""));
		user.setDesc(getPref(context).getString(DESC, ""));
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
		editor.putString(PHONE, "");
		editor.putString(EMAIL, "");
		editor.putString(UID, "");
		editor.putString(SINA_ID, "");
		editor.putString(QQ_ID, "");
		editor.putString(WEIXIN_ID, "");
		editor.putString(TOKEN, "");
		editor.putString(NICKNAME, "");
		editor.putString(ISSUELEVEL, "0");
		editor.putString(DESC, "");
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

	/**
	 * 存储用户付费level
	 * 
	 * @param context
	 * @param issuelevel
	 * @return
	 */
	public static void setIssueLevel(Context context, String issuelevel) {
		Editor editor = getPref(context).edit();
		editor.putString(ISSUELEVEL, issuelevel);
		editor.commit();
	}

	/**
	 * 获取用户付费level
	 */
	public static String getIssueLevel(Context context) {
		return getPref(context).getString(ISSUELEVEL, "");
	}

	/**
	 * 存储用户付费到期时间
	 * 
	 * @param context
	 * @param time
	 */
	public static void setEndTime(Context context, long time) {
		Editor editor = getPref(context).edit();
		editor.putLong(END_TIME, time);
		editor.commit();
	}

	/**
	 * 获取用户付费到期时间
	 * 
	 * @param context
	 * @return
	 */
	public static long getEndTime(Context context) {
		return getPref(context).getLong(END_TIME, 0);
	}

	/**
	 * 存储用户签名
	 * 
	 * @param context
	 * @param desc
	 */
	public static void setDesc(Context context, String desc) {
		Editor editor = getPref(context).edit();
		editor.putString(DESC, desc);
		editor.commit();
	}

	/**
	 * 获取用户签名
	 * 
	 * @param context
	 * @return
	 */
	public static String getDesc(Context context) {
		return getPref(context).getString(DESC, "");
	}

	/**
	 * 存储用户昵称
	 * 
	 * @param context
	 * @param nickname
	 */
	public static void setNickname(Context context, String nickname) {
		Editor editor = getPref(context).edit();
		editor.putString(NICKNAME, nickname);
		editor.commit();
	}

	/**
	 * 获取用户昵称
	 * 
	 * @param context
	 * @return
	 */
	public static String getNickname(Context context) {
		return getPref(context).getString(NICKNAME, "");
	}

	/**
	 * 存储提交失败的订单
	 * 
	 * @param context
	 * @param order
	 *            -- DES算法加密后的订单信息
	 * @return
	 */
	public static void setOrder(Context context, String aliOrWeixin,
			String order) {
		Editor editor = getPref(context).edit();
		editor.putString(aliOrWeixin, order);
		editor.commit();
	}

	/**
	 * 获取未提交订单
	 * 
	 * @param context
	 * @param aliOrWeixin
	 *            参数是“alipay”或者“weixin”
	 * @return
	 */
	public static String getOrder(Context context, String aliOrWeixin) {
		return getPref(context).getString(aliOrWeixin, null);
	}

}
