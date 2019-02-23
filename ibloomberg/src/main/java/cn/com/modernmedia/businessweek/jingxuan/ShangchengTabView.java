package cn.com.modernmedia.businessweek.jingxuan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.PDFActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ShangchengIndex;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.newtag.mainprocess.TagProcessManage;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.FullVideoView;
import cn.com.modernmedia.widget.RoundAngleImageView;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.DateFormatTool;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.jzvd.JZVideoPlayer;

/**
 * 新的商城tab
 * Created by Eva. on 2017/8/9.
 */

public class ShangchengTabView extends BaseView implements View.OnClickListener {
    private Context context;
    private OperateController operateController;
    private LayoutParams sanPamas;
    private ShangchengIndex shangchengIndex; // 初始化模块数据

    private TagArticleList tagArticleList;
    private ArrayList<ArticleItem> zhuankanDatas = new ArrayList<>(), dubaoDatas = new ArrayList<>(), fmDatas = new ArrayList<>(), zhuantiDatas = new ArrayList<>(), kechengDatas = new ArrayList<>();
    private List<AdvItem> headDatas = new ArrayList<>(), unheadDatas = new ArrayList<>();
    private TextView kePrice, kanPrice, duPrice, fmPrice, tiPrice;
    private ImageView keDing, kanDing, duDing, fmDing, tiDing;
    private TextView kechengName,zhuankanName,dubaoName,fmName,zhuantiName;
    private LinearLayout kechengBigLayout,zhuankanBigLayout,dubaoBigLayout,fmBigLayout,zhuantiBigLayout;

    //view
    private ViewPager headViewPager;
    private ShangHeadAdapter headAdapter;
    private RelativeLayout ad2Layout;// 位置2广告
    private ImageView ad2Img;
    private TextView ad2Title, ad2Time;
    private LinearLayout zhuankanLayout, dubaoLayout, fmLayout, zhuantiLayout, kechengLayout;
    private RelativeLayout specialTitle;


    public ShangchengTabView(Context context) {
        super(context);
        this.context = context;
        operateController = OperateController.getInstance(context);
        initView();
    }


    public void loadData() {
        initHeadAdv();
        initData();
    }

