package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.modernmedia.pay.newlogic.PayHttpsOperate;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.FetchDataListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.BandDetailActivity;
import cn.com.modernmediausermodel.NewCoinActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.BandAccountOperate;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;


/**
 * vip 录入基本信息
 *
 * @author: zhufei
 */
public class VipInfoActivity extends SlateBaseActivity implements View.OnClickListener {
    public static final int SETINDUSTRY_CODE = 2002;
    private TextView vip_sex, vip_birth, vip_district, vip_industry, vip_job, vip_income, vip_phone;
    private User user;
    private VipInfo vipInfo;
    private UserOperateController controller;
    private EditText vip_name;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipinfo);
        user = SlateDataHelper.getUserLoginInfo(this);
        initView();
        code = getIntent().getIntExtra("code", 0);
        controller = UserOperateController.getInstance(this);
        controller.getVipInfo(new UserFetchEntryListener() {//拉取行业，职位，年收入
            @Override
            public void setData(Entry entry) {
                if (entry instanceof VipInfo) {
                    vipInfo = (VipInfo) entry;
                }
            }
        });
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.vip_sex_linear) {//性别
            changeSex(this, vip_sex);
        } else if (v.getId() == R.id.vip_birth_linear) {//生日
            changeBirth(this, vip_birth);
        } else if (v.getId() == R.id.vip_district_linear) {//地区
            changeDistrict(this, vip_district);
        } else if (v.getId() == R.id.vip_industry_relative) {//行业 多条数据跳转新页面
            selectIndustry();
        } else if (v.getId() == R.id.vip_job_linear) {//职位
            selectJob();
        } else if (v.getId() == R.id.vip_income_linear) {//年收入
            selectIncome();
        } else if (v.getId() == R.id.vip_phone_linear) {//手机号
            gotoBandDetail(BandAccountOperate.PHONE);
        } else if (v.getId() == R.id.vip_complete) {//完成
            String rname = vip_name.getText().toString();
            if (!TextUtils.isEmpty(rname)) {
                user.setRealname(rname);
            }
            if (postVip()) {//都填写完了
                //提交数据
                mHandler.sendEmptyMessage(0);
            } else showToast(R.string.vip_uncomplete);

        } else if (v.getId() == R.id.vip_back) {
            finish();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showLoadingDialog(true);
                    controller.modifyUserVipInfo(user.getUid(), user.getToken(), user.getRealname(), user.getSex(), user.getBirth(), user.getProvince(), user.getCity(), user.getIndustry(), user.getPosition(), user.getIncome(), user.getPhone(), new UserFetchEntryListener() {
                        @Override
                        public void setData(Entry entry) {//上传数据
                            showLoadingDialog(false);
                            if (entry instanceof User) {//补全资料OK
                                user = (User) entry;
                                SlateDataHelper.saveUserLoginInfo(VipInfoActivity.this, user);//保存数据
                                if (user.getLevel() == 2 && user.getCompletevip() == 1) {
                                    if (code == VipPayResultActivity.CODE_RESULT || code == VipCodeSuccessActivity.CODE_JIHUO) {//购买vip后补全成功
                                        showToast(getString(R.string.save_success));
                                        startActivity(new Intent(VipInfoActivity.this, MyVipActivity.class));
                                        finish();
                                    } else {//付费用户升级
                                        PayHttpsOperate.getInstance(VipInfoActivity.this).vipUp(new FetchDataListener() {
                                            @Override
                                            public void fetchData(boolean isSuccess, String data, boolean fromHttp) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(data);
                                                    if (jsonObject.optJSONObject("error").optInt("no") == 0) {//升级成功

                                                        /**
                                                         * 升级成功，强制置为2
                                                         */
                                                        SlateDataHelper.setUserLevel(VipInfoActivity.this, 2);
                                                        startActivityForResult(new Intent(VipInfoActivity.this, VipUpSuccessActivity.class), 3022);
                                                    } else//升级失败
                                                        showToast(jsonObject.optJSONObject("error").optString("desc"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    if (code == NewCoinActivity.CODE_ICON) {//普通用户 积分兑换vip用
                                        startActivity(new Intent(VipInfoActivity.this, NewCoinActivity.class));
                                        finish();
                                    } else if (code == VipCodeSuccessActivity.CODE_JIHUO) {// 激活码，购买vip用
                                        startActivity(new Intent(VipInfoActivity.this, MyVipActivity.class));
                                        finish();
                                    }
                                }

                            } else showToast(R.string.vip_complete_fail);
                        }
                    });
                    break;
            }
        }
    };

    /**
     * @param type
     */
    public void gotoBandDetail(int type) {
        Intent i = new Intent(this, BandDetailActivity.class);
        i.putExtra("band_type", type);
        i.putExtra("band_user", user);
        startActivityForResult(i, BandDetailActivity.BAND_SUCCESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BandDetailActivity.BAND_SUCCESS) {//返回的手机号
            if (resultCode == BandDetailActivity.BAND_SUCCESS) {
                String phone = SlateDataHelper.getUserPhone(VipInfoActivity.this);
                vip_phone.setText(phone);
                user.setPhone(phone);
            } else if (!TextUtils.isEmpty(user.getPhone())) {
                vip_phone.setText(user.getPhone());
                user.setPhone(user.getPhone());
            }
        } else if (requestCode == SETINDUSTRY_CODE) {//返回的行业
            if (resultCode == RESULT_CANCELED) {
                vip_industry.setText("");
            } else if (!TextUtils.isEmpty(data.getExtras().getString("industry_category"))) {
                vip_industry.setText(data.getExtras().getString("industry_category"));
                user.setIndustry(data.getExtras().getString("industry_category"));
            } else vip_industry.setText("");
        } else if (requestCode == 3012 && resultCode == 4002) {//开通VIP返回
            this.setResult(4002);
            this.finish();
        } else if (requestCode == 3022 && resultCode == 4022) {//升级成功
            this.setResult(422);
            this.finish();
        }
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
                    user.setSex(2);
                } else if (sex.equals(getString(R.string.vip_man))) {
                    user.setSex(1);
                } else if (sex.equals(getString(R.string.vip_unknow))) user.setSex(3);
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
                user.setBirth(year + "-" + month + "-" + day);
            }
        });
    }

    private void changeDistrict(Context context, final TextView district_textview) {
        ChangeDistrictDialog mChangeAddressDialog = new ChangeDistrictDialog(context);
        mChangeAddressDialog.setAddress("北京", "朝阳");
        mChangeAddressDialog.show();
        mChangeAddressDialog.setAddresskListener(new ChangeDistrictDialog.OnAddressCListener() {

            @Override
            public void onClick(String province, String city) {
                district_textview.setText(province + "  " + city);
                user.setProvince(province);
                user.setCity(city);
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
                if (childJob == null || TextUtils.isEmpty(childJob.cate_id)) {
                    vip_job.setText(job.category);
                    user.setPosition(job.category);
                } else {
                    vip_job.setText(childJob.category);
                    user.setPosition(childJob.category);
                }
            }
        });
    }

    /**
     * 选择年收入
     */
    private void selectIncome() {
        if (vipInfo == null) return;

        VipInfo.VipCategory incomeList = vipInfo.categoryMap.get(VipInfo.INCOME);
        if (incomeList == null) return;

        ChangeIncomeDialog changeIncomeDialog = new ChangeIncomeDialog(this, incomeList, "");
        changeIncomeDialog.show();
        changeIncomeDialog.setIncomeListener(new ChangeIncomeDialog.OnIncomeListener() {

            @Override
            public void onClick(VipInfo.VipChildCategory income) {
                vip_income.setText(income.category);
                user.setIncome(income.category);
            }
        });
    }


    private boolean postVip() {//判断是否全部填写
        String msg = vip_name.getText().toString();
        String str = msg.replaceAll(" ", "");//去掉所有空格
        if (!TextUtils.isEmpty(str)) {
            if (!TextUtils.isEmpty(vip_sex.getText().toString())) {
                if (!TextUtils.isEmpty(vip_birth.getText().toString())) {
                    if (!TextUtils.isEmpty(vip_district.getText().toString())) {
                        if (!TextUtils.isEmpty(vip_industry.getText().toString())) {
                            if (!TextUtils.isEmpty(vip_job.getText().toString())) {
                                if (!TextUtils.isEmpty(vip_income.getText().toString())) {
                                    if (!TextUtils.isEmpty(vip_phone.getText().toString())) {
                                        return true;
                                    } else return false;
                                } else return false;
                            } else return false;
                        } else return false;
                    } else return false;
                } else return false;
            } else return false;
        } else return false;
    }

    @Override
    public String getActivityName() {
        return VipInfoActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return VipInfoActivity.this;
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

    private void initView() {
        vip_name = (EditText) findViewById(R.id.vip_name);
        vip_sex = (TextView) findViewById(R.id.vip_sex);
        vip_birth = (TextView) findViewById(R.id.vip_birth);
        vip_district = (TextView) findViewById(R.id.vip_district);
        vip_industry = (TextView) findViewById(R.id.vip_industry_hint);
        vip_job = (TextView) findViewById(R.id.vip_job);
        vip_income = (TextView) findViewById(R.id.vip_income);
        vip_phone = (TextView) findViewById(R.id.vip_phone);

        vip_sex.setText(setSexText());
        vip_name.setText(user.getRealname());
        vip_birth.setText(user.getBirth());
        if (!TextUtils.isEmpty(user.getProvince()) && !TextUtils.isEmpty(user.getCity()))
            vip_district.setText(user.getProvince() + " " + user.getCity());
        vip_industry.setText(user.getIndustry());
        vip_job.setText(user.getPosition());
        vip_income.setText(user.getIncome());
        vip_phone.setText(user.getPhone());
        findViewById(R.id.vip_industry_relative).setOnClickListener(this);
        findViewById(R.id.vip_name_linear).setOnClickListener(this);
        findViewById(R.id.vip_sex_linear).setOnClickListener(this);
        findViewById(R.id.vip_birth_linear).setOnClickListener(this);
        findViewById(R.id.vip_district_linear).setOnClickListener(this);
        findViewById(R.id.vip_job_linear).setOnClickListener(this);
        findViewById(R.id.vip_income_linear).setOnClickListener(this);
        findViewById(R.id.vip_phone_linear).setOnClickListener(this);

        findViewById(R.id.vip_back).setOnClickListener(this);
        findViewById(R.id.vip_complete).setOnClickListener(this);
    }

    private String setSexText() {//默认3--保密
        if (user.getSex() == 2) {
            return getString(R.string.vip_woman);
        } else if (user.getSex() == 1) {
            return getString(R.string.vip_man);
        } else return getString(R.string.vip_unknow);

    }
}
