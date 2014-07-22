package cn.com.modernmedia.util;

import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.UrlMaker;

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
	/**
	 * 应用id bloomberg:1 modernlady:2 icollection:3 turningpoints:5 adv:6
	 * onewaystreet:10 zhihu:11 tanc:12 itv:13 life:14 car:15 lohas:16
	 * finance:17 bloomberg繁体版:18 iweekly:20
	 **/
	private static int APP_ID = 1;
	/** 设备号 **/
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
	public static final int VERSION = 150;
	/** 客户端名称 **/
	public static String APP_NAME = "";
	/** splash停留时间 **/
	public static int SPLASH_DELAY_TIME = 1000;
	/** 图片保存地址 **/
	public static String DEFAULT_IMAGE_PATH;
	public static String DEFAULT_DATA_PATH;
	public static String DEFAULT_APK_PATH;
	public static String DEFAULT_PACKAGE_PATH;
	public static final String PUSH_ACTION = "cn.com.modernmedia.businessweek.service.UPDATE_STATUS";
	public static final String CRASH_NAME = "crash";
	/**
	 * 没有成功同步到服务器上时使用当前uid,为了兼顾以前没有uid字段的数据，默认为0
	 */
	public static final String UN_UPLOAD_UID = "0";

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
		initDefaultValue();
		UrlMaker.setMODEL_URL();
	}

	private static void setAppId(int appId) {
		APP_ID = appId;
	}

	private static void setDebug(int debug) {
		IS_DEBUG = debug;
	}

	/**
	 * Flurry统计key
	 * 
	 * @return
	 */
	public static String getFlurryApiKey() {
		if (IS_DEBUG != 0)
			return "";
		if (APP_ID == 1 || APP_ID == 18) {
			return "KHSTGVVGP7422NSW4TM4";
		} else if (APP_ID == 2) {
			return "6T4YCHYM8PK6RVS858F4";
		} else if (APP_ID == 20) {
			return "9659V6KFBT68JJXDMDM2";
		} else if (APP_ID == 12) {
			return "2XYF5NM5RNYJ3QQRJ8MJ";
		}
		return "";
	}

	/**
	 * 设置应用id
	 * 
	 * @param appId
	 * @return
	 */
	public static String getAppName() {
		if (APP_ID == 1) {
			APP_NAME = "彭博商业周刊";
			return "BussinessWeek";
		} else if (APP_ID == 2) {
			APP_NAME = "优家画报";
			return "ModernLady";
		} else if (APP_ID == 16) {
			APP_NAME = "乐活 LOHAS";
			return "lohas";
		} else if (APP_ID == 18) {
			APP_NAME = "彭博商業週刊";
		} else if (APP_ID == 20) {
			APP_NAME = "周末画报";
			return "iWeekly";
		} else if (APP_ID == 12) {
			APP_NAME = "艺术新闻";
			return "tanc";
		}
		return "";
	}

	/**
	 * 设置应用名称
	 * 
	 * @param name
	 */
	private static void initDefaultValue() {
		DEFAULT_IMAGE_PATH = "/" + getAppName() + "/imgs/";
		DEFAULT_DATA_PATH = "/" + getAppName() + "/datas/";
		DEFAULT_APK_PATH = "/" + getAppName() + "/apk/";
		DEFAULT_PACKAGE_PATH = "/" + getAppName() + "/package/";
	}

	/**
	 * issue文件名，(只有网络不好的情况下才从本地取)
	 * 
	 * @return
	 */
	public static String getIssueFileName() {
		return "getissue_";
	}

	/**
	 * 往期列表文件名(只有网络不好的情况下才从本地取,因为不好判断是否有些期被下了)
	 * 
	 * @return
	 */
	public static String getIssuelistFileName(int page) {
		return "issuelist_" + page;
	}

	/**
	 * catlist文件名
	 * 
	 * @return
	 */
	public static String getCatListFileName() {
		return "cartlist_";
	}

	/**
	 * 独立栏目catlist文件名
	 * 
	 * @return
	 */
	public static String getSoloCatListFileName() {
		return "solo_cartlist_";
	}

	/**
	 * index文件名
	 * 
	 * @return
	 */
	public static String getIndexFileName() {
		return "index_";
	}

	/**
	 * catindex文件名
	 * 
	 * @param columnId
	 * @return
	 */
	public static String getCatIndexFileName(String columnId) {
		return "catindex_" + columnId;
	}

	/**
	 * 获取articlelist文件名
	 * 
	 * @return
	 */
	public static String getArticleListFileName() {
		return "articlelist_";
	}

	/**
	 * 获取图集文章文件名
	 * 
	 * @param issueId
	 * @param columnId
	 * @param articleId
	 * @return
	 */
	public static String getArticleFileName(String issueId, String columnId,
			String articleId) {
		return "article_" + issueId + "_" + columnId + "_" + articleId;
	}

	/**
	 * 获取分享信息文件名
	 * 
	 * @param issueId
	 * @param columnId
	 * @param articleId
	 * @return
	 */
	public static String getShareFileName(String issueId, String columnId,
			String articleId, String shareType) {
		return "share_" + issueId + "_" + columnId + "_" + articleId + "_"
				+ shareType;
	}

	public static String getCurrentIssueFold() {
		String issueFolder = "";
		if (CommonApplication.currentIssueId != -1) {
			issueFolder = CommonApplication.currentIssueId + "/";
		}
		if (TextUtils.isEmpty(issueFolder))
			issueFolder = DataHelper.getIssueId(CommonApplication.mContext)
					+ "/";
		return DEFAULT_DATA_PATH + issueFolder;
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
	 * @return
	 */
	public static String getWeeklyInApp(){
		return "iweekly_in_app";
	}
}
