package cn.com.modernmediausermodel.widget;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmedia.widget.CheckFooterListView.FooterCallBack;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.LoginActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.adapter.RecommendUsersAdapter;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.db.UserListDb;
import cn.com.modernmediausermodel.listener.CardViewListener;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.listener.UserInfoChangeListener;
import cn.com.modernmediausermodel.model.UserCardInfoList;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.util.UserConstData;
import cn.com.modernmediausermodel.util.UserPageTransfer;

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
	private CheckFooterListView listView;
	private RecommendUsersAdapter adapter;
	private UserOperateController controller;
	private UserCardInfoList userCardInfoList;
	private int pageType; // 页面类型
	private User mUser;// 从此用户获取信息(非登录用户)
	private Handler handler = new Handler();
	private boolean isGetMore = false; // 是否加载更多

	public RecommendUserView(Context context, int pageType, User user) {
		mContext = context;
		this.pageType = pageType;
		mUser = user;
		initWidget();
		// TODO微博登陆成功后弹框可能在登录页面finish后才dismiss,所以等跳转了页面再把登录页面finish掉
		if (ParseUtil.mapContainsKey(SlateApplication.activityMap,
				LoginActivity.class.getName())) {
			SlateApplication.activityMap.get(LoginActivity.class.getName())
					.finish();
		}
	}

	private void initWidget() {
		view = LayoutInflater.from(mContext).inflate(
				R.layout.activity_recommenduser, null);
		titleBar = view.findViewById(R.id.bar_layout);
		titleText = (TextView) view.findViewById(R.id.recommend_user_bar_title);
		complete = (Button) view.findViewById(R.id.complete);
		followAll = (Button) view.findViewById(R.id.button_follow_all);
		divider = (ImageView) view.findViewById(R.id.recommend_divider);
		listView = (CheckFooterListView) view
				.findViewById(R.id.recommend_user_list_view);
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
		listView.enableAutoFetch(false, false);
		adapter = new RecommendUsersAdapter(mContext, pageType, mUser);
		listView.setAdapter(adapter);
		loadCacheFirst();
		// 加载更多
		listView.setCallBack(new FooterCallBack() {

			@Override
			public void onLoad() {
				if (adapter.getCount() < UserConstData.MAX_USER_ITEM_COUNT
						|| userCardInfoList == null) {
					return;
				}
				List<UserCardInfo> list = userCardInfoList.getList();
				if (ParseUtil.listNotNull(list)) {
					isGetMore = true;
					getRecommendUsersData(false);
				}
			}
		});

		followAll.setOnClickListener(this);
		complete.setOnClickListener(this);
		// 根据页面类型，获得相应的数据，并显示
		showPageContent(true);
	}

	private void refresh() {
		isGetMore = false;
		handler.post(new Runnable() {

			@Override
			public void run() {
				getRecommendUsersData(false);
			}
		});
	}

	// TODO 先加载缓存数据
	private void loadCacheFirst() {
		UserCardInfoList list = getListFromDb();
		if (ParseUtil.listNotNull(list.getList())) {
			userCardInfoList = list;
			adapter.setData(list.getList());
		}
	}

	private UserCardInfoList getListFromDb() {
		int dbId = -1;
		if (userCardInfoList != null
				&& ParseUtil.listNotNull(userCardInfoList.getList())) {
			dbId = userCardInfoList.getList()
					.get(userCardInfoList.getList().size() - 1).getDb_id();
		}
		return UserListDb.getInstance(mContext).getUserInfoList(pageType + "",
				mUser.getUid(), dbId);
	}

	private void showPageContent(boolean showLoading) {
		boolean isCurrentUser = mUser.getUid().equals(Tools.getUid(mContext)); // 是否显示当前用户的相关信息
		switch (pageType) {
		case PAGE_RECOMMEND_FRIEND:
			followAll.setVisibility(View.VISIBLE);
			divider.setVisibility(View.VISIBLE);
			complete.setText(R.string.complete);
			titleText.setText(R.string.recommend_user);
			break;
		case PAGE_FRIEND:
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
			Tools.showLoading(mContext, true);
		}
		final String offsetId = isGetMore ? userCardInfoList.getOffsetId()
				: "0";
		int dbId = isGetMore ? userCardInfoList.getList()
				.get(userCardInfoList.getList().size() - 1).getDb_id() : -1;
		controller.getRecommendUsers(mUser.getUid(), pageType, offsetId, dbId,
				mContext, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						afterGetUserCardInfos(entry, offsetId);
					}
				});
	}

	private void afterGetUserCardInfos(Entry entry, String offsetId) {
		UserCardInfoList infoList;
		if (entry instanceof UserCardInfoList) {
			infoList = (UserCardInfoList) entry;
			List<UserCardInfo> list = infoList.getList();
			if (!ParseUtil.listNotNull(list)) {
				if (!isGetMore) {
					adapter.clear();
					Tools.showLoading(mContext, false);
				} else {
					listView.loadOk(false);
				}
				if (adapter.getCount() == 0) {
					tipText.setVisibility(View.VISIBLE);
					listView.removeFooter();
				} else {
					tipText.setVisibility(View.GONE);
				}
				return;
			}
			if (isGetMore) {
				userCardInfoList.getList().addAll(list);
				userCardInfoList.setOffsetId(infoList.getOffsetId());
			} else {
				userCardInfoList = infoList;
			}
			Tools.showLoading(mContext, false);
			adapter.clear();
			adapter.setData(userCardInfoList.getList());
			listView.loadOk(true);
			if (list.size() < UserConstData.MAX_USER_ITEM_COUNT) {
				listView.removeFooter();
			} else {
				listView.showFooter();
			}
		} else if (isGetMore) {
			listView.onLoadError();
		} else {
			Tools.showLoading(mContext, false);
		}

	}

	/**
	 * 关注用户(推荐列表，全部关注)
	 */
	private void addFollow(List<UserCardInfo> list) {
		Tools.showLoading(mContext, true);
		controller.addFollow(Tools.getUid(mContext), list, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof ErrorMsg
								&& ((ErrorMsg) entry).getNo() == 0)
							checkGotoMyHomeActivity();
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
				checkGotoMyHomeActivity();
			}
		} else if (v.getId() == R.id.button_follow_all) {
			addFollow(userCardInfoList.getList());
		}
	}

	/**
	 * 灵感直接finish
	 */
	private void checkGotoMyHomeActivity() {
		if (SlateApplication.APP_ID == 102) {
			if (UserApplication.logOutListener != null) {
				UserApplication.logOutListener.onLogout();
			}
			((Activity) mContext).finish();
		}
		UserPageTransfer.gotoMyHomePageActivity(mContext, true);
	}

}
