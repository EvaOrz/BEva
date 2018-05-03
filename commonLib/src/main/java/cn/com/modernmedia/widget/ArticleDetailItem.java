package cn.com.modernmedia.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import cn.com.modernmedia.CommonArticleActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.CallWebStatusChangeListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.pay.GooglePayActivity;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;

/**
 * 文章详情
 *
 * @author ZhuQiao
 */
public abstract class ArticleDetailItem extends BaseView implements CallWebStatusChangeListener {
    private Context mContext;
    private CommonWebView webView;
    private ArticleItem detail;
    private int errorType;
    private boolean bgIsTransparent, isSlateWeb;
    private LinearLayout web_contain;
    private RelativeLayout payView;// 付费遮盖层
    private View mView;

    private WebProcessListener listener = new WebProcessListener() {

        @Override
        public void showStyle(int style) {
            errorType = style;
            if (style == 0) {
                disProcess();
            } else if (style == 1) {
                showLoading();
            } else if (style == 2) {
                showError();
            }
        }

    };

    public ArticleDetailItem(Context context, boolean bgIsTransparent) {
        this(context, bgIsTransparent, false);
    }

    public ArticleDetailItem(Context context, boolean bgIsTransparent, boolean isSlateWeb) {
        super(context);
        mContext = context;
        this.bgIsTransparent = bgIsTransparent;
        this.isSlateWeb = isSlateWeb;
        init(bgIsTransparent, isSlateWeb);
    }

    /**
     * 初始化
     *
     * @param bgIsTransparent webview 背景是否透明
     * @param isSlateWeb      是否slate跳转内部浏览器
     */
    @SuppressLint("NewApi")
    private void init(boolean bgIsTransparent, boolean isSlateWeb) {
        this.addView(LayoutInflater.from(mContext).inflate(R.layout.article_detail, null));
        this.setBackgroundColor(Color.WHITE);
        initProcess();
        initPayView();
        web_contain = (LinearLayout) findViewById(R.id.web_contain);
        webView = new CommonWebView(mContext, bgIsTransparent) {

            @Override
            public void gotoGalleryActivity(List<String> urlList, String currentUrl, List<String> descList) {
                showGallery(urlList, currentUrl, descList);
            }

            @Override
            protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                doScroll(l, t, oldl, oldt);
            }

            @Override
            public void gotoWriteNewCardActivity(ArticleItem item) {
                myGotoWriteNewCardActivity(item);
            }

        };
        //        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setListener(listener);
        webView.setSlateWeb(isSlateWeb);
        web_contain.addView(webView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        setBackGroundRes((ImageView) findViewById(R.id.web_back));
    }

    /**
     * 初始化付费遮盖view
     */
    private void initPayView() {
        payView = (RelativeLayout) findViewById(R.id.default_pay_view);
        payView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SlateApplication.APP_ID == 37 || SlateApplication.APP_ID == 18) { // 号外跳转google支付
                    Intent i = new Intent();
                    i.setClass(mContext, GooglePayActivity.class);
                    mContext.startActivity(i);

                } else if (SlateDataHelper.getUserLoginInfo(mContext) == null) {//未登录
                    String broadcastIntent = "cn.com.modernmediausermodel.LoginActivity_nomal";
                    Intent intent = new Intent(broadcastIntent);
                    mContext.sendBroadcast(intent);
                } else if ((SlateDataHelper.getUserLoginInfo(mContext) != null)) {//普通用户 非付费用户,非VIP

                    /**
                     * 点击付费遮盖层 取用户权限--没有权限，跳往开通vip页面
                     */
                    PayHttpsOperate.getInstance(mContext).getUserPermission(new FetchDataListener() {
                        @Override
                        public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                            if (isSuccess) {
                                PayHttpsOperate.getInstance(mContext).saveLevel(data);
                            }

                            if (detail.getProperty().getLevel() > 0 && !SlateDataHelper.getLevelByType(mContext, detail.getProperty().getLevel())) {
                                String broadcastIntent = "cn.com.modernmediausermodel.VipOpenActivity_nomal";
                                Intent intent = new Intent(broadcastIntent);
                                mContext.sendBroadcast(intent);
                            } else {
                                ((CommonArticleActivity) mContext).reLoadData();
                            }
                        }
                    });


                }

            }

        });

        ImageView payBanner = (ImageView) findViewById(R.id.pay_bannar_view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pay_bannar_view);
        int bwidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        bitmap.recycle();// 释放

        int height = mContext.getResources().getDisplayMetrics().widthPixels * bHeight / bwidth;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        payBanner.setLayoutParams(lp);
    }


    public void setData(ArticleItem detail) {
        this.detail = detail;
        int level = detail.getProperty().getLevel();// 付费
        //        webView.startLoadHtml(detail.getArticleId(),detail.getHtml());
        loadArticleHtml();
        if (level > 0 && !SlateDataHelper.getLevelByType(mContext, level)) {//
            // 如果是商周，并且需要付费

            payView.setVisibility(View.VISIBLE);
        }
    }

    //设置参数
    public void setGallery(ArticleItem detail, View view) {
        this.detail = detail;
        this.mView = view;
        UriParse.clickSlate(mContext, new ArticleItem[]{detail}, view);


    }

    /**
     * 改变行间距
     */
    @Override
    public void changeLineHeight() {
        webView.changeLineHeight();
    }

    /**
     * 改变字体
     */
    @Override
    public void changeFontSize() {
        webView.changeFont();
    }

    @Override
    protected void reLoad() {
        // webView.reload();
        listener.showStyle(1);
        loadArticleHtml();
    }

    public CommonWebView getWebView() {
        return webView;
    }

    /**
     * 获取文章详情
     */
    private void loadArticleHtml() {
        OperateController.getInstance(mContext).getArticleHtml(detail.getArticleId(), new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                if (entry instanceof TagArticleList) {
                    TagArticleList tagArticleList = (TagArticleList) entry;
                    if (ParseUtil.listNotNull(tagArticleList.getArticleList())) {
                        detail = tagArticleList.getArticleList().get(0);
                        webView.startLoadHtml(detail);
                    }
                }
            }
        });
    }

    @Override
    public void checkPayStatus() {
        if (detail.getProperty().getLevel() > 0 && !SlateDataHelper.getLevelByType(mContext, detail.getProperty().getLevel())) {// 如果是商周或者Vericity，并且需要付费
            payView.setVisibility(View.VISIBLE);
        } else {
            payView.setVisibility(View.GONE);
            //不需要刷新
            //            if(detail != null &&! TextUtils.isEmpty(detail.getHtml())){
            //                webView.startLoadHtml(detail);
            //            }
        }
    }

    public abstract void setBackGroundRes(ImageView imageView);

    public abstract void showGallery(List<String> urlList, String currentUrl, List<String> descList);

    /**
     * 跳转至写卡片页
     *
     * @param item
     */
    public void myGotoWriteNewCardActivity(ArticleItem item) {
    }

    public void doScroll(int l, int t, int oldl, int oldt) {
    }

    ;

    public void changeFont() {
        webView.changeFont();
    }

    public int getErrorType() {
        return errorType;
    }

    public ArticleItem getDetail() {
        return detail;
    }

}