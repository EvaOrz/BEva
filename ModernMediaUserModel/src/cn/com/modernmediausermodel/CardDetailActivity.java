package cn.com.modernmediausermodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import cn.com.modernmedia.BaseActivity;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.adapter.CardDetailPagerAdapter;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.MultiComment;
import cn.com.modernmediausermodel.model.MultiComment.Comment;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.Users;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.util.UserPageTransfer;
import cn.com.modernmediausermodel.util.UserTools;

public class CardDetailActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_CARD = "card"; // 卡片数据
	public static final int REQUEST_CODE_COMMENT = 110; // 评论界面requescode
	public static final int REQUEST_CODE_SELF = 111; // 启动当前界面的request code
	public static final int REQUEST_ADD_FAV = 112;// 收藏
	public static final int REQUEST_WRITE_COMMENT = 113;// 写评论
	public static final String KEY_CARD_INDEX = "card_index"; // 选择的卡片在数组中得位置
	public static final String KEY_IS_SINGLE = "key_is_single"; // 是否是单个卡片详情
	public static final String KEY_CARD_ID = "card_id";
	private ImageView backBtn, deleteBtn, favBtn, shareBtn;
	private ViewPager viewPager;
	private CardDetailPagerAdapter pagerAdapter;
	private Button writeComment;
	private UserOperateController controller;
	private List<CardItem> cardItemList;
	private Card mCard;
	private MultiComment mComments = new MultiComment();
	private int index; // 当前显示页面
	private boolean isDataModified = false; // 卡片数据是否修改，若有修改，则通知上页刷新数据
	private String cardId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_detail);
		initDataFromBundle();
	}

	private void initDataFromBundle() {
		controller = UserOperateController.getInstance(this);
		if (getIntent() != null && getIntent().getExtras() != null) {
			boolean isSingle = getIntent().getExtras().getBoolean(
					KEY_IS_SINGLE, false);
			if (isSingle) {
				// 如果从通知页面过来，则需取得该页面的所需的card及确定通知卡片所处的位置
				cardId = getIntent().getExtras().getString(KEY_CARD_ID);
				getCardDetail();
			} else {
				Card card = (Card) getIntent().getExtras().getSerializable(
						KEY_CARD);
				this.mCard = card;
				if (card != null) {
					this.cardItemList = card.getCardItemList();
				}
				this.index = getIntent().getExtras().getInt(KEY_CARD_INDEX);
				init();
			}

		}
	}

	private void init() {
		backBtn = (ImageView) findViewById(R.id.card_detail_nav_back);
		deleteBtn = (ImageView) findViewById(R.id.card_detail_nav_delete);
		favBtn = (ImageView) findViewById(R.id.card_detail_nav_fav);
		shareBtn = (ImageView) findViewById(R.id.card_detail_nav_share);
		viewPager = (ViewPager) findViewById(R.id.card_detail_viewpager);
		writeComment = (Button) findViewById(R.id.card_detail_page_write_comment);
		pagerAdapter = new CardDetailPagerAdapter(this);
		viewPager.setAdapter(pagerAdapter);

		backBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		favBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		writeComment.setOnClickListener(this);
		setNavBtnStatus();

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				index = arg0;
				setNavBtnStatus();
				viewPager.setCurrentItem(arg0, false);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		getComments(0, false);
	}

	/**
	 * 设置收藏按钮的背景
	 * 
	 * @param isFav
	 */
	public void setFavBtnBackground(int isFav) {
		if (isFav == 0) {
			favBtn.setImageResource(R.drawable.nav_un_fav);
		} else {
			favBtn.setImageResource(R.drawable.nav_has_fav);
		}
	}

	/**
	 * 设置导航栏上功能按钮的状态
	 */
	public void setNavBtnStatus() {
		if (ParseUtil.listNotNull(cardItemList) && cardItemList.size() > index) {
			CardItem item = cardItemList.get(index);
			// 当前卡片被删除时，则给用户提示，同时隐藏删除、分享按钮
			if (item.getIsDel() == 1) {
				deleteBtn.setVisibility(View.GONE);
				shareBtn.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);
			} else {
				// 显示分享按钮
				shareBtn.setVisibility(View.VISIBLE);
				// 当前显示的card为登录用户时，隐藏收藏功能；反之，则隐藏删除功能
				String uid = item.getType() == 2 ? item.getFuid() : item
						.getUid();
				if (uid.equals(UserTools.getUid(this))) {
					favBtn.setVisibility(View.GONE);
					deleteBtn.setVisibility(View.VISIBLE);
				} else {
					deleteBtn.setVisibility(View.GONE);
					favBtn.setVisibility(View.VISIBLE);
					// 确定收藏状态，显示相应的图标
					setFavBtnBackground(item.getIsFav());
				}
			}
		}
	}

	/**
	 * 获取评论
	 */
	public void getComments(int commentId, final boolean isGetMore) {
		if (!isGetMore)
			showLoadingDialog(true);
		ArrayList<CardItem> items = new ArrayList<Card.CardItem>();
		if (isGetMore) {
			items.add(cardItemList.get(index));
		} else {
			items.addAll(cardItemList);
		}
		controller.getCardComments(items, commentId, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (!isGetMore)
							showLoadingDialog(false);
						if (entry instanceof MultiComment) {
							if (isGetMore) {
								MultiComment multiComment = (MultiComment) entry;
								if (ParseUtil.listNotNull(multiComment
										.getCommentList())) {
									Comment moreComment = multiComment
											.getCommentList().get(0);
									mComments
											.getCommentList()
											.get(index)
											.getCommentItemList()
											.addAll(moreComment
													.getCommentItemList());
								}
							} else {
								mComments = (MultiComment) entry;
							}
						}
						checkCommonsIsNull();
						getUsersInfo(isGetMore);
					}
				});
	}

	/**
	 * 如果评论为空时，初始化每个card的评论列表
	 */
	private void checkCommonsIsNull() {
		if (!ParseUtil.listNotNull(mComments.getCommentList())) {
			for (int i = 0; i < cardItemList.size(); i++) {
				mComments.getCommentList().add(new Comment());
			}
		}
	}

	/**
	 * 获得所有评论者的用户信息
	 * 
	 * @param users
	 */
	private void getUsersInfo(final boolean isGetMore) {
		if (!isGetMore)
			showLoadingDialog(true);
		Map<String, User> map = mComments.getUserInfoMap();
		Set<String> set = new HashSet<String>();
		for (Comment comment : mComments.getCommentList()) {
			if (ParseUtil.listNotNull(comment.getCommentItemList())) {
				for (CommentItem item : comment.getCommentItemList()) {
					if (map.get(item.getUid()) == null) {
						set.add(item.getUid());
					}
				}
			}
		}
		if (set.size() > 0) {
			controller.getUsersInfo(set, new UserFetchEntryListener() {

				@Override
				public void setData(Entry entry) {
					if (!isGetMore)
						showLoadingDialog(false);
					if (entry instanceof Users) {
						Users users = (Users) entry;
						// 将登录用户自己的信息存入map中
						User curLoginUser = UserDataHelper
								.getUserLoginInfo(CardDetailActivity.this);
						Map<String, User> userInfos = users.getUserInfoMap();
						if (curLoginUser != null
								&& !userInfos.containsKey(curLoginUser.getUid())) {
							userInfos.put(curLoginUser.getUid(), curLoginUser);
						}
						mComments.setUserInfoMap(userInfos);
					}
					if (isGetMore) { // 重新加载当前页的评论列表
						pagerAdapter.getCurrentCardDetatilItem().setData(
								mComments.getCommentList().get(index),
								mComments.getUserInfoMap());
					} else {
						pagerAdapter.setPagerData(mCard, mComments);
						viewPager.setCurrentItem(index, false);
					}
				}
			});
		} else {
			// 没有评论且不是加载更多数据时
			if (!isGetMore) {
				pagerAdapter.setPagerData(mCard, mComments);
				viewPager.setCurrentItem(index, false);
			}
			showLoadingDialog(false);
		}
	}

	/**
	 * ' 给卡片添加一条评论
	 * 
	 * @param item
	 */
	private void addComment(CommentItem item) {
		// 当前所在的卡片
		if (mComments.getCommentList().size() <= index)
			return;
		// 将登录用户自己的信息存入map中
		User curLoginUser = UserDataHelper.getUserLoginInfo(this);
		if (!mComments.getUserInfoMap().containsKey(curLoginUser.getUid())) {
			mComments.getUserInfoMap().put(curLoginUser.getUid(), curLoginUser);
		}

		Comment comment = mComments.getCommentList().get(index);
		comment.getCommentItemList().add(0, item);
		// pagerAdapter.getCurrentCardDetatilItem().addComment(item);
		pagerAdapter.getCurrentCardDetatilItem().setData(comment,
				mComments.getUserInfoMap());
	}

	/**
	 * 添加卡片收藏
	 * 
	 * @param uid
	 * @param cardId
	 */
	public void addCardFav(String uid, String cardId) {
		showLoadingDialog(true);
		controller.addCardFav(uid, cardId, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				showLoadingDialog(false);
				if (entry instanceof User.Error) {
					User.Error error = (User.Error) entry;
					if (error.getNo() == 0) {
						isDataModified = true;
						cardItemList.get(index).setIsFav(1);
						setFavBtnBackground(1);
					} else {
						showToast(error.getDesc());
					}
				}
			}
		});
	}

	/**
	 * 取消卡片收藏
	 * 
	 * @param uid
	 * @param cardId
	 */
	public void deleteCardFav(String uid, String cardId) {
		showLoadingDialog(true);
		controller.cancelCardFav(uid, cardId, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				showLoadingDialog(false);
				if (entry instanceof User.Error) {
					User.Error error = (User.Error) entry;
					if (error.getNo() == 0) {
						isDataModified = true;
						cardItemList.get(index).setIsFav(0);
						setFavBtnBackground(0);
					} else {
						showToast(error.getDesc());
					}
				}
			}
		});
	}

	/**
	 * 删除卡片
	 * 
	 * @param uid
	 * @param cardId
	 */
	public void deleteCard(String uid, String cardId) {
		showLoadingDialog(true);
		controller.deleteCard(uid, cardId, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				showLoadingDialog(false);
				if (entry instanceof User.Error) {
					User.Error error = (User.Error) entry;
					if (error.getNo() == 0) {
						isDataModified = true;
						mCard.getCardItemList().remove(index);
						cardItemList = mCard.getCardItemList(); // 状态栏按钮设置用
						setNavBtnStatus(); //更新状态栏按钮
						if (mCard.getCardItemList().size() != 0) { // 不是删除最后一个
							pagerAdapter.setPagerData(mCard, mComments);
							pagerAdapter.notifyDataSetChanged();
							viewPager.setCurrentItem(index);
						} else {
							finish();
						}
					} else {
						showToast(R.string.delete_card_failed);
					}
				}
			}
		});
	}

	/**
	 * 获取单张卡片详情
	 * 
	 */
	private void getCardDetail() {
		if (TextUtils.isEmpty(cardId))
			return;
		showLoadingDialog(true);
		controller.getCardDetail(cardId, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				showLoadingDialog(false);
				if (entry instanceof Card) {
					Card card = (Card) entry;
					if (card.getError().getNo() == 0) {
						mCard = card;
						cardItemList = mCard.getCardItemList();
						doAfterGetUserCard(cardId);
					} else {
						showLoadingDialog(false);
					}
				} else {
					showLoadingDialog(false);
				}
			}
		});
	}

	private void doAfterGetUserCard(String cardId) {
		if (mCard == null)
			return;
		// 取得该用户卡片对应的用户信息
		Set<String> set = new HashSet<String>();
		for (CardItem item : mCard.getCardItemList()) {
			if (item.getType() == 2) {
				set.add(item.getFuid());
			} else {
				set.add(item.getUid());
			}
		}
		controller.getUsersInfo(set, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				showLoadingDialog(false);
				if (entry instanceof Users) {
					Users users = (Users) entry;
					mCard.setUserInfoMap(users.getUserInfoMap());
					init();
				}
			}
		});
	}

	@Override
	public void reLoadData() {
	}

	@Override
	public String getActivityName() {
		return CardDetailActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.card_detail_nav_back) {
			finish();
			return;
		}
		if (!ParseUtil.listNotNull(cardItemList)
				|| cardItemList.size() <= index) {
			return;
		}
		if (view.getId() == R.id.card_detail_nav_share) { // 分享
			CardItem cardItem = cardItemList.get(index);
			String content = cardItem.getContents();
			UserTools.shareCard(this, content);
		} else {
			if (view.getId() == R.id.card_detail_nav_delete) { // 删除
				deleteCard(UserTools.getUid(CardDetailActivity.this),
						cardItemList.get(index).getId() + "");
			} else if (view.getId() == R.id.card_detail_nav_fav) { // 收藏、取消收藏
				if (checkLogin(REQUEST_ADD_FAV)) {
					changeFavStatus();
				}
			} else if (view.getId() == R.id.card_detail_page_write_comment) {
				// TODO 写评论
				if (checkLogin(REQUEST_WRITE_COMMENT)) {
					gotoWriteCommentAtivity();
				}
			}
		}
	}

	/**
	 * 判断是否登录
	 * 
	 * @param requestCode
	 * @return
	 */
	private boolean checkLogin(int requestCode) {
		if (UserDataHelper.getUserLoginInfo(this) == null) {
			UserPageTransfer.gotoLoginActivityRequest(this, requestCode);
			return false;
		}
		return true;
	}

	private void changeFavStatus() {
		String uid = UserTools.getUid(this);
		CardItem cardItem = cardItemList.get(index);
		if (cardItem.getIsFav() == 0) { // 未收藏
			addCardFav(uid, cardItem.getId() + "");
		} else {
			deleteCardFav(uid, cardItem.getId() + "");
		}
	}

	/**
	 * 跳转至写评论页面
	 */
	private void gotoWriteCommentAtivity() {
		if (cardItemList.size() > index) {
			String cardId = cardItemList.get(index).getId();
			UserPageTransfer.gotoWriteComment(this, REQUEST_CODE_COMMENT,
					cardId, false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_COMMENT && data != null) {
				if (data.getExtras().getSerializable(
						WriteCommentActivity.RETURN_DATA) instanceof CommentItem)
					addComment((CommentItem) data.getExtras().getSerializable(
							WriteCommentActivity.RETURN_DATA));
			} else if (requestCode == REQUEST_ADD_FAV) {
				changeFavStatus();
			} else if (requestCode == REQUEST_WRITE_COMMENT) {
				gotoWriteCommentAtivity();
			}
		}
	}

	@Override
	public void finish() {
		if (isDataModified) {
			setResult(RESULT_OK);
		} else {
			setResult(RESULT_CANCELED);
		}
		super.finish();
	}
}
