package cn.com.modernmediausermodel.model;

import java.util.ArrayList;

import cn.com.modernmediaslate.model.Entry;

public class Follow extends Entry {
	private static final long serialVersionUID = 1L;
	private String uid = "";
	private String token = "";
	private ArrayList<FollowItem> list = new ArrayList<Follow.FollowItem>();
	private Error error = new Error();

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public ArrayList<FollowItem> getList() {
		return list;
	}

	public void setList(ArrayList<FollowItem> list) {
		this.list = list;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public class FollowItem extends Entry {
		private static final long serialVersionUID = 1L;
		private String uid = "";
		private int type;
		private int cardnum;
		private int isFollow;
		private Error error = new Error();

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getCardnum() {
			return cardnum;
		}

		public void setCardnum(int cardnum) {
			this.cardnum = cardnum;
		}

		public int getIsFollow() {
			return isFollow;
		}

		public void setIsFollow(int isFollow) {
			this.isFollow = isFollow;
		}

		public Error getError() {
			return error;
		}

		public void setError(Error error) {
			this.error = error;
		}
	}
}
