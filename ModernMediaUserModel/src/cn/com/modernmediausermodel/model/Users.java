package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.model.User.Error;

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
	private Map<String, UserCardInfo> userCardInfoMap = new HashMap<String, Users.UserCardInfo>();
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

	public static class UserCardInfo extends Entry {

		private static final long serialVersionUID = 1L;
		/**
		 * 用户ID
		 */
		private String uid = "";
		/**
		 * 该用户关注的用户数量
		 */
		private int followNum;
		/**
		 * 粉丝数量
		 */
		private int fansNum;
		/**
		 * 该用户笔记数量
		 */
		private int cardNum;
		/**
		 * 该用户是否被当前登录用户关注
		 */
		private int isFollowed;

		public int getFollowNum() {
			return followNum;
		}

		public void setFollowNum(int followNum) {
			this.followNum = followNum;
		}

		public int getFansNum() {
			return fansNum;
		}

		public void setFansNum(int fansNum) {
			this.fansNum = fansNum;
		}

		public int getCardNum() {
			return cardNum;
		}

		public void setCardNum(int cardNum) {
			this.cardNum = cardNum;
		}

		public int getIsFollowed() {
			return isFollowed;
		}

		public void setIsFollowed(int isFollowed) {
			this.isFollowed = isFollowed;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

	}

}
