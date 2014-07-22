package cn.com.modernmediausermodel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.TimelineDb;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 我的首页
 * 
 * @author user
 * 
 */
public class MyHomePageActivity extends BaseCardListActivity implements
		OnClickListener {
	private TextView textView, tipText;
	private ImageView writeCard, back;
	private UserOperateController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);
		init();
	}

	private void init() {
		textView = (TextView) findViewById(R.id.square_bar_title);
		textView.setText(R.string.my_homepage);
		writeCard = (ImageView) findViewById(R.id.square_write_card);
		back = (ImageView) findViewById(R.id.square_back);
		listView = (CheckFooterListView) findViewById(R.id.square_list_view);
		tipText = (TextView) findViewById(R.id.square_no_tip);

		writeCard.setOnClickListener(this);
		back.setOnClickListener(this);
		controller = UserOperateController.getInstance(this);

		initListView(false);
		getCardList("0", false, false);
	}

	@Override
	protected void getCardList(final String timelineId,
			final boolean isGetMore, final boolean isPull) {
		String uid = getUId();
		if (TextUtils.isEmpty(uid)) {
			return;
		}
		super.getCardList(timelineId, isGetMore, isPull);
		controller.getUserTimeLine(uid, timelineId, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterGetCardList(entry, timelineId, isGetMore, isPull);
					}
				});
	}

	protected Card getCardFromDb(String timeLine) {
		return TimelineDb.getInstance(this).getCard(timeLine);
	}

	@Override
	protected void setTipVisibility(boolean show) {
		super.setTipVisibility(show);
		tipText.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public String getActivityName() {
		return MyHomePageActivity.class.getName();
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
			if (requestCode == UserPageTransfer.AFTER_WRITE_CARD
					|| requestCode == CardDetailActivity.REQUEST_CODE_SELF) {
				// TODO 从写笔记页或者笔记详情页返回
				getCardList("0", false, false);
			}
		}
	}
}
