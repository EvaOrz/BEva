package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 除首页以外的栏目首页数据
 * 
 * @author ZhuQiao
 * 
 */
public class CatIndexArticle extends Entry {
	private static final long serialVersionUID = 1L;
	private int id = -1;// 栏目ID
	private List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();// 文章列表
	private List<ArticleItem> titleActicleList = new ArrayList<ArticleItem>();// 上面大图展示

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
