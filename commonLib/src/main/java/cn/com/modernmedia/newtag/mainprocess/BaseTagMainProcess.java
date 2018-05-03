package cn.com.modernmedia.newtag.mainprocess;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.GetTagInfoOperate.TAG_TYPE;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.LastestArticleId;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagArticleListDb;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.newtag.mainprocess.MainProcessObservable.ObserverItem;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage.MainProcessParseCallBack;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.EnsubscriptHelper;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.TimeCollectUtil;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 入版主流程基类
 *
 * @author zhuqiao
 */
public abstract class BaseTagMainProcess {
    protected Context mContext;
    protected OperateController mController;
    protected MainProcessParseCallBack callBack;

    /**
     * 1.应用信息;2.栏目列表;3.栏目首页;4.子栏目列表;5.获取订阅列表;6.获取广告;7.获取文章列表
     */
    protected int errorType;
    protected TagInfo currTagInfo;

    protected ProcessState mState = new ProcessState();

    /**
     * 是否设置了数据(缓存或者线上数据,通知刷新栏目列表就代表有数据了)
     */
    public static boolean hasFilledData = false;

    public static class ProcessState {
        public boolean isEnd = false;
        public boolean isSuccess = false;
    }

    public BaseTagMainProcess(Context context, MainProcessParseCallBack callBack) {
        mContext = context;
        mController = OperateController.getInstance(mContext);
        this.callBack = callBack;
    }

    public abstract void onStart(Object... objs);

