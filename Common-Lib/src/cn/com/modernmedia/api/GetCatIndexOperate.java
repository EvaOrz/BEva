package cn.com.modernmedia.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.IndexProperty;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.CatIndexArticle.SoloColumnIndexItem;
import cn.com.modernmedia.model.IndexArticle.Position;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.model.SoloColumn.SoloColumnItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;

/**
 * 除首页以外其他栏目首页数据
 * 
 * @author ZhuQiao
 * 
 */
public class GetCatIndexOperate extends BaseIndexAdvOperate {
	private String url = "";
	private CatIndexArticle catIndexArticle;
	private String columnId = "";

	private boolean isSolo;// 是否是独立栏目

	// ==========独立栏目参数
	/**
	 * 需要装载全部数据的栏目名称(self的栏目动态根据自己名称加载)
	 */
	private List<String> fullKey = new ArrayList<String>();
	// 添加数据库使用
	private List<ArticleItem> itemList = new ArrayList<ArticleItem>();
	// 添加数据库使用
	private List<ArticleItem> headItemList = new ArrayList<ArticleItem>();

	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private List<String> dateList = new ArrayList<String>();

	/**
	 * 非独立栏目
	 * 
	 * @param issueId
	 *            期id
	 * @param columnUpdateTime
	 *            更新时间
	 * @param columnId
	 *            column id
	 * @param position
	 *            当前栏目再栏目列表里的位置
	 */
	protected GetCatIndexOperate(String issueId, String columnUpdateTime,
			String columnId, int position) {
		reSetPosition();
		catIndexArticle = new CatIndexArticle();
		this.columnId = columnId;
		isSolo = false;
		url = UrlMaker.getCatIndex(issueId, columnUpdateTime, columnId);
		initAdv(issueId, columnId, position);
	}

	/**
	 * 独立栏目
	 * 
	 * @param context
	 * @param catId
	 * @param fromOffset
	 * @param toOffset
	 * @param soloColumn
	 * @param position
	 *            当前栏目再栏目列表里的位置
	 */
	@SuppressLint("UseSparseArrays")
	public GetCatIndexOperate(Context context, String catId, String fromOffset,
			String toOffset, SoloColumn soloColumn, int position) {
		reSetPosition();
		isSolo = true;
		catIndexArticle = new CatIndexArticle();
		SoloColumnItem mColumnItem = null;
		if (soloColumn != null && ParseUtil.listNotNull(soloColumn.getList())) {
			for (SoloColumnItem it : soloColumn.getList()) {
				if (String.valueOf(it.getId()).equals(catId)) {
					mColumnItem = it;
					break;
				}
			}
		}
		if (mColumnItem != null && ParseUtil.listNotNull(mColumnItem.getList())) {
			for (SoloColumnChild child : mColumnItem.getList()) {
				catIndexArticle.getSoloMap().put(child.getName(),
						new HashMap<Integer, List<ArticleItem>>());
				if (child.getType().equals(SoloColumnChild.FULL_TYPE)) {
					fullKey.add(child.getName());
					if (TextUtils.isEmpty(catIndexArticle.getFullKeyTag()))
						catIndexArticle.setFullKeyTag(child.getName());
				}
			}
		}
		String columnUpdateTime = mColumnItem != null ? mColumnItem
				.getColumnUpdateTime() : "0";
		url = UrlMaker.getSoloCatIndex(catId, fromOffset, toOffset,
				columnUpdateTime);
		initAdv("0", catId, position);
	}

	public CatIndexArticle getCatIndexArticle() {
		return catIndexArticle;
	}

	public List<ArticleItem> getItemList() {
		return itemList;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		catIndexArticle.setId(jsonObject.optInt("id", -1));
		parseTemplate(catIndexArticle, jsonObject);
		JSONArray articleArr = jsonObject.optJSONArray("article");
		if (isNull(articleArr)) {
			if (isSolo)
				catIndexArticle.setHasData(false);
			return;
		}
		JSONObject articleObj;
		int length = articleArr.length();
		for (int i = 0; i < length; i++) {
			articleObj = articleArr.optJSONObject(i);
			// TODO 过滤老版本广告
			if (isNull(articleObj) || isAdv(articleObj))
				continue;
			parseArticle(articleObj);
		}
		if (isSolo) {
			// 批量添加数据库
			if (ParseUtil.listNotNull(itemList)) {
				addSoloDb(catIndexArticle.getId(), itemList);
				addSoloFoucsDb(catIndexArticle.getId(), headItemList);
			}
		} else {
			// TODO 添加可能在列表末尾的广告(独立栏目不考虑，因为有分页)
			getListFromMap(1).addAll(
					getTitleAdvsByEndPosition(currentPosition1));
			getListFromMap(2)
					.addAll(getListAdvsByEndPosition(currentPosition2));
		}
		catIndexArticle.setImpressionUrlList(impressionUrlList);
	}

