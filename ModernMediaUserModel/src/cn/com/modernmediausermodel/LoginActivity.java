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
import android.widget.TextView;
import cn.com.modernmedia.util.sina.SinaAPI;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmedia.util.sina.UserModelAuthListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.QQLoginUtil;
import cn.com.modernmediausermodel.util.UserCentManager;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 默认登录页面
 * 
 * @author ZhuQiao
 * 
 */
public class LoginActivity extends SlateBaseActivity implements OnClickListener {
	private Context mContext;
	private EditText mAcountEdit, mPasswordEdit;
	private ImageView mClearImage, mForgetPwdImage, mCloseImage;
	private Button mRegisterBtn, mLoginBtn;
	private ImageView mSinaLoginBtn, mQQLoginBtn;
	private Animation shakeAnim;
	private SinaAuth weiboAuth;

	private String shareData = "";// 分享的内容
	private int gotoPage;// 登录完成需要跳转的页面
	private boolean shouldFinish = false;// 当直接跳到发笔记页的时候，不会立即执行destory，所以延迟500ms
	private UserOperateController mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mController = UserOperateController.getInstance(mContext);
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
			layoutResID = R.layout.activity_login;
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
		mSinaLoginBtn = (ImageView) findViewById(R.id.login_btn_sina_login);
		mQQLoginBtn = (ImageView) findViewById(R.id.login_btn_qq_login);
		initOpenBtnHeight();
		if (SlateApplication.mConfig.getHas_sina() != 1
				&& SlateApplication.mConfig.getHas_qq() != 1) { // 都不存在时，隐藏所有
			findViewById(R.id.login_other_desc).setVisibility(View.GONE);
			mSinaLoginBtn.setVisibility(View.GONE);
			mQQLoginBtn.setVisibility(View.GONE);
			((TextView) findViewById(R.id.login_desc))
					.setText(R.string.login_title_text);
		} else if (SlateApplication.mConfig.getHas_sina() != 1) {// 不用新浪微博登陆
			mSinaLoginBtn.setVisibility(View.GONE);
		} else if (SlateApplication.mConfig.getHas_qq() != 1) {// 不用QQ登陆
			mQQLoginBtn.setVisibility(View.GONE);
		}

