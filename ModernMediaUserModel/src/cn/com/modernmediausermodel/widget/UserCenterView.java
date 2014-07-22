package cn.com.modernmediausermodel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.RecommendUserActivity;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.Users.UserCardInfo;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

public class UserCenterView extends BaseView implements OnClickListener {
	private ImageView avatar;
	private ImageView msgCenter;
	private TextView userText, cardNumText, followNumText, fansNumText;
	private LinearLayout cardLayout, followLayout, fansLayout;
	private UserOperateController controller;
	private Context mContext;
	private User user;
	private RelativeLayout businessPage, homePage, cardFind, msgCenterLayout,
			myFav;
	private RelativeLayout cardInfoLayout;
	private Button login;
	private Message mMessage = new Message();
	private Issue issue;

	public UserCenterView(Context context) {
		this(context, null);
	}

	public UserCenterView(Context context, AttributeSet attr) {
		super(context, attr);
		this.mContext = context;
		init();
	}

	private void init() {
		user = UserDataHelper.getUserLoginInfo(mContext);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		this.addView(inflater.inflate(R.layout.user_center, null),
				new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
		controller = UserOperateController.getInstance(mContext);

		cardInfoLayout = (RelativeLayout) findViewById(R.id.user_center_card_info);
		businessPage = (RelativeLayout) findViewById(R.id.user_center_layout_business_card);
		homePage = (RelativeLayout) findViewById(R.id.user_center_layout_homepage);
		cardFind = (RelativeLayout) findViewById(R.id.user_center_layout_find);
		msgCenterLayout = (RelativeLayout) findViewById(R.id.user_center_layout_message_center);
		myFav = (RelativeLayout) findViewById(R.id.user_center_layout_fav);
		login = (Button) findViewById(R.id.user_center_btn_login);
		avatar = (ImageView) findViewById(R.id.user_center_info_avatar);

		userText = (TextView) findViewById(R.id.user_center_info_user_name);
		cardNumText = (TextView) findViewById(R.id.user_center_card_number);
		followNumText = (TextView) findViewById(R.id.user_center_follow_number);
		fansNumText = (TextView) findViewById(R.id.user_center_fan_number);
		msgCenter = (ImageView) findViewById(R.id.user_center_has_message);
		cardLayout = (LinearLayout) findViewById(R.id.user_center_layout_card);
		followLayout = (LinearLayout) findViewById(R.id.user_center_layout_follow);
		fansLayout = (LinearLayout) findViewById(R.id.user_center_layout_fans);

		businessPage.setOnClickListener(this);
		homePage.setOnClickListener(this);
		cardFind.setOnClickListener(this);
		msgCenterLayout.setOnClickListener(this);
		myFav.setOnClickListener(this);
		login.setOnClickListener(this);
		avatar.setOnClickListener(this);
		cardLayout.setOnClickListener(this);
		followLayout.setOnClickListener(this);
		fansLayout.setOnClickListener(this);

		reLoad();
	}

	/**
	 * 初始化头部
	 */
	private void initHeadView() {
		UserTools.setAvatar(mContext, user, avatar);
		// 设置用户昵称
		if (user != null)
			userText.setText(user.getNickName());
	}

	public void setHeadData(UserCardInfo cardInfo) {
		if (cardInfo != null) {
			cardNumText.setText(UserTools.changeNum(cardInfo.getCardNum()));
			followNumText.setText(UserTools.changeNum(cardInfo.getFollowNum()));
			fansNumText.setText(UserTools.changeNum(cardInfo.getFansNum()));
		}
	}

	/**
	 * 获得用户的卡片信息
	 * 
	 * @param users
	 */
	public void getUserCardInfo() {
		// showLoading();
		controller.getUserCardInfo(user.getUid(), null,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						// disProcess();
						UserCardInfo cardInfo = (UserCardInfo) entry;
						if (cardInfo != null) {
							setHeadData(cardInfo);
						}
					}
				});
	}

	/**
	 * 获取消息列表
	 * 
	 */
	public void getMessageList() {
		controller.getMessageList(UserTools.getUid(mContext),
				UserDataHelper.getMessageLastId(mContext),
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry != null && entry instanceof Message) {
							mMessage = (Message) entry;
							if (ParseUtil.listNotNull(mMessage.getMessageList())) {
								msgCenter.setVisibility(View.VISIBLE);
							} else {
								msgCenter.setVisibility(View.GONE);
							}
						}
					}
				});
	}

	@Override
	public void reLoad() {
		user = UserDataHelper.getUserLoginInfo(mContext);
		if (user == null) {
			login.setVisibility(View.VISIBLE);
			cardInfoLayout.setVisibility(View.GONE);
			msgCenter.setVisibility(View.GONE);
		} else {
			login.setVisibility(View.GONE);
			cardInfoLayout.setVisibility(View.VISIBLE);
			// 获取头部用数据
			getUserCardInfo();
			// 获取消息列表
			getMessageList();
		}
		initHeadView();
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.user_center_layout_fav) { // 我的收藏,如果为登录，则显示本地收藏
			UserPageTransfer.gotoFavoritesActivity(mContext, issue);
		} else if (id == R.id.user_center_info_avatar) { // 前往用户信息页面
			UserPageTransfer.gotoUserInfoActivity(mContext, 0, null, 0);
		} else if (id == R.id.user_center_btn_login) {// 点击登录按钮
			UserPageTransfer.gotoLoginActivity(mContext, 0);
		} else if (id == R.id.user_center_layout_homepage
				|| id == R.id.user_center_layout_business_card) { // 我的首页
			UserPageTransfer.gotoMyHomePageActivity(mContext, false);
		} else if (id == R.id.user_center_layout_message_center) {// 通知中心
			UserPageTransfer.gotoMessageActivity(mContext, mMessage, false);
		} else if (id == R.id.user_center_layout_card) { // 前往笔记页面
			UserPageTransfer.gotoUserCardInfoActivity(mContext, user, false);
		} else if (id == R.id.user_center_layout_follow) { // 前往关注界面
			UserPageTransfer.gotoUserListActivity(mContext, user,
					RecommendUserActivity.PAGE_FRIEND, false);
		} else if (id == R.id.user_center_layout_fans) { // 前往粉丝界面
			UserPageTransfer.gotoUserListActivity(mContext, user,
					RecommendUserActivity.PAGE_FANS, false);
		} else if (id == R.id.user_center_layout_find) {// 热门商业笔记(广场)
			UserPageTransfer.gotoSquareActivity(mContext, false);
		}
	}

	protected void setTextColor(int color) {
		userText.setTextColor(color);
		cardNumText.setTextColor(color);
		followNumText.setTextColor(color);
		fansNumText.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_business_card))
				.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_my_homepage))
				.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_message_center))
				.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_find))
				.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_fav))
				.setTextColor(color);
		((TextView) findViewById(R.id.user_center_card)).setTextColor(color);
		((TextView) findViewById(R.id.user_center_follow)).setTextColor(color);
		((TextView) findViewById(R.id.user_center_fan)).setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_setting))
				.setTextColor(color);
	}
}