	private List<ArticleItem> getListFromMap(int position) {
		if (!catIndexArticle.getMap().containsKey(position)) {
			catIndexArticle.getMap()
					.put(position, new ArrayList<ArticleItem>());
		}
		return catIndexArticle.getMap().get(position);
	}

	/**
	 * 解析文章数据
	 * 
	 * @param arr
	 */
	public void parseArticle(JSONObject obj) {
		ArticleItem item = new ArticleItem();
		if (isSolo) {
			item.setSoloItem(new SoloColumnIndexItem());
			item.setCatId(obj.optInt("catid", -1));
		} else {
			item.setCatId(catIndexArticle.getId());
		}

		item.setArticleId(obj.optInt("id", -1));
		item.setDesc(obj.optString("desc", ""));
		item.setTitle(obj.optString("title", ""));
		item.setTag(obj.optString("tag", ""));
		item.setSlateLink(obj.optString("link", ""));
		JSONArray links = obj.optJSONArray("links");
		if (!isNull(links)) {
			item.setSlateLinkList(parseLinks(links));
		}
		item.setAuthor(obj.optString("author", ""));
		item.setOutline(obj.optString("outline", ""));
		item.setInputtime(obj.optString("inputtime"));
		item.setWeburl(obj.optString("weburl", ""));
		item.setProperty(parseProperty(obj.optJSONObject("property")));

		if (isSolo) {
			item.getSoloItem().setPagenum(obj.optInt("pagenum", -1));
			item.getSoloItem().setUpdateTime(obj.optString("updateTime", ""));
			item.getSoloItem().setInputtime(obj.optString("inputtime", ""));
			item.getSoloItem().setOffset(obj.optString("offset", "0"));
			item.getSoloItem().setJsonObject(obj.toString());
		}

		// 大图解析
		JSONArray picArr = obj.optJSONArray("picture");
		if (!isNull(picArr)) {
			item.setPicList(parsePicture(picArr));
		}

		// 列表图解析
		JSONArray thumbArr = obj.optJSONArray("thumb");
		if (!isNull(thumbArr)) {
			item.setThumbList(parsePicture(thumbArr));
		}

		// 图片位置
		item.setPosition(parsePosition(obj.optJSONObject("position")));

		if (isSolo) {
			addDataToMap(item);
		} else {
			addDataToList(item);
		}
	}

	/**
	 * 非独立栏目添加数据
	 * 
	 * @param item
	 */
	private void addDataToList(ArticleItem item) {
		if (item.getPosition().getId() == 1) {
			// 焦点图
			getListFromMap(1).addAll(getTitleAdvsByPosition(currentPosition1));
			getListFromMap(1).add(item);
			currentPosition1++;
		} else if (item.getPosition().getId() == 2) {
			// 列表
			if (ConstData.isWeeklyNews(catIndexArticle.getId())) {
				// TODO iweekly新闻栏目根据日期组合
				String date = format.format(new Date(ParseUtil.stol(item
						.getInputtime()) * 1000L));
				if (!dateList.contains(date)) {
					dateList.add(date);
					item.setDateFirst(true);
				}
			}
			getListFromMap(2).addAll(getListAdvsByPosition(currentPosition2));
			getListFromMap(2).add(item);
			currentPosition2++;
		}
	}

