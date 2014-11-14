package cn.com.modernmediausermodel.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.UserApplication;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;

/**
 * 接口控制
 * 
 * @author ZhuQiao
 * 
 */
public class UserOperateController {
	private static UserOperateController instance;
	private static Context mContext;

	private Handler mHandler = new Handler();

	private UserOperateController(Context context) {
		mContext = context;
	}

	public static synchronized UserOperateController getInstance(Context context) {
		mContext = context;
		if (instance == null)
			instance = new UserOperateController(context);
		return instance;
	}

	/**
	 * 通过handler返回数据
	 * 
	 * @param entry
	 * @param listener
	 */
	private void sendMessage(final Entry entry,
			final UserFetchEntryListener listener) {
		synchronized (mHandler) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					listener.setData(entry);
				}
			});
		}
	}

	/**
	 * 用户登录
	 * 
	 * @param userName
	 *            用户名称
	 * @param password
	 *            密码
	 * @param listener
	 *            view数据回调接口
	 */
	public void login(String userName, String password,
			final UserFetchEntryListener listener) {
		final UserLoginOperate operate = new UserLoginOperate(userName,
				password);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 开放平台(新浪微博、QQ等)账号登录
	 * 
	 * @param user
	 *            用户信息
	 * @param avatar
	 *            服务器相对地址
	 * @param type
	 *            平台类型,目前0:普通登录；1：新浪微博；2：腾讯qq
	 * @param listener
	 *            view数据回调接口
	 */
	public void openLogin(User user, String avatar, int type,
			final UserFetchEntryListener listener) {
		final OpenLoginOperate operate = new OpenLoginOperate(mContext, user,
				avatar, type);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 用户注册
	 * 
	 * @param userName
	 *            用户名称
	 * @param password
	 *            密码
	 * @param listener
	 *            view数据回调接口
	 */
	public void register(String userName, String password,
			final UserFetchEntryListener listener) {
		final UserRegisterOperate operate = new UserRegisterOperate(userName,
				password);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 用户登出
	 * 
	 * @param uid
	 *            uid
	 * @param token
	 *            用户token
	 * @param listener
	 *            view数据回调接口
	 */
	public void loginOut(String uid, String token,
			final UserFetchEntryListener listener) {

		final UserLoginOutOperate operate = new UserLoginOutOperate(uid, token);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 获取当前用户的信息 通过uid和token获取当前用户信息，uid和token用pb格式post方式接收(可检测用户登录状态) url
	 * 
	 * @param uid
	 *            uid
	 * @param token
	 *            用户token
	 * @param listener
	 *            view数据回调接口
	 */
	public void getInfoByIdAndToken(String uid, String token,
			final UserFetchEntryListener listener) {
		final GetUserInfoOperate operate = new GetUserInfoOperate(uid, token);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 忘记密码
	 * 
	 * @param uid
	 *            uid
	 * @param token
	 *            用户token
	 * @param listener
	 *            view数据回调接口
	 */
	public void getPassword(String userName,
			final UserFetchEntryListener listener) {
		final UserFindPasswordOperate operate = new UserFindPasswordOperate(
				userName);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 修改用户资料
	 * 
	 * @param uid
	 *            uid
	 * @param token
	 *            用户token
	 * @param userName
	 *            用户名
	 * @param nickName
	 *            昵称
	 * @param url
	 *            图片的相对地址(通过上传头像获得)
	 * @param password
	 *            用户登录密码
	 * @param listener
	 *            view数据回调接口
	 */
	public void modifyUserInfo(String uid, String token, String userName,
			String nickName, String url, String password,
			final UserFetchEntryListener listener) {
		final ModifyUserInfoOperate operate = new ModifyUserInfoOperate(uid,
				token, userName, nickName, url, password);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 修改用户密码
	 * 
	 * @param uid
	 *            uid
	 * @param token
	 *            用户token
	 * @param userName
	 *            用户名
	 * @param password
	 *            旧密码
	 * @param newPassword
	 *            新密码
	 * @param listener
	 *            view数据回调接口
	 */
	public void modifyUserPassword(String uid, String token, String userName,
			String password, String newPassword,
			final UserFetchEntryListener listener) {
		final ModifyUserPasswordOperate operate = new ModifyUserPasswordOperate(
				uid, token, userName, password, newPassword);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 上传用户头像
	 * 
	 * @param imagePath
	 *            头像存储在本地的路径
	 * @param listener
	 *            view数据回调接口
	 */
	public void uploadUserAvatar(String imagePath,
			final UserFetchEntryListener listener) {
		final UploadUserAvaterOperate operate = new UploadUserAvaterOperate(
				imagePath);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUploadResult() : null,
						listener);
			}
		});
	}

	/**
	 * 获取推荐用户列表
	 * 
	 */
	public void getRecommendUsers(String uid, int pageType, String offsetId,
			int lastDbId, Context context, final UserFetchEntryListener listener) {
		final GetRecommendUsersOperate operate = new GetRecommendUsersOperate(
				uid, pageType, offsetId, lastDbId, context);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUserCardInfoList() : null,
						listener);
			}
		});
	}

	/**
	 * 获取多用户信息列表
	 * 
	 */
	public void getUsersInfo(Set<String> uidSet,
			final UserFetchEntryListener listener) {
		final GetUsersInfoOperate operate = new GetUsersInfoOperate(mContext);
		operate.setUids(uidSet);
		operate.setShowToast(false);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUsers() : null, listener);
			}
		});
	}

	/**
	 * 取获取用户卡片相关统计信息(收藏数、卡片数、粉丝数)
	 * 
	 * @param uid
	 * @param users
	 * @param listener
	 */
	public void getUserCardInfo(String uid, String customerUid,
			final UserFetchEntryListener listener) {
		final GetUserCardInfoOperate operate = new GetUserCardInfoOperate(uid,
				customerUid);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUserCardInfo() : null,
						listener);
			}
		});
	}

	/**
	 * 添加关注用户
	 * 
	 * @param uid
	 * @param user
	 * @param refreshList
	 *            是否需要刷新朋友页
	 * @param listener
	 */
	public void addFollow(String uid, List<UserCardInfo> user,
			boolean refreshList, final UserFetchEntryListener listener) {
		final AddFollowOperate operate = new AddFollowOperate(uid, user,
				refreshList);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 取消关注用户
	 * 
	 * @param uid
	 * @param users
	 * @param refreshList
	 *            是否需要刷新朋友页
	 * @param listener
	 */
	public void deleteFollow(String uid, List<UserCardInfo> userCardInfoList,
			boolean refreshList, final UserFetchEntryListener listener) {
		final DeleteFollowOperate operate = new DeleteFollowOperate(uid,
				userCardInfoList, refreshList);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 获取用户卡片
	 * 
	 */
	public void getUserCard(String uid, String timelineId,
			boolean isGetNewData, final UserFetchEntryListener listener) {
		final GetUserCardOperate operate = new GetUserCardOperate(mContext,
				uid, timelineId, isGetNewData);
		operate.setShowToast(false);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getCard() : null, listener);
			}
		});
	}

	/**
	 * 获取推荐的卡片
	 * 
	 */
	public void getRecommendCard(String timelineId, boolean isGetNewData,
			final UserFetchEntryListener listener) {
		final GetRecommendCardOperate operate = new GetRecommendCardOperate(
				mContext, timelineId, isGetNewData);
		operate.setShowToast(false);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getCard() : null, listener);
			}
		});
	}

	/**
	 * 获取卡片评论
	 * 
	 */
	public void getCardComments(ArrayList<CardItem> cardItems, int commentId,
			boolean isGetNewData, final UserFetchEntryListener listener) {
		final GetCardCommentsOperate operate = new GetCardCommentsOperate(
				cardItems, commentId, isGetNewData);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getComments() : null, listener);
			}
		});
	}

	/**
	 * 添加评论
	 * 
	 */
	public void addCardComment(CommentItem comment,
			final UserFetchEntryListener listener) {
		final CardAddCommentOperate operate = new CardAddCommentOperate(comment);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 添加卡片
	 * 
	 * @param cardItem
	 * @param listener
	 */
	public void addCard(CardItem cardItem, final UserFetchEntryListener listener) {
		final AddCardOperate operate = new AddCardOperate(cardItem);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 添加卡片
	 * 
	 * @param data
	 * @param listener
	 */
	public void addCard(String data, final UserFetchEntryListener listener) {
		final AddCardOperate operate = new AddCardOperate(data);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 删除卡片
	 * 
	 * @param uid
	 * @param cardId
	 * @param listener
	 */
	public void deleteCard(String uid, String cardId,
			final UserFetchEntryListener listener) {
		final DeleteCardOperate operate = new DeleteCardOperate(uid, cardId);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 收藏卡片
	 * 
	 * @param uid
	 * @param cardId
	 * @param listener
	 */
	public void addCardFav(String uid, String cardId,
			final UserFetchEntryListener listener) {
		final CardFavOperate operate = new CardFavOperate(uid, cardId,
				CardFavOperate.TYPE_ADD);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 取消收藏卡片
	 * 
	 * @param uid
	 * @param cardId
	 * @param listener
	 */
	public void cancelCardFav(String uid, String cardId,
			final UserFetchEntryListener listener) {
		final CardFavOperate operate = new CardFavOperate(uid, cardId,
				CardFavOperate.TYPE_DELTE);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 获取用户timeline
	 * 
	 * @param uid
	 * @param listener
	 */
	public void getUserTimeLine(String uid, String timelineId,
			boolean isGetNewData, final UserFetchEntryListener listener) {
		final GetUserTimeLineOperate operate = new GetUserTimeLineOperate(
				mContext, uid, timelineId, isGetNewData);
		operate.setShowToast(false);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getCard() : null, listener);
			}
		});
	}

	/**
	 * 获取消息列表
	 * 
	 * @param uid
	 * @param listener
	 */
	public void getMessageList(String uid, int lastId,
			final UserFetchEntryListener listener) {
		final GetMessageListOperate operate = new GetMessageListOperate(uid,
				lastId);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getMessage() : null, listener);
			}
		});
	}

	/**
	 * 获取单张卡片的详情
	 * 
	 * @param cardId
	 * @param listener
	 */
	public void getCardDetail(String cardId,
			final UserFetchEntryListener listener) {
		final GetCardDetailOperate operate = new GetCardDetailOperate(cardId);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getCard() : null, listener);
			}
		});
	}

	/**
	 * 根据文章id获取相关的卡片
	 * 
	 * @param listener
	 */
	public void getCardListByArticleId(String articleId,
			final UserFetchEntryListener listener) {
		final GetCardByArticleIdOperate operate = new GetCardByArticleIdOperate(
				mContext, articleId, "", "");
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getCard() : null, listener);
			}
		});

	}

	/**
	 * 获取用户金币数量
	 * 
	 * @param uid
	 * @param token
	 * @param listener
	 */
	public void getUserCoinNumber(String uid, String token,
			final UserFetchEntryListener listener) {
		final GetUserCoinNumberOperate operate = new GetUserCoinNumberOperate(
				uid, token);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getUserCent() : null, listener);
			}
		});
	}

	/**
	 * 获取应用规则列表
	 * 
	 * @param listener
	 */
	public void getActionRules(final UserFetchEntryListener listener) {
		if (UserApplication.actionRuleList != null) {
			sendMessage(UserApplication.actionRuleList, listener);
			return;
		}
		final GetAppActionRulesOperate operate = new GetAppActionRulesOperate();
		operate.asyncRequest(mContext, true, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getActionRuleList() : null,
						listener);
			}
		});
	}

	/**
	 * 获取商品列表
	 * 
	 * @param listener
	 */
	public void getGoodsList(final UserFetchEntryListener listener) {
		final GetGoodsListOperate operate = new GetGoodsListOperate();
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getGoodsList() : null, listener);
			}
		});
	}

	/**
	 * 商品订购
	 * 
	 * @param uid
	 * @param token
	 * @param goodsId
	 * @param listener
	 */
	public void addGoodsOrder(String uid, String token, String goodsId,
			final UserFetchEntryListener listener) {
		final AddGoodsOrderOperate operate = new AddGoodsOrderOperate(uid,
				token, goodsId);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}

	/**
	 * 增加积分
	 * 
	 * @param uid
	 * @param token
	 * @param actionRuleIds
	 *            规则id,数组或者,号分割字符串
	 * @param listener
	 */
	public void addUserCoinNumber(String uid, String token,
			String actionRuleIds, final UserFetchEntryListener listener) {
		final AddUserCoinNumberOperate operate = new AddUserCoinNumberOperate(
				uid, token, actionRuleIds);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success, boolean fromHttp) {
				sendMessage(success ? operate.getError() : null, listener);
			}
		});
	}
}