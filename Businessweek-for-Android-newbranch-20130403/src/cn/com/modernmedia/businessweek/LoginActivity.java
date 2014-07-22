package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmediausermodel.DefaultLoginActivity;
import cn.com.modernmediausermodel.model.User;

/**
 * 登录页
 * 
 * @author ZhuQiao
 * 
 */
public class LoginActivity extends DefaultLoginActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(-1);
		init();
	}

	private void init() {
		((TextView) findViewById(R.id.login_desc))
				.setText(R.string.login_title_text);
		((ImageView) findViewById(R.id.login_img))
				.setImageResource(R.drawable.img_ad_iphone);
	}

	@Override
	protected void afterLogin(User user) {
		super.afterLogin(user);
	}

	@Override
	protected void afterRegister(User user) {
		super.afterRegister(user);
		Intent intent = new Intent(this, UserInfoActivity.class);
		intent.putExtra("FROM_REGISTER", true);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.zoom_out);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.right_out);
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
