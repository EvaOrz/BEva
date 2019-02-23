package cn.com.modernmedia.newtag.mainprocess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.newtag.mainprocess.BaseTagMainProcess.ProcessState;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.unit.TimeCollectUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 新接口主页流程管理器
 *
 * @author zhuqiao
 */
public class TagProcessManage {

    public static final String KEY_PUSH_ARTICLE_ID = "key_push_article_id";
    public static final String KEY_PUSH_ARTICLE = "key_push_article";
    public static final String KEY_PUSH_ARTICLE_LEVEL = "key_push_article_level";


    private Context mContext;

    private static TagProcessManage instance = null;

    /**
     * http流程
     */
    private TagMainProcessHttp processHttp;
    /**
     * 缓存流程
     */
    private TagMainProcessCache processCache;

    /**
     * app是否正在运行
     */
    private boolean appIsRunning = false;

    /**
     * 推送文章地址
     */
    private String pushArticleId = "";
    private int pushArticleLevel = 0;
    /**
     * splash页回调
     */
    private SplashCallback splashCallback;

    private TagProcessManage() {
    }

    public static TagProcessManage getInstance(Context context) {
        if (instance == null) {
            instance = new TagProcessManage();
        }
        instance.setContext(context);
        return instance;
    }

    /**
     * 销毁单例
     */
    public void destory() {
        processHttp = null;
        processCache = null;
        appIsRunning = false;
        BaseTagMainProcess.clear();

        instance = null;
    }

    private void setContext(Context context) {
        mContext = context;
    }

    public TagMainProcessCache getProcessCache() {
        return processCache;
    }

    public TagMainProcessHttp getProcessHttp() {
        return processHttp;
    }

    /**
     * 主流程回调
     *
     * @author zhuqiao
     */
    public interface MainProcessParseCallBack {
        /**
         * 结束http流程
         *
         * @param success
         */
        public void afterFetchHttp(boolean success);

        /**
         * 结束cache流程
         *
         * @param success
         */
        public void afterFetchCache(boolean success);
    }


    /**
     * 推送回调
     *
     * @author zhuqiao
     */
    public interface PushCallback {
        /**
         * 结束parse流程
         *
         * @param fromHtmlUrl   从网页来的链接
         * @param pushArticleId 推送文章id;如果url不为空，代表需要显示推送文章
         * @param level         0:免费文章；1：付费文章。
         */
        public void afterParseProcess(String fromHtmlUrl, String pushArticleId, int level);

        /**
         * 关闭了推送文章页
         */
        public void onPushArticleFinished();
    }

    /**
     * splash页回调
     *
     * @author zhuqiao
     */
    public interface SplashCallback {

        /**
         * parse消息分析结束
         *
         * @param isPush        是否是push
         * @param pushArticleId 推送文章地址
         * @param level         文章付费情况
         * @param isAppRunning  app是否运行中
         */
        public void onParseMsgAnalyed(boolean isPush, String pushArticleId, int level, boolean isAppRunning);

    }

    private MainProcessParseCallBack callBack = new MainProcessParseCallBack() {

        @Override
        public void afterFetchHttp(boolean success) {
            if (processHttp == null) {
                return;
            }

            if (processCache == null) return;

            ProcessState cacheState = processCache.getProcessState();
            if (success) {
                // http流程成功
                if (cacheState.isEnd) {
                    TimeCollectUtil.getInstance().savePageTime(TimeCollectUtil.EVENT_LAUNCH_APP, false);
                    if (cacheState.isSuccess) showToast(true);
                } else {
                    showToast(false);
                }
            } else {
                // http流程未成功
                if (cacheState.isEnd && !cacheState.isSuccess) {
                    // NOTE 如果缓存流程走完并且没有成功，那么显示错误页面
                    processCache.showError();
                }
            }
        }

        @Override
        public void afterFetchCache(boolean success) {
            if (processHttp == null || processCache == null) {
                return;
            }

            ProcessState httpState = processHttp.getProcessState();
            if (success) {
                // cache流程成功
                if (httpState.isEnd) {
                    TimeCollectUtil.getInstance().savePageTime(TimeCollectUtil.EVENT_LAUNCH_APP, false);
                    if (httpState.isSuccess) showToast(true);
                } else {
                    showToast(false);
                }
            } else {
                // cache流程未成功
                if (httpState.isEnd && !httpState.isSuccess) {
                    // NOTE 如果http流程走完并且没有成功，那么显示错误页面
                    processCache.showError();
                }
            }
        }

    };

