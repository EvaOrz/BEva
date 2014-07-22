package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 文章列表
 * 
 * @author ZhuQiao
 * 
 */
public class ArticleList extends Entry {
	private static final long serialVersionUID = 1L;
	// 栏目下文章集合
	private List<ArticleColumnList> list = new ArrayList<ArticleColumnList>();
	// 所有文章列表
	private List<FavoriteItem> allArticleList = new ArrayList<FavoriteItem>();

	// solo是否有更多数据
	private boolean hasData = true;

	private String fromOffset = "";
	private String toOffset = "";

	public List<ArticleColumnList> getList() {
		return list;
	}

	public void setList(List<ArticleColumnList> list) {
		this.list = list;
	}

	public List<FavoriteItem> getAllArticleList() {
		return allArticleList;
	}

	public void setAllArticleList(List<FavoriteItem> allArticleList) {
		this.allArticleList = allArticleList;
	}

	public boolean isHasData() {
		return hasData;
	}

	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}

	public String getFromOffset() {
		return fromOffset;
	}

	public void setFromOffset(String fromOffset) {
		this.fromOffset = fromOffset;
	}

	public String getToOffset() {
		return toOffset;
	}

	public void setToOffset(String toOffset) {
		this.toOffset = toOffset;
	}

	public static class ArticleColumnList {
		private int id = -1;// 该栏目id
		private List<FavoriteItem> list = new ArrayList<FavoriteItem>();

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public List<FavoriteItem> getList() {
			return list;
		}

		public void setList(List<FavoriteItem> list) {
			this.list = list;
		}
	}

}
