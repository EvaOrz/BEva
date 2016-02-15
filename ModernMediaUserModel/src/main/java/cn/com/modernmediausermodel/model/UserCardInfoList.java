package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;

/**
 * 用户卡片信息列表类
 * 
 * @author jiancong
 * 
 */
public class UserCardInfoList extends Entry {
	private static final long serialVersionUID = 1L;
	private List<UserCardInfo> list = new ArrayList<UserCardInfo>();
	private ErrorMsg error = new ErrorMsg();
	private String uid = ""; // 用户id
	private String offsetId = "0"; // 偏移值,加载更多用

	public List<UserCardInfo> getList() {
		return list;
	}

	public void setList(List<UserCardInfo> list) {
		this.list = list;
	}

	public ErrorMsg getError() {
		return error;
	}

	public void setError(ErrorMsg error) {
		this.error = error;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getOffsetId() {
		return offsetId;
	}

	public void setOffsetId(String offsetId) {
		this.offsetId = offsetId;
	}

	public static class UserCardInfo extends Entry {
		private static final long serialVersionUID = 1L;
		/**
		 * 数据库里面的id
		 */
		private int db_id;
		/**
		 * 用户ID
		 */
		private String uid = "";
		/**
		 * 用户昵称
		 */
		private String nickName = "";
		/**
		 * 用户头像URL
		 */
		private String avatar = "";

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

		public int getDb_id() {
			return db_id;
		}

		public void setDb_id(int db_id) {
			this.db_id = db_id;
		}

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
	}
}
