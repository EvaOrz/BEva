package cn.com.modernmedia.businessweek;

import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediasolo.SoloApplication;
import cn.com.modernmediausermodel.util.UserConstData;

import com.parse.Parse;
import com.parse.PushService;

/**
 * 全局变量
 * 
 * @author ZhuQiao
 * 
 */
public class MyApplication extends SoloApplication {
	public static int APPID = 1;
	public static int DEBUG = 0;

	@Override
	public void onCreate() {
		initChannel();
		System.out.println(CHANNEL);
		ConstData.setAppId(APPID, DEBUG);
		UserConstData.setAppId(APPID, 0);
		super.onCreate();
		Parse.initialize(this, "GrmqBvHVN9OBwpcgRXz9uKSxFGPOrT0QN46D8kpS",
				"ITaYiCxWDBjcBxFYLceiwnlyzTFgmilT0WE82L8s");

		// 订阅push通知
		// 最后一个参数YourActivity.class，是指点击任务栏推送消息时接收处理的Activity，可以从getIntent中取到推送数据，例如
		// ：
		// com.parse.Channel:null
		// com.parse.Data:{"alert":"test","push_hash":"098f6bcd4621d373cade4e832627b4f6"}
		PushService.subscribe(this, "businessweek_" + ConstData.VERSION,
				MainActivity.class, R.drawable.icon_36);
		PushService.setDefaultPushCallback(this, MainActivity.class);
		mContext = this.getApplicationContext();
	}
}
