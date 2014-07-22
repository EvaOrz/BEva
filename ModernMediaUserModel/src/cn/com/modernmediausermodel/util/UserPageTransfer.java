package cn.com.modernmediausermodel.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.CardDetailActivity;
import cn.com.modernmediausermodel.DefaultUserInfoActivity;
import cn.com.modernmediausermodel.FavoritesActivity;
import cn.com.modernmediausermodel.MessageActivity;
import cn.com.modernmediausermodel.MyHomePageActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.RecommendUserActivity;
import cn.com.modernmediausermodel.SquareActivity;
import cn.com.modernmediausermodel.UserCardInfoActivity;
import cn.com.modernmediausermodel.WriteCommentActivity;
import cn.com.modernmediausermodel.WriteNewCardActivity;
import cn.com.modernmediausermodel.help.UserHelper;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.model.User;

/**
 * 用户模块页面跳转
 * 
 * @author user
 * 
 */
public class UserPageTransfer {
	public static final String LOGIN_KEY = "login_key";// 登录完成跳转页面参数key
	public static final String USER_KEY = "user_key";// 传递user参数
	public static final String UID_KEY = "uid_key";// 传递用户id参数

	public static final int GOTO_HOME_PAGE = 1;// 登录完成进入我的首页
	public static final int GOTO_MESSAGE = 2;// 登录完成进入通知中心页
	public static final int TO_LOGIN_BY_WRITE = 1001;// 填写卡片前登录回来刷新页面
	public static final int AFTER_WRITE_CARD = 1002;// 填写卡片回来

	// 跳转页面需要的参数
	private static Message message;

	/**
	 * 是否登录
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isLogin(Context context) {
		return UserDataHelper.getUserLoginInfo(context) != null;
	}

	@SuppressWarnings("unused")
	private static void transfer(Context context, Class<?> cls, boolean anim) {
		transfer(context, cls, null, false, -1, anim);
	}

	private static void transfer(Context context, Class<?> cls, Bundle bundle,
			boolean anim) {
		transfer(context, cls, bundle, false, -1, anim);
	}

	private static void transfer(Context context, Class<?> cls, Bundle bundle,
			boolean finish, boolean anim) {
		transfer(context, cls, bundle, finish, -1, anim);
	}

	/**
	 * 跳转页面
	 * 
	 * @param context
	 * @param cls
	 *            跳转至的页面
	 * @param bundle
	 *            传递参数
	 * @param finish
	 *            是否关闭页面
	 * @param requestCode
	 *            !=-1，startActivityForResult
	 * @param rightIn
	 *            跳转页面执行右边进入动画
	 */
	private static void transfer(Context context, Class<?> cls, Bundle bundle,
			boolean finish, int requestCode, boolean rightIn) {
		Intent intent = new Intent(context, cls);
		if (bundle != null)
			intent.putExtras(bundle);
		if (requestCode == -1)
			context.startActivity(intent);
		else
			((Activity) context).startActivityForResult(intent, requestCode);
		if (finish)
			((Activity) context).finish();
	}

