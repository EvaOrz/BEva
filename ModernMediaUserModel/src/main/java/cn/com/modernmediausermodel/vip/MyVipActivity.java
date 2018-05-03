package cn.com.modernmediausermodel.vip;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmedia.widget.GifView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.api.SlateBaseOperate;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.UserNewInfoActivity;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 我的VIP页面
 *
 * @author: zhufei
 */
public class MyVipActivity extends SlateBaseActivity implements View.OnClickListener {
    private TextView vip_card_number;
    private TextView vip_card_first;
    private TextView vip_card_endtime;
    private ImageView vip_card_info_img;//VIP信息二维码图
    private User mUser;
    private ImageView adImg;
    private GifView adGif;
    private WebView adWeb;
    private List<AdvList.AdvItem> advItems = new ArrayList<>();

    private PayHttpsOperate controller;
    private List<VipGoodList.VipGood> pros = new ArrayList<>();
    private List<VipGoodList.Fun> funList = new ArrayList<>();
    private MyVipFunAdapter adapter;
    private LinearLayout right_linear;
    private Button xufeiButton, upButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myvip);
        initView();
        initAdv();

    }

    private void initView() {
        SlateApplication.getInstance().addActivity(this);
        controller = PayHttpsOperate.getInstance(this);
        mUser = SlateDataHelper.getUserLoginInfo(this);
        if (mUser == null) {
            UserPageTransfer.gotoLoginActivity(this, 0);
        }
        vip_card_number = (TextView) findViewById(R.id.vip_card_number);
        vip_card_first = (TextView) findViewById(R.id.vip_card_first);
        vip_card_endtime = (TextView) findViewById(R.id.vip_card_endtime);
        TextView vip_open_phone = (TextView) findViewById(R.id.myvip_phone);
        vip_card_info_img = (ImageView) findViewById(R.id.vip_card_info_img);
        right_linear = (LinearLayout) findViewById(R.id.vip_right_linear);
        GridView gridView = (GridView) findViewById(R.id.myvip_gridview);
        adImg = (ImageView) findViewById(R.id.myvip_ad_img);
        findViewById(R.id.myvip_ad_layout).setOnClickListener(this);

        adapter = new MyVipFunAdapter(this);
        gridView.setAdapter(adapter);
        initMyPermission();
        setMyVip();
        findViewById(R.id.vip_myvip_back).setOnClickListener(this);
        findViewById(R.id.vip_mine_info).setOnClickListener(this);
        findViewById(R.id.vip_myvip_notice).setOnClickListener(this);
        findViewById(R.id.myvip_more).setOnClickListener(this);

        xufeiButton = (Button) findViewById(R.id.vip_mine_pay);
        xufeiButton.setOnClickListener(this);
        upButton = (Button) findViewById(R.id.vip_mine_up);
        upButton.setOnClickListener(this);
        vip_card_info_img.setOnClickListener(this);
        vip_open_phone.setOnClickListener(this);
        SpannableStringBuilder mBuilder = new SpannableStringBuilder(getString(R.string.vip_open_phone_numb));
        mBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), 6, 16, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        vip_open_phone.setText(mBuilder);
    }

    /*设置电话颜色*/
    private void setPhone(TextView textView) {

    }

    /**
     * 初始化广告
     */
    private void initAdv() {
        OperateController.getInstance(this).getAdvList(SlateBaseOperate.FetchApiType.USE_CACHE_ONLY, new FetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                if (entry != null && entry instanceof AdvList) {
                    AdvList advDatas = (AdvList) entry;
                    Map<Integer, List<AdvList.AdvItem>> advMap = advDatas.getAdvMap();
                    if (ParseUtil.mapContainsKey(advMap, AdvList.MYVIP_ADV)) {
                        advItems.clear();
                        advItems.addAll(advMap.get(AdvList.MYVIP_ADV));
                        mHandler.sendEmptyMessage(1);
                    }

                }
            }
        });
    }

    private void setMyVip() {
        long time;
        if (!TextUtils.isEmpty(mUser.getVip())) {
            vip_card_number.setText(String.format(getString(R.string.vip_card_no), mUser.getVip()));//VIP卡号id
        }
        if (mUser.getStart_time() > 0) {
            vip_card_first.setText(String.format(getString(R.string.vip_start_time), Utils.strToDate(mUser.getStart_time())));//首次开通VIP时间
        }
        time = mUser.getVip_end_time();
        vip_card_endtime.setText(String.format(getString(R.string.vip_show_endtime), Utils.strToDate(time)));//有效期

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_myvip_back) {
            SlateApplication.getInstance().exitActivity();
            finish();
        } else if (v.getId() == R.id.myvip_more) {//更多套餐详情
            doIntent(VipPlanDetailActivity.class);
        } else if (v.getId() == R.id.vip_mine_pay) {//续费
            if (mUser.getUser_status() == 2) {
                LogHelper.logVipOnlinepay(this, "renew", pros.get(0).getGoodId());
                doIntent(VipProductPayActivity.class);
            } else {//VIP过期
                LogHelper.checkVipOpen(this, "me-renew");
                doIntent(VipOpenActivity.class);
            }
        } else if (v.getId() == R.id.vip_mine_up) {// 套餐升级
            startActivity(new Intent(MyVipActivity.this, TaocanUpActivity.class));

        } else if (v.getId() == R.id.vip_mine_info) {//VIP会员信息
            startActivity(new Intent(MyVipActivity.this, UserNewInfoActivity.class));
        } else if (v.getId() == R.id.vip_myvip_notice) {//VIP会员说明
            UriParse.doLinkWeb(MyVipActivity.this, UrlMaker.vipNotice(),true);
        } else if (v.getId() == R.id.myvip_phone) {//客服
            VipOpenActivity.setCallPhone(this);
        } else if (v.getId() == R.id.vip_card_info_img) {
            showPopupWindow(vip_card_info_img);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.3f;
            getWindow().setAttributes(lp);
        } else if (v.getId() == R.id.myvip_ad_layout) {// 广告点击
            if (ParseUtil.listNotNull(advItems)) {
                UriParse.clickSlate(MyVipActivity.this, advItems.get(0).getSourceList().get(0).getLink(), new Entry[]{new ArticleItem()}, null, new Class<?>[0]);
            }


        }

    }

    private void doIntent(Class mClass) {
        Intent i = new Intent(MyVipActivity.this, mClass);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", pros.get(0));
        bundle.putInt("right", 10);
        i.putExtras(bundle);
        startActivityForResult(i, 306);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 306 && resultCode == 403) {//确认-->支付-->交易成功
            finish();
        } else if (requestCode == 306 && resultCode == 404) {//不走确认页，支付成功
            finish();
        } else if (requestCode == 306 && resultCode == 401) {//vip过期，续费结果页返回
            finish();
        } else if (requestCode == 306 && resultCode == 402) {//vip，续费结果页
            finish();
        }
    }

    private void showPopupWindow(View view) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.vip_qrcode, null);
        ImageView avatar = (ImageView) contentView.findViewById(R.id.vip_qr_avatar);
        ImageView vip_qr = (ImageView) contentView.findViewById(R.id.vip_qrcode);
        TextView nickname = (TextView) contentView.findViewById(R.id.vip_qr_nickname);
        TextView city = (TextView) contentView.findViewById(R.id.vip_qr_city);
        FinalBitmap.create(this).display(vip_qr, cn.com.modernmediausermodel.api.UrlMaker.getQrCode(mUser.getUid(), mUser.getToken()));
        nickname.setText(mUser.getNickName());
        if (!TextUtils.isEmpty(mUser.getProvince()) && !TextUtils.isEmpty(mUser.getCity())) {
            city.setText(mUser.getProvince() + " " + mUser.getCity());
        } else city.setText(getString(R.string.vip_city_null));
        UserTools.setAvatar(this, mUser.getAvatar(), avatar);
        final PopupWindow popupWindow = new PopupWindow(contentView);
        int h = getWindowManager().getDefaultDisplay().getHeight();
        int w = getWindowManager().getDefaultDisplay().getWidth();
        popupWindow.setHeight(h * 6 / 10);
        popupWindow.setWidth(w * 3 / 4);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_corner_bg));

        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

    }

    @Override
    public String getActivityName() {
        return MyVipActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        setMyVip();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (ParseUtil.listNotNull(pros)) {
                        right_linear.setVisibility(View.VISIBLE);
                        funList.clear();
                        xufeiButton.setVisibility(View.VISIBLE);
                        if (pros.get(0).getIsExchange() == 1) {
                            upButton.setVisibility(View.VISIBLE);
                        } else {
                            upButton.setVisibility(View.GONE);
                        }
                        adapter.setFunList(pros.get(0).getFunList(), mUser);
                    }
                    break;

                case 1:// 初始化广告
                    if (ParseUtil.listNotNull(advItems)) {
                        AdvList.AdvItem advItem = advItems.get(0);
                        if (ParseUtil.listNotNull(advItem.getSourceList())) {
                            int wid = advItem.getSourceList().get(0).getWidth();
                            int hei = advItem.getSourceList().get(0).getHeight();
                            if (wid != 0 && hei != 0) {
                                adImg.setVisibility(View.VISIBLE);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(CommonApplication.width, CommonApplication.width * hei / wid);
                                adImg.setLayoutParams(params);
                            }
                            CommonApplication.finalBitmap.display(adImg, advItem.getSourceList().get(0).getUrl());
                        }
                    }
                    break;
            }
        }
    };


    private void initMyPermission() {
        showLoadingDialog(true);
        controller.myPermission(new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                showLoadingDialog(false);
                if (isSuccess) {
                    try {
                        JSONObject j = new JSONObject(data);
                        controller.parseJson(j.optJSONArray("good"), pros);
                        mHandler.sendEmptyMessage(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = new JSONObject(data);
                        showToast(object.optString("desc"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
