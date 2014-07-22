package cn.com.modernmediausermodel;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserModelInterface;
import cn.com.modernmediausermodel.listener.RequestListener;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 默认登录页面
 * 
 * @author ZhuQiao
 * 
 */
public abstract class DefaultLoginActivity extends SlateBaseActivity implements
		OnClickListener {
	private Context mContext;
	private EditText mAcountEdit;
	private EditText mPasswordEdit;
	private ImageView mClearImage;
	private ImageView mForgetPwdImage;
	private ImageView mCloseImage;
	private Button mRegisterBtn;
	private Button mLoginBtn;
	private UserModelInterface userModel;
	private Animation shakeAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		userModel = UserModelInterface.getInstance(this);
	}

	/**
	 * 设置页面
	 * 
	 * @param layoutResID
	 *            页面layout_id,可传-1，代表使用默认页面
	 */
	@Override
	public void setContentView(int layoutResID) {
		if (layoutResID == -1) {
			layoutResID = R.layout.default_login_activity;
			super.setContentView(layoutResID);
			init();
		} else {
			super.setContentView(layoutResID);
		}
	}

	private void init() {
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		mAcountEdit = (EditText) findViewById(R.id.login_account);
		mPasswordEdit = (EditText) findViewById(R.id.login_password);
		mClearImage = (ImageView) findViewById(R.id.login_img_clear);
		mForgetPwdImage = (ImageView) findViewById(R.id.login_img_forget);
		mCloseImage = (ImageView) findViewById(R.id.login_img_close);
		mRegisterBtn = (Button) findViewById(R.id.login_btn_register);
		mLoginBtn = (Button) findViewById(R.id.login_btn_login);

		mCloseImage.setOnClickListener(this);
		mClearImage.setOnClickListener(this);
		mForgetPwdImage.setOnClickListener(this);
		mRegisterBtn.setOnClickListener(this);
		mLoginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO switch报错？？
		String userName = "";
		String psd = "";
		if (mAcountEdit != null) {
			userName = mAcountEdit.getText().toString();
		}
		if (mPasswordEdit != null) {
			psd = mPasswordEdit.getText().toString();
		}
		if (v.getId() == R.id.login_img_clear) {
			doClear();
		} else if (v.getId() == R.id.login_img_forget) {
			if (UserTools.checkString(userName, mAcountEdit, shakeAnim))
				doForgetPwd(userName);
		} else if (v.getId() == R.id.login_img_close) {
			doClose();
		} else if (v.getId() == R.id.login_btn_register) {
			if (UserTools.checkString(userName, mAcountEdit, shakeAnim)
					&& UserTools.checkString(psd, mPasswordEdit, shakeAnim))
				doRegister(userName, psd);
		} else if (v.getId() == R.id.login_btn_login) {
			if (UserTools.checkString(userName, mAcountEdit, shakeAnim)
					&& UserTools.checkString(psd, mPasswordEdit, shakeAnim))
				doLogin(userName, psd);
		}
	}

	/**
	 * 清除用户名
	 */
	protected void doClear() {
		if (mAcountEdit != null)
			mAcountEdit.setText("");
	}

	/**
	 * 忘记密码
	 */
	protected void doForgetPwd(final String userName) {
		if (UserTools.checkIsEmail(mContext, userName)) {
			showLoadingDialog(true);
			userModel.getPassword(userName, new RequestListener() {

				@Override
				public void onSuccess(Entry entry) {
					showLoadingDialog(false);
					String msg = String.format(getString(
							R.string.msg_find_pwd_success, userName));
					UserTools.showDialogMsg(mContext,
							getString(R.string.reset_password), msg);
				}

				@Override
				public void onFailed(Entry error) {
					showLoadingDialog(false);
					if (error instanceof User.Error)
						showToast(((Error) error).getDesc());
				}
			});
		}
	}

	/**
	 * 注册
	 */
	protected void doRegister(String userName, String password) {
		// 检查格式
		if (UserTools.checkIsEmail(mContext, userName)
				&& UserTools.checkPasswordFormat(mContext, password)) {
			showLoadingDialog(true);
			userModel.register(userName, password, new RequestListener() {

				@Override
				public void onSuccess(Entry entry) {
					showLoadingDialog(false);
					User user = (User) entry;
					// 返回上一级界面
					afterRegister(user);
				}

				@Override
				public void onFailed(Entry error) {
					showLoadingDialog(false);
					if (error instanceof User.Error) {
						showToast(((User.Error) error).getDesc());
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
	}

	/**
	 * 登录
	 */
	protected void doLogin(final String userName, String password) {
		// 检查格式
		if (UserTools.checkIsEmail(mContext, userName)
				&& UserTools.checkPasswordFormat(mContext, password)) {
			showLoadingDialog(true);
			userModel.login(userName, password, new RequestListener() {

				@Override
				public void onSuccess(Entry entry) {
					showLoadingDialog(false);
					User user = (User) entry;
					afterLogin(user);
				}

				@Override
				public void onFailed(Entry error) {
					showLoadingDialog(false);
					if (error instanceof User.Error) {
						showToast(((User.Error) error).getDesc());
					}
				}
			});
		}
	}

	/**
	 * 登录成功
	 * 
	 * @param user
	 */
	protected void afterLogin(User user) {
		// 返回上一级界面
		showToast(R.string.msg_login_success);
		finish();
	}

	/**
	 * 返回
	 */
	protected void doClose() {
		 finish();
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

}
