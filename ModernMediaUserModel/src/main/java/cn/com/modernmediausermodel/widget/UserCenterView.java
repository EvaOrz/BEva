package cn.com.modernmediausermodel.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 用户中心
 * 
 * @author user
 */
public class UserCenterView extends RelativeLayout implements OnClickListener {
	private ImageView avatar, messageDot;
	private TextView msgCenter;
	protected TextView userText, cardNumText, followNumText, fansNumText;
	private LinearLayout cardLayout, followLayout, fansLayout;
	private UserOperateController controller;
	private Context mContext;
	private User user;
	public RelativeLayout businessPage, homePage, myCoin, cardFind, myFav,
			messageLayout, settingLayout, musicLayout, scanLayout;
	private RelativeLayout cardInfoLayout;
	private Button login;
	private boolean accountHasChecked = false;
	private Message mMessage = new Message();

	public UserCenterView(Context context) {
		this(context, null);
	}

	public UserCenterView(Context context, AttributeSet attr) {
		super(context, attr);
		this.mContext = context;
		init();
	}

	private void init() {
		user = SlateDataHelper.getUserLoginInfo(mContext);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		this.addView(inflater.inflate(R.layout.user_center, null),
				new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
		controller = UserOperateController.getInstance(mContext);

		cardInfoLayout = (RelativeLayout) findViewById(R.id.user_center_card_info);
		businessPage = (RelativeLayout) findViewById(R.id.user_center_layout_business_card);
		homePage = (RelativeLayout) findViewById(R.id.user_center_layout_homepage);
		cardFind = (RelativeLayout) findViewById(R.id.user_center_layout_find);
		myCoin = (RelativeLayout) findViewById(R.id.user_center_layout_my_coin);
		myFav = (RelativeLayout) findViewById(R.id.user_center_layout_fav);
		messageLayout = (RelativeLayout) findViewById(R.id.user_center_layout_message);
		login = (Button) findViewById(R.id.user_center_btn_login);
		avatar = (ImageView) findViewById(R.id.user_center_info_avatar);
		messageDot = (ImageView) findViewById(R.id.user_center_message_dot);
		userText = (TextView) findViewById(R.id.user_center_info_user_name);
		cardNumText = (TextView) findViewById(R.id.user_center_card_number);
		followNumText = (TextView) findViewById(R.id.user_center_follow_number);
		fansNumText = (TextView) findViewById(R.id.user_center_fan_number);
		msgCenter = (TextView) findViewById(R.id.user_center_message_number);
		cardLayout = (LinearLayout) findViewById(R.id.user_center_layout_card);
		followLayout = (LinearLayout) findViewById(R.id.user_center_layout_follow);
		fansLayout = (LinearLayout) findViewById(R.id.user_center_layout_fans);
		settingLayout = (RelativeLayout) findViewById(R.id.user_center_layout_setting);
		musicLayout = (RelativeLayout) findViewById(R.id.user_center_music);
		scanLayout = (RelativeLayout) findViewById(R.id.user_center_scan);

		if (ConstData.getAppId() == 20) {// iweekly电台栏目
			musicLayout.setVisibility(View.VISIBLE);
		}

		businessPage.setOnClickListener(this);
		homePage.setOnClickListener(this);
		cardFind.setOnClickListener(this);
		myFav.setOnClickListener(this);
		login.setOnClickListener(this);
		avatar.setOnClickListener(this);
		cardLayout.setOnClickListener(this);
		followLayout.setOnClickListener(this);
		fansLayout.setOnClickListener(this);
		messageLayout.setOnClickListener(this);
		myCoin.setOnClickListener(this);
		msgCenter.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
		musicLayout.setOnClickListener(this);
		scanLayout.setOnClickListener(this);

		// 如果存在我的金币页面时，通知中心以数字形式存在
		if (SlateApplication.mConfig.getHas_coin() == 1) {
			messageLayout.setVisibility(View.GONE);
			myCoin.setVisibility(View.VISIBLE);
		} else {
			myCoin.setVisibility(View.GONE);
			messageLayout.setVisibility(View.VISIBLE);
		}
		cardInfoLayout.setVisibility(View.GONE);
		initHeadView();
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

	public void reLoad() {
		user = SlateDataHelper.getUserLoginInfo(mContext);
		if (user == null) {
			login.setVisibility(View.VISIBLE);
			cardInfoLayout.setVisibility(View.GONE);
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
	}

	private void checkAccountIsValid() {
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
						new Handler().post(new Runnable() {

							@Override
							public void run() {
								reLoad();
							}
						});
					}
				});

	}

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
			UserPageTransfer.gotoLoginActivity(mContext,
					UserPageTransfer.GOTO_HOME_PAGE);
		} else if (id == R.id.user_center_layout_homepage
				|| id == R.id.user_center_layout_business_card) { // 我的首页
			UserPageTransfer.gotoMyHomePageActivity(mContext, false);
		} else if (id == R.id.user_center_message_number
				|| id == R.id.user_center_layout_message) { // 通知中心
			UserPageTransfer.gotoMessageActivity(mContext, mMessage, false);
		} else if (id == R.id.user_center_layout_my_coin) { // 我的金币
			UserPageTransfer.gotoMyCoinActivity(mContext, false, false);
		} else if (id == R.id.user_center_layout_card) { // 前往笔记页面
			UserPageTransfer.gotoUserCardInfoActivity(mContext, user, false);
		} else if (id == R.id.user_center_layout_follow) { // 前往关注界面
			UserPageTransfer.gotoUserListActivity(mContext, user,
					RecommendUserView.PAGE_FRIEND, false);
		} else if (id == R.id.user_center_layout_fans) { // 前往粉丝界面
			UserPageTransfer.gotoUserListActivity(mContext, user,
					RecommendUserView.PAGE_FANS, false);
		} else if (id == R.id.user_center_layout_find) {// 热门商业笔记(广场)
			UserPageTransfer.gotoSquareActivity(mContext, false);
		} else if (id == R.id.user_center_layout_setting) {// 设置页面
			UserPageTransfer.gotoSettingActivity(mContext, false);
		} else if (id == R.id.user_center_music) {// 临时电台页面
			UserPageTransfer.gotoMusicActivity(mContext, false);
		} else if (id == R.id.user_center_scan) {
			UserPageTransfer.gotoScanActivity(mContext, false);
		}
	}

	public void setTextColor(int color) {
		userText.setTextColor(color);
		cardNumText.setTextColor(color);
		followNumText.setTextColor(color);
		fansNumText.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_business_card))
				.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_my_homepage))
				.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_music))
				.setTextColor(color);
		((TextView) findViewById(R.id.user_center_text_my_coin))
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
		((TextView) findViewById(R.id.user_center_text_message))
				.setTextColor(color);
	}

	public void setCardName(int id) {
		((TextView) findViewById(R.id.user_center_text_business_card))
				.setText(id);
	}

	public Message getmMessage() {
		return mMessage;
	}

	/**
	 * 设置整个页面的背景色
	 */
	public void setBackgroundColor(int color) {
		findViewById(R.id.use_center_contain).setBackgroundColor(color);
	}

	/**
	 * 设置我的首页、浏览发现、我的金币icon
	 * 
	 * @param picture
	 */
	public void setTextLeftPicture(Drawable picture) {
		((TextView) findViewById(R.id.user_center_text_my_homepage))
				.setCompoundDrawablesWithIntrinsicBounds(picture, null, null,
						null);
		((TextView) findViewById(R.id.user_center_text_find))
				.setCompoundDrawablesWithIntrinsicBounds(picture, null, null,
						null);
		((TextView) findViewById(R.id.user_center_text_my_coin))
				.setCompoundDrawablesWithIntrinsicBounds(picture, null, null,
						null);
		((TextView) findViewById(R.id.user_center_text_message))
				.setCompoundDrawablesWithIntrinsicBounds(picture, null, null,
						null);
	}

	/**
	 * 设置分隔线背景
	 * 
	 * @param drawable
	 */
	public void setDivider(Drawable drawable) {
		((ImageView) findViewById(R.id.user_center_divider1))
				.setImageDrawable(drawable);
		((ImageView) findViewById(R.id.user_center_divider2))
				.setImageDrawable(drawable);
		((ImageView) findViewById(R.id.user_center_divider3))
				.setImageDrawable(drawable);
		((ImageView) findViewById(R.id.user_center_divider4))
				.setImageDrawable(drawable);
		((ImageView) findViewById(R.id.user_center_divider5))
				.setImageDrawable(drawable);
		((ImageView) findViewById(R.id.user_center_divider6))
				.setImageDrawable(drawable);
	}

	/**
	 * 设置分隔线高度
	 * 
	 * @param height
	 */
	public void setDividerHeight(int height) {
		findViewById(R.id.user_center_divider1).getLayoutParams().height = height;
		findViewById(R.id.user_center_divider2).getLayoutParams().height = height;
		findViewById(R.id.user_center_divider3).getLayoutParams().height = height;
		findViewById(R.id.user_center_divider4).getLayoutParams().height = height;
		findViewById(R.id.user_center_divider5).getLayoutParams().height = height;
		findViewById(R.id.user_center_divider6).getLayoutParams().height = height;
	}
}
