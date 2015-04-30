package cn.com.modernmedia.api;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
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
	private static String SLATE_URL = "";
	private static String USER_MODEL_URL = ""; // 与用户相关
	private static String SLATE_BASE_URL = "";

	public static void setMODEL_URL() {
		if (ConstData.IS_DEBUG == 0) {
			if (ConstData.getAppId() == 1) {
				HOST = "http://content.cdn.bb.bbwc.cn";
			} else if (ConstData.getAppId() == 2) {
				HOST = "http://content.cdn.imlady.bbwc.cn";
			} else if (ConstData.getAppId() == 20) {
				HOST = "http://content.cdn.iweekly.bbwc.cn";
			} else if (ConstData.getAppId() == 10) {
				HOST = "http://content.cdn.onewaystreet.cn";
			} else {
				HOST = "http://content.cdn.bbwc.cn";
			}
			USER_MODEL_URL = "http://user.bbwc.cn/interface/index.php";
		} else if (ConstData.IS_DEBUG == 1) {
			HOST = "http://content.test.bbwc.cn";
			USER_MODEL_URL = "http://user.test.bbwc.cn/interface/index.php";
		} else if (ConstData.IS_DEBUG == 2) {
			HOST = "http://develop.cname.bbwc.cn/dev";
			SLATE_BASE_URL = "http://develop.cname.bbwc.cn/slateInterface";
			USER_MODEL_URL = "http://develop.cname.bbwc.cn/mmuser/interface/index.php";
		} else if (ConstData.IS_DEBUG == 4) {
			HOST = "http://develop.cname.bbwc.cn/zhanglei";
		} else if (ConstData.IS_DEBUG == 5) {
			HOST = "http://cms.bbwc.cn/dev";// 预览版
		} else if (ConstData.IS_DEBUG == 6) {
			HOST = "http://cms.bbwc.cn/editor";// 编辑版
		} else if (ConstData.IS_DEBUG == 7) {
			HOST = "http://content.test.bbwc.cn/zhanglei";
		} else if (ConstData.IS_DEBUG == 8) {
			HOST = "http://content.editor.bbwc.cn";
		}
		if (SLATE_BASE_URL == "")
			SLATE_BASE_URL = HOST + "/slateInterface";
		MODEL_URL = HOST + "/v" + ConstData.API_VERSION + "/app"
				+ ConstData.getInitialAppId();

		SLATE_URL = SLATE_BASE_URL + "/v" + ConstData.API_VERSION + "/app_"
				+ ConstData.getInitialAppId() + "/android";
	}

	private static String getEnd() {
		return ConstData.IS_DEBUG == 1 || ConstData.IS_DEBUG == 2
				|| ConstData.IS_DEBUG == 4 || ConstData.IS_DEBUG == 6
				|| ConstData.IS_DEBUG == 7 ? ".html" : ".api";
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
	 * 统计装机量
	 * 
	 * @return
	 */
	protected static String download(Context context) {
		return "http://android.bbwc.cn/interface/index.php?m=down&res="
				+ CommonApplication.CHANNEL + "&uuid="
				+ CommonApplication.getMyUUID() + "&appid="
				+ ConstData.getInitialAppId() + "&version=" + ConstData.VERSION;
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

	protected static String getWeather(double longitude, double latitude) {
		return "http://weather.iweek.ly/get_weather?longitude=" + longitude
				+ "&latitude=" + latitude;
	}

	/**
	 * 获取iweekly最新文章
	 * 
	 * @param tagName
	 * @return
	 */
	protected static String getWeeklyLatestArticleId(String tagName) {
		String str = TextUtils.isEmpty(tagName) ? "/tag" : "/tag/" + tagName;
		String url = SLATE_BASE_URL + "/v" + ConstData.API_VERSION
				+ "/app_20/android";
		return url + str + "/newarticles";
	}

	protected static String getAdvList() {
		String host = "";
		if (ConstData.IS_DEBUG == 0) {
			host = "http://adver.cdn.bbwc.cn";
		} else if (ConstData.IS_DEBUG == 8) {
			host = "http://adver.editor.bbwc.cn";
		} else {
			host = "http://adver.develop.bbwc.cn";
		}
		String url = host + "/adv/v2/list/" + ConstData.getInitialAppId() + "-"
				+ ConstData.DEVICE_TYPE + "-" + ConstData.DATA_TYPE + ".html";
		if (!TextUtils.isEmpty(AppValue.appInfo.getAdvUpdateTime())) {
			return url + "?updatetime=" + AppValue.appInfo.getAdvUpdateTime();
		}
		return url;
	}

	/**
	 * iWeekly入版广告
	 * 
	 * @return
	 */
	// protected static String getWeeklyInApp() {
	// if (ConstData.IS_DEBUG == 0)
	// return "http://beta.iweek.ly/api/json/intro";
	// else
	// return "http://iw-cdn-test.iweek.ly/api/json/intro";
	// }

	/**
	 * 获取某tag信息URL
	 * 
	 * @param tagName
	 *            tag统一标识，可以传多个tagname,用逗号隔开
	 * @return
	 */
	public static String getTagInfo(String tagName) {
		return SLATE_URL + "/tag/" + tagName;
	}

	/**
	 * 获取tag子标签URL
	 * 
	 * @param tagName
	 *            tag统一标识，可以传多个tagname,用逗号隔开
	 * @param group
	 *            1.app类 2.issue类 3.column类 4.独立栏目 'all'.所有（默认值）
	 * @param top
	 *            用于分页，取下一页$top=最后一个标签的offset
	 * @param bottom
	 *            用于分页，取上一页$bottom=第一个标签的offset
	 * @return
	 */
	protected static String getTagChild(String parentTagName, String tagName,
			String group, String top, boolean isChild) {
		String url = SLATE_URL + "/tag";
		if (!TextUtils.isEmpty(parentTagName)) {
			url += "/" + parentTagName;
		}
		if (!TextUtils.isEmpty(tagName))
			url += "/" + tagName;
		url += isChild ? "/child" : "/tree";
		if (TextUtils.isEmpty(group) && TextUtils.isEmpty(top)) {
			if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
				url += "?";
				return addUpdatetime(url);
			}
			return url;
		}
		url += "?";
		if (!TextUtils.isEmpty(group)) {
			String and = url.endsWith("?") ? "" : "&";
			url += and + "group=" + group;
		}
		if (!TextUtils.isEmpty(top)) {
			String and = url.endsWith("?") ? "" : "&";
			url += and + "top=" + top;
		}
		if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
			url = addUpdatetime(url);
		}
		return url;
	}

	/**
	 * 获取栏目列表
	 * 
	 * @param url
	 * @return
	 */
	public static String getCatList(String tagName) {
		String url = SLATE_URL + "/tag";
		if (!TextUtils.isEmpty(tagName)) {
			url += "/" + tagName;
		}
		url += "/subscribelist";
		if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
			url += "?";
			return addUpdatetime(url);
		}
		return url;
	}

	public static String addUpdatetime(String url) {
		String and = url.endsWith("?") ? "" : "&";
		url += and + "updatetime=" + AppValue.appInfo.getUpdatetime();
		return url;
	}

	/**
	 * 获取tag对应的文章URl
	 * 
	 * @param tagName
	 *            tag统一标识，可以传多个tagname,用逗号隔开, 当前tagname只支持栏目ID且该栏目没有child
	 * @param top
	 *            用于分页，取下一页$top=最后一个标签的offset
	 * @param bottom
	 *            用于分页，取上一页$bottom=第一个标签的offset
	 * @return
	 */
	protected static String getTagCatIndex(TagInfo info, String top,
			String limited) {
		if (info == null)
			return "";
		String url = SLATE_URL + "/tag/";
		if (TextUtils.equals(limited, "5")) {
			url += info.getTagName() + "/";
			url += AppValue.ensubscriptColumnList.getSubscriptTagMergeName();
		} else {
			url += info.getTagName();
			String merge = info.getMergeName(true);// 子栏目集合
			if (!TextUtils.isEmpty(merge)) {
				url += "/" + merge;
			}
		}
		url += "/tagindex";
		if (!TextUtils.isEmpty(top)) {
			url += "?top=" + top;
		}
		if (!TextUtils.isEmpty(limited)) {
			url += url.contains("?") ? "&" : "?";
			url += "limited=" + limited;
		}
		url += url.contains("?") ? "&" : "?";
		if (TextUtils.equals(limited, "5")
				&& !TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
			url += "updatetime=" + AppValue.appInfo.getUpdatetime();
		} else {
			url += "updatetime=" + info.getColoumnupdatetime();
		}
		return url;
	}

	/**
	 * 获取tag对应的文章URl
	 * 
	 * @param info
	 *            tag信息
	 * @param top
	 *            用于分页，取下一页$top=最后一个标签的offset
	 * @param bottom
	 *            用于分页，取上一页$bottom=第一个标签的offset
	 * @return
	 */
	protected static String getArticlesByTag(TagInfo info, String top,
			String limited) {
		if (info == null)
			return "";
		String url = SLATE_URL + "/tag/";
		if (TextUtils.equals(limited, "5")) {
			url += info.getTagName() + "/";
			url += AppValue.ensubscriptColumnList.getSubscriptTagMergeName();
		} else {
			url += info.getTagName();
			String merge = info.getMergeName(true);// 子栏目集合
			if (!TextUtils.isEmpty(merge)) {
				url += "/" + merge;
			}
		}
		url += "/articlelist";
		if (!TextUtils.isEmpty(top)) {
			url += "?top=" + top;
		}
		if (!TextUtils.isEmpty(limited)) {
			url += url.contains("?") ? "&" : "?";
			url += "limited=" + limited;
		}
		url += url.contains("?") ? "&" : "?";
		if (TextUtils.equals(limited, "5")
				&& !TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
			url += "updatetime=" + AppValue.appInfo.getUpdatetime();
		} else {
			url += "updatetime=" + info.getArticleupdatetime();
		}
		return url;
	}

	/**
	 * 获取tag对应的文章URl
	 * 
	 * @param tagName
	 *            tag统一标识，可以传多个tagname,用逗号隔开, 当前tagname只支持栏目ID且该栏目没有child
	 * @param top
	 *            用于分页，取下一页$top=最后一个标签的offset
	 * @return
	 */
	protected static String getArticlesByTag(String tagname, String top) {
		String url = SLATE_URL + "/tag/" + tagname + "/articlelist";
		if (!TextUtils.isEmpty(top)) {
			url += "?top=" + top;
		}
		return url;
	}

	/**
	 * 获取往期tag对应的文章URl
	 * 
	 * @param tagName
	 *            tag统一标识，可以传多个tagname,用逗号隔开, 当前tagname只支持栏目ID且该栏目没有child
	 * @param top
	 *            用于分页，取下一页$top=最后一个标签的offset
	 * @return
	 */
	protected static String getLastArticlesByTag(String tagname, String top,
			String publishTime) {
		String url = SLATE_URL + "/tag/" + tagname
				+ "/articlelist?orderby=sortByTagnameFirst";
		if (!TextUtils.isEmpty(top)) {
			url += "&top=" + top;
		}
		if (!TextUtils.isEmpty(publishTime)) {
			url += "&updatetime=" + publishTime;
		}
		return url;
	}

	/**
	 * 获取文章详细信息URL
	 * 
	 * @param articleId
	 *            文章统一标识，可以传多个articleid
	 * @param contentType
	 *            1.文章全部信息 2.文章摘要信息 (默认为1)
	 * @return
	 */
	protected static String getArticleDetails(String articleId, int contentType) {
		String url = SLATE_URL + "/article/" + articleId;
		return contentType == 2 ? url + "?contenttype=2" : url;
	}

	/**
	 * 取用户订阅列表URL
	 * 
	 * @param uid
	 * @return
	 */
	public static String getUserSubscribeList(String uid, String token) {
		return USER_MODEL_URL
				+ "?m=subscribeColumn&a=getUserSubscribeList&uid=" + uid
				+ "&appid=" + ConstData.getInitialAppId() + "&datatype="
				+ ConstData.DATA_TYPE + "&token=" + token;
	}

	/**
	 * 存用户订阅列表URL
	 * 
	 * @param uid
	 * @return
	 */
	public static String getAddUserSubscribeList(String uid) {
		return USER_MODEL_URL
				+ "?m=subscribeColumn&a=saveUserSubscribeList&uid=" + uid
				+ "&appid=" + ConstData.getInitialAppId() + "&datatype="
				+ ConstData.DATA_TYPE;
	}

	/**
	 * 同步收藏
	 * 
	 * @return
	 */
	public static String getUpdateFav(String uid) {
		return SLATE_BASE_URL + "/Favorites/save?datatype="
				+ ConstData.DATA_TYPE + "&uid=" + uid + "&appId="
				+ ConstData.getInitialAppId() + "&deviceType="
				+ ConstData.DEVICE_TYPE;
	}

	/**
	 * 获取服务器上的收藏列表 uid,appid,
	 * 
	 * @return
	 */
	public static String getFav(String uid) {
		return SLATE_BASE_URL + "/Favorites/get?datatype="
				+ ConstData.DATA_TYPE + "&uid=" + uid + "&appId="
				+ ConstData.getInitialAppId() + "&deviceType="
				+ ConstData.DEVICE_TYPE;
	}

	/**
	 * 获取push文章地址
	 * 
	 * @param articleId
	 * @return
	 */
	public static String getPushArticle(String articleId) {
		String url = SLATE_URL + "/push/" + articleId;
		if (!TextUtils.isEmpty(AppValue.appInfo.getUpdatetime())) {
			url += "?";
			return addUpdatetime(url);
		}
		return url;
	}
}
