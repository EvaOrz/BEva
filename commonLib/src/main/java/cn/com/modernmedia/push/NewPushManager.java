package cn.com.modernmedia.push;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.jpush.android.api.JPushInterface;

/**
 * 推送管理类
 * Created by Eva. on 16/7/14.
 */
public class NewPushManager {
    private static Context mContext;

    private static NewPushManager instance;

    public static String tokenType; // token类型的缓存

    public static String token; // token的缓存

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PushType.MI, PushType.JPUSH})
    public @interface PushType {

        String MI = "MI", JPUSH = "JPUSH";
    }

    public NewPushManager(Context context) {
        this.mContext = context;
    }

    public static synchronized NewPushManager getInstance(Context context) {
        // 建立图集表
        if (null == instance) {
            instance = new NewPushManager(context);
        }
        return instance;
    }

    /**
     * 注册推送
     */
    public void register() {
        if (isMiUi()) {
            Log.e("注册小米推送", "注册小米推送");
            registerMiPush();
        } else {
            Log.e("注册极光推送", "注册极光推送");
            registerJPush();
        }
        DataHelper.setPushServiceEnable(mContext, true);
    }

    /**
     * setting 页面关闭推送
     */
    public void closePush(Context context) {
        if (isMiUi()) {
            Log.e("反注册小米推送", "反注册小米推送");
            MiPushClient.unregisterPush(context);
        } else {
            Log.e("反注册极光推送", "反注册极光推送");
            JPushInterface.stopPush(context.getApplicationContext());
        }
        DataHelper.setPushServiceEnable(context, false);
    }

    /**
     * 注册小米push服务
     * 注册成功后会向{@link MiPushReceiver}发送广播
     */
    public void registerMiPush() {
        if (ConstData.IS_DEBUG != 0) {// 非正式环境
            MiPushClient.checkManifest(mContext);
        }

        if (isInMainProcess()) {
            MiPushClient.registerPush(mContext, SlateApplication.mConfig.getXiaomi_push_appid(), SlateApplication.mConfig.getXiaomi_push_appkey());
        }

        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
            }

            @Override
            public void log(String content) {
            }
        };
        Logger.setLogger(mContext, newLogger);

        if (ConstData.IS_DEBUG != 0) {
            Logger.disablePushFileLog(mContext);
        }
    }

    /**
     * mainactivity的onresume 生命周期需要添加第三方的统计数据
     */
    public void onresume(Context context) {
        // 关闭推送状态时，不需要onresume
        if (!DataHelper.isPushServiceEnable(context)) return;
        if (isMiUi()) {
            MiPushClient.resumePush(context, "");
        } else {
            Log.e("极光恢复", "极光恢复推送");
            JPushInterface.onResume(context);
        }
    }

    /**
     * mainactivity的onpause 生命周期需要添加第三方的统计数据
     *
     * @param context
     */
    public void onpause(Activity context) {
        if (isMiUi()) {
            MiPushClient.pausePush(context, "");
        } else {
            JPushInterface.onPause(context);
        }
    }

    /**
     * 注册极光的推送服务
     */
    private void registerJPush() {
        // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
        JPushInterface.setDebugMode(false);//设置调试模式
        JPushInterface.init(mContext.getApplicationContext());

    }

    /**
     * 目前一个设备仅仅有一个token，所以用了静态的string做缓存；
     * 做如果以后有多个token，那么把{@link NewPushManager}变成单例，存一个token的map即可。
     */
    public static void sendDeviceToken(@PushType String type, String token) {
        if (token == null) {
            return;
        }
        NewPushManager.tokenType = type;
        NewPushManager.token = token;
        if (!TextUtils.isEmpty(token)) {
            SlateDataHelper.setPushToken(mContext, token);
        }

        /**
         * 没注册过，向服务器上传注册id
         */
        OperateController.getInstance(mContext).pushDeviceInfo(mContext, token, type, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                /**
                 * 记录已注册成功的状态
                 */
                DataHelper.setPushServiceEnable(mContext, true);
            }
        });

    }

    /**
     * 判断room是否是miui，用于选择推送系统
     *
     * @return
     */
    public boolean isMiUi() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    /**
     * 判断当前activity是否在main进程中
     * <p/>
     * 注意：因为推送服务等设置为运行在另外一个进程，这导致本Application会被实例化两次。
     * 而有些操作我们需要让应用的主进程时才进行，所以用到了这个方法
     */
    public boolean isInMainProcess() {
        ActivityManager am = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        String mainProcessName = mContext.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取推送文章url
     *
     * @param jsonString
     * @return
     */
    private String newParseArticleUrl(String jsonString) {
        String url = "";
        try { /*
         * {"alert":"新接口",
		 * "na":"1111-11-11","na_tag":"cat_11-10043332","level":"0"}
		 */
            JSONObject json = new JSONObject(jsonString);
            String na = json.optString("na_tag", "");
            int level = Integer.valueOf(json.optString("level"));// ibloomberg3.3新增推送付费文章

            if (!TextUtils.isEmpty(na)) {
                String[] arr = UriParse.parsePush(na);
                if (arr != null && arr.length == 2) {
                    // 需要显示推送文章
                    url = UrlMaker.getPushArticle(arr[1]);
                }
            }
        } catch (JSONException e) {

        }
        Log.e("push ariticle url", url);
        return url;
    }

    public static void gotoArticle(String jsonString) {
        String articleId = "";
        try { /*
         * {"alert":"新接口",
		 * "na":"1111-11-11","na_tag":"cat_11-10043332","level":"0"}
		 */
            JSONObject json = new JSONObject(jsonString);
            String na = json.optString("na_tag", "");

            if (!TextUtils.isEmpty(na)) {
                String[] arr = UriParse.parsePush(na);
                if (arr != null && arr.length == 2) {
                    // 需要显示推送文章
                    articleId = arr[1];
                }
            }
        } catch (JSONException e) {

        }
        Log.e("推送文章地址", articleId);
        if (!TextUtils.isEmpty(articleId)) {
            Intent intent = new Intent(mContext, CommonApplication.pushArticleCls);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, articleId);
            mContext.startActivity(intent);
        } else if (CommonApplication.splashCls != null) {
            Intent i = new Intent(mContext, CommonApplication.splashCls);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }
    }


}