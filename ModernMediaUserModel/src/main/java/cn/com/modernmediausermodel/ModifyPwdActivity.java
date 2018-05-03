package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 默认修改密码页面
 *
 * @author ZhuQiao
 */
public class ModifyPwdActivity extends SlateBaseActivity implements OnClickListener {
    private Context mContext;
    private EditText mOldPwdEdit;
    private EditText mNewPwdEdit;
    private ImageView mClearImage;
    private ImageView mForgetImage;
    private ImageView mCloseImage;
    private Button mSureBtn;
    private UserOperateController mController;
    private Animation shakeAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(-1);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (layoutResID == -1) {
            layoutResID = R.layout.modify_pwd_activity;
            super.setContentView(layoutResID);
            init();
        } else {
            super.setContentView(layoutResID);
        }
    }

    private void init() {
        shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
        mController = UserOperateController.getInstance(mContext);
        mOldPwdEdit = (EditText) findViewById(R.id.modify_pwd_old_edit);
        mNewPwdEdit = (EditText) findViewById(R.id.modify_pwd_new_edit);
        mClearImage = (ImageView) findViewById(R.id.modify_pwd_img_clear);
        mForgetImage = (ImageView) findViewById(R.id.modify_pwd_img_forget);
        mCloseImage = (ImageView) findViewById(R.id.modify_pwd_close);
        mSureBtn = (Button) findViewById(R.id.modify_sure);

        mClearImage.setOnClickListener(this);
        mForgetImage.setOnClickListener(this);
        mCloseImage.setOnClickListener(this);
        mSureBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.modify_pwd_img_clear) {
            doClear(1);
        } else if (id == R.id.modify_pwd_img_forget) {
            doClear(2);
            // User user = SlateDataHelper.getUserLoginInfo(mContext);
            // if (user != null && !TextUtils.isEmpty(user.getUserName())) {
            // doForgetPwd(user.getUserName());
            // }
        } else if (id == R.id.modify_pwd_close) {
            doClose();
        } else if (id == R.id.modify_sure) {
            if (mOldPwdEdit != null && mNewPwdEdit != null) {
                String old = mOldPwdEdit.getText().toString();
                String ne = mNewPwdEdit.getText().toString();
                //UserTools.checkString(old, mOldPwdEdit, shakeAnim) &&
                if (UserTools.checkString(ne, mNewPwdEdit, shakeAnim)) {

                    if (ne.length() > 3 && ne.length() < 17) {
                        doModifyPwd(old, ne);
                    } else showToast(R.string.password_length_error);// 密码长度错误
                }
            }
        }
    }

    /**
     * 清除旧密码
     */
    protected void doClear(int type) {
        if (type == 1) {
            if (mOldPwdEdit != null) mOldPwdEdit.setText("");
        } else if (type == 2) {
            if (mNewPwdEdit != null) mNewPwdEdit.setText("");
        }

    }

    /**
     * 忘记密码
     *
     * @param userName 用户userName
     */
    protected void doForgetPwd(final String userName) {
        if (UserTools.checkIsEmailOrPhone(mContext, userName)) {
            showLoadingDialog(true);
            mController.getPassword(userName, null, null, new UserFetchEntryListener() {

                @Override
                public void setData(final Entry entry) {
                    showLoadingDialog(false);
                    String toast = "";
                    if (entry instanceof User) {
                        User resUser = (User) entry;
                        ErrorMsg error = resUser.getError();
                        if (error.getNo() == 0) {// 发送请求成功
                            String msg = String.format(getString(R.string.msg_find_pwd_success, userName));
                            UserTools.showDialogMsg(mContext, getString(R.string.reset_password), msg);
                            return;
                        } else {
                            toast = error.getDesc();
                        }
                    }
                    showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_find_pwd_failed) : toast);
                }
            });
        }
    }

    /**
     * 返回
     */
    protected void doClose() {
        finish();
    }

    /**
     * 修改用户密码
     *
     * @param oldPwd      旧密码
     * @param newPassword 新密码
     */
    protected void doModifyPwd(String oldPwd, String newPassword) {
        User user = SlateDataHelper.getUserLoginInfo(mContext);
        if (UserTools.checkPasswordFormat(mContext, newPassword) && user != null) {
            showLoadingDialog(true);
            mController.modifyUserPassword(user.getUid(), user.getToken(), user.getUserName(), oldPwd, newPassword, new UserFetchEntryListener() {

                @Override
                public void setData(final Entry entry) {
                    showLoadingDialog(false);
                    String toast = "";
                    if (entry instanceof User) {
                        User resUser = (User) entry;
                        ErrorMsg error = resUser.getError();
                        // 修改成功
                        if (error.getNo() == 0) {
                            showToast(R.string.msg_modify_success);
                            finish();
                            return;
                        } else {
                            toast = error.getDesc();
                        }
                    }
                    showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_modify_pwd_failed) : toast);
                }
            });
        }
    }

    @Override
    public String getActivityName() {
        return ModifyPwdActivity.class.getName();
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
