package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * �����б�
 * 
 * @author ZhuQiao
 * 
 */
public class ArticleList extends Entry {
	private static final long serialVersionUID = 1L;
	private List<ArticleColumnList> list = new ArrayList<ArticleColumnList>();
	// ���������б�
	private List<ArticleDetail> allArticleList = new ArrayList<ArticleDetail>();

	public List<ArticleColumnList> getList() {
		return list;
	}

	public void setList(List<ArticleColumnList> list) {
		this.list = list;
	}

	public List<ArticleDetail> getAllArticleList() {
		return allArticleList;
	}

	public void setAllArticleList(List<ArticleDetail> allArticleList) {
		this.allArticleList = allArticleList;
	}

	public static class ArticleColumnList {
		private int id = -1;// ����Ŀid
		private List<ArticleDetail> list = new ArrayList<ArticleDetail>();

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public List<ArticleDetail> getList() {
			return list;
		}

		public void setList(List<ArticleDetail> list) {
			this.list = list;
		}
	}

	/**
	 * ��������
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class ArticleDetail extends ArticleItem {
		private static final long serialVersionUID = 1L;
		private String link = "";// ����url
		private int pagenum = -1;
		private String updateTime = "";// ����������ʱ��
		private Property property = new Property();

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public int getPagenum() {
			return pagenum;
		}

		public void setPagenum(int pagenum) {
			this.pagenum = pagenum;
		}

		public String getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}

		public Property getProperty() {
			return property;
		}

		public void setProperty(Property property) {
			this.property = property;
		}

	}

	/**
	 * ��������
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public static class Property {
		private String type = "";// 1 html 2 gallery 3 video

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}
}
