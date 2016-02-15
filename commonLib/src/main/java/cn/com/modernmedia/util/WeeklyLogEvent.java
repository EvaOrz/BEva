package cn.com.modernmedia.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmediaslate.unit.Tools;

import com.flurry.android.FlurryAgent;

public class WeeklyLogEvent {
	public static final String ANDROID_ADD_FAVOURITE_COUNT = "android-add-favourite-count";
	public static final String ANDROID_COLUMN_ARTICLE_CLICK_COUNT = "android-column-article-click-count";
	public static final String ANDROID_COLUMN_HEADVIEW_CLICK_COUNT = "android-column-headview-click-count";
	public static final String ANDROID_COLUMN_HEADVIEW_SHOW_COUNT = "android-column-headview-show-count";
	public static final String ANDROID_ENTER_COVERFLOW = "android-enter-coverflow";
	public static final String ANDROID_SAVE_TO_IMAGE_ALBUM = "android-save-to-image-album";
	public static final String ANDROID_SHARE_TO_SINA_COUNT = "android-share-to-sina-count";
	public static final String ANDROID_SHOW_COLUMN_BY_CLICK = "android-show-column-by-click";
	public static final String ANDROID_SHOW_COVERFLOW = "android-show-coverflow";
	public static final String ANDROID_VIEW_PICTURE = "android-view-picture";
	public static final String ANDROID_SHOW_SHIYE = "android-show-shiye";
	public static final String ANDROID_SHOW_SHIYE_BY_CLICK = "android-show-shiye-by-click";

	/**
	 * 显示视野
	 */
	public static void logAndroidShowShiYe(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_SHOW_SHIYE);
		SelectionHelper.getInstance().add(context, ANDROID_SHOW_SHIYE, map);
	}

	/**
	 * 点击视野
	 */
	public static void logAndroidShowShiYeByClick(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_SHOW_SHIYE_BY_CLICK);
		SelectionHelper.getInstance().add(context, ANDROID_SHOW_SHIYE_BY_CLICK,
				map);
	}

	/**
	 * 添加收藏
	 */
	public static void logAndroidAddFavouriteCount(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_ADD_FAVOURITE_COUNT);
		SelectionHelper.getInstance().add(context, ANDROID_ADD_FAVOURITE_COUNT,
				map);
	}

	/**
	 * 点击栏目列表文章
	 */
	public static void logAndroidColumnArticleClickCount(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_COLUMN_ARTICLE_CLICK_COUNT);
		SelectionHelper.getInstance().add(context,
				ANDROID_COLUMN_ARTICLE_CLICK_COUNT, map);
	}

	/**
	 * 点击栏目headview文章
	 */
	public static void logAndroidColumnHeadviewClickCount(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_COLUMN_HEADVIEW_CLICK_COUNT);
		SelectionHelper.getInstance().add(context,
				ANDROID_COLUMN_HEADVIEW_CLICK_COUNT, map);
	}

	/**
	 * 栏目headview显示
	 */
	public static void logAndroidColumnHeadviewShowCount(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_COLUMN_HEADVIEW_SHOW_COUNT);
		SelectionHelper.getInstance().add(context,
				ANDROID_COLUMN_HEADVIEW_SHOW_COUNT, map);
	}

	/**
	 * 进入画报
	 */
	public static void logAndroidEnterCoverflow(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_ENTER_COVERFLOW);
		SelectionHelper.getInstance()
				.add(context, ANDROID_ENTER_COVERFLOW, map);
	}

	/**
	 * 保存图片至相册
	 */
	public static void logAndroidSaveToImageAlbum(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_SAVE_TO_IMAGE_ALBUM);
		SelectionHelper.getInstance().add(context, ANDROID_SAVE_TO_IMAGE_ALBUM,
				map);
	}

	/**
	 * 分享至新浪
	 */
	public static void logAndroidShareToSinaCount(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_SHARE_TO_SINA_COUNT);
		SelectionHelper.getInstance().add(context, ANDROID_SHARE_TO_SINA_COUNT,
				map);
	}

	/**
	 * 点击栏目
	 */
	public static void logAndroidShowColumnByClick(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_SHOW_COLUMN_BY_CLICK);
		SelectionHelper.getInstance().add(context,
				ANDROID_SHOW_COLUMN_BY_CLICK, map);
	}

	/**
	 * 显示画报
	 */
	public static void logAndroidShowCoverflow(Context context) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		FlurryAgent.logEvent(ANDROID_SHOW_COVERFLOW);
		SelectionHelper.getInstance().add(context, ANDROID_SHOW_COVERFLOW, map);
	}

	/**
	 * 视野栏目中的图片统计
	 * 
	 * @param url
	 *            图片url
	 * @param position
	 *            图片位置
	 */
	public static void logAndroidViewPicture(Context context, String url,
			int position) {
		Map<String, String> map = setDefaultMap(context, "0", "0");
		map.put("uri", url);
		map.put("position", position + "");
		FlurryAgent.logEvent(ANDROID_VIEW_PICTURE, map);
		SelectionHelper.getInstance().add(context, ANDROID_VIEW_PICTURE, map);
	}

	/**
	 * deviceToken|uid|articleId|catId 例：
	 * 314D3D6C-A350-4924-BCBA-DD8644445400|17259|0|cat_32_zuixin
	 */
	private static Map<String, String> setDefaultMap(Context context,
			String articleId, String catId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("linkageKey",
				CommonApplication.getMyUUID() + "|" + Tools.getUid(context)
						+ "|" + articleId + "|" + catId);
		return map;
	}
}
