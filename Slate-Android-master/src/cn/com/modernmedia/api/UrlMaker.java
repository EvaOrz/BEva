package cn.com.modernmedia.api;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.ConstData;

/**
 * ��ȡ�����Ϣ
 * 
 * @author ZhuQiao
 * 
 */
public class UrlMaker {
	/** Host��Ϣ **/
	private static String HOST;
	/** ����URL��Ϣ **/
	private static String MODEL_URL = "";

	public static void setMODEL_URL() {
		if (ConstData.IS_DEBUG == 0) {
			HOST = "http://content.cdn.bb.bbwc.cn";
		} else if (ConstData.IS_DEBUG == 1) {
			HOST = "http://content.test.bbwc.cn";
		} else if (ConstData.IS_DEBUG == 2) {
			HOST = "http://10.0.7.184/dev";
		} else if (ConstData.IS_DEBUG == 4) {
			HOST = "http://10.0.7.184/zhanglei";
		} else if (ConstData.IS_DEBUG == 5) {
			HOST = "http://cms.bbwc.cn/dev";// Ԥ����
		} else if (ConstData.IS_DEBUG == 6) {
			HOST = "http://cms.bbwc.cn/editor";// �༭��
		} else if (ConstData.IS_DEBUG == 7) {
			HOST = "http://content.test.bbwc.cn/zhanglei";
		}
		MODEL_URL = HOST + "/v" + ConstData.API_VERSION + "/app"
				+ ConstData.getInitialAppId();
	}

	private static String getEnd() {
		return ConstData.IS_DEBUG == 2 || ConstData.IS_DEBUG == 4
				|| ConstData.IS_DEBUG == 6 || ConstData.IS_DEBUG == 7 ? ".html"
				: ".api";
	}

	/**
	 * ��ȡ����ϢURL
	 * 
	 * @param page
	 *            �����Ϊ0�������ȡ���б�
	 * @param issueId
	 *            �����Ϊ�գ������ȡĳһ����Ϣ
	 * @return
	 */
	protected static String getIssueUrl(int page, String issueId) {
		String url = MODEL_URL + "/interface/content-getissue-"
				+ ConstData.DEVICE_TYPE + "-" + ConstData.DATA_TYPE;
		if (page != 0) {
			url += "-" + page;
		} else if (!TextUtils.isEmpty(issueId)) {
			url += "-" + issueId;
		}
		url += getEnd();
		return url;
	}

