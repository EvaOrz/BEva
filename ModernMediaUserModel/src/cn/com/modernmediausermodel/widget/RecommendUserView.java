package cn.com.modernmediausermodel.widget;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.CheckScrollListview;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.adapter.RecommendUsersAdapter;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.UserInfoDb;
import cn.com.modernmediausermodel.listener.CardViewListener;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.listener.UserInfoChangeListener;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.User.Error;
import cn.com.modernmediausermodel.model.Users;
import cn.com.modernmediausermodel.model.Users.UserCardInfo;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 推荐关注、粉丝、好友页面
 * 
 * @author user
 * 
 */
public class RecommendUserView implements OnClickListener, CardViewListener {
	public static final int PAGE_RECOMMEND_FRIEND = 0; // 推荐用户界面
	public static final int PAGE_FRIEND = 1; // 朋友
	public static final int PAGE_FANS = 2; // 粉丝
	public static final String KEY_PAGE_TYPE = "page_type";

	private Context mContext;
	private View view, titleBar;
	private TextView tipText, titleText;
	private ImageView divider;
	private Button complete, followAll;
	private CheckScrollListview listView;
	private RecommendUsersAdapter adapter;
	private UserOperateController controller;
	private Users mUsers;
	private int pageType; // 页面类型
	private User mUser;// 从此用户获取信息(非登录用户)
	private Handler handler = new Handler();

	public RecommendUserView(Context context, int pageType, User user) {
		mContext = context;
		this.pageType = pageType;
		mUser = user;
		initWidget();
	}

	private void initWidget() {
		view = LayoutInflater.from(mContext).inflate(
				R.layout.activity_recommenduser, null);
		titleBar = view.findViewById(R.id.bar_layout);
		titleText = (TextView) view.findViewById(R.id.recommend_user_bar_title);
		complete = (Button) view.findViewById(R.id.complete);
		followAll = (Button) view.findViewById(R.id.button_follow_all);
		divider = (ImageView) view.findViewById(R.id.recommend_divider);
		listView = (CheckScrollListview) view.findViewById(R.id.list_view);
		tipText = (TextView) view.findViewById(R.id.no_friend_tip);
		UserApplication.recommInfoChangeListener = new UserInfoChangeListener() {

			@Override
			public void deleteFollow(int num) {
				if (pageType == PAGE_FRIEND) {
					refresh();
				}
			}

			@Override
			public void deleteCard(int num) {
			}

			@Override
			public void addFollow(int num) {
				if (pageType == PAGE_FRIEND) {
					refresh();
				}
			}

			@Override
			public void addCard(int num) {
			}
		};
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

	public View getBackBtn() {
		return view.findViewById(R.id.button_back);
	}

	private void init() {
		controller = UserOperateController.getInstance(mContext);
		adapter = new RecommendUsersAdapter(mContext, pageType, mUser);
		listView.setAdapter(adapter);
		followAll.setOnClickListener(this);
		complete.setOnClickListener(this);
		// 根据页面类型，获得相应的数据，并显示
		showPageContent(true);
	}

	private void refresh() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				showPageContent(false);
			}
		});
	}

	private void showPageContent(boolean showLoading) {
		boolean isCurrentUser = mUser.getUid().equals(
				UserTools.getUid(mContext)); // 是否显示当前用户的相关信息
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
				tipText.setText(R.string.no_friend_tip);
			} else {
				titleText.setText(R.string.his_friends);
				tipText.setText(mUser.getNickName()
						+ mContext.getString(R.string.no_friend_tip).substring(
								1));
			}
			break;
		case PAGE_FANS:
			followAll.setVisibility(View.GONE);
			divider.setVisibility(View.GONE);
			if (isCurrentUser) {
				titleText.setText(R.string.my_fans);
				tipText.setText(R.string.no_fan_tip);
			} else {
				titleText.setText(R.string.his_fans);
				tipText.setText(mUser.getNickName()
						+ mContext.getString(R.string.no_fan_tip).substring(1));
			}
			break;
		default:
			break;
		}
		tipText.setVisibility(View.GONE);
		// 获得推荐关注用户列表
		getRecommendUsersData(showLoading);
	}

	/**
	 * 获得推荐的用户列表
	 */
	private void getRecommendUsersData(boolean showLoading) {
		if (showLoading) {
			ModernMediaTools.showLoading(mContext, true);
		}
		controller.getRecommendUsers(mUser.getUid(), pageType,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry != null) {
							final Users users = (Users) entry;
							if (users.getError().getNo() == 0) {
								getUsersInfo(users);
							} else {
								ModernMediaTools.showLoading(mContext, false);
								ModernMediaTools.showToast(mContext, users
										.getError().getDesc());
							}
						} else {
							ModernMediaTools.showLoading(mContext, false);
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
		adapter.clear();
		adapter.setmUsers(mUsers);
		if (mUsers.getUserCardInfoMap().size() > 0) {
			controller.getUsersInfo(users.getUserCardInfoMap().keySet(),
					new UserFetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							ModernMediaTools.showLoading(mContext, false);
							if (entry instanceof Users) {
								mUsers.setUserList(((Users) entry)
										.getUserList());
								adapter.setData(mUsers);
								System.out.println(adapter.getCount());
							} else {
								// 所有缓存的卡片用户信息
								Users mAllUsers = UserInfoDb.getInstance(
										mContext).getUsersInfo();
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
			ModernMediaTools.showLoading(mContext, false);
			tipText.setVisibility(adapter.getCount() == 0 ? View.VISIBLE
					: View.GONE);
		}
	}

	/**
	 * 关注用户(推荐列表，全部关注)
	 */
	private void addFollow(List<User> users) {
		ModernMediaTools.showLoading(mContext, true);
		controller.addFollow(UserTools.getUid(mContext), users, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof Error
								&& ((Error) entry).getNo() == 0)
							checkGotoSquareActivity();
					}
				});
	}

	public RecommendUsersAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.complete) {
			if (pageType == PAGE_RECOMMEND_FRIEND) {
				checkGotoSquareActivity();
			}
		} else if (v.getId() == R.id.button_follow_all) {
			addFollow(mUsers.getUserList());
		}
	}

	/**
	 * 灵感直接finish
	 */
	private void checkGotoSquareActivity() {
		if (ConstData.getAppId() == 102) {
			if (UserApplication.logOutListener != null) {
				UserApplication.logOutListener.onLogout();
			}
			((Activity) mContext).finish();
		} else {
			UserPageTransfer.gotoSquareActivity(mContext, true);
		}
	}

}
