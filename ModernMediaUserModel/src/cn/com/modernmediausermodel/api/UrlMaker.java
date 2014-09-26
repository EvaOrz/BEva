package cn.com.modernmediausermodel.api;

import android.text.TextUtils;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 获取接口url信息
 * 
 * @author ZhuQiao
 * 
 */
public class UrlMaker {
	/** 用户模块基础URL信息 */
	private static String USER_MODEL_URL = "";
	private static String CARD_URL = "";
	private static String COIN_URL = "";

	public static void setUserModelUrl() {
		String card_host = "";
		if (UserConstData.IS_DEBUG == 0) {
			USER_MODEL_URL = "http://user.bbwc.cn/interface/index.php";
			card_host = "http://card.bb.bbwc.cn/vt/app%s/card/api/";
			COIN_URL = "http://cent.bbwc.cn/cent/";
		} else if (UserConstData.IS_DEBUG == 1) {
			USER_MODEL_URL = "http://user.test.bbwc.cn/interface/index.php";
			// card_host = "http://card.test.bbwc.cn/v1/app%s/"; 没布置
		} else if (UserConstData.IS_DEBUG == 2) {
			USER_MODEL_URL = "http://develop.cname.bbwc.cn/mmuser/interface/index.php";
			card_host = "http://develop.cname.bbwc.cn/dev/vt/app%s/card/api/";
			COIN_URL = "http://develop.bbwc.cn/cent/";
		} else if (UserConstData.IS_DEBUG == 4) { // 本地
			USER_MODEL_URL = "http://1develop.cname.bbwc.cn/jinxin/interface/index.php";
		}
		CARD_URL = String.format(card_host, UserConstData.getInitialAppId());
	}

