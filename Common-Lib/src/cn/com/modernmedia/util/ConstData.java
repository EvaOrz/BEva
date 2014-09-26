package cn.com.modernmedia.util;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmediaslate.SlateApplication;

/**
 * 
 * @author ZhuQiao
 * 
 */
public class ConstData {
	/**
	 * 当前环境 0:线上，1：测试，2：开发
	 **/
	public static int IS_DEBUG = 0;
	/** 接口版本号 **/
	public static String API_VERSION = "5";
	/** 设备号 **/
	// test xiaomihezi=9
	public static String DEVICE_TYPE = "10";
	/** 数据类型（1 protobuf格式 2 json格式） **/
	public static final String DATA_TYPE = "2";
	/** 连接超时时间 **/
	public static final int CONNECT_TIMEOUT = 10 * 1000;
	/** 读取超时时间 **/
	public static final int READ_TIMEOUT = 10 * 1000;
	/** 读取数据buffer大小 **/
	public static final int BUFFERSIZE = 1024;
	/** toast显示时间 **/
	public static final int TOAST_LENGTH = 1000;
	/** 版本号 **/
	public static final int VERSION = 162;
	/** splash停留时间 **/
	public static int SPLASH_DELAY_TIME = 1000;
	/** 文件保存地址 **/
	public static String DEFAULT_DATA_PATH;
	public static String DEFAULT_APK_PATH;
	public static String DEFAULT_PACKAGE_PATH;
	public static String DEFAULT_TEMPLATE_PATH;
	public static final String PUSH_ACTION = "cn.com.modernmedia.businessweek.service.UPDATE_STATUS";
	public static final String CRASH_NAME = "crash";

	public enum APP_TYPE {
		BUSINESS, IWEEKLY, OTHERS;
	}

	/**
	 * 由于繁体版和简体版很多逻辑是一样的，如果碰到相同逻辑，只有appid不同，通过这个方法获取appid
	 * 
	 * @return
	 */
	public static int getAppId() {
		return CommonApplication.APP_ID == 18 ? 1 : CommonApplication.APP_ID;
	}

	/**
	 * 获取appid
	 * 
	 * @return
	 */
	public static int getInitialAppId() {
		return CommonApplication.APP_ID;
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
		initDefaultValue();
		UrlMaker.setMODEL_URL();
	}

	private static void setAppId(int appId) {
		CommonApplication.APP_ID = appId;
	}

	private static void setDebug(int debug) {
		IS_DEBUG = debug;
	}

	public static void setDeviceType(String deviceType) {
		DEVICE_TYPE = deviceType;
	}

	/**
	 * 设置文件存储路径
	 * 
	 * @param name
	 */
	private static void initDefaultValue() {
		String fileName = CommonApplication.mConfig.getCache_file_name();
		SlateApplication.DEFAULT_IMAGE_PATH = "/" + fileName + "/imgs/";
		DEFAULT_DATA_PATH = "/" + fileName + "/datas/";
		DEFAULT_APK_PATH = "/" + fileName + "/apk/";
		DEFAULT_PACKAGE_PATH = "/" + fileName + "/package/";
		DEFAULT_TEMPLATE_PATH = "/" + fileName + "/templates/";
	}

	/**
	 * 崩溃日志文件名
	 * 
	 * @return
	 */
	public static String getCrashLogFilename() {
		return CRASH_NAME;
	}

	public static String getLastestArticleIdFileName() {
		return "lasetest_entry_ids";
	}

	/**
	 * 广告列表
	 * 
	 * @return
	 */
	public static String getAdvList() {
		return "adv_list";
	}

	/**
	 * iWeekly入版广告
	 * 
	 * @return
	 */
	public static String getWeeklyInApp() {
		return "iweekly_in_app";
	}

	/**
	 * 是否是iweekly新闻栏目
	 * 
	 * @return
	 */
	public static boolean isWeeklyNews(int catId) {
		return CommonApplication.APP_ID == 20 && catId == 192;
	}

	/**
	 * 获取文章详情文件名
	 * 
	 * @param articleId
	 *            文章统一标识，可以传多个articleid
	 * @return
	 */
	public static String getArticleDetailFileName(String articleId) {
		return "article_detail_" + articleId;
	}

	/**
	 * <<<<<<< HEAD 获取往期文章列表的缓存名字
	 * 
	 * @param tagName
	 *            期tag
	 * @return
	 */
	public static String getLastArticlesFileName(String tagName) {
		return "last_articles_" + tagName;
	}

	/**
	 * 获取往期列表的缓存名字
	 * 
	 * @param tagName
	 *            期tag
	 * @return
	 */
	public static String getLastIssueListFileName(String tagName) {
		return "last_issuelist_" + tagName;
	}

	/**
	 * 是否是goodlife
	 * 
	 * @return
	 */
	public static boolean isGoodLife() {
		return CommonApplication.APP_ID == 32 || CommonApplication.APP_ID == 33;
	}
}
