package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.model.Atlas.AtlasPicture;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediaslate.model.Favorite.Property;
import cn.com.modernmediaslate.model.Favorite.Thumb;

/**
 * 图集解析
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class GetArticleOperate extends BaseOperate {
	private String url = "";
	private Atlas atlas;
	private int issueId;
	private String columnId = "";
	private String articleId = "";

	protected GetArticleOperate(int issueId, String columnId, String articleId,
			String articleUpdateTime) {
		atlas = new Atlas();
		this.issueId = issueId;
		this.columnId = columnId;
		this.articleId = articleId;
		if (TextUtils.isEmpty(articleUpdateTime))
			articleUpdateTime = "0";
		url = UrlMaker.getArticleById(issueId + "", columnId, articleId,
				articleUpdateTime);
	}

	/**
	 * 独立栏目
	 * 
	 * @param issue
	 * @param detail
	 */
	protected GetArticleOperate(FavoriteItem detail) {
		atlas = new Atlas();
		issueId = detail.getIssueid();
		this.columnId = detail.getCatid() + "";
		this.articleId = detail.getId() + "";
		url = UrlMaker.getSoloArticleById(detail);
	}

	public Atlas getAtlas() {
		return atlas;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		atlas.setId(jsonObject.optInt("id", -1));
		atlas.setTitle(jsonObject.optString("title", ""));
		atlas.setDesc(jsonObject.optString("desc", ""));
		atlas.setLink(jsonObject.optString("link", ""));
		atlas.setWeburl(jsonObject.optString("weburl", ""));
		atlas.setUpdateTime(jsonObject.optString("updateTime", ""));
		JSONArray thumbArr = jsonObject.optJSONArray("thumb");
		if (!isNull(thumbArr)) {
			for (int j = 0; j < thumbArr.length(); j++) {
				JSONObject thumbObj = thumbArr.optJSONObject(j);
				if (!isNull(thumbObj)) {
					Thumb thumb = new Thumb();
					thumb.setUrl(thumbObj.optString("url", ""));
					atlas.getThumb().add(thumb);
				}
			}
		}
		atlas.setProperty(parseProperty(jsonObject.optJSONObject("property")));
		JSONArray arr = jsonObject.optJSONArray("picture");
		if (!isNull(arr))
			parsePicture(arr);
	}

	private void parsePicture(JSONArray arr) {
		int length = arr.length();
		AtlasPicture picture;
		JSONObject obj;
		for (int i = 0; i < length; i++) {
			obj = arr.optJSONObject(i);
			if (isNull(obj))
				continue;
			picture = new AtlasPicture();
			picture.setArticleId(atlas.getId());
			picture.setUrl(obj.optString("url", ""));
			picture.setDesc(obj.optString("desc", ""));
			picture.setTitle(obj.optString("title", ""));
			atlas.getList().add(picture);
		}
	}

	/**
	 * 解析文章属性
	 * 
	 * @param obj
	 * @return
	 */
	private Property parseProperty(JSONObject obj) {
		Property property = new Property();
		if (isNull(obj))
			return property;
		property.setType(obj.optInt("type", 1));
		property.setScrollHidden(obj.optInt("scrollHidden", 0));
		return property;
	}

	@Override
	protected void saveData(String data) {
		String fileName = ConstData.getArticleFileName(issueId + "", columnId,
				articleId);
		if (!CommonApplication.articleUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getArticleFileName(issueId + "", columnId, articleId);
	}

}
