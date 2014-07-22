package cn.com.modernmediausermodel;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.UserCardInfoDb;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.model.Users.UserCardInfo;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 用户信息及卡片列表类（卡片包括自己创建及收藏的）
 * 
 * @author jiancong
 * 
 */
public class UserCardInfoActivity extends BaseCardListActivity implements
		OnClickListener {
	public final static String KEY_USER = "USER";
	public final static int REQUEST_CODE_USER_LIST = 102; // 请求朋友列表、粉丝列表
	private Button follow;
	private ImageView avatar;
	private ImageView back;
	private TextView userText;
	private TextView title, cardNumText, followNumText, fansNumText, tipText;
	private View cardLayout, followLayout, fansLayout;
	private User user; // 用户
	private UserOperateController controller;
	private boolean isFollowed = false; // 该用户是否被当前登录用户关注(用户查看自己资料时不使用)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_card_info);
		initDataFromBundle();
		init();
	}

	private void initDataFromBundle() {
		// 取得上个页面传来的用户信息
		if (getIntent().getExtras() != null) {
			this.user = (User) getIntent().getSerializableExtra(KEY_USER);
		}
	}

	private void init() {
		if (user == null)
			return;
		back = (ImageView) findViewById(R.id.button_back);
		follow = (Button) findViewById(R.id.button_follow);
		title = (TextView) findViewById(R.id.card_info_title);
		tipText = (TextView) findViewById(R.id.user_card_info_no_tip);
		back.setOnClickListener(this);
		follow.setOnClickListener(this);
		controller = UserOperateController.getInstance(this);
		// listView的头部
		View view = LayoutInflater.from(this).inflate(
				R.layout.activity_card_info_head, null);
		avatar = (ImageView) view.findViewById(R.id.user_card_info_avatar);
		userText = (TextView) view.findViewById(R.id.user_card_info_user_name);
		cardNumText = (TextView) view.findViewById(R.id.card_number);
		followNumText = (TextView) view.findViewById(R.id.follow_number);
		fansNumText = (TextView) view.findViewById(R.id.fan_number);
		cardLayout = view.findViewById(R.id.card_info_layout_card);
		followLayout = view.findViewById(R.id.card_info_layout_follow);
		fansLayout = view.findViewById(R.id.card_info_layout_fans);

		// 设置用户头像
		UserTools.setAvatar(this, user.getAvatar(), avatar);
		// 设置用户昵称
		userText.setText(user.getNickName());

		// listView
		listView = (CheckFooterListView) findViewById(R.id.card_list_view);
		initListView(true, view);

		// 显示登录用户自己时，隐藏关注按钮
		if (UserTools.getUid(this).equals(user.getUid())) {
			follow.setVisibility(View.GONE);
			avatar.setOnClickListener(this);
			cardLayout.setOnClickListener(this);
			title.setText(getString(R.string.me));
		} else {
			title.setText(user.getNickName());
		}
		followLayout.setOnClickListener(this);
		fansLayout.setOnClickListener(this);

		// 设置提示语
		if (!user.getUid().equals(UserTools.getUid(this))) {
			tipText.setText(user.getNickName()
					+ getString(R.string.user_no_card).substring(1));
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
			showLoadingDialog(true);
		String customerUid = "";
		String uid = UserTools.getUid(this);
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
								getCardList("0", false, false);
							}
						} else {
							showLoadingDialog(false);
						}
					}
				});
	}

	/**
	 * 获得用户的卡片
	 */
	@Override
	protected void getCardList(final String timelineId,
			final boolean isGetMore, final boolean isPull) {
		super.getCardList(timelineId, isGetMore, isPull);
		controller.getUserCard(user.getUid(), timelineId, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof Card) {
							Card card = (Card) entry;
							if (card.getError().getNo() == 0) {
								afterGetCardList(entry, timelineId, isGetMore,
										isPull);
							} else {
								showToast(card.getError().getDesc());
								listView.onLoadError();
							}
						} else {
							afterGetCardList(entry, timelineId, isGetMore,
									isPull);
						}
					}
				});
	}

	@Override
	protected Card getCardFromDb(String timeLine) {
		return UserCardInfoDb.getInstance(this)
				.getCard(timeLine, user.getUid());
	}

	/**
	 * 关注用户
	 */
	public void addFollow(ArrayList<User> users) {
		showLoadingDialog(true);
		controller.addFollow(UserTools.getUid(this), users,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						if (entry instanceof Error) {
							Error error = (Error) entry;
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
	public void deleteFollow(ArrayList<User> users) {
		showLoadingDialog(true);
		controller.deleteFollow(UserTools.getUid(this), users,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						if (entry instanceof Error) {
							Error error = (Error) entry;
							if (error.getNo() == 0) {
								isFollowed = false;
								setFollowBtnText();
							}
						}
					}
				});
	}

	public void setFollowBtnText() {
		if (isFollowed) {
			follow.setText(R.string.followed);
		} else {
			follow.setText(R.string.follow);
		}
	}

	@Override
	protected void setTipVisibility(boolean show) {
		super.setTipVisibility(show);
		tipText.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public String getActivityName() {
		return UserCardInfoActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_back) {
			finish();
		} else if (v.getId() == R.id.button_follow) {
			if (follow.getVisibility() == View.VISIBLE) {
				ArrayList<User> users = new ArrayList<User>();
				users.add(user);
				if (isFollowed) {
					deleteFollow(users);
				} else {
					addFollow(users);
				}
			}
		} else if (v.getId() == R.id.user_card_info_avatar) {
			if (user.getUid().equals(UserTools.getUid(this))) {
				UserPageTransfer.gotoUserInfoActivity(this, 0, null, 0);
			}
		} else if (v.getId() == R.id.card_info_layout_follow) {
			UserPageTransfer.gotoUserListActivity(this, user,
					RecommendUserActivity.PAGE_FRIEND, false);
		} else if (v.getId() == R.id.card_info_layout_fans) {
			UserPageTransfer.gotoUserListActivity(this, user,
					RecommendUserActivity.PAGE_FANS, false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_USER_LIST) {
				// TODO 从朋友、粉丝页面返回，当是自己的页面并且朋友、粉丝有变化，刷新
				getUserCardInfo(true);
			} else if (requestCode == CardDetailActivity.REQUEST_CODE_SELF) {
				// TODO 从笔记详情页返回，可能文章状态有变化，刷新全部
				getUserCardInfo(false);
			}
		}
	}

}
