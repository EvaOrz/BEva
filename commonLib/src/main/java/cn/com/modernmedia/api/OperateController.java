package cn.com.modernmedia.api;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import cn.com.modernmedia.api.GetTagInfoOperate.TAG_TYPE;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.SubscribeOrderList.SubscribeColumn;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.util.TagDataHelper;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.model.Entry;

/**
 * 接口控制
 *
 * @author ZhuQiao
 */
public class OperateController {

    private static OperateController instance;
    private static Context mContext;

    private Handler mHandler = new Handler();

    private OperateController(Context context) {
        mContext = context;
    }

    public static synchronized OperateController getInstance(Context context) {
        mContext = context;
        if (instance == null) instance = new OperateController(context);
        return instance;
    }

    private static interface AfterCallBack {
        public void afterCallBack(Entry entry, boolean fromHttp);
    }

    private void doRequest(BaseOperate operate, Entry entry, FetchApiType type, FetchEntryListener listener) {
        doRequest(operate, entry, type, listener, null);
    }

    private void doRequest(BaseOperate operate, final Entry entry, FetchApiType type, final FetchEntryListener listener, final AfterCallBack afterCallBack) {
        operate.asyncRequest(mContext, type, new DataCallBack() {

            @Override
            public void callback(boolean success, boolean fromHttp) {
                sendMessage(success ? entry : null, listener, fromHttp, afterCallBack);
            }
        });
    }

    private void doPostRequest(BaseOperate operate, Entry entry, FetchApiType type, FetchEntryListener listener) {
        doPostRequest(operate, entry, type, listener, null);
    }

    private void doPostRequest(BaseOperate operate, final Entry entry, FetchApiType type, final FetchEntryListener listener, final AfterCallBack afterCallBack) {
        operate.asyncRequestByPost(mContext, type, new DataCallBack() {

            @Override
            public void callback(boolean success, boolean fromHttp) {
                sendMessage(success ? entry : null, listener, fromHttp, afterCallBack);
            }
        });
    }

