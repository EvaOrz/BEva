package cn.com.modernmedia.businessweek.jingxuan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.SplashScreenActivity;
import cn.com.modernmedia.businessweek.jingxuan.dubao.DubaoAdapter;
import cn.com.modernmedia.businessweek.jingxuan.fm.FmView;
import cn.com.modernmedia.businessweek.jingxuan.kecheng.KeChengAdapter;
import cn.com.modernmedia.businessweek.jingxuan.pdf.ZhuanTiKanView;
import cn.com.modernmedia.businessweek.jingxuan.zhuantipian.ZhuantiPianAdapter;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ShangchengIndex;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.views.widget.PullToZoomListView;
import cn.com.modernmedia.widget.newrefresh.PullToRefreshLayout;
import cn.com.modernmedia.widget.newrefresh.PullableListView;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.vip.VipProductPayActivity;
import cn.jzvd.JZVideoPlayer;

/**
 * Created by Eva. on 2017/8/15.
 */

public class ShangchengListActivity extends BaseActivity implements View.OnClickListener {

    private int type;// 6专刊；8读报；7FM；9专题片；10课程
    private List<ArticleItem> datas = new ArrayList<>();
    private int fmId;//当前需要播放fm articleid
    private String cuTagName = "";// 当前的tagname
    private String top = ""; // 偏移量
    private ShangchengIndexItem shangchengIndexItem;// 显示简介model

