package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.widget.ImageView;
import cn.com.modernmedia.CommonSplashActivity;
import cn.com.modernmedia.util.ConstData;

/**
 * 应用启动页
 * 
 * @author ZhuQiao
 * 
 */
public class SplashScreenActivity extends CommonSplashActivity {
	@Override
	public void reLoadData() {
	}

	@Override
	protected void setContentViewById() {
		setContentView(R.layout.splash_screen);
		findViewById(R.id.splash_view).setBackgroundColor(Color.BLACK);
		if (MyApplication.language.equals(MyApplication.ZH_TW)) {
			((ImageView) findViewById(R.id.splash_image))
					.setImageResource(R.drawable.splash_traditional);
		}
	}

	/**
	 * 跳转至首页
	 */
	@Override
	protected void gotoMainActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(SplashScreenActivity.this,
						MainActivity.class);
				intent.putExtra("FROM_ACTIVITY", "SPLASH");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out, R.anim.hold);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	}

	/**
	 * 跳转至文章页
	 */
	@Override
	protected void gotoAdvActivity() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(SplashScreenActivity.this,
						AdvActivity.class);
				intent.putExtra("FROM_ACTIVITY", "SPLASH");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.alpha_out, R.anim.hold);
			}
		}, ConstData.SPLASH_DELAY_TIME);
	}

	@Override
	public String getActivityName() {
		return SplashScreenActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}
