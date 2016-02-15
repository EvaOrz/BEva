package cn.com.modernmediausermodel.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmedia.widget.CheckFooterListView.FooterCallBack;
import cn.com.modernmedia.widget.PullToRefreshListView.OnRefreshListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.adapter.UserCardListAdapter;
import cn.com.modernmediausermodel.db.UserInfoDb;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.Users;
import cn.com.modernmediausermodel.util.UserConstData;

/**
 * 卡片列表页基础类
 * 
 * @author user
 * 
 */
public abstract class BaseCardListView {
	private Context mContext;
	private CheckFooterListView listView;
	protected UserCardListAdapter adapter;
	private UserInfoDb userInfoDb;
	private Card mCard = new Card();

	public BaseCardListView(Context context, CheckFooterListView listView) {
		mContext = context;
		this.listView = listView;
	}

	public void initListView(boolean isForUser) {
		initListView(isForUser, null);
	}

	/**
	 * 初始化listview
	 * 
	 * @param isForUser
	 *            是否用户资料页
	 * @param head
	 *            headview，后加
	 */
	public void initListView(boolean isForUser, View head) {
		listView.enableAutoFetch(true, false);
		if (head != null)
			listView.addHeaderView(head);
		listView.setHeadRes(false, Color.WHITE);
		userInfoDb = UserInfoDb.getInstance(mContext);
		adapter = new UserCardListAdapter(mContext);
		adapter.setIsForUser(isForUser);
		listView.setAdapter(adapter);
		loadCacheFirst();
		listView.setCallBack(new FooterCallBack() {

			@Override
			public void onLoad() {
				if (adapter.getCount() < UserConstData.MAX_CARD_ITEM_COUNT
						|| mCard == null) {
					return;
				}
				List<CardItem> list = mCard.getCardItemList();
				if (ParseUtil.listNotNull(list)) {
					String timelineId = list.get(list.size() - 1)
							.getTimeLineId();
					getCardList(timelineId, true, false);
				}
			}
		});
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefreshDone() {
			}

			@Override
			public void onRefreshComplete() {
			}

			@Override
			public void onRefresh() {
				getCardList("0", false, true);
			}

			@Override
			public void onPulling() {
			}

			@Override
			public void onLoad() {
			}
		});
	}

	// TODO 先加载缓存数据
	private void loadCacheFirst() {
		Card card = getCardFromDb("0");
		if (ParseUtil.listNotNull(card.getCardItemList())) {
			mCard = card;
			Users users = userInfoDb.getUsersInfo();
			mCard.setUserInfoMap(users.getUserInfoMap());
			adapter.setCard(mCard);
			adapter.setData(card.getCardItemList());
		}
	}

	/**
	 * 获取卡片列表
	 * 
	 * @param timelineId
	 * @param isGetMore
	 *            是否加载更多
	 * @param isPull
	 *            是否下拉刷新
	 */
	public void getCardList(final String timelineId, final boolean isGetMore,
			boolean isPull) {
		setTipVisibility(false);
		if (!isGetMore && !isPull) {
			Tools.showLoading(mContext, true);
		}
	}

	/**
	 * 从服务器获取完卡片列表之后
	 * 
	 * @param entry
	 * @param timeLine
	 * @param isGetMore
	 */
	public void afterGetCardList(Entry entry, String timeLine,
			boolean isGetMore, boolean isPull) {
		Card card;
		if (entry instanceof Card) {
			card = (Card) entry;
			List<CardItem> list = card.getCardItemList();
			if (!ParseUtil.listNotNull(list)) {
				if (!isGetMore && !isPull) {
					adapter.clear();
				}
				if (isGetMore)
					listView.loadOk(false);
				else
					Tools.showLoading(mContext, false);
				setTipVisibility(adapter.getCount() == 0);
				return;
			}
			if (isGetMore) {
				mCard.getCardItemList().addAll(list);
				mCard.getUserInfoMap().putAll(card.getUserInfoMap());
			} else {
				mCard = card;
			}
			if (isPull)
				listView.onRefreshComplete(false, -1);
			else if (!isGetMore)
				Tools.showLoading(mContext, false);

			adapter.clear();
			adapter.setCard(mCard);
			adapter.setData(mCard.getCardItemList());
			listView.loadOk(true);
			if (mCard.getCardItemList().size() < UserConstData.MAX_CARD_ITEM_COUNT) {
				listView.removeFooter();
			} else {
				listView.showFooter();
			}

		} else if (isGetMore) {
			listView.onLoadError();
		} else {
			Tools.showLoading(mContext, false);
			if (isPull)
				listView.onRefreshComplete(false, -1);
		}
	}

	/**
	 * 从数据库获取card列表
	 * 
	 * @param timeLine
	 * @return
	 */
	public abstract Card getCardFromDb(String timeLine);

	/**
	 * 当没有内容时,设置提示语
	 * 
	 * @param show
	 */
	public abstract void setTipVisibility(boolean show);

	protected String getUId() {
		return Tools.getUid(mContext);
	}
}
