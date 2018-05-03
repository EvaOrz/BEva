package cn.com.modernmedia.businessweek;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.taobao.hotfix.HotFixManager;
import com.taobao.hotfix.PatchLoadStatusListener;
import com.taobao.hotfix.util.PatchStatusCode;
import com.youzan.androidsdk.YouzanSDK;
import com.youzan.androidsdk.basic.YouzanBasicSDKAdapter;

import cn.com.modernmedia.ArticleGalleryActivity;
import cn.com.modernmedia.common.WeixinShare;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.sina.SinaConstants;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmediausermodel.ActiveActivity;
import cn.com.modernmediausermodel.util.UserConstData;
import io.fabric.sdk.android.Fabric;


/**
 * 全局变量
 *
 * @author ZhuQiao
 */
public class MyApplication extends ViewsApplication {

    // 1--商周简体，18---商周繁体
    public static int APPID = 1;
    public static int DEBUG = 1;

    public static MusicService musicService;// 全局电台播放Service

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ConstData.setAppId(APPID, DEBUG);
        UserConstData.setAppId(APPID, DEBUG);
        drawCls = R.drawable.class;
        stringCls = R.string.class;
        mainCls = MainActivity.class;
        activeCls = ActiveActivity.class;
        articleGalleryCls = ArticleGalleryActivity.class;
        splashCls = SplashScreenActivity.class;
        if (CHANNEL.equals("googleplay")) {
            SinaConstants.APP_KEY = mConfig.getWeibo_app_id_goole();
            WeixinShare.APP_ID = mConfig.getWeixin_app_id_google();
        }

        mContext = this.getApplicationContext();
        initFabric();
        initHotFix();
        initYouZanYun();

    }

    /**
     * 初始化有赞云
     */
    private void initYouZanYun() {
        YouzanSDK.init(this, mConfig.getYouzan_client_id(), new YouzanBasicSDKAdapter());
    }

    /**
     * 初始化fabric
     */
    private void initFabric() {
        //        Crashlytics crashlytics = new Crashlytics.Builder()
        //                .delay(1)
        //                .listener(createCrashlyticsListener())
        //                .pinningInfo(createPinningInfoProvider())
        //                .build();

        /**
         * 开启debug模式
         */
        final Fabric fabric = new Fabric.Builder(this).kits(new Crashlytics()).debuggable(false).build();

        Fabric.with(fabric);
        Fabric.with(this, new Answers());
    }

    /**
     * 初始化HotFix
     */
    private void initHotFix() {
        HotFixManager.getInstance().setContext(this).setAppVersion(String.valueOf(ConstData.VERSION)).setAppId(String.valueOf(94557 - 1)).setAesKey(null).setSupportHotpatch(true).setEnableDebug(true).setPatchLoadStatusStub(new PatchLoadStatusListener() {
            @Override
            public void onload(final int mode, final int code, final String info, final int handlePatchVersion) {
                // 补丁加载回调通知
                if (code == PatchStatusCode.CODE_SUCCESS_LOAD) {
                    Log.e("补丁下载成功1", "" + code);
                    // TODO: 10/24/16 表明补丁加载成功
                } else if (code == PatchStatusCode.CODE_ERROR_NEEDRESTART) {
                    // TODO: 10/24/16 表明新补丁生效需要重启. 业务方可自行实现逻辑, 提示用户或者强制重启, 建议: 用户可以监听进入后台事件, 然后应用自杀

                    Log.e("补丁下载成功2", "" + code);
                } else if (code == PatchStatusCode.CODE_ERROR_INNERENGINEFAIL) {
                    // 内部引擎加载异常, 推荐此时清空本地补丁, 但是不清空本地版本号, 防止失败补丁重复加载
                    //HotFixManager.getInstance().cleanPatches(false);
                    Log.e("补丁下载成功", "" + code);
                } else {
                    // TODO: 10/25/16 其它错误信息, 查看PatchStatusCode类说明
                }
            }
        }).initialize();
    }
}
