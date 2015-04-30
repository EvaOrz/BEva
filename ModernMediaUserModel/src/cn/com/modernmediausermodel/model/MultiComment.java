package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.ErrorMsg;
import cn.com.modernmediaslate.model.User;

public class MultiComment extends Entry {
	private static final long serialVersionUID = 1L;
	private List<Comment> commentList = new ArrayList<Comment>();
	private Map<String, User> userInfoMap = new HashMap<String, User>();// 存放user信息，key为用户ID

	public List<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(ArrayList<Comment> commentList) {
		this.commentList = commentList;
	}

	public Map<String, User> getUserInfoMap() {
		return userInfoMap;
	}

	public void setUserInfoMap(Map<String, User> userInfoMap) {
		this.userInfoMap = userInfoMap;
	}

	public static class Comment extends Entry {
		private static final long serialVersionUID = 1L;
		private String uid = "";
		private int pageCount;
		private List<CommentItem> commentItemList = new ArrayList<CommentItem>();

		private ErrorMsg error = new ErrorMsg();

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public int getPageCount() {
			return pageCount;
		}

		public void setPageCount(int pageCount) {
			this.pageCount = pageCount;
		}

		public List<CommentItem> getCommentItemList() {
			return commentItemList;
		}

		public void setCommentItemList(ArrayList<CommentItem> commentItemList) {
			this.commentItemList = commentItemList;
		}

		public ErrorMsg getError() {
			return error;
		}

		public void setError(ErrorMsg error) {
			this.error = error;
		}
	}

	/**
	 * 评论
	 * 
	 * @author jiancong
	 */
	public static class CommentItem extends Entry {
		private static final long serialVersionUID = 1L;
		private int id;
		private String uid;
		private String cardId;
		private String time;
		private int type;
		private String content;
		private String token;
		private int isdel;
		private Error error;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getCardId() {
			return cardId;
		}

		public void setCardId(String cardId) {
			this.cardId = cardId;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public int getIsdel() {
			return isdel;
		}

		public void setIsdel(int isdel) {
			this.isdel = isdel;
		}

		public Error getError() {
			return error;
		}

		public void setError(Error error) {
			this.error = error;
		}

	}
}
