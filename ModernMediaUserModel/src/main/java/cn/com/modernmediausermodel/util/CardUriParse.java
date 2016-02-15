package cn.com.modernmediausermodel.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.listener.CardUriListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;
import cn.com.modernmediausermodel.widget.RecommendUserView;

/**
 * 卡片uri解析
 * 
 * @author user
 * 
 */
public class CardUriParse {
	public static final String REGISTER = "register"; // 注册
	public static final String LOGIN = "login"; // 登录
	public static final String PASSWORD = "password"; // 修改密码
	public static final String DETAIL = "detail"; // 卡片详情
	public static final String ADD = "add"; // 添加卡片
	public static final String DELETE = "delete"; // 删除卡片
	public static final String FAVORITE = "addfavorite"; // 收藏
	public static final String UNFAVORITE = "deletefavorite"; // 取消收藏
	public static final String ADDCOMMENT = "addcomment"; // 添加评论
	public static final String FOLLOW = "follow"; // 关注某人
	public static final String UNFOLLOW = "unfollow"; // 取消关注某人
	public static final String USERCARDLIST = "cards";
	public static final String TIMELINE = "timeline";
	public static final String FOLLOWS = "follows"; // 关注列表
	public static final String FOLLOWERS = "followers"; // 粉丝列表
	public static final String ARTICLECARDS = "articlecards"; // 文章的相关卡片
	public static final String RECOMMENDCARDS = "recommendcards"; // 热门卡片列表
	public static final String RECOMMENDUSERS = "recommendusers"; // 热门用户列表

	private CardUriListener listener = new CardUriListener() {

		@Override
		public void doCardUri(Context context, String link) {
			parse(context, link);
		}
	};

	private static CardUriParse instance = null;

	private CardUriParse() {
		SlateApplication.userUriListener = listener;
	}

	public static CardUriParse getInstance() {
		if (instance == null || SlateApplication.userUriListener == null)
			instance = new CardUriParse();
		return instance;
	}

	/**
	 * 解析
	 * 
	 * @param link
	 */
	private void parse(Context context, String link) {
		String arr[] = link.split("/");
		if (arr == null || arr.length < 1)
			return;
		if (arr[0].equals(DETAIL)) {
			// 打开某个卡片详情
			UserPageTransfer.gotoCardDetailActivity(context, arr[1], false);
		} else if (arr[0].equals(REGISTER) || arr[0].equals(LOGIN)) { // 注册/登录
			UserPageTransfer.gotoLoginActivity(context, 0);
		} else if (arr[0].equals(RECOMMENDCARDS)) {
			UserPageTransfer.gotoSquareActivity(context, false);
		} else if (SlateDataHelper.getUserLoginInfo(context) == null) {// 未登录
			UserPageTransfer.gotoLoginActivity(context, 0);
		} else if (arr[0].equals(PASSWORD)) {
			UserPageTransfer.gotoModifyPasswordActivity(context);
		} else if (arr.length < 2) {
			return;
		} else if (arr[0].equals(ADD)) {
			doAddCard(context, arr[1]);
		} else if (arr[0].equals(DELETE)) {
			doDeleteCard(context, arr[1]);
		} else if (arr[0].equals(FAVORITE)) {
			doAddFavortite(context, arr[1]);
		} else if (arr[0].equals(UNFAVORITE)) {
			doUnFavorite(context, arr[1]);
		} else if (arr[0].equals(ADDCOMMENT)) {
			UserPageTransfer.gotoWriteComment(context, -1, arr[1], true);
		} else if (arr[0].equals(FOLLOW)) {
			doFollow(context, arr[1]);
		} else if (arr[0].equals(UNFOLLOW)) {
			doUnFollow(context, arr[1]);
		} else if (arr[0].equals(TIMELINE)) { // 只支持前往当前登录用户的首页
			UserPageTransfer.gotoMyHomePageActivity(context, false);
		} else if (arr[0].equals(USERCARDLIST)) {
			doUserCardInfoList(context, arr[1]);
		} else if (arr[0].equals(FOLLOWS)) {
			doUserLists(context, arr[1], RecommendUserView.PAGE_FRIEND);
		} else if (arr[0].equals(FOLLOWERS)) {
			doUserLists(context, arr[1], RecommendUserView.PAGE_FANS);
		} else if (arr[0].equals(ARTICLECARDS)) {
			UserPageTransfer.gotoArticleCardActivity(context, arr[1]);
		} else if (arr[0].equals(RECOMMENDUSERS)) {
			doUserLists(context, arr[1],
					RecommendUserView.PAGE_RECOMMEND_FRIEND);
		}
	}

