package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页数据(今日焦点页)
 * 
 * @author ZhuQiao
 * 
 */
public class IndexArticle extends Entry {
	private static final long serialVersionUID = 1L;
	@SuppressLint("UseSparseArrays")
	/**
	 * key:position;value:文章列表
	 */
	private Map<Integer, List<ArticleItem>> map = new HashMap<Integer, List<ArticleItem>>();
	private List<Today> todayList = new ArrayList<Today>();// 只在商周里面有today
	// 当列表里面有广告时，统计显示
	private List<String> impressionUrlList = new ArrayList<String>();

	public Map<Integer, List<ArticleItem>> getMap() {
		return map;
	}

	public void setMap(Map<Integer, List<ArticleItem>> map) {
		this.map = map;
	}

	public List<Today> getTodayList() {
		return todayList;
	}

	public void setTodayList(List<Today> todayList) {
		this.todayList = todayList;
	}

	public List<String> getImpressionUrlList() {
		return impressionUrlList;
	}

	public void setImpressionUrlList(List<String> impressionUrlList) {
		this.impressionUrlList = impressionUrlList;
	}

	public boolean hasData(int position) {
		return map.containsKey(position)
				&& ParseUtil.listNotNull(map.get(position));
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
