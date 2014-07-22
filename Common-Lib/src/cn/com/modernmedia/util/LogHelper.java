package cn.com.modernmedia.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.flurry.android.FlurryAgent;

/**
 * Flurry统计
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
	 * 呼出左侧栏目列表
	 * 
	 * @param context
	 */
	public static void logOpenColumnList(Context context) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(OPEN_COLUMN_LIST, map);
	}

	/**
	 * 呼出右侧收藏列表
	 */
	public static void logOpenFavoriteArticleList() {
		if (ConstData.getAppId() != 1)
			return;
		FlurryAgent.logEvent(OPEN_FAVORITE_ARTICLE_LIST);
	}

	/**
	 * 在左侧栏目列表点击栏目
	 * 
	 * @param context
	 * @param columnId
	 */
	public static void logOpenColumn(Context context, String columnId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(OPEN_COLUMN, map);
	}

	/**
	 * 在右侧收藏列表点击文章
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logOpenArticleFromFavoriteArticleList(Context context,
			String articleId, String columnId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(OPEN_ARTICLE_FROM_FAVORITE_ARTICLE_LIST, map);
	}

	/**
	 * 在栏目页点击文章
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logOpenArticleFromColumnPage(Context context,
			String articleId, String columnId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(OPEN_ARTICLE_FROM_COLUMN_PAGE, map);
	}

	/**
	 * 文章页里点击“收藏”按钮
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logAddFavoriteArticle(Context context, String articleId,
			String columnId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(ADD_FAVORITE_ARTICLE, map);
	}

	/**
	 * 文章页里点击“Aa”按钮
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logChangeArticleFontSize(Context context,
			String articleId, String columnId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(CHANGE_ARTICLE_FONT_SIZE, map);
	}

	/**
	 * 文章页里分享文章by email
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logShareArticleByEmail(Context context,
			String articleId, String columnId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(SHARE_ARTICLE_BY_EMAIL, map);
	}

	/**
	 * 文章页里分享文章by sina weibo
	 * 
	 * @param context
	 * @param articleId
	 * @param columnId
	 */
	public static void logShareArticleByWeibo(Context context,
			String articleId, String columnId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("articleId", articleId);
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(SHARE_ARTICLE_BY_WEIBO, map);
	}

	/**
	 * 进版广告
	 * 
	 * @param advId
	 */
	public static void logAndroidAdvEnterapp(String advId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("advId", advId);
		FlurryAgent.logEvent(ANDROID_ADV_ENTERAPP, map);
	}

	/**
	 * 头条广告
	 * 
	 * @param advId
	 */
	public static void logAndroidAdvHeadline(String advId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("advId", advId);
		FlurryAgent.logEvent(ANDROID_ADV_HEADLINE, map);
	}

	/**
	 * 焦点页展示(首页展示)
	 */
	public static void logAndroidShowHighlights() {
		if (ConstData.getAppId() != 1)
			return;
		FlurryAgent.logEvent(ANDROID_SHOW_HIGHLIGHTS);
	}

	/**
	 * 焦点页头条展示(首页焦点图)
	 * 
	 * @param position
	 *            焦点图位置
	 */
	public static void lodAndroidShowHeadline(int position) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("index", position + "");
		FlurryAgent.logEvent(ANDROID_SHOW_HEADLINE, map);
	}

	/**
	 * 焦点页头条点击(首页点击焦点图)
	 * 
	 * @param position
	 *            焦点图位置
	 */
	public static void logAndroidTouchHeadline(int position) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("index", position + "");
		FlurryAgent.logEvent(ANDROID_TOUCH_HEADLINE, map);
	}

	/**
	 * 焦点页表格点击(首页)
	 * 
	 * @param section
	 *            第几个栏目
	 * @param row
	 *            栏目中第几个位置
	 */
	public static void logAndroidTouchHighlightsTable(int section, int row) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("section", section + "");
		map.put("row", row + "");
		FlurryAgent.logEvent(ANDROID_TOUCH_HIGHLIGHTS_TABLE, map);
	}

	/**
	 * 更多即时头条按钮点击
	 */
	public static void logAndroidTouchMorenews() {
		if (ConstData.getAppId() != 1)
			return;
		FlurryAgent.logEvent(ANDROID_TOUCH_MORENEWS);
	}

	/**
	 * 栏目页展示
	 * 
	 * @param columnId
	 *            栏目id
	 */
	public static void logAndroidShowColumn(Context context, String columnId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("columnId", columnId);
		map.put("issueId", DataHelper.getIssueId(context) + "");
		FlurryAgent.logEvent(ANDROID_SHOW_COLUMN, map);
	}

	/**
	 * 文章页展示
	 * 
	 * @param context
	 * @param columnId
	 * @param articleId
	 */
	public static void logAndroidShowArticle(Context context, String columnId,
			String articleId) {
		if (ConstData.getAppId() != 1)
			return;
		Map<String, String> map = new HashMap<String, String>();
		map.put("issueId", DataHelper.getIssueId(context) + "");
		map.put("columnId", columnId);
		map.put("articleId", articleId);
		FlurryAgent.logEvent(ANDROID_SHOW_ARTICLE, map);
	}
}
