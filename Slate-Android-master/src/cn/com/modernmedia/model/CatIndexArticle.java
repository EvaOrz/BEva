package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ����ҳ�������Ŀ��ҳ����
 * 
 * @author ZhuQiao
 * 
 */
public class CatIndexArticle extends Entry {
	private static final long serialVersionUID = 1L;
	private int id = -1;// ��ĿID
	private List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();// �����б�
	private List<ArticleItem> titleActicleList = new ArrayList<ArticleItem>();// �����ͼչʾ

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ArticleItem> getArticleItemList() {
		return articleItemList;
	}

	public void setArticleItemList(List<ArticleItem> articleItemList) {
		this.articleItemList = articleItemList;
	}

	public List<ArticleItem> getTitleActicleList() {
		return titleActicleList;
	}

	public void setTitleActicleList(List<ArticleItem> titleActicleList) {
		this.titleActicleList = titleActicleList;
	}

}
