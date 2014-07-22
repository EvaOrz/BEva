package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.util.sina.SinaAPI;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmedia.util.sina.UserModelAuthListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserModelInterface;
import cn.com.modernmediausermodel.listener.RequestListener;
import cn.com.modernmediausermodel.model.LoginParm;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 默认登录页面
 * 
 * @author ZhuQiao
 * 
 */
public class DefaultLoginActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private EditText mAcountEdit, mPasswordEdit;
	private ImageView mClearImage, mForgetPwdImage, mCloseImage;
	private Button mRegisterBtn, mLoginBtn, mSinaLoginBtn;
	private UserModelInterface userModel;
	private Animation shakeAnim;
	private SinaAuth weiboAuth;

	private String shareData = "";// 分享的内容
	private int gotoPage;// 登录完成需要跳转的页面
	private boolean shouldFinish = false;// 当直接跳到发笔记页的时候，不会立即执行destory，所以延迟500ms

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		userModel = UserModelInterface.getInstance(this);
		if (checkIsShare() && UserDataHelper.getUserLoginInfo(mContext) != null) {
			UserPageTransfer.gotoWriteCardActivity(this, shareData, false);
			shouldFinish = true;
		}
		initContentView();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (shouldFinish) {
					finish();
				}
			}
		}, 500);
	}

	protected void initContentView() {
		setContentView(-1);
	}

	/**
	 * 检查是否来自应用分享，若是，则会取得要分享的文本信息
	 * 
	 * @return
	 */
	public boolean checkIsShare() {
		Intent intent = getIntent();
		if (intent != null && intent.getExtras() != null) {
			Bundle bundle = intent.getExtras();
			gotoPage = bundle.getInt(UserPageTransfer.LOGIN_KEY);
			if (intent.getAction() != null
					&& intent.getAction().equals(Intent.ACTION_SEND)) {
				if (bundle.containsKey(Intent.EXTRA_TEXT)) {
					shareData = bundle.getString(Intent.EXTRA_TEXT);
					if (TextUtils.isEmpty(shareData)) {
						finish();
					} else {
						return true;
					}
				}
			}
		}
		return false;
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
		mSinaLoginBtn = (Button) findViewById(R.id.login_btn_sina_login);

		if (CommonApplication.width < 720) {
			// 字数太多，显示不完整..
			mSinaLoginBtn.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
		}

		LoginParm parm = UserTools.parseColumn(this);
		UserTools.setText((TextView) findViewById(R.id.login_desc),
				parm.getLogin_desc());

		mCloseImage.setOnClickListener(this);
		mClearImage.setOnClickListener(this);
		mForgetPwdImage.setOnClickListener(this);
		mRegisterBtn.setOnClickListener(this);
		mLoginBtn.setOnClickListener(this);
		mSinaLoginBtn.setOnClickListener(this);
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
		} else if (v.getId() == R.id.login_btn_sina_login) { // 新浪登录
			doSinaLogin();
		}
	}

	private void doSinaLogin() {
		// 新浪微博认证
		weiboAuth = new SinaAuth(mContext);
		if (!weiboAuth.checkIsOAuthed()) {
			weiboAuth.oAuth();
		} else {
			doAfterIsOAuthed();
		}
		weiboAuth.setAuthListener(new UserModelAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess) {
				if (isSuccess) {
					doAfterIsOAuthed();
				} else {
					showToast(R.string.sina_login_failed);
				}
			}
		});

	}

	private void doAfterIsOAuthed() {
		User user = UserDataHelper.getUserLoginInfo(mContext);
		String sinaId = SinaAPI.getInstance(mContext).getSinaId();
		String userName = UserDataHelper.getSinaLoginedName(mContext, sinaId);
		if (user != null && !TextUtils.isEmpty(user.getSinaId())
				&& user.getSinaId().equals(sinaId)) { // 已经用新浪微博账号登录
			afterLogin(user);
		} else { // 用户名不为空时，说明以前登录过；反之，则为第一次登录
			User mUser = new User();
			mUser.setUserName(userName);
			mUser.setSinaId(sinaId);
			sinaLogin(mUser);
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
					afterRegister(user, shareData);
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
	 * 登录页面登录操作
	 * 
	 * @param user
	 */
	public void sinaLogin(final User user) {
		userModel.sinaLogin(user, "", new RequestListener() {

			@Override
			public void onSuccess(Entry entry) {
				showLoadingDialog(false);
				User mUser = (User) entry;
				UserDataHelper.saveUserLoginInfo(mContext, mUser);
				UserDataHelper.saveSinaLoginedName(mContext, mUser.getSinaId(),
						mUser.getUserName());
				UserDataHelper.saveAvatarUrl(mContext, mUser.getUserName(),
						mUser.getAvatar());
				afterLogin(mUser);
			}

			@Override
			public void onFailed(Entry error) {
				showLoadingDialog(false);
				if (error != null) {
					Error errors = (Error) error;
					// 微博用户第一次登录时，前往资料页面完善资料
					if (errors.getNo() == 2041) {
						UserPageTransfer.gotoUserInfoActivity(mContext,
								DefaultUserInfoActivity.FROM_SINA_LOGIN, null,
								gotoPage);
					} else {
						showToast(errors.getDesc());
					}

				}
			}
		});
	}

	/**
	 * 注册成功，默认进入设置用户信息界面
	 * 
	 * @param user
	 * @param content
	 *            分享内容
	 */
	protected void afterRegister(User user, String content) {
		showToast(R.string.msg_register_success);
		UserPageTransfer.gotoUserInfoActivity(this,
				DefaultUserInfoActivity.FROM_REGISTER, content, gotoPage);
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
		UserPageTransfer.afterLogin(this, user, shareData, gotoPage);
	}

	/**
	 * 返回
	 */
	protected void doClose() {
		finish();
	}

	@Override
	public void finish() {
		if (UserDataHelper.getUserLoginInfo(this) != null)
			setResult(RESULT_OK);
		super.finish();
		overridePendingTransition(R.anim.activity_close_enter,
				R.anim.activity_close_exit);
	}

	public static Class<DefaultLoginActivity> getLoginClass() {
		return DefaultLoginActivity.class;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doClose();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void reLoadData() {
	}

	@Override
	public String getActivityName() {
		return DefaultLoginActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
