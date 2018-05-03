package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.BandAccountOperate;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 绑定手机号、邮箱、收货地址
 *
 * @author lusiyuan
 */
public class BandDetailActivity extends SlateBaseActivity implements OnClickListener {
    public static int BAND_SUCCESS = 22;
    private int type;
    private TextView title, getCode, emailtitle, changePhone;
    private EditText userName, code;
    private Button complete;
    private LinearLayout verifyLayout, changePhoneLayout, bandDetail;// 收货地址layout
    private boolean canGetVerify = true;// 是否可获取验证码
    private UserOperateController mController;
    private User user;
    private boolean isBanding = false;

    private Animation shakeAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.band_detail_activity);
        type = getIntent().getIntExtra("band_type", 0);
        user = (User) getIntent().getSerializableExtra("band_user");
        mController = UserOperateController.getInstance(this);
        initView();
    }

    private void initView() {
        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
        verifyLayout = (LinearLayout) findViewById(R.id.layout_verify);
        title = (TextView) findViewById(R.id.band_title);
        userName = (EditText) findViewById(R.id.forget_phone);
        code = (EditText) findViewById(R.id.forget_verify_edit);
        emailtitle = (TextView) findViewById(R.id.band_email_tiele);
        complete = (Button) findViewById(R.id.forget_complete);
        getCode = (TextView) findViewById(R.id.forget_get_verify);
        changePhone = findViewById(R.id.change_phone_hint);
        changePhoneLayout = findViewById(R.id.change_phone_layout);
        bandDetail = findViewById(R.id.band_layout);

        if (type == BandAccountOperate.PHONE) {
            if (user.isBandPhone()) {
                changePhoneLayout.setVisibility(View.VISIBLE);
                bandDetail.setVisibility(View.GONE);
            } else {
                changePhoneLayout.setVisibility(View.GONE);
                bandDetail.setVisibility(View.VISIBLE);
            }
            changePhone.setText(String.format(getResources().getString(R.string.change_phone_hint), user.getPhone().replaceAll("" + "(\\d{3})\\d{4}(\\d{4})", "$1****$2")));
            title.setText(R.string.band_phone);
            userName.setHint(R.string.phone_number);
            userName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (UserTools.checkIsPhone(BandDetailActivity.this, s.toString())) {
                        getCode.setTextColor(getResources().getColor(R.color.black_bg));
                    } else getCode.setTextColor(getResources().getColor(R.color.grgray));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            userName.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (type == BandAccountOperate.EMAIL) {
            title.setText(R.string.band_email);
            userName.setHint("Email");
            emailtitle.setVisibility(View.VISIBLE);
            emailtitle.setText(String.format(getString(R.string.band_email_text1), user.getEmail()));
            userName.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
            verifyLayout.setVisibility(View.GONE);
        }

        findViewById(R.id.band_back).setOnClickListener(this);
        findViewById(R.id.change_button).setOnClickListener(this);
        complete.setOnClickListener(this);
        getCode.setOnClickListener(this);
        findViewById(R.id.forget_phone_clear).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.forget_complete) {// 完成
            if (userName != null) {
                String name = userName.getText().toString();
                String c = code.getText().toString();
                if (type == BandAccountOperate.PHONE && UserTools.checkString(name, userName, shakeAnim) && UserTools.checkString(c, code, shakeAnim)) {
                    doBand(name, BandAccountOperate.PHONE, c);
                } else if (type == BandAccountOperate.EMAIL && UserTools.checkString(name, userName, shakeAnim)) {
                    if (UserTools.checkIsEmail(BandDetailActivity.this, name))
                        doBand(name, BandAccountOperate.EMAIL, null);
                    else showToast(R.string.get_account_error);
                }
            }
        } else if (v.getId() == R.id.forget_get_verify) {// 获取验证码
            if (userName != null) {
                String name = userName.getText().toString();
                if (UserTools.checkIsPhone(BandDetailActivity.this, name) && UserTools.checkString(name, userName, shakeAnim))
                    doGetVerifyCode(name);
            }
        } else if (v.getId() == R.id.band_back) {
            finish();
        } else if (v.getId() == R.id.forget_phone_clear) {
            doClear();
        } else if (v.getId() == R.id.change_button) {// 更换手机号
            changePhoneLayout.setVisibility(View.GONE);
            bandDetail.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 清除昵称
     */
    protected void doClear() {
        if (userName != null) userName.setText("");
    }

    /**
     * 绑定
     *
     * @param c        username
     * @param bindType 绑定类型
     * @param code     验证码
     */
    protected void doBand(final String c, final int bindType, String code) {
        if (user == null || isBanding) return;
        isBanding = true;
        mController.bandAccount(user.getUid(), user.getToken(), bindType, c, code, new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                isBanding = false;
                ErrorMsg error = null;
                if (entry != null) error = (ErrorMsg) entry;
                else return;
                if (error.getNo() == 0) {
                    if (bindType == BandAccountOperate.PHONE) {// 存储绑定信息
                        user.setPhone(c);
                        user.setBandPhone(true);
                        showToast(R.string.band_succeed);
                        SlateDataHelper.setUserPhone(BandDetailActivity.this, c);
                    } else if (bindType == BandAccountOperate.EMAIL) {
                        user.setEmail(c);
                        user.setBandEmail(true);
                        showToast(R.string.band_email_succeed);
                        SlateDataHelper.saveUserLoginInfo(BandDetailActivity.this, user);
                    }
                    setResult(BAND_SUCCESS);
                    finish();
                } else {
                    showToast(error.getDesc());
                }
            }
        });
    }

    /**
     * 获取手机验证码
     */
    protected void doGetVerifyCode(String c) {
        if (canGetVerify) {
            canGetVerify = false;
            // 开启倒计时器
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    getCode.setText(millisUntilFinished / 1000 + "s重新获取");
                }

                public void onFinish() {
                    getCode.setText(R.string.get_verify_code);
                    canGetVerify = true;
                }
            }.start();
            mController.getVerifyCode(c, new UserFetchEntryListener() {

                @Override
                public void setData(Entry entry) {
                    if (entry instanceof ErrorMsg && ((ErrorMsg) entry).getNo() != 0) {

                        showToast(((ErrorMsg) entry).getDesc());
                    }
                }
            });
        }
    }

    @Override
    public String getActivityName() {
        return BandDetailActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
