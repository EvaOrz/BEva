package cn.com.modernmediausermodel.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.CardDetailActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.TimelineDb;
import cn.com.modernmediausermodel.listener.CardViewListener;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 我的首页
 * 
 * @author user
 * 
 */
public class MyHomeView implements OnClickListener, CardViewListener {
	private Context mContext;
	private View view, titleBar;
	private CheckFooterListView listView;
	private UserOperateController controller;
	private BaseCardListView cardListHelp;
	private TextView tipText;

	public MyHomeView(Context context) {
		mContext = context;
		initWidget();
	}

	private void initWidget() {
		view = LayoutInflater.from(mContext).inflate(R.layout.activity_square,
				null);
		titleBar = view.findViewById(R.id.square_bar_layout);
		tipText = (TextView) view.findViewById(R.id.square_no_tip);
		listView = (CheckFooterListView) view
				.findViewById(R.id.square_list_view);

		view.findViewById(R.id.square_write_card).setOnClickListener(this);
		view.findViewById(R.id.square_back).setOnClickListener(this);
		tipText.setText(R.string.user_no_card);
		((TextView) view.findViewById(R.id.square_bar_title))
				.setText(R.string.my_homepage);
		init();
	}

	/**
	 * 是否显示titleBar
	 * 
	 * @param show
	 */
	@Override
	public void showTitleBar(boolean show) {
		titleBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public View fetchView() {
		return view;
	}
	
	private void init() {
		controller = UserOperateController.getInstance(mContext);

		cardListHelp = new BaseCardListView(mContext, listView) {

			@Override
			public void setTipVisibility(boolean show) {
				tipText.setVisibility(show ? View.VISIBLE : View.GONE);
			}

			@Override
			public Card getCardFromDb(String timeLine) {
				return TimelineDb.getInstance(mContext).getCard(timeLine);
			}

			@Override
			public void getCardList(String timelineId, boolean isGetMore,
					boolean isPull) {
				String uid = UserTools.getUid(mContext);
				if (TextUtils.isEmpty(uid)
						|| uid.equals(ConstData.UN_UPLOAD_UID)) {
					return;
				}
				super.getCardList(timelineId, isGetMore, isPull);
				myGetCardList(uid, timelineId, isGetMore, isPull);
			}

		};
		cardListHelp.initListView(false);
		cardListHelp.getCardList("0", false, false);
	}

	private void myGetCardList(String uid, final String timelineId,
			final boolean isGetMore, final boolean isPull) {
		controller.getUserTimeLine(uid, timelineId, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						cardListHelp.afterGetCardList(entry, timelineId,
								isGetMore, isPull);
					}
				});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.square_write_card) {
			UserPageTransfer.gotoWriteCardActivity(mContext, false);
		} else if (v.getId() == R.id.square_back) {
			((Activity) mContext).finish();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == UserPageTransfer.AFTER_WRITE_CARD
				|| requestCode == CardDetailActivity.REQUEST_CODE_SELF) {
			// TODO 从写笔记页或者笔记详情页返回
			cardListHelp.getCardList("0", false, false);
		}
	}

}
