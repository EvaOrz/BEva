package cn.com.modernmediausermodel.util;

import cn.com.modernmediausermodel.api.UrlMaker;

/**
 * 
 * @author ZhuQiao
 * 
 */
public class UserConstData {
	/**
	 * 当前环境 0:线上，1：测试，2：开发
	 **/
	public static int IS_DEBUG = 0;
	/**
	 * 应用id bloomberg:1 modernlady:2 icollection:3 turningpoints:5 adv:6
	 * onewaystreet:10 zhihu:11 tanc:12 itv:13 life:14 car:15 lohas:16
	 * finance:17 bloomberg繁体版:18 iweekly:19
	 **/
	private static int APP_ID = 1;
	/** 设备号 **/
	public static String DEVICE_TYPE = "10";
	/** 数据类型（1 protobuf格式 2 json格式） **/
	public static final String DATA_TYPE = "2";
	/** toast显示时间 **/
	public static final int TOAST_LENGTH = 1000;

	/**
	 * 由于繁体版和简体版很多逻辑是一样的，如果碰到相同逻辑，只有appid不同，通过这个方法获取appid
	 * 
	 * @return
	 */
	public static int getAppId() {
		return APP_ID == 18 ? 1 : APP_ID;
	}

	/**
	 * 获取appid
	 * 
	 * @return
	 */
	public static int getInitialAppId() {
		return APP_ID;
	}

	/**
	 * 设置应用id以及debug环境
	 * 
	 * @param appId
	 *            1.商周；2.优家
	 */
	public static void setAppId(int appId, int debug) {
		if (appId != -1)
			setAppId(appId);
		if (debug != -1)
			setDebug(debug);
		UrlMaker.setUserModelUrl();
	}

	private static void setAppId(int appId) {
		APP_ID = appId;
	}

	private static void setDebug(int debug) {
		IS_DEBUG = debug;
	}

}
