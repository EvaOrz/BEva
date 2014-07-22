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
 * ��������
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
	private static final String LINE_HEIGHT = "line_height";// webview�м��

	public static Map<Integer, String> columnTitleMap = new HashMap<Integer, String>();// catid���ڵ�cat����
	public static Map<Integer, Integer> columnColorMap = new HashMap<Integer, Integer>();// catid��Ӧ��cat��ɫ
	public static Map<Integer, String> columnRowMap = new HashMap<Integer, String>();// catid��Ӧ�ļ�ͷͼƬ
	public static Map<Integer, List<CatItem>> childMap = new HashMap<Integer, List<CatItem>>();// key:parent_cat_id,value:����Ŀ�б�

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
	 * �Ƿ��ǵ�һ�ν�Ӧ��
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getFirstStartApp(Context context) {
		return getPref(context).getBoolean(FIRST_USE, true);
	}

	/**
	 * ���ò��ǵ�һ�ν�Ӧ��
	 * 
	 * @param context
	 */
	public static void setFirstStartApp(Context context) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(FIRST_USE, false);
		editor.commit();
	}

	/**
	 * ��ȡ�����issue_id
	 * 
	 * @param context
	 * @return ���û��������߱��û��ֶ������������-1
	 */
	public static int getIssueId(Context context) {
		return getPref(context).getInt(ISSUE_ID, -1);
	}

	/**
	 * �������һ�λ�ȡ����issueid
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
	 * ��ȡ������������С
	 * 
	 * @param context
	 * @return
	 */
	public static int getFontSize(Context context) {
		return getPref(context).getInt(FONT_SIZE,
				ConstData.getInitialAppId() == 20 ? 3 : 1);
	}

	/**
	 * ���������С
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
	 * ��ȡ���һ�θ��¹���ColumnUpdateTime
	 * 
	 * @param context
	 * @return
	 */
	public static long getColumnUpdateTime(Context context) {
		return getPref(context).getLong(COLUMN_UPDATE_TIME, -1);
	}

	/**
	 * �������һ�λ�ȡ��ColumnUpdateTime
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
	 * ��ȡ���һ�θ��µ�ARTICLE_UPDATE_TIME
	 * 
	 * @param context
	 * @return
	 */
	public static long getArticleUpdateTime(Context context) {
		return getPref(context).getLong(ARTICLE_UPDATE_TIME, -1);
	}

	/**
	 * �������һ�λ�ȡ��ARTICLE_UPDATE_TIME
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
	 * ͳ������
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getDown(Context context) {
		return getPref(context).getBoolean(DOWN, false);
	}

	/**
	 * ����ͳ�Ƴɹ�
	 * 
	 * @param context
	 */
	public static void setDown(Context context) {
		Editor editor = getPref(context).edit();
		editor.putBoolean(DOWN, true);
		editor.commit();
	}

	/**
	 * ��ȡ�ϴε��ȡ��������ʱ��
	 * 
	 * @return
	 */
	public static long getUpdate(Context context) {
		return getPref(context).getLong(UPDATE, 0);
	}

	/**
	 * ��������ʱ���ȡ����ʱ��
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
	 * ������ҳ���ͼ��ʼʱ��
	 * 
	 * @return
	 */
	public static String getStartTime(Context context) {
		return getPref(context).getString(START_TIME, "");
	}

	/**
	 * ���ý�����ҳ���ͼ��ʼʱ��
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
	 * ������ҳ���ͼ����ʱ��
	 * 
	 * @return
	 */
	public static String getEndTime(Context context) {
		return getPref(context).getString(END_TIME, "");
	}

	/**
	 * ���ý�����ҳ���ͼ����ʱ��
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
	 * ��ȡ������ͼ
	 * 
	 * @param context
	 * @return
	 */
	public static String getAdvPic(Context context) {
		return getPref(context).getString(ADV_PIC, "");
	}

	/**
	 * ���ý�����ͼ
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
