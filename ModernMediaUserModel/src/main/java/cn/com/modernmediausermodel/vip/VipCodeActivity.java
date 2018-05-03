package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 兑换码兑换VIP
 *
 * @author: zhufei
 */
public class VipCodeActivity extends SlateBaseActivity implements View.OnClickListener {
    private EditText code;
    private User mUser;
    private UserOperateController controller;
    private Context mContext;
    private String sn;//兑换码
    private ErrorMsg error;
    private ImageView avartar;
    private TextView nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_code_exchange);
        mContext = this;
        LogHelper.logCodeShow(this);
        SlateApplication.getInstance().addActivity(this);
        findViewById(R.id.vip_code_back).setOnClickListener(this);
        findViewById(R.id.vip_code_btn).setOnClickListener(this);

        code = (EditText) findViewById(R.id.vip_code_edit);
        avartar = (ImageView) findViewById(R.id.vip_code_avartar);
        nickname = (TextView) findViewById(R.id.vip_code_nickname);
        mUser = SlateDataHelper.getUserLoginInfo(this);

        if (mUser == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        controller = UserOperateController.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUser = SlateDataHelper.getUserLoginInfo(this);
        if (mUser != null) {
            UserTools.setAvatar(this, mUser.getAvatar(), avartar);
            nickname.setText(mUser.getNickName());
        }
        MobclickAgent.onResume(this);
    }


    @Override
    public void onClick(View v) {
        sn = code.getText().toString();//兑换码
        if (v.getId() == R.id.vip_code_back) {
            finish();
        } else if (v.getId() == R.id.vip_code_btn) {
            LogHelper.checkVipCodingExchange(this);
            if (TextUtils.isEmpty(sn)) {
                showToast(R.string.vip_code_null);
            } else {
                showLoadingDialog(true);
                controller.exchangeCodeType(mUser.getUid(), mUser.getToken(), sn, new UserFetchEntryListener() {
                    @Override
                    public void setData(Entry entry) {
                        showLoadingDialog(false);
                        if (entry != null && entry instanceof ErrorMsg) {
                            error = (ErrorMsg) entry;
                            if (200 == error.getNo()) {
                                if (SlateDataHelper.getCodeIsVip(mContext).equals("1")) {//兑换VIP
                                    if (SlateDataHelper.getCodeNeedAddress(mContext).equals("1")) {//需要补全地址
                                        startActivityForResult(new Intent(mContext, PostUserOrderInfoActivity.class), 3080);
                                    } else if (mUser.getSend().equals("0") || TextUtils.isEmpty(mUser.getSend())) {//未邮寄，提示邮寄VIP卡
                                        setCardDialog(mContext, sn);
                                    } else {//不需要地址，已寄卡
                                        requestCode(sn, true);
                                    }
                                } else {//非VIP兑换码
                                    requestCode(sn, false);
                                }
                            } else {
                                showToast(error.getDesc());
                            }
                        } else {
                            showToast(R.string.vip_code_failed);
                        }
                    }
                });
            }
        }
    }

    /**
     * 是否邮寄VIP卡片
     *
     * @param context
     */

    private void setCardDialog(Context context, final String sn) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setMessage(context.getString(R.string.vip_code_dialog));
        normalDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCode(sn, true);
            }
        });
        normalDialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(mContext, PostUserOrderInfoActivity.class), 3080);
            }
        });
        normalDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3005 && resultCode == 4005) {
            this.setResult(4004);
            this.finish();
        } else if (3080 == requestCode && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                User errorMsg = (User) bundle.getSerializable("error");
                if (errorMsg != null) {
                    String name = errorMsg.getRealname();
                    String phone = errorMsg.getPhone();
                    String address = errorMsg.getCity() + errorMsg.getAddress();
                    requestCodeAndAddress(sn, name, phone, address, true);
                }
            }
        }
    }

    private void requestCode(String sn, final boolean isVip) {
        showLoadingDialog(true);
        controller.exchangeCodeVip(mUser.getUid(), mUser.getToken(), sn, new UserFetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                if (entry != null && entry instanceof ErrorMsg) {
                    ErrorMsg error = (ErrorMsg) entry;
                    if (error.getNo() == 200) {//成功跳转兑换成功页面
                        Intent i = new Intent(VipCodeActivity.this, VipCodeSuccessActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isVip", isVip);
                        i.putExtras(bundle);
                        startActivityForResult(i, 3005);
                    } else {//兑换错误
                        showToast(error.getDesc());
                    }
                }
            }
        });
    }

    private void requestCodeAndAddress(String sn, final String name, final String phone, final String address, final boolean isVip) {
        showLoadingDialog(true);
        controller.exchangeCodeVip(mUser.getUid(), mUser.getToken(), sn, new UserFetchEntryListener() {
            @Override
            public void setData(Entry entry) {
                showLoadingDialog(false);
                if (entry != null && entry instanceof ErrorMsg) {
                    ErrorMsg error = (ErrorMsg) entry;
                    if (error.getNo() == 200) {//成功跳转兑换成功页面
                        Intent i = new Intent(VipCodeActivity.this, VipCodeSuccessActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        bundle.putString("phone", phone);
                        bundle.putString("address", address);
                        bundle.putBoolean("isVip", isVip);
                        i.putExtras(bundle);
                        startActivityForResult(i, 3005);
                    } else {//兑换错误
                        showToast(error.getDesc());
                    }
                }
            }
        });
    }


    @Override
    public String getActivityName() {
        return VipCodeActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return VipCodeActivity.this;
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
