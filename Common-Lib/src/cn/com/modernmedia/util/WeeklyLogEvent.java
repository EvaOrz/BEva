package cn.com.modernmedia.util;

import java.util.HashMap;
import java.util.Map;

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

	/**
	 * 添加收藏
	 */
	public static void logAndroidAddFavouriteCount() {
		FlurryAgent.logEvent(ANDROID_ADD_FAVOURITE_COUNT);
	}

	/**
	 * 点击栏目列表文章
	 */
	public static void logAndroidColumnArticleClickCount() {
		FlurryAgent.logEvent(ANDROID_COLUMN_ARTICLE_CLICK_COUNT);
	}

	/**
	 * 点击栏目headview文章
	 */
	public static void logAndroidColumnHeadviewClickCount() {
		FlurryAgent.logEvent(ANDROID_COLUMN_HEADVIEW_CLICK_COUNT);
	}

	/**
	 * 栏目headview显示
	 */
	public static void logAndroidColumnHeadviewShowCount() {
		FlurryAgent.logEvent(ANDROID_COLUMN_HEADVIEW_SHOW_COUNT);
	}

	/**
	 * 进入画报
	 */
	public static void logAndroidEnterCoverflow() {
		FlurryAgent.logEvent(ANDROID_ENTER_COVERFLOW);
	}

	/**
	 * 保存图片至相册
	 */
	public static void logAndroidSaveToImageAlbum() {
		FlurryAgent.logEvent(ANDROID_SAVE_TO_IMAGE_ALBUM);
	}

	/**
	 * 分享至新浪
	 */
	public static void logAndroidShareToSinaCount() {
		FlurryAgent.logEvent(ANDROID_SHARE_TO_SINA_COUNT);
	}

	/**
	 * 点击栏目
	 */
	public static void logAndroidShowColumnByClick() {
		FlurryAgent.logEvent(ANDROID_SHOW_COLUMN_BY_CLICK);
	}

	/**
	 * 显示画报
	 */
	public static void logAndroidShowCoverflow() {
		FlurryAgent.logEvent(ANDROID_SHOW_COVERFLOW);
	}

	/**
	 * 视野栏目中的图片统计
	 * 
	 * @param url
	 *            图片url
	 * @param position
	 *            图片位置
	 */
	public static void logAndroidViewPicture(String url, int position) {
		 Map<String, String> map = new HashMap<String, String>();
		 map.put("uri", url);
		 map.put("position", position + "");
		 FlurryAgent.logEvent(ANDROID_VIEW_PICTURE, map);
	}
}
