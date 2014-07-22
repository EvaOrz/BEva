package cn.com.modernmediausermodel.api;

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

	public static void setUserModelUrl() {
		String card_host = "";
		if (UserConstData.IS_DEBUG == 0) {
			USER_MODEL_URL = "http://user.bbwc.cn/interface/index.php";
			card_host = "http://card.bb.bbwc.cn/v1/app%s/";
		} else if (UserConstData.IS_DEBUG == 1) {
			USER_MODEL_URL = "http://user.test.bbwc.cn/interface/index.php";
			card_host = "http://card.test.bbwc.cn/v1/app%s/";
		} else if (UserConstData.IS_DEBUG == 2) {
			USER_MODEL_URL = "http://develop.cname.bbwc.cn/mmuser/interface/index.php";
			// card_host =
			// "http://develop.cname.bbwc.cn/dev/v1/app%s/card/interface/datatype/%s";
		} else if (UserConstData.IS_DEBUG == 4) { // 本地
			USER_MODEL_URL = "http://1develop.cname.bbwc.cn/jinxin/interface/index.php";
			// card_host =
			// "http://develop.cname.bbwc.cn/jinxin/slate/v1/app%s/card/interface/datatype/%s";
		}
		CARD_URL = String.format(card_host, UserConstData.getAppId());
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
	 * 同步收藏
	 * 
	 * @return
	 */
	public static String getUpdateFav() {
		return USER_MODEL_URL + "?m=user&a=updateFav&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 获取服务器上的收藏列表 uid,appid,
	 * 
	 * @return
	 */
	public static String getFva() {
		return USER_MODEL_URL + "?m=user&a=getFav&datatype="
				+ UserConstData.DATA_TYPE;
	}

	/**
	 * 获取服务器推荐用户关注的用户列表
	 * 
	 * @return
	 */
	public static String getReccommendUsers() {
		return CARD_URL + "user/recommend/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取用户卡片相关信息
	 * 
	 * @return
	 */
	public static String getUserCardInfo() {
		return CARD_URL + "user/get/datatype/" + UserConstData.DATA_TYPE;
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
		return CARD_URL + "follow/add/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 取消关注用户
	 * 
	 * @return
	 */
	public static String getDelFollow() {
		return CARD_URL + "follow/delete/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取用户卡片信息
	 * 
	 * @return
	 */
	public static String getUserCard() {
		return CARD_URL + "card/list/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取推荐给用户的卡片
	 * 
	 * @return
	 */
	public static String getRecommentCard() {
		return CARD_URL + "card/recommend/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取对卡片的评论
	 * 
	 * @return
	 */
	public static String getCardComments() {
		return CARD_URL + "comment/list/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 添加评论
	 * 
	 * @return
	 */
	public static String getAddComment() {
		return CARD_URL + "comment/add/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 粉丝列表
	 * 
	 * @return
	 */
	public static String getFans() {
		return CARD_URL + "user/follower/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 朋友列表
	 * 
	 * @return
	 */
	public static String getFriends() {
		return CARD_URL + "user/follow/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 会员timeline
	 * 
	 * @return
	 */
	public static String getTimeLine() {
		return CARD_URL + "card/timeline/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 添加卡片
	 * 
	 * @return
	 */
	public static String getAddCard() {
		return CARD_URL + "card/add/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 删除卡片
	 * 
	 * @return
	 */
	public static String getDelCard() {
		return CARD_URL + "card/delete/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 收藏卡片
	 * 
	 * @return
	 */
	public static String getAddCardFav() {
		return CARD_URL + "favorite/add/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 取消卡片收藏
	 * 
	 * @return
	 */
	public static String getCancelCardFav() {
		return CARD_URL + "favorite/delete/datatype/" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取消息列表
	 * 
	 * @return
	 */
	public static String getMessageList() {
		String url = "http://user.bbwc.cn/interface/";
		return url + "?m=sms&a=noticeList&datatype=" + UserConstData.DATA_TYPE;
	}

	/**
	 * 获取卡片详情
	 * 
	 * @param cardId
	 * @return
	 */
	public static String getCardDetail(String cardId) {
		return CARD_URL + "card/get/datatype/" + UserConstData.DATA_TYPE
				+ "/cardid/" + cardId;
	}
}
