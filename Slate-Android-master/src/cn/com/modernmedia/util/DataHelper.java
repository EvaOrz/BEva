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
	private static final String COLUMN_UPDATE_TIME = "columnUpdateTime";
	private static final String ARTICLE_UPDATE_TIME = "articleUpdateTime";
	private static final String DOWN = "down";
	private static final String UPDATE = "update";
	private static final String START_TIME = "start_time";
	private static final String END_TIME = "end_time";
	private static final String ADV_PIC = "adv_pic";
	private static final String LINE_HEIGHT = "line_height";// webview行间距

	public static Map<Integer, String> columnTitleMap = new HashMap<Integer, String>();// catid对于的cat名称
	public static Map<Integer, Integer> columnColorMap = new HashMap<Integer, Integer>();// catid对应的cat颜色
	public static Map<Integer, String> columnRowMap = new HashMap<Integer, String>();// catid对应的箭头图片
	public static Map<Integer, List<CatItem>> childMap = new HashMap<Integer, List<CatItem>>();// key:parent_cat_id,value:子栏目列表

	public static void clear() {
		columnTitleMap.clear();
		columnColorMap.clear();
		columnRowMap.clear();
		childMap.clear();
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
	public static long getColumnUpdateTime(Context context) {
		return getPref(context).getLong(COLUMN_UPDATE_TIME, -1);
	}

	/**
	 * 保存最近一次获取的ColumnUpdateTime
	 * 
	 * @param context
	 * @param time
	 */
	public static void setColumnUpdateTime(Context context, long time) {
		Editor editor = getPref(context).edit();
		editor.putLong(COLUMN_UPDATE_TIME, time);
		editor.commit();
	}

	/**
	 * 获取最近一次更新的ARTICLE_UPDATE_TIME
	 * 
	 * @param context
	 * @return
	 */
	public static long getArticleUpdateTime(Context context) {
		return getPref(context).getLong(ARTICLE_UPDATE_TIME, -1);
	}

	/**
	 * 保存最近一次获取的ARTICLE_UPDATE_TIME
	 * 
	 * @param context
	 * @param time
	 */
	public static void setArticleUpdateTime(Context context, long time) {
		Editor editor = getPref(context).edit();
		editor.putLong(ARTICLE_UPDATE_TIME, time);
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

	/**
	 * 进版首页广告图开始时间
	 * 
	 * @return
	 */
	public static String getStartTime(Context context) {
		return getPref(context).getString(START_TIME, "");
	}

	/**
	 * 设置进版首页广告图开始时间
	 * 
	 * @param context
	 * @param time
	 */
	public static void setStartTime(Context context, String time) {
		Editor editor = getPref(context).edit();
		editor.putString(START_TIME, time);
		editor.commit();
	}

	/**
	 * 进版首页广告图结束时间
	 * 
	 * @return
	 */
	public static String getEndTime(Context context) {
		return getPref(context).getString(END_TIME, "");
	}

	/**
	 * 设置进版首页广告图结束时间
	 * 
	 * @param context
	 * @param time
	 */
	public static void setEndTime(Context context, String time) {
		Editor editor = getPref(context).edit();
		editor.putString(END_TIME, time);
		editor.commit();
	}

	/**
	 * 获取进版广告图
	 * 
	 * @param context
	 * @return
	 */
	public static String getAdvPic(Context context) {
		return getPref(context).getString(ADV_PIC, "");
	}

	/**
	 * 设置进版广告图
	 * 
	 * @param context
	 * @param time
	 */
	public static void setAdvPic(Context context, String url) {
		Editor editor = getPref(context).edit();
		editor.putString(ADV_PIC, url);
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
}
