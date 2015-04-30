package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.User;
import cn.com.modernmediausermodel.model.UserCardInfoList.UserCardInfo;

/**
 * 卡片对应的用户信息
 * 
 * @author user
 * 
 */
public class Users extends Entry {
	private static final long serialVersionUID = 1L;
	private String uid;
	private List<User> userList = new ArrayList<User>();
	private Map<String, UserCardInfo> userCardInfoMap = new HashMap<String, UserCardInfo>();
	private Map<String, User> userInfoMap = new HashMap<String, User>();// 将User对象数组转换成以用户id作key，对象自己作为值的map
	private Error error = new Error();

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public Map<String, UserCardInfo> getUserCardInfoMap() {
		return userCardInfoMap;
	}

	public void setUserCardInfoMap(Map<String, UserCardInfo> userCardInfoMap) {
		this.userCardInfoMap = userCardInfoMap;
	}

	public Map<String, User> getUserInfoMap() {
		return userInfoMap;
	}

	public void setUserInfoMap(Map<String, User> userInfoMap) {
		this.userInfoMap = userInfoMap;
	}
}
