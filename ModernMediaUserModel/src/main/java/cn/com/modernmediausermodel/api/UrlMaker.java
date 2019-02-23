package cn.com.modernmediausermodel.api;

import android.text.TextUtils;

import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 获取接口url信息
 *
 * @author ZhuQiao
 */
public class UrlMaker {
    ;
    /**
     * 用户模块基础URL信息
     */
    private static String USER_MODEL_URL = "";
    private static String CARD_URL = "";
    private static String COIN_URL = "";


    public static void setUserModelUrl() {
        String card_host = "";
        if (UserConstData.IS_DEBUG == 0) {// 线上环境
            USER_MODEL_URL = "http://user.bbwc.cn/";
            card_host = "http://card.bb.bbwc.cn/vt/app%s/card/api/";
            COIN_URL = "http://cent.bbwc.cn/cent/";
        } else if (UserConstData.IS_DEBUG == 1) { // in-house环境
            USER_MODEL_URL = "http://user.test.bbwc.cn/";
            card_host = "http://card.test.bbwc.cn/vt/app%s/card/api/";
            COIN_URL = "http://cent.inhouse.bbwc.cn/cent/";
        } else if (UserConstData.IS_DEBUG == 2) {
            USER_MODEL_URL = "http://debug.bbwc.cn/account/";
            card_host = "http://develop.cname.bbwc.cn/dev/vt/app%s/card/api/";
        } else if (UserConstData.IS_DEBUG == 4) { // 本地
            USER_MODEL_URL = "http://1develop.cname.bbwc.cn/jinxin/interface/index.php";
        } else if (ConstData.IS_DEBUG == 8) {// 编辑版
            USER_MODEL_URL = "http://user.bbwc.cn/";
        }
        CARD_URL = String.format(card_host, UserConstData.getInitialAppId());
    }