		mCloseImage.setOnClickListener(this);
		mClearImage.setOnClickListener(this);
		mForgetPwdImage.setOnClickListener(this);
		mRegisterBtn.setOnClickListener(this);
		mLoginBtn.setOnClickListener(this);
		mSinaLoginBtn.setOnClickListener(this);
		mQQLoginBtn.setOnClickListener(this);
	}

	private void initOpenBtnHeight() {
		int offset = getResources().getDimensionPixelSize(
				R.dimen.login_btn_weibo_marginLeft);
		int height = (SlateApplication.width - 2 * offset) * 64 / 442;
		mQQLoginBtn.getLayoutParams().height = height;
		mSinaLoginBtn.getLayoutParams().height = height;
	}

	@Override
	public void onClick(View v) {
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
		} else if (v.getId() == R.id.login_btn_qq_login) { // qq登录
			doQQLogin();
		}
	}

	private void doQQLogin() {
		showLoadingDialog(true);
		QQLoginUtil qqLoginUtil = QQLoginUtil.getInstance(mContext);
		qqLoginUtil.login();
		qqLoginUtil.setLoginListener(new UserModelAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess) {
				if (isSuccess) {
					doAfterQQIsAuthed();
				} else {
					showLoadingDialog(false);
				}
			}
		});
	}

	private void doSinaLogin() {
		showLoadingDialog(true);
		// 新浪微博认证
		weiboAuth = new SinaAuth(mContext);
		if (!weiboAuth.checkIsOAuthed()) {
			weiboAuth.oAuth();
		} else {
			doAfterSinaIsOAuthed();
		}
		weiboAuth.setAuthListener(new UserModelAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess) {
				if (isSuccess) {
					doAfterSinaIsOAuthed();
				} else {
					showLoadingDialog(false);
				}
			}
		});

	}

	private void doAfterSinaIsOAuthed() {
		User user = UserDataHelper.getUserLoginInfo(mContext);
		String sinaId = SinaAPI.getInstance(mContext).getSinaId();
		if (user != null && !TextUtils.isEmpty(user.getSinaId())
				&& user.getSinaId().equals(sinaId)) { // 已经用新浪微博账号在本应用上登录
			afterLogin(user);
		} else {
			User mUser = new User();
			// 用户名不为空时，说明以前登录过；反之，则为第一次登录
			mUser.setUserName(UserDataHelper.getSinaLoginedName(mContext,
					sinaId));
			mUser.setSinaId(sinaId);
			openLogin(mUser, 1);
		}
	}

	private void doAfterQQIsAuthed() {
		User user = UserDataHelper.getUserLoginInfo(mContext);
		String qqId = QQLoginUtil.getInstance(mContext).getOpenId();
		if (user != null && !TextUtils.isEmpty(user.getQqId())
				&& user.getQqId().equals(qqId)) { // 已经用QQ账号在本应用上登录
			afterLogin(user);
		} else {
			User mUser = new User();
			// 用户名不为空时，说明以前登录过；反之，则为第一次登录
			mUser.setUserName(UserDataHelper.getQqLoginedName(mContext, qqId));
			mUser.setQqId(qqId);
			openLogin(mUser, 2);
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
	 * 
	 * @param userName
	 */
	protected void doForgetPwd(final String userName) {
		if (UserTools.checkIsEmail(mContext, userName)) {
			showLoadingDialog(true);
			mController.getPassword(userName, new UserFetchEntryListener() {

				@Override
				public void setData(final Entry entry) {
					showLoadingDialog(false);
					String toast = "";
					if (entry instanceof User) {
						User resUser = (User) entry;
						ErrorMsg error = resUser.getError();
						if (error.getNo() == 0) { // 发送请求成功
							String msg = String.format(getString(
									R.string.msg_find_pwd_success, userName));
							UserTools.showDialogMsg(mContext,
									getString(R.string.reset_password), msg);
							return;
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
	 * 用户注册
	 * 
	 * @param userName
	 *            用户名称
	 * @param password
	 *            密码
	 */
	protected void doRegister(String userName, final String password) {
		// 检查格式
		if (UserTools.checkIsEmail(mContext, userName)
				&& UserTools.checkPasswordFormat(mContext, password)) {
			showLoadingDialog(true);
			mController.register(userName, password,
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
									user.setPassword(password);
									user.setLogined(true);
									// 将相关信息用SharedPreferences存储
									UserDataHelper.saveUserLoginInfo(mContext,
											user);
									// 返回上一级界面
									afterRegister(user, shareData);
									return;
								} else {
									toast = error.getDesc();
								}
							}
							showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_register_failed)
									: toast);
						}
					});
		}
	}

	/**
	 * 开放平台(新浪微博、QQ等)账号登录
	 * 
	 * @param user
	 *            用户信息
	 * @param type
	 *            平台类型,目前0:普通登录；1：新浪微博；2：腾讯qq
	 */
	public void openLogin(final User user, final int type) {
		mController.openLogin(user, "", type, new UserFetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				showLoadingDialog(false);
				String toast = "";
				if (entry instanceof User) {
					User mUser = (User) entry;
					ErrorMsg error = mUser.getError();
					if (error.getNo() == 0) {
						UserDataHelper.saveUserLoginInfo(mContext, mUser);
						if (type == 1) { // 新浪微博
							UserDataHelper.saveSinaLoginedName(mContext,
									mUser.getSinaId(), mUser.getUserName());
						} else if (type == 2) { // QQ
							UserDataHelper.saveQqLoginedName(mContext,
									mUser.getQqId(), mUser.getUserName());
						}
						UserDataHelper.saveAvatarUrl(mContext,
								mUser.getUserName(), mUser.getAvatar());
						afterLogin(mUser);
						return;
					} else if (error.getNo() == 2041) { // 用户第一次登录时，前往资料页面完善资料
						int from = 0;
						if (type == 1) {
							from = UserInfoActivity.FROM_SINA_LOGIN;
						} else if (type == 2) {
							from = UserInfoActivity.FROM_QQ_LOGIN;
						}
						UserPageTransfer.gotoUserInfoActivity(mContext, from,
								null, gotoPage);
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
	 * 注册成功，默认进入设置用户信息界面
	 * 
	 * @param user
	 * @param content
	 *            分享内容
	 */
	protected void afterRegister(User user, String content) {
		showToast(R.string.msg_register_success);
		UserCentManager.getInstance(mContext).addLoginCoinCent();
		SlateApplication.loginStatusChange = true;
		UserPageTransfer.gotoUserInfoActivity(this,
				UserInfoActivity.FROM_REGISTER, content, gotoPage);
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
		// 检查格式
		if (UserTools.checkIsEmail(mContext, userName)
				&& UserTools.checkPasswordFormat(mContext, password)) {
			showLoadingDialog(true);
			mController.login(userName, password, new UserFetchEntryListener() {

				@Override
				public void setData(final Entry entry) {
					showLoadingDialog(false);
					String toast = "";
					if (entry instanceof User) {
						User user = (User) entry;
						ErrorMsg error = user.getError();
						if (error.getNo() == 0) {
							// 登录成功
							user.setPassword(password);
							user.setLogined(true);
							// 将相关信息用SharedPreferences存储
							UserDataHelper.saveUserLoginInfo(mContext, user);
							UserDataHelper.saveAvatarUrl(mContext,
									user.getUserName(), user.getAvatar());
							afterLogin(user);
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
	}

	/**
	 * 登录成功
	 * 
	 * @param user
	 */
	protected void afterLogin(User user) {
		showLoadingDialog(false);
		// 返回上一级界面
		showToast(R.string.msg_login_success);
		UserCentManager.getInstance(mContext).addLoginCoinCent();
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

	public static Class<LoginActivity> getLoginClass() {
		return LoginActivity.class;
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
	public String getActivityName() {
		return LoginActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