    private PushCallback pushCallback = new PushCallback() {

        @Override
        public void afterParseProcess(String fromHtmlUrl, String id, int level) {
            Log.e("afterParseProcess", level + "");
            // NOTE 标记是否需要显示推送文章
            pushArticleId = id;
            pushArticleLevel = level;
            if (splashCallback != null)
                splashCallback.onParseMsgAnalyed(true, pushArticleId, level, appIsRunning);
        }

        @Override
        public void onPushArticleFinished() {
        }
    };

    /**
     * 修复弹两次bug
     */
    public void showpushOrurl() {
        if (pushArticleLevel == -1) {
            //            UriParse.clickSlate(mContext, pushArticleUrl, new Entry[]{new ArticleItem()}, null, new Class<?>[0]);
        } else showPushArticleActivity(mContext, pushArticleId, pushArticleLevel);
    }


    /**
     * 开始流程
     *
     * @param intent         splash页和main页获得的intent
     * @param splashCallback splash回调
     */
    public void onStart(final Intent intent, SplashCallback splashCallback) {
        this.splashCallback = splashCallback;
        if (null == intent || intent.getExtras() == null) {
            // splash
            Log.e("from splash start http", "from splash start http");
            if (splashCallback != null) splashCallback.onParseMsgAnalyed(false, "", 0, false);
        } else {
            Log.e("push itent", intent.getExtras() + "");
            String from = intent.getExtras().getString(GenericConstant.FROM_ACTIVITY);
            if (TextUtils.equals(from, GenericConstant.FROM_ACTIVITY_VALUE)) {
                // 首页
                Log.e("from main start cache", "from main start cache");
                fecthFromCache();
                appIsRunning = true;
            } else {
                // splash push
                Log.e("from splash start push", "from splash start push");
                parsePush(intent);

            }
        }
    }

    private void printStartLog(String msg) {
        PrintHelper.print("===========" + msg + "===========");
    }

    /**
     * 加载缓存数据
     */
    private void fecthFromCache() {
        processCache = new TagMainProcessCache(mContext, callBack);
        processCache.onStart();
    }

    /**
     * 加载服务器数据
     */
    public void fetchFromHttp() {
        processHttp = new TagMainProcessHttp(mContext, callBack);
        processHttp.onStart();
    }

    /**
     * 推送
     */
    private void parsePush(Intent intent) {
        new TagMainProcessParse(mContext, callBack).onStart(intent, pushCallback);
    }


    /**
     * 显示push文章页
     *
     * @param context
     * @param id
     */
    public void showPushArticleActivity(Context context, String id, int level) {

        String _id = TextUtils.isEmpty(id) ? pushArticleId : id;
        int _level = (level == 0 ? pushArticleLevel : level);
        if (context == null || TextUtils.isEmpty(_id) || CommonApplication.pushArticleCls == null)
            return;

        Intent intent = new Intent(context, CommonApplication.pushArticleCls);
        intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, _id);
        intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL, _level);
        context.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.down_in, R.anim.hold);
    }

    public void onPushArticleActivityFinished() {
        if (pushCallback != null) {
            pushCallback.onPushArticleFinished();
        }
    }

    public void reLoadData() {
        if (processCache == null) return;
        // 只有cache流程才会有错误页展示，所以更改cache流程的apiType
        final FetchApiType type = FetchApiType.USE_HTTP_FIRST;
        processCache.setApiType(type);
        switch (processCache.errorType) {
            case 1:
                processCache.onStart();
                break;
            case 2:
                processCache.getCatList(type);
                break;
            case 5:
                processCache.getSubscribeList(type);
                break;
            case 6:
                processCache.getAdvList(type);
                break;
            default:
                break;
        }
    }

    private void showToast(boolean isEnd) {
        if (ConstData.getAppId() == 20) return;
        Tools.showToast(mContext, isEnd ? R.string.fecthing_ok : R.string.fecthing_http);
    }


    public boolean isAppIsRunning() {
        return appIsRunning;
    }
}
