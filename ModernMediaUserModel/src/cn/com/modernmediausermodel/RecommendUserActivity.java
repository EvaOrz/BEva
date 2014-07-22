package cn.com.modernmediausermodel;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.widget.RecommendUserView;

/**
 * 推荐关注、粉丝、好友页面
 * 
 * @author user
 * 
 */
public class RecommendUserActivity extends BaseActivity implements
		OnClickListener {
	private int pageType; // 页面类型
	private User mUser;// 从此用户获取信息(非登录用户)
	private RecommendUserView userView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDataFromBundle();
		init();
	}

	private void initDataFromBundle() {
		// 取得上个页面传来的用户信息
		if (getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();
			this.pageType = bundle.getInt(RecommendUserView.KEY_PAGE_TYPE);
			if (bundle.getSerializable(UserPageTransfer.USER_KEY) instanceof User) {
				mUser = (User) bundle
						.getSerializable(UserPageTransfer.USER_KEY);
			}
		}

		// 默认设置USER为当前登录用户
		if (mUser == null) {
			mUser = UserDataHelper.getUserLoginInfo(this);
		}
	}

	private void init() {
		if (mUser == null)
			return;
		userView = new RecommendUserView(this, pageType, mUser);
		setContentView(userView.fetchView());
		userView.getBackBtn().setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_back) {
			doBack();
		}
	}

	/**
	 * 返回
	 */
	private void doBack() {
		if (userView.getAdapter().isHasModify() && mUser != null
				&& mUser.getUid().equals(UserTools.getUid(this)))
			setResult(RESULT_OK);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public String getActivityName() {
		return RecommendUserActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void reLoadData() {
	}
}
