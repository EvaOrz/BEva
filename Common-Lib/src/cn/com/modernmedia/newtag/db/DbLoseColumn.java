package cn.com.modernmedia.newtag.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 数据库丢失字段缓存
 * 
 * @author user
 *
 */
public class DbLoseColumn {
	private static SharedPreferences mPref;

	private static final String UPDATETIME = "db_updatetime";
	private static final String ADV_UPDATETIME = "db_adv_updatetime";

	private static SharedPreferences getPref(Context context) {
		if (mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return mPref;
	}

	public static String getUpdatetime(Context context) {
		return getPref(context).getString(UPDATETIME, "");
	}

	public static void setUpdatetime(Context context, String updatetime) {
		Editor editor = getPref(context).edit();
		editor.putString(UPDATETIME, updatetime);
		editor.commit();
	}

	public static String getAdvUpdatetime(Context context) {
		return getPref(context).getString(ADV_UPDATETIME, "");
	}

	public static void setAdvUpdatetime(Context context, String updatetime) {
		Editor editor = getPref(context).edit();
		editor.putString(ADV_UPDATETIME, updatetime);
		editor.commit();
	}
}
