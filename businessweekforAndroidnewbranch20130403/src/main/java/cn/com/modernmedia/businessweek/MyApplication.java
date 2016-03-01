package cn.com.modernmedia.businessweek;

import android.net.TrafficStats;
import android.util.Log;
import cn.com.modernmedia.common.WeixinShare;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.sina.SinaConstants;
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
		// test
		// Parse.initialize(this, "5kqOEMzkZ7OOdCkAwF6y6EIQg8qbLrCd15uTGqxj",
		// "cudO599rrcn92vhCVYZUvf4SycfCh71XeaNIZXu5");
		// PushService.subscribe(this, "new_parse_test",
		// SplashScreenActivity.class, R.drawable.icon_36);

		// 正式
		Parse.initialize(this, "GrmqBvHVN9OBwpcgRXz9uKSxFGPOrT0QN46D8kpS",
				"ITaYiCxWDBjcBxFYLceiwnlyzTFgmilT0WE82L8s");

		PushService.subscribe(this, "businessweek_" + ConstData.VERSION,
				MainActivity.class, R.drawable.icon_36);

		PushService.setDefaultPushCallback(this, SplashScreenActivity.class);
		mContext = this.getApplicationContext();
		showLiuliang();
	}

	// static long getMobileRxBytes() //获取通过Mobile连接收到的字节总数，不包含WiFi
	// static long getMobileRxPackets() //获取Mobile连接收到的数据包总数
	// static long getMobileTxBytes() //Mobile发送的总字节数
	// static long getMobileTxPackets() //Mobile发送的总数据包数
	// static long getTotalRxPackets() //总的接受数据包数，包含Mobile和WiFi等
	// static long getTotalTxPackets() //发送的总数据包数，包含Mobile和WiFi等
	// static long getUidRxBytes(int uid) //获取某个网络UID的接受字节数
	// static long getUidTxBytes(int uid) //获取某个网络UID的发送字节数
	// 总接受流量TrafficStats.getTotalRxBytes()，
	// 总发送流量TrafficStats.getTotalTxBytes());
	/**
	 * 单位M
	 */
	public void showLiuliang() {
		Log.e("*总接受数据包数*",
				(TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0
						: TrafficStats.getTotalRxPackets()) + "");
		Log.e("*总发送数据包数*",
				(TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0
						: TrafficStats.getTotalTxPackets()) + "");
		Log.e("*总接受流量*",
				(TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0
						: (TrafficStats.getTotalRxBytes() / 1024 / 1024)) + "");
		Log.e("*总发送流量*",
				(TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0
						: (TrafficStats.getTotalTxBytes() / 1024 / 1024)) + "");
	}
}
