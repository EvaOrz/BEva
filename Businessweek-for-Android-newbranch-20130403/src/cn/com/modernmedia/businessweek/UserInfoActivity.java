package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.listener.ImageDownloadStateListener;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediausermodel.DefaultUserInfoActivity;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 用户信息页
 * 
 * @author ZhuQiao
 * 
 */
public class UserInfoActivity extends DefaultUserInfoActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPicturePath(Environment.getExternalStorageDirectory().getPath()
				+ "/" + ConstData.getAppName() + "/");
		setContentView(-1);
		init();
	}

	private void init() {
		((ImageView) findViewById(R.id.userinfo_img))
				.setImageResource(R.drawable.img_ad_iphone);
		if (isFromRegister)
			((TextView) findViewById(R.id.userinfo_desc))
					.setText(R.string.set_userinfo_desc);
		User user = UserDataHelper.getUserLoginInfo(this);
		if (user != null) {
			afterFetchPicture(user,
					UserDataHelper.getAvatarUrl(this, user.getUserName()));
		}
	}

	@Override
	protected void afterFetchPicture(User user, String picUrl) {
		if (user != null && !TextUtils.isEmpty(picUrl)) {
			MyApplication.getImageDownloader().download(picUrl,
					new ImageDownloadStateListener() {

						@Override
						public void loading() {
						}

						@Override
						public void loadOk(Bitmap bitmap) {
							if (bitmap != null) {
								setBitmapForAvatar(bitmap);
							}
						}

						@Override
						public void loadError() {
						}
					});
		}
	}

	@Override
	protected void gotoModifyPwdActivity() {
		Intent intent = new Intent(this, ModifyPasswordActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.zoom_out);
	}

	@Override
	protected void gotoLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		super.gotoLoginActivity();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.right_out);
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
