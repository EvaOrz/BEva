package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.IndexProperty;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.model.ArticleItem.Picture;
import cn.com.modernmedia.model.FavList;

/**
 * 用户模块收藏公共解析类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class UserFavBaseOperate extends BaseOperate {
	private FavList favorite;
	protected ArrayList<NameValuePair> postParams; // post参数

	public UserFavBaseOperate() {
		favorite = new FavList();
		postParams = new ArrayList<NameValuePair>();
	}

	public FavList getFavorite() {
		return favorite;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		favorite.setUid(jsonObject.optString("uid"));
		favorite.setAppid(jsonObject.optInt("appid"));
		JSONArray arr = jsonObject.optJSONArray("article");
		if (!isNull(arr)) {
			int length = arr.length();
			JSONObject obj;
			ArticleItem item;
			for (int i = 0; i < length; i++) {
				obj = arr.optJSONObject(i);
				if (isNull(obj))
					continue;
				item = new ArticleItem();
				item.setAppid(obj.optInt("appid"));
				item.setArticleId(obj.optInt("articleid"));
				item.setTitle(obj.optString("title"));
				item.setSubtitle(obj.optString("subtitle"));
				item.setDesc(obj.optString("desc"));
				item.setSlateLink(obj.optString("link"));
				item.setUpdateTime(obj.optString("updatetime"));
				// 文章url地址解析
				JSONArray pageUrlList = obj.optJSONArray("phonepagelist");
				if (!isNull(pageUrlList)) {
					item.setPageUrlList(parsePageUrl(pageUrlList));
				}
				item.setFavTime(obj.optString("favtime"));
				item.setFavDel(obj.optInt("isdelete"));
				JSONArray thumbArr = obj.optJSONArray("thumb");
				if (!isNull(thumbArr)) {
					for (int j = 0; j < thumbArr.length(); j++) {
						JSONObject thumbObj = thumbArr.optJSONObject(j);
						if (!isNull(thumbObj)) {
							Picture picture = new Picture();
							picture.setUrl(thumbObj.optString("url"));
							item.getThumbList().add(picture);
						}
					}
				}
				item.setProperty(parseProperty(obj.optJSONObject("property")));
				item.setTag(obj.optString("tag"));
				item.setTagName(obj.optString("tagname", ""));
				item.setWeburl(obj.optString("weburl"));
				favorite.getArticle().add(item);
			}
		}
	}

	/**
	 * 解析文章属性
	 * 
	 * @param obj
	 * @return
	 */
	private IndexProperty parseProperty(JSONObject obj) {
		IndexProperty property = new IndexProperty();
		if (isNull(obj))
			return property;
		property.setLevel(obj.optInt("level", 0));
		property.setType(obj.optInt("type", 1));
		property.setHavecard(obj.optInt("havecard", 1));
		property.setScrollHidden(obj.optInt("scrollHidden", 0));
		property.setHasvideo(obj.optInt("hasvideo", 0));
		return property;
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
			page.setUri(object.optString("uri"));
			pageUrlList.add(page);
		}
		return pageUrlList;
	}

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void saveData(String data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getDefaultFileName() {
		// TODO Auto-generated method stub
		return null;
	}
}
