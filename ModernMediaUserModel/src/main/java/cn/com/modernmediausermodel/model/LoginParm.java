package cn.com.modernmediausermodel.model;

/**
 * 登录文本资源
 * 
 * @author user
 * 
 */
public class LoginParm {
	private String login_desc = "";// 登录页面描述
	private String userinfo_desc = "";// 用户信息页面描述

	public String getLogin_desc() {
		return login_desc;
	}

	public void setLogin_desc(String login_desc) {
		this.login_desc = login_desc;
	}

	public String getUserinfo_desc() {
		return userinfo_desc;
	}

	public void setUserinfo_desc(String userinfo_desc) {
		this.userinfo_desc = userinfo_desc;
	}

}
