package cn.com.modernmedia.model;

import java.util.HashMap;

import android.annotation.SuppressLint;

/**
 * 各个栏目的最新文章(用来标注未读)
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class LastestArticleId extends Entry {
	private static final long serialVersionUID = 1L;
	//key:catId;value====key:articleId,vaule:updatetTime
	private HashMap<Integer, HashMap<Integer, String>> unReadedEntryIds = new HashMap<Integer, HashMap<Integer, String>>();
	// key:文章id;value:栏目id
	private HashMap<Integer, Integer> unReadedId = new HashMap<Integer, Integer>();

	public HashMap<Integer, HashMap<Integer, String>> getUnReadedEntryIds() {
		return unReadedEntryIds;
	}

	public void setUnReadedEntryIds(
			HashMap<Integer, HashMap<Integer, String>> unReadedEntryIds) {
		this.unReadedEntryIds = unReadedEntryIds;
	}

	public HashMap<Integer, Integer> getUnReadedId() {
		return unReadedId;
	}

	public void setUnReadedId(HashMap<Integer, Integer> unReadedId) {
		this.unReadedId = unReadedId;
	}

}
