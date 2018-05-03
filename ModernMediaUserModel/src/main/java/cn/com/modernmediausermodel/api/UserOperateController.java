package cn.com.modernmediausermodel.api;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.vip.GetCodeTypeOperate;
import cn.com.modernmediausermodel.vip.GetCodeVipOperate;
import cn.com.modernmediausermodel.vip.GetVipCodeTypeOperate;
import cn.com.modernmediausermodel.vip.GetVipInfoOperate;

import static cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType.USE_HTTP_ONLY;

/**
 * 接口控制
 *
 * @author ZhuQiao
 */
public class UserOperateController {

    private static UserOperateController instance;
    private static Context mContext;

    private Handler mHandler = new Handler();

    private UserOperateController(Context context) {
        mContext = context;
    }

    public static synchronized UserOperateController getInstance(Context context) {
        mContext = context;
        if (instance == null) instance = new UserOperateController(context);
        return instance;
    }

    private void doRequest(SlateBaseOperate operate, final Entry entry, FetchApiType type, final UserFetchEntryListener listener) {
        operate.asyncRequest(mContext, type, new DataCallBack() {

            @Override
            public void callback(boolean success, boolean fromHttp) {
                sendMessage(success ? entry : null, listener, fromHttp);
            }
        });
    }

    private void doPostRequest(SlateBaseOperate operate, final Entry entry, FetchApiType type, final UserFetchEntryListener listener) {
        operate.asyncRequestByPost(mContext, type, new DataCallBack() {

            @Override
            public void callback(boolean success, boolean fromHttp) {
                sendMessage(success ? entry : null, listener, fromHttp);
            }
        });
    }

