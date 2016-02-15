package cn.com.modernmedia.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

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
	private static final String APP_UPDATETIME = "app_updatetime";
	private static final String ADV_UPDATETIME = "adv_updatetime";
	private static final String DOWN = "down";
	private static final String UPDATE = "update";
	private static final String LINE_HEIGHT = "line_height";// webview行间距
	private static final String INDEX_UPDATE_TIME = "index_update_time_";// 首页、栏目首页更新时间
	private static final String AFTER_PUSH = "after_push"; // 用于存储第一次打开文章时是否在阅读push文章后
	private static final String DB_CHANGED = "db_changed";
	private static final String LAST_ISSUE_PUBLISH_TIME = "last_issue_publish_time_";// 往期文章更新时间
	private static final String INDEX_HEAD_AUTO_LOOP = "index_head_auto_loop";// 首页自动轮播开关
	private static final String WIFI_AUTO_PLAY_VEDIO = "wifi_auto_play_vedio";// WiFi下自动播放视频

	public static Map<String, String> columnTitleMap = new HashMap<String, String>();// tagname对于的cat名称
	public static Map<String, Integer> columnColorMap = new HashMap<String, Integer>();// tagname对应的cat颜色
	public static Map<String, List<String>> columnPicMap = new HashMap<String, List<String>>();// tagname对应的图片列表
	public static Map<String, List<String>> columnBigMap = new HashMap<String, List<String>>();// tagname对应的大图列表

	public static void clear() {
		columnTitleMap.clear();
		columnColorMap.clear();
		columnPicMap.clear();
		columnBigMap.clear();
	}

	private static SharedPreferences getPref(Context context) {
		if (mPref == null) {
			mPref = PreferenceManager.getDefaultSharedPreferences(context);
		}
		return mPref;
	}
	
	/**
	 * 是否WiFi下自动播放视频
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getWiFiAutoPlayVedio(Context context) {
		return getPref(context).getBoolean(WIFI_AUTO_PLAY_VEDIO, true);
	}

	/**
	 * 设置WiFi下自动播放视频
	 * 
	 * @param context
	 */
	public static void setWiFiAutoPlayVedio(Context context, boolean ifAuto) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(WIFI_AUTO_PLAY_VEDIO, ifAuto);
		editor.commit();
	}

	/**
	 * 是否首页自动轮播
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getIndexHeadAutoLoop(Context context) {
		return getPref(context).getBoolean(INDEX_HEAD_AUTO_LOOP, true);
	}

	/**
	 * 设置首页是否轮播
	 * 
	 * @param context
	 */
	public static void setIndexHeadAutoLoop(Context context, boolean ifAuto) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(INDEX_HEAD_AUTO_LOOP, ifAuto);
		editor.commit();
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
		return getPref(context).getInt(
				FONT_SIZE,
				(ConstData.getInitialAppId() == 20 || ConstData
						.getInitialAppId() == 1) ? 3 : 1);
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
	 * 获取最近一次更新的APP_UPDATETIME
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppUpdateTime(Context context) {
		return getPref(context).getString(APP_UPDATETIME, "");
	}

	/**
	 * 保存最近一次获取的APP_UPDATETIME
	 * 
	 * @param context
	 * @param time
	 */
	public static void setAppUpdateTime(Context context, String time) {
		Editor editor = getPref(context).edit();
		editor.putString(APP_UPDATETIME, time);
		editor.commit();
	}

	/**
	 * 获取最近一次更新的ADV_UPDATETIME
	 * 
	 * @param context
	 * @return
	 */
	public static String getAdvUpdateTime(Context context) {
		return getPref(context).getString(ADV_UPDATETIME, "");
	}

	/**
	 * 保存最近一次获取的ADV_UPDATETIME
	 * 
	 * @param context
	 * @param time
	 */
	public static void setAdvUpdateTime(Context context, String time) {
		Editor editor = getPref(context).edit();
		editor.putString(ADV_UPDATETIME, time);
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
	 * 获取上次是否阅读过push的文章
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getAfterPushStatus(Context context) {
		return getPref(context).getBoolean(AFTER_PUSH, false);
	}

	/**
	 * 设置上次是否阅读过push的文章
	 * 
	 * @param context
	 * @param status
	 *            true:开启；otherwise:关闭
	 */
	public static void setAfterPushStatus(Context context, boolean status) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(AFTER_PUSH, status);
		editor.commit();
	}

	/**
	 * 是否已经修改收藏数据库
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getDbChanged(Context context) {
		return getPref(context).getBoolean(DB_CHANGED, false);
	}

	/**
	 * 设置是否已经修改收藏数据库
	 * 
	 * @param context
	 */
	public static void setDbChanged(Context context) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(DB_CHANGED, true);
		editor.commit();
	}

	/**
	 * 获取上次点击的子栏目id
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static int getChildId(Context context, String key) {
		return getPref(context).getInt(key, -1);
	}

	/**
	 * 保存上次点击的子栏目id
	 * 
	 * @param context
	 * @param parentId
	 * @param childId
	 */
	public static void setChildId(Context context, int parentId, int childId) {
		Editor editor = getPref(context).edit();
		editor.putInt(parentId + "", childId);
		editor.commit();
	}

	/**
	 * 获取往期更新时间
	 * 
	 * @param context
	 * @return
	 */
	public static String getLastIssuePublishTime(Context context, String tagName) {
		return getPref(context)
				.getString(LAST_ISSUE_PUBLISH_TIME + tagName, "");
	}

	/**
	 * 保存往期更新时间
	 * 
	 * @param context
	 */
	public static void setLastIssuePublishTime(Context context, String tagName,
			String publishTime) {
		Editor editor = getPref(context).edit();
		editor.putString(LAST_ISSUE_PUBLISH_TIME + tagName, publishTime);
		editor.commit();
	}
}
