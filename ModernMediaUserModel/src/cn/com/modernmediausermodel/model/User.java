package cn.com.modernmediausermodel.model;

import cn.com.modernmediaslate.model.Entry;

public class User extends Entry {
	private static final long serialVersionUID = 1L;
	private String uid = "";
	// 用户名(邮箱)
	private String userName = "";
	// 密码(注册和修改资料时传)
	private String password = "";
	// 昵称
	private String nickName = "";
	// 头像
	private String avatar = "";
	// 错误信息
	private Error error = new Error();
	// 新浪uid(新浪用户登陆时用)
	private String sinaId = "";
	// 用户token
	private String token = "";
	// 设备id
	private String deviceId = "";
	// 设备token
	private String deviceToken = "";
	// 新密码(修改密码时用)
	private String newPassword = "";
	// 应用appid
	private String appid = "";
	// 应用版本
	private String version = "";
	// 个人签名(一句话简介)
	private String desc = "";
	// 该用户的登录状态(默认未登录)
	private boolean isLogined = false;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public String getSinaId() {
		return sinaId;
	}

	public void setSinaId(String sinaId) {
		this.sinaId = sinaId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isLogined() {
		return isLogined;
	}

	public void setLogined(boolean isLogined) {
		this.isLogined = isLogined;
	}

	public static class Error extends Entry {
		private static final long serialVersionUID = 1L;
		private int no = 0;
		private String desc;

		public int getNo() {
			return no;
		}

		public void setNo(int no) {
			this.no = no;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}
}
