package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.VerifyCode;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 手机注册忘记密码
 * 
 * @author lusiyuan
 *
 */
public class ForgetPwdActivity extends SlateBaseActivity implements
		OnClickListener {

	private EditText phoneEdit, codeEdit, passwordEdit;
	private ImageView close, phoneClear, pwdClear;
	private Button complete, getCode;
	private String phoneNum;
	private boolean canGetVerify = true;// 是否可获取验证码
	private UserOperateController mController;
	private Animation shakeAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		phoneNum = getIntent().getStringExtra("phone_number");
		mController = UserOperateController.getInstance(this);
		initView();
	}

	private void initView() {
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		close = (ImageView) findViewById(R.id.forget_img_close);// 退出
		phoneEdit = (EditText) findViewById(R.id.forget_phone);
		codeEdit = (EditText) findViewById(R.id.forget_verify_edit);
		passwordEdit = (EditText) findViewById(R.id.forget_new_pwd);
		phoneClear = (ImageView) findViewById(R.id.forget_phone_clear);
		pwdClear = (ImageView) findViewById(R.id.forget_new_pwd_clear);
		complete = (Button) findViewById(R.id.forget_complete);
		getCode = (Button) findViewById(R.id.forget_get_verify);

		close.setOnClickListener(this);
		// phoneEdit.setOnClickListener(this);
		// codeEdit.setOnClickListener(this);
		// passwordEdit.setOnClickListener(this);
		phoneClear.setOnClickListener(this);
		pwdClear.setOnClickListener(this);
		complete.setOnClickListener(this);
		getCode.setOnClickListener(this);

		phoneEdit.setText(phoneNum + "");

	}

	@Override
	public void onClick(View v) {
		String newPass = "", code = "";
		if (phoneEdit != null) {
			phoneNum = phoneEdit.getText().toString();
		}
		if (codeEdit != null) {
			code = phoneEdit.getText().toString();
		}
		if (passwordEdit != null) {
			newPass = passwordEdit.getText().toString();
		}

		if (v.getId() == R.id.forget_img_close) {
			finish();
		} else if (v.getId() == R.id.forget_phone_clear) {
			doClear(phoneEdit);
		} else if (v.getId() == R.id.forget_new_pwd_clear) {
			doClear(passwordEdit);
		} else if (v.getId() == R.id.forget_complete) {//
			doForgetPwd(phoneNum, newPass, code);
		} else if (v.getId() == R.id.forget_get_verify) {// 获取验证码
			doGetVerifyCode();
		}
	}

	/**
	 * 获取手机验证码
	 */
	protected void doGetVerifyCode() {
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
			mController.getVerifyCode(phoneNum, new UserFetchEntryListener() {

				@Override
				public void setData(Entry entry) {
					if (entry instanceof VerifyCode) {
						showToast(entry.toString());

					}
				}
			});
		}

	}

	/**
	 * 忘记密码
	 * 
	 * @param uid
	 *            uid
	 * @param token
	 *            用户token
	 */
	protected void doForgetPwd(final String userName, final String newPwd,
			final String code) {
		if (UserTools.checkString(userName, phoneEdit, shakeAnim)
				&& UserTools.checkIsPhone(this, userName)
				&& UserTools.checkString(code, codeEdit, shakeAnim)
				&& UserTools.checkString(newPwd, passwordEdit, shakeAnim)
				&& UserTools.checkPasswordFormat(this, newPwd)) {// 判空，判电话格式，判密码格式
			showLoadingDialog(true);
			mController.getPassword(userName, code, newPwd,
					new UserFetchEntryListener() {

						@Override
						public void setData(final Entry entry) {
							showLoadingDialog(false);
							String toast = "";
							if (entry instanceof User) {
								User resUser = (User) entry;
								ErrorMsg error = resUser.getError();
								if (error.getNo() == 0) {// 发送请求成功
									toast = "修改成功";
									doLogin(userName, newPwd);
								} else {
									toast = error.getDesc();
								}
							}
							showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_find_pwd_failed)
									: toast);
						}
					});
		}
	}

	/**
	 * 用户登录
	 * 
	 * @param userName
	 *            用户名称
	 * @param password
	 *            密码
	 */
	protected void doLogin(final String userName, final String password) {
		showLoadingDialog(true);
		mController.login(this, userName, password,
				new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						showLoadingDialog(false);
						String toast = "";
						if (entry instanceof User) {
							User user = (User) entry;
							ErrorMsg error = user.getError();
							if (error.getNo() == 0
									&& !TextUtils.isEmpty(user.getUid())) {
								// 登录成功
								user.setPassword(password);
								user.setLogined(true);
								user.setPhone(userName);
								// 将相关信息用SharedPreferences存储
								SlateDataHelper.saveUserLoginInfo(
										ForgetPwdActivity.this, user);
								SlateDataHelper.saveAvatarUrl(
										ForgetPwdActivity.this,
										user.getUserName(), user.getAvatar());
								finish();
								return;
							} else {
								toast = error.getDesc();
							}
						}
						showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_login_fail)
								: toast);
					}
				});
	}

	/**
	 * 清除用户名
	 */
	protected void doClear(EditText e) {
		if (e != null)
			e.setText("");
	}

	@Override
	public String getActivityName() {
		return null;
	}

	@Override
	public Activity getActivity() {
		return null;
	}

}
