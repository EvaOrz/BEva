package cn.com.modernmedia.businessweek;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;
import cn.com.modernmediausermodel.widget.RecommendUserView;

/**
 * 商周用户中心
 * 
 * @author Eva.
 * 
 */
public class UserCenterTabView extends RelativeLayout implements
		OnClickListener {
	private ImageView avatar, messageDot;
	private TextView msgCenter;
	protected TextView userText, cardNumText, followNumText, fansNumText;
	private UserOperateController controller;
	private Context mContext;
	private RelativeLayout cardInfoLayout;
	private User user;
	private UserCardInfo cardInfo;
	private Button login;
	private boolean accountHasChecked = false;
	private Message mMessage = new Message();

	public UserCenterTabView(Context context) {
		this(context, null);
	}

	public UserCenterTabView(Context context, AttributeSet attr) {
		super(context, attr);
		this.mContext = context;
		init();
	}

	private void init() {
		user = SlateDataHelper.getUserLoginInfo(mContext);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		this.addView(inflater.inflate(R.layout.main_usercenter_view, null),
				new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
		controller = UserOperateController.getInstance(mContext);

		cardInfoLayout = (RelativeLayout) findViewById(R.id.user_center_card_info);
		login = (Button) findViewById(R.id.user_center_btn_login);
		avatar = (ImageView) findViewById(R.id.user_center_info_avatar);
		messageDot = (ImageView) findViewById(R.id.user_center_message_dot);
		msgCenter = (TextView) findViewById(R.id.user_center_message_number);
		userText = (TextView) findViewById(R.id.user_center_info_user_name);
		cardNumText = (TextView) findViewById(R.id.user_center_card_number);
		followNumText = (TextView) findViewById(R.id.user_center_follow_number);
		fansNumText = (TextView) findViewById(R.id.user_center_fan_number);

		findViewById(R.id.user_center_layout_card).setOnClickListener(this);
		findViewById(R.id.user_center_layout_follow).setOnClickListener(this);
		findViewById(R.id.user_center_layout_fans).setOnClickListener(this);
		findViewById(R.id.user_center_layout_business_card).setOnClickListener(
				this);
		findViewById(R.id.user_center_square).setOnClickListener(this);
		findViewById(R.id.user_center_scan).setOnClickListener(this);
		findViewById(R.id.user_center_layout_my_coin).setOnClickListener(this);
		findViewById(R.id.user_center_layout_fav).setOnClickListener(this);
		findViewById(R.id.user_center_layout_setting).setOnClickListener(this);

		login.setOnClickListener(this);
		avatar.setOnClickListener(this);

		initHeadView();
		reLoad();
	}

	/**
	 * 初始化头部
	 */
	private void initHeadView() {
		UserTools.setAvatar(mContext, user, avatar);
		// 设置用户昵称
		if (user != null) {
			userText.setText(user.getNickName());
			cardInfoLayout.setVisibility(View.VISIBLE);
			login.setVisibility(View.GONE);
		}
	}

	/**
	 * 获得用户的卡片信息
	 * 
	 */
	public void getUserCardInfo() {
		// showLoading();
		controller.getUserCardInfo(user.getUid(), null,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						// disProcess();
						cardInfo = (UserCardInfo) entry;
						handler.sendEmptyMessage(0);
					}
				});
	}

	/**
	 * 获取消息列表
	 */
	public void getMessageList() {
		controller.getMessageList(Tools.getUid(mContext),
				UserDataHelper.getMessageLastId(mContext),
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof Message) {
							mMessage = (Message) entry;
							if (ParseUtil.listNotNull(mMessage.getMessageList())) {
								if (SlateApplication.mConfig.getHas_coin() == 1) {
									msgCenter.setVisibility(View.VISIBLE);
									msgCenter.setText(mMessage.getMessageList()
											.size() + "");
								} else {
									messageDot.setVisibility(View.VISIBLE);
									msgCenter.setVisibility(View.GONE);
								}
							} else {
								msgCenter.setVisibility(View.GONE);
								messageDot.setVisibility(View.GONE);
							}
						} else {
							msgCenter.setVisibility(View.GONE);
							messageDot.setVisibility(View.GONE);
						}
					}
				});
	}

	private void checkAccountIsValid() {
		if (user == null)
			return;
		controller.getInfoByIdAndToken(user.getUid(), user.getToken(),
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof User) {
							User tempUser = (User) entry;
							ErrorMsg error = tempUser.getError();
							// 取得成功
							if (error.getNo() == 0
									&& !TextUtils.isEmpty(tempUser.getUid())) {
								user.setLogined(true);
								user = tempUser;
								SlateDataHelper.saveUserLoginInfo(mContext,
										user);
								SlateDataHelper.saveAvatarUrl(mContext,
										user.getUserName(), user.getAvatar());
							} else {
								// 无效用户，清掉缓存
								SlateDataHelper.clearLoginInfo(mContext);
							}
						} else {
							// 无效用户，清掉缓存
							SlateDataHelper.clearLoginInfo(mContext);
						}
						reLoad();
					}
				});

	}

	public void reLoad() {
		handler.sendEmptyMessage(1);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:// 更新用户卡片信息
				if (cardInfo != null) {
					cardNumText.setText(UserTools.changeNum(cardInfo
							.getCardNum()));
					followNumText.setText(UserTools.changeNum(cardInfo
							.getFollowNum()));
					fansNumText.setText(UserTools.changeNum(cardInfo
							.getFansNum()));
				}
				break;
			case 1:
				user = SlateDataHelper.getUserLoginInfo(mContext);
				if (user == null) {
					login.setVisibility(View.VISIBLE);
					cardInfoLayout.setVisibility(View.GONE);
					avatar.setImageResource(R.drawable.avatar_placeholder);
					msgCenter.setVisibility(View.GONE);
					initHeadView();
				} else if (accountHasChecked) {
					login.setVisibility(View.GONE);
					cardInfoLayout.setVisibility(View.VISIBLE);
					// 获取头部用数据
					getUserCardInfo();
					// 获取消息列表
					getMessageList();
					initHeadView();
				} else if (Tools.checkNetWork(mContext)) {
					checkAccountIsValid();
					accountHasChecked = true;
				}

				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.user_center_layout_fav) { // 我的收藏,如果为登录，则显示本地收藏
			UserPageTransfer.gotoFavoritesActivity(mContext);
		} else if (id == R.id.user_center_info_avatar) { // 前往用户信息页面
			if (user == null)
				UserPageTransfer.gotoLoginActivity(mContext,
						UserPageTransfer.GOTO_HOME_PAGE);
			else
				UserPageTransfer.gotoUserInfoActivity(mContext, 0, null, null,
						0);
		} else if (id == R.id.user_center_btn_login) {// 点击登录按钮
			UserPageTransfer.gotoLoginActivity(mContext, -1);
		} else if (id == R.id.user_center_layout_my_coin) { // 我的金币
			UserPageTransfer.gotoMyCoinActivity(mContext, false, false);
		} else if (id == R.id.user_center_layout_business_card) {// 前往商业笔记页面
			UserPageTransfer.gotoMyHomePageActivity(mContext, false);
		} else if (id == R.id.user_center_layout_card) { // 前往我的首页页面
			UserPageTransfer.gotoUserCardInfoActivity(mContext, user, false);
		} else if (id == R.id.user_center_layout_follow) { // 前往关注界面
			UserPageTransfer.gotoUserListActivity(mContext, user,
					RecommendUserView.PAGE_FRIEND, false);
		} else if (id == R.id.user_center_layout_fans) { // 前往粉丝界面
			UserPageTransfer.gotoUserListActivity(mContext, user,
					RecommendUserView.PAGE_FANS, false);
		} else if (id == R.id.user_center_square) {// 热门商业笔记(广场)
			UserPageTransfer.gotoSquareActivity(mContext, false);
		} else if (id == R.id.user_center_layout_setting) {// 设置页面
			UserPageTransfer.gotoSettingActivity(mContext, false);
		} else if (id == R.id.user_center_scan) {
			UserPageTransfer.gotoScanActivity(mContext, false);
		}
	}
}
