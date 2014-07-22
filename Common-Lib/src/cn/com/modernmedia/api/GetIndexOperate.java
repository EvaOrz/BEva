package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.IndexProperty;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.IndexArticle.Position;
import cn.com.modernmedia.model.IndexArticle.Today;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ModernMediaTools;

/**
 * 获取今日焦点首页数据
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class GetIndexOperate extends BaseIndexAdvOperate {
	private String url = "";
	private IndexArticle indexArticle;

	protected GetIndexOperate(String issueId, String columnUpdateTime) {
		reSetPosition();
		indexArticle = new IndexArticle();
		url = UrlMaker.getIndex(issueId, columnUpdateTime);
		initAdv(issueId, "0", 0);
	}

	protected IndexArticle getIndexArticle() {
		return indexArticle;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray articleArray = jsonObject.optJSONArray("article");
		if (!isNull(articleArray))
			parseTitleArticle(articleArray);
		JSONArray todayArr = jsonObject.optJSONArray("today");
		if (!isNull(todayArr) && ConstData.getAppId() == 1)// 只有商周有
			parseToday(todayArr);
		indexArticle.setImpressionUrlList(impressionUrlList);
	}

	/**
	 * 解析焦点图片
	 * 
	 * @param array
	 */
	private void parseTitleArticle(JSONArray array) {
		int length = array.length();
		JSONObject obj;
		ArticleItem titleArticle;
		for (int i = 0; i < length; i++) {
			obj = array.optJSONObject(i);
			// TODO 过滤老版本广告
			if (isNull(obj) || isAdv(obj))
				continue;
			titleArticle = new ArticleItem();
			titleArticle.setArticleId(obj.optInt("id", -1));
			titleArticle.setTitle(obj.optString("title", ""));
			titleArticle.setDesc(obj.optString("desc", ""));

			// 大图解析
			JSONArray picArr = obj.optJSONArray("picture");
			if (!isNull(picArr)) {
				titleArticle.setPicList(parsePicture(picArr));
			}

			// 列表图解析
			JSONArray thumbArr = obj.optJSONArray("thumb");
			if (!isNull(thumbArr)) {
				titleArticle.setThumbList(parsePicture(thumbArr));
			}

			titleArticle.setCatId(obj.optInt("catid", -1));
			titleArticle.setOutline(obj.optString("outline", ""));
			titleArticle.setWeburl(obj.optString("weburl", ""));
			titleArticle.setProperty(parseProperty(obj
					.optJSONObject("property")));

			JSONObject positionObj = obj.optJSONObject("position");
			if (!isNull(positionObj))
				titleArticle.setPosition(parseTitlePosition(positionObj));
			titleArticle.setSlateLink(obj.optString("link", ""));
			if (ConstData.getAppId() == 1) {// 商周
				// TODO 填添加该位置存在的广告
				getListFromMap(1).addAll(
						getTitleAdvsByPosition(currentPosition1));
				getListFromMap(1).add(titleArticle);
				currentPosition1++;
			} else {
				if (titleArticle.getPosition().getId() == 1) {
					getListFromMap(1).addAll(
							getTitleAdvsByPosition(currentPosition1));
					getListFromMap(1).add(titleArticle);
					currentPosition1++;
				} else if (titleArticle.getPosition().getId() == 2) {
					getListFromMap(2).addAll(
							getListAdvsByPosition(currentPosition2));
					getListFromMap(2).add(titleArticle);
					currentPosition2++;
				}
			}
		}
		// TODO 添加可能在列表末尾的广告
		if (ConstData.getAppId() == 1) {
			getListFromMap(1).addAll(
					getTitleAdvsByEndPosition(currentPosition2));
		} else {
			getListFromMap(1).addAll(
					getTitleAdvsByEndPosition(currentPosition2));
			getListFromMap(2)
					.addAll(getListAdvsByEndPosition(currentPosition2));
		}
	}

	private List<ArticleItem> getListFromMap(int position) {
		if (!indexArticle.getMap().containsKey(position)) {
			indexArticle.getMap().put(position, new ArrayList<ArticleItem>());
		}
		return indexArticle.getMap().get(position);
	}

	/**
	 * 解析title图片
	 * 
	 * @param array
	 */
	private List<String> parsePicture(JSONArray array) {
		List<String> pictureList = new ArrayList<String>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			pictureList.add(object.optString("url", ""));
		}
		return pictureList;
	}

	/**
	 * 解析焦点图片位置
	 * 
	 * @param obj
	 * @return
	 */
	private Position parseTitlePosition(JSONObject obj) {
		Position position = new Position();
		position.setId(obj.optInt("id", -1));
		position.setStyle(obj.optInt("style", -1));
		return position;
	}

	/**
	 * 解析today数据(暂不支持广告)
	 * 
	 * @param jsonObject
	 */
	private void parseToday(JSONArray arr) {
		int length = arr.length();
		List<Today> list = new ArrayList<Today>();
		Today today;
		JSONObject jsonObject;
		for (int i = 0; i < length; i++) {
			jsonObject = arr.optJSONObject(i);
			if (isNull(jsonObject))
				continue;
			today = new Today();
			today.setTodayCatId(jsonObject.optInt("id", -1));
			JSONArray articleArr = jsonObject.optJSONArray("article");
			if (!isNull(articleArr))
				today.setArticleItemList(parseTodayArticle(articleArr,
						today.getTodayCatId(), i));
			list.add(today);
		}
		indexArticle.setTodayList(list);
	}

	/**
	 * 解析today里的文章信息
	 * 
	 * @param array
	 * @return
	 */
	private List<ArticleItem> parseTodayArticle(JSONArray array, int catId,
			int pos) {
		List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();
		JSONObject obj;
		int length = array.length();
		ArticleItem articleItem;
		for (int i = 0; i < length; i++) {
			obj = array.optJSONObject(i);
			if (isNull(obj) || isAdv(obj))
				continue;
			articleItem = new ArticleItem();
			articleItem.setArticleId(obj.optInt("id", -1));
			articleItem.setTitle(obj.optString("title", ""));
			articleItem.setCatId(catId);
			articleItem.setDesc(obj.optString("desc", ""));
			articleItem.setOutline(obj.optString("outline", ""));
			articleItem.setSlateLink(obj.optString("link", ""));
			articleItem.setWeburl(obj.optString("weburl", ""));
			articleItem
					.setProperty(parseProperty(obj.optJSONObject("property")));

			// 大图解析
			JSONArray picArr = obj.optJSONArray("picture");
			if (!isNull(picArr)) {
				articleItem.setPicList(parsePicture(picArr));
			}

			// 列表图解析
			JSONArray thumbArr = obj.optJSONArray("thumb");
			if (!isNull(thumbArr)) {
				articleItem.setThumbList(parsePicture(thumbArr));
			}

			articleItemList.addAll(getBBIndexListAdv(pos, i));
			articleItemList.add(articleItem);
			if (i == 0) {
				// TODO 设置catid主要为了显示titlebar的标题和颜色
				articleItemList.get(0).setCatId(articleItem.getCatId());
				articleItemList.get(0).setShowTitleBar(true);
			}
			if (i == length - 1) {
				if (DataHelper.childMap.containsKey(catId)
						|| DataHelper.soloCatMap.containsKey(catId)) {
					articleItemList.get(articleItemList.size() - 1)
							.setShowMoreCat(true);
				}
			}
		}
		return articleItemList;
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
		if (ConstData.getAppId() == 1
				&& ModernMediaTools.checkNetWork(getmContext())
				&& indexArticle != null) {
			// 当有网并且请求成功，保存更新时间
			DataHelper.setIndexUpdateTime(getmContext(),
					ModernMediaTools.fetchTime(), 0);
		}
		String fileName = ConstData.getIndexFileName();
		if (!CommonApplication.columnUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getIndexFileName();
	}
}
