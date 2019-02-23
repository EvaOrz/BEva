package cn.com.modernmedia.businessweek;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tsz.afinal.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UrlMaker;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.widget.RecommendUserView;


/**
 * 商周用户中心
 *
 * @author Eva.
 */
public class UserCenterTabView extends RelativeLayout implements OnClickListener {
    private ImageView avatar, messageDot, vipLevel, zhanneiIcon;
    private TextView msgCenter;
    protected TextView userText, cardNumText, followNumText, fansNumText, vipEndTime, vipTitle;
    private UserOperateController controller;
    private Context mContext;
    private RelativeLayout cardInfoLayout, myOrderLayout, myBookLayout, myLicaiLayout, zhanneiLayout;
    private User user;
    private UserCardInfo cardInfo;
    private Button login;
    private boolean accountHasChecked = false;
    private Message mMessage = new Message();
    // 站内信list 未读的站内信list
    private List<AdvItem> zhanneiList = new ArrayList<>(), unReadList = new ArrayList<>();

    public UserCenterTabView(Context context) {
        this(context, null);
    }

    public UserCenterTabView(Context context, AttributeSet attr) {
        super(context, attr);
        this.mContext = context;
        init();
    }

    private void init() {
        user = SlateDataHelper.getUserLoginInfo(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        this.addView(inflater.inflate(R.layout.main_usercenter_view, null), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        controller = UserOperateController.getInstance(mContext);

        cardInfoLayout = (RelativeLayout) findViewById(R.id.user_center_card_info);
        myOrderLayout = (RelativeLayout) findViewById(R.id.user_center_my_ordered);
        myBookLayout = findViewById(R.id.user_center_my_booked);
        myLicaiLayout = findViewById(R.id.user_center_my_licai);
        zhanneiLayout = findViewById(R.id.user_center_layout_zhannei);
        zhanneiIcon = findViewById(R.id.user_center_zhannei_icon);
        login = (Button) findViewById(R.id.user_center_btn_login);
        avatar = (ImageView) findViewById(R.id.user_center_info_avatar);
        messageDot = (ImageView) findViewById(R.id.user_center_message_dot);
        vipLevel = (ImageView) findViewById(R.id.user_center_info_user_level_icon);
        msgCenter = (TextView) findViewById(R.id.user_center_message_number);
        userText = (TextView) findViewById(R.id.user_center_info_user_name);
        cardNumText = (TextView) findViewById(R.id.user_center_card_number);
        followNumText = (TextView) findViewById(R.id.user_center_follow_number);
        fansNumText = (TextView) findViewById(R.id.user_center_fan_number);
        vipEndTime = (TextView) findViewById(R.id.user_center_text_vip_endtime);
        vipTitle = (TextView) findViewById(R.id.user_center_text_vip);

        findViewById(R.id.use_center_contain).setOnClickListener(this);

        findViewById(R.id.user_center_layout_card).setOnClickListener(this);
        findViewById(R.id.user_center_layout_follow).setOnClickListener(this);
        findViewById(R.id.user_center_layout_fans).setOnClickListener(this);
        findViewById(R.id.user_center_layout_business_card).setOnClickListener(this);
        findViewById(R.id.user_center_square).setOnClickListener(this);
        findViewById(R.id.user_center_my_ordered).setOnClickListener(this);
        findViewById(R.id.user_center_my_booked).setOnClickListener(this);
        findViewById(R.id.user_center_my_licai).setOnClickListener(this);
        findViewById(R.id.user_center_layout_my_coin).setOnClickListener(this);
        findViewById(R.id.user_center_layout_buy).setOnClickListener(this);
        findViewById(R.id.user_center_layout_fav).setOnClickListener(this);
        findViewById(R.id.user_center_layout_setting).setOnClickListener(this);
        findViewById(R.id.user_center_layout_vip_mine).setOnClickListener(this);
        findViewById(R.id.user_center_layout_code).setOnClickListener(this);
        findViewById(R.id.user_center_layout_zhannei).setOnClickListener(this);
        login.setOnClickListener(this);
        avatar.setOnClickListener(this);

        initHeadView();
        reLoad();

    }

    /**
     * 初始化头部
     */
    private void initHeadView() {
        UserTools.setAvatar(mContext, user, avatar);
        // 设置用户昵称
        if (user != null) {
            userText.setText(user.getNickName());
            cardInfoLayout.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            showUserEndTime();
        }
    }

    /**
     * //判断用户状态 ，1订阅用户， 2vip用户，3订阅用户过期／或没有购买过，4vip用户过期
     */
    private void showUserEndTime() {//显示用户状态
        if (SlateDataHelper.getVipLevel(mContext) > 0) {
            vipLevel.setVisibility(VISIBLE);
            vipTitle.setText(R.string.vip_mine);
            vipLevel.setImageResource(R.drawable.vip_level_in);
            vipEndTime.setText(String.format(mContext.getString(R.string.date_text), Utils.strToDate(SlateDataHelper.getVipEndTime(mContext))));
        } else if (SlateDataHelper.getVipLevel(mContext) == -1) {
            vipTitle.setText(R.string.vip_open);
            vipLevel.setVisibility(VISIBLE);
            vipLevel.setImageResource(R.drawable.vip_level_out);
            vipEndTime.setText(R.string.vip_time_out);

        } else {
            vipTitle.setText(R.string.vip_open);
            vipLevel.setVisibility(GONE);
            vipEndTime.setText("");
        }
    }

    /**
     * 获得用户的卡片信息
     */
    public void getUserCardInfo() {
        if (user == null || TextUtils.isEmpty(user.getUid())) return;
        // showLoading();
        controller.getUserCardInfo(user.getUid(), null, new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                //                 disProcess();
                cardInfo = (UserCardInfo) entry;
                handler.sendEmptyMessage(0);
            }
        });
    }

