package cn.com.modernmedia.views.index;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.ViewsMainActivity.LifeCycle;
import cn.com.modernmedia.views.adapter.IndexViewPagerAdapter;
import cn.com.modernmedia.views.adapter.IndexViewPagerAdapter.CheckNavHideListener;
import cn.com.modernmedia.views.listener.LoadCallBack;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.solo.BaseChildCatHead;
import cn.com.modernmedia.views.solo.ChildCatHead;
import cn.com.modernmedia.views.util.DownloadTemplate;
import cn.com.modernmedia.views.util.DownloadTemplate.DownloadTemplateCallBack;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.widget.VerticalGallery;
import cn.com.modernmedia.widget.RedProcess;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.TimeCollectUtil;

/**
 * 可滑动的首页view item
 *
 * @author user
 */
public class IndexViewPagerItem implements Observer, CheckNavHideListener {
    protected Context mContext;
    private RelativeLayout view;
    private FrameLayout headFrame, frame;
    private View divider;
    private TagIndexListView indexListView;// 普通首页
    private VerticalGallery galleryView;// 图集
    private IndexWebView mWebView; // 网页
    private ImageView advImageView; // 广告图片

    protected TagInfo tagInfo;
    protected IndexViewPagerAdapter adapter;
    private View layout, loading, error;
    protected boolean hasSetData;
    protected TagInfoList childInfoList;
    private BaseChildCatHead childHead;
    private Template template;

    private int catHeadOffset;

    public IndexViewPagerItem(Context context, TagInfo tagInfo, IndexViewPagerAdapter adapter) {
        mContext = context;
        this.adapter = adapter;
        this.tagInfo = tagInfo;
        hasSetData = false;
        BaseChildCatHead.selectPosition = -1;
        init();
        if (SlateApplication.mConfig.getNav_hide() == 1)
            ViewsApplication.navObservable.addObserver(this);
        if (mContext instanceof ViewsMainActivity) {
            ((ViewsMainActivity) mContext).getLifecycleObservable().addObserver(this);
        }
    }

    @SuppressLint("InflateParams")
    private void init() {
        if (tagInfo == null) return;
        view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.index_view_pager_item, null);
        view.setTag(this);
        if (ConstData.getAppId() == 20) {
            layout = view.findViewById(R.id.pager_item_layout_weekly);
            loading = view.findViewById(R.id.pager_item_loading_weekly);
            layout.setVisibility(View.VISIBLE);
        } else {
            layout = view.findViewById(R.id.pager_item_layout);
            loading = view.findViewById(R.id.pager_item_loading);
            error = view.findViewById(R.id.pager_item_error);
        }

