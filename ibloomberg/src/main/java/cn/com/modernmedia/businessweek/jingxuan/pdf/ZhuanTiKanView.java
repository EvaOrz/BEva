package cn.com.modernmedia.businessweek.jingxuan.pdf;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.widget.newrefresh.PullToRefreshLayout;
import cn.com.modernmedia.widget.newrefresh.PullableListView;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.Entry;

/**
 * 专题 专刊view
 * Created by Eva. on 17/3/2.
 */

public class ZhuanTiKanView {
    private Context context;
    private PullToRefreshLayout pullToRefreshLayout;
    private PullableListView listView;
    private View view;

    private List<ArticleItem> articleData = new ArrayList<ArticleItem>();
    private ZhuantiAdapter zhuankanAdapter;

    private OperateController operateController;
    private String tagName;
    private String top = "";

    public ZhuanTiKanView(Context context, String tagName) {
        this.context = context;
        this.tagName = tagName;
        operateController = OperateController.getInstance(context);
        initView();
        initData(false);
    }

    /**
     * 加载专刊数据
     */
    private void initData(final boolean isLoadMore) {

        TagInfoList.TagInfo t = new TagInfoList.TagInfo();
        t.setTagName(tagName);
        operateController.getTagIndex(t, top, "20", null, SlateBaseOperate.FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof TagArticleList) {
                    TagArticleList articleList = (TagArticleList) entry;
                    /**
                     * 加载更多
                     */
                    if (isLoadMore) {
                        articleData.addAll(articleList.getArticleList());
                        handler.sendEmptyMessage(1);
                        /**
                         * 刷新
                         */
                    } else {
                        articleData.clear();
                        articleData.addAll(articleList.getArticleList());
                        handler.sendEmptyMessage(0);
                    }
                    // 更新偏移量
                    if (articleData.size() > 0) {
                        top = articleData.get(articleData.size() - 1).getOffset();
                    }

                }
            }
        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// 刷新
                    refreshFinish();
                    zhuankanAdapter.notifyDataSetChanged();
                    break;
                case 1:// 加载更多刷新
                    loadmoreFinish();
                    zhuankanAdapter.notifyDataSetChanged();

                    break;
            }
        }
    };

    private void initView() {
        view = LayoutInflater.from(context).inflate(R.layout.view_zhuanti, null);
        //        addView(view);
        listView = view.findViewById(R.id.zhuanti_listview);
        listView.setBackgroundColor(context.getResources().getColor(R.color.white_bg));
        listView.setDivider(null);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        zhuankanAdapter = new ZhuantiAdapter(context, null, articleData, false);
        listView.setAdapter(zhuankanAdapter);

        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                top = "";
                initData(false);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                initData(true);
            }
        });

    }

    public View fetchView() {
        return view;
    }

    /**
     * 成功刷新完毕
     */
    private void refreshFinish() {
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    /**
     * 成功加载完毕
     */
    private void loadmoreFinish() {
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    /**
     * 自动刷新
     */
    private void autoRefresh() {
        pullToRefreshLayout.autoRefresh();
    }

    /**
     * 下载
     */
    public void downloadPdf(final ArticleItem a) {
        final String url = a.getPageUrlList().get(0).getUrl();
        final DownPdfTask downloadTask = new DownPdfTask(context, new DownPdfTask.DownLoadPdfLitener() {
            @Override
            public void onProgressUpdate(int p) {
                for (int i = 0; i < articleData.size(); i++) {
                    if (articleData.get(i).getPageUrlList().get(0).getUrl().equals(url)) {
                        articleData.get(i).setDownloadPercent(p);
                    }
                }
                zhuankanAdapter.notifyDataSetChanged();
            }
        });
        downloadTask.execute(url);

    }

}