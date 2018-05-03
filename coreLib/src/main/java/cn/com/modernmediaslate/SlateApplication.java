package cn.com.modernmediaslate;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;

import net.tsz.afinal.FinalBitmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.com.modernmediaslate.listener.CardUriListener;
import cn.com.modernmediaslate.model.Configuration;
import cn.com.modernmediaslate.unit.FavObservable;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlatePrintHelper;

public class SlateApplication extends Application {
    public static Context mContext;
    /**
     * 应用id bloomberg:1; modernlady:2; icollection:3; turningpoints:5; adv:6;
     * onewaystreet:10; zhihu:11; tanc:12; itv:13; life:14; car:15; lohas:16;
     * finance:17; bloomberg繁体版:18; iweekly:20; ifashion:21; verycity 37; iCenCi
     * 104; youthMba 108; zhihuiyun:110;wezeit:105
     **/
    public static int APP_ID;
    /**
     * 没有成功同步到服务器上时使用当前uid,为了兼顾以前没有uid字段的数据，默认为0
     */
    public static final String UN_UPLOAD_UID = "0";
    public static int width;
    public static int height;
    public static float density;
    public static Map<String, Activity> activityMap = new HashMap<String, Activity>();
    public static Configuration mConfig = new Configuration();

    private static int memorySize;
    public static FinalBitmap finalBitmap;
    private static SlateApplication instance;
    private List<Activity> mList = new LinkedList<Activity>();
    /**
     * 图片保存地址
     **/
    public static String DEFAULT_IMAGE_PATH;
    /**
     * 视频保存地址
     */
    public static String DEFAULT_VIDEO_PATH;
    /**
     * 收藏模块观察者
     */
    public static FavObservable favObservable = new FavObservable();
    public static CardUriListener userUriListener;

    public static Class<?> drawCls;// 图片资源class
    public static Class<?> stringCls;// string资源class
    public static Class<?> idsCls;// id资源class
    public static Class<?> mainCls;// MainActivity
    public static Class<?> articleCls;// ArticleActivity
    public static Class<?> splashCls;// SplashActivity

