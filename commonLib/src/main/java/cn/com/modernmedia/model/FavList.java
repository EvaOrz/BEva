package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

import com.google.gson.annotations.Expose;

/**
 * 收藏列表
 * 
 * @author jiancong
 * 
 */
public class FavList extends Entry {
	private static final long serialVersionUID = 1L;
	private String uid = "";
	private int appid;
	@Expose
	private List<ArticleItem> article = new ArrayList<ArticleItem>();

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public List<ArticleItem> getArticle() {
		return article;
	}

	public void setArticle(List<ArticleItem> article) {
		this.article = article;
	}

}
