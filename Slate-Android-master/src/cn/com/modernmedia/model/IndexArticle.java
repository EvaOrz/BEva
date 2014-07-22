package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页数据(今日焦点页)
 * 
 * @author ZhuQiao
 * 
 */
public class IndexArticle extends Entry {
	private static final long serialVersionUID = 1L;
	private List<ArticleItem> titleArticleList;// 首页焦点图文章数据
	private List<Today> todayList;

	public List<ArticleItem> getTitleArticleList() {
		return titleArticleList;
	}

	public void setTitleArticleList(List<ArticleItem> titleArticleList) {
		this.titleArticleList = titleArticleList;
	}

	public List<Today> getTodayList() {
		return todayList;
	}

	public void setTodayList(List<Today> todayList) {
		this.todayList = todayList;
	}

	/**
	 * 焦点图片位置信息
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class Position {
		private int id = -1;
		private int style = -1;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getStyle() {
			return style;
		}

		public void setStyle(int style) {
			this.style = style;
		}

	}

	/**
	 * 今日焦点（显示在焦点图片下面）
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class Today {
		private int todayCatId = -1;// 所属栏目id
		private List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();// 当前栏目下的文章列表

		public int getTodayCatId() {
			return todayCatId;
		}

		public void setTodayCatId(int todayCatId) {
			this.todayCatId = todayCatId;
		}

		public List<ArticleItem> getArticleItemList() {
			return articleItemList;
		}

		public void setArticleItemList(List<ArticleItem> articleItemList) {
			this.articleItemList = articleItemList;
		}

	}
}
