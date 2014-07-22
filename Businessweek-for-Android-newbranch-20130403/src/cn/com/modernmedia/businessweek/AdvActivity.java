package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import cn.com.modernmedia.CommonAdvActivity;
import cn.com.modernmedia.util.ConstData;

/**
 * 广告页面
 * 
 * @author ZhuQiao
 * 
 */
public class AdvActivity extends CommonAdvActivity {
	/**
	 * 跳转至主页
	 */
	protected void gotoMainActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(AdvActivity.this, MainActivity.class);
				intent.putExtra("FROM_ACTIVITY", "SPLASH");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out, R.anim.hold);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	}

	@Override
	public void reLoadData() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public String getActivityName() {
		return AdvActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
