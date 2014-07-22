package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.model.ArticleList.ArticleDetail;

/**
 * Œ“µƒ ’≤ÿ
 * 
 * @author ZhuQiao
 * 
 */
public class Favorite extends Entry {
	private static final long serialVersionUID = 1L;
	private List<ArticleDetail> list = new ArrayList<ArticleDetail>();
	public List<ArticleDetail> getList() {
		return list;
	}
	public void setList(List<ArticleDetail> list) {
		this.list = list;
	}
	
}
