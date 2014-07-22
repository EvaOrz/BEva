package cn.com.modernmediausermodel.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.CardDetailActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.RecommendCardDb;
import cn.com.modernmediausermodel.listener.CardViewListener;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;

/**
 * 广场View
 * 
 * @author user
 * 
 */
public class SquareView implements OnClickListener, CardViewListener {
	public static final int TO_LOGIN = 100;// 从登录回来刷新页面

	private Context mContext;
	private View view, titleBar;
	private CheckFooterListView listView;
	private UserOperateController controller;
	private BaseCardListView cardListHelp;

	public SquareView(Context context) {
		mContext = context;
		initWidget();
	}

	private void initWidget() {
		view = LayoutInflater.from(mContext).inflate(R.layout.activity_square,
				null);
		titleBar = view.findViewById(R.id.square_bar_layout);
		listView = (CheckFooterListView) view
				.findViewById(R.id.square_list_view);

		view.findViewById(R.id.square_write_card).setOnClickListener(this);
		view.findViewById(R.id.square_back).setOnClickListener(this);
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
			}

			@Override
			public Card getCardFromDb(String timeLine) {
				return RecommendCardDb.getInstance(mContext).getCard(timeLine);
			}

			@Override
			public void getCardList(String timelineId, boolean isGetMore,
					boolean isPull) {
				super.getCardList(timelineId, isGetMore, isPull);
				myGetCardList(timelineId, isGetMore, isPull);
			}

		};
		cardListHelp.initListView(false);
		cardListHelp.getCardList("0", false, false);
	}

	private void myGetCardList(final String timelineId,
			final boolean isGetMore, final boolean isPull) {
		controller.getRecommendCard(timelineId, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						cardListHelp.afterGetCardList(entry, timelineId,
								isGetMore, isPull);
					}
				});
	}

	/**
	 * 获取当前用户ID，可能刚进页面的时候是未登录状态，所以时时获取一下
	 * 
	 * @return
	 */
	public String checkUid() {
		User user = UserDataHelper.getUserLoginInfo(mContext);
		if (user == null) {
			UserPageTransfer.gotoLoginActivityRequest(mContext, TO_LOGIN);
			return "";
		}
		return user.getUid();
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
		if (requestCode == CardDetailActivity.REQUEST_CODE_SELF) {
			// TODO 从笔记详情页返回时更新数据
		} else if (requestCode == TO_LOGIN) {
			// TODO 登录回来,刷新页面
		} else if (requestCode == UserPageTransfer.TO_LOGIN_BY_WRITE) {
			// TODO 从登录回来刷新页面并跳转至写卡片页
			UserPageTransfer.gotoWriteCardActivity(mContext, false);
		} else if (requestCode == UserPageTransfer.AFTER_WRITE_CARD) {
			// 填写卡片回来
		}
		cardListHelp.getCardList("0", false, false);
	}

}
