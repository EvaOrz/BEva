package cn.com.modernmediausermodel.vip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.BandDetailActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.BandAccountOperate;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;


/**
 * 更改邮箱
 *
 * @author: zhufei
 */

public class ChangeEmailActivity extends SlateBaseActivity implements View.OnClickListener {
    private TextView email;
    private CheckBox receive;//接收简报
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        mUser = SlateDataHelper.getUserLoginInfo(this);
        email = (TextView) findViewById(R.id.vip_change_email);
        receive = (CheckBox) findViewById(R.id.uinfo_checkbox);
        findViewById(R.id.vip_change_back).setOnClickListener(this);
        findViewById(R.id.vip_change_email_btn).setOnClickListener(this);
        if (mUser.getEmail() != null)
            email.setText(mUser.getEmail());
        handler.sendEmptyMessage(1);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (mUser.isPushEmail() == 1) {//3.5接受简报 isPushEmail
                    receive.setChecked(true);
                } else receive.setChecked(false);
            }

        }
    };

    @Override
    public String getActivityName() {
        return ChangeEmailActivity.class.getName();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_change_back) {
            finish();
        } else if (v.getId() == R.id.vip_change_email_btn) {
            Intent i = new Intent(this, BandDetailActivity.class);
            i.putExtra("band_type", BandAccountOperate.EMAIL);
            i.putExtra("band_user", mUser);
            startActivityForResult(i, BandDetailActivity.BAND_SUCCESS);
        } else if (v.getId() == R.id.uinfo_checkbox) {
            if (mUser.isPushEmail() == 1) {//isPushEmail
                handler.sendEmptyMessage(1);
                Tools.showToast(ChangeEmailActivity.this, R.string.userinfo_receive_tips1);
            } else {
                if (mUser.isValEmail()) {
                    User u = mUser;
                    u.setPushEmail(1);
                    modifyUserInfo(u, "", true);
                } else {
                    Tools.showToast(ChangeEmailActivity.this, R.string.userinfo_receive_tips);
                    handler.sendEmptyMessage(1);
                }
            }
        }
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @param url        图片的相对地址(通过上传头像获得)
     */
    public void modifyUserInfo(User user, String url, final boolean isPushEmail) {
        if (user == null) return;
        showLoadingDialog(true);
        // 只更新头像、昵称信息
        UserOperateController.getInstance(this).modifyUserInfo(user.getUid(), user.getToken(),
                user.getUserName(), user.getNickName(), url, null, user.getDesc(), isPushEmail, new UserFetchEntryListener() {
            @Override
            public void setData(final Entry entry) {
                showLoadingDialog(false);
                if (entry instanceof User) {
                    User resUser = (User) entry;
                    ErrorMsg error = resUser.getError();
                    if (error.getNo() == 0) {
                        mUser.setPushEmail(resUser.isPushEmail());
                        if (isPushEmail) {
                            showToast(R.string.userinfo_receive_success);
                        }
                        handler.sendEmptyMessage(1);
                    }// 修改失败
                    else {
                        showToast(error.getDesc());
                    }
                }
            }
        });
    }
}
