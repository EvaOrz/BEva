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
	public static final String API_VERSION = "4";
	/** 应用id **/
	public static int APP_ID = 1;
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
	public static final int VERSION = 120;
	/** 客户端名称 **/
	public static String APP_NAME = "";
	/** splash停留时间 **/
	public static int SPLASH_DELAY_TIME = 1000;
	/** 图片保存地址 **/
	public static String DEFAULT_IMAGE_PATH;
	public static String DEFAULT_DATA_PATH;
	public static String DEFAULT_APK_PATH;
	public static final String PUSH_ACTION = "cn.com.modernmedia.businessweek.service.UPDATE_STATUS";
	public static String FLURRYAGENT_KEY;// 统计
	public static final String CRASH_NAME = "crash";

	/**
	 * 设置应用id以及debug环境
	 * 
	 * @param appId
	 *            1.商周；2.优家
	 * @param mode
	 *            当前环境 0:线上，1：测试，2：开发
	 */
	public static void setAppId(int appId, int mode) {
		APP_ID = appId;
		IS_DEBUG = mode;
		if (APP_ID == 20)
			DEVICE_TYPE = "20";
		initDefaultValue();
		UrlMaker.setMODEL_URL();
	}

	/**
	 * Flurry统计key
	 * 
	 * @return
	 */
	public static String getFlurryApiKey() {
		if (IS_DEBUG != 0)
			return "";
		if (APP_ID == 1) {
			return "KHSTGVVGP7422NSW4TM4";
		} else if (APP_ID == 2) {
			return "6T4YCHYM8PK6RVS858F4";
		}
		return "";
	}

	/**
	 * 设置应用id
	 * 
	 * @param appId
	 * @return
	 */
	private static String getAppName() {
		if (APP_ID == 1) {
			APP_NAME = "彭博商业周刊";
			return "BussinessWeek";
		} else if (APP_ID == 2) {
			APP_NAME = "优家画报";
			return "ModernLady";
		} else if (APP_ID == 20) {
			return "iWeekly";
		} else if (APP_ID == 16) {
			APP_NAME = "乐活";
			return "lohas";
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
		if (getAppName().equals("BussinessWeek")) {
			FLURRYAGENT_KEY = "KHSTGVVGP7422NSW4TM4";
		}
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
	 * catlist文件名
	 * 
	 * @return
	 */
	public static String getCatListFileName() {
		return "cartlist_";
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
		if (CommonApplication.currentIssue != null) {
			issueFolder = CommonApplication.currentIssue.getId() + "/";
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
}
