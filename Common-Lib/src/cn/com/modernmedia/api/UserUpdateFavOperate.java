package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.FavList;
import cn.com.modernmediaslate.model.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 提交收藏更新
 * 
 * @author ZhuQiao
 * 
 */
public class UserUpdateFavOperate extends UserFavBaseOperate {
	private String url;

	public UserUpdateFavOperate(String uid, int appid, List<ArticleItem> list) {
		super();
		url = UrlMaker.getUpdateFav(uid);
		FavList favorite = new FavList();
		favorite.setUid(uid);
		favorite.setAppid(appid);
		favorite.setArticle(list);
		postParams.add(new BasicNameValuePair("data", objectToJson(favorite)));
	}

	@Override
	protected String getUrl() {
		return url;
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

	@Override
	protected ArrayList<NameValuePair> getPostParams() {
		return postParams;
	}

	/**
	 * 把对象转换成json
	 * 
	 * @param entry
	 * @return
	 */
	private String objectToJson(Entry entry) {
		try {
			GsonBuilder builder = new GsonBuilder();
			// 不转换没有 @Expose 注解的字段
			builder.excludeFieldsWithoutExposeAnnotation();
			Gson gson = builder.create();
			return gson.toJson(entry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
