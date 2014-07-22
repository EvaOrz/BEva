package cn.com.modernmedia.businessweek;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.ConstData;

import com.parse.Parse;
import com.parse.PushService;

/**
 * 全局变量
 * 
 * @author ZhuQiao
 * 
 */
public class MyApplication extends CommonApplication {
	public static final int APPID = 1;
	public static int DEBUG = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		ConstData.setAppId(APPID, DEBUG);
		if (DEBUG != 0) {
			Parse.initialize(this, "5kqOEMzkZ7OOdCkAwF6y6EIQg8qbLrCd15uTGqxj",
					"cudO599rrcn92vhCVYZUvf4SycfCh71XeaNIZXu5");
		} else {
			Parse.initialize(this, "GrmqBvHVN9OBwpcgRXz9uKSxFGPOrT0QN46D8kpS",
					"ITaYiCxWDBjcBxFYLceiwnlyzTFgmilT0WE82L8s");
		}

		// 订阅push通知
		// 最后一个参数YourActivity.class，是指点击任务栏推送消息时接收处理的Activity，可以从getIntent中取到推送数据，例如
		// ：
		// com.parse.Channel:null
		// com.parse.Data:{"alert":"test","push_hash":"098f6bcd4621d373cade4e832627b4f6"}
		PushService.subscribe(this, "businessweek", MainActivity.class);
		PushService.setDefaultPushCallback(this, MainActivity.class);
		mContext = this.getApplicationContext();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

}
