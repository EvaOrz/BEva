package cn.com.modernmediausermodel.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.com.modernmedia.MusicActivity;
import cn.com.modernmedia.zxing.activity.CaptureActivity;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediaslate.unit.FavObservable;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.ArticleCardListActivity;
import cn.com.modernmediausermodel.CardDetailActivity;
import cn.com.modernmediausermodel.MessageActivity;
import cn.com.modernmediausermodel.ModifyPwdActivity;
import cn.com.modernmediausermodel.MyCoinActivity;
import cn.com.modernmediausermodel.MyCoinUseNoticeActivity;
import cn.com.modernmediausermodel.MyHomePageActivity;
import cn.com.modernmediausermodel.R;
import cn.com.modernmediausermodel.RecommendUserActivity;
import cn.com.modernmediausermodel.SettingActivity;
import cn.com.modernmediausermodel.SquareActivity;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.UserCardInfoActivity;
import cn.com.modernmediausermodel.UserInfoActivity;
import cn.com.modernmediausermodel.WriteCommentActivity;
import cn.com.modernmediausermodel.WriteNewCardActivity;
import cn.com.modernmediausermodel.model.Card;
import cn.com.modernmediausermodel.model.Message;
import cn.com.modernmediausermodel.widget.MessageView;
import cn.com.modernmediausermodel.widget.RecommendUserView;
import cn.com.modernmediausermodel.widget.UserCardView;

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
	public static final int GOTO_MY_COIN = 3;// 登录完成进入我的金币页
	public static final int GOTO_BUSINESS_NOTE = 4;// 登录完成进入商业笔记页
	public static final int GOTO_MY_FAV = 5;// 登录完成进入我的收藏页
	public static final int TO_LOGIN_BY_WRITE = 1001;// 填写卡片前登录回来刷新页面
	public static final int AFTER_WRITE_CARD = 1002;// 填写卡片回来
	public static final int TO_LOGIN_BY_ARTICLE_CARD = 1003;// 查看文章摘要，未登录状态下进入登录页的请求code

	// 跳转页面需要的参数
	private static Message message;

	/**
	 * 是否登录
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isLogin(Context context) {
		return SlateDataHelper.getUserLoginInfo(context) != null;
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
		if (cls == UserConstData.getLoginClass()) {
			((Activity) context).overridePendingTransition(
					R.anim.activity_open_enter, R.anim.activity_open_exit);
		}
	}

	/**
	 * 跳转至商业笔记页面
	 */
	public static void gotoUserCardInfoActivity(Context context, User user,
			boolean finish) {
		if (isLogin(context)) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(UserCardView.KEY_USER, user);
			transfer(context, UserCardInfoActivity.class, bundle, finish, true);
		} else
			gotoLoginActivity(context, GOTO_BUSINESS_NOTE);
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
	 * 跳转至设置页面
	 * 
	 */
	public static void gotoSettingActivity(Context context, boolean finish) {
		transfer(context, SettingActivity.class, null, finish, true);
	}

	/**
	 * 跳转至电台页面
	 * 
	 */
	public static void gotoMusicActivity(Context context, boolean finish) {
		transfer(context, MusicActivity.class, null, finish, false);
	}

	/**
	 * 跳转至扫一扫页面
	 * 
	 */
	public static void gotoScanActivity(Context context, boolean finish) {
		transfer(context, CaptureActivity.class, null, finish, false);
	}

	/**
	 * 跳转至消息页
	 * 
	 * @param msg
	 */
	public static void gotoMessageActivity(Context context, Message msg,
			boolean finish) {
		message = msg;
		if (!isLogin(context)) {
			gotoLoginActivity(context, GOTO_MESSAGE);
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable(MessageView.KEY_MESSAGE, message);
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
		bundle.putInt(RecommendUserView.KEY_PAGE_TYPE, pageType);
		transfer(context, RecommendUserActivity.class, bundle, finish,
				UserCardView.REQUEST_CODE_USER_LIST, true);
	}

	/**
	 * 跳转至收藏夹界面
	 */
	public static void gotoFavoritesActivity(Context context) {
		// if (isLogin(context)) {
		Bundle bundle = new Bundle();
		if (UserApplication.favActivity != null)
			transfer(context, UserApplication.favActivity, bundle, false, true);
		// } else
		// gotoLoginActivity(context, GOTO_MY_FAV);

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
	 */
	public static void gotoWriteCardActivity(Context context, String content,
			boolean finish) {
		if (TextUtils.isEmpty(content)) {
			((Activity) context).finish();
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putString(WriteNewCardActivity.KEY_FROM,
				WriteNewCardActivity.VALUE_SHARE);
		bundle.putString(WriteNewCardActivity.KEY_DATA, content);
		transfer(context, WriteNewCardActivity.class, bundle, finish, false);
		((Activity) context).overridePendingTransition(R.anim.down_in,
				R.anim.hold);
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
	 * @param gotoPage
	 *            需要跳转至的页面
	 */
	public static void afterLogin(Context context, User user, String content,
			int gotoPage) {
		if (user == null)
			return;
		SlateApplication.loginStatusChange = true;
		// 如果是第三方应用分享笔记，则进入发表笔记页面
		if (!TextUtils.isEmpty(content)) {
			gotoWriteCardActivity(context, content, true);
		} else {
			// 更新收藏文件夹
			SlateApplication.favObservable.setData(FavObservable.AFTER_LOGIN);
			// 如果是第一次登录，则进入推荐关注界面
			if (UserDataHelper.getIsFirstLogin(context, user.getUserName())) {
				UserDataHelper.saveIsFirstLogin(context, user.getUserName(),
						false);
				gotoUserListActivity(context, null,
						RecommendUserView.PAGE_RECOMMEND_FRIEND, false);
			} else {
				if (UserApplication.afterLoginListener != null) {
					UserApplication.afterLoginListener.doAfterLogin(gotoPage);
					((Activity) context).finish();
				} else if (!checkAfterLogin(context, gotoPage)) {
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
		} else if (gotoPage == GOTO_MY_COIN) {
			gotoMyCoinActivity(context, true, false);
		} else if (gotoPage == GOTO_BUSINESS_NOTE) {
			gotoUserCardInfoActivity(context,
					SlateDataHelper.getUserLoginInfo(context), false);
		} else if (gotoPage == GOTO_MY_FAV) {
			gotoFavoritesActivity(context);
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
	 *            页面跳转起源（1：邮箱注册；2：新浪微博登录；3: QQ登录；4：手机注册；5:微信登录；其它为0）
	 * @param content
	 *            第三方应用或者本应用分享的内容
	 * @param gotoPage
	 *            需要跳转的页面
	 */
	public static void gotoUserInfoActivity(Context context, int from,
			String content, String password, int gotoPage) {
		Bundle bundle = new Bundle();
		bundle.putInt(UserInfoActivity.KEY_ACTION_FROM, from);
		if (!TextUtils.isEmpty(content)) {
			bundle.putString(WriteNewCardActivity.KEY_DATA, content);
		}
		bundle.putInt(LOGIN_KEY, gotoPage);
		bundle.putString(UserInfoActivity.PASSEORD, password);

		transfer(context, UserConstData.getUserInfoClass(), bundle, from != 0,
				true);
	}

	/**
	 * 跳转至修改密码页面
	 * 
	 * @param context
	 */
	public static void gotoModifyPasswordActivity(Context context) {
		transfer(context, ModifyPwdActivity.class, null, false, true);
	}

	/**
	 * 跳转到商城须知页面
	 */
	public static void gotoMyCoinUseNoticeActivity(Context context) {
		if (isLogin(context))
			transfer(context, MyCoinUseNoticeActivity.class, null, false, true);
		else
			gotoLoginActivity(context, 0);
	}

	/**
	 * 跳转到我的金币页面
	 * 
	 * @param context
	 * @param isFinish
	 * @param isFromNotice
	 */
	public static void gotoMyCoinActivity(Context context, boolean isFinish,
			boolean isFromNotice) {
		if (isLogin(context)) {
			// 如果是第一次进入并且不是从说明页而来，则进入商城使用说明页面
			String uid = Tools.getUid(context);
			if (!TextUtils.isEmpty(uid)
					&& UserDataHelper.getIsFirstUseCoin(context, uid)
					&& !isFromNotice) {
				UserPageTransfer.gotoMyCoinUseNoticeActivity(context);
			} else {
				transfer(context, MyCoinActivity.class, null, isFinish, true);
			}
		} else
			gotoLoginActivity(context, UserPageTransfer.GOTO_MY_COIN);
	}

	/**
	 * 前往文章摘要与评论页面
	 * 
	 * @param context
	 * @param issueId
	 * @param articleId
	 */
	public static void gotoArticleCardActivity(Context context, String articleId) {
		if (!isLogin(context)) {
			gotoLoginActivityRequest(context, TO_LOGIN_BY_ARTICLE_CARD);
			return;
		}
		if (TextUtils.isEmpty(articleId)) {
			((Activity) context).finish();
			return;
		}
		Bundle bundle = new Bundle();
		bundle.putString(ArticleCardListActivity.KEY_ARTICLE_ID, articleId);
		transfer(context, ArticleCardListActivity.class, bundle, false, false);
	}
}
