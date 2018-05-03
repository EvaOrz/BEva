package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import cn.com.modernmediaslate.model.Entry;

/**
 * 各个栏目的最新文章(用来标注未读)
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class LastestArticleId extends Entry {
	private static final long serialVersionUID = 1L;
	// key:栏目tag;value:articleid列表
	private Map<String, ArrayList<Integer>> unReadedArticles = new HashMap<String, ArrayList<Integer>>();
	// key:文章id
	private List<Integer> unReadedId = new ArrayList<Integer>();

	public Map<String, ArrayList<Integer>> getUnReadedArticles() {
		return unReadedArticles;
	}

	public void setUnReadedArticles(
			Map<String, ArrayList<Integer>> unReadedArticles) {
		this.unReadedArticles = unReadedArticles;
	}

	public List<Integer> getUnReadedId() {
		return unReadedId;
	}

	public void setUnReadedId(List<Integer> unReadedId) {
		this.unReadedId = unReadedId;
	}

}