        headFrame = (FrameLayout) view.findViewById(R.id.pager_item_head_frame);
        frame = (FrameLayout) view.findViewById(R.id.pager_item_frame);
        divider = view.findViewById(R.id.pager_item_head_frame_divider);
        if (error != null) error.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                fetchData("", false, false, null, null);
            }
        });

        mWebView = (IndexWebView) view.findViewById(R.id.pager_item_webview);
        mWebView.setProcessListener(new WebProcessListener() {

            @Override
            public void showStyle(int style) {
                if (style == 0) {
                    disProcess();
                } else if (style == 1) {
                    showLoading();
                } else if (style == 2) {
                    showError();
                }
            }
        });
        advImageView = view.findViewById(R.id.pager_item_imageview);
        V.setImage(advImageView, "placeholder");
        advImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagInfo.getAdvItem() != null && ParseUtil.listNotNull(tagInfo.getAdvItem().getSourceList())) {
                    UriParse.clickSlate(mContext, tagInfo.getAdvItem().getSourceList().get(0).getLink(), new Entry[]{new ArticleItem()}, view);
                }

            }
        });
        if (tagInfo.getGroup() == -1) {// 广告
            if (tagInfo.getAdvItem() != null) {
                AdvList.AdvItem advItem = tagInfo.getAdvItem();
                if (advItem.getShowType() == 1) {// 网页
                    frame.setVisibility(View.GONE);
                    advImageView.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                } else if (advItem.getShowType() == 0) {// 图片
                    frame.setVisibility(View.GONE);
                    advImageView.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                }
            }
        } else if (tagInfo.getGroup() == 7) { // 栏目是网页形式
            frame.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            advImageView.setVisibility(View.GONE);
        } else {
            frame.setVisibility(View.VISIBLE);
            advImageView.setVisibility(View.GONE);
            mWebView.setVisibility(View.GONE);
            showLoading();
        }
    }

    /**
     * 获取数据
     */
    public void fetchData(String top, boolean isRefresh, boolean isLoadMore, LoadCallBack callBack, TagArticleList tagArticleList) {
        if (tagInfo.getGroup() == -1) {
            if (tagInfo.getAdvItem() != null && ParseUtil.listNotNull(tagInfo.getAdvItem().getSourceList())) {
                AdvList.AdvItem advItem = tagInfo.getAdvItem();
                if (advItem.getShowType() == 1) {// 网页
                    mWebView.setData(advItem.getSourceList().get(0).getUrl());
                } else if (advItem.getShowType() == 0) {// 图片
                    V.setImage(advImageView, advItem.getSourceList().get(0).getUrl());
                }
            }
            return;
        }
        if (tagInfo.getGroup() == 7) { // 网页栏目
            if (!TextUtils.isEmpty(tagInfo.getLink())) {
                mWebView.setData(tagInfo.getLink());
            } else {
                showError();
            }
            return;
        }

        if (hasSetData) return;
        TimeCollectUtil.getInstance().savePageTime(TimeCollectUtil.EVENT_OPEN_COLUMN + tagInfo.getTagName(), true);
        if (tagInfo.showChildren()) {
            getChildren();
        } else {
            getArticleList(tagInfo, top, isRefresh, isLoadMore, callBack, tagArticleList);
        }
    }

    /**
     * 刷新
     */
    public void refresh(LoadCallBack callBack) {
        hasSetData = false;
        fetchData("", true, false, callBack, null);
    }

    /**
     * 加载更多
     *
     * @param callBack
     * @param top
     */
    public void loadMore(LoadCallBack callBack, String top, TagArticleList articleList) {
        hasSetData = false;
        fetchData(top, false, true, callBack, articleList);
    }

    /**
     * 获取指定的子栏目数据
     */
    public void fecthSpecificChildData(int position, int catHeadOffset) {
        hasSetData = false;
        this.catHeadOffset = catHeadOffset;
        if (childInfoList.getList().size() > position) {
            TimeCollectUtil.getInstance().savePageTime(TimeCollectUtil.EVENT_OPEN_COLUMN + tagInfo.getTagName(), true);
            getCatIndex(childInfoList.getList().get(position));
        }
    }

    public void setHeadFrame(Template template) {
        if (childInfoList == null || template.getCatHead().getCat_list_hold() == 0) return;
        if (childHead != null) return;
        headFrame.removeAllViews();
        divider.setVisibility(View.GONE);
        if (!checkShowCatHead()) {
            return;
        }
        // TODO 子栏目导航栏固定在顶部
        childHead = new ChildCatHead(mContext, this, template);
        childHead.setChildValues(childInfoList, tagInfo.getTagName());
        headFrame.addView(childHead.fetchView(), new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        String bg = template.getCatHead().getColor();
        if (TextUtils.isEmpty(bg) || bg.equalsIgnoreCase("#FFFFFFFF")) {
            divider.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 判断是否需要显示cathead
     *
     * @return
     */
    protected boolean checkShowCatHead() {
        if (tagInfo.getTagLevel() == 2) {
            TagInfo parentTag = TagInfoListDb.getInstance(mContext).getTagInfoByName(tagInfo.getParent(), "", true);
            return parentTag.showChildren();
        }
        return tagInfo.showChildren();
    }

    /**
     * 获取子栏目
     */
    protected void getChildren() {
        childInfoList = AppValue.ensubscriptColumnList.getChildHasSubscriptTagInfoList(tagInfo.getTagName());
        if (ParseUtil.listNotNull(childInfoList.getList())) {
            getCatIndex(childInfoList.getList().get(0));
        }
    }

    protected void getCatIndex(TagInfo tagInfo) {
        getArticleList(tagInfo, "", false, false, null, null);
    }

    /**
     * 获取api请求时的tagname
     *
     * @return
     */
    protected String getApiTagName() {
        String tagName = tagInfo.getMergeName(true);
        return TextUtils.isEmpty(tagName) ? tagInfo.getTagName() : tagName;
    }

    /**
     * 先请求文章列表(与栏目首页联动);文章列表的数据库缓存会在接口里实现
     *
     * @param tagInfo
     * @param top
     * @param isRefresh
     * @param isLoadMore
     * @param callBack
     * @param tagArticleList
     */
    protected void getArticleList(final TagInfo tagInfo, final String top, final boolean isRefresh, final boolean isLoadMore, final LoadCallBack callBack, final TagArticleList tagArticleList) {
        if (!isRefresh && !isLoadMore) showLoading();
        OperateController.getInstance(mContext).getTagArticles(mContext,tagInfo, top, "", null,
                new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagArticleList) {
                    TagArticleList articleList = (TagArticleList) entry;
                    if (ParseUtil.listNotNull(articleList.getArticleList())) {
                        // 添加是否可订阅参数
                        tagInfo.setEnablesubscribe(articleList.getEnablesubscribe());
                        getCatIndex(tagInfo, top, isRefresh, isLoadMore, callBack, tagArticleList);
                    } else {
                        if (isLoadMore && indexListView != null && indexListView.getListView() != null) {
                            indexListView.getListView().loadOk(false);

                        }
                    }
                    return;
                }
                if (!isRefresh && !isLoadMore) showError();
            }
        });
    }

    /**
     * 获取栏目首页
     *
     * @param isRefresh  是否刷新
     * @param isLoadMore 是否加载更多
     */
    private void getCatIndex(TagInfo tagInfo, String top, final boolean isRefresh, final boolean isLoadMore, final LoadCallBack callBack, TagArticleList tagArticleList) {
        this.tagInfo = tagInfo;
        OperateController.getInstance(mContext).getTagIndex(tagInfo, top, "", tagArticleList, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                boolean success = false;
                if (entry instanceof TagArticleList) {
                    success = true;
                    String s = new Gson().toJson((TagArticleList) entry);
                    getTemplate((TagArticleList) entry, isLoadMore);
                } else {
                    if (!isRefresh && !isLoadMore) showError();
                }
                loadCallBack(callBack, isRefresh, isLoadMore, success);
            }
        });
    }

    /**
     * 通知加载完成
     *
     * @param callBack
     * @param success
     */
    private void loadCallBack(LoadCallBack callBack, boolean isRefresh, boolean isLoadMore, boolean success) {
        if (callBack == null) return;
        if (isRefresh) callBack.onRefreshed(success);
        else if (isLoadMore) callBack.onLoaded(success);
    }

    /**
     * 设置列表/图集数据
     *
     * @param articleList
     */
    private void setValueForIndex(TagArticleList articleList, boolean loadMore) {
        disProcess();
        if (template.getList().getIs_gallery() == 1) {
            // iweekly图集
            setHeadFrame(template);
            frame.removeAllViews();
            view.setBackgroundColor(Color.BLACK);
            galleryView = new VerticalGallery(mContext);
            frame.addView(galleryView.getView());
            galleryView.setData(articleList, template);
            TimeCollectUtil.getInstance().savePageTime(TimeCollectUtil.EVENT_OPEN_COLUMN + tagInfo.getTagName(), false);
        } else {
            // 列表
            if (indexListView != null && loadMore) {
                indexListView.setData(articleList, childInfoList, true, template);
            } else {
                frame.removeAllViews();
                indexListView = new TagIndexListView(mContext, this);
                frame.addView(indexListView.fetchView());
                indexListView.setData(articleList, childInfoList, loadMore, template);
            }
        }
        hasSetData = true;
    }

    /**
     * 获取模板
     *
     * @param articleList
     * @param loadMore
     */
    private void getTemplate(final TagArticleList articleList, final boolean loadMore) {
        if (template != null) {
            setValueForIndex(articleList, loadMore);
            return;
        }
        new DownloadTemplate(mContext, articleList, new DownloadTemplateCallBack() {

            @Override
            public void afterDownload(Template _template) {
                template = _template;
                // setValueForIndex(articleList, loadMore);
                checkShouldAddMarquee(articleList, loadMore);
            }
        }).getTemplate();
    }

    /**
     * 判断是否需要添加跑马灯
     *
     * @param articleList
     * @param loadMore
     */
    private void checkShouldAddMarquee(TagArticleList articleList, boolean loadMore) {
        if (loadMore) {
            setValueForIndex(articleList, loadMore);
            return;
        }
        if (template != null && template.getList().getShow_marquee() == 1 && articleList.getTagName().equals("cat_15")) {
            getMarqueeTagIndex(articleList, loadMore);
        } else {
            setValueForIndex(articleList, loadMore);
        }
    }

    /**
     * 获取跑马灯栏目首页
     */
    private void getMarqueeTagIndex(final TagArticleList articleList, final boolean loadMore) {
        OperateController.getInstance(mContext).getTagIndex(AppValue.getMarqueeTagInfo(), "", "", null, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagArticleList) {
                    TagArticleList index = (TagArticleList) entry;
                    // 直播url
                    if (mContext instanceof CommonMainActivity)
                        CommonMainActivity.liveUrl = index.getLink();
                    if (ParseUtil.listNotNull(index.getArticleList())) {
                        String outline = "";
                        for (ArticleItem a : index.getArticleList()) {
                            outline = outline + DateFormatTool.format(a.getInputtime() * 1000L, "hh:mm") + "·" + a.getTitle() + "    ";
                        }
                        ArticleItem marqueeItem = index.getArticleList().get(0);
                        marqueeItem.setOutline(outline);// 整合跑马灯内容
                        marqueeItem.getPosition().setStyle(102);

                        if (articleList.getMap().containsKey(3) && ParseUtil.listNotNull(articleList.getMap().get(3))) {// ibloomberg临时修复
                            // 把跑马灯文章添加到栏目首页列表第一篇
                            articleList.getMap().get(2).add(0, marqueeItem);
                        } else if (articleList.getMap().containsKey(2) && ParseUtil.listNotNull(articleList.getMap().get(2))) {
                            // 把跑马灯文章添加到栏目首页列表第一篇
                            articleList.getMap().get(2).add(0, marqueeItem);
                        }
                    }
                }
                String s = new Gson().toJson(articleList);
                setValueForIndex(articleList, loadMore);
            }
        });
    }

    public List<View> getHeadView() {
        List<View> list = new ArrayList<View>();
        list.add(headFrame);
        if (indexListView != null) {
            list.addAll(indexListView.getSelfScrollViews());
        } else if (galleryView != null) {
            list.add(galleryView.getViewPager());
        }
        return list;
    }

    /**
     * 显示loading图标
     */
    public void showLoading() {
        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        if (loading instanceof RedProcess) ((RedProcess) loading).start();
        if (error != null) error.setVisibility(View.GONE);
    }

    /**
     * 显示错误提示
     */
    public void showError() {
        if (tagInfo.getGroup() == 7) {
            disProcess();
        } else {
            layout.setVisibility(View.VISIBLE);
            if (error != null) {
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 隐藏process_layout
     */
    public void disProcess() {
        layout.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        if (loading instanceof RedProcess) ((RedProcess) loading).stop();
        if (error != null) error.setVisibility(View.GONE);
    }

    public TagInfo getTagInfo() {
        return tagInfo;
    }

    public void destory() {
        hasSetData = false;
        if (indexListView != null && indexListView.getHeadView() != null) {
            indexListView.getHeadView().stopRefresh();
        }
        if (SlateApplication.mConfig.getNav_hide() == 1)
            ViewsApplication.navObservable.deleteObserver(this);
        if (mContext instanceof ViewsMainActivity) {
            ((ViewsMainActivity) mContext).getLifecycleObservable().deleteObserver(this);
        }
    }

    public View fetchView() {
        return view;
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        if (arg1 instanceof LifeCycle) {
            if (indexListView != null) {
                LifeCycle life = (LifeCycle) arg1;
                indexListView.life(life);
            }
        } else {
            if (indexListView != null) {
                indexListView.setSelection(-1);
            } else if (galleryView != null) {
                galleryView.setSelection(IndexView.height);
            }
        }
    }

    public int getCatHeadOffset() {
        return catHeadOffset;
    }

    @Override
    public void onResume() {
        if (indexListView != null) {
            indexListView.setStopCheckNav(false);
            indexListView.life(LifeCycle.RESUME);
        } else if (galleryView != null) {
            galleryView.setStopCheckNav(false);
        }
    }

    @Override
    public void onPause() {
        if (indexListView != null) {
            indexListView.setStopCheckNav(true);
            indexListView.life(LifeCycle.PAUSE);
        } else if (galleryView != null) {
            galleryView.setStopCheckNav(true);
        }
    }

    /**
     * 栏目内容是网页时，需判断是否是起始栏目内容，若不是，点击返回键时则返回到上一级内容，直至起始栏目内容
     *
     * @return
     */
    public boolean doGoBack() {
        return mWebView.getVisibility() == View.VISIBLE && mWebView.doGoBack();
    }

    public TagIndexListView getIndexListView() {
        return indexListView;
    }

}
