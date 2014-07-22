package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.RecommendCardDb;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 广场页面
 * 
 * @author user
 * 
 */
public class SquareActivity extends BaseCardListActivity implements
		OnClickListener {
	public static final int TO_LOGIN = 100;// 从登录回来刷新页面

	private ImageView writeCard, back;
	private UserOperateController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);
		init();
	}

	private void init() {
		writeCard = (ImageView) findViewById(R.id.square_write_card);
		back = (ImageView) findViewById(R.id.square_back);
		listView = (CheckFooterListView) findViewById(R.id.square_list_view);
		writeCard.setOnClickListener(this);
		back.setOnClickListener(this);
		controller = UserOperateController.getInstance(this);

		initListView(false);
		getCardList("0", false, false);
	}

	@Override
	protected void getCardList(final String timelineId,
			final boolean isGetMore, final boolean isPull) {
		super.getCardList(timelineId, isGetMore, isPull);
		controller.getRecommendCard(timelineId, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterGetCardList(entry, timelineId, isGetMore, isPull);
					}
				});
	}

	@Override
	protected Card getCardFromDb(String timeLine) {
		return RecommendCardDb.getInstance(this).getCard(timeLine);
	}

	/**
	 * 获取当前用户ID，可能刚进页面的时候是未登录状态，所以时时获取一下
	 * 
	 * @return
	 */
	public String checkUid() {
		User user = UserDataHelper.getUserLoginInfo(this);
		if (user == null) {
			UserPageTransfer.gotoLoginActivityRequest(this, TO_LOGIN);
			return "";
		}
		return user.getUid();
	}

	@Override
	public String getActivityName() {
		return SquareActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.square_write_card) {
			UserPageTransfer.gotoWriteCardActivity(this, false);
		} else if (v.getId() == R.id.square_back) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CardDetailActivity.REQUEST_CODE_SELF) {
				// TODO 从笔记详情页返回时更新数据
			} else if (requestCode == TO_LOGIN) {
				// TODO 登录回来,刷新页面
			} else if (requestCode == UserPageTransfer.TO_LOGIN_BY_WRITE) {
				// TODO 从登录回来刷新页面并跳转至写卡片页
				UserPageTransfer.gotoWriteCardActivity(this, false);
			} else if (requestCode == UserPageTransfer.AFTER_WRITE_CARD) {
				// 填写卡片回来
			}
			getCardList("0", false, false);
		}
	}
}