    /**
     * 获取应用信息
     */
    protected void getAppInfo(FetchApiType type) {
        errorType = 1;
        TimeCollectUtil.getInstance().addCollectUrl(UrlMaker.getTagInfo(""));
        mController.getAppInfo(type, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagInfoList) {
                    TagInfoList appInfo = (TagInfoList) entry;
                    if (ParseUtil.listNotNull(appInfo.getList())) {
                        doAfterFecthAppInfo(appInfo, true);
                        return;
                    }
                }
                doAfterFecthAppInfo(null, false);
            }
        });
    }

    /**
     * 获取完应用信息
     *
     * @param appInfo
     * @param success 是否成功
     */
    protected void doAfterFecthAppInfo(TagInfoList appInfo, boolean success) {
        if (success) {

            AppValue.appInfo = appInfo.getList().get(0).getAppProperty();
            AppValue.appInfo.setTagName(appInfo.getList().get(0).getTagName());
            if (!TextUtils.equals(DataHelper.getAppUpdateTime(mContext), AppValue.appInfo.getUpdatetime())) {
                clearCacheWhenUpdatetimeChange();
            }
        }
    }

    /**
     * 如果应用更新时间变了，那么清除特定的缓存
     */
    protected void clearCacheWhenUpdatetimeChange() {
        TagIndexDb.getInstance(mContext).clearSubscribeTopArticle();
        TagArticleListDb.getInstance(mContext).clearSubscribeTopArticle();
        TagInfoListDb.getInstance(mContext).clearTable("", "", "", TAG_TYPE.TAG_INFO);
        TagInfoListDb.getInstance(mContext).clearTable("", "", "", TAG_TYPE.CHILD_CAT);
    }

    /**
     * 获取广告列表
     */
    protected void getAdvList(FetchApiType type) {
        errorType = 6;
        TimeCollectUtil.getInstance().addCollectUrl(UrlMaker.getAdvList());
        mController.getAdvList(type, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof AdvList) {
                    doAfterFetchAdvList((AdvList) entry, true);
                } else {
                    doAfterFetchAdvList(null, false);
                }
            }
        });
    }

    protected void doAfterFetchAdvList(AdvList advList, boolean success) {
        if (success) {
            CommonApplication.advList = advList;
        }
    }

    /**
     * 判断获取到广告列表之后的操作
     */
    protected void checkAfterFetchAdvList(FetchApiType apiType) {
        if (ConstData.getAppId() == 20) {
            // iweekly，从获取视野栏目开始
            new FetchShiyeIndexHelper(mContext) {

                @Override
                public void doAfterFecthShiye(TagArticleList articleList, boolean success) {
                    BaseTagMainProcess.this.doAfterFecthShiye(articleList, success);
                }
            }.getShiyeTagIndex(apiType);
        } else {
            // 非iweekly，从获取栏目列表开始
            getCatList(apiType);
        }
    }

    /**
     * 获取栏目列表
     */
    protected void getCatList(final FetchApiType type) {
        errorType = 2;
        TimeCollectUtil.getInstance().addCollectUrl(UrlMaker.getCatList((AppValue.appInfo.getTagName())));
        mController.getTagInfo(AppValue.appInfo.getTagName(), "", "3", "", TAG_TYPE.TREE_CAT, type, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagInfoList) {
                    TagInfoList catList = (TagInfoList) entry;
                    if (ParseUtil.listNotNull(catList.getList())) {
                        doAfterFecthCatList(catList, true, type);
                        return;
                    }
                }
                doAfterFecthCatList(null, false, type);
            }
        });
    }

    /**
     * 获取完栏目列表
     *
     * @param type 可由子类提供
     */
    protected void doAfterFecthCatList(TagInfoList catList, boolean success, FetchApiType type) {
        if (success) {
            AppValue.defaultColumnList = catList;
            // iweekly需先获取最新文章
            if (ConstData.getInitialAppId() == 20) {
                getLastestArticle(type);
            } else {
                getSubscribeList(type);
            }
        }
    }

    /**
     * 获取iweekly最新未读文章
     */
    protected void getLastestArticle(final FetchApiType type) {
        TimeCollectUtil.getInstance().addCollectUrl(UrlMaker.getWeeklyLatestArticleId((AppValue.appInfo.getTagName())));
        OperateController.getInstance(mContext).getLatestArticleIds("", type, new FetchEntryListener() {

            @Override
            public void setData(final Entry entry) {
                doAfterGetLastestArticle(entry, type);
            }
        });
    }

    protected void doAfterGetLastestArticle(Entry entry, FetchApiType type) {
        if (entry instanceof LastestArticleId) {
            CommonApplication.lastestArticleId = (LastestArticleId) entry;
        }
        getSubscribeList(type);
    }

    /**
     * 获取用户订阅列表
     */
    protected void getSubscribeList(FetchApiType type) {
        if (TextUtils.equals(Tools.getUid(mContext), SlateApplication.UN_UPLOAD_UID) || AppValue.appInfo.getHaveSubscribe() == 0 || SlateApplication.mConfig.getHas_subscribe() == 0) {
            // TODO 未登录或者不支持订阅
            doAfterFetchSubscribeList(new SubscribeOrderList(), true);
        } else {
            getSubscribeOrderList(type);
        }
    }

    protected void getSubscribeOrderList(FetchApiType type) {
        errorType = 5;
        TimeCollectUtil.getInstance().addCollectUrl(UrlMaker.getUserSubscribeList(Tools.getUid(mContext), SlateDataHelper.getToken(mContext)));
        mController.getSubscribeOrderList(Tools.getUid(mContext), SlateDataHelper.getToken(mContext), type, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof SubscribeOrderList) {
                    doAfterFetchSubscribeList((SubscribeOrderList) entry, true);
                } else {
                    doAfterFetchSubscribeList(null, false);
                }
            }
        });
    }

    /**
     * 获取完订阅列表
     *
     * @param subscribeList 订阅栏目列表
     */
    protected void doAfterFetchSubscribeList(SubscribeOrderList subscribeList, boolean success) {
        EnsubscriptHelper.addEnsubscriptColumn(mContext);

        // NOTE 通知刷新栏目列表
        ObserverItem catListObserver = new ObserverItem(MainProcessObservable.SET_DATA_TO_COLUMN, new Entry());
        CommonApplication.mainProcessObservable.notifyProcessChange(catListObserver);

        // NOE 通知显示IndexViewPager
        ObserverItem indexPagerObserver = new ObserverItem(MainProcessObservable.SHOW_INDEX_PAGER, new Entry());
        CommonApplication.mainProcessObservable.notifyProcessChange(indexPagerObserver);

        hasFilledData = true;
        toEnd(true);
        fetchFirstCatData();
    }

    /**
     * 获取完文章列表
     *
     * @param articleList
     * @param success
     */
    protected void doAfterFecthShiye(TagArticleList articleList, boolean success) {
        if (!success) return;
        ObserverItem shiyeObserver = new ObserverItem(MainProcessObservable.SET_DATA_TO_SHIYE, articleList);
        CommonApplication.mainProcessObservable.notifyProcessChange(shiyeObserver);
    }

    /**
     * 获取首页第一屏数据
     */
    protected void fetchFirstCatData() {
    }

    public TagInfo getCurrTagInfo() {
        return currTagInfo;
    }

    /**
     * 获取数据状态
     */
    public ProcessState getProcessState() {
        return mState;
    }

    protected abstract void toEnd(boolean success);

    /**
     * 退出应用时还原变量
     */
    protected static void clear() {
        hasFilledData = false;
    }
}
