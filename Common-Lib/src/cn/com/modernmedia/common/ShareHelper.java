package cn.com.modernmedia.common;

import android.content.Context;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.WeeklyLogEvent;

public class ShareHelper {

	/**
	 * 首页列表分享
	 * 
	 * @param context
	 * @param item
	 */
	public static void shareByDefault(Context context, ArticleItem item) {
		share(context, item);
	}

	/**
	 * iweekly文章分享
	 * 
	 * @param context
	 * @param item
	 */
	public static void shareByWeekly(Context context, ArticleItem item,
			int bottomResId) {
		if (item != null)
			item.setBottomResId(bottomResId);
		shareByWeekly(context, item);
	}

	/**
	 * 非iweekly分享
	 * 
	 * @param context
	 * @param share
	 */
	private static void share(final Context context, ArticleItem item) {
		new ShareDialog(context) {

			@Override
			public void logAndroidShareToSinaCount(String articleId,
					String columnId) {
				LogHelper.logShareArticleByWeibo(context, articleId, columnId);
			}

			@Override
			public void logAndroidShareToMail(String articleId, String columnId) {
				LogHelper.logShareArticleByEmail(context, articleId, columnId);
			}

			@Override
			public void logAndroidSaveToImageAlbum() {
			}
		}.startShareDefault(item);
	}

	/**
	 * iweekly分享
	 * 
	 * @param context
	 * @param share
	 */
	private static void shareByWeekly(Context context, ArticleItem item) {
		new ShareDialog(context) {

			@Override
			public void logAndroidShareToSinaCount(String articleId,
					String columnId) {
				WeeklyLogEvent.logAndroidShareToSinaCount();
			}

			@Override
			public void logAndroidShareToMail(String articleId, String columnId) {
			}

			@Override
			public void logAndroidSaveToImageAlbum() {
				WeeklyLogEvent.logAndroidSaveToImageAlbum();
			}
		}.startShareWeekly(item);
	}

}
