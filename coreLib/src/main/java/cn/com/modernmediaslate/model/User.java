package cn.com.modernmediaslate.model;

public class User extends Entry {
	private static final long serialVersionUID = 1L;
	private String uid = "";
	// 用户名(邮箱或邮箱)
	private String userName = "";
	private String phone = "";
	private String email = "";
	// 密码(注册和修改资料时传)
	private String password = "";
	// 昵称
	private String nickName = "";
	// 头像
	private String avatar = "";
	// 错误信息
	private ErrorMsg error = new ErrorMsg();
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
	// qq openid(qq用户登陆时用)
	private String qqId = "";
	// weixin openid(weixin用户登录时用)
	private String weixinId = "";

	public String getWeixinId() {
		return weixinId;
	}

	public void setWeixinId(String weixinId) {
		this.weixinId = weixinId;
	}

	// 绑定电话状态
	private boolean isBandPhone = false;
	// 绑定微信状态
	private boolean isBandWeixin = false;
	// 绑定邮箱状态
	private boolean isBandEmail = false;
	// 绑定微博状态
	private boolean isBandWeibo = false;
	// 绑定qq状态
	private boolean isBandQQ = false;
	// 邮箱验证状态
	private boolean isValEmail = false;

	public boolean isValEmail() {
		return isValEmail;
	}

	public void setValEmail(boolean isValEmail) {
		this.isValEmail = isValEmail;
	}

	public boolean isBandPhone() {
		return isBandPhone;
	}

	public void setBandPhone(boolean isBandPhone) {
		this.isBandPhone = isBandPhone;
	}

	public boolean isBandWeixin() {
		return isBandWeixin;
	}

	public void setBandWeixin(boolean isBandWeixin) {
		this.isBandWeixin = isBandWeixin;
	}

	public boolean isBandEmail() {
		return isBandEmail;
	}

	public void setBandEmail(boolean isBandEmail) {
		this.isBandEmail = isBandEmail;
	}

	public boolean isBandWeibo() {
		return isBandWeibo;
	}

	public void setBandWeibo(boolean isBandWeibo) {
		this.isBandWeibo = isBandWeibo;
	}

	public boolean isBandQQ() {
		return isBandQQ;
	}

	public void setBandQQ(boolean isBandQQ) {
		this.isBandQQ = isBandQQ;
	}

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

	public ErrorMsg getError() {
		return error;
	}

	public void setError(ErrorMsg error) {
		this.error = error;
	}

	public String getSinaId() {
		return sinaId;
	}

	public void setSinaId(String sinaId) {
		this.sinaId = sinaId;
	}

	public String getQqId() {
		return qqId;
	}

	public void setQqId(String qqId) {
		this.qqId = qqId;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
