package cn.com.modernmediausermodel.api;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import cn.com.modernmediaslate.listener.DataCallBack;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 接口控制
 * 
 * @author ZhuQiao
 * 
 */
public class UserOperateController {
	private static UserOperateController instance;
	private Context mContext;

	private Handler mHandler = new Handler();

	private UserOperateController(Context context) {
		mContext = context;
	}

	protected static synchronized UserOperateController getInstance(
			Context context) {
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
}
