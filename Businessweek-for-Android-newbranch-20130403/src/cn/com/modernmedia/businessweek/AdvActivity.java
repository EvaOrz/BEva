package cn.com.modernmedia.businessweek;

import android.app.Activity;
import cn.com.modernmedia.CommonAdvActivity;

/**
 * 广告页面
 * 
 * @author ZhuQiao
 * 
 */
public class AdvActivity extends CommonAdvActivity {
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

	@Override
	protected Class<?> getMainActivity() {
		return MainActivity.class;
	}


}