    /**
     * @return 用户登录用的url
     */
    public static String getLoginUrl() {
        return USER_MODEL_URL + "user/login?datatype=" + UserConstData.DATA_TYPE + "&datapass=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 新浪用户登录用的url
     */
    public static String getSinaLoginUrl() {
        return USER_MODEL_URL + "user/sina_login?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 开放平台用户登录用的url
     */
    public static String getOpenLoginUrl() {
        return USER_MODEL_URL + "user/open_login?datatype=" + UserConstData.DATA_TYPE + "&datapass=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 用户注册用的url
     */
    public static String getRegisterUrl() {
        return USER_MODEL_URL + "user/add?datatype=" + UserConstData.DATA_TYPE + "&datapass=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 用户登出用的url
     */
    public static String getLoginOutUrl() {
        return USER_MODEL_URL + "user/logout?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 用于获取当前用户信息(通过uid和token)的url
     */
    public static String getUserInfoUrlByUidAndToken() {
        return USER_MODEL_URL + "user/get?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 用于获取当前用户信息(通过uid)的url
     */
    public static String getUserInfoUrlByUid() {
        return USER_MODEL_URL + "user/getUserInfo?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 用于找回密码的url
     */
    public static String getPasswordUrl() {
        return USER_MODEL_URL + "user/find_password?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 用于修改用户资料的url
     */
    public static String getModifyInfoUrl() {
        return USER_MODEL_URL + "user/modify?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 用于修改密码的url
     */
    public static String getModifyPasswordUrl() {
        return USER_MODEL_URL + "user/modify_password?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * @return 用于上传头像的url
     */
    public static String getUploadAvatarUrl() {
        return USER_MODEL_URL + "user/upload?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 获取服务器推荐用户关注的用户列表
     *
     * @return
     */
    public static String getReccommendUsers() {
        return CARD_URL + "recommendUserList/datatype/" + UserConstData.DATA_TYPE;
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
        return USER_MODEL_URL + "user/getUserInfo?datatype=" + UserConstData.DATA_TYPE;
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
        return CARD_URL + "recommendCardList/datatype/" + UserConstData.DATA_TYPE;
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
        return USER_MODEL_URL + "sms/noticeList?datatype=2";
    }


    /**
     * 获取卡片详情
     *
     * @param cardId
     * @return
     */
    public static String getCardDetail(String cardId) {
        return CARD_URL + "getCard/datatype/" + UserConstData.DATA_TYPE + "/cardid/" + cardId;
    }

    /**
     * 获取摘自该文章的卡片
     *
     * @param articleId
     * @param issueId   期id，可为空
     * @param uid       用户id，可为空
     * @return
     */
    public static String getCardByArticleId(String articleId, String issueId, String uid) {
        String url = CARD_URL + "listbyarticleid/datatype/" + UserConstData.DATA_TYPE + "/articleid/" + articleId;
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
        return COIN_URL + "shopinfo/" + UserConstData.getInitialAppId() + "?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 应用规则接口
     *
     * @return
     */
    public static String getAppActionRule(String uid, String token) {
        return COIN_URL + "index/cent/getrulenametest" + "?appid=" + UserConstData.getInitialAppId() + "&datatype=" + UserConstData.DATA_TYPE + "&uid=" + uid + "&token=" + token;

    }

    /**
     * 签到接口
     *
     * @return
     */
    public static String getSign() {
        return COIN_URL + "index/sign/sign" + "&datatype=" + UserConstData.DATA_TYPE + "&datapass=1&datapb=sign";
    }

    /**
     * 应用商品列表
     *
     * @return
     */
    public static String getAppGoods() {
        return COIN_URL + "goods/" + UserConstData.getInitialAppId() + "?datatype=" + UserConstData.DATA_TYPE + "&new=1";
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
        return COIN_URL + "orders/finishOrders" + "?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 获取用户订购按钮状态
     *
     * @return
     */
    public static String getChangeStatus() {
        return COIN_URL + "orders/getorderstatus" + "?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 获取用户在该应用下的订单接口（暂时不用）
     *
     * @return
     */
    public static String getUserAppOrder(String uid) {
        return COIN_URL + "getorders/appid/" + UserConstData.getInitialAppId() + "/uid/" + uid + "?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 获取验证码
     *
     * @return
     */
    public static String getVerifyCode() {
        return USER_MODEL_URL + "sms/send?datatype=" + UserConstData.DATA_TYPE;
    }


    /**
     * 获取绑定状态
     *
     * @return
     */
    public static String getBandStatus() {
        return USER_MODEL_URL + "user/bindstatus?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 绑定账号
     *
     * @return
     */
    public static String bandAccount() {
        return USER_MODEL_URL + "user/bind?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 获取活动列表
     */
    public static String getActiveList(String uid, String token) {
        return USER_MODEL_URL + "inteface/index.php?m=active&r=service/list/datatype/" + UserConstData.DATA_TYPE + "/uid/" + uid + "/appid/" + UserConstData.getInitialAppId() + "/token/" + token;
    }

    /**
     * 意见反馈
     *
     * @return
     */
    public static String getFeedBackUrl() {
        if (UserConstData.IS_DEBUG == 0) return "http://wecenter.bbwc.cn/?/api/QuestionApi/";
        else return "http://debug.bbwc.cn/wecenter/?/api/QuestionApi/";
    }


    /**
     * 获取VIP基本信息接口 行业 职位 年收入
     *
     * @return
     */
    public static String getVipInfo() {
        return USER_MODEL_URL + "user/getCategory?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * VIP 获取二维码
     *
     * @return
     */
    public static String getQrCode(String uid, String token) {
        return USER_MODEL_URL + "user/qrcode?uid=" + uid + "&token=" + token;
    }

    /**
     * 礼品码兑换
     *
     * @return
     */
    public static String getCodeVip() {
        return COIN_URL + "index/codeb/usesn?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 礼品码兑换类型
     *
     * @return
     */
    public static String getCodeType() {
        return COIN_URL + "index/codeb/sntype?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 杂志购买
     */
    public static String getMagazineBuy() {
        return "https://shop420847.koudaitong.com/v2/feature/19x6o2qov?sf=wx_sm";
    }

    /**
     * https://user-test.bbwc.cn/userbehavior/listenFM/type/1?datatype=2
     *
     * @return
     */
    public static String addFmPlaytime() {
        return USER_MODEL_URL + "userbehavior/listenFM/type/1?datatype=" + UserConstData.DATA_TYPE;
    }

    /**
     * 获取我的已购页面
     *
     * @return
     */
    public static String getMyOrderedUrl() {
        return "http://live.bbwc.cn/public/course/users/my.html";
    }

    /**
     * 获取我的理财页面
     *
     * @return
     */
    public static String getMyLicaiUrl() {
        if (UserConstData.IS_DEBUG != 0)
            return "https://live-test.bbwc.cn/public/course/users/finance.html";
        else

            return "https://live.bbwc.cn/public/course/users/finance.html";
    }
}
