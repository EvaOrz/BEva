package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 修改邮箱页
 * 
 * @author jiancong
 * 
 */
public class ModifyEmailActivity extends SlateBaseActivity implements
		OnClickListener {

	private EditText mEmailEdit, mPwdEdit;
	private Animation shakeAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		setContentView(R.layout.activity_modify_email);
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		mEmailEdit = (EditText) findViewById(R.id.modify_email_address_edit);
		mPwdEdit = (EditText) findViewById(R.id.modify_email_password_edit);
		ImageView mClearAddressImage = (ImageView) findViewById(R.id.modify_email_address_clear);
		ImageView mClearPwdImage = (ImageView) findViewById(R.id.modify_email_pwd_clear);
		TextView complete = (TextView) findViewById(R.id.modify_email_button_complete);
		View back = findViewById(R.id.modify_email_button_back);
		back.setOnClickListener(this);
		complete.setOnClickListener(this);
		mClearAddressImage.setOnClickListener(this);
		mClearPwdImage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.modify_email_button_back) {
			finish();
		} else if (id == R.id.modify_email_button_complete) {
			String email = mEmailEdit.getText().toString();
			String password = mPwdEdit.getText().toString();
			if (UserTools.checkString(email, mEmailEdit, shakeAnim)
					&& UserTools.checkString(password, mPwdEdit, shakeAnim))
				doModifyEmail(email, password);
		} else if (id == R.id.modify_email_address_clear) {
			mEmailEdit.setText("");
		} else if (id == R.id.modify_email_pwd_clear) {
			mPwdEdit.setText("");
		}
	}

	/**
	 * 修改邮箱
	 * 
	 * @param email
	 *            新的邮箱
	 * @param password
	 *            新的密码
	 */
	private void doModifyEmail(final String email, String password) {
		User user = SlateDataHelper.getUserLoginInfo(this);
		if (user == null)
			return;
		user.setUserName(email);
		user.setPassword(password);
		showLoadingDialog(true);
		UserOperateController.getInstance(this).modifyUserInfo(user.getUid(),
				user.getToken(), user.getUserName(), user.getNickName(), "",
				user.getPassword(), new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						showLoadingDialog(false);
						String msg = "";
						if (entry instanceof User) {
							User resUser = (User) entry;
							ErrorMsg error = resUser.getError();
							// 修改成功
							if (error.getNo() == 0) {
								showToast(R.string.msg_modify_success);
								// 更新本地存储的用户名
								SlateDataHelper.saveUserName(
										ModifyEmailActivity.this, email);
								// 跳转到金币页面
								UserPageTransfer.gotoMyCoinActivity(
										ModifyEmailActivity.this, true, true);
								return;
							} else {
								msg = error.getDesc();
							}
						}
						UserTools
								.showDialogMsg(
										ModifyEmailActivity.this,
										"",
										TextUtils.isEmpty(msg) ? getString(R.string.msg_modify_email_failed)
												: msg);
					}
				});
	}

	@Override
	public String getActivityName() {
		return ModifyEmailActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}
