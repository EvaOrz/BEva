package cn.com.modernmedia.api;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

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
			if (ConstData.getAppId() == 1) {
				HOST = "http://content.cdn.bb.bbwc.cn";
			} else if (ConstData.getAppId() == 2) {
				HOST = "http://content.cdn.imlady.bbwc.cn";
			} else if (ConstData.getAppId() == 20) {
				HOST = "http://content.cdn.iweekly.bbwc.cn";
			} else {
				HOST = "http://content.cdn.bbwc.cn";
			}
		} else if (ConstData.IS_DEBUG == 1) {
			HOST = "http://content.test.bbwc.cn";
		} else if (ConstData.IS_DEBUG == 2) {
			HOST = "http://develop.cname.bbwc.cn/dev";
		} else if (ConstData.IS_DEBUG == 4) {
			HOST = "http://develop.cname.bbwc.cn/zhanglei";
		} else if (ConstData.IS_DEBUG == 5) {
			HOST = "http://cms.bbwc.cn/dev";// 预览版
		} else if (ConstData.IS_DEBUG == 6) {
			HOST = "http://cms.bbwc.cn/editor";// 编辑版
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
	 * 获取往期列表
	 * 
	 * @param page
	 *            页数
	 * @return
	 */
	protected static String getIssueList(int page) {
		return MODEL_URL + "/interface/content-getissuelist-"
				+ ConstData.DEVICE_TYPE + "-" + ConstData.DATA_TYPE + "-"
				+ page + getEnd();
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
	 * 独立栏目列表
	 * 
	 * @return
	 */
	public static String getSoloCatList() {
		return getCatList("0");
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
		String time = issueId.equals("0") ? "-" + columnUpdateTime : "_"
				+ columnUpdateTime;
		return MODEL_URL + "/issue_" + issueId
				+ "/category/content-getcatindex-" + ConstData.DEVICE_TYPE
				+ "-" + ConstData.DATA_TYPE + "-" + issueId + "-" + columnId
				+ time + getEnd();
	}

	/**
	 * 独立栏目index
	 * 
	 * @param catId
	 * 
	 * @param fromOffset
	 *            (from_0)取from前的所有数据(最新数据)
	 * 
	 * @param toOffset
	 *            (0_to)取to后的所有数据(旧数据) ......0_0代表取全部数据
	 * @param columnUpdateTime
	 *            上次更新时间（从独立栏目getCatList里面获取）
	 * 
	 * @return
	 */
	protected static String getSoloCatIndex(String catId, String fromOffset,
			String toOffset, String columnUpdateTime) {
		return getCatIndex("0", fromOffset + "-" + toOffset + "_"
				+ columnUpdateTime, catId);
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
		String time = issueId.equals("0") ? "-" + articleUpdateTime : "_"
				+ articleUpdateTime;
		return MODEL_URL + "/issue_" + issueId
				+ "/interface/content-getarticlelist-" + ConstData.DEVICE_TYPE
				+ "-" + ConstData.DATA_TYPE + "-" + issueId + time + getEnd();
	}

	/**
	 * 独立栏目文章列表
	 * 
	 * @param fromOffset
	 *            (from_0)取from前的所有数据(最新数据)
	 * 
	 * @param toOffset
	 *            (0_to)取to后的所有数据(旧数据) ......0_0代表取全部数据
	 * @param articleUpdateTime
	 *            上次更新时间（从独立栏目getCatList里面获取）
	 * @return
	 */
	protected static String getSoloArticleList(String catId, String fromOffset,
			String toOffset, String articleUpdateTime) {
		return getArticleList("0", catId + "-" + fromOffset + "-" + toOffset
				+ "_" + articleUpdateTime);
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
		String time = TextUtils.isEmpty(articleUpdateTime) ? "" : "_"
				+ articleUpdateTime;
		return MODEL_URL + "/issue_" + issueId + "/articles/" + articleId
				+ "/getarticle-" + ConstData.DEVICE_TYPE + "-"
				+ ConstData.DATA_TYPE + "-" + issueId + "-" + columnId + "-"
				+ articleId + "-1-1" + time + ".html";
	}

	/**
	 * 获取文章详情
	 * 
	 * @param detail
	 * @return
	 */
	protected static String getSoloArticleById(FavoriteItem detail) {
		if (detail == null)
			return "";
		return getArticleById("0", detail.getCatid() + "", detail.getId() + "",
				detail.getUpdateTime());
	}

	/**
	 * 统计装机量
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
	 * 判断版本号，不一致就升级
	 * 
	 * @return
	 */
	protected static String checkVersion() {
		// 本地：http://develop.cname.bbwc.cn/jinxin
		return "http://android.bbwc.cn/interface/?m=app&a=version&appid="
				+ ConstData.getInitialAppId() + "&src="
				+ CommonApplication.CHANNEL;
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

	protected static String getAdvList() {
		String host = ConstData.IS_DEBUG == 0 ? "http://adver.cdn.bbwc.cn"
				: "http://adver.test.bbwc.cn";
		return host + "/adv/list/" + ConstData.getInitialAppId() + "-"
				+ ConstData.DEVICE_TYPE + "-" + ConstData.DATA_TYPE + ".html";
	}

	/**
	 * iWeekly入版广告
	 * 
	 * @return
	 */
	protected static String getWeeklyInApp() {
		return "http://beta.iweek.ly/api/json/intro";
	}
}
