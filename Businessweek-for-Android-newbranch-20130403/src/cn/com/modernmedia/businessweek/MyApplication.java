package cn.com.modernmedia.businessweek;

import cn.com.modernmedia.common.WeixinShare;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.sina.SinaConstants;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmediausermodel.util.UserConstData;

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

	@Override
	public void onCreate() {
		super.onCreate();
		ConstData.setAppId(APPID, DEBUG);
		UserConstData.setAppId(APPID, DEBUG);
		drawCls = R.drawable.class;
		stringCls = R.string.class;
		mainCls = MainActivity.class;
		if (CHANNEL.equals("googleplay")) {
			SinaConstants.APP_KEY = mConfig.getWeibo_app_id_goole();
			WeixinShare.APP_ID = mConfig.getWeixin_app_id_google();
		}

		PushService.subscribe(this, "businessweek_" + ConstData.VERSION,
				MainActivity.class, R.drawable.icon_36);

		PushService.setDefaultPushCallback(this, MainActivity.class);
		mContext = this.getApplicationContext();
	}

}
