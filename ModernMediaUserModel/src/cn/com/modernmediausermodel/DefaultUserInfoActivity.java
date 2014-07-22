package cn.com.modernmediausermodel;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
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
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.listener.ImageDownloadStateListener;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.sina.SinaAPI;
import cn.com.modernmedia.util.sina.SinaRequestListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserModelInterface;
import cn.com.modernmediausermodel.listener.RequestListener;
import cn.com.modernmediausermodel.model.LoginParm;
import cn.com.modernmediausermodel.model.UploadAvatarResult;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.util.FetchPhotoManager;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserFileManager;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.widget.UserCardView;

/**
 * 用户信息界面
 * 
 * @author ZhuQiao
 * 
 */
public class DefaultUserInfoActivity extends BaseActivity implements
		OnClickListener {
	public static final String KEY_ACTION_FROM = "from"; // 作为获得标识从哪个按钮点击跳转到该页面的key
	public static final int FROM_REGISTER = 1;
	public static final int FROM_SINA_LOGIN = 2;

	private static final String KEY_IMAGE = "data";
	private static final String AVATAR_PIC = "avatar.jpg";
	private Context mContext;
	private EditText mNickNameEdit, mUserNameEdit;
	private ImageView mClearImage, mCloseImage, avatar;
	private TextView mModifyPwdText, mLogoutText;
	private RelativeLayout mAccountLayout;
	private Button mSureBtn;
	private UserModelInterface mUserInterface;
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
		mUserInterface = UserModelInterface.getInstance(this);
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

		if (actionFrom != 0) {
			LoginParm parm = UserTools.parseColumn(this);
			UserTools.setText((TextView) findViewById(R.id.userinfo_desc),
					parm.getUserinfo_desc());
		}

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
		} else if (actionFrom == FROM_SINA_LOGIN) { // 新浪登录跳转
			mModifyPwdText.setVisibility(View.GONE);
			mLogoutText.setVisibility(View.GONE);
			mAccountLayout.setVisibility(View.VISIBLE);
		} else {
			mSureBtn.setBackgroundResource(R.drawable.bg_btn);
			mSureBtn.setText(R.string.confirm_change);
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
				if (actionFrom == FROM_SINA_LOGIN && mUserNameEdit != null
						&& mUser != null) { // 新浪微博首次登录
					if (UserTools.checkString(mUserNameEdit.getText()
							.toString(), mUserNameEdit, shakeAnim)
							&& UserTools.checkString(nick, mNickNameEdit,
									shakeAnim)) {
						mUser.setUserName(mUserNameEdit.getText().toString());
						mUser.setNickName(mNickNameEdit.getText().toString());
						getSinaAvatar(mUser.getAvatar());
					}
				} else if (UserTools
						.checkString(nick, mNickNameEdit, shakeAnim)) {
					doAfterSetup(nick);
				}
			}
		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @param uId
	 * @param token
	 */
	protected void getUserInfo(String uId, String token) {
		showLoadingDialog(true);
		mUserInterface.getUserInfo(uId, token, new RequestListener() {

			@Override
			public void onSuccess(Entry entry) {
				showLoadingDialog(false);
				mUser = (User) entry;
				setDataForWidget(mUser);
				// 由于getUserInfo接口没返回token，所以用本地保存的
				if (UserDataHelper.getUserLoginInfo(mContext) != null) {
					mUser.setToken(UserDataHelper.getUserLoginInfo(mContext)
							.getToken());
				}
			}

			@Override
			public void onFailed(Entry error) {
				showLoadingDialog(false);
				if (error instanceof Error) {
					showToast(((Error) error).getDesc());
					gotoLoginActivity();
				}
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
				JSONObject object;
				try {
					showLoadingDialog(false);
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
					showLoadingDialog(false);
				}
			}

			@Override
			public void onFailed(String error) {
				showLoadingDialog(false);
			}
		});
	}

	/**
	 * 获取新浪微博头像
	 */
	private void getSinaAvatar(final String url) {
		showLoadingDialog(true);
		CommonApplication.finalBitmap.display(url,
				new ImageDownloadStateListener() {

					@Override
					public void loading() {
						showLoadingDialog(false);
					}

					@Override
					public void loadOk(Bitmap bitmap) {
						showLoadingDialog(false);
						uploadAvatar(FileManager.getBitmapPath(url));
					}

					@Override
					public void loadError() {
						showLoadingDialog(false);
					}
				});
	}

	/**
	 * 新浪微博登录
	 */
	protected void sinaLogin(String avatar) {
		showLoadingDialog(true);
		mUserInterface.sinaLogin(mUser, avatar, new RequestListener() {

			@Override
			public void onSuccess(Entry entry) {
				showLoadingDialog(false);
				User user = (User) entry;
				UserDataHelper.saveUserLoginInfo(mContext, user);
				UserDataHelper.saveAvatarUrl(mContext, user.getUserName(),
						user.getAvatar());
				UserPageTransfer.afterLogin(mContext, user, shareContent,
						gotoPage);
				// 用于判定是否用新浪微博登录过
				UserDataHelper.saveSinaLoginedName(mContext, mUser.getSinaId(),
						mUser.getUserName());
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
	 * 当验证错误，跳至登陆页重新登录
	 */
	protected void gotoLoginActivity() {
		super.finish();
	}

	/**
	 * 登出
	 */
	private void doLoginOut() {
		// 新浪微博登录状态时，需清除存储的新浪ID对应的用户名
		if (mUser != null && !TextUtils.isEmpty(mUser.getSinaId())) {
			UserDataHelper.saveSinaLoginedName(mContext, mUser.getSinaId(), "");
		}
		// 清除存储的登录信息
		UserDataHelper.clearLoginInfo(mContext);
		afterLoginOut();
	}

	protected void afterLoginOut() {
		if (UserApplication.logOutListener != null) {
			UserApplication.logOutListener.onLogout();
		}
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
	 * 上传头像
	 * 
	 * @param imagePath
	 */
	protected void uploadAvatar(String imagePath) {
		if (mUser == null || TextUtils.isEmpty(imagePath))
			return;

		if (!new File(imagePath).exists()) {
			showLoadingDialog(false);
			showToast("取得头像失败");
			return;
		}

		showLoadingDialog(true);
		mUserInterface.uploadAvatar(imagePath, new RequestListener() {

			@Override
			public void onSuccess(Entry entry) {
				showLoadingDialog(false);
				UploadAvatarResult result = (UploadAvatarResult) entry;
				if (!TextUtils.isEmpty(result.getImagePath())
						&& !TextUtils.isEmpty(result.getAvatarPath()))
					if (actionFrom == FROM_SINA_LOGIN) {
						// 将得到的头像下载地址存储到本地
						UserDataHelper.saveAvatarUrl(mContext,
								mUser.getUserName(), result.getAvatarPath());
						afterFetchPicture(mUser, result.getAvatarPath());
						sinaLogin(result.getImagePath());
					} else {
						modifyUserInfo(mUser, result.getImagePath(),
								result.getAvatarPath(), false);
					}
			}

			@Override
			public void onFailed(Entry error) {
				showLoadingDialog(false);
				if (error instanceof Error) {
					showToast(((Error) error).getDesc());
				}
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
		mUserInterface.modifyUserInfo(user, url, new RequestListener() {

			@Override
			public void onSuccess(Entry entry) {
				showLoadingDialog(false);
				if (setNickName) {
					afterSetNickName();
				} else {
					showToast(R.string.msg_avatar_upload_success);
					// 将得到的头像下载地址存储到本地
					UserDataHelper.saveAvatarUrl(mContext, mUser.getUserName(),
							avatar_url);
					afterFetchPicture(mUser, avatar_url);
				}
			}

			@Override
			public void onFailed(Entry error) {
				showLoadingDialog(false);
				if (error instanceof Error) {
					showToast(((Error) error).getDesc());
				}
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
		showToast(R.string.modify_success);
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
					UserFileManager.saveImage(bitmap, picturePath);
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
	public void reLoadData() {
	}

	@Override
	public String getActivityName() {
		return DefaultUserInfoActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}
