package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.VerifyCode;
import cn.com.modernmediausermodel.util.UserCentManager;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 注册页面
 * 
 * @author Eva. 密码长度为4~16位 密码有安全性等级划分，根据各项指标进行加分，最后根据总分数定级 密码字符类型分为四种
 *         数字（Unicode:30~39） 小写字母（Unicode：61~7A） 大写字母（Unicode：41~5A）
 *         符号（Unicode：除以上外，20~7E） 密码长度分数 4~6字符：0分 7~8字符：5分 9~16字符：10分 单类型字符总数分数
 *         1~2字符：0分 3~16字符：5分 总类型数目分数 1种：0分 2种：2分 3种：3分 4种：5分 每有一个字符与上一个字符相同：-1分
 *         分数定级 -∞~15分：弱 16~25分：中 26~35分：强
 * 
 * 
 * 
 * 
 */
public class RegisterActivity extends SlateBaseActivity implements
		OnClickListener {
	private UserOperateController mController;
	private Animation shakeAnim;
	private TextView weak, mid, high;
	private EditText mAcountEdit, mNickEdit, mPwdEdit, mVerifyEdit;
	private TextView getVerify;

	private boolean canGetVerify = true;// 是否可获取验证码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mController = UserOperateController.getInstance(this);
		initView();
	}

	private void initView() {
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		findViewById(R.id.register_close).setOnClickListener(this);
		mAcountEdit = (EditText) findViewById(R.id.register_account);
		mNickEdit = (EditText) findViewById(R.id.register_nickname);
		mPwdEdit = (EditText) findViewById(R.id.register_password);
		weak = (TextView) findViewById(R.id.register_weak);
		mid = (TextView) findViewById(R.id.register_mid);
		high = (TextView) findViewById(R.id.register_high);
		mPwdEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && s.toString().length() > 0) {
					String ss = s.toString();
					if (ss.length() < 7) {
						password(weak, true);
						password(mid, false);
						password(high, false);
					} else if (ss.length() > 6 && ss.length() < 13) {
						password(weak, false);
						password(mid, true);
						password(high, false);
					} else if (ss.length() > 12) {
						password(weak, false);
						password(mid, false);
						password(high, true);
					}
				}
			}
		});
		mVerifyEdit = (EditText) findViewById(R.id.register_verify);
		getVerify = (TextView) findViewById(R.id.register_verify_get);
		getVerify.setOnClickListener(this);
		findViewById(R.id.register_account_clear).setOnClickListener(this);
		findViewById(R.id.register_nickname_clear).setOnClickListener(this);
		findViewById(R.id.register_pwd_clear).setOnClickListener(this);
	}

	/**
	 * 
	 * @param tv
	 * @param ifshow
	 *            {true 显示高亮}
	 */
	private void password(TextView tv, boolean ifshow) {
		if (ifshow) {
			if (tv.getId() == R.id.register_weak)
				tv.setBackgroundColor(getResources().getColor(R.color.red));
			else if (tv.getId() == R.id.register_mid)
				tv.setBackgroundColor(getResources().getColor(R.color.yellow));
			else if (tv.getId() == R.id.register_high)
				tv.setBackgroundColor(getResources().getColor(
						R.color.my_coin_green_color));

		} else
			tv.setBackgroundColor(getResources().getColor(R.color.gray_line));
	}

	@Override
	public void onClick(View v) {
		String account = "", nick = "", pwd = "", verifyCode = "";// 账号、昵称、密码、验证码
		account = mAcountEdit.getText().toString();
		nick = mNickEdit.getText().toString();
		pwd = mPwdEdit.getText().toString();

		// 点击处理
		if (mVerifyEdit != null)
			verifyCode = mVerifyEdit.getText().toString();

		if (v.getId() == R.id.register_close) {// 关闭
			finish();
		} else if (v.getId() == R.id.register) {// 注册
			if (UserTools.checkIsEmailOrPhone(this, account)) {
				if (UserTools.checkIsPhone(this, account))
					doRegister(account, nick, pwd, account, verifyCode);
				else
					doRegister(account, nick, pwd, null, null);
			} else
				showToast(R.string.get_account_error);// 账号格式错误

		} else if (v.getId() == R.id.register_verify_get) {// 获取验证码
			if (UserTools.checkIsPhone(this, account))
				doGetVerifyCode(account);
			else
				showToast(R.string.get_account_error);// 手机号码格式错误

		} else if (v.getId() == R.id.register_account_clear) {// 清除账号输入
			mAcountEdit.setText("");
		} else if (v.getId() == R.id.register_nickname_clear) {// 清除昵称输入
			mNickEdit.setText("");
		} else if (v.getId() == R.id.register_pwd_clear) {// 清除密码输入
			mPwdEdit.setText("");
		}
	}

	/**
	 * 用户注册
	 * 
	 * @param userName
	 *            用户名称
	 * @param password
	 *            密码
	 */
	protected void doRegister(final String account, String nick,
			final String pwd, String phone, String code) {
		showLoadingDialog(true);
		// username \ password \ code \ phone \ nick
		mController.register(account, pwd, code, phone, nick,
				new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						showLoadingDialog(false);
						String toast = "";
						if (entry instanceof User) {
							User user = (User) entry;
							ErrorMsg error = user.getError();
							if (error.getNo() == 0) {
								// 注册成功
								user.setEmail(account);
								user.setPassword(pwd);
								user.setLogined(true);
								// 将相关信息用SharedPreferences存储
								SlateDataHelper.saveUserLoginInfo(
										RegisterActivity.this, user);
								// 返回上一级界面
								afterRegister(user);
								return;
							} else {
								toast = error.getDesc();
							}
						}
						showToast(R.string.msg_register_failed);
					}
				});
	}

	/**
	 * 获取手机验证码
	 */
	protected void doGetVerifyCode(String phone) {
		if (canGetVerify) {
			canGetVerify = false;
			// 开启倒计时器
			new CountDownTimer(60000, 1000) {
				public void onTick(long millisUntilFinished) {
					getVerify.setText(millisUntilFinished / 1000 + "s重新获取");
				}

				public void onFinish() {
					getVerify.setText(R.string.get_verify_code);
					canGetVerify = true;
				}
			}.start();
			mController.getVerifyCode(phone, new UserFetchEntryListener() {

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
	 * 注册成功，默认进入设置用户信息界面
	 * 
	 * @param user
	 */
	protected void afterRegister(User user) {
		showToast(R.string.msg_register_success);
		UserCentManager.getInstance(this).addLoginCoinCent();
		SlateApplication.loginStatusChange = true;
		UserPageTransfer.gotoUserInfoActivity(this, 1, null,
				user.getPassword(), 1);
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