    /**
     * 返回给ui层
     *
     * @param entry
     * @param listener
     * @param fromHttp
     * @param afterCallBack
     */
    private void sendMessage(final Entry entry, final FetchEntryListener listener, final boolean fromHttp, final AfterCallBack afterCallBack) {
        Thread thread = Thread.currentThread();
        Log.i("====", "run: " + entry + "...thread" + thread);
        synchronized (mHandler) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    listener.setData(entry);
                    String s = new Gson().toJson(entry);
                    if (afterCallBack != null) afterCallBack.afterCallBack(entry, fromHttp);
                }
            });
        }
    }

    /**
     * 获取统计装机量是否成功
     *
     * @param listener
     */
    public void getDown(FetchEntryListener listener) {
        DownOperate operate = new DownOperate(mContext);
        doRequest(operate, operate.getDown(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 判断版本号
     *
     * @param listener
     */
    public void checkVersion(String v, FetchEntryListener listener) {
        CheckVersionOperate operate = new CheckVersionOperate(v);
        doRequest(operate, operate.getVersion(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 获取天气预报
     *
     * @param longitude 经度
     * @param latitude  纬度
     */
    public void getWeather(double longitude, double latitude, FetchEntryListener listener) {
        GetWeatherOperate operate = new GetWeatherOperate(longitude, latitude);
        doRequest(operate, operate.getWeather(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 获取最新未读文章id
     *
     * @param listener
     */
    public void getLatestArticleIds(String tagName, FetchApiType type, FetchEntryListener listener) {
        GetLatestArticleIdOperate operate = new GetLatestArticleIdOperate(mContext, tagName);
        doRequest(operate, operate.getmLastestArticleId(), type, listener);
    }

    /**
     * 获取广告列表
     *
     * @param type
     * @param listener
     */
    public void getAdvList(FetchApiType type, FetchEntryListener listener) {
        GetAdvListOperate operate = new GetAdvListOperate();
        operate.setShowToast(type != FetchApiType.USE_CACHE_ONLY);
        doRequest(operate, operate.getAdvList(), type, listener);
    }

    /**
     * 获取第三方广告
     */
    public void getOtherAdv(FetchApiType type, FetchEntryListener listener){
        GetOtherAdvOperate operate = new GetOtherAdvOperate();

        doRequest(operate,operate.getOtherAdvList(),type,listener);
    }

    /**
     * 获取应用信息
     *
     * @param listener
     */
    public void getAppInfo(FetchApiType type, FetchEntryListener listener) {
        getTagInfo("", "", "1", "", TAG_TYPE.APP_INFO, type, listener);
    }

    /**
     * 获取tag信息 默认取缓存，如果应用更新时间变了，会统一清缓存(下拉刷新不使用缓存)
     *
     * @param listener
     */
    public void getTagInfo(String tagName, FetchApiType type, FetchEntryListener listener) {
        GetTagInfoOperate operate = new GetTagInfoOperate("", tagName, "", "", TAG_TYPE.TAG_INFO);
        doRequest(operate, operate.getTagInfoList(), type, listener);
    }

    /**
     * 获取子栏目信息 默认取缓存，如果应用更新时间变了，会统一清缓存
     *
     * @param listener
     */
    public void getChildTagInfo(String parentTagName, FetchEntryListener listener) {
        GetTagInfoOperate operate = new GetTagInfoOperate(parentTagName, "", "", "", TAG_TYPE.CHILD_CAT);
        doRequest(operate, operate.getTagInfoList(), FetchApiType.USE_CACHE_FIRST, listener);
    }

    /**
     * @param parentTagName 父类标签
     * @param tagName       标签
     * @param group         分组
     * @param top
     * @param type          是否获取child,tree或者其他
     * @param listener
     */
    public void getTagInfo(String parentTagName, String tagName, String group, String top, TAG_TYPE type, FetchApiType fetchType, FetchEntryListener listener) {
        GetTagInfoOperate operate = new GetTagInfoOperate(parentTagName, tagName, group, top, type);
        doRequest(operate, operate.getTagInfoList(), fetchType, listener);
    }

    /**
     * 获取往期列表
     *
     * @param top
     * @param listener
     */
    public void getLastIssueList(String top, FetchEntryListener listener) {
        GetLastIssueListOperate operate = new GetLastIssueListOperate(top);
        doRequest(operate, operate.getTagInfoList(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取往期的子栏目
     *
     * @param tagName
     * @param listener
     */
    public void getLastIssueCats(String tagName, FetchEntryListener listener) {
        GetLastIssueCatsOperate operate = new GetLastIssueCatsOperate(tagName);
        doRequest(operate, operate.getTagInfoList(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取一个或者多个栏目的文章列表
     *
     * @param tagInfo  tag统一标识，可以传多个tagname,用逗号隔开
     * @param top      用于分页，取下一页$top=最后一个标签的offset
     * @param limited  用于分页，取上一页$bottom=第一个标签的offset
     * @param listener
     */
    public void getTagArticles(Context c, final TagInfo tagInfo, String top, String limited, TagArticleList articleList, FetchEntryListener listener) {
        boolean useCache = !TextUtils.equals(limited, "5") && TextUtils.equals(tagInfo.getArticleupdatetime(), TagDataHelper.getCatArticleUpdateTime(mContext, tagInfo.getTagName()));
        FetchApiType type = useCache ? FetchApiType.USE_CACHE_FIRST : FetchApiType.USE_HTTP_FIRST;
        getTagArticles(c, tagInfo, top, limited, articleList, type, listener);
    }

    public void getTagArticles(Context c, final TagInfo tagInfo, String top, String limited, TagArticleList articleList, FetchApiType type, FetchEntryListener listener) {
        final GetTagArticlesOperate operate = new GetTagArticlesOperate(c, tagInfo, top, limited, articleList);
        doRequest(operate, operate.getArticleList(), type, listener, new AfterCallBack() {

            @Override
            public void afterCallBack(Entry entry, boolean fromHttp) {
                if (entry != null && fromHttp) {
                    TagDataHelper.setCatArticleUpdateTime(mContext, tagInfo.getTagName(), tagInfo.getArticleupdatetime());
                }
            }
        });
    }

    /**
     * 获取往期栏目的文章列表
     *
     * @param tagName     tag统一标识，可以传多个tagname,用逗号隔开
     * @param top         用于分页，取下一页$top=最后一个标签的offset
     * @param publishTime 发布时间
     * @param listener
     */
    public void getLastIssueArticles(String lastIssueTag, String tagName, String top, String publishTime, FetchApiType type, FetchEntryListener listener) {
        GetLastIssueArticlesOperate operate = new GetLastIssueArticlesOperate(lastIssueTag, tagName, top, publishTime);
        doRequest(operate, operate.getArticleList(), type, listener);
    }

    /**
     * 获取栏目首页数据
     *
     * @param tagInfo
     * @param listener
     */
    public void getTagIndex(final TagInfo tagInfo, String top, String limited, TagArticleList articleList, FetchEntryListener listener) {
        boolean useCache = !TextUtils.equals(limited, "5") && TextUtils.equals(tagInfo.getColoumnupdatetime(), TagDataHelper.getCatIndexUpdateTime(mContext, tagInfo.getTagName()));
        FetchApiType type = useCache ? FetchApiType.USE_CACHE_FIRST : FetchApiType.USE_HTTP_FIRST;
        getTagIndex(tagInfo, top, limited, articleList, type, listener);
    }

    /**
     * 获取栏目首页数据
     *
     * @param tagInfo
     * @param top
     * @param limited
     * @param articleList
     * @param type
     * @param listener
     */
    public void getTagIndex(final TagInfo tagInfo, String top, String limited, TagArticleList articleList, FetchApiType type, FetchEntryListener listener) {
        GetTagIndexOperate operate = new GetTagIndexOperate(mContext, tagInfo, top, limited, articleList);
        doRequest(operate, operate.getArticleList(), type, listener, new AfterCallBack() {

            @Override
            public void afterCallBack(Entry entry, boolean fromHttp) {
                if (entry != null && fromHttp) {
                    TagDataHelper.setCatIndexUpdateTime(mContext, tagInfo.getTagName(), tagInfo.getColoumnupdatetime());
                }
            }
        });
    }

    /**
     * 取出用户栏目订阅列表
     *
     * @param uid
     */
    public void getSubscribeOrderList(String uid, String token, FetchApiType type, FetchEntryListener listener) {
        GetUserSubscribeListOpertate operate = new GetUserSubscribeListOpertate(uid, token);
        doRequest(operate, operate.getSubscribeOrderList(), type, listener);
    }

    /**
     * 更新用户订阅的栏目列表
     *
     * @param list     用户订阅的所有栏目列表
     * @param listener
     */
    public void saveSubscribeColumnList(String uid, String token, List<SubscribeColumn> list, FetchEntryListener listener) {
        SaveUserSubscribeListOpertate operate = new SaveUserSubscribeListOpertate(uid, token, list);
        doPostRequest(operate, operate.getError(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 更新用户订阅的单个栏目
     *
     * @param data     用户订阅的单个栏目
     * @param listener
     */
    public void saveSubscribeColumnSingle(String uid, String token, SubscribeColumn data, FetchEntryListener listener) {
        SaveUserSubcribeSingleOperate operate = new SaveUserSubcribeSingleOperate(uid, token, data);
        doPostRequest(operate, operate.getError(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 获取服务器数据
     *
     * @param uid
     * @param listener
     */
    public void getFav(String uid, FetchEntryListener listener) {
        final UserGetFavOperate operate = new UserGetFavOperate(uid);
        doRequest(operate, operate.getFavorite(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 同步收藏
     */
    public void updateFav(String uid, int appid, List<ArticleItem> list, FetchEntryListener listener) {
        if (list == null || list.size() == 0) sendMessage(null, listener, false, null);
        final UserUpdateFavOperate operate = new UserUpdateFavOperate(uid, appid, list);
        doPostRequest(operate, operate.getFavorite(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 获取文章详情
     *
     * @param articleId
     * @param listener
     */
    public void getArticleDetails(int articleId, FetchEntryListener listener) {
        GetArticleDetailsOperate operate = new GetArticleDetailsOperate(articleId, false);
        doRequest(operate, operate.getArticleList(), FetchApiType.USE_HTTP_FIRST, listener);
    }

    /**
     * 获取文章html文本
     *
     * @param articleId
     * @param listener
     */
    public void getArticleHtml(int articleId, FetchEntryListener listener) {
        GetArticleDetailsOperate operate = new GetArticleDetailsOperate(articleId, true);
        doRequest(operate, operate.getArticleList(), FetchApiType.USE_HTTP_FIRST, listener);

    }

    /**
     * 添加日志(参数方式)
     */
    public void addSelection(Context context, JSONObject array, FetchEntryListener listener) {
        SelectionOperate operate = new SelectionOperate(array);
        doRequest(operate, operate.getError(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 添加日志（文件方式）
     */
    public void addSelectionByFile(Context context, String path, FetchEntryListener listener) {
        SelectionOperate operate = new SelectionOperate(path);
        doPostRequest(operate, operate.getError(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 全文搜索结果
     */
    public void getResult(String msg, long s, long e, String top, FetchEntryListener listener) {
        GetSearchResultsOperate operate = new GetSearchResultsOperate(msg, s, e, top);
        doRequest(operate, operate.getArticleList(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 推送：提交device info
     *
     * @param context
     * @param listener
     */
    public void pushDeviceInfo(Context context, String token, String type, FetchEntryListener listener) {
        PushDeviceInfoOperate pushDeviceInfoOperate = new PushDeviceInfoOperate(context, token, type);
        doPostRequest(pushDeviceInfoOperate, pushDeviceInfoOperate.getError(), FetchApiType.USE_HTTP_ONLY, listener);

    }

    /**
     * 扫描二维码显示vip基本信息
     */
    public void showVipInfo(String uid, String token, String vipuid, FetchEntryListener listener) {
        GetVipShowOperate operate = new GetVipShowOperate(uid, token, vipuid);
        doPostRequest(operate, operate.showVipInfo(), FetchApiType.USE_HTTP_ONLY, listener);
    }

    /**
     * 获取商城首页数据
     */
    public void getShangchengIndex(Context context, FetchEntryListener listener) {
        GetShangchengIndexOperate operate = new GetShangchengIndexOperate(context);
        doPostRequest(operate, operate.getDatas(), FetchApiType.USE_HTTP_FIRST, listener);

    }

    /**
     *
     */
    public void getShangchengSplash(Context context, String sceneid, FetchEntryListener listener) {
        GetShangchengSplashDataOperate operate = new GetShangchengSplashDataOperate(context, sceneid);
        doPostRequest(operate, operate.getDatas(), FetchApiType.USE_HTTP_ONLY, listener);

    }

    /**
     * 获取商城详情页数据
     */
    public void getShangchengList(String tags, FetchEntryListener listener) {
        GetShangchengListOperate operate = new GetShangchengListOperate(tags);
        doRequest(operate, operate.getDatas(), FetchApiType.USE_HTTP_ONLY, listener);

    }

}