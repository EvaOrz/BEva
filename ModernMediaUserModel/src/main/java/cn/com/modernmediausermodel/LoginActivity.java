package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.QQLoginUtil;
import cn.com.modernmediausermodel.util.UserCentManager;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.util.WeixinLoginUtil;
import cn.com.modernmediausermodel.util.WeixinLoginUtil.WeixinAuthListener;

/**
 * 登录页面
 * 
 * @author Eva.
 * 
 */
public class LoginActivity extends SlateBaseActivity implements OnClickListener {
	private Context mContext;
	private UserOperateController mController;
	private Animation shakeAnim;
	private SinaAuth weiboAuth;
	private Button mLoginBtn;
	private TextView forgetPwd, register;
	private ImageView mSinaLoginBtn, mQQLoginBtn, mWeixinLoginBtn, mClearImage,
			mCloseImage;
	private EditText mAcountEdit, mPasswordEdit;
	private String shareData = "";// 分享的内容
	private int gotoPage;// 登录完成需要跳转的页面
	private boolean shouldFinish = false;// 当直接跳到发笔记页的时候，不会立即执行destory，所以延迟500ms

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mController = UserOperateController.getInstance(this);
		if (checkIsShare() && SlateDataHelper.getUserLoginInfo(this) != null) {
			UserPageTransfer.gotoWriteCardActivity(this, shareData, false);
			shouldFinish = true;
		}

