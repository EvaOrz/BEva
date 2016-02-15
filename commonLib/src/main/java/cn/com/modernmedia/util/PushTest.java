package cn.com.modernmedia.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * 测试推送
 * 
 * @author zhuqiao
 *
 */
public class PushTest {

	/**
	 * 获取普通推送intent
	 * 
	 * @return
	 */
	public static Intent getPushIntent() {
		Intent intent = new Intent();
		intent.putExtra("com.parse.Data", "alert");
		return intent;
	}

	public static Intent getPushToArticleIntent() {
		return getPushToArticleIntent(10062939);
	}

	/**
	 * 获取推送至文章的intent
	 * 
	 * @return
	 */
	public static Intent getPushToArticleIntent(int articleId) {
		Intent intent = new Intent();
		intent.putExtra("com.parse.Data",
				"{\"alert\":\"新接口\",\"na\":\"1177-304-10062939-\",\"na_tag\":\"cat_1177_304-"
						+ articleId + "\"}");
		return intent;
	}

	public static void pushDelayIntentToArticle(final Context context,
			Class<?> splashCls, final int delay) {
		pushDelayIntentToArticle(context, splashCls, delay, 10062939);
	}

	public static void pushDelayIntentToArticle(final Context context,
			Class<?> splashCls, final int delay, int article) {
		final Intent intent = getPushToArticleIntent(article);
		intent.setClass(context, splashCls);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				context.startActivity(intent);
			}
		}, delay);
	}
}