	/**
	 * 独立栏目添加数据，区分是tag
	 * 
	 * @param item
	 */
	@SuppressLint("UseSparseArrays")
	private void addDataToMap(ArticleItem item) {
		if (item.getPosition().getId() == 1) {
			headItemList.add(item);
		} else {
			itemList.add(item);
		}

		for (String full_key : fullKey) {
			if (item.getPosition().getId() == 1) {
				if (!getMapFromSoloMap(full_key).containsKey(1)) {
					getMapFromSoloMap(full_key).put(1,
							new ArrayList<ArticleItem>());
				}
				getMapFromSoloMap(full_key).get(1).addAll(
						getTitleAdvsByPosition(currentPosition1
								- DataHelper.solo_head_count));
				getMapFromSoloMap(full_key).get(1).add(item);
				currentPosition1++;
			} else if (item.getPosition().getId() == 2) {
				if (!getMapFromSoloMap(full_key).containsKey(2)) {
					getMapFromSoloMap(full_key).put(2,
							new ArrayList<ArticleItem>());
				}
				getMapFromSoloMap(full_key).get(2).addAll(
						getListAdvsByPosition(currentPosition2
								- DataHelper.solo_list_count));
				getMapFromSoloMap(full_key).get(2).add(item);
				currentPosition2++;
			}
		}

		String key = item.getTag();
		if (!fullKey.contains(key)) {
			if (item.getPosition().getId() == 1) {
				if (!getMapFromSoloMap(key).containsKey(1)) {
					getMapFromSoloMap(key).put(1, new ArrayList<ArticleItem>());
				}
				getMapFromSoloMap(key).get(1).add(item);
			} else {
				if (!getMapFromSoloMap(key).containsKey(2)) {
					getMapFromSoloMap(key).put(2, new ArrayList<ArticleItem>());
				}
				getMapFromSoloMap(key).get(2).add(item);
			}
		}
	}

	@SuppressLint("UseSparseArrays")
	private Map<Integer, List<ArticleItem>> getMapFromSoloMap(String key) {
		if (!catIndexArticle.getSoloMap().containsKey(key)) {
			catIndexArticle.getSoloMap().put(key,
					new HashMap<Integer, List<ArticleItem>>());
		}
		return catIndexArticle.getSoloMap().get(key);
	}

	/**
	 * 解析links，iweekly视野使用
	 * 
	 * @param array
	 * @return
	 */
	private List<String> parseLinks(JSONArray array) {
		List<String> linkList = new ArrayList<String>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			linkList.add(object.optString("url", ""));
		}
		return linkList;
	}

	/**
	 * 解析图片
	 * 
	 * @param array
	 * @return
	 */
	private List<String> parsePicture(JSONArray array) {
		List<String> pictureList = new ArrayList<String>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			pictureList.add(object.optString("url", ""));
			// TODO iweekly视野使用
			String bigUrl = object.optString("bigimgurl");
			if (!TextUtils.isEmpty(bigUrl)) {
				pictureList.add(bigUrl);
			}
		}
		return pictureList;
	}

	/**
	 * 解析position
	 * 
	 * @param obj
	 * @return
	 */
	private Position parsePosition(JSONObject obj) {
		Position position = new Position();
		if (!isNull(obj)) {
			position.setId(obj.optInt("id", -1));
			position.setStyle(obj.optInt("style", -1));
		}
		return position;
	}

	/**
	 * 解析Property
	 * 
	 * @param obj
	 * @return
	 */
	private IndexProperty parseProperty(JSONObject obj) {
		IndexProperty property = new IndexProperty();
		if (!isNull(obj)) {
			property.setLevel(obj.optInt("level", 0));
			property.setType(obj.optInt("type", 1));
			property.setHavecard(obj.optInt("havecard", 1));
		}
		return property;
	}

	@Override
	protected void saveData(String data) {
		if (isSolo)
			return;
		if (ConstData.getAppId() == 1
				&& ModernMediaTools.checkNetWork(getmContext())
				&& catIndexArticle != null) {
			// 当有网并且请求成功，保存更新时间
			DataHelper.setIndexUpdateTime(getmContext(),
					ModernMediaTools.fetchTime(), catIndexArticle.getId());
		}
		String fileName = ConstData.getCatIndexFileName(columnId);
		if (!CommonApplication.columnUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		if (isSolo)
			return null;
		return ConstData.getCatIndexFileName(columnId);
	}

	public void addSoloDb(int catId, List<ArticleItem> list) {
	}

	public void addSoloFoucsDb(int catId, List<ArticleItem> list) {
	}
}
