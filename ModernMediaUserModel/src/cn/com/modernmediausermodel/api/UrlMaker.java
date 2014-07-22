package cn.com.modernmediausermodel.api;

import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 获取借口信息
 * 
 * @author ZhuQiao
 * 
 */
public class UrlMaker {
	/** 用户模块基础URL信息 */
	private static String USER_MODEL_URL = "";

	public static void setUserModelUrl() {
		if (UserConstData.IS_DEBUG == 0) {
			USER_MODEL_URL = "http://user.bbwc.cn/interface/index.php";
		} else if (UserConstData.IS_DEBUG == 1) {
			USER_MODEL_URL = "http://user.test.bbwc.cn/interface/index.php";
		} else if (UserConstData.IS_DEBUG == 2) {
			USER_MODEL_URL = "http://10.0.7.184/mmuser/interface/index.php";
		} else if (UserConstData.IS_DEBUG == 4) {
			USER_MODEL_URL = "http://10.0.7.184/jinxin/interface/index.php";
		}
	}

	/**
	 * @return 用户登录用的url
	 */
	public static String getLoginUrl() {
		return USER_MODEL_URL + "?m=user&a=login&datatype="
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
}
