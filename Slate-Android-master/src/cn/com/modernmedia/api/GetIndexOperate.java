package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Adv;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.IndexArticle.Position;
import cn.com.modernmedia.model.IndexArticle.Today;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取今日焦点首页数据
 * 
 * @author ZhuQiao
 * 
 */
public class GetIndexOperate extends BaseOperate {
	private String url = "";
	private IndexArticle indexArticle;

	protected GetIndexOperate(String issueId, String columnUpdateTime) {
		indexArticle = new IndexArticle();
		url = UrlMaker.getIndex(issueId, columnUpdateTime);
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
	}

	/**
	 * 解析焦点图片
	 * 
	 * @param array
	 */
	private void parseTitleArticle(JSONArray array) {
		int length = array.length();
		JSONObject obj;
		// List<ArticleItem> list = new ArrayList<ArticleItem>();
		List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();
		List<ArticleItem> titleActicleList = new ArrayList<ArticleItem>();
		ArticleItem titleArticle;
		for (int i = 0; i < length; i++) {
			obj = array.optJSONObject(i);
			if (isNull(obj))
				continue;
			titleArticle = new ArticleItem();
			titleArticle.setArticleId(obj.optInt("id", -1));
			titleArticle.setTitle(obj.optString("title", ""));
			titleArticle.setDesc(obj.optString("desc", ""));
			JSONArray pictureArr = obj.optJSONArray("picture");
			if (!isNull(pictureArr))
				titleArticle.setPictureList(parseTitlePicture(pictureArr));
			titleArticle.setCatId(obj.optInt("catid", -1));
			JSONObject positionObj = obj.optJSONObject("position");
			if (!isNull(positionObj))
				titleArticle.setPosition(parseTitlePosition(positionObj));
			titleArticle.setSlateLink(obj.optString("link", ""));
			Adv adv = parseAdv(obj);
			titleArticle.setAdv(adv);
			if (adv.getAdvProperty().getIsadv() == 1 && adv.isExpired()) {
				// 如果广告过期就不显示
				continue;
			}
			if (ConstData.getAppId() == 1) {// 商周
				titleActicleList.add(titleArticle);
			} else {
				if (titleArticle.getPosition().getId() == 1) {
					titleActicleList.add(titleArticle);
				} else {
					articleItemList.add(titleArticle);
				}
			}
		}
		indexArticle.setTitleArticleList(titleActicleList);
		indexArticle.setArticleItemList(articleItemList);
	}

	/**
	 * 解析title图片
	 * 
	 * @param array
	 */
	private List<String> parseTitlePicture(JSONArray array) {
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
	 * 解析today数据
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
						today.getTodayCatId()));
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
	private List<ArticleItem> parseTodayArticle(JSONArray array, int catId) {
		List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();
		JSONObject obj;
		int length = array.length();
		ArticleItem articleItem;
		for (int i = 0; i < length; i++) {
			obj = array.optJSONObject(i);
			if (isNull(obj))
				continue;
			articleItem = new ArticleItem();
			articleItem.setArticleId(obj.optInt("id", -1));
			articleItem.setTitle(obj.optString("title", ""));
			articleItem.setCatId(catId);
			articleItem.setDesc(obj.optString("desc", ""));
			if (i == 0)
				articleItem.setShowTitleBar(true);
			if (i == length - 1 && DataHelper.childMap.containsKey(catId))
				articleItem.setShowMoreCat(true);
			JSONArray thumbArr = obj.optJSONArray("thumb");
			if (!isNull(thumbArr))
				articleItem.setPictureList(parseThumb(thumbArr));
			articleItem.setSlateLink(obj.optString("link", ""));
			Adv adv = parseAdv(obj);
			articleItem.setAdv(adv);
			if (adv.getAdvProperty().getIsadv() == 1) {// 商周列表过滤掉广告
				if (adv.isExpired() || ConstData.getAppId() == 1) {
					// 如果广告过期就不显示
					continue;
				}
			}
			articleItemList.add(articleItem);
		}
		return articleItemList;
	}

	/**
	 * 解析文章对于的图片
	 * 
	 * @param array
	 * @return
	 */
	private List<String> parseThumb(JSONArray array) {
		List<String> thumbList = new ArrayList<String>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			thumbList.add(object.optString("url", ""));
		}
		return thumbList;
	}

	@Override
	protected void saveData(String data) {
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
