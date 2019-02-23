package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
import cn.com.modernmediausermodel.vip.ChangeBirthDialog;
import cn.com.modernmediausermodel.vip.ChangeDistrictDialog;
import cn.com.modernmediausermodel.vip.ChangeIncomeDialog;
import cn.com.modernmediausermodel.vip.ChangeJobDialog;
import cn.com.modernmediausermodel.vip.ChangeSexDialog;
import cn.com.modernmediausermodel.vip.SelectIndustryActivity;
import cn.com.modernmediausermodel.vip.VipInfo;

/**
 * 商周 新用户界面信息（4.1.0）
 *
 * @author: zhufei
 */
public class UserNewInfoActivity extends SlateBaseActivity implements View.OnClickListener {
//    public static final String KEY_ACTION_FROM = "from"; // 作为获得标识从哪个按钮点击跳转到该页面的key
//    public static final String PASSEORD = "password";
//
//    private static final String KEY_IMAGE = "data";
//    private static final String AVATAR_PIC = "avatar.jpg";
    private static final int SETINDUSTRY_CODE = 2003;

    private Context mContext;
    private UserOperateController mController;
    //    private TextView nickName,emailText;
    private TextView phone, vip_sex, vip_birth, vip_city, vip_industry, vip_position, vip_income;//vip信息
//    private RelativeLayout vip_industry_relative;
//    private TextView unvolied;// 邮箱未验证状态栏
//    private ImageView avatar, sina, weixin, qq;
//    private String picturePath;// 头像
    private User mUser;// 用户信息
    private boolean canMotifyInfo = false;// 取到用户绑定信息之前，不可以修改或者绑定
    // 第三方app与微信通信的openapi接口
//    private IWXAPI api;
//    public static int weixin_band = 0;
    private VipInfo vipInfo;
    private EditText name;

    public interface OnWXBandCallback {
        void onBand(boolean isFirstLogin, User user);
    }

    public static OnWXBandCallback sWXBandCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_newinfo);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mContext = this;
        mController = UserOperateController.getInstance(mContext);
        mController.getVipInfo(new UserFetchEntryListener() {
            @Override
            public void setData(Entry entry) {//拉取职位，行业，年收入
                if (entry instanceof VipInfo) {
                    vipInfo = (VipInfo) entry;
                }
            }
        });
