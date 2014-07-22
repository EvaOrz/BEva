package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import cn.com.modernmediausermodel.DefaultLoginActivity;

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
	}

	@Override
	protected void initContentView() {
		setContentView(-1);
		initView();
	}

	private void initView() {
		((TextView) findViewById(R.id.login_desc))
				.setText(R.string.login_title_text);
	}

	@Override
	public String getActivityName() {
		return LoginActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void reLoadData() {
	}

}
