package cn.com.modernmediausermodel.model;

import cn.com.modernmediaslate.model.Entry;

/**
 * 活动model
 * 
 * @author lusiyuan
 *
 */
public class Active extends Entry {

	/**
	 * {"id":1,"status":0,"url":
	 * "http:\/\/user.bbwc.cn\/interface\/?m=active&a=enterSn&appid=1&id=1&uid=549&token=5%3A%5D%28%5Dgtc%2C%7BwclxkBss%2Camo%5D%28%5D66%5D%28%5D76%3B%5D%28%5D20","name
	 * " : " 赠 阅 活 动 " }
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int status;
	private String url;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
