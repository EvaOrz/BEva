package cn.com.modernmediausermodel.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.CardDetailActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.UserCardInfoDb;
import cn.com.modernmediausermodel.listener.CardViewListener;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.UserCardInfoList;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 用户信息及卡片列表类（卡片包括自己创建及收藏的）
 * 
 * @author user
 * 
 */
public class UserCardView implements OnClickListener, CardViewListener {
	public static final String KEY_USER = "USER";
	public static final int REQUEST_CODE_USER_LIST = 102; // 请求朋友列表、粉丝列表

	private Context mContext;
	private View view, titleBar;
	private CheckFooterListView listView;
	private Button follow;
	private ImageView avatar;
	private TextView userText, title, cardNumText, followNumText, fansNumText,
			tipText;
	private View cardLayout, followLayout, fansLayout;
	private User user; // 用户
	private UserOperateController controller;
	private boolean isFollowed = false; // 该用户是否被当前登录用户关注(用户查看自己资料时不使用)
	private BaseCardListView cardListHelp;

	public UserCardView(Context context, User user) {
		mContext = context;
		this.user = user;
		initWidget();
	}

	private void initWidget() {
		view = LayoutInflater.from(mContext).inflate(
				R.layout.activity_user_card_info, null);
		titleBar = view.findViewById(R.id.bar_layout);
		follow = (Button) view.findViewById(R.id.button_follow);
		title = (TextView) view.findViewById(R.id.card_info_title);
		tipText = (TextView) view.findViewById(R.id.user_card_info_no_tip);
		listView = (CheckFooterListView) view.findViewById(R.id.card_list_view);
		// head
		View headView = LayoutInflater.from(mContext).inflate(
				R.layout.activity_card_info_head, null);
		avatar = (ImageView) headView.findViewById(R.id.user_card_info_avatar);
		userText = (TextView) headView
				.findViewById(R.id.user_card_info_user_name);
		cardNumText = (TextView) headView.findViewById(R.id.card_number);
		followNumText = (TextView) headView.findViewById(R.id.follow_number);
		fansNumText = (TextView) headView.findViewById(R.id.fan_number);
		cardLayout = headView.findViewById(R.id.card_info_layout_card);
		followLayout = headView.findViewById(R.id.card_info_layout_follow);
		fansLayout = headView.findViewById(R.id.card_info_layout_fans);

		view.findViewById(R.id.button_back).setOnClickListener(this);
		follow.setOnClickListener(this);
		init(headView);
	}

	private void init(View headView) {
		controller = UserOperateController.getInstance(mContext);
		// 设置用户头像
		UserTools.setAvatar(mContext, user.getAvatar(), avatar);
		// 设置用户昵称
		userText.setText(user.getNickName());

		cardListHelp = new BaseCardListView(mContext, listView) {

			@Override
			public void setTipVisibility(boolean show) {
				tipText.setVisibility(show ? View.VISIBLE : View.GONE);
			}

			@Override
			public Card getCardFromDb(String timeLine) {
				return UserCardInfoDb.getInstance(mContext).getCard(timeLine,
						user.getUid());
			}

			@Override
			public void getCardList(String timelineId, boolean isGetMore,
					boolean isPull) {
				super.getCardList(timelineId, isGetMore, isPull);
				myGetCardList(timelineId, isGetMore, isPull);
			}

			@Override
			public void afterGetCardList(Entry entry, String timeLine,
					boolean isGetMore, boolean isPull) {
				super.afterGetCardList(entry, timeLine, isGetMore, isPull);
				boolean isPullOrDown = isPull || isGetMore;// 下拉刷新或者加载更多时，同时更新用户卡片信息
				if (isPullOrDown && entry instanceof Card) {
					List<CardItem> list = ((Card) entry).getCardItemList();
					if (ParseUtil.listNotNull(list)) {
						getUserCardInfo(true);
					}
				}

			}

		};
		cardListHelp.initListView(true, headView);
		// 显示登录用户自己时，隐藏关注按钮
		if (Tools.getUid(mContext).equals(user.getUid())) {
			follow.setVisibility(View.GONE);
			avatar.setOnClickListener(this);
			cardLayout.setOnClickListener(this);
			title.setText(R.string.me);
		} else {
			title.setText(user.getNickName());
		}
		followLayout.setOnClickListener(this);
		fansLayout.setOnClickListener(this);

		// 设置提示语
		if (!user.getUid().equals(Tools.getUid(mContext))) {
			tipText.setText(user.getNickName()
					+ mContext.getString(R.string.user_no_card).substring(1));
		}
		getUserCardInfo(false);
	}

