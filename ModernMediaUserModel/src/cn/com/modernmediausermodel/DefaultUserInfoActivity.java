package cn.com.modernmediausermodel;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserModelInterface;
import cn.com.modernmediausermodel.listener.RequestListener;
import cn.com.modernmediausermodel.model.UploadAvatarResult;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.util.FetchPhotoManager;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 用户信息界面
 * 
 * @author ZhuQiao
 * 
 */
public abstract class DefaultUserInfoActivity extends SlateBaseActivity
		implements OnClickListener {
	private static final String KEY_IMAGE = "data";
	private static final String AVATAR_PIC = "avatar.jpg";

	private Context mContext;
	private EditText mNickNameEdit;
	private ImageView mClearImage;
	private ImageView mCloseImage;
	private ImageView mAvatarImage;
	private TextView mModifyPwdText;
	private TextView mLogoutText;
	private Button mSureBtn;
	private UserModelInterface mUserInterface;
	private String picturePath;
	private User mUser;
	// 是否是注册成功后进入的
	protected boolean isFromRegister = false;
	private Animation shakeAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mUserInterface = UserModelInterface.getInstance(this);
		picturePath = Environment.getExternalStorageDirectory().getPath() + "/"
				+ AVATAR_PIC;
		if (getIntent() != null && getIntent().getExtras() != null) {
			isFromRegister = getIntent().getExtras().getBoolean(
					"FROM_REGISTER", false);
		}
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
		mClearImage = (ImageView) findViewById(R.id.userinfo_clear);
		mCloseImage = (ImageView) findViewById(R.id.userinfo_img_close);
		mAvatarImage = (ImageView) findViewById(R.id.userinfo_avatar);
		mModifyPwdText = (TextView) findViewById(R.id.userinfo_modify_pwd_btn);
		mLogoutText = (TextView) findViewById(R.id.userinfo_logout_btn);
		mSureBtn = (Button) findViewById(R.id.userinfo_complete);

		mClearImage.setOnClickListener(this);
		mCloseImage.setOnClickListener(this);
		mAvatarImage.setOnClickListener(this);
		mModifyPwdText.setOnClickListener(this);
		mLogoutText.setOnClickListener(this);
		mSureBtn.setOnClickListener(this);

		if (isFromRegister) {
			mModifyPwdText.setVisibility(View.GONE);
			mLogoutText.setVisibility(View.GONE);
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

		mAvatarImage.setImageBitmap(UserTools
				.transforCircleBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.avatar_placeholder)));

		User user = UserDataHelper.getUserLoginInfo(this);
		if (user != null) {
			getUserInfo(user.getUid(), user.getToken());
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
				if (UserTools.checkString(nick, mNickNameEdit, shakeAnim))
					doAfterSetup(nick);
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

	protected void setDataForWidget(User user) {
		if (mNickNameEdit != null)
			mNickNameEdit.setText(user.getNickName());
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
	public void doLoginOut() {
		showLoadingDialog(true);
		if (mUser != null) {
			mUserInterface.loginOut(mUser.getUid(), mUser.getToken(),
					new RequestListener() {

						@Override
						public void onSuccess(Entry entry) {
							showLoadingDialog(false);
							afterLoginOut();
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

	protected void afterLoginOut() {
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
		if (isFromRegister)
			finish();
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
		showLoadingDialog(true);
		mUserInterface.uploadAvatar(imagePath, new RequestListener() {

			@Override
			public void onSuccess(Entry entry) {
				showLoadingDialog(false);
				UploadAvatarResult result = (UploadAvatarResult) entry;
				if (!TextUtils.isEmpty(result.getImagePath())
						&& !TextUtils.isEmpty(result.getAvatarPath()))
					modifyUserInfo(mUser, result.getImagePath(),
							result.getAvatarPath(), false);
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
	}

	/**
	 * 成功设置昵称
	 */
	protected void afterSetNickName() {
		if (isFromRegister)
			finish();
	}

	/**
	 * 设置头像
	 * 
	 * @param bitmap
	 */
	protected void setBitmapForAvatar(Bitmap bitmap) {
		if (mAvatarImage != null)
			mAvatarImage.setImageBitmap(UserTools.transforCircleBitmap(bitmap));
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
					if (bitmap != null) {
						uploadAvatar(picturePath);
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
}
