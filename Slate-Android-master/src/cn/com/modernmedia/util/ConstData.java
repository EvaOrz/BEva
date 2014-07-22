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
	 * ��ǰ���� 0:���ϣ�1�����ԣ�2������
	 **/
	public static int IS_DEBUG = 0;
	/** �ӿڰ汾�� **/
	public static final String API_VERSION = "4";
	/**
	 * Ӧ��id bloomberg:1 modernlady:2 icollection:3 turningpoints:5 adv:6
	 * onewaystreet:10 zhihu:11 tanc:12 itv:13 life:14 car:15 lohas:16
	 * finance:17 bloomberg�����:18 iweekly:19
	 **/
	private static int APP_ID = 1;
	/** �豸�� **/
	public static String DEVICE_TYPE = "10";
	/** �������ͣ�1 protobuf��ʽ 2 json��ʽ�� **/
	public static final String DATA_TYPE = "2";
	/** ���ӳ�ʱʱ�� **/
	public static final int CONNECT_TIMEOUT = 10 * 1000;
	/** ��ȡ��ʱʱ�� **/
	public static final int READ_TIMEOUT = 10 * 1000;
	/** ��ȡ����buffer��С **/
	public static final int BUFFERSIZE = 1024;
	/** toast��ʾʱ�� **/
	public static final int TOAST_LENGTH = 1000;
	/** �汾�� **/
	public static final int VERSION = 132;
	/** �ͻ������� **/
	public static String APP_NAME = "";
	/** splashͣ��ʱ�� **/
	public static int SPLASH_DELAY_TIME = 1000;
	/** ͼƬ�����ַ **/
	public static String DEFAULT_IMAGE_PATH;
	public static String DEFAULT_DATA_PATH;
	public static String DEFAULT_APK_PATH;
	public static final String PUSH_ACTION = "cn.com.modernmedia.businessweek.service.UPDATE_STATUS";
	public static String FLURRYAGENT_KEY;// ͳ��
	public static final String CRASH_NAME = "crash";
	public static final String API_LOG = "api_log";

	/**
	 * ���ڷ����ͼ����ܶ��߼���һ���ģ����������ͬ�߼���ֻ��appid��ͬ��ͨ�����������ȡappid
	 * 
	 * @return
	 */
	public static int getAppId() {
		return APP_ID == 18 ? 1 : APP_ID;
	}

	/**
	 * ��ȡappid
	 * 
	 * @return
	 */
	public static int getInitialAppId() {
		return APP_ID;
	}

	/**
	 * ����Ӧ��id�Լ�debug����
	 * 
	 * @param appId
	 *            1.���ܣ�2.�ż�
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
	 * Flurryͳ��key
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
		}
		return "";
	}

	/**
	 * ����Ӧ��id
	 * 
	 * @param appId
	 * @return
	 */
	public static String getAppName() {
		if (APP_ID == 1) {
			APP_NAME = "����ҵ�ܿ�";
			return "BussinessWeek";
		} else if (APP_ID == 2) {
			APP_NAME = "�żһ���";
			return "ModernLady";
		} else if (APP_ID == 20) {
			return "iWeekly";
		} else if (APP_ID == 16) {
			APP_NAME = "�ֻ�";
			return "lohas";
		} else if (APP_ID == 18) {
			APP_NAME = "���̘I�L��";
			return "BussinessWeek";
		}
		return "";
	}

	/**
	 * ����Ӧ������
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
	 * issue�ļ�����(ֻ�����粻�õ�����²Ŵӱ���ȡ)
	 * 
	 * @return
	 */
	public static String getIssueFileName() {
		return "getissue_";
	}

	/**
	 * catlist�ļ���
	 * 
	 * @return
	 */
	public static String getCatListFileName() {
		return "cartlist_";
	}

	/**
	 * index�ļ���
	 * 
	 * @return
	 */
	public static String getIndexFileName() {
		return "index_";
	}

	/**
	 * catindex�ļ���
	 * 
	 * @param columnId
	 * @return
	 */
	public static String getCatIndexFileName(String columnId) {
		return "catindex_" + columnId;
	}

	/**
	 * ��ȡarticlelist�ļ���
	 * 
	 * @return
	 */
	public static String getArticleListFileName() {
		return "articlelist_";
	}

	/**
	 * ��ȡͼ�������ļ���
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
	 * ��ȡ������Ϣ�ļ���
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
	 * ������־�ļ���
	 * 
	 * @return
	 */
	public static String getCrashLogFilename() {
		return CRASH_NAME;
	}

	public static String getLastestArticleIdFileName() {
		return "lasetest_entry_ids";
	}
}
