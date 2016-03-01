package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
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
	private TextView getCode;
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
		phoneEdit = (EditText) findViewById(R.id.forget_account);
		codeEdit = (EditText) findViewById(R.id.forget_verify);
		passwordEdit = (EditText) findViewById(R.id.forget_password);

		findViewById(R.id.forget_account_clear).setOnClickListener(this);
		findViewById(R.id.forget_pwd_clear).setOnClickListener(this);
		findViewById(R.id.forget_confirm).setOnClickListener(this);
		getCode = (TextView) findViewById(R.id.forget_verify_get);

		findViewById(R.id.forget_close).setOnClickListener(this);
		getCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String newPass = "", code = "";
		phoneNum = phoneEdit.getText().toString();
		code = phoneEdit.getText().toString();
		newPass = passwordEdit.getText().toString();

		if (v.getId() == R.id.forget_close) {// 关闭
			finish();
		} else if (v.getId() == R.id.forget_account_clear) {// 清除账号
			doClear(phoneEdit);
		} else if (v.getId() == R.id.forget_pwd_clear) {// 清除密码
			doClear(passwordEdit);
		} else if (v.getId() == R.id.forget_confirm) {// 完成
			doForgetPwd(phoneNum, newPass, code);
		} else if (v.getId() == R.id.forget_verify_get) {// 获取验证码
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
