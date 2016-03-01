package cn.com.modernmediausermodel;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.util.sina.SinaAPI;
import cn.com.modernmedia.util.sina.SinaAuth;
import cn.com.modernmedia.util.sina.SinaRequestListener;
import cn.com.modernmedia.util.sina.UserModelAuthListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ImgFileManager;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediausermodel.api.BandAccountOperate;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.UploadAvatarResult;
import cn.com.modernmediausermodel.util.FetchPhotoManager;
import cn.com.modernmediausermodel.util.QQLoginUtil;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.util.WeixinLoginUtil;
import cn.com.modernmediausermodel.util.WeixinLoginUtil.WeixinAuthListener;
import cn.com.modernmediausermodel.widget.SignDialog;

import com.alipay.sdk.util.i;
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
	public static final String PASSEORD = "password";

	private static final String KEY_IMAGE = "data";
	private static final String AVATAR_PIC = "avatar.jpg";

	private Context mContext;
	private UserOperateController mController;
	private TextView nickName, signText, emailText, phoneText;// 个人签名、电子邮件、手机号码
	private ImageView avatar, sina, weixin, qq;
	private String picturePath;// 头像
	private User mUser;// 用户信息

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_userinfo);
		mController = UserOperateController.getInstance(mContext);
		picturePath = Environment.getExternalStorageDirectory().getPath() + "/"
				+ AVATAR_PIC;
		mUser = SlateDataHelper.getUserLoginInfo(this);
		initView();
	}

	private void initView() {
		nickName = (TextView) findViewById(R.id.uinfo_nick);
		signText = (TextView) findViewById(R.id.uinfo_sign);
		emailText = (TextView) findViewById(R.id.uinfo_email);
		phoneText = (TextView) findViewById(R.id.uinfo_phone);
		sina = (ImageView) findViewById(R.id.uinfo_btn_sina_login);
		weixin = (ImageView) findViewById(R.id.uinfo_btn_weixin_login);
		qq = (ImageView) findViewById(R.id.uinfo_btn_qq_login);
		avatar = (ImageView) findViewById(R.id.userinfo_avatar);

		sina.setOnClickListener(this);
		weixin.setOnClickListener(this);
		qq.setOnClickListener(this);
		avatar.setOnClickListener(this);
		findViewById(R.id.uinfo_close).setOnClickListener(this);
		findViewById(R.id.uinfo_motify_pwd).setOnClickListener(this);
		findViewById(R.id.uinfo_logout).setOnClickListener(this);
		signText.setOnClickListener(this);
		nickName.setOnClickListener(this);
		emailText.setOnClickListener(this);
		phoneText.setOnClickListener(this);
		handler.sendEmptyMessage(1);
		if (mUser == null) 
			return;
		/**
		 * 获取绑定信息
		 */
		mController.getBandStatus(mUser.getUid(), mUser.getToken(),
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						User u = (User) entry;
						if (u != null) {
							mUser.setBandPhone(u.isBandPhone());
							mUser.setBandEmail(u.isBandEmail());
							mUser.setBandQQ(u.isBandQQ());
							mUser.setBandWeibo(u.isBandWeibo());
							mUser.setBandWeixin(u.isBandWeixin());
							handler.sendEmptyMessage(0);
						}

					}
				});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {// 绑定信息变更
				if (mUser.isBandQQ())
					qq.setImageResource(R.drawable.login_qq);
				if (mUser.isBandWeibo())
					sina.setImageResource(R.drawable.login_sina);
				if (mUser.isBandWeixin())
					sina.setImageResource(R.drawable.login_weixin);
				if (mUser.isBandPhone())
					phoneText.setText(mUser.getPhone());
				else
					phoneText.setText(R.string.band_yet);// 未绑定
				if (mUser.isBandEmail())
					emailText.setText(mUser.getEmail());
				else
					emailText.setText(R.string.band_yet);// 未绑定
			} else if (msg.what == 1) {// 昵称、头像、签名变更
				signText.setText(SlateDataHelper.getDesc(mContext));
				nickName.setText(SlateDataHelper.getNickname(mContext));
				// 设置头像
				UserTools.setAvatar(mContext, mUser, avatar);
			}

		}
	};

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
		if (id == R.id.uinfo_close)
			finish();
		else if (id == R.id.uinfo_motify_pwd) // 修改密码
			UserPageTransfer.gotoModifyPasswordActivity(this);
		else if (id == R.id.uinfo_logout) // 登出
			doLoginOut();
		else if (id == R.id.userinfo_avatar) // 修改头像
			doFecthPicture();
		else if (id == R.id.uinfo_btn_sina_login) { // 新浪绑定
			if (!mUser.isBandWeibo())
				doSinaBand();
		} else if (id == R.id.uinfo_btn_qq_login) { // qq绑定
			if (!mUser.isBandQQ())
				doSinaBand();
		} else if (id == R.id.uinfo_btn_weixin_login) { // 微信绑定
			if (!mUser.isBandWeixin())
				doWeixinBand();
		} else if (id == R.id.uinfo_sign) {// 签名
			new SignDialog(UserInfoActivity.this, 2);
		} else if (id == R.id.uinfo_email) {// 绑定邮箱
			if (!mUser.isBandEmail())
				gotoBandDetail(BandAccountOperate.EMAIL);
		} else if (id == R.id.uinfo_phone) {// 绑定手机号码
			if (!mUser.isBandPhone())
				gotoBandDetail(BandAccountOperate.PHONE);
		} else if (id == R.id.uinfo_nick) {// 修改昵称
			new SignDialog(UserInfoActivity.this, 1);
		}
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
					// mUser.setNickName(object.optString("screen_name")); // 昵称
					// mUser.setUserName(object.optString("name"));
					// mUser.setAvatar(object.optString("profile_image_url"));//
					// 用户头像地址（中图），50×50像素
					// mUser.setToken(sinaAPI.getToken());
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
					mUser = new User();
					mUser.setQqId(QQLoginUtil.getInstance(mContext).getOpenId());
					// mUser.setNickName(object.optString("nickname")); // 昵称
					// mUser.setAvatar(object.optString("figureurl_qq_1"));//
					// 用户头像地址（小图，40×40像素；'figureurl_qq_2'是100*100像素大图）
				}
			}

			@Override
			public void onCancel() {
				showLoadingDialog(false);
			}
		});
	}

	/**
	 * 微博授权
	 */
	private void doSinaBand() {
		// 新浪微博认证
		SinaAuth weiboAuth = new SinaAuth(this);
		if (!weiboAuth.checkIsOAuthed()) {
			weiboAuth.oAuth();
		} else {
			String sinaId = SinaAPI.getInstance(this).getSinaId();
			mUser.setSinaId(sinaId);
			doBand(sinaId, BandAccountOperate.WEIBO);
		}
		weiboAuth.setAuthListener(new UserModelAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess) {
				String sinaId = SinaAPI.getInstance(UserInfoActivity.this)
						.getSinaId();
				mUser.setSinaId(sinaId);
				doBand(sinaId, BandAccountOperate.WEIBO);
			}
		});
	}

	/**
	 * 绑定
	 * 
	 * @param c
	 *            username
	 * @param bindType
	 *            绑定类型
	 */
	protected void doBand(final String c, final int bindType) {
		if (mUser == null)
			return;
		mController.bandAccount(mUser.getUid(), mUser.getToken(), bindType, c,
				null, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry == null || ((ErrorMsg) entry).getNo() == 0) {
							showToast(R.string.band_succeed);
							// 存储绑定信息
							if (bindType == BandAccountOperate.WEIBO)
								mUser.setBandWeibo(true);
							else if (bindType == BandAccountOperate.WEIXIN)
								mUser.setBandWeixin(true);
							handler.sendEmptyMessage(0);
							SlateDataHelper.saveUserLoginInfo(
									UserInfoActivity.this, mUser);

							showLoadingDialog(false);
						}
					}
				});
	}

	/**
	 * 
	 * @param type
	 */
	private void gotoBandDetail(int type) {
		Intent i = new Intent(this, BandDetailActivity.class);
		i.putExtra("band_type", type);
		i.putExtra("band_user", mUser);
		startActivity(i);
	}

	/**
	 * 微信绑定
	 */
	private void doWeixinBand() {
		WeixinLoginUtil weixinloginUtil = WeixinLoginUtil.getInstance(this);
		weixinloginUtil.loginWithWeixin();
		weixinloginUtil.setLoginListener(new WeixinAuthListener() {

			@Override
			public void onCallBack(boolean isSuccess, User user) {
				if (user != null) {
					mUser.setWeixinId(user.getWeixinId());
					doBand(user.getWeixinId(), BandAccountOperate.WEIXIN);
				}
			}
		});
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
			} else if (!TextUtils.isEmpty(mUser.getWeixinId())) {
				UserDataHelper.saveWeinxinLoginedName(mContext,
						mUser.getWeixinId(), "");
			}
		}
		// 清除存储的登录信息
		SlateDataHelper.clearLoginInfo(mContext);
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
			showToast(R.string.msg_avatar_get_failed);// 头像获取失败
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
							modifyUserInfo(mUser, result.getImagePath(),
									result.getAvatarPath());
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
	public void modifyUserInfo(User user, String url, final String avatar_url) {
		if (user == null)
			return;
		showLoadingDialog(true);
		// 只更新头像、昵称信息
		mController.modifyUserInfo(user.getUid(), user.getToken(),
				user.getUserName(), user.getNickName(), url, null,
				user.getDesc(), new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						showLoadingDialog(false);
						if (entry instanceof User) {
							User resUser = (User) entry;
							ErrorMsg error = resUser.getError();
							if (error.getNo() == 0) {
								handler.sendEmptyMessage(1);
							}// 修改失败
							else
								showToast(error.getDesc());
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