	/**
	 * 跳转至用户资料页面
	 */
	public static void gotoUserCardInfoActivity(Context context, User user,
			boolean finish) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(UserCardInfoActivity.KEY_USER, user);
		transfer(context, UserCardInfoActivity.class, bundle, finish, true);
	}

	/**
	 * 跳转至广场页面
	 */
	public static void gotoSquareActivity(Context context, boolean finish) {
		transfer(context, SquareActivity.class, null, finish, true);
	}

	/**
	 * 跳转至我的首页
	 * 
	 */
	public static void gotoMyHomePageActivity(Context context, boolean finish) {
		if (isLogin(context))
			transfer(context, MyHomePageActivity.class, null, finish, true);
		else
			gotoLoginActivity(context, GOTO_HOME_PAGE);
	}

	/**
	 * 跳转至消息页
	 * 
	 * @param mMessage
	 */
	public static void gotoMessageActivity(Context context, Message msg,
			boolean finish) {
		message = msg;
		if (!isLogin(context)) {
			gotoLoginActivity(context, GOTO_MESSAGE);
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable(MessageActivity.KEY_MESSAGE, message);
		transfer(context, MessageActivity.class, bundle, finish, true);
	}

	/**
	 * 跳转至卡片详情页面
	 */
	public static void gotoCardDetailActivity(Context context, Card card,
			int index) {
		if (card == null || card.getCardItemList().size() <= index)
			return;
		Bundle bundle = new Bundle();
		bundle.putSerializable(CardDetailActivity.KEY_CARD, card);
		bundle.putInt(CardDetailActivity.KEY_CARD_INDEX, index);
		transfer(context, CardDetailActivity.class, bundle, false,
				CardDetailActivity.REQUEST_CODE_SELF, true);
	}

	/**
	 * 跳转至卡片详情页面(消息页使用)
	 */
	public static void gotoCardDetailActivity(Context context, String cardId,
			boolean finish) {
		Bundle bundle = new Bundle();
		bundle.putBoolean(CardDetailActivity.KEY_IS_SINGLE, true);
		bundle.putString(CardDetailActivity.KEY_CARD_ID, cardId);
		transfer(context, CardDetailActivity.class, bundle, finish, true);
	}

	/**
	 * 跳转至用户列表界面(推荐用户、朋友、粉丝)
	 */
	public static void gotoUserListActivity(Context context, User user,
			int pageType, boolean finish) {
		Bundle bundle = new Bundle();
		if (user != null)
			bundle.putSerializable(USER_KEY, user);
		bundle.putInt(RecommendUserActivity.KEY_PAGE_TYPE, pageType);
		transfer(context, RecommendUserActivity.class, bundle, finish,
				UserCardInfoActivity.REQUEST_CODE_USER_LIST, true);
	}

	/**
	 * 跳转至收藏夹界面
	 */
	public static void gotoFavoritesActivity(Context context, Issue issue) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(FavoritesActivity.ISSUE_KEY, issue);
		transfer(context, FavoritesActivity.class, bundle, true);
	}

	/**
	 * 跳转至写评论界面
	 * 
	 * @param context
	 * @param cardId
	 * @param isShowToast
	 */
	public static void gotoWriteComment(Context context, int requestCode,
			String cardId, boolean isShowToast) {
		Bundle bundle = new Bundle();
		bundle.putString(WriteCommentActivity.KEY_CARD_ID, cardId);
		bundle.putBoolean(WriteCommentActivity.KEY_IS_SHOW_TOAST, isShowToast);
		transfer(context, WriteCommentActivity.class, bundle, false,
				requestCode, false);
		((Activity) context).overridePendingTransition(R.anim.down_in,
				R.anim.hold);
	}

	/**
	 * 跳转至写笔记页面
	 */
	public static void gotoWriteCardActivity(Context context, boolean finish) {
		if (!isLogin(context)) {
			gotoLoginActivityRequest(context, TO_LOGIN_BY_WRITE);
			return;
		}
		transfer(context, WriteNewCardActivity.class, null, finish,
				AFTER_WRITE_CARD, false);
		((Activity) context).overridePendingTransition(R.anim.down_in,
				R.anim.hold);
	}

	/**
	 * 跳转到写笔记页面
	 * 
	 * @param content
	 * @param appId
	 *            应用id，区分是第三方应用还是当前应用
	 */
	public static void gotoWriteCardActivity(Context context, String content,
			int appId, boolean finish) {
		if (TextUtils.isEmpty(content)) {
			((Activity) context).finish();
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putString(WriteNewCardActivity.KEY_FROM,
				WriteNewCardActivity.VALUE_SHARE);
		bundle.putString(WriteNewCardActivity.KEY_DATA, content);
		bundle.putInt(ConstData.SHARE_APP_ID, appId);
		transfer(context, WriteNewCardActivity.class, bundle, finish, false);
	}

	/**
	 * 跳转至登录页面
	 * 
	 * @param context
	 * @param gotoPage
	 *            需要跳转的页面
	 */
	public static void gotoLoginActivity(Context context, int gotoPage) {
		Bundle bundle = new Bundle();
		bundle.putInt(LOGIN_KEY, gotoPage);
		transfer(context, UserConstData.getLoginClass(), bundle, true);
	}

	/**
	 * 执行操作前需要登录
	 * 
	 * @param context
	 */
	public static void gotoLoginActivityRequest(Context context, int requestCode) {
		transfer(context, UserConstData.getLoginClass(), null, false,
				requestCode, true);
	}

	/**
	 * 登录成功,包括微博账号登录
	 * 
	 * @param user
	 * @param content
	 *            第三放分享内容
	 * @param appId
	 *            当前应用id
	 * @param gotoPage
	 *            需要跳转至的页面
	 */
	public static void afterLogin(Context context, User user, String content,
			int appId, int gotoPage) {
		if (user == null)
			return;
		// 如果是第三方应用分享笔记，则进入发表笔记页面
		if (!TextUtils.isEmpty(content)) {
			gotoWriteCardActivity(context, content, appId, true);
		} else {
			// 更新收藏文件夹
			List<FavoriteItem> list = FavDb.getInstance(context)
					.getUserUnUpdateFav(user.getUid(), false);
			if (ParseUtil.listNotNull(list)) {
				UserHelper.updateFav(context);
			} else {
				UserHelper.getFav(context);
			}
			// 如果是第一次登录，则进入推荐关注界面
			if (UserDataHelper.getIsFirstLogin(context, user.getUserName())) {
				UserDataHelper.saveIsFirstLogin(context, user.getUserName(),
						false);
				gotoUserListActivity(context, null,
						RecommendUserActivity.PAGE_RECOMMEND_FRIEND, true);
			} else {
				if (!checkAfterLogin(context, gotoPage)) {
					((Activity) context).finish();
				}
			}
		}
	}

	/**
	 * 判断登录成功后是否跳转页面
	 * 
	 * @param context
	 * @return
	 */
	private static boolean checkAfterLogin(Context context, int gotoPage) {
		if (gotoPage == GOTO_HOME_PAGE) {
			gotoMyHomePageActivity(context, true);
		} else if (gotoPage == GOTO_MESSAGE) {
			gotoMessageActivity(context, message, true);
		} else {
			return false;
		}
		return true;
	}

	/***
	 * 跳转至用户信息页面
	 * 
	 * @param context
	 * @param from
	 *            页面跳转起源（1：注册；2：新浪微博登录；其它为0）
	 * @param content
	 *            第三方应用或者本应用分享的内容
	 * @param gotoPage
	 *            需要跳转的页面
	 */
	public static void gotoUserInfoActivity(Context context, int from,
			String content, int gotoPage) {
		if (!isLogin(context)) {
			gotoLoginActivity(context, 0);
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putInt(DefaultUserInfoActivity.KEY_ACTION_FROM, from);
		if (!TextUtils.isEmpty(content)) {
			bundle.putString(WriteNewCardActivity.KEY_DATA, content);
		}
		bundle.putInt(LOGIN_KEY, gotoPage);
		transfer(context, UserConstData.getUserInfoClass(), bundle, from != 0,
				true);
	}
}
