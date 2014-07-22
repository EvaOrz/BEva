package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.view.View;
import cn.com.modernmedia.views.ArticleActivity;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmediausermodel.widget.UserCenterView;

/**
 * 首页
 * 
 * @author ZhuQiao
 * 
 */
public class MainActivity extends ViewsMainActivity {
	private UserCenterView userCenterView;

	@Override
	protected void init() {
		super.init();
		scrollView.setIntercept(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 用户中心页数据变化时，刷新页面
		if (userCenterView != null) {
			userCenterView.reLoad();
		}
	}

	@Override
	protected Class<?> getArticleActivity() {
		return ArticleActivity.class;
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

	@Override
	protected View fetchRightView() {
		userCenterView = new UserCenterView(this);
		return userCenterView;
	}
	
}
