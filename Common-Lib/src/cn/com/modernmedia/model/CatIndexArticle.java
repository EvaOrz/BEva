package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import cn.com.modernmedia.util.ParseUtil;
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
	 * key:position;value:文章列表
	 */
	@SuppressLint("UseSparseArrays")
	private Map<Integer, List<ArticleItem>> map = new HashMap<Integer, List<ArticleItem>>();

	/**
	 * 是否有数据 ，solo栏目
	 */
	private boolean hasData = true;

	/**
	 * 按照不同的栏目加载其需要的数据,焦点图，solo栏目
	 */
	private Map<String, Map<Integer, List<ArticleItem>>> soloMap = new HashMap<String, Map<Integer, List<ArticleItem>>>();

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

	public Map<Integer, List<ArticleItem>> getMap() {
		return map;
	}

	public void setMap(Map<Integer, List<ArticleItem>> map) {
		this.map = map;
	}

	public boolean isHasData() {
		return hasData;
	}

	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}

	public Map<String, Map<Integer, List<ArticleItem>>> getSoloMap() {
		return soloMap;
	}

	public void setSoloMap(Map<String, Map<Integer, List<ArticleItem>>> soloMap) {
		this.soloMap = soloMap;
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

	public boolean hasData(int position) {
		return map.containsKey(position)
				&& ParseUtil.listNotNull(map.get(position));
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
