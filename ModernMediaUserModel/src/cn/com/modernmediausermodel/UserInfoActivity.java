package cn.com.modernmediausermodel;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.com.modernmedia.util.sina.SinaAPI;
import cn.com.modernmedia.util.sina.SinaRequestListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.unit.ImgFileManager;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.UploadAvatarResult;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.FetchPhotoManager;
import cn.com.modernmediausermodel.util.QQLoginUtil;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.widget.UserCardView;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * 用户信息界面
 * 
 * @author ZhuQiao
 * 
 */
public class UserInfoActivity extends SlateBaseActivity implements
		OnClickListener {
	public static final String KEY_ACTION_FROM = "from"; // 作为获得标识从哪个按钮点击跳转到该页面的key
	public static final int FROM_REGISTER = 1;
	public static final int FROM_SINA_LOGIN = 2;
	public static final int FROM_QQ_LOGIN = 3;

	private static final String KEY_IMAGE = "data";
	private static final String AVATAR_PIC = "avatar.jpg";
	private Context mContext;
	private EditText mNickNameEdit, mUserNameEdit;
	private ImageView mClearImage, mCloseImage, avatar;
	private TextView mModifyPwdText, mLogoutText;
	private RelativeLayout mAccountLayout;
	private Button mSureBtn;
	// private UserModelInterface mUserInterface;
	private UserOperateController mController;
	private String picturePath;
	private User mUser;
	// // 是否是注册成功后进入的
	// protected boolean isFromRegister = false;
	protected int actionFrom = 0; // 识从哪个按钮点击跳转到该页面
	private String shareContent = "";// 分享内容
	private int gotoPage;// 完成需要跳转的页面
	private Animation shakeAnim;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		// mUserInterface = UserModelInterface.getInstance(this);
		mController = UserOperateController.getInstance(mContext);
		picturePath = Environment.getExternalStorageDirectory().getPath() + "/"
				+ AVATAR_PIC;
		if (getIntent() != null && getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();
			actionFrom = bundle.getInt(KEY_ACTION_FROM, 0);
			shareContent = bundle.getString(WriteNewCardActivity.KEY_DATA);
			mUser = (User) getIntent().getSerializableExtra(
					UserCardView.KEY_USER);
			gotoPage = bundle.getInt(UserPageTransfer.LOGIN_KEY);
		}
		setContentView(-1);
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
			layoutResID = R.layout.user_info_activity;
			super.setContentView(layoutResID);
			init();
		} else {
			super.setContentView(layoutResID);
		}
	}

	private void init() {
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		mNickNameEdit = (EditText) findViewById(R.id.userinfo_name_edit);
		mUserNameEdit = (EditText) findViewById(R.id.userinfo_accout_edit);
		mAccountLayout = (RelativeLayout) findViewById(R.id.userinfo_account_rl);
		mClearImage = (ImageView) findViewById(R.id.userinfo_clear);
		mCloseImage = (ImageView) findViewById(R.id.userinfo_img_close);
		avatar = (ImageView) findViewById(R.id.userinfo_avatar);
		mModifyPwdText = (TextView) findViewById(R.id.userinfo_modify_pwd_btn);
		mLogoutText = (TextView) findViewById(R.id.userinfo_logout_btn);
		mSureBtn = (Button) findViewById(R.id.userinfo_complete);

		mClearImage.setOnClickListener(this);
		mCloseImage.setOnClickListener(this);
		avatar.setOnClickListener(this);
		mModifyPwdText.setOnClickListener(this);
		mLogoutText.setOnClickListener(this);
		mSureBtn.setOnClickListener(this);

		if (actionFrom == FROM_REGISTER) { // 注册页面
			mModifyPwdText.setVisibility(View.GONE);
			mLogoutText.setVisibility(View.GONE);
			mAccountLayout.setVisibility(View.GONE);
		} else if (actionFrom == FROM_SINA_LOGIN || actionFrom == FROM_QQ_LOGIN) { // 新浪微博、QQ登录跳转
			mModifyPwdText.setVisibility(View.GONE);
			mLogoutText.setVisibility(View.GONE);
			mAccountLayout.setVisibility(View.VISIBLE);
		} else {
			mSureBtn.setBackgroundResource(R.drawable.login_btn_register_bg);
			mSureBtn.setText(R.string.confirm_change);
			mSureBtn.setTextColor(getResources().getColor(R.color.black_bg));
			LayoutParams layoutParams = (LayoutParams) mSureBtn
					.getLayoutParams();
			layoutParams.topMargin = (int) getResources()
					.getDimensionPixelSize(
							R.dimen.userinfo_confirm_change_margintop);
			mModifyPwdText.getPaint().setFlags(
					mModifyPwdText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			mLogoutText.getPaint().setFlags(
					mLogoutText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		}
		UserTools.setAvatar(this, "", avatar);

		if (actionFrom == FROM_SINA_LOGIN) { // 微博首次登录
			getSinaUserInfo();
		} else if (actionFrom == FROM_QQ_LOGIN) { // QQ首次登录
			getQqUserInfo();
		} else {
			User user = UserDataHelper.getUserLoginInfo(this);
			if (user != null) {
				getUserInfo(user.getUid(), user.getToken());
			}
		}
	}

	/**
	 * 获取保存图片的路径
	 * 
	 * @return
	 */
	protected void setPicturePath(String path) {
		picturePath = path + AVATAR_PIC;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.userinfo_clear) {
			doClear();
		} else if (id == R.id.userinfo_img_close) {
			doClose();
		} else if (id == R.id.userinfo_avatar) {
			doFecthPicture();
		} else if (id == R.id.userinfo_modify_pwd_btn) {
			gotoModifyPwdActivity();
		} else if (id == R.id.userinfo_logout_btn) {
			doLoginOut();
		} else if (id == R.id.userinfo_complete) {
			if (mNickNameEdit != null) {
				String nick = mNickNameEdit.getText().toString();
				if ((actionFrom == FROM_SINA_LOGIN || actionFrom == FROM_QQ_LOGIN)
						&& mUserNameEdit != null && mUser != null) { // 新浪微博首次登录
					if (UserTools.checkString(mUserNameEdit.getText()
							.toString(), mUserNameEdit, shakeAnim)
							&& UserTools.checkString(nick, mNickNameEdit,
									shakeAnim)) {
						mUser.setUserName(mUserNameEdit.getText().toString());
						mUser.setNickName(mNickNameEdit.getText().toString());
						getOpenUserAvatar(mUser.getAvatar());
					}
				} else if (UserTools
						.checkString(nick, mNickNameEdit, shakeAnim)) { // 注册
					doAfterSetup(nick);
				}
			}
		}
	}

	/**
	 * 获取当前用户的信息 通过uid和token获取当前用户信息， uid和token用post方式接收(可检测用户登录状态) url
	 * 
	 * @param uid
	 *            uid
	 * @param token
	 *            用户token
	 */
	protected void getUserInfo(String uId, String token) {
		showLoadingDialog(true);
		mController.getInfoByIdAndToken(uId, token,
				new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						showLoadingDialog(false);
						String toast = "";
						if (entry instanceof User) {
							mUser = (User) entry;
							ErrorMsg error = mUser.getError();
							// 取得成功
							if (error.getNo() == 0) {
								mUser.setLogined(true);
								UserDataHelper.saveUserLoginInfo(mContext,
										mUser);
								UserDataHelper.saveAvatarUrl(mContext,
										mUser.getUserName(), mUser.getAvatar());
								setDataForWidget(mUser);
								// 由于getUserInfo接口没返回token，所以用本地保存的
								if (UserDataHelper.getUserLoginInfo(mContext) != null) {
									mUser.setToken(UserDataHelper
											.getUserLoginInfo(mContext)
											.getToken());
								}
								return;
							} else {
								toast = error.getDesc();
							}
						}
						showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_get_userinfo_failed)
								: toast);
						// token过期或者其他的情况，把本地保存的用户信息删除
						UserDataHelper.clearLoginInfo(mContext);
						// 当验证错误，跳至登陆页重新登录
						finish();
					}
				});
	}

	/**
	 * 获取新浪用户相关信息
	 * 
	 */
	public void getSinaUserInfo() {
		showLoadingDialog(true);
		final SinaAPI sinaAPI = SinaAPI.getInstance(mContext);
		sinaAPI.fetchUserInfo(new SinaRequestListener() {

			@Override
			public void onSuccess(String response) {
				showLoadingDialog(false);
				JSONObject object;
				try {
					object = new JSONObject(response);
					mUser = new User();
					mUser.setSinaId(object.optString("idstr", "")); // 新浪ID
					mUser.setNickName(object.optString("screen_name")); // 昵称
					mUser.setUserName(object.optString("name"));
					mUser.setAvatar(object.optString("profile_image_url"));// 用户头像地址（中图），50×50像素
					mUser.setToken(sinaAPI.getToken());
					UserDataHelper.saveAvatarUrl(mContext, mUser.getUserName(),
							mUser.getAvatar());
					// 设置昵称
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							setDataForWidget(mUser);
							UserTools.setAvatar(mContext, mUser.getAvatar(),
									avatar);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailed(String error) {
				showLoadingDialog(false);
			}
		});
	}

	/**
	 * 获取QQ用户相关信息
	 * 
	 */
	public void getQqUserInfo() {
		showLoadingDialog(true);
		QQLoginUtil.getInstance(mContext).getUserInfo(new IUiListener() {

			@Override
			public void onError(UiError arg0) {
				showLoadingDialog(false);
			}

			@Override
			public void onComplete(Object arg0) {
				showLoadingDialog(false);
				if (arg0 instanceof JSONObject) {
					JSONObject object = (JSONObject) arg0;
					mUser = new User();
					mUser.setQqId(QQLoginUtil.getInstance(mContext).getOpenId());
					mUser.setNickName(object.optString("nickname")); // 昵称
					mUser.setAvatar(object.optString("figureurl_qq_1"));// 用户头像地址（小图，40×40像素；'figureurl_qq_2'是100*100像素大图）
					// 设置昵称
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							setDataForWidget(mUser);
							UserTools.setAvatar(mContext, mUser.getAvatar(),
									avatar);
						}
					});
				}
			}

			@Override
			public void onCancel() {
				showLoadingDialog(false);
			}
		});
	}

	/**
	 * 获取开放平台用户头像
	 */
	private void getOpenUserAvatar(final String url) {
		showLoadingDialog(true);
		SlateApplication.finalBitmap.display(url,
				new ImageDownloadStateListener() {

					@Override
					public void loading() {
						showLoadingDialog(false);
					}

					@Override
					public void loadOk(Bitmap bitmap, NinePatchDrawable drawable) {
						showLoadingDialog(false);
						uploadAvatar(ImgFileManager.getBitmapPath(url));
					}

					@Override
					public void loadError() {
						showLoadingDialog(false);
					}
				});
	}

	/**
	 * 开放平台(新浪微博、QQ等)账号登录
	 * 
	 * @param user
	 *            用户信息
	 * @param avatar
	 *            用户开放平台的头像
	 * @param type
	 *            平台类型,目前0:普通登录；1：新浪微博；2：腾讯qq
	 */
	public void openLogin(String avatar) {
		int type = 0;
		if (actionFrom == FROM_SINA_LOGIN) {
			type = 1;
		} else if (actionFrom == FROM_QQ_LOGIN) {
			type = 2;
		}
		mController.openLogin(mUser, avatar, type,
				new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						showLoadingDialog(false);
						String toast = "";
						if (entry instanceof User) {
							mUser = (User) entry;
							ErrorMsg error = mUser.getError();
							if (error.getNo() == 0) {
								UserDataHelper
										.saveUserLoginInfo(mContext, mUser);
								UserDataHelper.saveAvatarUrl(mContext,
										mUser.getUserName(), mUser.getAvatar());
								// 用于判定是否用开放平台账号登录过
								if (actionFrom == FROM_SINA_LOGIN) { // 新浪微博
									UserDataHelper.saveSinaLoginedName(
											mContext, mUser.getSinaId(),
											mUser.getUserName());
								} else if (actionFrom == FROM_QQ_LOGIN) { // QQ
									UserDataHelper.saveQqLoginedName(mContext,
											mUser.getQqId(),
											mUser.getUserName());
								}
								UserPageTransfer.afterLogin(mContext, mUser,
										shareContent, gotoPage);
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
	 * 设置昵称
	 * 
	 * @param user
	 */
	protected void setDataForWidget(User user) {
		if (mNickNameEdit != null)
			mNickNameEdit.setText(user.getNickName());
		if (avatar != null)
			UserTools.setAvatar(this, user, avatar);
	}

	/**
	 * 清除昵称
	 */
	protected void doClear() {
		if (mNickNameEdit != null)
			mNickNameEdit.setText("");
	}

	/**
	 * 返回
	 */
	protected void doClose() {
		finish();
	}

	/**
	 * 跳转至修改密码页
	 */
	protected void gotoModifyPwdActivity() {
		UserPageTransfer.gotoModifyPasswordActivity(this);
	}

	/**
	 * 登出
	 */
	private void doLoginOut() {
		// 新浪微博登录状态时，需清除存储的新浪ID或者QQID对应的用户名
		if (mUser != null) {
			if (!TextUtils.isEmpty(mUser.getSinaId())) {
				UserDataHelper.saveSinaLoginedName(mContext, mUser.getSinaId(),
						"");
			} else if (!TextUtils.isEmpty(mUser.getQqId())) {
				UserDataHelper.saveQqLoginedName(mContext, mUser.getQqId(), "");
			}
		}
		// 清除存储的登录信息
		UserDataHelper.clearLoginInfo(mContext);
		// 清除金币信息
		if (mUser != null) {
			UserDataHelper.saveIsFirstUseCoin(mContext, mUser.getUid(), true);
		}
		afterLoginOut();
	}

	protected void afterLoginOut() {
		if (UserApplication.logOutListener != null) {
			UserApplication.logOutListener.onLogout();
		}
		SlateApplication.loginStatusChange = true;
		// 返回上级界面
		finish();
	}

	/**
	 * 设置成功
	 */
	protected void doAfterSetup(String nickName) {
		if (!TextUtils.isEmpty(nickName)) {
			mUser.setNickName(nickName);
			modifyUserInfo(mUser, "", "", true);
		}
		if (actionFrom == FROM_REGISTER) { // 注册页面
			UserPageTransfer.afterLogin(this, mUser, shareContent, gotoPage);
		}
	}

	protected void doFecthPicture() {
		FetchPhotoManager fetchPhotoManager = new FetchPhotoManager(this,
				picturePath);
		fetchPhotoManager.doFecthPicture();
	}

	/**
	 * 上传用户头像
	 * 
	 * @param imagePath
	 *            头像存储在本地的路径
	 */
	protected void uploadAvatar(String imagePath) {
		if (mUser == null || TextUtils.isEmpty(imagePath))
			return;

		if (!new File(imagePath).exists()) {
			showLoadingDialog(false);
			showToast(R.string.msg_avatar_get_failed);
			return;
		}

		showLoadingDialog(true);
		mController.uploadUserAvatar(imagePath, new UserFetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				showLoadingDialog(false);
				String toast = "";
				if (entry instanceof UploadAvatarResult) {
					UploadAvatarResult result = (UploadAvatarResult) entry;
					String status = result.getStatus();
					if (status.equals("success")) { // 头像上传成功
						if (!TextUtils.isEmpty(result.getImagePath())
								&& !TextUtils.isEmpty(result.getAvatarPath())) {
							if (actionFrom == FROM_SINA_LOGIN
									|| actionFrom == FROM_QQ_LOGIN) {
								// 将得到的头像下载地址存储到本地
								UserDataHelper.saveAvatarUrl(mContext,
										mUser.getUserName(),
										result.getAvatarPath());
								afterFetchPicture(mUser, result.getAvatarPath());
								openLogin(result.getImagePath());
							} else {
								modifyUserInfo(mUser, result.getImagePath(),
										result.getAvatarPath(), false);
							}
							return;
						}
					} else {
						toast = result.getMsg();
					}
				}
				showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_avatar_upload_failed)
						: toast);
			}
		});
	}

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 * @param url
	 *            图片的相对地址(通过上传头像获得)
	 * @param avatar_url
	 *            头像的绝对地址
	 * @param setNickName
	 */
	protected void modifyUserInfo(User user, String url,
			final String avatar_url, final boolean setNickName) {
		if (mUser == null)
			return;
		showLoadingDialog(true);
		user.setPassword(""); // 只更新头像、昵称信息
		mController.modifyUserInfo(user.getUid(), user.getToken(),
				user.getUserName(), user.getNickName(), url,
				user.getPassword(), new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						showLoadingDialog(false);
						String toast = "";
						if (entry instanceof User) {
							User resUser = (User) entry;
							ErrorMsg error = resUser.getError();
							// 修改成功
							if (error.getNo() == 0) {
								if (setNickName) {
									afterSetNickName();
								} else {
									showToast(R.string.msg_avatar_upload_success);
									// 将得到的头像下载地址存储到本地
									UserDataHelper.saveAvatarUrl(mContext,
											mUser.getUserName(), avatar_url);
									afterFetchPicture(mUser, avatar_url);
								}
								return;
							} else {
								toast = error.getDesc();
							}
						}
						showToast(TextUtils.isEmpty(toast) ? getString(R.string.msg_modify_failed)
								: toast);
					}
				});
	}

	/**
	 * 设置拿到头像后的操作
	 * 
	 * @param user
	 * @param picUrl
	 */
	protected void afterFetchPicture(User user, String picUrl) {
		UserTools.setAvatar(this, picUrl, avatar);
	}

	/**
	 * 成功设置昵称
	 */
	protected void afterSetNickName() {
		showToast(R.string.msg_modify_success);
		if (mUser != null)
			UserDataHelper.saveUserLoginInfo(this, mUser);
		if (actionFrom == FROM_REGISTER)
			finish();
	}

	/**
	 * 设置头像
	 * 
	 * @param bitmap
	 */
	protected void setBitmapForAvatar(Bitmap bitmap) {
		if (avatar != null)
			UserTools.transforCircleBitmap(bitmap, avatar);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == FetchPhotoManager.REQUEST_CAMERA) {
				UserTools.startPhotoZoom(this,
						Uri.fromFile(new File(picturePath)), picturePath);
			} else if (requestCode == FetchPhotoManager.REQUEST_GALLERY) {
				if (data != null) {
					UserTools.startPhotoZoom(this, data.getData(), picturePath);
				}
			} else if (requestCode == UserTools.REQUEST_ZOOM) {
				if (data != null) {
					Bitmap bitmap = data.getExtras().getParcelable(KEY_IMAGE);
					ImgFileManager.saveImage(bitmap, picturePath);
					if (bitmap != null) {
						uploadAvatar(picturePath);
						bitmap.recycle();
						bitmap = null;
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

	@Override
	public String getActivityName() {
		return UserInfoActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}
