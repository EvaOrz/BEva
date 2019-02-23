package cn.com.modernmedia.businessweek.jingxuan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.SplashScreenActivity;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ShangchengIndex;
import cn.com.modernmedia.model.ShangchengIndex.ShangchengIndexItem;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.vip.VipProductPayActivity;

/**
 * 商城简介页面
 * Created by Eva. on 2017/8/31.
 */

public class ShangchengInfoActivity extends BaseActivity implements View.OnClickListener {

    private int type;// 6专刊；8读报；7FM；9专题片；10课程；1金融研究所
    private CommonWebView webView;
    private ShangchengIndexItem shangchengIndexItem;// 显示简介model
    private TextView button1, button2, kaitong, title,kaitongPrice;
    private LinearLayout payLayout,kaitongLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shangcheng_info);
        type = getIntent().getIntExtra("ShangchengInfo_type", 0);
        isFromSplash = getIntent().getBooleanExtra("is_from_splash", false);
        initView();
        if (getIntent().getSerializableExtra("ShangchengInfo_info") == null) {//
            // 课程需要单独获取购买数据
            String senid = getIntent().getStringExtra("ShangchengInfo_senid");
            initSplashData(senid);
        } else {
            shangchengIndexItem = (ShangchengIndexItem) getIntent().getSerializableExtra("ShangchengInfo_info");
            int cmsShowStyle = shangchengIndexItem.getCmsShowStyle();
            if (cmsShowStyle == 10 || cmsShowStyle == 6 || cmsShowStyle == 8 || cmsShowStyle == 7 || cmsShowStyle == 9) {
                initSplashData(shangchengIndexItem.getId());
            }
            handler.sendEmptyMessage(
                    0);
        }


    }

    private void initSplashData(String senid) {
        OperateController.getInstance(this).getShangchengSplash(this, senid, new FetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                Log.i("===", "setData: " + shangchengIndexItem);
                if (entry != null && entry instanceof ShangchengIndex.ShangchengIndexItem) {
                    shangchengIndexItem = (ShangchengIndexItem) entry;
                    if (type == 10) {
                        handler.sendEmptyMessage(1);
                    } else handler.sendEmptyMessage(0);
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        if (shangchengIndexItem != null) {
                            title.setText(shangchengIndexItem.getName());
                            webView.loadUrl(URLDecoder.decode(shangchengIndexItem.getDescUrl(), "UTF-8"));
                            if (shangchengIndexItem.getCmsShowStyle() == 1) kaitongLayout.setVisibility(View.GONE);//

                            //获取商品设置单独付费价格
                            if (ParseUtil.listNotNull(shangchengIndexItem.getGoods())) {
                                kaitongPrice.setText(shangchengIndexItem.getGoods().get(0).getShowPrice());
                                kaitongPrice.setTag(shangchengIndexItem.getGoods().get(0));

                                if (shangchengIndexItem.getGoods().size() > 1) {
                                  kaitong.setText(shangchengIndexItem.getGoods().get(1).getShowPrice());
                                  kaitong.setTag(shangchengIndexItem.getGoods().get(1));
                                }
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                    }
                    break;
                case 1:
                    loadStatus();
                    break;
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        loadStatus();
    }

    private void initView() {
        webView = (CommonWebView) findViewById(R.id.shang_info_webview);
        title = (TextView) findViewById(R.id.shang_title);
        payLayout = findViewById(R.id.ke_price_layout);
        button1 = findViewById(R.id.ke_price1);
        button2 = findViewById(R.id.ke_price2);
        kaitong = findViewById(R.id.kaitong_text);
        kaitong.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        findViewById(R.id.shang_back).setOnClickListener(this);

        kaitongLayout = findViewById(R.id.kaitong_layout);
        kaitongPrice = findViewById(R.id.kaitong_price);
        kaitongPrice.setOnClickListener(this);
    }

    private void loadStatus() {
        if (type == 1) {// 金融研究课
            kaitongLayout.setVisibility(View.GONE);
            payLayout.setVisibility(View.GONE);
            if (CommonApplication.loginStatusChange) handler.sendEmptyMessage(0);
        } else if (type == 10) {// 课程
            if (shangchengIndexItem != null && SlateDataHelper.getUserReadLevel(this).contains(shangchengIndexItem.getReadLevel() + "")) {
                payLayout.setVisibility(View.GONE);
                return;
            }
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

        }else if (type == 0){
            kaitongLayout.setVisibility(View.GONE);
            payLayout.setVisibility(View.GONE);
        }else {
            kaitongLayout.setVisibility(View.VISIBLE);
            payLayout.setVisibility(View.GONE);

            if (SlateDataHelper.getUserReadLevel(this).contains(shangchengIndexItem.getReadLevel() + "")/*SlateDataHelper.getVipLevel(this) > 1*/) {
                kaitongLayout.setVisibility(View.GONE);
            } else {
                if (shangchengIndexItem != null && ParseUtil.listNotNull(shangchengIndexItem.getGoods())) {
                    kaitongPrice.setText(shangchengIndexItem.getGoods().get(0).getShowPrice());
                    if (shangchengIndexItem.getGoods().size() > 1) {
                        kaitong.setText(shangchengIndexItem.getGoods().get(1).getShowPrice());
                    }
                }
                /*if (SlateDataHelper.getVipLevel(this) == 1) {
                    kaitong.setText("升级VIP进阶套餐");
                } else if (SlateDataHelper.getVipLevel(this) > 1) {
                    kaitong.setText("查看列表");
                } else {
                    kaitong.setText("开通VIP进阶套餐");
                }*/
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shang_back:
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
            case R.id.kaitong_price:
                if (SlateDataHelper.getVipLevel(ShangchengInfoActivity.this) > 1) {// vip2\3 查看列表
                    Intent i = new Intent(ShangchengInfoActivity.this, ShangchengListActivity.class);
                    i.putExtra("ShangchengList_type", type);
                    if (type == 7) i.putExtra("ShangchengList_fm", 0);
                    else i.putExtra("ShangchengList_fm", -1);
                    i.putExtra("ShangchengList_info", shangchengIndexItem);
                    startActivity(i);
                } else {
                    UserPageTransfer.gotoShangchengActivity(ShangchengInfoActivity.this, false);
                }
                break;
            case R.id.kaitong_text:
                if ((type == 7 || type == 8 || type == 6 || type == 9) && kaitong.getTag() != null && kaitong.getTag() instanceof VipGoodList.VipGood) {
                    System.out.println(shangchengIndexItem);
                    VipGoodList.VipGood good = (VipGoodList.VipGood) kaitong.getTag();
                    goPay(good);
                }
                break;
        }

    }


    private void goPay(VipGoodList.VipGood good) {
        Intent i = new Intent(ShangchengInfoActivity.this, VipProductPayActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("product", good);
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    public void reLoadData() {

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
        return ShangchengInfoActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return ShangchengInfoActivity.this;
    }
}
