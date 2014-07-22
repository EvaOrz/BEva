package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediausermodel.DefaultModifyPwdActivity;
import cn.com.modernmediausermodel.DefaultUserInfoActivity;

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
		if (actionFrom != 0)
			((TextView) findViewById(R.id.userinfo_desc))
					.setText(R.string.set_userinfo_desc);
	}

	@Override
	protected void gotoModifyPwdActivity() {
		Intent intent = new Intent(this, DefaultModifyPwdActivity.class);
		startActivity(intent);
	}

	@Override
	protected void gotoLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		super.gotoLoginActivity();
	}

	@Override
	public String getActivityName() {
		return UserInfoActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void reLoadData() {
	}
}
