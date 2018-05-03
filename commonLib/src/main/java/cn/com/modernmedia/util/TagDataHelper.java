package cn.com.modernmedia.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 新接口保存数据
 * 
 * @author zhuqiao
 * 
 */
public class TagDataHelper {
	private static SharedPreferences mPref;

	private static final String CAT_ARTICLE_UPDATETIME = "cat_article_updatetime_";
	private static final String CAT_INDEX_UPDATETIME = "cat_index_updatetime_";

	private static SharedPreferences getPref(Context context) {
		if (mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return mPref;
	}

	/**
	 * 获取当前栏目文章更新时间
	 * 
	 * @param context
	 * @return
	 */
	public static String getCatArticleUpdateTime(Context context, String tagName) {
		return getPref(context).getString(CAT_ARTICLE_UPDATETIME + tagName, "");
	}

	/**
	 * 保存最近一次获取到的栏目文章更新时间
	 * 
	 * @param context
	 * @param issueId
	 */
	public static void setCatArticleUpdateTime(Context context, String tagName,
			String updateTime) {
		Editor editor = getPref(context).edit();
		editor.putString(CAT_ARTICLE_UPDATETIME + tagName, updateTime);
		editor.commit();
	}

	/**
	 * 获取当前栏目更新时间
	 * 
	 * @param context
	 * @return
	 */
	public static String getCatIndexUpdateTime(Context context, String tagName) {
		return getPref(context).getString(CAT_INDEX_UPDATETIME + tagName, "");
	}

	/**
	 * 保存最近一次获取到的栏目更新时间
	 * 
	 * @param context
	 * @param issueId
	 */
	public static void setCatIndexUpdateTime(Context context, String tagName,
			String updateTime) {
		Editor editor = getPref(context).edit();
		editor.putString(CAT_INDEX_UPDATETIME + tagName, updateTime);
		editor.commit();
	}
}
