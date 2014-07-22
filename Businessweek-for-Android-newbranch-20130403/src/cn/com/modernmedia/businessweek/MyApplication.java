package cn.com.modernmedia.businessweek;

import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediasolo.SoloApplication;
import cn.com.modernmediausermodel.db.RecommendCardDb;
import cn.com.modernmediausermodel.db.TimelineDb;
import cn.com.modernmediausermodel.db.UserCardInfoDb;
import cn.com.modernmediausermodel.db.UserInfoDb;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.sina.SinaConstants;

import com.parse.Parse;
import com.parse.PushService;

/**
 * 全局变量
 * 
 * @author ZhuQiao
 * 
 */
public class MyApplication extends SoloApplication {
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
		UserConstData.initClass(LoginActivity.class, UserInfoActivity.class,
				ArticleActivity.class);
		if (CHANNEL.equals("googleplay")) {
			SinaConstants.APP_KEY = GOOGLE_APP_KEY;
		} else {
			SinaConstants.APP_KEY = APP_KEY;
		}
		super.onCreate();
		Parse.initialize(this, "GrmqBvHVN9OBwpcgRXz9uKSxFGPOrT0QN46D8kpS",
				"ITaYiCxWDBjcBxFYLceiwnlyzTFgmilT0WE82L8s");

		// 订阅push通知
		// 最后一个参数YourActivity.class，是指点击任务栏推送消息时接收处理的Activity，可以从getIntent中取到推送数据，例如
		// ：
		// com.parse.Channel:null
		// com.parse.Data:{"alert":"test","push_hash":"098f6bcd4621d373cade4e832627b4f6"}
		PushService.subscribe(this, "businessweek_" + ConstData.VERSION
				+ "_test", MainActivity.class, R.drawable.icon_36);
		PushService.setDefaultPushCallback(this, MainActivity.class);
		mContext = this.getApplicationContext();
	}

	public static void exit() {
		RecommendCardDb.getInstance(mContext).close();
		TimelineDb.getInstance(mContext).close();
		UserCardInfoDb.getInstance(mContext).close();
		UserInfoDb.getInstance(mContext).close();
		soloColumn = null;
	}
}
