package cn.com.modernmediausermodel;

import cn.com.modernmediausermodel.listener.LogOutListener;
import cn.com.modernmediausermodel.listener.UserInfoChangeListener;

public class UserApplication {
	public static LogOutListener logOutListener;
	public static UserInfoChangeListener infoChangeListener;
	public static UserInfoChangeListener recommInfoChangeListener;// 关注页面需要刷新

	public static void exit() {
		logOutListener = null;
		infoChangeListener = null;
		recommInfoChangeListener = null;
	}
}