    /**
     * 获取消息列表
     */
    public void getMessageList() {
        controller.getMessageList(Tools.getUid(mContext), UserDataHelper.getMessageLastId(mContext), new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof Message) {
                    mMessage = (Message) entry;
                    if (ParseUtil.listNotNull(mMessage.getMessageList())) {
                        if (SlateApplication.mConfig.getHas_coin() == 1) {
                            msgCenter.setVisibility(View.VISIBLE);
                            msgCenter.setText(mMessage.getMessageList().size() + "");
                        } else {
                            messageDot.setVisibility(View.VISIBLE);
                            msgCenter.setVisibility(View.GONE);
                        }
                    } else {
                        msgCenter.setVisibility(View.GONE);
                        messageDot.setVisibility(View.GONE);
                    }
                } else {
                    msgCenter.setVisibility(View.GONE);
                    messageDot.setVisibility(View.GONE);
                }
            }
        });
    }


    private void checkAccountIsValid() {
        if (user == null || TextUtils.isEmpty(user.getUid())) return;
        controller.getInfoByIdAndToken(user.getUid(), user.getToken(), new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof User) {
                    User tempUser = (User) entry;
                    ErrorMsg error = tempUser.getError();
                    // 取得成功
                    if (user != null && error.getNo() == 0 && !TextUtils.isEmpty(tempUser.getUid())) {
                        user.setLogined(true);
                        user = tempUser;
                        Log.e("第三方登录返回uid", user.getUid());
                        SlateDataHelper.saveUserLoginInfo(mContext, user);
                        SlateDataHelper.saveAvatarUrl(mContext, user.getUserName(), user.getAvatar());
                    } else {
                        // 无效用户，清掉缓存
                        SlateDataHelper.clearLoginInfo(mContext);
                    }
                } else {
                    // 无效用户，清掉缓存
                    SlateDataHelper.clearLoginInfo(mContext);
                }
                reLoad();
            }
        });

    }


    public void reLoad() {
        user = SlateDataHelper.getUserLoginInfo(mContext);
        handler.sendEmptyMessageDelayed(1, 1000);
        checkZhannei();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:// 更新用户卡片信息
                    if (cardInfo != null) {
                        cardNumText.setText(UserTools.changeNum(cardInfo.getCardNum()));
                        followNumText.setText(UserTools.changeNum(cardInfo.getFollowNum()));
                        fansNumText.setText(UserTools.changeNum(cardInfo.getFansNum()));
                    }
                    break;
                case 1:
                    if (user == null) {
                        login.setVisibility(View.VISIBLE);
                        cardInfoLayout.setVisibility(View.GONE);
                        myOrderLayout.setVisibility(View.GONE);
                        myBookLayout.setVisibility(View.GONE);
                        myLicaiLayout.setVisibility(View.GONE);
                        avatar.setImageResource(R.drawable.avatar_placeholder);
                        msgCenter.setVisibility(View.GONE);
                        vipTitle.setText(R.string.vip_open);
                        vipLevel.setVisibility(GONE);
                        vipEndTime.setText("");

                    } else if (accountHasChecked) {
                        login.setVisibility(View.GONE);
                        cardInfoLayout.setVisibility(View.VISIBLE);
                        myOrderLayout.setVisibility(View.VISIBLE);
                        myBookLayout.setVisibility(View.VISIBLE);
                        myLicaiLayout.setVisibility(View.VISIBLE);
                        // 获取头部用数据
                        getUserCardInfo();
                        // 获取消息列表
                        //                        getMessageList();
                        initHeadView();
                    } else if (Tools.checkNetWork(mContext)) {
                        checkAccountIsValid();
                        accountHasChecked = true;
                    }
                    break;

                case 2:// 检查站内信
                    if (ParseUtil.listNotNull(zhanneiList)) {
                        zhanneiLayout.setVisibility(View.VISIBLE);
                        if (ParseUtil.listNotNull(unReadList)) {
                            zhanneiIcon.setImageResource(R.drawable.user_center_zhan_icon1);
                        } else {
                            zhanneiIcon.setImageResource(R.drawable.user_center_zhan_icon);
                        }
                    } else {
                        zhanneiLayout.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    };

    /**
     * 是否显示站内信layout
     *
     * @return
     */
    public void checkZhannei() {
        if (mContext instanceof MainActivity) {
            zhanneiList.clear();
            zhanneiList.addAll(((MainActivity) mContext).getZhanneiadvList(AppValue.advTagList));
            unReadList.clear();
            unReadList.addAll(((MainActivity) mContext).getUnReadedAdvList(zhanneiList));
            handler.sendEmptyMessageDelayed(2, 500);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.user_center_layout_fav) { // 我的收藏,如果为登录，则显示本地收藏
            UserPageTransfer.gotoFavoritesActivity(mContext);
        } else if (id == R.id.user_center_info_avatar) { // 前往用户信息页面
            if (user == null)
                UserPageTransfer.gotoLoginActivity(mContext, UserPageTransfer.GOTO_HOME_PAGE);
            else UserPageTransfer.gotoUserInfoActivity(mContext, 0, null, null, 0);
        } else if (id == R.id.user_center_btn_login) {// 点击登录按钮
            UserPageTransfer.gotoLoginActivity(mContext, -1);
        } else if (id == R.id.user_center_layout_my_coin) { // 我的金币
            UserPageTransfer.gotoMyCoinActivity(mContext, false, false);
        } else if (id == R.id.user_center_layout_business_card) {// 前往商业笔记页面
            UserPageTransfer.gotoMyHomePageActivity(mContext, false);
        } else if (id == R.id.user_center_layout_card) { // 前往我的首页页面
            UserPageTransfer.gotoUserCardInfoActivity(mContext, user, false);
        } else if (id == R.id.user_center_layout_follow) { // 前往关注界面
            UserPageTransfer.gotoUserListActivity(mContext, user, RecommendUserView.PAGE_FRIEND, false);
        } else if (id == R.id.user_center_layout_fans) { // 前往粉丝界面
            UserPageTransfer.gotoUserListActivity(mContext, user, RecommendUserView.PAGE_FANS, false);
        } else if (id == R.id.user_center_square) {// 热门商业笔记(广场)
            UserPageTransfer.gotoSquareActivity(mContext, false);
        } else if (id == R.id.user_center_layout_setting) {// 设置页面
            UserPageTransfer.gotoSettingActivity(mContext, false);
        } else if (id == R.id.user_center_layout_buy) {//前往杂志购买
            UriParse.doLinkWeb(mContext, UrlMaker.getMagazineBuy(), false);
        } else if (id == R.id.user_center_layout_code) {//激活兑换码
            LogHelper.checkCode(mContext);
            UserPageTransfer.gotoCodeActivity(mContext, false);
        } else if (id == R.id.user_center_my_ordered) {// 我的已购
            UriParse.doLinkWeb(mContext, UrlMaker.getMyOrderedUrl(), false);
        } else if (id == R.id.user_center_layout_vip_mine) {
            if (user != null) {
                if (SlateDataHelper.getVipLevel(mContext) > 0) {//vip有效期内
                    UserPageTransfer.gotoMyVipActivity(mContext, false);
                } else UserPageTransfer.gotoVipActivity(mContext, false);//开通或升级VIP
            } else UserPageTransfer.gotoLoginActivity(mContext, UserPageTransfer.GOTO_MY_VIP);
        } else if (id == R.id.user_center_my_booked) {// 我的订阅
            mContext.startActivity(new Intent(mContext, MyBookedActivity.class));
        } else if (id == R.id.user_center_my_licai) {// 我的理财
            UriParse.doLinkWeb(mContext, UrlMaker.getMyLicaiUrl(), false);
        } else if (id == R.id.user_center_layout_zhannei) {// 站内信
            AdvItem advItem = null;
            if (ParseUtil.listNotNull(unReadList)) {
                advItem = unReadList.get(0);
            } else if (ParseUtil.listNotNull(zhanneiList)) {
                advItem = zhanneiList.get(0);
            }
            if (advItem != null && ParseUtil.listNotNull(advItem.getSourceList())) {
                UriParse.clickSlate(mContext, advItem.getSourceList().get(0).getLink(), new Entry[]{new ArticleItem()}, null, new Class<?>[0]);
                DataHelper.setShowZhannei(mContext, SlateDataHelper.getUid(mContext), advItem.getAdvId() + "");
                if (mContext instanceof MainActivity) ((MainActivity) mContext).checkZhannei();
            }
        }
    }
}