	/**
	 * ��ȡ��Ŀ�б�
	 * 
	 * @param issueId
	 *            ��id
	 * @return
	 */
	protected static String getCatList(String issueId) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-getcatlist-" + ConstData.DEVICE_TYPE
				+ "-" + ConstData.DATA_TYPE + "-" + issueId + getEnd();
	}

	/**
	 * ��ȡ����б�
	 * 
	 * @param issueId
	 *            ��id
	 * @param articleUpdateTime
	 *            ����ʱ��
	 * @return
	 */
	protected static String getLeftMenu(String issueId, String articleUpdateTime) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-getleftmenu-" + ConstData.DEVICE_TYPE
				+ "-" + ConstData.DATA_TYPE + "-" + issueId + "_"
				+ articleUpdateTime + getEnd();
	}

	/**
	 * ��ȡ���ս���ҳ
	 * 
	 * @param issueId
	 *            ��id
	 * @param columnUpdateTime
	 *            ����ʱ��
	 * @return
	 */
	protected static String getIndex(String issueId, String columnUpdateTime) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId + "/interface/content-getindex-"
				+ ConstData.DEVICE_TYPE + "-" + ConstData.DATA_TYPE + "-"
				+ issueId + "_" + columnUpdateTime + getEnd();
	}

	/**
	 * ��ͨ��Ŀ��ҳ
	 * 
	 * @param issueId
	 *            ��id
	 * @param columnUpdateTime
	 *            ����ʱ��
	 * @param columnId
	 *            column id
	 * @return
	 */
	protected static String getCatIndex(String issueId,
			String columnUpdateTime, String columnId) {
		if (TextUtils.isEmpty(issueId) || TextUtils.isEmpty(columnId))
			return "";
		return MODEL_URL + "/issue_" + issueId
				+ "/category/content-getcatindex-" + ConstData.DEVICE_TYPE
				+ "-" + ConstData.DATA_TYPE + "-" + issueId + "-" + columnId
				+ "_" + columnUpdateTime + getEnd();
	}

	/**
	 * ��ĿͼƬ�б�
	 * 
	 * @param issueId
	 *            ��ID
	 * @return
	 */
	protected static String getCatimgList(String issueId) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-getcatimglist-" + issueId + getEnd();
	}

	/**
	 * ������Դ�б�
	 * 
	 * @return
	 */
	protected static String manifest() {
		return MODEL_URL + "/interface/content-manifest-"
				+ ConstData.DEVICE_TYPE + getEnd();
	}

	/**
	 * ȫ�������б�
	 * 
	 * @param issueId
	 *            ��id
	 * @param articleUpdateTime
	 *            ����ʱ��
	 * @return
	 */
	protected static String getArticleList(String issueId,
			String articleUpdateTime) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-getarticlelist-" + ConstData.DEVICE_TYPE
				+ "-" + ConstData.DATA_TYPE + "-" + issueId + "_"
				+ articleUpdateTime + getEnd();
	}

	/**
	 * ����ͼƬ�б�
	 * 
	 * @param issueId
	 *            ��id
	 * @return
	 */
	protected static String getImgList(String issueId) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-getimglist-" + issueId + getEnd();
	}

	/**
	 * ��ȡ��������
	 * 
	 * @param issueId
	 * @param columnId
	 * @param articleId
	 * @param articleUpdateTime
	 * @return
	 */
	protected static String getArticleById(String issueId, String columnId,
			String articleId, String articleUpdateTime) {
		if (TextUtils.isEmpty(articleId))
			return "";
		return MODEL_URL + "/issue_" + issueId + "/articles/" + articleId
				+ "/getarticle-" + ConstData.DEVICE_TYPE + "-"
				+ ConstData.DATA_TYPE + "-" + issueId + "-" + columnId + "-"
				+ articleId + "-1-1_" + articleUpdateTime + ".html";
	}

	/**
	 * ͳ��װ����
	 * 
	 * @return
	 */
	protected static String download(Context context) {
		CommonApplication app = (CommonApplication) context
				.getApplicationContext();
		return HOST + "/interface/index.php?m=down&res="
				+ CommonApplication.CHANNEL + "&uuid=" + app.getMyUUID()
				+ "&appid=" + ConstData.getInitialAppId() + "&version="
				+ ConstData.VERSION;
	}

	/**
	 * �жϰ汾�ţ���һ�¾�����
	 * 
	 * @return
	 */
	protected static String checkVersion() {
		String host = ConstData.IS_DEBUG != 0 ? "http://cms.bbwc.cn/mmuser/checkversion.php?src="
				: "http://android.bbwc.cn/checkversion.php?src=";
		return host + CommonApplication.CHANNEL + "&appid="
				+ ConstData.getInitialAppId();
	}

	/**
	 * ����
	 * 
	 * @param issueId
	 * @param catid
	 * @param articleid
	 * @param articleUpdateTime
	 * @param shareType
	 *            01:�ʼ�,02:΢��,03:΢��,04:����,05:LinkedIn,06:evernote
	 * @return
	 */
	protected static String share(String issueId, String catid,
			String articleid, String articleUpdateTime, String shareType) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId + "/articles/" + articleid
				+ "/share-" + ConstData.DEVICE_TYPE + "-" + ConstData.DATA_TYPE
				+ "-" + issueId + "-" + catid + "-" + articleid + "-1-"
				+ shareType + "_" + articleUpdateTime + ".html";
	}

	protected static String getWeather(double longitude, double latitude) {
		return "http://weather.iweek.ly/get_weather?longitude=" + longitude
				+ "&latitude=" + latitude;
	}

	// http://10.0.7.184/zhanglei/v4/app20/issue_200/interface/content-latest_entry_id-20-2-200.html
	protected static String getLastestArticleId(int issueId) {
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-latest_entry_id-" + ConstData.getAppId()
				+ "-" + ConstData.DATA_TYPE + "-" + issueId + getEnd();
	}

}