    /**
     * 登录状态是否改变
     */
    public static boolean loginStatusChange = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initScreenInfo();
    }

    /**
     * 初始化页面信息
     */
    public void initScreenInfo() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        // 获取屏幕密度
        density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0
        initMemorySize();
        parseConfiguration();
    }

    private void initMemorySize() {
        final int memoryClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        memorySize = memoryClass * 1024 * 1024 / 8;
        initImageLoader(mContext);
    }

    /**
     * 初始化整个app的配置
     *
     * @param context
     */
    private static void initImageLoader(Context context) {
        finalBitmap = FinalBitmap.create(context); // 初始化
        finalBitmap.configBitmapLoadThreadSize(3);// 定义线程数量

        finalBitmap.configMemoryCacheSize(memorySize);// 设置缓存大小
    }

    public synchronized static SlateApplication getInstance() {
        if (null == instance) {
            instance = new SlateApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exitActivity() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addActivity(String name, Activity activity) {
        if (TextUtils.isEmpty(name) || activity == null) return;
        if (activityMap.containsKey(name)) {
            if (activityMap.get(name) != null) activityMap.get(name).finish();
            activityMap.remove(name);
        }
        activityMap.put(name, activity);
    }

    public static void removeActivity(String name) {
        if (TextUtils.isEmpty(name)) return;
        if (!activityMap.isEmpty() && activityMap.containsKey(name)) {
            activityMap.remove(name);
        }
    }

    public static void removeActivityExcept(String name) {
        if (TextUtils.isEmpty(name)) return;
        if (!activityMap.isEmpty()) {
            for (String key : activityMap.keySet()) {
                System.out.println(key);
                if (key.equals(name)) continue;
                Activity activity = activityMap.get(key);
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    public static void slateExit() {
        SlatePrintHelper.print("SlateApplication exit");
        if (!activityMap.isEmpty()) {
            for (String key : activityMap.keySet()) {
                Activity activity = activityMap.get(key);
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
        activityMap.clear();
        loginStatusChange = false;
    }


    /**
     * 解析应用整体配置文件
     *
     * @return
     */
    private void parseConfiguration() {
        InputStream is = null;
        try {
            is = mContext.getAssets().open("configuration.plist");
            NSDictionary rootDic = (NSDictionary) PropertyListParser.parse(is);
            mConfig.setHas_sina(ParseUtil.getIntValue(rootDic, "has_sina", 1));
            mConfig.setHas_weixin(ParseUtil.getIntValue(rootDic, "has_weixin", 1));
            mConfig.setHas_qq(ParseUtil.getIntValue(rootDic, "has_qq", 0));
            mConfig.setIs_index_pager(ParseUtil.getIntValue(rootDic, "is_index_pager", 1));
            mConfig.setHas_coin(ParseUtil.getIntValue(rootDic, "has_coin", 0));
            mConfig.setHas_single_view(ParseUtil.getIntValue(rootDic, "has_single_view", 0));
            mConfig.setFlurry_api_key(ParseUtil.getStringValue(rootDic, "flurry_api_key", ""));
            mConfig.setParse_app_id(ParseUtil.getStringValue(rootDic, "parse_app_id", ""));
            mConfig.setParse_client_id(ParseUtil.getStringValue(rootDic, "parse_client_id", ""));
            mConfig.setWeixin_app_id(ParseUtil.getStringValue(rootDic, "weixin_app_id", ""));
            mConfig.setWeixin_app_secret(ParseUtil.getStringValue(rootDic, "weixin_app_secret", ""));
            mConfig.setWeixin_partner_id(ParseUtil.getStringValue(rootDic, "weixin_partner_id", ""));
            mConfig.setWeixin_app_id_google(ParseUtil.getStringValue(rootDic, "weixin_app_id_google", ""));
            mConfig.setWeibo_app_id(ParseUtil.getStringValue(rootDic, "weibo_app_id", ""));
            mConfig.setWeibo_app_id_goole(ParseUtil.getStringValue(rootDic, "weibo_app_id_google", ""));
            mConfig.setQq_app_id(ParseUtil.getStringValue(rootDic, "qq_app_id", ""));
            mConfig.setCache_file_name(ParseUtil.getStringValue(rootDic, "cache_file_name", ""));
            mConfig.setTemplate_version(ParseUtil.getIntValue(rootDic, "template_version", 1));
            mConfig.setHas_subscribe(ParseUtil.getIntValue(rootDic, "has_subscribe", 0));
            mConfig.setNav_hide(ParseUtil.getIntValue(rootDic, "nav_hide", 0));
            mConfig.setAlign_bar(ParseUtil.getIntValue(rootDic, "align_bar", 0));
            mConfig.setWeibo_uid(ParseUtil.getIntValue(rootDic, "weibo_uid", 0));
            mConfig.setWeixin_public_number(ParseUtil.getStringValue(rootDic, "weixin_public_number", ""));
            mConfig.setIs_navbar_bg_change(ParseUtil.getIntValue(rootDic, "is_navbar_bg_change", 0));
            mConfig.setUmeng_key(ParseUtil.getStringValue(rootDic, "umeng_key", ""));
            mConfig.setXiaomi_push_appid(ParseUtil.getStringValue(rootDic, "xiaomi_push_appid", ""));
            mConfig.setXiaomi_push_appkey(ParseUtil.getStringValue(rootDic, "xiaomi_push_appkey", ""));
            mConfig.setXiaomi_push_appsecret(ParseUtil.getStringValue(rootDic, "xiaomi_push_appsecret", ""));
            mConfig.setYouzan_client_id(ParseUtil.getStringValue(rootDic, "youzan_client_id", ""));
            mConfig.setYouzan_client_secret(ParseUtil.getStringValue(rootDic, "youzan_client_secret", ""));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
