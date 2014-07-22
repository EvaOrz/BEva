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
	private List<ArticleItem> titleArticleList;// 首页焦点图文章数据(如果是商周，就取article节点里的数据，其他的因为只有article节点，所以要判断position.1焦点，2列表)
	private List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();// 文章列表(除了商周以外的用)
	private List<Today> todayList;// 只在商周里面有today

	public List<ArticleItem> getTitleArticleList() {
		return titleArticleList;
	}

	public void setTitleArticleList(List<ArticleItem> titleArticleList) {
		this.titleArticleList = titleArticleList;
	}

	public List<ArticleItem> getArticleItemList() {
		return articleItemList;
	}

	public void setArticleItemList(List<ArticleItem> articleItemList) {
		this.articleItemList = articleItemList;
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
	public static class Position extends Entry {
		private static final long serialVersionUID = 1L;
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
	public static class Today extends Entry {
		private static final long serialVersionUID = 1L;
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
