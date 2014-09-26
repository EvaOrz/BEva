package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediausermodel.widget.MyHomeView;

/**
 * 我的首页
 * 
 * @author user
 * 
 */
public class MyHomePageActivity extends SlateBaseActivity {
	private MyHomeView homeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		homeView = new MyHomeView(this);
		setContentView(homeView.fetchView());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			homeView.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public String getActivityName() {
		return MyHomePageActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}
