package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.IndexProperty;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.model.ArticleItem.Position;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.util.ConstData;

/**
 * 获取文章详情
 * 
 * @author zhuqiao
 *
 */
public class GetArticleDetailsOperate extends BaseOperate {
	private int articleId;
	private TagArticleList articleList;

	public GetArticleDetailsOperate(int articleId) {
		this.articleId = articleId;
		articleList = new TagArticleList();
	}

	public TagArticleList getArticleList() {
		return articleList;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getArticleDetails(articleId + "", 1);
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray artcleArr = jsonObject.optJSONArray("article");
		if (!isNull(artcleArr)) {
			int length = artcleArr.length();
			JSONObject obj;
			for (int i = 0; i < length; i++) {
				obj = artcleArr.optJSONObject(i);
				if (isNull(obj))
					continue;
				parseArticleItem(obj);
			}
		}
	}

	private void parseArticleItem(JSONObject obj) {
		ArticleItem item = new ArticleItem();
		item.setJsonObject(obj.toString());
		item.setTagName(obj.optString("tagname"));
		item.setArticleId(obj.optInt("articleid", -1));
		item.setTitle(obj.optString("title", ""));
		item.setDesc(obj.optString("desc", ""));
		item.setAppid(obj.optInt("appid", ConstData.getInitialAppId()));
		item.setSlateLink(obj.optString("link", ""));
		item.setOffset(obj.optString("offset", ""));
		JSONArray links = obj.optJSONArray("links");
		if (!isNull(links)) {
			item.setSlateLinkList(parseLinks(links));
		}
		item.setAuthor(obj.optString("author", ""));
		item.setOutline(obj.optString("outline", ""));
		item.setInputtime(obj.optString("inputtime"));
		item.setUpdateTime(obj.optString("updatetime"));
		item.setWeburl(obj.optString("weburl", ""));
		item.setProperty(parseProperty(obj.optJSONObject("property")));
		item.setGroupname(obj.optString("groupname"));
		item.setApiTag(item.getTagName());
		item.setSubtitle(obj.optString("subtitle"));
		item.setCreateuser(obj.optString("createuser"));
		item.setModifyuser(obj.optString("modifyuser"));
		// 文章url地址解析
		JSONArray pageUrlList = obj.optJSONArray("phonepagelist");
		if (!isNull(pageUrlList)) {
			item.setPageUrlList(parsePageUrl(pageUrlList));
		}
		// 大图解析
		JSONArray picArr = obj.optJSONArray("picture");
		if (!isNull(picArr)) {
			item.setPicList(parsePicture(picArr));
		}
		// 列表图解析
		JSONArray thumbArr = obj.optJSONArray("thumb");
		if (!isNull(thumbArr)) {
			item.setThumbList(parseThumb(thumbArr));
		}
		// 图片位置
		item.setPosition(parsePosition(obj.optJSONObject("position")));
		articleList.setEndOffset(item.getOffset());

		articleList.getArticleList().add(item);
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
			linkList.add(object.optString("url"));
		}
		return linkList;
	}

	/**
	 * 解析图片
	 * 
	 * @param array
	 * @return
	 */
	private List<PhonePageList> parsePageUrl(JSONArray array) {
		List<PhonePageList> pageUrlList = new ArrayList<PhonePageList>();
		JSONObject object;
		PhonePageList page;
		int length = array.length();
		for (int i = 0; i < length; i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			page = new PhonePageList();
			page.setUrl(object.optString("url"));
			page.setTitle(object.optString("title"));
			page.setDesc(object.optString("desc"));
			page.setUri(object.optString("link"));
			pageUrlList.add(page);
		}
		return pageUrlList;
	}

	/**
	 * 解析图片
	 * 
	 * @param array
	 * @return
	 */
	private List<Picture> parsePicture(JSONArray array) {
		List<Picture> pictureList = new ArrayList<Picture>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			Picture picture = new Picture();
			picture.setUrl(object.optString("url"));
			picture.setVideolink(object.optString("videolink"));
			// iweekly视野使用
			picture.setBigimgurl(object.optString("bigimgurl"));
			pictureList.add(picture);
		}
		return pictureList;
	}

	/**
	 * 解析缩略图
	 * 
	 * @param array
	 * @return
	 */
	private List<Picture> parseThumb(JSONArray array) {
		List<Picture> pictureList = new ArrayList<Picture>();
		JSONObject object;
		for (int i = 0; i < array.length(); i++) {
			object = array.optJSONObject(i);
			if (isNull(object))
				continue;
			Picture picture = new Picture();
			picture.setUrl(object.optString("url"));
			pictureList.add(picture);
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
			position.setId(obj.optInt("positionid", -1));
			position.setStyle(obj.optInt("style", 1));
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
			property.setScrollHidden(obj.optInt("scrollHidden", 0));
			property.setHasvideo(obj.optInt("hasvideo", 0));
		}
		return property;
	}

	@Override
	protected void saveData(String data) {
	}

	@Override
	protected String getDefaultFileName() {
		return null;
	}

}
