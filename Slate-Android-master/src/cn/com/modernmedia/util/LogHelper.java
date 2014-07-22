package cn.com.modernmedia.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.flurry.android.FlurryAgent;

/**
 * Flurryͳ��
 * 
 * @author ZhuQiao
 * 
 */
public class LogHelper {
	public static final String OPEN_COLUMN_LIST = "OpenColumnList";
	public static final String OPEN_FAVORITE_ARTICLE_LIST = "OpenFavoriteArticleList";
	public static final String OPEN_COLUMN = "OpenColumn";
	public static final String OPEN_ARTICLE_FROM_FAVORITE_ARTICLE_LIST = "OpenArticleFromFavoriteArticleList";
	public static final String OPEN_ARTICLE_FROM_COLUMN_PAGE = "OpenArticleFromColumnPage";
	public static final String ADD_FAVORITE_ARTICLE = "AddFavoriteArticle";
	public static final String CHANGE_ARTICLE_FONT_SIZE = "ChangeArticleFontSize";
	public static final String SHARE_ARTICLE_BY_EMAIL = "ShareArticleByEmail";
	public static final String SHARE_ARTICLE_BY_WEIBO = "ShareArticleByWeibo";
	public static final String ANDROID_ADV_ENTERAPP = "android-adv-enterapp";
	public static final String ANDROID_ADV_HEADLINE = "android-adv-headline";
	public static final String ANDROID_SHOW_HIGHLIGHTS = "android-show-highlights";
	public static final String ANDROID_SHOW_HEADLINE = "android-show-headline";
	public static final String ANDROID_TOUCH_HEADLINE = "android-touch-headline";
	public static final String ANDROID_TOUCH_HIGHLIGHTS_TABLE = "android-touch-highlights-table";
	public static final String ANDROID_TOUCH_MORENEWS = "android-touch-morenews";
	public static final String ANDROID_SHOW_COLUMN = "android-show-column";
	public static final String ANDROID_SHOW_ARTICLE = "android-show-article";

	/**
	 * ���������Ŀ�б�
	 * 
	 * @param context
	 */
	public static void logOpenColumnList(Context context) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(OPEN_COLUMN_LIST, map);
	}

	/**
	 * �����Ҳ��ղ��б�
	 */
	public static void logOpenFavoriteArticleList() {
		FlurryAgent.logEvent(OPEN_FAVORITE_ARTICLE_LIST);
	}

	/**
	 * �������Ŀ�б�����Ŀ
	 * 
	 * @param context
	 * @param columnId
	 */
	public static void logOpenColumn(Context context, String columnId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(OPEN_COLUMN, map);
	}

	/**
	 * ���Ҳ��ղ��б�������
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logOpenArticleFromFavoriteArticleList(Context context,
			String articleId, String columnId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(OPEN_ARTICLE_FROM_FAVORITE_ARTICLE_LIST, map);
	}

	/**
	 * ����Ŀҳ�������
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logOpenArticleFromColumnPage(Context context,
			String articleId, String columnId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(OPEN_ARTICLE_FROM_COLUMN_PAGE, map);
	}

	/**
	 * ����ҳ�������ղء���ť
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logAddFavoriteArticle(Context context, String articleId,
			String columnId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(ADD_FAVORITE_ARTICLE, map);
	}

	/**
	 * ����ҳ������Aa����ť
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logChangeArticleFontSize(Context context,
			String articleId, String columnId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(CHANGE_ARTICLE_FONT_SIZE, map);
	}

	/**
	 * ����ҳ���������by email
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logShareArticleByEmail(Context context,
			String articleId, String columnId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(SHARE_ARTICLE_BY_EMAIL, map);
	}

	/**
	 * ����ҳ���������by sina weibo
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logShareArticleByWeibo(Context context,
			String articleId, String columnId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(SHARE_ARTICLE_BY_WEIBO, map);
	}

	/**
	 * ������
	 * 
	 * @param advId
	 */
	public static void logAndroidAdvEnterapp(String advId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("advId", advId);
		FlurryAgent.logEvent(ANDROID_ADV_ENTERAPP, map);
	}

	/**
	 * ͷ�����
	 * 
	 * @param advId
	 */
	public static void logAndroidAdvHeadline(String advId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("advId", advId);
		FlurryAgent.logEvent(ANDROID_ADV_HEADLINE, map);
	}

	/**
	 * ����ҳչʾ(��ҳչʾ)
	 */
	public static void logAndroidShowHighlights() {
		FlurryAgent.logEvent(ANDROID_SHOW_HIGHLIGHTS);
	}

	/**
	 * ����ҳͷ��չʾ(��ҳ����ͼ)
	 * 
	 * @param position
	 *            ����ͼλ��
	 */
	public static void lodAndroidShowHeadline(int position) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("index", position + "");
		FlurryAgent.logEvent(ANDROID_SHOW_HEADLINE, map);
	}

	/**
	 * ����ҳͷ�����(��ҳ�������ͼ)
	 * 
	 * @param position
	 *            ����ͼλ��
	 */
	public static void logAndroidTouchHeadline(int position) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("index", position + "");
		FlurryAgent.logEvent(ANDROID_TOUCH_HEADLINE, map);
	}

	/**
	 * ����ҳ�����(��ҳ)
	 * 
	 * @param section
	 *            �ڼ�����Ŀ
	 * @param row
	 *            ��Ŀ�еڼ���λ��
	 */
	public static void logAndroidTouchHighlightsTable(int section, int row) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("section", section + "");
		map.put("row", row + "");
		FlurryAgent.logEvent(ANDROID_TOUCH_HIGHLIGHTS_TABLE, map);
	}

	/**
	 * ���༴ʱͷ����ť���
	 */
	public static void logAndroidTouchMorenews() {
		FlurryAgent.logEvent(ANDROID_TOUCH_MORENEWS);
	}

	/**
	 * ��Ŀҳչʾ
	 * 
	 * @param columnId
	 *            ��Ŀid
	 */
	public static void logAndroidShowColumn(Context context, String columnId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(ANDROID_SHOW_COLUMN, map);
	}

	/**
	 * ����ҳչʾ
	 * 
	 * @param context
	 * @param columnId
	 * @param articleId
	 */
	public static void logAndroidShowArticle(Context context, String columnId,
			String articleId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("issueId", DataHelper.getIssueId(context) + "");
		map.put("columnId", columnId);
		map.put("articleId", articleId);
		FlurryAgent.logEvent(ANDROID_SHOW_ARTICLE, map);
	}
}
