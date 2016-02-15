package cn.com.modernmedia.businessweek;

import android.app.Activity;
import cn.com.modernmedia.util.PushTest;
import cn.com.modernmedia.views.ViewsMainActivity;

/**
 * 首页
 * 
 * @author ZhuQiao
 * 
 */
public class MainActivity extends ViewsMainActivity {

	/**
	 * 给scrollview设置正在下拉刷新
	 * 
	 * @param isPulling
	 */
	public void setPulling(boolean isPulling) {
		scrollView.setPassToUp(isPulling);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public String getActivityName() {
		return MainActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
