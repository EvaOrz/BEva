package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.utils.Utils;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.common.SharePopWindow;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.push.NewPushManager;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.UpdateManager;
import cn.com.modernmedia.widget.EvaSwitchBar;
import cn.com.modernmedia.widget.EvaSwitchBar.OnChangeListener;
import cn.com.modernmedia.widget.WebViewPop;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Active;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserTools;
import cn.jpush.android.api.JPushInterface;

/**
 * 设置
 *
 * @author ZhuQiao
 */
public class SettingActivity extends BaseActivity implements OnClickListener, OnChangeListener {
    private Active active;
    private TextView bookStatus;// 订阅显示
    private TextView version;// 版本信息
    private EvaSwitchBar autoLoop;// head自动循环播放开关
    private EvaSwitchBar wifiVedio;// WiFi下视频自动播放开关
    private EvaSwitchBar pushSwitch;// 推送开关

    private LinearLayout login_out_linear;// 退出layout
    private LinearLayout setting_book_linear;// 订阅layout
    private boolean index_head_auto_loop = true, wifi_auto_play_vedio = true, ifPush = true;

    private User mUser;
    private ImageView versionRedPoint;
    private boolean canCheckUpdate = true;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initstatus();
        initView();
        initBookStatus();
        initActiveList();
    }

    private void initstatus() {
        index_head_auto_loop = DataHelper.getIndexHeadAutoLoop(this);
        wifi_auto_play_vedio = DataHelper.getWiFiAutoPlayVedio(this);
        ifPush = DataHelper.isPushServiceEnable(this);
        if (ifPush) {
            Log.e("setting", "推送开启状态");

        } else Log.e("setting", "推送关闭状态");
    }

    private void initView() {
        mUser = SlateDataHelper.getUserLoginInfo(this);
        findViewById(R.id.setting_back).setOnClickListener(this);
        bookStatus = (TextView) findViewById(R.id.setting_book_single);
        bookStatus.setOnClickListener(this);
        login_out_linear = (LinearLayout) findViewById(R.id.login_out_linear);
        setting_book_linear = (LinearLayout) findViewById(R.id.setting_book_linear);
        if (mUser == null) {
            login_out_linear.setVisibility(View.GONE);
        } else login_out_linear.setVisibility(View.VISIBLE);
        //        get_book_single = (TextView) findViewById(R.id.setting_get_book_single);
        //        get_book_single.setOnClickListener(this);
        findViewById(R.id.setting_auto_loop).setOnClickListener(this);
        findViewById(R.id.setting_wifi_auto_vedio).setOnClickListener(this);
        findViewById(R.id.settings_recommend).setOnClickListener(this);
        findViewById(R.id.nomal_question).setOnClickListener(this);
        findViewById(R.id.feed_back).setOnClickListener(this);
        findViewById(R.id.settings_login_out).setOnClickListener(this);
        findViewById(R.id.setting_update).setOnClickListener(this);
        findViewById(R.id.clear_cache).setOnClickListener(this);
        progressBar = findViewById(R.id.clear_cache_progress);
        versionRedPoint = (ImageView) findViewById(R.id.setting_update_red);
        if (DataHelper.getLastVersion(this) > Tools.getAppIntVersionName(this)) {
            versionRedPoint.setVisibility(View.VISIBLE);
        } else {
            versionRedPoint.setVisibility(View.GONE);
        }

        version = (TextView) findViewById(R.id.setting_version);
        initVersion();
        autoLoop = (EvaSwitchBar) findViewById(R.id.auto_loop_switch);
        autoLoop.setChecked(index_head_auto_loop);
        wifiVedio = (EvaSwitchBar) findViewById(R.id.wifi_auto_vedio_switch);
        wifiVedio.setChecked(wifi_auto_play_vedio);
        pushSwitch = (EvaSwitchBar) findViewById(R.id.setting_push_switch);

        pushSwitch.setChecked(ifPush);

        autoLoop.setOnChangeListener(this);
        wifiVedio.setOnChangeListener(this);
        pushSwitch.setOnChangeListener(this);

        //        findViewById(R.id.setting_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setting_auto_loop) {// 首页焦点图自动轮播

        } else if (v.getId() == R.id.setting_wifi_auto_vedio) {// Wi-Fi下自动播放视频

        } else if (v.getId() == R.id.settings_recommend) { // 向朋友推荐本应用
            setupRecommendPreference();
        } else if (v.getId() == R.id.nomal_question) {// 常见问题

        } else if (v.getId() == R.id.feed_back) {// 用户反馈
            if (SlateDataHelper.getUserLoginInfo(SettingActivity.this) != null) {// 已登录
                Intent i = new Intent(SettingActivity.this, FeedBackActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        } else if (v.getId() == R.id.setting_test) {
            //            startActivity(new Intent(SettingActivity.this, TestActivity.class));

            //            WebViewPop webViewPop = new WebViewPop(SettingActivity.this, "http://live.bbwc.cn/public/course/index.html?eventid=10000");
        } else if (v.getId() == R.id.setting_back) {
            finish();
        } else if (v.getId() == R.id.settings_login_out) {
            doLoginOut();
        } else if (v.getId() == R.id.setting_update) {
            if (canCheckUpdate) checkBbwc();

        } else if (v.getId() == R.id.clear_cache) {// 清理缓存
            progressBar.setVisibility(View.VISIBLE);
            UserTools.clearApplicationData(SettingActivity.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    showToast("缓存已清理完毕。");
                }
            }, 3000);
        }

    }


    /**
     * 更新版本
     */
    private void checkBbwc() {
        //googleplay 市场禁止检测版本更新
        if (CommonApplication.CHANNEL.equals("googleplay")) {
            return;
        }

        if (DataHelper.getLastVersion(this) == Tools.getAppIntVersionName(this)) {
            showToast("当前版本已是最新版本");
        } else {
            showLoadingDialog(true);
            canCheckUpdate = false;
            UpdateManager manager = new UpdateManager(this, new UpdateManager.CheckVersionListener() {
                @Override
                public void checkEnd() {
                    showLoadingDialog(false);
                    canCheckUpdate = true;
                }
            });
            manager.checkVersion();
        }


    }

    /**
     * 登出
     */
    private void doLoginOut() {
        // 清除存储的登录信息
        SlateDataHelper.clearLoginInfo(this);
        SlateDataHelper.cleanAddressId(this);
        AppValue.advTagList =null;
        // 清除金币信息
        if (mUser != null) {
            UserDataHelper.saveIsFirstUseCoin(this, mUser.getUid(), true);
            PayHttpsOperate.getInstance(this).youzanLogout(mUser.getUid());
            afterLoginOut();
        }
    }

    protected void afterLoginOut() {
        if (UserApplication.logOutListener != null) {
            UserApplication.logOutListener.onLogout();
        }
        SlateApplication.loginStatusChange = true;
        // 返回上级界面
        finish();
    }

    /**
     * 初始化活动列表页面
     */
    private void initActiveList() {
        UserOperateController.getInstance(this).getActiveList(SlateDataHelper.getUid(this), SlateDataHelper.getToken(this), new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                active = (Active) entry;
            }
        });
    }


    /**
     * 初始化订阅状态
     */
    private void initBookStatus() {
        if (mUser == null || mUser.getLevel() == 2 || mUser.getLevel() == 0) { //未登录 || vip || 小白

            setting_book_linear.setVisibility(View.GONE);
        } else {// 付费用户，显示已订阅
//            setting_book_linear.setVisibility(View.VISIBLE);//去掉
            long endtime = SlateDataHelper.getEndTimeByType(this, 1);
            if (SlateDataHelper.getLevelByType(this, 1)) {
                bookStatus.setText(String.format(this.getString(R.string.book_already), Utils.strToDate(endtime)));// 显示到期时间
            } else {
                bookStatus.setText(String.format(this.getString(R.string.book_daoqi), Utils.strToDate(endtime)));
            }
        }

    }

    /**
     * 获赠订阅
     */
    private void setupGetBookPreference() {
        if (SlateDataHelper.getUserLoginInfo(SettingActivity.this) != null) {
            if (active != null) {
                WebViewPop pop = new WebViewPop(SettingActivity.this, active.getUrl());
            }
        } else {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }
    }

    /**
     * 消息推送
     */
    private void setPush() {
        Toast.makeText(this, "消息推送", Toast.LENGTH_SHORT).show();
    }

    /**
     * 推荐给朋友
     */
    private void setupRecommendPreference() {

        ArticleItem articleItem = new ArticleItem();
        articleItem.setTitle(getResources().getString(R.string.preference_recommend_subject));
        articleItem.setDesc(getResources().getString(R.string.preference_recommend_text));
        articleItem.setWeburl("http://app.bbwc.cn/download.html");
        SharePopWindow sharePopWindow = new SharePopWindow(SettingActivity.this, articleItem);
        sharePopWindow.setIsShareApp(true);
    }

    /**
     * 初始化程序名称和版本信息
     */
    private void initVersion() {
        PackageManager packageManager = getPackageManager();
        try {
            final PackageInfo info = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            String versionText = info.applicationInfo.loadLabel(packageManager) + " " + info.versionName;
            if (ConstData.IS_DEBUG != 0) {
                versionText = versionText + "（" + "测试版" + "）";
            }
            version.setText(versionText);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

    }


    /**
     * 更新订阅状态用
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initstatus();
        initView();
        initBookStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
//        overridePendingTransition(R.anim.hold, R.anim.down_out);
    }

    @Override
    public void onChange(EvaSwitchBar buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.auto_loop_switch) {
            DataHelper.setIndexHeadAutoLoop(SettingActivity.this, isChecked);
        } else if (buttonView.getId() == R.id.wifi_auto_vedio_switch) {
            DataHelper.setWiFiAutoPlayVedio(SettingActivity.this, isChecked);
        } else if (buttonView.getId() == R.id.setting_push_switch) {
            if (isChecked) {// 开启推送
                DataHelper.setPushServiceEnable(this, true);
                resumePush();
            } else {// 关闭推送
                NewPushManager.getInstance(this).closePush(SettingActivity.this);
            }
        }

    }

    public void resumePush() {
        if (NewPushManager.getInstance(this).isMiUi()) {
            Log.e("恢复小米推送", "恢复小米推送");
            NewPushManager.getInstance(this).registerMiPush();
        } else {
            Log.e("极光恢复", "极光恢复推送");
            JPushInterface.resumePush(getApplicationContext());
            Log.e("极光恢复推送", JPushInterface.isPushStopped(getApplicationContext()) ? "停止" : "开启");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_API) {
                if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                    Tencent.handleResultData(data, iUiListener);
                }
            }
        }
    }

    @Override
    public Activity getActivity() {
        return SettingActivity.this;
    }

    @Override
    public String getActivityName() {
        return SettingActivity.class.getName();
    }

    @Override
    public void reLoadData() {

    }
}