	/**
	 * 获得用户的卡片信息
	 * 
	 * @param isRefresh
	 *            进入页面或者从其他页面返回刷新,true只刷新头部信息；else刷新全部
	 */
	private void getUserCardInfo(final boolean isRefresh) {
		if (!isRefresh)
			Tools.showLoading(mContext, true);
		String customerUid = "";
		String uid = Tools.getUid(mContext);
		if (uid != null && !uid.equals(user.getUid())) { // 登录用户自己
			customerUid = uid;
		}
		controller.getUserCardInfo(user.getUid(), customerUid,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof UserCardInfo) {
							UserCardInfo cardInfo = (UserCardInfo) entry;
							cardNumText.setText(UserTools.changeNum(cardInfo
									.getCardNum()));
							followNumText.setText(UserTools.changeNum(cardInfo
									.getFollowNum()));
							fansNumText.setText(UserTools.changeNum(cardInfo
									.getFansNum()));

							if (cardInfo.getIsFollowed() == 1) {
								isFollowed = true;
							} else {
								isFollowed = false;
							}
							setFollowBtnText();
							if (!isRefresh) {
								cardListHelp.getCardList("0", false, false);
							}
						} else {
							Tools.showLoading(mContext, false);
						}
					}
				});
	}

	/**
	 * 获得用户的卡片
	 */
	private void myGetCardList(final String timelineId,
			final boolean isGetMore, final boolean isPull) {
		controller.getUserCard(user.getUid(), timelineId, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof Card) {
							Card card = (Card) entry;
							if (card.getError().getNo() == 0) {
								cardListHelp.afterGetCardList(entry,
										timelineId, isGetMore, isPull);
							} else {
								Tools.showToast(mContext, card.getError()
										.getDesc());
								listView.onLoadError();
							}
						} else {
							cardListHelp.afterGetCardList(entry, timelineId,
									isGetMore, isPull);
						}
					}
				});
	}

	/**
	 * 关注用户
	 */
	private void addFollow(List<UserCardInfo> users) {
		Tools.showLoading(mContext, true);
		controller.addFollow(Tools.getUid(mContext), users, true,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						Tools.showLoading(mContext, false);
						if (entry instanceof ErrorMsg) {
							ErrorMsg error = (ErrorMsg) entry;
							if (error.getNo() == 0) {
								isFollowed = true;
								setFollowBtnText();
							}
						}
					}
				});
	}

	/**
	 * 取消关注用户
	 */
	private void deleteFollow(List<UserCardInfo> users) {
		Tools.showLoading(mContext, true);
		controller.deleteFollow(Tools.getUid(mContext), users, true,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						Tools.showLoading(mContext, false);
						if (entry instanceof ErrorMsg) {
							ErrorMsg error = (ErrorMsg) entry;
							if (error.getNo() == 0) {
								isFollowed = false;
								setFollowBtnText();
							}
						}
					}
				});
	}

	private void setFollowBtnText() {
		if (isFollowed) {
			follow.setText(R.string.followed);
		} else {
			follow.setText(R.string.follow);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_USER_LIST) {
			// TODO 从朋友、粉丝页面返回，当是自己的页面并且朋友、粉丝有变化，刷新
			getUserCardInfo(true);
		} else if (requestCode == UserPageTransfer.AFTER_WRITE_CARD
				|| requestCode == CardDetailActivity.REQUEST_CODE_SELF) {
			// TODO 从笔记详情页返回，可能文章状态有变化，刷新全部
			getUserCardInfo(false);
		}
	}

	@Override
	public void showTitleBar(boolean show) {
		titleBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public View fetchView() {
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_back) {
			((Activity) mContext).finish();
		} else if (v.getId() == R.id.button_follow) {
			if (follow.getVisibility() == View.VISIBLE) {
				UserCardInfo userCardInfo = new UserCardInfo();
				userCardInfo.setUid(user.getUid());
				UserCardInfoList list = new UserCardInfoList();
				list.getList().add(userCardInfo);
				if (isFollowed) {
					deleteFollow(list.getList());
				} else {
					addFollow(list.getList());
				}
			}
		} else if (v.getId() == R.id.user_card_info_avatar) {
			if (user.getUid().equals(Tools.getUid(mContext))) {
				UserPageTransfer.gotoUserInfoActivity(mContext, 0, null, null,
						0);
			}
		} else if (v.getId() == R.id.card_info_layout_follow) {
			UserPageTransfer.gotoUserListActivity(mContext, user,
					RecommendUserView.PAGE_FRIEND, false);
		} else if (v.getId() == R.id.card_info_layout_fans) {
			UserPageTransfer.gotoUserListActivity(mContext, user,
					RecommendUserView.PAGE_FANS, false);
		}
	}
}
