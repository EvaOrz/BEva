package cn.com.modernmediausermodel.api;

import java.util.List;

import android.content.Context;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.listener.RequestListener;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.model.UploadAvatarResult;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * user模块数据接口
 * 
 * @author ZhuQiao
 * 
 */
public class UserModelInterface {
	private static UserModelInterface userModel;
	private UserOperateController controller;
	private Context mContext;

	public static UserModelInterface getInstance(Context context) {
		if (userModel == null) {
			userModel = new UserModelInterface(context);
		}
		return userModel;
	}

	private UserModelInterface(Context context) {
		this.controller = UserOperateController.getInstance(context);
		mContext = context;
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
	public void login(String userName, final String password,
			final RequestListener listener) {
		controller.login(userName, password, new UserFetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				if (entry instanceof User) {
					User user = (User) entry;
					User.Error error = user.getError();
					if (error.getNo() == 0) {
						// 登录成功
						user.setPassword(password);
						user.setLogined(true);
						// 将相关信息用SharedPreferences存储
						UserDataHelper.saveUserLoginInfo(mContext, user);
						UserDataHelper.saveAvatarUrl(mContext,
								user.getUserName(), user.getAvatar());
						listener.onSuccess(user);
					} else {
						// 登录失败
						listener.onFailed(error);
					}
				} else {
					// 登录失败
					listener.onFailed(null);
				}
			}
		});
	}

	/**
	 * 新浪微博账号登录
	 * 
	 * @param user
	 *            用户信息
	 * @param listener
	 *            view数据回调接口
	 */
	public void sinaLogin(User user, String avatar,
			final RequestListener listener) {
		controller.sinaLogin(user, avatar, new UserFetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				if (entry instanceof User) {
					User mUser = (User) entry;
					User.Error error = mUser.getError();
					if (error.getNo() == 0) {
						// user.setNickName(userName);
						// user.setSinaId(sinaId);
						// UserDataHelper.saveUserLoginInfo(mContext, user);
						listener.onSuccess(mUser);
					} else {
						// 登录失败
						listener.onFailed(error);
					}
				} else {
					// 登录失败
					listener.onFailed(null);
				}
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
	public void register(final String userName, final String password,
			final RequestListener listener) {
		controller.register(userName, password, new UserFetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				if (entry instanceof User) {
					User user = (User) entry;
					User.Error error = user.getError();
					if (error.getNo() == 0) {
						// 注册成功
						user.setPassword(password);
						user.setLogined(true);
						// 将相关信息用SharedPreferences存储
						UserDataHelper.saveUserLoginInfo(mContext, user);
						listener.onSuccess(user);
					} else {
						// 注册失败
						listener.onFailed(error);
					}
				} else {
					listener.onFailed(null);
				}
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
	public void getPassword(final String userName,
			final RequestListener listener) {
		controller.getPassword(userName, new UserFetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				if (entry instanceof User) {
					User resUser = (User) entry;
					User.Error error = resUser.getError();
					if (error.getNo() == 0) {
						// 发送请求成功
						listener.onSuccess(resUser);
					} else {
						// 找回密码失败
						listener.onFailed(error);
					}
				} else {
					listener.onFailed(null);
				}
			}
		});
	}

	/**
	 * 获取当前用户的信息 通过uid和token获取当前用户信息， uid和token用post方式接收(可检测用户登录状态) url
	 * 
	 * @param uid
	 *            uid
	 * @param token
	 *            用户token
	 * @param listener
	 *            view数据回调接口
	 */
	public void getUserInfo(String uid, String token,
			final RequestListener listener) {
		controller.getInfoByIdAndToken(uid, token,
				new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof User) {
							User user = (User) entry;
							User.Error error = user.getError();
							// 取得成功
							if (error != null && error.getNo() == 0) {
								user.setLogined(true);
								UserDataHelper
										.saveUserLoginInfo(mContext, user);
								UserDataHelper.saveAvatarUrl(mContext,
										user.getUserName(), user.getAvatar());
								listener.onSuccess(entry);
							} else {
								// token过期或者其他的情况，把本地保存的用户信息删除
								UserDataHelper.clearLoginInfo(mContext);
								listener.onFailed(error);
							}
						} else {
							listener.onFailed(null);
						}
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
			final RequestListener listener) {
		controller.loginOut(uid, token, new UserFetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				if (entry instanceof User) {
					User resUser = (User) entry;
					User.Error error = resUser.getError();
					// 登出成功
					if (error.getNo() == 0) {
						listener.onSuccess(entry);
					} else {
						listener.onFailed(error);
					}
				} else {
					listener.onFailed(null);
				}
			}

		});
	}

	/**
	 * 修改用户资料(目前只修改昵称)
	 * 
	 * @param User
	 *            包含新信息的User对象
	 * @param url
	 *            图片的相对地址(通过上传头像获得)
	 */
	public void modifyUserInfo(User user, String url,
			final RequestListener listener) {
		controller.modifyUserInfo(user.getUid(), user.getToken(),
				user.getUserName(), user.getNickName(), url,
				new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof User) {
							User resUser = (User) entry;
							User.Error error = resUser.getError();
							// 修改成功
							if (error.getNo() == 0) {
								listener.onSuccess(entry);
							} else {
								listener.onFailed(error);
							}
						} else {
							listener.onFailed(null);
						}
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
	public void modifyPassword(String uid, String token, String userName,
			String password, String newPassword, final RequestListener listener) {
		controller.modifyUserPassword(uid, token, userName, password,
				newPassword, new UserFetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof User) {
							User resUser = (User) entry;
							User.Error error = resUser.getError();
							// 修改成功
							if (error.getNo() == 0) {
								listener.onSuccess(entry);
							} else {
								listener.onFailed(error);
							}
						} else {
							listener.onFailed(null);
						}
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
	public void uploadAvatar(String path, final RequestListener listener) {
		controller.uploadUserAvatar(path, new UserFetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				if (entry instanceof UploadAvatarResult) {
					UploadAvatarResult result = (UploadAvatarResult) entry;
					String status = result.getStatus();
					if (status.equals("success")) { // 头像上传成功
						listener.onSuccess(result);
					} else {
						listener.onFailed(result);
					}
				} else {
					listener.onFailed(null);
				}
			}
		});
	}

	/**
	 * 获取收藏
	 * 
	 * @param uid
	 * @param listener
	 */
	public void getFav(String uid, final RequestListener listener) {
		controller.getFav(uid, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof Favorite) {
					Favorite favorite = (Favorite) entry;
					if (favorite.getList() != null
							&& !favorite.getList().isEmpty()) {
						listener.onSuccess(entry);
					} else {
						listener.onFailed(null);
					}
				} else {
					listener.onFailed(null);
				}
			}
		});
	}

	/**
	 * 同步收藏
	 * 
	 * @param jsonStr
	 * @param listener
	 */
	public void updateFav(String uid, String appid, List<FavoriteItem> list,
			final RequestListener listener) {
		controller.updateFav(uid, appid, list, new UserFetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof Favorite) {
					listener.onSuccess(entry);
				} else {
					listener.onFailed(null);
				}
			}
		});
	}
}
