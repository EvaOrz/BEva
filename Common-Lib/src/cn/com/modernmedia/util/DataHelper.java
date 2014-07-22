package cn.com.modernmedia.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import cn.com.modernmedia.model.Cat.CatItem;

/**
 * 保存数据
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class DataHelper {
	private static SharedPreferences mPref;

	private static final String FIRST_USE = "firststartapp_";
	private static final String ISSUE_ID = "id";
	private static final String FONT_SIZE = "Font_Size";
	private static final String COLUMN_UPDATE_TIME = "columnUpdateTime_";
	private static final String ARTICLE_UPDATE_TIME = "articleUpdateTime_";
	private static final String DOWN = "down";
	private static final String UPDATE = "update";
	private static final String LINE_HEIGHT = "line_height";// webview行间距
	private static final String INDEX_UPDATE_TIME = "index_update_time_";// 首页、栏目首页更新时间
	private static final String PUSH = "push";
	private static final String DB_ADD_COLUMN = "db_add_column";

	public static Map<Integer, String> columnTitleMap = new HashMap<Integer, String>();// catid对于的cat名称
	public static Map<Integer, Integer> columnColorMap = new HashMap<Integer, Integer>();// catid对应的cat颜色
	public static Map<Integer, String> columnRowMap = new HashMap<Integer, String>();// catid对应的箭头图片
	public static Map<Integer, List<CatItem>> childMap = new HashMap<Integer, List<CatItem>>();// key:parent_cat_id,value:子栏目列表
	public static Map<Integer, Integer> soloCatMap = new HashMap<Integer, Integer>();// 独立栏目
																						// key:catid,value:跳转至的catid
	public static Map<String, String> fromOffset = new HashMap<String, String>();// 独立栏目from_offset
	public static Map<String, String> toOffset = new HashMap<String, String>();// 独立栏目from_offset
	/**
	 * 当前焦点图的个数（full页的，用来做广告）
	 */
	public static int solo_head_count = 0;
	/**
	 * 当前列表图的个数（full页的，用来做广告）
	 */
	public static int solo_list_count = 0;

	public static void clear() {
		columnTitleMap.clear();
		columnColorMap.clear();
		columnRowMap.clear();
		childMap.clear();
		fromOffset.clear();
		toOffset.clear();
		soloCatMap.clear();
		solo_head_count = 0;
		solo_list_count = 0;
	}

	private static SharedPreferences getPref(Context context) {
		if (mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return mPref;
	}

	/**
	 * 是否是第一次进应用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getFirstStartApp(Context context) {
		return getPref(context).getBoolean(FIRST_USE, true);
	}

	/**
	 * 设置不是第一次进应用
	 * 
	 * @param context
	 */
	public static void setFirstStartApp(Context context) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(FIRST_USE, false);
		editor.commit();
	}

	/**
	 * 获取保存的issue_id
	 * 
	 * @param context
	 * @return 如果没保存过或者被用户手动清除过，返回-1
	 */
	public static int getIssueId(Context context) {
		return getPref(context).getInt(ISSUE_ID, -1);
	}

	/**
	 * 保存最近一次获取到的issueid
	 * 
	 * @param context
	 * @param issueId
	 */
	public static void setIssueId(Context context, int issueId) {
		Editor editor = getPref(context).edit();
		editor.putInt(ISSUE_ID, issueId);
		editor.commit();
	}

	/**
	 * 获取保存过的字体大小
	 * 
	 * @param context
	 * @return
	 */
	public static int getFontSize(Context context) {
		return getPref(context).getInt(FONT_SIZE,
				ConstData.getInitialAppId() == 20 ? 3 : 1);
	}

	/**
	 * 保存字体大小
	 * 
	 * @param context
	 * @param fontsize
	 */
	public static void setFontSize(Context context, int fontsize) {
		Editor editor = getPref(context).edit();
		editor.putInt(FONT_SIZE, fontsize);
		editor.commit();
	}

	/**
	 * 获取最近一次更新过的ColumnUpdateTime
	 * 
	 * @param context
	 * @return
	 */
	public static long getColumnUpdateTime(Context context, int issueId) {
		return getPref(context).getLong(COLUMN_UPDATE_TIME + issueId, -1);
	}

	/**
	 * 保存最近一次获取的ColumnUpdateTime
	 * 
	 * @param context
	 * @param time
	 */
	public static void setColumnUpdateTime(Context context, long time,
			int issueId) {
		Editor editor = getPref(context).edit();
		editor.putLong(COLUMN_UPDATE_TIME + issueId, time);
		editor.commit();
	}

	/**
	 * 获取最近一次更新的ARTICLE_UPDATE_TIME
	 * 
	 * @param context
	 * @return
	 */
	public static long getArticleUpdateTime(Context context, int issueId) {
		return getPref(context).getLong(ARTICLE_UPDATE_TIME + issueId, -1);
	}

	/**
	 * 保存最近一次获取的ARTICLE_UPDATE_TIME
	 * 
	 * @param context
	 * @param time
	 */
	public static void setArticleUpdateTime(Context context, long time,
			int issueId) {
		Editor editor = getPref(context).edit();
		editor.putLong(ARTICLE_UPDATE_TIME + issueId, time);
		editor.commit();
	}

	/**
	 * 统计渠道
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getDown(Context context) {
		return getPref(context).getBoolean(DOWN, false);
	}

	/**
	 * 设置统计成功
	 * 
	 * @param context
	 */
	public static void setDown(Context context) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(DOWN, true);
		editor.commit();
	}

	/**
	 * 获取上次点击取消升级的时间
	 * 
	 * @return
	 */
	public static long getUpdate(Context context) {
		return getPref(context).getLong(UPDATE, 0);
	}

	/**
	 * 设置升级时点击取消的时间
	 * 
	 * @param context
	 * @param time
	 */
	public static void setUpdte(Context context, long time) {
		Editor editor = getPref(context).edit();
		editor.putLong(UPDATE, time);
		editor.commit();
	}

	public static int getAppId(Context context) {
		return getPref(context).getInt("appid", -1);
	}

	public static void setAppId(Context context, int id) {
		Editor editor = getPref(context).edit();
		editor.putInt("appid", id);
		editor.commit();
	}

	public static int getLineHeight(Context context) {
		return getPref(context).getInt(LINE_HEIGHT, 3);
	}

	public static void setLineHeight(Context context, int lineHeight) {
		Editor editor = getPref(context).edit();
		editor.putInt(LINE_HEIGHT, lineHeight);
		editor.commit();
	}

	/**
	 * 获取最近一次更新issue的时间
	 * 
	 * @param context
	 * @return
	 */
	public static String getIndexUpdateTime(Context context, int catId) {
		return getPref(context).getString(INDEX_UPDATE_TIME + catId, "");
	}

	/**
	 * 保存最近一次更新issue的时间
	 * 
	 * @param context
	 * @param time
	 * @param catId
	 */
	public static void setIndexUpdateTime(Context context, String time,
			int catId) {
		Editor editor = getPref(context).edit();
		editor.putString(INDEX_UPDATE_TIME + catId, time);
		editor.commit();
	}

	/**
	 * 清除当前catid对应的更新时间(当发现columnupdatetime发送变化时执行)
	 * 
	 * @param context
	 * @param catId
	 */
	public static void removeIndexUpdateTime(Context context, int catId) {
		try {
			Editor editor = getPref(context).edit();
			editor.remove(INDEX_UPDATE_TIME + catId);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取parse push开关状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getPushStatus(Context context) {
		return getPref(context).getBoolean(PUSH, true);
	}

	/**
	 * 设置parse push开关状态
	 * 
	 * @param context
	 * @param status
	 *            true:开启；otherwise:关闭
	 */
	public static void setPushStatus(Context context, boolean status) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(PUSH, status);
		editor.commit();
	}

	/**
	 * 是否是第一次进应用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getAddColumn(Context context) {
		return getPref(context).getBoolean(DB_ADD_COLUMN, false);
	}

	/**
	 * 设置不是第一次进应用
	 * 
	 * @param context
	 */
	public static void setAddColumn(Context context) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(DB_ADD_COLUMN, true);
		editor.commit();
	}

}
