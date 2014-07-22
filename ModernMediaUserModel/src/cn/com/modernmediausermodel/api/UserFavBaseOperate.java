package cn.com.modernmediausermodel.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.db.FavDb;
import cn.com.modernmediaslate.model.Favorite;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediaslate.model.Favorite.Property;
import cn.com.modernmediaslate.model.Favorite.Thumb;

/**
 * 用户模块收藏公共解析类
 * 
 * @author ZhuQiao
 * 
 */
public abstract class UserFavBaseOperate extends UserModelBaseOperate {
	private Favorite favorite;
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public UserFavBaseOperate() {
		favorite = new Favorite();
	}

	public Favorite getFavorite() {
		return favorite;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		favorite.setUid(jsonObject.optString("uid", ""));
		favorite.setAppid(jsonObject.optString("appid", "0"));
		JSONArray arr = jsonObject.optJSONArray("article");
		if (!isNull(arr)) {
			int length = arr.length();
			JSONObject obj;
			FavoriteItem item;
			for (int i = 0; i < length; i++) {
				obj = arr.optJSONObject(i);
				if (isNull(obj))
					continue;
				item = new FavoriteItem();
				item.setId(obj.optInt("id", 0));
				item.setTitle(obj.optString("title", ""));
				item.setDesc(obj.optString("desc", ""));
				item.setCatid(obj.optInt("catid", 0));
				item.setLink(obj.optString("link", ""));
				item.setIssueid(obj.optInt("issueid", 0));
				item.setUpdateTime(obj.optString("updateTime", ""));
				if (item.getUpdateTime().contains("年")) {
					item.setUpdateTime("0");
				}
				item.setFavtime(obj.optString("favtime", ""));
				if (item.getFavtime().contains("-")) {
					try {
						item.setFavtime(format.parse(item.getFavtime()) + "");
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (item.getFavtime().contains(FavDb.IOS)) {
					item.setFavtime(item.getFavtime().replace(FavDb.IOS, ""));
				}
				item.setFavdel(obj.optInt("favdel", 0));
				JSONArray thumbArr = obj.optJSONArray("thumb");
				if (!isNull(thumbArr)) {
					for (int j = 0; j < thumbArr.length(); j++) {
						JSONObject thumbObj = thumbArr.optJSONObject(j);
						if (!isNull(thumbObj)) {
							Thumb thumb = new Thumb();
							thumb.setUrl(thumbObj.optString("url", ""));
							item.getThumb().add(thumb);
						}
					}
				}
				item.setProperty(parseProperty(obj.optJSONObject("property")));
				item.setPagenum(obj.optInt("pagenum", 0));
				item.setTag(obj.optString("tag", ""));
				favorite.getList().add(item);
			}
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

}
