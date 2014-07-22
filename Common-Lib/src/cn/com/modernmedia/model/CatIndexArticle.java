package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.modernmediaslate.model.Entry;

/**
 * 除首页以外的栏目首页数据
 * 
 * @author ZhuQiao
 * 
 */
public class CatIndexArticle extends Entry {
	private static final long serialVersionUID = 1L;
	private int id = -1;// 栏目ID
	/**
	 * 文章列表,普通栏目列表
	 */
	private List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();
	/**
	 * 上面大图展示,普通栏目列表
	 */
	private List<ArticleItem> titleActicleList = new ArrayList<ArticleItem>();

	/**
	 * 是否有数据 ，solo栏目
	 */
	private boolean hasData = true;
	/**
	 * 按照不同的栏目加载其需要的数据,列表，solo栏目
	 */
	private HashMap<String, List<ArticleItem>> listMap = new HashMap<String, List<ArticleItem>>();

	/**
	 * 按照不同的栏目加载其需要的数据,焦点图，solo栏目
	 */
	private HashMap<String, List<ArticleItem>> headMap = new HashMap<String, List<ArticleItem>>();

	public String fullKeyTag = "";

	// 当列表里面有广告时，统计显示
	private List<String> impressionUrlList = new ArrayList<String>();

	// /**
	// * 根据请求成功的list的fromOffset
	// */
	// private String fromOffset = "";
	// /**
	// * 根据请求成功的list的toOffset
	// */
	// private String toOffset = "";

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

	public boolean isHasData() {
		return hasData;
	}

	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}

	public HashMap<String, List<ArticleItem>> getListMap() {
		return listMap;
	}

	public void setListMap(HashMap<String, List<ArticleItem>> listMap) {
		this.listMap = listMap;
	}

	public HashMap<String, List<ArticleItem>> getHeadMap() {
		return headMap;
	}

	public void setHeadMap(HashMap<String, List<ArticleItem>> headMap) {
		this.headMap = headMap;
	}

	public String getFullKeyTag() {
		return fullKeyTag;
	}

	public void setFullKeyTag(String fullKeyTag) {
		this.fullKeyTag = fullKeyTag;
	}

	public List<String> getImpressionUrlList() {
		return impressionUrlList;
	}

	public void setImpressionUrlList(List<String> impressionUrlList) {
		this.impressionUrlList = impressionUrlList;
	}

	public static class SoloColumnIndexItem extends Entry {
		private static final long serialVersionUID = 1L;
		private int pagenum;
		private String updateTime = "";
		private String inputtime = "";
		private String offset = "";
		private String jsonObject = "";

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

		public String getInputtime() {
			return inputtime;
		}

		public void setInputtime(String inputtime) {
			this.inputtime = inputtime;
		}

		public String getOffset() {
			return offset;
		}

		public void setOffset(String offset) {
			this.offset = offset;
		}

		public String getJsonObject() {
			return jsonObject;
		}

		public void setJsonObject(String jsonObject) {
			this.jsonObject = jsonObject;
		}

	}
}
