package cn.com.modernmediausermodel.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

public class Message extends Entry {
	private static final long serialVersionUID = -9033120261351567982L;
	private String uid; // 消息属主的uid
	private int appId; // 应用id
	private int lastId; // 末尾id
	private List<MessageItem> messageList = new ArrayList<MessageItem>();// 消息列表

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getLastId() {
		return lastId;
	}

	public void setLastId(int lastId) {
		this.lastId = lastId;
	}

	public List<MessageItem> getMessageList() {
		return messageList;
	}

	public void setMessageList(ArrayList<MessageItem> messageList) {
		this.messageList = messageList;
	}

	public static class MessageItem extends Entry {
		private static final long serialVersionUID = 1L;
		private String uid = ""; // 事件发生uid
		private int cardid = 0; // 卡片id
		private String content = ""; // 消息主体
		private String time = ""; // 消息发生时间
		private int type = 0; // 0、测试用 1、添加评论 2、新增粉丝 3、自己被推荐 4、自己的卡片被推荐
		private int fansNum = 0; // 粉丝数目（type = 2）
		private int commentNum = 0; // 评论数目（type = 1）

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public int getCardid() {
			return cardid;
		}

		public void setCardid(int cardid) {
			this.cardid = cardid;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
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

		public int getFansNum() {
			return fansNum;
		}

		public void setFansNum(int fansNum) {
			this.fansNum = fansNum;
		}

		public int getCommentNum() {
			return commentNum;
		}

		public void setCommentNum(int commentNum) {
			this.commentNum = commentNum;
		}
	}

}
