package cn.com.modernmedia.api;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.ConstData;

/**
 * 获取借口信息
 * 
 * @author ZhuQiao
 * 
 */
public class UrlMaker {
	/** Host信息 **/
	private static String HOST;
	/** 基础URL信息 **/
	private static String MODEL_URL = "";

	public static void setMODEL_URL() {
		if (ConstData.IS_DEBUG == 0) {
			HOST = "http://content.cdn.bb.bbwc.cn";
		} else if (ConstData.IS_DEBUG == 1) {
			HOST = "http://content.test.bbwc.cn";
		} else if (ConstData.IS_DEBUG == 2) {
			HOST = "http://10.0.7.184/dev";
		}
		MODEL_URL = HOST + "/v" + ConstData.API_VERSION + "/app"
				+ ConstData.APP_ID;
	}

	private static String getEnd() {
		return ConstData.IS_DEBUG == 2 ? ".html" : ".api";
	}

	/**
	 * 获取期信息URL
	 * 
	 * @param page
	 *            如果不为0，代表获取期列表
	 * @param issueId
	 *            如果不为空，代表获取某一期信息
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
	 * 获取栏目列表
	 * 
	 * @param issueId
	 *            期id
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
	 * 获取左侧列表
	 * 
	 * @param issueId
	 *            期id
	 * @param articleUpdateTime
	 *            更新时间
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
	 * 获取今日焦点页
	 * 
	 * @param issueId
	 *            期id
	 * @param columnUpdateTime
	 *            更新时间
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
	 * 普通栏目首页
	 * 
	 * @param issueId
	 *            期id
	 * @param columnUpdateTime
	 *            更新时间
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
	 * 栏目图片列表
	 * 
	 * @param issueId
	 *            期ID
	 * @return
	 */
	protected static String getCatimgList(String issueId) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-getcatimglist-" + issueId + getEnd();
	}

	/**
	 * 公共资源列表
	 * 
	 * @return
	 */
	protected static String manifest() {
		return MODEL_URL + "/interface/content-manifest-"
				+ ConstData.DEVICE_TYPE + getEnd();
	}

	/**
	 * 全部文章列表
	 * 
	 * @param issueId
	 *            期id
	 * @param articleUpdateTime
	 *            更新时间
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
	 * 文章图片列表
	 * 
	 * @param issueId
	 *            期id
	 * @return
	 */
	protected static String getImgList(String issueId) {
		if (TextUtils.isEmpty(issueId))
			return "";
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-getimglist-" + issueId + getEnd();
	}

	/**
	 * 获取文章详情
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
	 * 统计装机量
	 * 
	 * @return
	 */
	protected static String download(Context context) {
		CommonApplication app = (CommonApplication) context
				.getApplicationContext();
		return HOST + "/interface/index.php?m=down&res=" + ConstData.CHANNEL
				+ "&uuid=" + app.getMyUUID() + "&appid=" + ConstData.APP_ID
				+ "&version=" + ConstData.VERSION;
	}

	/**
	 * 判断版本号，不一致就升级
	 * 
	 * @return
	 */
	protected static String checkVersion() {
		String host = ConstData.IS_DEBUG != 0 ? "http://cms.bbwc.cn/mmuser/checkversion.php?src="
				: "http://android.bbwc.cn/checkversion.php?src=";
		return host + ConstData.CHANNEL + "&appid=" + ConstData.APP_ID;
	}

	/**
	 * 分享
	 * 
	 * @param issueId
	 * @param catid
	 * @param articleid
	 * @param articleUpdateTime
	 * @param shareType
	 *            01:邮件,02:微博,03:微信,04:短信,05:LinkedIn,06:evernote
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
}