	/**
	 * 添加卡片
	 * 
	 * @param context
	 * @param data
	 */
	private void doAddCard(final Context context, String data) {
		if (TextUtils.isEmpty(data)) {
			Tools.showToast(context, R.string.card_not_allow_null_toast);
			return;
		}
		UserOperateController.getInstance(context).addCard(data,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof ErrorMsg) {
							if (((ErrorMsg) entry).getNo() == 0) {
								Tools.showToast(context,
										R.string.card_add_success);
								return;
							}
						}
						Tools.showToast(context, R.string.card_add_failed_toast);
					}
				});
	}

	/**
	 * 删除卡片
	 * 
	 * @param context
	 * @param cardId
	 */
	private void doDeleteCard(final Context context, String cardId) {
		UserOperateController.getInstance(context).deleteCard(
				Tools.getUid(context), cardId, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof ErrorMsg) {
							if (((ErrorMsg) entry).getNo() == 0) {
								Tools.showToast(context,
										R.string.delete_card_success);
								return;
							}
						}
						Tools.showToast(context, R.string.delete_card_failed);
					}
				});
	}

	/**
	 * 收藏卡片
	 * 
	 * @param context
	 * @param cardId
	 */
	private void doAddFavortite(final Context context, String cardId) {
		UserOperateController.getInstance(context).addCardFav(
				Tools.getUid(context), cardId, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof ErrorMsg) {
							if (((ErrorMsg) entry).getNo() == 0) {
								Tools.showToast(context,
										R.string.collect_success);
								return;
							}
						}
						Tools.showToast(context, R.string.collect_failed);
					}
				});
	}

	/**
	 * 取消收藏卡片
	 * 
	 * @param context
	 * @param cardId
	 */
	private void doUnFavorite(final Context context, String cardId) {
		UserOperateController.getInstance(context).cancelCardFav(
				Tools.getUid(context), cardId, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof ErrorMsg) {
							if (((ErrorMsg) entry).getNo() == 0) {
								Tools.showToast(context,
										R.string.uncollect_success);
								return;
							}
						}
						Tools.showToast(context, R.string.uncollect_failed);
					}
				});
	}

	/**
	 * 关注某用户
	 * 
	 * @param context
	 * @param uid
	 */
	private void doFollow(final Context context, String uid) {
		List<UserCardInfo> list = new ArrayList<UserCardInfo>();
		UserCardInfo userCardInfo = new UserCardInfo();
		userCardInfo.setUid(uid);
		list.add(userCardInfo);
		UserOperateController.getInstance(context).addFollow(
				Tools.getUid(context), list, false,
				new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof ErrorMsg) {
							if (((ErrorMsg) entry).getNo() == 0) {
								Tools.showToast(context,
										R.string.follow_success);
								return;
							}
						}
						Tools.showToast(context, R.string.follow_failed);
					}
				});
	}

	/**
	 * 取消关注某用户
	 * 
	 * @param context
	 * @param uid
	 */
	private void doUnFollow(final Context context, String uid) {
		List<UserCardInfo> list = new ArrayList<UserCardInfo>();
		UserCardInfo userCardInfo = new UserCardInfo();
		userCardInfo.setUid(uid);
		list.add(userCardInfo);
		UserOperateController.getInstance(context).deleteFollow(uid, list,
				false, new UserFetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof ErrorMsg) {
							if (((ErrorMsg) entry).getNo() == 0) {
								Tools.showToast(context,
										R.string.unfollow_success);
								return;
							}
						}
						Tools.showToast(context, R.string.unfollow_failed);
					}
				});
	}

	/**
	 * 打开用户信息及卡片列表
	 * 
	 * @param context
	 * @param uid
	 */
	private void doUserCardInfoList(final Context context, String uid) {
		UserOperateController controller = UserOperateController
				.getInstance(context);
		Set<String> sets = new HashSet<String>();
		sets.add(uid);
		controller.getUsersInfo(sets, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof User) {
					UserPageTransfer.gotoUserCardInfoActivity(context,
							(User) entry, false);
				}
			}
		});
	}

	/**
	 * 打开推荐关注、朋友、粉丝页
	 * 
	 * @param context
	 * @param uid
	 * @param type
	 */
	private void doUserLists(final Context context, String uid, final int type) {
		UserOperateController controller = UserOperateController
				.getInstance(context);
		Set<String> sets = new HashSet<String>();
		sets.add(uid);
		controller.getUsersInfo(sets, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof User) {
					UserPageTransfer.gotoUserListActivity(context,
							(User) entry, type, false);
				}
			}
		});
	}

}
