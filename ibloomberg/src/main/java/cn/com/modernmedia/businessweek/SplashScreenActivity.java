package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import cn.com.modernmedia.CommonSplashActivity;
import cn.com.modernmedia.util.DESCoder;

/**
 * 应用启动页
 * 
 * @author ZhuQiao
 * 
 */
public class SplashScreenActivity extends CommonSplashActivity {

	@Override
	protected void setContentViewById() {
		setContentView(R.layout.splash_screen);
		findViewById(R.id.splash_view).setBackgroundColor(Color.BLACK);
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