    private LinearLayout container, kaitongLayout;
    private RelativeLayout headLayout;
    private TextView button1, button2;
    private LinearLayout payLayout;
    private TextView title, kaitongButton, infoButton;
    private ZhuanTiKanView zhuankanView;// 专刊
    private FmView fmView;// FM
    // 记录片
    private ZhuantiPianAdapter jiluAdapter;
    private PullableListView jiluListview;
    private PullToRefreshLayout jiluRefreshLayout;
    //读报
    private DubaoAdapter dubaoAdapter;
    private PullToZoomListView dubaoListView;
    //课程
    private KeChengAdapter keChengAdapter;
    private PullableListView keListView;
    private PullToRefreshLayout keRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shangcheng_list);

        isFromSplash = getIntent().getBooleanExtra("is_from_splash", false);
        type = getIntent().getIntExtra("ShangchengList_type", 0);
        fmId = getIntent().getIntExtra("ShangchengList_fm", 0);
        initView();
        if (isFromSplash) {
            String senid = getIntent().getStringExtra("ShangchengList_senid");
            initSplashData(senid);
        } else {
            shangchengIndexItem = (ShangchengIndexItem) getIntent().getSerializableExtra("ShangchengList_info");
            int cmsShowStyle = shangchengIndexItem.getCmsShowStyle();
            if (cmsShowStyle == 10 || cmsShowStyle == 6 || cmsShowStyle == 8 || cmsShowStyle == 7 || cmsShowStyle == 9) {
                initSplashData(shangchengIndexItem.getId());
            }
            handler.sendEmptyMessage(4);
        }
    }

    private void initSplashData(String senid) {
        OperateController.getInstance(this).getShangchengSplash(this, senid, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof ShangchengIndex.ShangchengIndexItem) {
                    shangchengIndexItem = (ShangchengIndexItem) entry;
                    if (type == 10 || type == 6 || type == 8 || type == 7 || type == 9) {
                        handler.sendEmptyMessage(5);
                    } else handler.sendEmptyMessage(4);
                }
            }
        });
    }

    private void initData(final boolean isLoadMore) {
        TagInfoList.TagInfo t = new TagInfoList.TagInfo();
        t.setTagName(cuTagName);
        OperateController.getInstance(this).getTagIndex(t, top, "20", null, SlateBaseOperate.FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof TagArticleList) {
                    TagArticleList tag = (TagArticleList) entry;
                    if (isLoadMore) {
                        if (tag.getArticleList().size() == 0) {
                            handler.sendEmptyMessage(2);// 没有更多
                        } else {
                            datas.addAll(tag.getArticleList());
                            handler.sendEmptyMessage(1);// 加载更多去刷新
                        }

                    } else {
                        datas.clear();
                        datas.addAll(tag.getArticleList());
                        handler.sendEmptyMessage(0);// 首次加载
                    }
                    // 更新偏移量
                    if (datas.size() > 0) {
                        top = datas.get(datas.size() - 1).getOffset();
                    }

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatus();
    }

    private void loadStatus() {
        if (type == 10) {// 课程
            kaitongLayout.setVisibility(View.GONE);
            if (ParseUtil.listNotNull(shangchengIndexItem.getGoods())) {
                payLayout.setVisibility(View.VISIBLE);
//                button2.setBackgroundColor(getResources().getColor(R.color.white_bg));
                button2.setTextColor(getResources().getColor(R.color.black_bg));
                VipGoodList.VipGood good1 = shangchengIndexItem.getGoods().get(0);
                button1.setText(good1.getShowPrice());
                button1.setTag(good1);
                if (shangchengIndexItem.getGoods().size() > 1) {
                    VipGoodList.VipGood good2 = shangchengIndexItem.getGoods().get(1);
                    button2.setText(good2.getShowPrice());
                    button2.setTag(good2);
                }
            }

        } else {
            payLayout.setVisibility(View.GONE);
            int vipLevel = SlateDataHelper.getVipLevel(this);
            if (SlateDataHelper.getUserReadLevel(this).contains(shangchengIndexItem.getReadLevel() + "")/*SlateDataHelper.getVipLevel(this) > 1*/) {
                kaitongLayout.setVisibility(View.GONE);
            } else {
                kaitongLayout.setVisibility(View.VISIBLE);
                if (shangchengIndexItem != null && ParseUtil.listNotNull(shangchengIndexItem.getGoods())) {
                    infoButton.setText(shangchengIndexItem.getGoods().get(0).getShowPrice());
                    infoButton.setTag(shangchengIndexItem.getGoods().get(0));
                }
                if (SlateDataHelper.getVipLevel(this) == 1) {
                    kaitongButton.setText("升级VIP进阶套餐");
                } else {
                    kaitongButton.setText("开通VIP进阶套餐");
                }

            }
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// 刷新数据
                    if (type == 8) {
                        dubaoAdapter.notifyDataSetChanged();
                    } else if (type == 9) {// 记录片
                        jiluRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        jiluAdapter.notifyDataSetChanged();
                    } else if (type == 10) {// 记录片
                        keRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        keChengAdapter.notifyDataSetChanged();
                    }

                    break;

                case 1:
                    if (type == 8) {
                        dubaoListView.setLoadFinish(false);
                        dubaoAdapter.notifyDataSetChanged();
                    } else if (type == 9) {// 记录片
                        jiluRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        jiluAdapter.notifyDataSetChanged();
                    } else if (type == 10) {
                        keRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        keChengAdapter.notifyDataSetChanged();
                    }

                    break;

                case 2:
                    if (type == 8) {
                        dubaoListView.setLoadFinish(true);
                    } else if (type == 9) {// 记录片
                        jiluRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    } else if (type == 10) {
                        keRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }

                    break;
                //                case 3:
                //                    jiluAdapter.notifyDataSetChanged();
                //                    break;
                case 4:
                    cuTagName = shangchengIndexItem.getCmsTagId();
                    if (type == 6) {
                        title.setText(R.string.zhuankan);
                        zhuankanView = new ZhuanTiKanView(ShangchengListActivity.this, cuTagName);
                        container.addView(zhuankanView.fetchView());
                    } else if (type == 7) {
                        title.setText(R.string.fm);
                        fmView = new FmView(ShangchengListActivity.this, cuTagName, fmId);
                        container.addView(fmView.fetchView());
                    } else if (type == 8) {//读报
                        headLayout.setVisibility(View.GONE);

                        dubaoListView = new PullToZoomListView(ShangchengListActivity.this);
                        dubaoListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            /*========================= 设置头部的的布局====================================*/
                        View mHeaderView = getLayoutInflater().inflate(R.layout.view_dubao_head, null);
                        mHeaderView.findViewById(R.id.dubao_back).setOnClickListener(ShangchengListActivity.this);
                        dubaoListView.getHeaderContainer().addView(mHeaderView);
                        dubaoListView.setHeaderView();
             /*========================= 设置头部的高度 ====================================*/
                        //            dubaoListView.setmHeaderHeight(mHeaderView.getHeight());

                        dubaoListView.setPullToZoomListViewListener(new PullToZoomListView.PullToZoomListViewListener() {
                            @Override
                            public void onLoadMore() {
                                initData(true);
                            }
                        });
                        dubaoAdapter = new DubaoAdapter(ShangchengListActivity.this, datas);
                        dubaoListView.setAdapter(dubaoAdapter);
                        dubaoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) return;
                                if (datas.get(position - 1).getProperty().getLevel() > 0 && !SlateDataHelper.getLevelByType(ShangchengListActivity.this, 3)) {
                                    goInfo();

                                } else {
                                    Intent intent = new Intent(ShangchengListActivity.this, CommonApplication.pushArticleCls);
                                    intent.putExtra("is_need_share", false);
                                    intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, datas.get(position - 1).getArticleId() + "");
                                    intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL, datas.get(position - 1).getProperty().getLevel());
                                    startActivity(intent);
                                }

                            }
                        });
                        container.addView(dubaoListView);
                    } else if (type == 9) {//专题片
                        title.setText(R.string.zhuantipian);
                        View view = LayoutInflater.from(ShangchengListActivity.this).inflate(R.layout.view_zhuanti, null);
                        jiluRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
                        jiluRefreshLayout.setPadding(0, 0, 0, 0);
                        jiluRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
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
                        jiluListview = (PullableListView) view.findViewById(R.id.zhuanti_listview);
                        jiluListview.setBackgroundColor(ShangchengListActivity.this.getResources().getColor(R.color.white_bg));
                        //            jiluListview.setDivider(getResources().getDrawable(R.drawable.list_item_divider));
                        jiluListview.setVerticalScrollBarEnabled(false);
                        jiluListview.setSelector(new ColorDrawable(Color.TRANSPARENT));
                        jiluAdapter = new ZhuantiPianAdapter(ShangchengListActivity.this, datas);
                        jiluListview.setAdapter(jiluAdapter);
                        container.addView(view);
                    } else if (type == 10) {// 课程
                        title.setText(R.string.kecheng);
                        View view = LayoutInflater.from(ShangchengListActivity.this).inflate(R.layout.view_zhuanti, null);
                        keRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
                        keRefreshLayout.setPadding(0, 0, 0, 0);
                        keRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
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
                        keListView = (PullableListView) view.findViewById(R.id.zhuanti_listview);
                        keListView.setBackgroundColor(ShangchengListActivity.this.getResources().getColor(R.color.white_bg));
                        keListView.setVerticalScrollBarEnabled(false);
                        keListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
                        keChengAdapter = new KeChengAdapter(ShangchengListActivity.this, datas);
                        keListView.setAdapter(keChengAdapter);
                        keListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (datas.get(position).getProperty().getLevel() > 0 && !SlateDataHelper.getLevelByType(ShangchengListActivity.this, 7)) {
                                    goInfo();
                                } else {
                                    Intent intent = new Intent(ShangchengListActivity.this, CommonApplication.pushArticleCls);
                                    intent.putExtra("is_need_share", false);
                                    intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, datas.get(position).getArticleId() + "");
                                    intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL, datas.get(position).getProperty().getLevel());
                                    startActivity(intent);
                                }

                            }
                        });
                        container.addView(view);
                    }
                    initData(false);
                    break;
                case 5:
                    loadStatus();
                    break;
            }
        }
    };


    private void initView() {
        findViewById(R.id.shang_back).setOnClickListener(this);
        headLayout = (RelativeLayout) findViewById(R.id.shang_headview);
        title = (TextView) findViewById(R.id.shang_title);
        container = (LinearLayout) findViewById(R.id.shang_container);

        kaitongLayout = (LinearLayout) findViewById(R.id.kaitong_layout);
        payLayout = findViewById(R.id.ke_price_layout);
        button1 = findViewById(R.id.ke_price1);
        button2 = findViewById(R.id.ke_price2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        kaitongButton = (TextView) findViewById(R.id.kaitong_text);
        kaitongButton.setOnClickListener(this);
        infoButton = (TextView) findViewById(R.id.kaitong_info);
        infoButton.setOnClickListener(this);
//        loadStatus();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shang_back:
                finish();
                break;
            case R.id.kaitong_text:// 开通 || 升级
                UserPageTransfer.gotoShangchengActivity(ShangchengListActivity.this, false);
                break;
            case R.id.kaitong_info:// 查看简介改为单独购买
//                goInfo();
                if ((type == 7 || type == 8 || type == 6 || type == 9) && infoButton.getTag() != null && infoButton.getTag() instanceof VipGoodList.VipGood) {
                    System.out.println(shangchengIndexItem);
                    VipGoodList.VipGood good = (VipGoodList.VipGood) infoButton.getTag();
                    goPay(good);
                }
                break;
            case R.id.dubao_back:
                finish();
                break;
            case R.id.ke_price1:
                if (type == 10 && button1.getTag() != null && button1.getTag() instanceof VipGoodList.VipGood) {
                    VipGoodList.VipGood good = (VipGoodList.VipGood) button1.getTag();
                    goPay(good);
                }
                break;
            case R.id.ke_price2:
                if (type == 10 && button2.getTag() != null && button2.getTag() instanceof VipGoodList.VipGood) {
                    VipGoodList.VipGood good = (VipGoodList.VipGood) button2.getTag();
                    goPay(good);
                }
                break;
        }
    }

    private void goPay(VipGoodList.VipGood good) {
        Intent i = new Intent(ShangchengListActivity.this, VipProductPayActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("product", good);
        i.putExtras(b);
        startActivity(i);
    }

    public void goInfo() {
        Intent i = new Intent(ShangchengListActivity.this, ShangchengInfoActivity.class);
        i.putExtra("ShangchengInfo_info", shangchengIndexItem);
        i.putExtra("ShangchengInfo_type", type);
        startActivity(i);
    }

    @Override
    public void reLoadData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 释放之前播放的视频
        JZVideoPlayer.goOnPlayOnPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * 停止播放
         */
        if (type == 7) fmView.unbindService();

    }

    @Override
    public void finish() {
        super.finish();
        if (isFromSplash) {
            startActivity(new Intent(this, SplashScreenActivity.class));
        }
    }

    @Override
    public String getActivityName() {
        return ShangchengListActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return ShangchengListActivity.this;
    }

    public void downloadPdf(ArticleItem a) {
        zhuankanView.downloadPdf(a);
    }

}
