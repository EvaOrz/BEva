package cn.com.modernmediausermodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.CheckScrollListview;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.adapter.RecommendUsersAdapter;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.UserInfoDb;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.model.Users;
import cn.com.modernmediausermodel.model.Users.UserCardInfo;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 推荐关注、粉丝、好友页面
 * 
 * @author user
 * 
 */
public class RecommendUserActivity extends BaseActivity implements
		OnClickListener {
	public static final int PAGE_RECOMMEND_FRIEND = 0; // 推荐用户界面
	public static final int PAGE_FRIEND = 1; // 朋友
	public static final int PAGE_FANS = 2; // 粉丝
	public static final String KEY_PAGE_TYPE = "page_type";

	private TextView titleText, noFriendTip;
	private ImageView back, divider;
	private Button complete, followAll;
	private CheckScrollListview listView;
	private RecommendUsersAdapter adapter;
	private UserOperateController controller;
	private Users mUsers;
	private int pageType; // 页面类型
	private User mUser;// 从此用户获取信息(非登录用户)
	private boolean hasModify = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommenduser);
		initDataFromBundle();
		init();
	}

	private void initDataFromBundle() {
		// 取得上个页面传来的用户信息
		if (getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();
			this.pageType = bundle.getInt(KEY_PAGE_TYPE);
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
		titleText = (TextView) findViewById(R.id.recommend_user_bar_title);
		back = (ImageView) findViewById(R.id.button_back);
		complete = (Button) findViewById(R.id.complete);
		followAll = (Button) findViewById(R.id.button_follow_all);
		divider = (ImageView) findViewById(R.id.recommend_divider);
		listView = (CheckScrollListview) findViewById(R.id.list_view);
		noFriendTip = (TextView) findViewById(R.id.no_friend_tip);
		adapter = new RecommendUsersAdapter(this, pageType);
		listView.setAdapter(adapter);

		back.setOnClickListener(this);
		complete.setOnClickListener(this);
		followAll.setOnClickListener(this);
		controller = UserOperateController.getInstance(this);
		// 根据页面类型，获得相应的数据，并显示
		showPageContent();
	}

	private void showPageContent() {
		boolean isCurrentUser = mUser.getUid().equals(UserTools.getUid(this)); // 是否显示当前用户的相关信息
		switch (pageType) {
		case PAGE_RECOMMEND_FRIEND:
			complete.setText(R.string.complete);
			titleText.setText(R.string.recommend_user);
			break;
		case PAGE_FRIEND:
			followAll.setVisibility(View.GONE);
			divider.setVisibility(View.GONE);
			if (isCurrentUser) {
				titleText.setText(R.string.my_friends);
				noFriendTip.setText(R.string.no_friend_tip);
			} else {
				titleText.setText(R.string.his_friends);
				noFriendTip.setText(mUser.getNickName()
						+ getString(R.string.no_friend_tip).substring(1));
			}
			break;
		case PAGE_FANS:
			followAll.setVisibility(View.GONE);
			divider.setVisibility(View.GONE);
			if (isCurrentUser) {
				titleText.setText(R.string.my_fans);
				noFriendTip.setText(R.string.no_fan_tip);
			} else {
				titleText.setText(R.string.his_fans);
				noFriendTip.setText(mUser.getNickName()
						+ getString(R.string.no_fan_tip).substring(1));
			}
			break;
		default:
			break;
		}
		noFriendTip.setVisibility(View.GONE);
		// 获得推荐关注用户列表
		getRecommendUsersData();
	}

	/**
	 * 获得推荐的用户列表
	 */
	public void getRecommendUsersData() {
		showLoadingDialog(true);
		controller.getRecommendUsers(mUser.getUid(), pageType,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry != null) {
							final Users users = (Users) entry;
							if (users.getError().getNo() == 0) {
								getUsersInfo(users);
							} else {
								showLoadingDialog(false);
								showToast(users.getError().getDesc());
							}
						} else {
							showLoadingDialog(false);
						}
					}
				});
	}

	/**
	 * 获得所有推荐用户的相关信息
	 * 
	 * @param users
	 */
	private void getUsersInfo(Users users) {
		this.mUsers = users;
		if (mUsers.getUserCardInfoMap().size() > 0) {
			controller.getUsersInfo(users.getUserCardInfoMap().keySet(),
					new UserFetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							showLoadingDialog(false);
							if (entry instanceof Users) {
								mUsers.setUserList(((Users) entry)
										.getUserList());
								adapter.setData(mUsers);
							} else {
								// 所有缓存的卡片用户信息
								Users mAllUsers = UserInfoDb.getInstance(
										RecommendUserActivity.this)
										.getUsersInfo();
								List<User> mUserList = mAllUsers.getUserList();
								if (ParseUtil.listNotNull(mUserList)) {
									Map<String, UserCardInfo> map = mUsers
											.getUserCardInfoMap();
									for (User user : mUserList) {
										// 清除不属于粉丝或者好友列表的用户
										if (map.containsKey(user.getUid())) {
											mUsers.getUserList().add(user);
										}
									}
									adapter.setData(mUsers);
								}
							}
						}
					});
		} else {
			showLoadingDialog(false);
			noFriendTip.setVisibility(adapter.getCount() == 0 ? View.VISIBLE
					: View.GONE);
		}
	}

	/**
	 * 关注用户(推荐列表，全部关注)
	 */
	private void addFollow(List<User> users) {
		showLoadingDialog(true);
		controller.addFollow(UserTools.getUid(this), users,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterFollow(entry, null, 1);
					}
				});
	}

	/**
	 * 关注用户（单个用户）
	 * 
	 * @param user
	 */
	public void addFollow(final User user) {
		if (user == null)
			return;
		List<User> users = new ArrayList<User>();
		users.add(user);
		showLoadingDialog(true);
		controller.addFollow(UserTools.getUid(this), users,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterFollow(entry, user, 2);
					}
				});
	}

	/**
	 * 刷新adapter
	 * 
	 * @param user
	 *            操作的user
	 * @param isFollow
	 *            1.全部关注，2关注，3取消关注
	 */
	private void afterFollow(Entry entry, User user, int followType) {
		showLoadingDialog(false);
		if (entry instanceof Error && ((Error) entry).getNo() == 0) {
			if (followType == 1) {
				UserPageTransfer.gotoSquareActivity(this, true);
				return;
			}
			if (mUser != null
					&& mUsers.getUserCardInfoMap().containsKey(user.getUid())) {
				hasModify = true;
				int follow = followType == 2 ? 1 : 0;
				mUsers.getUserCardInfoMap().get(user.getUid())
						.setIsFollowed(follow);
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 取消关注用户
	 */
	public void deleteFollow(final User user) {
		if (user == null)
			return;
		List<User> users = new ArrayList<User>();
		users.add(user);
		showLoadingDialog(true);
		controller.deleteFollow(UserTools.getUid(this), users,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterFollow(entry, user, 3);
					}
				});
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
	public void onClick(View v) {
		if (v.getId() == R.id.button_back) {
			doBack();
		} else if (v.getId() == R.id.complete) {
			if (pageType == PAGE_RECOMMEND_FRIEND) {
				UserPageTransfer.gotoSquareActivity(this, true);
			}
		} else if (v.getId() == R.id.button_follow_all) {
			addFollow(mUsers.getUserList());
		}
	}

	@Override
	public void reLoadData() {
	}

	/**
	 * 返回
	 */
	private void doBack() {
		if (hasModify && mUser != null
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
}
