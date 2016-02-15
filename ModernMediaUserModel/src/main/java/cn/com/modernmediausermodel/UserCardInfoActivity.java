package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.com.modernmediaslate.SlateBaseActivity;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediausermodel.widget.UserCardView;

/**
 * 用户信息及卡片列表类（卡片包括自己创建及收藏的）
 * 
 * @author jiancong
 * 
 */
public class UserCardInfoActivity extends SlateBaseActivity {
	private User user; // 用户
	private UserCardView userCardView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDataFromBundle();
		init();
	}

	private void initDataFromBundle() {
		// 取得上个页面传来的用户信息
		if (getIntent().getExtras() != null) {
			this.user = (User) getIntent().getSerializableExtra(
					UserCardView.KEY_USER);
		}
	}

	private void init() {
		if (user == null)
			return;
		userCardView = new UserCardView(this, user);
		setContentView(userCardView.fetchView());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			userCardView.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public String getActivityName() {
		return UserCardInfoActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
