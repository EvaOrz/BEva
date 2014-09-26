package cn.com.modernmediausermodel.model;

import cn.com.modernmediaslate.model.Entry;

public class UserCent extends Entry {
	private static final long serialVersionUID = 1L;
	private String uid;
	private int number;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