		setContentView(-1);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (shouldFinish) {
					finish();
				}
			}
		}, 500);
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
			layoutResID = R.layout.activity_new_login;
			super.setContentView(layoutResID);
			init();
		} else {
			super.setContentView(layoutResID);
		}
	}

	private void init() {
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		forgetPwd = (TextView) findViewById(R.id.login_forget_pwd);
		register = (TextView) findViewById(R.id.login_registers);
		// 忘记密码 注册设置下划线
		forgetPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mAcountEdit = (EditText) findViewById(R.id.login_account);
		mClearImage = (ImageView) findViewById(R.id.login_img_clear);
		mLoginBtn = (Button) findViewById(R.id.login_btn_login);
		mSinaLoginBtn = (ImageView) findViewById(R.id.login_btn_sina_login);
		mQQLoginBtn = (ImageView) findViewById(R.id.login_btn_qq_login);
		mWeixinLoginBtn = (ImageView) findViewById(R.id.login_btn_weixin_login);
		mCloseImage = (ImageView) findViewById(R.id.login_img_close);

		mPasswordEdit = (EditText) findViewById(R.id.login_password);
		if (SlateApplication.APP_ID == 37) {// verycity
			mAcountEdit.setHint(R.string.email);
			mQQLoginBtn.setVisibility(View.INVISIBLE);
		}

		// if (SlateApplication.mConfig.getHas_sina() != 1
		// && SlateApplication.mConfig.getHas_qq() != 1
		// && SlateApplication.mConfig.getHas_weixin() != 1) { // 都不存在时，隐藏所有
		// findViewById(R.id.login_other_desc).setVisibility(View.GONE);
		// mSinaLoginBtn.setVisibility(View.GONE);
		// mQQLoginBtn.setVisibility(View.GONE);
		// mWeixinLoginBtn.setVisibility(View.GONE);
		// ((TextView) findViewById(R.id.login_desc))
		// .setText(R.string.login_title_text);
		// } else if (SlateApplication.mConfig.getHas_weixin() != 1) {// 不用微信登陆
		// mWeixinLoginBtn.setVisibility(View.GONE);
		// } else if (SlateApplication.mConfig.getHas_sina() != 1) {// 不用新浪微博登陆
		// mSinaLoginBtn.setVisibility(View.GONE);
		// } else if (SlateApplication.mConfig.getHas_qq() != 1) {// 不用QQ登陆
		// mQQLoginBtn.setVisibility(View.GONE);
		// }

		mCloseImage.setOnClickListener(this);
		mClearImage.setOnClickListener(this);
		forgetPwd.setOnClickListener(this);
		register.setOnClickListener(this);
		mLoginBtn.setOnClickListener(this);
		mSinaLoginBtn.setOnClickListener(this);
		mQQLoginBtn.setOnClickListener(this);
		mWeixinLoginBtn.setOnClickListener(this);
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
			if (mAcountEdit != null)
				mAcountEdit.setText("");
		} else if (v.getId() == R.id.login_forget_pwd) {
			Intent i = new Intent(mContext, ForgetPwdActivity.class);
			startActivity(i);
		} else if (v.getId() == R.id.login_img_close) {
			finish();
		} else if (v.getId() == R.id.login_registers) {// 注册
			Intent i = new Intent(mContext, RegisterActivity.class);
			startActivity(i);
		} else if (v.getId() == R.id.login_btn_login) {
			if (UserTools.checkString(userName, mAcountEdit, shakeAnim)
					&& UserTools.checkString(psd, mPasswordEdit, shakeAnim))
				doLogin(userName, psd);
		} else if (v.getId() == R.id.login_btn_sina_login) { // 新浪登录
			doSinaLogin();
		} else if (v.getId() == R.id.login_btn_qq_login) { // qq登录
			doQQLogin();
		} else if (v.getId() == R.id.login_btn_weixin_login) { // 微信登录
			doWeixinlogin();
		}

	}

	private void doWeixinlogin() {
		// showLoadingDialog(true);
		WeixinLoginUtil weixinloginUtil = WeixinLoginUtil.getInstance(mContext);
		weixinloginUtil.loginWithWeixin();
		weixinloginUtil.setLoginListener(new WeixinAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess, User user) {
				if (isSuccess) {
					openLogin(user, 3);
				} else {// 登录过
					afterLogin(user);
					showLoadingDialog(false);
				}
			}
		});
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
	 * 开放平台(新浪微博、QQ等)账号登录
	 * 
	 * @param user
	 *            用户信息
	 * @param type
	 *            平台类型,目前0:普通登录；1：新浪微博；2：腾讯qq；3：微信
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
						SlateDataHelper.saveUserLoginInfo(mContext, mUser);
						if (type == 1) { // 新浪微博
							UserDataHelper.saveSinaLoginedName(mContext,
									mUser.getSinaId(), mUser.getUserName());
						} else if (type == 2) { // QQ
							UserDataHelper.saveQqLoginedName(mContext,
									mUser.getQqId(), mUser.getUserName());
						} else if (type == 3) {// 微信
							UserDataHelper.saveWeinxinLoginedName(mContext,
									mUser.getWeixinId(), mUser.getUserName());
						}
						SlateDataHelper.saveAvatarUrl(mContext,
								mUser.getUserName(), mUser.getAvatar());
						afterLogin(mUser);
						return;
					} else if (error.getNo() == 2041
							|| (error.getNo() == 1004 && (type != 0))) { // 用户第一次登录时，前往资料页面完善资料
						UserPageTransfer.gotoUserInfoActivity(mContext, type,
								null, "", gotoPage);
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

	private void doQQLogin() {
		// showLoadingDialog(true);
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
		// showLoadingDialog(true);
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
		User user = SlateDataHelper.getUserLoginInfo(mContext);
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
		User user = SlateDataHelper.getUserLoginInfo(mContext);
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
	 * 用户登录
	 * 
	 * @param userName
	 *            用户名称
	 * @param password
	 *            密码
	 */
	protected void doLogin(final String userName, final String password) {
		// 检查格式
		if (UserTools.checkIsEmailOrPhone(mContext, userName)
				&& UserTools.checkPasswordFormat(mContext, password)) {
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
									// 将相关信息用SharedPreferences存储
									if (UserTools.checkIsPhone(mContext,
											userName)) {
										user.setPhone(userName);
									} else {
										user.setEmail(userName);
									}
									SlateDataHelper.saveUserLoginInfo(mContext,
											user);
									SlateDataHelper.saveAvatarUrl(mContext,
											user.getUserName(),
											user.getAvatar());
									afterLogin(user);
									return;
								} else {
									toast = error.getDesc();
								}
							}
							showToast(R.string.msg_login_fail);
						}
					});
		}
	}

	@Override
	public void finish() {
		if (SlateDataHelper.getUserLoginInfo(this) != null)
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
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
