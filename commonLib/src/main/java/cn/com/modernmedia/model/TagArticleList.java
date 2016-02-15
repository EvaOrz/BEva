package cn.com.modernmedia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import cn.com.modernmedia.R;
import cn.com.modernmedia.model.TagInfoList.ColumnProperty;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 某栏目对应的文章列表
 * 
 * @author jiancong
 * 
 */
public class TagArticleList extends Entry {
	private static final long serialVersionUID = 1L;
	public static final String BY_CATNAME = "bycatname";// 根据栏目排序
	public static final String BY_INPUTTIME = "byinputtime";// 根据日期排序
	// 应用id
	private int appid;
	// 栏目标识符
	private String tagName = "";
	// 栏目属性
	private ColumnProperty property = new ColumnProperty();
	// 栏目属性json
	private String columnJson = "";
	// 跑马灯外链
	private String link = "";

	/**
	 * key:position;value:文章列表
	 */
	@SuppressLint("UseSparseArrays")
	private Map<Integer, List<ArticleItem>> map = new HashMap<Integer, List<ArticleItem>>();
	// 文章列表
	private List<ArticleItem> articleList = new ArrayList<ArticleItem>();
	// 当列表里面有广告时，统计显示
	private List<String> impressionUrlList = new ArrayList<String>();
	private String endOffset = "";// 最后一个offset
	private String viewbygroup = "";// 排序
	private List<String> dateList = new ArrayList<String>();

	public TagArticleList copy() {
		TagArticleList tagArticleList = new TagArticleList();
		tagArticleList.setAppid(appid);
		tagArticleList.setTagName(tagName);
		tagArticleList.setProperty(property);
		tagArticleList.setColumnJson(columnJson);
		tagArticleList.setEndOffset(endOffset);
		tagArticleList.setViewbygroup(viewbygroup);
		tagArticleList.setLink(link);
		tagArticleList.getImpressionUrlList().addAll(impressionUrlList);
		tagArticleList.getDateList().addAll(dateList);
		for (int key : map.keySet()) {
			List<ArticleItem> list = map.get(key);
			for (ArticleItem item : list) {
				if (!tagArticleList.getMap().containsKey(key)) {
					tagArticleList.getMap().put(key,
							new ArrayList<ArticleItem>());
				}
				tagArticleList.getMap().get(key).add(item);
			}
		}
		tagArticleList.getArticleList().addAll(articleList);
		return tagArticleList;
	}

	public void addTagArticleList(TagArticleList list) {
		for (int key : list.getMap().keySet()) {
			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<ArticleItem>());
			}
			map.get(key).addAll(list.getMap().get(key));
		}
		for (String date : list.getDateList()) {
			if (!dateList.contains(date)) {
				dateList.add(date);
			}
		}
	}

	/**
	 * 插入订阅文章(在section1)
	 * 
	 * @param articleList
	 */
	public void insertSubscribeArticle(Context context,
			TagArticleList sArticleList, boolean isIndex) {
		if (sArticleList == null
				|| !ParseUtil.listNotNull(sArticleList.getArticleList())) {
			return;
		}
		sArticleList.getArticleList().get(0).setShowTitleBar(true);
		sArticleList.getArticleList().get(0)
				.setInputtime(context.getString(R.string.theme_magazine));
		if (dateList.size() <= 1) {
			dateList.add(context.getResources().getString(R.string.book));// 订阅
		} else {
			dateList.add(1, context.getResources().getString(R.string.book));
		}
		int keyId = ConstData.getAppId() == 20 ? 2 : 3;
		if (!hasData(keyId)) {
			map.put(keyId, new ArrayList<ArticleItem>());
			map.get(keyId).addAll(sArticleList.getArticleList());
			return;
		}
		int section = 0;
		int position = 0;
		int secondSectionId = -1;
		List<ArticleItem> list = map.get(keyId);
		for (int i = 0; i < list.size(); i++) {
			ArticleItem item = list.get(i);
			if (item.isShowTitleBar()) {
				section++;
			}
			if (section == 2) {
				// 如果找到第二个section，那么插在它前面
				position = i;
				secondSectionId = item.getArticleId();
				break;
			}
		}
		position = Math.max(position, 0);
		list.addAll(position, sArticleList.getArticleList());
		if (!isIndex && secondSectionId != -1) {
			int articlePosition = 0;
			for (int i = 0; i < articleList.size(); i++) {
				ArticleItem item = articleList.get(i);
				if (item.getArticleId() == secondSectionId) {
					articlePosition = i;
					break;
				}
			}
			articleList.addAll(articlePosition, sArticleList.getArticleList());
		}
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public ColumnProperty getProperty() {
		return property;
	}

	public void setProperty(ColumnProperty property) {
		this.property = property;
	}
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Map<Integer, List<ArticleItem>> getMap() {
		return map;
	}

	public void setMap(Map<Integer, List<ArticleItem>> map) {
		this.map = map;
	}

	public List<String> getImpressionUrlList() {
		return impressionUrlList;
	}

	public void setImpressionUrlList(List<String> impressionUrlList) {
		this.impressionUrlList = impressionUrlList;
	}

	public String getColumnJson() {
		return columnJson;
	}

	public void setColumnJson(String columnJson) {
		this.columnJson = columnJson;
	}

	public String getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(String endOffset) {
		this.endOffset = endOffset;
	}

	public String getViewbygroup() {
		return viewbygroup;
	}

	public void setViewbygroup(String viewbygroup) {
		this.viewbygroup = viewbygroup;
	}

	public List<String> getDateList() {
		return dateList;
	}

	public void setDateList(List<String> dateList) {
		this.dateList = dateList;
	}

	public List<ArticleItem> getArticleList() {
		return articleList;
	}

	public void setArticleList(List<ArticleItem> articleList) {
		this.articleList = articleList;
	}

	public boolean hasData(int position) {
		return map.containsKey(position)
				&& ParseUtil.listNotNull(map.get(position));
	}

}
