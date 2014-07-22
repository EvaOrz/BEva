package cn.com.modernmedia.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.model.ArticleList.ArticleColumnList;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.model.ArticleList.Property;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取当期所有文章列表
 * 
 * @author ZhuQiao
 * 
 */
public class GetArticleListOperate extends BaseOperate {
	private String url = "";
	private ArticleList articleList;

	/**
	 * 
	 * @param issueId
	 *            期id
	 * @param articleUpdateTime
	 *            更新时间
	 */
	protected GetArticleListOperate(String issueId, String articleUpdateTime) {
		url = UrlMaker.getArticleList(issueId, articleUpdateTime);
		articleList = new ArticleList();
	}

	protected ArticleList getArticleList() {
		return articleList;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray arr = jsonObject.optJSONArray("column");
		if (!isNull(arr)) {
			int length = arr.length();
			JSONObject obj;
			ArticleColumnList column;
			for (int i = 0; i < length; i++) {
				obj = arr.optJSONObject(i);
				if (isNull(obj))
					continue;
				column = new ArticleColumnList();
				column.setId(obj.optInt("id", -1));
				JSONArray artcleArr = obj.optJSONArray("article");
				if (!isNull(artcleArr)) {
					column.setList(parseArtcle(artcleArr, column.getId()));
				}
				articleList.getList().add(column);
			}
		}
	}

	/**
	 * 解析文章详情
	 * 
	 * @param arr
	 * @return
	 */
	private List<ArticleDetail> parseArtcle(JSONArray arr, int catId) {
		List<ArticleDetail> list = new ArrayList<ArticleDetail>();
		int length = arr.length();
		JSONObject obj;
		ArticleDetail detail;
		for (int i = 0; i < length; i++) {
			obj = arr.optJSONObject(i);
			if (isNull(obj))
				continue;
			detail = new ArticleDetail();
			detail.setArticleId(obj.optInt("id", -1));
			detail.setTitle(obj.optString("title", ""));
			detail.setLink(obj.optString("link", ""));
			detail.setPagenum(obj.optInt("pagenum", -1));
			detail.setUpdateTime(obj.optString("updateTime"));
			detail.setCatId(catId);
			detail.setDesc(obj.optString("desc", ""));
			JSONArray thumbArr = obj.optJSONArray("thumb");
			if (!isNull(thumbArr)) {
				for (int j = 0; j < thumbArr.length(); j++) {
					JSONObject thumbObj = thumbArr.optJSONObject(j);
					if (isNull(thumbObj))
						continue;
					detail.getPictureList().add(thumbObj.optString("url", ""));
				}
			}
			detail.setProperty(parseProperty(obj.optJSONObject("property")));
			list.add(detail);
		}
		articleList.getAllArticleList().addAll(list);
		return list;
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
		property.setType(obj.optString("type", ""));
		property.setScrollHidden(obj.optInt("scrollHidden", 0));
		return property;
	}

	@Override
	protected void saveData(String data) {
		String fileName = ConstData.getArticleListFileName();
		if (!CommonApplication.articleUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getArticleListFileName();
	}

}