	/**
	 * @return 用户登录用的url
	 */
	public static String getLoginUrl() {
		return USER_MODEL_URL + "?m=user&a=login&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 新浪用户登录用的url
	 */
	public static String getSinaLoginUrl() {
		return USER_MODEL_URL + "?m=user&a=sina_login&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 开放平台用户登录用的url
	 */
	public static String getOpenLoginUrl() {
		return USER_MODEL_URL + "?m=user&a=open_login&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 用户注册用的url
	 */
	public static String getRegisterUrl() {
		return USER_MODEL_URL + "?m=user&a=add&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 新浪用户绑定用的url
	 */
	public static String getSinaBundledUrl() {
		return USER_MODEL_URL + "?a=sina_login&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 用户登出用的url
	 */
	public static String getLoginOutUrl() {
		return USER_MODEL_URL + "?m=user&a=logout&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 用于获取当前用户信息(通过uid和token)的url
	 */
	public static String getUserInfoUrlByUidAndToken() {
		return USER_MODEL_URL + "?m=user&a=get&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 用于获取当前用户信息(通过uid)的url
	 */
	public static String getUserInfoUrlByUid() {
		return USER_MODEL_URL + "?m=user&a=getUserInfo&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 用于找回密码的url
	 */
	public static String getPasswordUrl() {
		return USER_MODEL_URL + "?m=user&a=find_password&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 用于修改用户资料的url
	 */
	public static String getModifyInfoUrl() {
		return USER_MODEL_URL + "?m=user&a=modify&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 用于修改密码的url
	 */
	public static String getModifyPasswordUrl() {
		return USER_MODEL_URL + "?m=user&a=modify_password&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * @return 用于上传头像的url
	 */
	public static String getUploadAvatarUrl() {
		return USER_MODEL_URL + "?m=user&a=upload&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 获取服务器推荐用户关注的用户列表
	 * 
	 * @return
	 */
	public static String getReccommendUsers() {
		return CARD_URL + "recommendUserList/datatype/"
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 获取用户卡片相关信息
	 * 
	 * @return
	 */
	public static String getUserCardInfo() {
		return CARD_URL + "getuserinfo/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取多用户信息列表
	 * 
	 * @return
	 */
	public static String getUsersInfo() {
		return USER_MODEL_URL + "?m=user&a=getUserInfo&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 关注用户
	 * 
	 * @return
	 */
	public static String getAddFollow() {
		return CARD_URL + "follow/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 取消关注用户
	 * 
	 * @return
	 */
	public static String getDelFollow() {
		return CARD_URL + "unfollow/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取用户卡片信息
	 * 
	 * @return
	 */
	public static String getUserCard() {
		return CARD_URL + "mylist/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取推荐给用户的卡片
	 * 
	 * @return
	 */
	public static String getRecommentCard() {
		return CARD_URL + "recommendCardList/datatype/"
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 获取对卡片的评论
	 * 
	 * @return
	 */
	public static String getCardComments() {
		return CARD_URL + "commentlist/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 添加评论
	 * 
	 * @return
	 */
	public static String getAddComment() {
		return CARD_URL + "comment/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 粉丝列表
	 * 
	 * @return
	 */
	public static String getFans() {
		return CARD_URL + "FollowerList/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 朋友列表
	 * 
	 * @return
	 */
	public static String getFriends() {
		return CARD_URL + "FollowList/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 会员timeline
	 * 
	 * @return
	 */
	public static String getTimeLine() {
		return CARD_URL + "timeline/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 添加卡片
	 * 
	 * @return
	 */
	public static String getAddCard() {
		return CARD_URL + "add/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 删除卡片
	 * 
	 * @return
	 */
	public static String getDelCard() {
		return CARD_URL + "delete/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 收藏卡片
	 * 
	 * @return
	 */
	public static String getAddCardFav() {
		return CARD_URL + "fav/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 取消卡片收藏
	 * 
	 * @return
	 */
	public static String getCancelCardFav() {
		return CARD_URL + "unfav/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取消息列表
	 * 
	 * @return
	 */
	public static String getMessageList() {
		return USER_MODEL_URL + "?m=sms&a=noticeList&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 获取卡片详情
	 * 
	 * @param cardId
	 * @return
	 */
	public static String getCardDetail(String cardId) {
		return CARD_URL + "getCard/datatype/" + UserConstData.DATA_TYPE
				+ "/cardid/" + cardId;
	}

	/**
	 * 获取摘自该文章的卡片
	 * 
	 * @param articleId
	 * @param issueId
	 *            期id，可为空
	 * @param uid
	 *            用户id，可为空
	 * @return
	 */
	public static String getCardByArticleId(String articleId, String issueId,
			String uid) {
		String url = CARD_URL + "listbyarticleid/datatype/"
				+ UserConstData.DATA_TYPE + "/articleid/" + articleId;
		if (!TextUtils.isEmpty(issueId)) {
			url += "/issueid/" + issueId;
		}
		if (!TextUtils.isEmpty(uid)) {
			url += "/customer_uid/" + uid;
		}
		return url;
	}

	/**
	 * 获取应用积分商城信息
	 */
	public static String getShopInfo() {
		return COIN_URL + "shopinfo/" + UserConstData.getInitialAppId()
				+ "?datatype=" + UserConstData.DATA_TYPE;
	}

	/**
	 * 用户积分接口
	 * 
	 * @return
	 */
	public static String getUserCent(String uid) {
		return COIN_URL + "cent/" + uid + "?datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 应用规则接口
	 * 
	 * @return
	 */
	public static String getAppActionRule() {
		return COIN_URL + "actionrule/" + UserConstData.getInitialAppId()
				+ "?datatype=" + UserConstData.DATA_TYPE;
	}

	/**
	 * 应用商品列表
	 * 
	 * @return
	 */
	public static String getAppGoods() {
		return COIN_URL + "goods/" + UserConstData.getInitialAppId()
				+ "?datatype=" + UserConstData.DATA_TYPE;
	}

	/**
	 * 用户添加积分
	 * 
	 * @return
	 */
	public static String getAddUserCent() {
		return COIN_URL + "cent/add" + "?datatype=" + UserConstData.DATA_TYPE;
	}

	/**
	 * 用户订购接口
	 * 
	 * @return
	 */
	public static String getUserOrder() {
		return COIN_URL + "orders/finishOrders" + "?datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 获取用户在该应用下的订单接口（暂时不用）
	 * 
	 * @return
	 */
	public static String getUserAppOrder(String uid) {
		return COIN_URL + "getorders/appid/" + UserConstData.getInitialAppId()
				+ "/uid/" + uid + "?datatype=" + UserConstData.DATA_TYPE;
	}

	/**
	 * 取用户订阅列表URL
	 * 
	 * @param uid
	 * @return
	 */
	public static String getUserSubscribeList(String uid) {
		return USER_MODEL_URL + "?m=subcribeColumn&a=getUserSubcribeList&uid="
				+ uid + "&appid=" + UserConstData.getInitialAppId()
				+ "&datatype=" + UserConstData.DATA_TYPE;
	}

	/**
	 * 存用户订阅列表URL
	 * 
	 * @param uid
	 * @return
	 */
	public static String getAddUserSubscribeList(String uid) {
		return USER_MODEL_URL + "?m=subcribeColumn&a=saveUserSubcribeList&uid="
				+ uid + "&appid=" + UserConstData.getInitialAppId()
				+ "&datatype=" + UserConstData.DATA_TYPE;
	}
}
