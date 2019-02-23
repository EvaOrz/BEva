package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.api.UrlMaker;
import cn.com.modernmedia.model.VipGoodList;
import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.util.UserTools;


/**
 * 开通VIP选择套餐界面
 *
 * @author: zhufei
 */
public class VipOpenActivity extends SlateBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context mContext;
    private ImageView avatar;
    private TextView nickname, vip_status, vip_open_phone, buy_btn;
    public static final int STROKE = 2;// 边框宽度
    private PayHttpsOperate controller;
    private List<VipGoodList.VipGood> pros = new ArrayList<>();
    private User user;
    private int pos = -1;

    private List<VipGoodList.Fun> funList = new ArrayList<>();
    private RadioGroup radioGroup;
    private VipGridView gridView;
    private VipFunAdapter adapter;
    private boolean needData = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipopen);
        SlateApplication.getInstance().addActivity(this);
        mContext = this;
        initView();
        controller = PayHttpsOperate.getInstance(mContext);
        user = SlateDataHelper.getUserLoginInfo(mContext);
        if (user == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        user = SlateDataHelper.getUserLoginInfo(this);
        if (user != null && !ParseUtil.listNotNull(pros)) {

            initProducts();
            UserTools.setAvatar(mContext, user.getAvatar(), avatar);
            nickname.setText(user.getNickName());
            //客服电话--蓝色
            setPhone(vip_open_phone);
            getVipStatus();//刷新VIP有效期

        }
    }

    private void initView() {
        avatar = (ImageView) findViewById(R.id.vip_open_avatar);
        nickname = (TextView) findViewById(R.id.vip_open_nickname);
        vip_status = (TextView) findViewById(R.id.vip_open_status);
        vip_open_phone = (TextView) findViewById(R.id.vip_open_phone);
        buy_btn = (TextView) findViewById(R.id.vip_open_btn);
        radioGroup = (RadioGroup) findViewById(R.id.vip_open_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                pos = checkedId;
                funList.clear();
                if (pros.get(checkedId).getFunList().size() == 0) {
                    gridView.setVisibility(View.GONE);
                } else {
                    gridView.setVisibility(View.VISIBLE);
                    funList.addAll(pros.get(checkedId).getFunList());
                    adapter.setFunList(funList);
                }
                buy_btn.setVisibility(View.VISIBLE);
                if (!pros.get(checkedId).getShowPrice().isEmpty()) {
                    buy_btn.setText(pros.get(checkedId).getShowPrice());
                } else {
                    buy_btn.setText(pros.get(checkedId).getGoodName());
                }
            }
        });
        gridView = (VipGridView) findViewById(R.id.vip_gridview);
        adapter = new VipFunAdapter(mContext);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        findViewById(R.id.vip_open_back).setOnClickListener(this);
        findViewById(R.id.vip_open_btn).setOnClickListener(this);
        findViewById(R.id.vip_agreement).setOnClickListener(this);
        vip_open_phone.setOnClickListener(this);
    }

    /**
     * 1. 开通vip (非订阅用户)
     * 2. vip过期，再次购买
     */
    private void getVipStatus() {
        if (user.getUser_status() == 4) {
            vip_status.setText(getString(R.string.vip_show_time_out));
        } else {
            vip_status.setText(R.string.vip_open_null);
        }
    }

    /*设置电话颜色*/
    private void setPhone(TextView textView) {
        SpannableStringBuilder mBuilder = new SpannableStringBuilder(getString(R.string.vip_open_tip));
        mBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), 6, 16, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(mBuilder);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_open_back) {
            finish();
        } else if (v.getId() == R.id.vip_open_phone) {//是否要拨打电话
            setCallPhone(this);
        } else if (v.getId() == R.id.vip_agreement) {//VIP服务协议
            UriParse.doLinkWeb(VipOpenActivity.this, UrlMaker.vipServiceAgreement(), true);
        } else if (v.getId() == R.id.vip_open_btn) {//购买
            LogHelper.logVipOnlinepay(this, "create", pros.get(pos).getGoodId());
            Intent intent = new Intent(this, VipProductPayActivity.class);//在线支付
            Bundle bundle = new Bundle();
            bundle.putSerializable("product", pros.get(pos));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public static void setCallPhone(final Context context) {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.icon);
        normalDialog.setTitle(context.getString(R.string.vip_open_callphone));
        normalDialog.setMessage(context.getString(R.string.vip_open_phone_numb));
        normalDialog.setNegativeButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //打给客服 4006503206
                UriParse.doCall(context, "4006503206");
            }
        });
        normalDialog.setPositiveButton(R.string.vip_pay_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        normalDialog.show();
    }

    /**
     * 获取商品列表
     */
    private void initProducts() {
        showLoadingDialog(true);
        controller.requestHttpAsycle(false, UrlMaker.vipOrder(), new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                showLoadingDialog(false);
                if (isSuccess) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray array = jsonObject.optJSONArray("good");
                        pros.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject j = array.optJSONObject(i);
                            pros.add(controller.parseVipGood(j));
                        }
                        mHandler.sendEmptyMessage(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    radioGroup.removeAllViews();
                    if (ParseUtil.listNotNull(pros)) {
                        for (int i = 0; i < pros.size(); i++) {
                            RadioButton r = null;
                            //                            //vip过期 按钮匹配与那套餐
                            //                            if (user.getUser_status() == 4 && pros.get(i).getGoodId().equals(SlateDataHelper.getVipPid(mContext))) {
                            //                                r = setRadioButton(R.drawable.tab_selected);
                            //                            } else {
                            r = setRadioButton(R.drawable.tab_selected2);
                            //                            }
                            r.setId(i);
                            r.setText(pros.get(i).getGoodName());
                            radioGroup.addView(r, getWindowManager().getDefaultDisplay().getWidth() / pros.size(), LinearLayout.LayoutParams.WRAP_CONTENT);
                        }
                        ((RadioButton) radioGroup.getChildAt(1)).setChecked(true);
                    }
                    break;
            }
        }
    };

    /**
     * 设置radiobutton样式
     */
    private RadioButton setRadioButton(int res) {
        RadioButton radioButton = new RadioButton(mContext);
        Drawable drawableTop = getResources().getDrawable(res);
        drawableTop.setBounds(1, 1, 75, 75);
        radioButton.setCompoundDrawables(null, drawableTop, null, null);
        Bitmap bitmap = null;
        radioButton.setButtonDrawable(new BitmapDrawable(bitmap));
        radioButton.setTextSize(12);
        // 同时兼容高、低版本
        radioButton.setTextColor(getResources().getColorStateList(R.color.radiobuttton_color));
        radioButton.setGravity(Gravity.CENTER);
        return radioButton;
    }

    @Override
    public String getActivityName() {
        return VipOpenActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return VipOpenActivity.this;
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /*点击更多套餐详情*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position + 1 == adapter.getCount()) {
            Intent intent = new Intent(this, VipPlanDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("product", pros.get(pos));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