    /**
     * 通过handler返回数据
     *
     * @param entry
     * @param listener
     */
    private void sendMessage(final Entry entry, final UserFetchEntryListener listener, boolean fromHttp) {
        synchronized (mHandler) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    listener.setData(entry);
                }
            });
        }
    }

    /**
     * 用户登录
     *
     * @param userName 用户名称
     * @param password 密码
     * @param listener view数据回调接口
     */
    public void login(Context context, String userName, String password, UserFetchEntryListener listener) {
        UserLoginOperate operate = new UserLoginOperate(context, userName, password);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 开放平台(新浪微博、QQ等)账号登录
     *
     * @param user     用户信息
     * @param avatar   服务器相对地址
     * @param type     平台类型,目前0:普通登录；1：新浪微博；2：腾讯qq；3：微信4Facebook；5phone
     * @param listener view数据回调接口
     */
    public void openLogin(User user, String avatar, String code, int type, UserFetchEntryListener listener) {
        NewOpenLoginOperate operate = new NewOpenLoginOperate(mContext, user, avatar, code, type);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 用户注册
     *
     * @param userName 用户名称
     * @param password 密码
     * @param listener view数据回调接口
     */
    public void register(String userName, String password, String code, String phone, String nick, UserFetchEntryListener listener) {
        UserRegisterOperate operate = new UserRegisterOperate(userName, password, code, phone, nick);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 用户登出
     *
     * @param uid      uid
     * @param token    用户token
     * @param listener view数据回调接口
     */
    public void loginOut(Context context, String uid, String token, UserFetchEntryListener listener) {
        UserLoginOutOperate operate = new UserLoginOutOperate(context, uid, token);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 获取当前用户的信息 通过uid和token获取当前用户信息，uid和token用pb格式post方式接收(可检测用户登录状态) url
     *
     * @param uid      uid
     * @param token    用户token
     * @param listener view数据回调接口
     */
    public void getInfoByIdAndToken(String uid, String token, UserFetchEntryListener listener) {
        getInfoByIdAndToken(uid, token, FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取当前用户的信息 通过uid和token获取当前用户信息，uid和token用pb格式post方式接收(可检测用户登录状态) url
     *
     * @param uid      uid
     * @param token    用户token
     * @param type     取数据方式
     * @param listener view数据回调接口
     */
    public void getInfoByIdAndToken(String uid, String token, FetchApiType type, UserFetchEntryListener listener) {
        GetUserInfoOperate operate = new GetUserInfoOperate(uid, token);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 忘记密码
     *
     * @param listener view数据回调接口
     */
    public void getPassword(String userName, String code, String newPwd, UserFetchEntryListener listener) {
        UserFindPasswordOperate operate = new UserFindPasswordOperate(userName, code, newPwd);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 修改用户资料
     *
     * @param uid      uid
     * @param token    用户token
     * @param userName 用户名
     * @param nickName 昵称
     * @param url      图片的相对地址(通过上传头像获得)
     * @param password 用户登录密码
     * @param desc     用户登录密码
     * @param listener view数据回调接口
     */
    public void modifyUserInfo(String uid, String token, String userName, String nickName, String url, String password, String desc, boolean pushEmail, UserFetchEntryListener listener) {
        ModifyUserInfoOperate operate = new ModifyUserInfoOperate(uid, token, userName, nickName, url, password, desc, pushEmail);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 修改VIP资料
     */
    public void modifyUserVipInfo(String uid, String token, String realname, int sex, String birth, String province, String city, String industry, String position, String income, String phone, UserFetchEntryListener listener) {
        ModifyUserInfoOperate operate = new ModifyUserInfoOperate(uid, token, realname, sex, birth, province, city, industry, position, income, phone);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 修改用户密码
     *
     * @param uid         uid
     * @param token       用户token
     * @param userName    用户名
     * @param password    旧密码
     * @param newPassword 新密码
     * @param listener    view数据回调接口
     */
    public void modifyUserPassword(String uid, String token, String userName, String password, String newPassword, UserFetchEntryListener listener) {
        ModifyUserPasswordOperate operate = new ModifyUserPasswordOperate(uid, token, userName, password, newPassword);
        doPostRequest(operate, operate.getUser(), USE_HTTP_ONLY, listener);
    }

    /**
     * 上传用户头像
     *
     * @param imagePath 头像存储在本地的路径
     * @param listener  view数据回调接口
     */
    public void uploadUserAvatar(String imagePath, UserFetchEntryListener listener) {
        UploadUserAvaterOperate operate = new UploadUserAvaterOperate(imagePath);
        doPostRequest(operate, operate.getUploadResult(), USE_HTTP_ONLY, listener);
    }

    /**
     * 获取推荐用户列表
     */
    public void getRecommendUsers(String uid, int pageType, String offsetId, int lastDbId, Context context, UserFetchEntryListener listener) {
        GetRecommendUsersOperate operate = new GetRecommendUsersOperate(uid, pageType, offsetId, lastDbId, context);
        doRequest(operate, operate.getUserCardInfoList(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取多用户信息列表
     */
    public void getUsersInfo(Set<String> uidSet, UserFetchEntryListener listener) {
        GetUsersInfoOperate operate = new GetUsersInfoOperate(mContext);
        operate.setUids(uidSet);
        operate.setShowToast(false);
        doRequest(operate, operate.getUsers(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 取获取用户卡片相关统计信息(收藏数、卡片数、粉丝数)
     *
     * @param uid
     * @param listener
     */
    public void getUserCardInfo(String uid, String customerUid, UserFetchEntryListener listener) {
        GetUserCardInfoOperate operate = new GetUserCardInfoOperate(uid, customerUid);
        doRequest(operate, operate.getUserCardInfo(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 添加关注用户
     *
     * @param uid
     * @param user
     * @param refreshList 是否需要刷新朋友页
     * @param listener
     */
    public void addFollow(String uid, List<UserCardInfo> user, boolean refreshList, UserFetchEntryListener listener) {
        AddFollowOperate operate = new AddFollowOperate(uid, user, refreshList);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 取消关注用户
     *
     * @param uid
     * @param refreshList 是否需要刷新朋友页
     * @param listener
     */
    public void deleteFollow(String uid, List<UserCardInfo> userCardInfoList, boolean refreshList, UserFetchEntryListener listener) {
        DeleteFollowOperate operate = new DeleteFollowOperate(uid, userCardInfoList, refreshList);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 获取用户卡片
     */
    public void getUserCard(String uid, String timelineId, boolean isGetNewData, UserFetchEntryListener listener) {
        GetUserCardOperate operate = new GetUserCardOperate(mContext, uid, timelineId, isGetNewData);
        operate.setShowToast(false);
        doRequest(operate, operate.getCard(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取推荐的卡片
     */
    public void getRecommendCard(String timelineId, boolean isGetNewData, UserFetchEntryListener listener) {
        GetRecommendCardOperate operate = new GetRecommendCardOperate(mContext, timelineId, isGetNewData);
        operate.setShowToast(false);
        doRequest(operate, operate.getCard(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取卡片评论
     */
    public void getCardComments(ArrayList<CardItem> cardItems, int commentId, boolean isGetNewData, UserFetchEntryListener listener) {
        GetCardCommentsOperate operate = new GetCardCommentsOperate(cardItems, commentId, isGetNewData);
        doRequest(operate, operate.getComments(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 添加评论
     */
    public void addCardComment(CommentItem comment, UserFetchEntryListener listener) {
        CardAddCommentOperate operate = new CardAddCommentOperate(comment);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 添加卡片
     *
     * @param cardItem
     * @param listener
     */
    public void addCard(CardItem cardItem, UserFetchEntryListener listener) {
        AddCardOperate operate = new AddCardOperate(cardItem);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 添加卡片
     *
     * @param data
     * @param listener
     */
    public void addCard(String data, UserFetchEntryListener listener) {
        AddCardOperate operate = new AddCardOperate(data);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 删除卡片
     *
     * @param uid
     * @param cardId
     * @param listener
     */
    public void deleteCard(String uid, String cardId, UserFetchEntryListener listener) {
        DeleteCardOperate operate = new DeleteCardOperate(uid, cardId);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 收藏卡片
     *
     * @param uid
     * @param cardId
     * @param listener
     */
    public void addCardFav(String uid, String cardId, UserFetchEntryListener listener) {
        CardFavOperate operate = new CardFavOperate(uid, cardId, CardFavOperate.TYPE_ADD);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 取消收藏卡片
     *
     * @param uid
     * @param cardId
     * @param listener
     */
    public void cancelCardFav(String uid, String cardId, UserFetchEntryListener listener) {
        CardFavOperate operate = new CardFavOperate(uid, cardId, CardFavOperate.TYPE_DELETE);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 获取用户timeline
     *
     * @param uid
     * @param listener
     */
    public void getUserTimeLine(String uid, String timelineId, boolean isGetNewData, UserFetchEntryListener listener) {
        GetUserTimeLineOperate operate = new GetUserTimeLineOperate(mContext, uid, timelineId, isGetNewData);
        operate.setShowToast(false);
        doRequest(operate, operate.getCard(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取消息列表
     *
     * @param uid
     * @param listener
     */
    public void getMessageList(String uid, int lastId, UserFetchEntryListener listener) {
        GetMessageListOperate operate = new GetMessageListOperate(uid, lastId);
        doPostRequest(operate, operate.getMessage(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @param listener
     */
    public void getVerifyCode(String phone, UserFetchEntryListener listener) {
        GetVerifyCodeOperate operate = new GetVerifyCodeOperate(phone);
        doPostRequest(operate, operate.getCode(), USE_HTTP_ONLY, listener);
    }

    /**
     * 获取绑定状态
     *
     * @param uid
     * @param token
     * @param listener
     */
    public void getBandStatus(Context c, String uid, String token, UserFetchEntryListener listener) {
        GetBandStatusOperate operate = new GetBandStatusOperate(c, uid, token);
        doPostRequest(operate, operate.getStatus(), USE_HTTP_ONLY, listener);
    }

    /**
     * 获取用户绑定信息
     */
    public void bandAccount(String uid, String token, int bindType, String userName, String code, UserFetchEntryListener listener) {
        BandAccountOperate operate = new BandAccountOperate(uid, bindType, token, userName, code);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 获取单张卡片的详情
     *
     * @param cardId
     * @param listener
     */
    public void getCardDetail(String cardId, UserFetchEntryListener listener) {
        GetCardDetailOperate operate = new GetCardDetailOperate(cardId);
        doRequest(operate, operate.getCard(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 根据文章id获取相关的卡片
     *
     * @param listener
     */
    public void getCardListByArticleId(String articleId, String uid, UserFetchEntryListener listener) {
        GetCardByArticleIdOperate operate = new GetCardByArticleIdOperate(mContext, articleId, "", "");
        doRequest(operate, operate.getCard(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取应用规则列表
     *
     * @param listener
     */
    public void getActionRules(String uid, String token, UserFetchEntryListener listener) {
        GetAppActionRulesOperate operate = new GetAppActionRulesOperate(uid, token);
        doRequest(operate, operate.getActionRuleList(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取商品列表
     *
     * @param listener
     */
    public void getGoodsList(UserFetchEntryListener listener) {
        GetGoodsListOperate operate = new GetGoodsListOperate();
        doRequest(operate, operate.getGoodsList(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 商品订购
     *
     * @param uid
     * @param token
     * @param goodsId
     * @param listener
     */
    public void addGoodsOrder(String uid, String token, String goodsId, UserFetchEntryListener listener) {
        AddGoodsOrderOperate operate = new AddGoodsOrderOperate(uid, token, goodsId);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 增加积分
     *
     * @param uid
     * @param token
     * @param actionRuleIds 规则id,数组或者,号分割字符串
     * @param listener
     */
    public void addUserCoinNumber(String uid, String token, String actionRuleIds, UserFetchEntryListener listener) {
        AddUserCoinNumberOperate operate = new AddUserCoinNumberOperate(uid, token, actionRuleIds);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 上传积分兑换实体商品的用户信息
     *
     * @param uid
     * @param address
     * @param name
     * @param phone
     * @param listener
     */

    public void postUserCentOrderInfo(String uid, String token, String goodsId, String address, String name, String phone, UserFetchEntryListener listener) {
        AddGoodsOrderOperate operate = new AddGoodsOrderOperate(uid, token, goodsId, address, name, phone);
        doPostRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);

    }

    /**
     * 获取兑换按钮状态
     *
     * @param uid
     * @param listener
     */
    public void getChangeStatus(String uid, String token, UserFetchEntryListener listener) {
        GetUserChangeStatusOperate operate = new GetUserChangeStatusOperate(uid, token);
        doPostRequest(operate, operate.getOrder(), USE_HTTP_ONLY, listener);
    }

    /**
     * 获取活动列表
     */
    public void getActiveList(String uid, String token, UserFetchEntryListener listener) {
        GetActiveListOperate operate = new GetActiveListOperate(uid, token);
        doRequest(operate, operate.getError(), USE_HTTP_ONLY, listener);
    }

    /**
     * 意见反馈
     */
    public void feedBack(Context context, String subject, String name, String emailOrPhone, List<String> images, UserFetchEntryListener listener) {
        FeedBackOperate operate = new FeedBackOperate(context, subject, name, emailOrPhone, images);
        doPostRequest(operate, operate.getResult(), USE_HTTP_ONLY, listener);
    }


    /**
     * 录入vip基本信息
     */
    public void getVipInfo(UserFetchEntryListener listener) {
        GetVipInfoOperate operate = new GetVipInfoOperate();
        doRequest(operate, operate.getVipInfo(), USE_HTTP_ONLY, listener);
    }


    /**
     * 兑换码兑换vip
     */
    public void exchangeCodeVip(String uid, String token, String sn, UserFetchEntryListener listener) {
        GetCodeVipOperate operate = new GetCodeVipOperate(uid, token, sn, mContext);
        doPostRequest(operate, operate.getErrorMsg(), USE_HTTP_ONLY, listener);
    }

    /**
     * 4.2.0礼品码兑换类型
     */
    public void exchangeCodeType(String uid, String token, String sn, UserFetchEntryListener listener) {
        GetCodeTypeOperate operate = new GetCodeTypeOperate(uid, token, sn, mContext);
        doPostRequest(operate, operate.getErrorMsg(), USE_HTTP_ONLY, listener);
    }

    /**
     * 过去兑换码类型
     */
    public void getVipCodeType(String uid, String token, String sn, UserFetchEntryListener listener) {
        GetVipCodeTypeOperate getVipCodeTypeOperate = new GetVipCodeTypeOperate(uid, token, sn);
        doPostRequest(getVipCodeTypeOperate, getVipCodeTypeOperate.getVipCodeType(), USE_HTTP_ONLY, listener);
    }

    /**
     * 积分签到
     */
    public void getSign(String uid, String token, UserFetchEntryListener listener) {
        GetSignOperate operate = new GetSignOperate(uid, token);
        doPostRequest(operate, operate.getErrorMsg(), FetchApiType.USE_HTTP_ONLY, listener);
    }


    /**
     * fm播放统计
     *
     * @param context
     * @param articleid
     * @param resid
     * @param actiontype
     * @param actionpostion
     * @param listener
     */
    public void addFmTime(Context context, String articleid, String resid, int actiontype, int actionpostion, UserFetchEntryListener listener) {
        FMAddOperate fmAddOperate = new FMAddOperate(context, articleid, resid, actiontype, actionpostion);
        doPostRequest(fmAddOperate, fmAddOperate.getErrorMsg(), FetchApiType.USE_HTTP_ONLY, listener);
    }
}