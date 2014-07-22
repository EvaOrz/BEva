package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ��ҳ����(���ս���ҳ)
 * 
 * @author ZhuQiao
 * 
 */
public class IndexArticle extends Entry {
	private static final long serialVersionUID = 1L;
	private List<ArticleItem> titleArticleList;// ��ҳ����ͼ��������(��������ܣ���ȡarticle�ڵ�������ݣ���������Ϊֻ��article�ڵ㣬����Ҫ�ж�position.1���㣬2�б�)
	private List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();// �����б�(���������������)
	private List<Today> todayList;// ֻ������������today

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
	 * ����ͼƬλ����Ϣ
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
	 * ���ս��㣨��ʾ�ڽ���ͼƬ���棩
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class Today extends Entry {
		private static final long serialVersionUID = 1L;
		private int todayCatId = -1;// ������Ŀid
		private List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();// ��ǰ��Ŀ�µ������б�

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
