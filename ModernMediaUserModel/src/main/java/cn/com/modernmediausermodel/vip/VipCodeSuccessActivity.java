package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.utils.Utils;

import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;

/**
 * 礼品码兑换成功页面
 *
 * @author: zhufei
 */
public class VipCodeSuccessActivity extends SlateBaseActivity implements View.OnClickListener {
    public static final int CODE_JIHUO = 30;
    private User mUser;
    private TextView endtime, bar_title, text_title, content, name_tv, phone_tv, address_tv;
    private LinearLayout address_info_linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipup_success);
        SlateApplication.getInstance().addActivity(this);
        endtime = (TextView) findViewById(R.id.vip_up_endtime);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String name = bundle.getString("name");
        String phone = bundle.getString("phone");
        String address = bundle.getString("address");
        final boolean isVip = bundle.getBoolean("isVip");
        initView();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(address)) {
            address_info_linear.setVisibility(View.VISIBLE);
            name_tv.setText(name);
            phone_tv.setText(phone);
            address_tv.setText(address);
        }
        bar_title.setText(getString(R.string.vip_code_title));
        content.setText(getString(R.string.vip_code_content));
        if (!TextUtils.isEmpty(SlateDataHelper.getCodeTitle(this)))
            text_title.setText(String.format(getString(R.string.vip_up_text_content), SlateDataHelper.getCodeTitle(this)));
        mUser = SlateDataHelper.getUserLoginInfo(this);

        PayHttpsOperate con = PayHttpsOperate.getInstance(this);
        //更新用户资料
        showLoadingDialog(true);
        con.getUserPermission(new FetchDataListener() {
            @Override
            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                showLoadingDialog(false);
                PayHttpsOperate.getInstance(VipCodeSuccessActivity.this).saveLevel(data);
            }
        });

        UserOperateController.getInstance(this).getInfoByIdAndToken(mUser.getUid(), mUser.getToken(), new UserFetchEntryListener() {
                    @Override
                    public void setData(Entry entry) {

                        User tempUser = (User) entry;
                        ErrorMsg error = tempUser.getError();
                        // 取得成功
                        if (error.getNo() == 0 && !TextUtils.isEmpty(tempUser.getUid())) {
                            mUser = tempUser;
                            SlateApplication.loginStatusChange = true;
                            SlateDataHelper.saveUserLoginInfo(VipCodeSuccessActivity.this, mUser);//保存新数据
                            endtime.setText(Utils.strToDate(mUser.getVip_end_time()));
                            SlateDataHelper.cleanIsVip(VipCodeSuccessActivity.this);
                            SlateDataHelper.cleanNeedAddress(VipCodeSuccessActivity.this);
                            if (mUser.getCompletevip() != 1 && isVip)
                                VipPayResultActivity.showInfoDialog(VipCodeSuccessActivity.this, CODE_JIHUO);//补充会员资料
                        }
                    }
                }

        );
        findViewById(R.id.vip_up_code_complete).setOnClickListener(this);
        Button btn = (Button) findViewById(R.id.vip_code_btn);
        btn.setOnClickListener(this);
        if (isVip) btn.setVisibility(View.VISIBLE);
    }

    private void initView() {
        bar_title = (TextView) findViewById(R.id.vip_up_bar_title);
        content = (TextView) findViewById(R.id.vip_up_content);
        text_title = (TextView) findViewById(R.id.vip_up_text_title);
        address_info_linear = (LinearLayout) findViewById(R.id.vip_address_linear);//配送信息
        name_tv = (TextView) findViewById(R.id.vip_paycallback_name);//收件人
        phone_tv = (TextView) findViewById(R.id.vip_paycallback_phone);//手机号
        address_tv = (TextView) findViewById(R.id.vip_paycallback_address);//收件地址
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_up_code_complete) {//完成回到用户中心
            this.setResult(4005);
            this.finish();
        } else if (v.getId() == R.id.vip_code_btn) {
            Intent i = new Intent(this, MyVipActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.setResult(4005);
        finish();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public String getActivityName() {
        return VipCodeSuccessActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}