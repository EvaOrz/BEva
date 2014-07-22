package cn.com.modernmedia.businessweek;

import cn.com.modernmedia.common.WeixinShare;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ConstData.APP_TYPE;
import cn.com.modernmedia.util.sina.SinaConstants;
import cn.com.modernmedia.views.ArticleActivity;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmediausermodel.util.UserConstData;

import com.parse.Parse;
import com.parse.PushService;

/**
 * 全局变量
 * 
 * @author ZhuQiao
 * 
 */
public class MyApplication extends ViewsApplication {
	// 1--商周简体，18---商周繁体
	public static int APPID = 1;
	public static int DEBUG = 0;
	public static final String APP_KEY = "3735038";
	public static final String GOOGLE_APP_KEY = "2994328520";

	@Override
	public void onCreate() {
		initChannel();
		System.out.println(CHANNEL);
		ConstData.setAppId(APPID, DEBUG);
		UserConstData.setAppId(APPID, 0);
		UserConstData.initClass(ArticleActivity.class);
		drawCls = R.drawable.class;
		stringCls = R.string.class;
		mainCls = MainActivity.class;
		appType = APP_TYPE.BUSINESS;
		if (CHANNEL.equals("googleplay")) {
			SinaConstants.APP_KEY = GOOGLE_APP_KEY;
		} else {
			SinaConstants.APP_KEY = APP_KEY;
		}
		WeixinShare.APP_ID = "wxd2ee0aa99bdda0a5";
		super.onCreate();
		// 正式
		Parse.initialize(this, "GrmqBvHVN9OBwpcgRXz9uKSxFGPOrT0QN46D8kpS",
				"ITaYiCxWDBjcBxFYLceiwnlyzTFgmilT0WE82L8s");
		PushService.subscribe(this, "businessweek_" + ConstData.VERSION,
				MainActivity.class, R.drawable.icon_36);

		PushService.setDefaultPushCallback(this, MainActivity.class);
		mContext = this.getApplicationContext();
	}

}