//        picturePath = Environment.getExternalStorageDirectory().getPath() + "/" + AVATAR_PIC;
        mUser = SlateDataHelper.getUserLoginInfo(this);
        init();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_newinfo_back) {
            finish();
        } else if (v.getId() == R.id.vip_newinfo_save) {//保存
            String rname = name.getText().toString();
            if (!TextUtils.isEmpty(rname)) {
                mUser.setRealname(rname);
            }
            showLoadingDialog(true);
            mController.modifyUserVipInfo(mUser.getUid(), mUser.getToken(), mUser.getRealname(), mUser.getSex(), mUser.getBirth(), mUser.getProvince(), mUser.getCity(), mUser.getIndustry(), mUser.getPosition(), mUser.getIncome(), mUser.getPhone(), new UserFetchEntryListener() {
                @Override
                public void setData(Entry entry) {//上传数据
                    showLoadingDialog(false);
                    if (entry instanceof User) {
                        User newUser = (User) entry;
                        mUser = newUser;
                        SlateDataHelper.saveUserLoginInfo(UserNewInfoActivity.this, mUser);//保存数据
                        handler.sendEmptyMessage(2);
                    } else showToast(R.string.save_fail);
                }
            });
        } else if (v.getId() == R.id.vip_sex_linear) {//改性别
            changeSex(mContext, vip_sex);
        } else if (v.getId() == R.id.vip_birth_linear) {//改生日
            changeBirth(mContext, vip_birth);
        } else if (v.getId() == R.id.vip_city_linear) {//改城市
            changeCity(mContext, vip_city);
        } else if (v.getId() == R.id.vip_phone_linear) {//改手机号
            if (canMotifyInfo) {
                gotoBandDetail(BandAccountOperate.PHONE);
            }
        } else if (v.getId() == R.id.vip_industry_relative) {//改行业
            selectIndustry();
        } else if (v.getId() == R.id.vip_job_linear) {//改职位
            selectJob();
        } else if (v.getId() == R.id.vip_income_linear) {//改年收入
            selectIncome();
        }
    }

    private void init() {
//        avatar = (ImageView) findViewById(R.id.vip_avatar_img);
//        nickName = (EditText) findViewById(R.id.vip_nickname);
        name = (EditText) findViewById(R.id.vip_info_name);
        phone = (TextView) findViewById(R.id.vip_info_phone);
        vip_sex = (TextView) findViewById(R.id.vip_info_sex);
        vip_birth = (TextView) findViewById(R.id.vip_info_birth);
        vip_city = (TextView) findViewById(R.id.vip_info_city);
        vip_industry = (TextView) findViewById(R.id.vip_info_industry);
        vip_position = (TextView) findViewById(R.id.vip_info_job);
        vip_income = (TextView) findViewById(R.id.vip_info_income);
//        emailText = (TextView) findViewById(R.id.vip_info_email);

//        sina = (ImageView) findViewById(R.id.vip_btn_sina_login);
//        weixin = (ImageView) findViewById(R.id.vip_btn_weixin_login);
//        qq = (ImageView) findViewById(R.id.vip_btn_qq_login);
//        unvolied = (TextView) findViewById(R.id.vip_un_valied);

        findViewById(R.id.vip_newinfo_back).setOnClickListener(this);
        findViewById(R.id.vip_newinfo_save).setOnClickListener(this);
//        findViewById(R.id.vip_avatar_linear).setOnClickListener(this);
//        findViewById(R.id.vip_nickname_linear).setOnClickListener(this);
        name.setOnClickListener(this);
        findViewById(R.id.vip_sex_linear).setOnClickListener(this);
        findViewById(R.id.vip_birth_linear).setOnClickListener(this);
        findViewById(R.id.vip_city_linear).setOnClickListener(this);
        findViewById(R.id.vip_phone_linear).setOnClickListener(this);
//        emailText.setOnClickListener(this);
//        unvolied.setOnClickListener(this);
        findViewById(R.id.vip_industry_relative).setOnClickListener(this);
        findViewById(R.id.vip_job_linear).setOnClickListener(this);
        findViewById(R.id.vip_income_linear).setOnClickListener(this);

        handler.sendEmptyMessage(1);
        if (mUser == null) return;
        /**
         * 获取绑定信息
         */
        mController.getBandStatus(UserNewInfoActivity.this,mUser.getUid(), mUser.getToken(), new
                UserFetchEntryListener
                () {

            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                User u = (User) entry;
                if (u != null) {
                    mUser.setBandPhone(u.isBandPhone());
                    handler.sendEmptyMessage(0);
                }

            }
        });
        getInfo();
    }

    private void getInfo() {

        vip_sex.setText(setSexText());

        if (!TextUtils.isEmpty(mUser.getRealname())) name.setText(mUser.getRealname());
        if (!TextUtils.isEmpty(mUser.getBirth())) vip_birth.setText(mUser.getBirth());
        if (!TextUtils.isEmpty(mUser.getProvince()) && !TextUtils.isEmpty(mUser.getCity()))
            vip_city.setText(mUser.getProvince() + " " + mUser.getCity());
        if (!TextUtils.isEmpty(mUser.getIndustry())) vip_industry.setText(mUser.getIndustry());
        if (!TextUtils.isEmpty(mUser.getPosition())) vip_position.setText(mUser.getPosition());
        if (!TextUtils.isEmpty(mUser.getIncome())) vip_income.setText(mUser.getIncome());
    }

    private String setSexText() {//默认3--保密
        if (mUser.getSex() == 2) {
            return getString(R.string.vip_woman);
        } else if (mUser.getSex() == 1) {
            return getString(R.string.vip_man);
        } else return getString(R.string.vip_unknow);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {// 绑定信息变更
                canMotifyInfo = true;
                if (mUser.isBandPhone())
                    phone.setText(mUser.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                else phone.setText(R.string.band_yet);// 未绑定
            } else if (msg.what == 2) {//保存信息后刷新
                init();
                showToast(R.string.save_success);
            }

        }
    };

    /**
     * 绑定
     *
     * @param c        username
     * @param bindType 绑定类型
     */
    public void doBand(final String c, final int bindType) {
        if (mUser == null) return;
        mController.bandAccount(mUser.getUid(), mUser.getToken(), bindType, c, null, new UserFetchEntryListener() {

            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                String toast = "";
                if (entry != null && entry instanceof ErrorMsg) {
                    ErrorMsg e = (ErrorMsg) entry;
                    if (e.getNo() == 0) {
                        toast = getString(R.string.band_succeed);
                        // 存储绑定信息
                        if (bindType == BandAccountOperate.WEIBO) mUser.setBandWeibo(true);
                        else if (bindType == BandAccountOperate.WEIXIN) mUser.setBandWeixin(true);
                        else if (bindType == BandAccountOperate.QQ) mUser.setBandQQ(true);
                        else if (bindType == BandAccountOperate.EMAIL) // 重新发送邮件
                            toast = getString(R.string.send_email_done);

                        handler.sendEmptyMessage(0);
                        SlateDataHelper.saveUserLoginInfo(UserNewInfoActivity.this, mUser);
                    } else toast = e.getDesc();
                }
                showToast(TextUtils.isEmpty(toast) ? getString(R.string.band_failed) : toast);
            }
        });
    }

    /**
     * @param type
     */
    public void gotoBandDetail(int type) {
        Intent i = new Intent(mContext, BandDetailActivity.class);
        i.putExtra("band_type", type);
        i.putExtra("band_user", mUser);
        startActivityForResult(i, BandDetailActivity.BAND_SUCCESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SETINDUSTRY_CODE) {//返回的行业
                if (data.getExtras() == null && mUser.getIndustry() == null) {
                    vip_industry.setText("");
                    mUser.setIndustry("");
                } else if (data.getExtras() == null && mUser.getIndustry() != null) {
                    vip_industry.setText(mUser.getIndustry());
                } else if (!TextUtils.isEmpty(data.getExtras().getString("industry_category"))) {
                    vip_industry.setText(data.getExtras().getString("industry_category"));
                    mUser.setIndustry(data.getExtras().getString("industry_category"));
                }
            }
        } else if (resultCode == BandDetailActivity.BAND_SUCCESS && requestCode == BandDetailActivity.BAND_SUCCESS) {
            mUser.setEmail(SlateDataHelper.getUserLoginInfo(this).getEmail());
            mUser.setPhone(SlateDataHelper.getUserPhone(this));
            handler.sendEmptyMessage(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeSex(Context context, final TextView sex_tv) {
        ChangeSexDialog mChangeSexDialog = new ChangeSexDialog(context);
        mChangeSexDialog.setData(context.getResources().getString(R.string.vip_woman));
        mChangeSexDialog.show();
        mChangeSexDialog.setSexListener(new ChangeSexDialog.OnSexListener() {
            @Override
            public void onClick(String sex) {
                sex_tv.setText(sex);
                if (sex.equals(getString(R.string.vip_woman))) {
                    mUser.setSex(2);
                } else if (sex.equals(getString(R.string.vip_man))) {
                    mUser.setSex(1);
                } else if (sex.equals(getString(R.string.vip_unknow))) mUser.setSex(3);
            }
        });
    }

    private void changeBirth(Context context, final TextView birth_textview) {
        ChangeBirthDialog mChangeBirthDialog = new ChangeBirthDialog(context);
        mChangeBirthDialog.setDate(2017, 1, 1);
        mChangeBirthDialog.show();
        mChangeBirthDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {

            @Override
            public void onClick(String year, String month, String day) {
                birth_textview.setText(year + "-" + month + "-" + day);
                mUser.setBirth(year + "-" + month + "-" + day);
            }
        });
    }

    private void changeCity(Context context, final TextView district_textview) {
        ChangeDistrictDialog mChangeAddressDialog = new ChangeDistrictDialog(context);
        mChangeAddressDialog.setAddress("北京", "朝阳");
        mChangeAddressDialog.show();
        mChangeAddressDialog.setAddresskListener(new ChangeDistrictDialog.OnAddressCListener() {

            @Override
            public void onClick(String province, String city) {
                district_textview.setText(province + "  " + city);
                mUser.setProvince(province);
                mUser.setCity(city);
            }
        });
    }

    /**
     * 选择行业
     */

    private void selectIndustry() {
        if (vipInfo == null) return;

        VipInfo.VipCategory industryList = vipInfo.categoryMap.get(VipInfo.INDUSTRY);
        if (industryList == null) return;
        Intent intent = new Intent(this, SelectIndustryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("industry", industryList);
        intent.putExtras(bundle);
        startActivityForResult(intent, SETINDUSTRY_CODE);
    }

    /**
     * 选择职位
     */
    private void selectJob() {
        if (vipInfo == null) return;

        VipInfo.VipCategory jobList = vipInfo.categoryMap.get(VipInfo.JOB);
        if (jobList == null) return;

        ChangeJobDialog mChangeJobDialog = new ChangeJobDialog(this, jobList, "");
        mChangeJobDialog.show();
        mChangeJobDialog.setJobListener(new ChangeJobDialog.OnJobListener() {

            @Override
            public void onClick(VipInfo.VipChildCategory job, VipInfo.VipChildCategory childJob) {
                if (childJob != null && !TextUtils.isEmpty(childJob.category)) {
                    vip_position.setText(childJob.category);
                    mUser.setPosition(childJob.category);
                }
            }
        });
    }

    /**
     * 选择年收入
     */
    private void selectIncome() {
        if (vipInfo == null || vipInfo.categoryMap == null) return;

        VipInfo.VipCategory incomeList = vipInfo.categoryMap.get(VipInfo.INCOME);
        if (incomeList == null) return;

        ChangeIncomeDialog changeIncomeDialog = new ChangeIncomeDialog(this, incomeList, "");
        changeIncomeDialog.show();
        changeIncomeDialog.setIncomeListener(new ChangeIncomeDialog.OnIncomeListener() {

            @Override
            public void onClick(VipInfo.VipChildCategory income) {
                vip_income.setText(income.category);
                mUser.setIncome(income.category);
            }
        });
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    public String getActivityName() {
        return UserNewInfoActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return UserNewInfoActivity.this;
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