    /**
     * 初始化广告
     */
    private void initHeadAdv() {
        operateController.getAdvList(FetchApiType.USE_CACHE_ONLY, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof AdvList) {
                    AdvList advDatas = (AdvList) entry;
                    Map<Integer, List<AdvItem>> advMap = advDatas.getAdvMap();
                    if (ParseUtil.mapContainsKey(advMap, AdvList.SHANG_HEAD)) {
                        headDatas.clear();
                        headDatas.addAll(advMap.get(AdvList.SHANG_HEAD));
                    }
                    handler.sendEmptyMessage(3);
                    if (ParseUtil.mapContainsKey(advMap, AdvList.SHANG_UNDERHEAD)) {
                        unheadDatas.clear();
                        unheadDatas.addAll(advMap.get(AdvList.SHANG_UNDERHEAD));
                        handler.sendEmptyMessage(4);
                    }
                }
            }
        });


    }

    @Override
    public void reLoad() {
        handler.sendEmptyMessage(1);
        handler.sendEmptyMessage(0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shang_ad2:
                if (ad2Title.getTag() != null && ad2Title.getTag() instanceof String)
                    UriParse.clickSlate(context, (String) ad2Title.getTag(), new Entry[]{new ArticleItem()}, null, new Class<?>[0]);

                break;
            case R.id.kecheng_order:
                goBuy(keDing.getTag());
                break;
            case R.id.zhuankan_order:
                goBuy(kanDing.getTag());
                break;
            case R.id.dubao_order:
                goBuy(duDing.getTag());
                break;
            case R.id.fm_order:
                goBuy(fmDing.getTag());
                break;
            case R.id.zhuanti_order:
                goBuy(tiDing.getTag());
                break;
            case R.id.ke_11:
                goInfo(10);
                break;
            case R.id.ke_22:
                goListActivity(10,-1);
                break;
            case R.id.kan_11:
                goInfo(6);
                break;
            case R.id.kan_22:
                goListActivity(6, -1);
                break;
            case R.id.fm_11:
                goInfo(7);
                break;
            case R.id.fm_22:
                goListActivity(7,0);
                break;
            case R.id.ti_11:
                goInfo(9);
                break;
            case R.id.ti_22:
                goListActivity(9,-1);
                break;
            case R.id.du_11:
                goInfo(8);
                break;
            case R.id.du_22:
                goListActivity(8,-1);
                break;

        }
    }

    private void goBuy(Object tag) {
        if (tag == null || !(tag instanceof ShangchengIndexItem)) return;
        ShangchengIndexItem indexItem = (ShangchengIndexItem) tag;
        if (SlateDataHelper.getLevelByType(context, indexItem.getReadLevel())) return;
        goInfo(indexItem.getCmsShowStyle());
    }

    /**
     * 前往列表页面
     *
     * @param type
     */
    private void goListActivity(int type, int fmId) {
        if (shangchengIndex == null) return;
        Intent i = new Intent(context, ShangchengListActivity.class);
        i.putExtra("ShangchengList_type", type);
        i.putExtra("ShangchengList_fm", fmId);
        for (ShangchengIndex.ShangchengIndexItem indexItem : shangchengIndex.getDatas()) {
            if (indexItem.getCmsShowStyle() == type) {
                i.putExtra("ShangchengList_info", indexItem);
            }
        }
        context.startActivity(i);
    }

    /**
     * 初始化专题 专刊数据
     */
    private void initData() {
        operateController.getShangchengIndex(context, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof ShangchengIndex) {
                    shangchengIndex = (ShangchengIndex) entry;
                    handler.sendEmptyMessage(0);
                }
            }
        });


    }

    /**
     * 加载读报数据
     */
    private void loadListData(final List<ShangchengIndexItem> items) {
        String tags = "";
        final List<Integer> cmsShowTypes = new ArrayList<>();
        for (ShangchengIndexItem item : items) {
            tags += item.getCmsTagId() + ",";
            cmsShowTypes.add(item.getCmsShowStyle());
        }
        tags.substring(0, tags.length() - 1);

        operateController.getShangchengList(tags, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagArticleList) {
                    tagArticleList = (TagArticleList) entry;
                    if (ParseUtil.listNotNull(tagArticleList.getArticleList())) {
                        for (int i = 0; i < tagArticleList.getArticleList().size(); i++) {
                            if (cmsShowTypes.get(i) == 6) { //
                                tagArticleList.getArticleList().get(i).setAppid(6);
                                zhuankanDatas.addAll(tagArticleList.getArticleList().get(i).getSubArticleList());
                            } else if (cmsShowTypes.get(i) == 7) {
                                tagArticleList.getArticleList().get(i).setAppid(7);
                                fmDatas.addAll(tagArticleList.getArticleList().get(i).getSubArticleList());
                            } else if (cmsShowTypes.get(i) == 8) {
                                tagArticleList.getArticleList().get(i).setAppid(8);
                                dubaoDatas.addAll(tagArticleList.getArticleList().get(i).getSubArticleList());
                            } else if (cmsShowTypes.get(i) == 9) {
                                tagArticleList.getArticleList().get(i).setAppid(9);
                                zhuantiDatas.addAll(tagArticleList.getArticleList().get(i).getSubArticleList());
                            } else if (cmsShowTypes.get(i) == 10) {
                                tagArticleList.getArticleList().get(i).setAppid(10);
                                if (tagArticleList.getArticleList().get(i).getSubArticleList() != null || tagArticleList.getArticleList().get(i).getSubArticleList().size() != 0) {
                                    kechengBigLayout.setVisibility(VISIBLE);
                                }
                                kechengDatas.addAll(tagArticleList.getArticleList().get(i).getSubArticleList());
                            }
                        }
                        handler.sendEmptyMessage(1);
                    }
                }
            }
        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:// load list data
                    if (shangchengIndex != null && ParseUtil.listNotNull(shangchengIndex.getDatas())) {

                        loadListData(shangchengIndex.getDatas());
                        for (ShangchengIndexItem item : shangchengIndex.getDatas()) {
                            switch (item.getCmsShowStyle()) {
                                case 6:
                                    if (SlateDataHelper.getLevelByType(context, item.getReadLevel())) {
                                        kanDing.setImageResource(R.drawable.yigou);
                                    } else kanDing.setImageResource(R.drawable.dinggou);
                                    kanDing.setTag(item);
                                    kanPrice.setText(item.getShowPrice());
                                    zhuankanName.setText(item.getName());
                                    break;
                                case 7:
                                    if (SlateDataHelper.getLevelByType(context, item.getReadLevel())) {
                                        fmDing.setImageResource(R.drawable.yigou);
                                    } else fmDing.setImageResource(R.drawable.dinggou);
                                    fmDing.setTag(item);
                                    fmPrice.setText(item.getShowPrice());
                                    fmName.setText(item.getName());
                                    break;
                                case 8:
                                    if (SlateDataHelper.getLevelByType(context, item.getReadLevel())) {
                                        duDing.setImageResource(R.drawable.yigou);
                                    } else duDing.setImageResource(R.drawable.dinggou);
                                    duDing.setTag(item);
                                    duPrice.setText(item.getShowPrice());
                                    dubaoName.setText(item.getName());
                                    break;
                                case 9:
                                    if (SlateDataHelper.getLevelByType(context, item.getReadLevel())) {
                                        tiDing.setImageResource(R.drawable.yigou);
                                    } else tiDing.setImageResource(R.drawable.dinggou);
                                    tiDing.setTag(item);
                                    tiPrice.setText(item.getShowPrice());
                                    zhuantiName.setText(item.getName());
                                    break;
                                case 10:
                                    if (SlateDataHelper.getLevelByType(context, item.getReadLevel())) {
                                        keDing.setImageResource(R.drawable.yigou);
                                    } else keDing.setImageResource(R.drawable.dinggou);
                                    keDing.setTag(item);
                                    kePrice.setText(item.getShowPrice());
                                    kechengName.setText(item.getName());
                                    break;


                            }
                            kePrice.setText(item.getShowPrice());
                        }

                    }
                    break;
                case 1:// load view
                    zhuankanLayout.removeAllViews();
                    for (int i = 0; i < 3; i++) {
                        if (zhuankanDatas.size() > i)
                            zhuankanLayout.addView(getZhuankanItem(zhuankanDatas.get(i)), sanPamas);
                    }
                    dubaoLayout.removeAllViews();
                    for (int i = 0; i < 3; i++) {
                        if (dubaoDatas.size() > i)
                            dubaoLayout.addView(getDuBaoItem(dubaoDatas.get(i)));
                    }

                    fmLayout.removeAllViews();
                    for (int i = 0; i < 3; i++) {
                        if (fmDatas.size() > i)
                            fmLayout.addView(getFmItem(fmDatas.get(i)), sanPamas);
                    }
                    if (ParseUtil.listNotNull(zhuantiDatas) && ParseUtil.listNotNull(zhuantiDatas.get(0).getPicList())) {
                        zhuantiLayout.removeAllViews();
                        zhuantiLayout.addView(getZhuantiItem(zhuantiDatas.get(0)));
                    }
                    kechengLayout.removeAllViews();
                    for (int i = 0; i < 3; i++) {
                        if (kechengDatas.size() > i)
                            kechengLayout.addView(getKechengItem(kechengDatas.get(i)));
                    }
                    //                    this.sendEmptyMessageDelayed(5, 500);
                    break;

                case 5:
                    //                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    //                    scrollView.scrollTo(0, 0);
                    break;

                case 3:// 更新head广告
                    if (headDatas.size() > 0) {
                        headViewPager.setVisibility(View.VISIBLE);
                        headAdapter.notifyDataSetChanged();
                        specialTitle.setVisibility(GONE);
                    } else {
                        headViewPager.setVisibility(View.GONE);
                        specialTitle.setVisibility(VISIBLE);
                    }

                    break;
                case 4:// 更新head下方广告
                    if (ParseUtil.listNotNull(unheadDatas)) {
                        ad2Layout.setVisibility(View.VISIBLE);
                        AdvItem advItem = unheadDatas.get(0);
                        if (advItem != null && ParseUtil.listNotNull(advItem.getSourceList())) {
                            if (!TextUtils.isEmpty(advItem.getSourceList().get(0).getUrl()))
                                MyApplication.finalBitmap.display(ad2Img, advItem.getSourceList().get(0).getUrl());
                            ad2Title.setText(advItem.getSourceList().get(0).getDesc());
                            ad2Title.setTag(advItem.getSourceList().get(0).getLink());
                            ad2Time.setText(DateFormatTool.format(Long.valueOf(advItem.getStartTime()), "yyyy.MM.dd hh:mm:ss"));
                        }
                    }

                    break;

            }
        }
    };


    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.main_shangcheng_view, null);
        addView(view);
        specialTitle = view.findViewById(R.id.special_title_rl);
        headViewPager = (ViewPager) view.findViewById(R.id.shang_ad1);
        headAdapter = new ShangHeadAdapter(context, headDatas);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonApplication.width, CommonApplication.width / 2);
        headViewPager.setLayoutParams(params);
        headViewPager.setAdapter(headAdapter);
        ad2Layout = (RelativeLayout) view.findViewById(R.id.shang_ad2);
        ad2Img = (ImageView) view.findViewById(R.id.shang_ad2_img);
        ad2Time = (TextView) view.findViewById(R.id.shang_ad2_time);
        ad2Title = (TextView) view.findViewById(R.id.shang_ad2_title);
        zhuankanLayout = (LinearLayout) view.findViewById(R.id.shang_zhuankan_layout);
        dubaoLayout = (LinearLayout) view.findViewById(R.id.shang_dubao_layout);
        fmLayout = (LinearLayout) view.findViewById(R.id.shang_fm_layout);
        zhuantiLayout = (LinearLayout) view.findViewById(R.id.shang_zhuantipian_layout);
        kechengLayout = view.findViewById(R.id.shang_kecheng_layout);
        kePrice = view.findViewById(R.id.kecheng_price);
        keDing = view.findViewById(R.id.kecheng_order);
        kanPrice = view.findViewById(R.id.zhuankan_price);
        kanDing = view.findViewById(R.id.zhuankan_order);
        duPrice = view.findViewById(R.id.dubao_price);
        duDing = view.findViewById(R.id.dubao_order);
        fmPrice = view.findViewById(R.id.fm_price);
        fmDing = view.findViewById(R.id.fm_order);
        tiPrice = view.findViewById(R.id.zhuanti_price);
        tiDing = view.findViewById(R.id.zhuanti_order);

        //商品名称
        kechengName = view.findViewById(R.id.kecheng_name);
        zhuankanName = view.findViewById(R.id.zhuankan_name);
        dubaoName = view.findViewById(R.id.dubao_name);
        fmName = view.findViewById(R.id.fm_name);
        zhuantiName = view.findViewById(R.id.zhuantipian_name);

        //商品layout
        kechengBigLayout = view.findViewById(R.id.shang_kecheng_big_layout);
        zhuankanBigLayout = view.findViewById(R.id.shang_zhuankan_big_layout);
        dubaoBigLayout = view.findViewById(R.id.shang_dubao_big_layout);
        fmBigLayout = view.findViewById(R.id.shang_fm_big_layout);
        zhuantiBigLayout = view.findViewById(R.id.shang_zhuanti_big_layout);


        view.findViewById(R.id.ke_11).setOnClickListener(this);
        view.findViewById(R.id.ke_22).setOnClickListener(this);
        view.findViewById(R.id.kan_11).setOnClickListener(this);
        view.findViewById(R.id.kan_22).setOnClickListener(this);
        view.findViewById(R.id.fm_11).setOnClickListener(this);
        view.findViewById(R.id.fm_22).setOnClickListener(this);
        view.findViewById(R.id.ti_11).setOnClickListener(this);
        view.findViewById(R.id.ti_22).setOnClickListener(this);
        view.findViewById(R.id.du_11).setOnClickListener(this);
        view.findViewById(R.id.du_22).setOnClickListener(this);

        keDing.setOnClickListener(this);
        kanDing.setOnClickListener(this);
        duDing.setOnClickListener(this);
        fmDing.setOnClickListener(this);
        tiDing.setOnClickListener(this);

        ad2Layout.setOnClickListener(this);

        sanPamas = new LayoutParams(CommonApplication.width / 3, LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < 3; i++) {
            zhuankanLayout.addView(getZhuankanItem(null), sanPamas);
            fmLayout.addView(getFmItem(null), sanPamas);
            dubaoLayout.addView(getDuBaoItem(null), sanPamas);
        }
    }

    private View getDuBaoItem(final ArticleItem a) {
        TextView view = new TextView(context);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setPadding(40, 20, 40, 20);
        view.setMaxLines(2);
        view.setTextSize(14);
        view.setTextColor(Color.BLACK);

        Drawable aaa = context.getResources().getDrawable(R.drawable.shidu_dot);
        aaa.setBounds(0, 0, 16, 16);
        view.setCompoundDrawables(aaa, null, null, null);
        view.setCompoundDrawablePadding(20);

        if (a == null) return view;
        if (a.getProperty().getLevel() == 0 && !SlateDataHelper.getLevelByType(context, 4)) {
            Drawable bb = getResources().getDrawable(R.drawable.shidu);
            bb.setBounds(0, 0, 80, 40);
            ImageSpan imgSpan = new ImageSpan(bb);
            SpannableString spanString = new SpannableString("  ssssssssss  ");
            spanString.setSpan(imgSpan, 2, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(spanString);
            view.append(a.getTitle());
        } else view.setText(a.getTitle());

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a.getProperty().getLevel() == 0 || SlateDataHelper.getLevelByType(context, 4)) {
                    Intent intent = new Intent(context, CommonApplication.pushArticleCls);
                    intent.putExtra("is_need_share", false);
                    intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, a.getArticleId() + "");
                    intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL, a.getProperty().getLevel());
                    context.startActivity(intent);
                } else {
                    goInfo(8);
                }

            }
        });
        return view;
    }

    private void goInfo(int type) {
        if (shangchengIndex == null) return;
        Intent i = new Intent(context, ShangchengInfoActivity.class);
        for (ShangchengIndex.ShangchengIndexItem indexItem : shangchengIndex.getDatas()) {
            if (indexItem.getCmsShowStyle() == type) {
                i.putExtra("ShangchengInfo_info", indexItem);
            }
        }
        i.putExtra("ShangchengInfo_type", type);
        context.startActivity(i);
    }

    private View getKechengItem(final ArticleItem a) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shang_kecheng, null);
        ImageView img = view.findViewById(R.id.shang_kecheng_img);
        TextView title = view.findViewById(R.id.shang_kecheng_title);

        img.setImageResource(R.drawable.placeholder);
        if (a.getThumbList() != null && a.getThumbList().size() > 0) {
            img.setTag(a.getThumbList().get(0).getUrl());
            img.setId(a.getArticleId());
            CommonApplication.finalBitmap.display(img, (String) img.getTag());
        } else if (a.getPicList() != null && a.getPicList().size() > 0) {
            img.setTag(a.getPicList().get(0).getUrl());
            img.setId(a.getArticleId());
            CommonApplication.finalBitmap.display(img, (String) img.getTag());
        }

        if (a.getProperty().getLevel() == 0 && !SlateDataHelper.getLevelByType(context, 7)) {//富文本
            Drawable bb = context.getResources().getDrawable(R.drawable.shidu);
            bb.setBounds(0, 0, 80, 40);
            ImageSpan imgSpan = new ImageSpan(bb);
            SpannableString spanString = new SpannableString("  ssssssssss  ");
            spanString.setSpan(imgSpan, 2, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(spanString);
            title.append(a.getTitle());
        } else title.setText(a.getTitle());
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a.getProperty().getLevel() > 0 && !SlateDataHelper.getLevelByType(context, 7)) {
                    goInfo(10);
                } else {
                    Intent intent = new Intent(context, CommonApplication.pushArticleCls);
                    intent.putExtra("is_need_share", false);
                    intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_ID, a.getArticleId() + "");
                    intent.putExtra(TagProcessManage.KEY_PUSH_ARTICLE_LEVEL, a.getProperty().getLevel());
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }

    private View getZhuantiItem(final ArticleItem a) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shang_jilu, null);
        view.setPadding(30, 20, 30, 20);
        FullVideoView videoView = view.findViewById(R.id.shang_jilu_video);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(CommonApplication.width - 60, (CommonApplication.width - 60) * 9 / 16);
        videoView.setLayoutParams(ll);

        TextView title = view.findViewById(R.id.shang_jilu_title);
        title.setPadding(0, 20, 0, 0);
        TextView desc = view.findViewById(R.id.shang_jilu_desc);
        desc.setPadding(0, 10, 0, 20);
        desc.setMaxLines(2);
        desc.setEllipsize(TextUtils.TruncateAt.END);
        if (a == null) return view;
        videoView.setData(a, a.getProperty().getLevel(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, new FullVideoView.OnVideoStartListener() {
            @Override
            public void onHasLevel(boolean hasLevel) {
                if (!hasLevel) {
                    goInfo(9);
                }
            }
        });

        if (a.getProperty().getLevel() == 0 && !SlateDataHelper.getLevelByType(context, 5)) {
            Drawable bb = getResources().getDrawable(R.drawable.shikan);
            bb.setBounds(0, 0, 80, 40);
            ImageSpan imgSpan = new ImageSpan(bb);
            SpannableString spanString = new SpannableString("  ssssssssss  ");
            spanString.setSpan(imgSpan, 2, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(spanString);
        }
        title.append(a.getTitle());

        desc.setText(a.getDesc());
        return view;
    }

    private View getFmItem(final ArticleItem a) {
        RelativeLayout view = new RelativeLayout(context);
        ImageView img = new ImageView(context);
        view.setPadding(40, 15, 40, 10);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        int width = CommonApplication.width / 3 - 80;
        LayoutParams l = new LayoutParams(width, width);
        img.setLayoutParams(l);
        img.setImageResource(R.drawable.head_placeholder);
        view.addView(img);
        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setLines(2);
        title.setPadding(4, 10, 4, 10);
        title.setTextSize(14);
        title.setTextColor(Color.BLACK);
        view.addView(title);


        ImageView erji = new ImageView(context);
        erji.setImageResource(R.drawable.fm_icon);
        LayoutParams lll = new LayoutParams(110, 110);
        lll.topMargin = width / 2 - 55;
        lll.addRule(RelativeLayout.CENTER_HORIZONTAL);
        erji.setLayoutParams(lll);
        view.addView(erji);
        if (a == null) return view;

        // 免费且没有权限
        if (a.getProperty().getLevel() == 0 && !SlateDataHelper.getLevelByType(context, 3)) {
            ImageView icon = new ImageView(context);
            icon.setImageResource(R.drawable.shiting);
            icon.setScaleType(ImageView.ScaleType.FIT_XY);
            LayoutParams ll = new LayoutParams(90, 45);
            icon.setLayoutParams(ll);
            view.addView(icon);
        }
        if (a.getPicList() != null && a.getPicList().size() > 0) {
            img.setTag(a.getPicList().get(0).getUrl());
            img.setId(a.getArticleId());
            CommonApplication.finalBitmap.display(img, (String) img.getTag());
        }

        title.setText(a.getTitle());
        View childView1 = view.getChildAt(0);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, childView1.getId());//设置item3在     //chlidView1的下面
        title.setLayoutParams(lp);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a.getProperty().getLevel() > 0 && !SlateDataHelper.getLevelByType(context, 3)) {
                    goInfo(7);
                } else goListActivity(7, a.getArticleId());
            }
        });

        return view;
    }

    private View getZhuankanItem(final ArticleItem a) {
        LinearLayout view = new LinearLayout(context);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setPadding(40, 15, 40, 10);

        RelativeLayout rela = new RelativeLayout(context);

        ImageView bg = new ImageView(context);
        bg.setBackground(context.getResources().getDrawable(R.drawable.zhuankan_bg));
        int width = CommonApplication.width / 3 - 80;
        LayoutParams l = new LayoutParams(width, width * 4 / 3);
        bg.setLayoutParams(l);
        rela.addView(bg);

        final RoundAngleImageView cover = new RoundAngleImageView(context, 12);
        LayoutParams ll = new LayoutParams(width - 8, (width - 8) * 4 / 3);
        ll.setMargins(6, 0, 2, 8);
        cover.setLayoutParams(ll);
        cover.setScaleType(ImageView.ScaleType.FIT_XY);
        cover.setImageResource(R.drawable.head_placeholder);
        rela.addView(cover);

        TextView title = new TextView(context);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setLines(2);
        title.setPadding(4, 10, 4, 10);
        title.setTextSize(14);
        title.setTextColor(Color.BLACK);
        view.addView(rela);
        view.addView(title);
        if (a == null) return view;
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (getStatus(a)) {
                    case 0:
                        Intent ii = new Intent(context, ShangchengInfoActivity.class);
                        ii.putExtra("ShangchengInfo_info", shangchengIndex);
                        ii.putExtra("ShangchengInfo_type", 6);
                        context.startActivity(ii);
                        break;
                    case 1:// 在这个页面，下载和缓存放在列表页面进行
                        goPdfActivity(a);
                        break;
                    case 2://查看
                        goPdfActivity(a);
                        break;
                    case 3://试读
                        goPdfActivity(a);
                        break;
                }
            }
        });

        if (a.getPicList() != null && a.getPicList().size() > 0) {
            cover.setTag(a.getPicList().get(0).getUrl());
            CommonApplication.finalBitmap.display(cover, (String) cover.getTag());
        }

        title.setText(a.getTitle());
        return view;
    }

    private void goPdfActivity(ArticleItem a) {
        Intent j = new Intent(context, PDFActivity.class);
        j.putExtra("pdf_article_item", a);
        context.startActivity(j);

    }

    /**
     * 获取当前电子书的状态
     * 0 ：购买 1：下载 2：查看 3：试读
     *
     * @return
     */
    private int getStatus(ArticleItem articleItem) {
        if (articleItem.getProperty().getLevel() == 0) {
            if (FileManager.ifHasPdfFilePath(articleItem.getPageUrlList().get(0).getUrl())) {
                return 2;
            } else {
                return 1;
            }
        } else {
            if (SlateDataHelper.getLevelByType(context, 2)) {
                if (FileManager.ifHasPdfFilePath(articleItem.getPageUrlList().get(0).getUrl())) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                if (articleItem.getFreePage() == 0) {
                    return 0;
                } else {
                    return 3;
                }
            }
        }
    }


}
