package cn.com.modernmediausermodel.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.Card.CardItem;
import cn.com.modernmediausermodel.model.MultiComment.CommentItem;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserTools;

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
	protected void login(String userName, String password,
			final UserFetchEntryListener listener) {
		final UserLoginOperate operate = new UserLoginOperate(userName,
				password);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getUser() : null, listener);
			}
		});
	}

	/**
	 * 新浪微博用户登录
	 * 
	 * @param user
	 *            用户信息
	 * @param avatar
	 *            服务器相对地址
	 * @param listener
	 *            view数据回调接口
	 */
	protected void sinaLogin(User user, String avatar,
			final UserFetchEntryListener listener) {
		final SinaLoginOperate operate = new SinaLoginOperate(mContext, user,
				avatar);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
	protected void register(String userName, String password,
			final UserFetchEntryListener listener) {
		final UserRegisterOperate operate = new UserRegisterOperate(userName,
				password);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
	protected void loginOut(String uid, String token,
			final UserFetchEntryListener listener) {

		final UserLoginOutOperate operate = new UserLoginOutOperate(uid, token);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
	protected void getInfoByIdAndToken(String uid, String token,
			final UserFetchEntryListener listener) {
		final GetUserInfoOperate operate = new GetUserInfoOperate(uid, token);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
	protected void getPassword(String userName,
			final UserFetchEntryListener listener) {
		final UserFindPasswordOperate operate = new UserFindPasswordOperate(
				userName);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
	 * @param listener
	 *            view数据回调接口
	 */
	protected void modifyUserInfo(String uid, String token, String userName,
			String nickName, String url, final UserFetchEntryListener listener) {
		final ModifyUserInfoOperate operate = new ModifyUserInfoOperate(uid,
				token, userName, nickName, url);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
	protected void modifyUserPassword(String uid, String token,
			String userName, String password, String newPassword,
			final UserFetchEntryListener listener) {
		final ModifyUserPasswordOperate operate = new ModifyUserPasswordOperate(
				uid, token, userName, password, newPassword);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
	protected void uploadUserAvatar(String imagePath,
			final UserFetchEntryListener listener) {
		final UploadUserAvaterOperate operate = new UploadUserAvaterOperate(
				imagePath);
		// http请求
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getUploadResult() : null,
						listener);
			}
		});
	}

	/**
	 * 获取服务器数据
	 * 
	 * @param uid
	 * @param listener
	 */
	protected void getFav(String uid, final UserFetchEntryListener listener) {
		final UserGetFavOperate operate = new UserGetFavOperate(uid);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getFavorite() : null, listener);
			}
		});
	}

	/**
	 * 同步收藏
	 * 
	 * @param jsonStr
	 * @param listener
	 */
	protected void updateFav(String uid, String appid, List<FavoriteItem> list,
			final UserFetchEntryListener listener) {
		if (list == null || list.size() == 0)
			sendMessage(null, listener);
		Favorite favorite = new Favorite();
		favorite.setUid(uid);
		favorite.setAppid(appid);
		favorite.setList(list);
		String jsonStr = UserTools.objectToJson(favorite);
		final UserUpdateFavOperate operate = new UserUpdateFavOperate(uid,
				appid, jsonStr);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getFavorite() : null, listener);
			}
		});
	}

	/**
	 * 获取推荐用户列表
	 * 
	 */
	public void getRecommendUsers(String uid, int pageType,
			final UserFetchEntryListener listener) {
		final GetRecommendUsersOperate operate = new GetRecommendUsersOperate(
				uid, pageType);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				sendMessage(success ? operate.getUsers() : null, listener);
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
	public void addFollow(String uid, List<User> user, boolean refreshList,
			final UserFetchEntryListener listener) {
		final AddFollowOperate operate = new AddFollowOperate(uid, user,
				refreshList);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
	public void deleteFollow(String uid, List<User> users, boolean refreshList,
			final UserFetchEntryListener listener) {
		final DeleteFollowOperate operate = new DeleteFollowOperate(uid, users,
				refreshList);
		operate.asyncRequestByPost(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
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
			public void callback(boolean success) {
				sendMessage(success ? operate.getCard() : null, listener);
			}
		});
	}
}
