package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.common.Adv;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.IndexArticle.Position;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 除首页以外其他栏目首页数据
 * 
 * @author ZhuQiao
 * 
 */
public class GetCatIndexOperate extends BaseOperate {
	private String url = "";
	private CatIndexArticle catIndexArticle;
	private String columnId = "";

	/**
	 * 
	 * @param issueId
	 *            期id
	 * @param columnUpdateTime
	 *            更新时间
	 * @param columnId
	 *            column id
	 */
	protected GetCatIndexOperate(String issueId, String columnUpdateTime,
			String columnId) {
		catIndexArticle = new CatIndexArticle();
		this.columnId = columnId;
		url = UrlMaker.getCatIndex(issueId, columnUpdateTime, columnId);
	}

	protected CatIndexArticle getCatIndexArticle() {
		return catIndexArticle;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		catIndexArticle.setId(jsonObject.optInt("id", -1));
		JSONArray articleArr = jsonObject.optJSONArray("article");
		if (!isNull(articleArr))
			parseArticle(articleArr);
	}

	/**
	 * 解析文章数据
	 * 
	 * @param arr
	 */
	private void parseArticle(JSONArray arr) {
		List<ArticleItem> articleItemList = new ArrayList<ArticleItem>();
		List<ArticleItem> titleActicleList = new ArrayList<ArticleItem>();
		int length = arr.length();
		ArticleItem item;
		JSONObject obj;
		for (int i = 0; i < length; i++) {
			obj = arr.optJSONObject(i);
			if (isNull(obj))
				continue;

			item = new ArticleItem();
			Position position = new Position();
			item.setCatId(catIndexArticle.getId());
			item.setArticleId(obj.optInt("id", -1));
			item.setDesc(obj.optString("desc", ""));
			item.setTitle(obj.optString("title", ""));
			item.setSlateLink(obj.optString("link", ""));

			// 大图解析
			JSONArray picArr = obj.optJSONArray("picture");
			if (!isNull(picArr)) {
				item.setPictureList(parsePicture(picArr));
			}

			// 列表图解析
			JSONArray thumbArr = obj.optJSONArray("thumb");
			if (!isNull(thumbArr)) {
				item.setPictureList(parsePicture(thumbArr));
			}

			JSONObject positionObj = obj.optJSONObject("position");
			Adv adv = parseAdv(obj);
			if (adv.getAdvProperty().getIsadv() == 1 && adv.isExpired())
				continue;
			if (!isNull(positionObj)) {
				position = parsePosition(positionObj);
				item.setPosition(position);
				if (position.getId() == 1) {
					item.setAdv(adv);
					titleActicleList.add(item);
				} else {
					// 上周列表过滤掉广告
					if (ConstData.APP_ID == 1
							&& adv.getAdvProperty().getIsadv() == 1)
						continue;
					articleItemList.add(item);
				}
			}
		}
		catIndexArticle.setArticleItemList(articleItemList);
		catIndexArticle.setTitleActicleList(titleActicleList);
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
		position.setId(obj.optInt("id", -1));
		position.setStyle(obj.optInt("style", -1));
		return position;
	}

	@Override
	protected void saveData(String data) {
		String fileName = ConstData.getCatIndexFileName(columnId);
		if (!CommonApplication.columnUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getCatIndexFileName(columnId);
	}

}
