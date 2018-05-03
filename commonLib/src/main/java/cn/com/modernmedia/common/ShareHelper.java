package cn.com.modernmedia.common;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.WeeklyLogEvent;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.unit.SlateDataHelper;

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
		if (SlateApplication.APP_ID == 1
				&& item.getProperty().getLevel() == 1
				&& !TextUtils.equals("1",
						SlateDataHelper.getIssueLevel(context))) {// 如果是商周，并且需要付费
			Toast.makeText(context, R.string.pay_for_share, Toast.LENGTH_SHORT)
					.show();// 请付费后分享
		} else
			new ShareDialog(context) {

				@Override
				public void logAndroidShareToSinaCount(String articleId,
						String columnId) {
					LogHelper.logShareArticleByWeibo(context, articleId,
							columnId);
				}

				@Override
				public void logAndroidShareToMail(String articleId,
						String columnId) {
					LogHelper.logShareArticleByEmail(context, articleId,
							columnId);
				}

				@Override
				public void logAndroidSaveToImageAlbum() {
				}

				@Override
				public void logAndroidShareToWeixin(String articleId,
						String columnId) {
					LogHelper.logShareArticleByWinxin(context, articleId,
							columnId);
				}

				@Override
				public void logAndroidShareToMoments(String articleId,
						String columnId) {
					LogHelper.logShareArticleByWinxinMoments(context,
							articleId, columnId);
				}
			}.startShareDefault(item);
	}

	/**
	 * iweekly分享
	 * 
	 * @param context
	 * @param share
	 */
	private static void shareByWeekly(final Context context, ArticleItem item) {
		new ShareDialog(context) {

			@Override
			public void logAndroidShareToSinaCount(String articleId,
					String columnId) {
				WeeklyLogEvent.logAndroidShareToSinaCount(context);
			}

			@Override
			public void logAndroidShareToMail(String articleId, String columnId) {
			}

			@Override
			public void logAndroidSaveToImageAlbum() {
				WeeklyLogEvent.logAndroidSaveToImageAlbum(context);
			}

			@Override
			public void logAndroidShareToWeixin(String articleId,
					String columnId) {
				LogHelper.logShareArticleByWinxin(context, articleId, columnId);
			}

			@Override
			public void logAndroidShareToMoments(String articleId,
					String columnId) {
				LogHelper.logShareArticleByWinxinMoments(context, articleId,
						columnId);
			}
		}.startShareWeekly(item);
	}

}
