package cn.com.modernmedia.businessweek;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.ConstData;

import com.parse.Parse;
import com.parse.PushService;

/**
 * ȫ�ֱ���
 * 
 * @author ZhuQiao
 * 
 */
public class MyApplication extends CommonApplication {
	// 1--���ܼ��壬18---���ܷ���
	public static int APPID = 1;
	public static int DEBUG = 0;

	@Override
	public void onCreate() {
		initChannel();
		System.out.println(CHANNEL);
		ConstData.setAppId(APPID, DEBUG);
		super.onCreate();
		if (DEBUG != 0) {
			Parse.initialize(this, "5kqOEMzkZ7OOdCkAwF6y6EIQg8qbLrCd15uTGqxj",
					"cudO599rrcn92vhCVYZUvf4SycfCh71XeaNIZXu5");
		} else {
			Parse.initialize(this, "GrmqBvHVN9OBwpcgRXz9uKSxFGPOrT0QN46D8kpS",
					"ITaYiCxWDBjcBxFYLceiwnlyzTFgmilT0WE82L8s");
		}

		// ����push֪ͨ
		// ���һ������YourActivity.class����ָ���������������Ϣʱ���մ����Activity�����Դ�getIntent��ȡ���������ݣ�����
		// ��
		// com.parse.Channel:null
		// com.parse.Data:{"alert":"test","push_hash":"098f6bcd4621d373cade4e832627b4f6"}
		getLocalLanguage();
		String traditional = language.equals(ZH_TW) ? "_traditional" : "";
		PushService.subscribe(this, "businessweek_" + ConstData.VERSION
				+ traditional, MainActivity.class);
		PushService.setDefaultPushCallback(this, MainActivity.class);
		mContext = this.getApplicationContext();
	}

}
