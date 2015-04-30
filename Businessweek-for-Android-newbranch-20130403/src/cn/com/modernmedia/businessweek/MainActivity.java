package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.os.Bundle;
import cn.com.modernmedia.views.ViewsMainActivity;

/**
 * 首页
 * 
 * @author ZhuQiao
 * 
 */
public class MainActivity extends ViewsMainActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 给scrollview设置正在下拉刷新
	 * 
	 * @param isPulling
	 */
	public void setPulling(boolean isPulling) {
		scrollView.setPassToUp(isPulling);
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
