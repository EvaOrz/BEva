package cn.com.modernmedia.api;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.model.ArticleList.ArticleColumnList;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediaslate.model.Favorite.Property;
import cn.com.modernmediaslate.model.Favorite.Thumb;

/**
 * 获取当期所有文章列表
 * 
 * @author ZhuQiao
 * 
 */
public class GetArticleListOperate extends BaseOperate {
	private String url = "";
	private ArticleList articleList;
	private boolean isSolo = false;
	private int issueId;

	/**
	 * 非独立栏目文章列表
	 * 
	 * @param issueId
	 *            期id
	 * @param articleUpdateTime
	 *            更新时间
	 */
	protected GetArticleListOperate(String issueId, String articleUpdateTime) {
		url = UrlMaker.getArticleList(issueId, articleUpdateTime);
		articleList = new ArticleList();
		isSolo = false;
		this.issueId = ParseUtil.stoi(issueId, -1);
	}

	/**
	 * 独立栏目文章列表
	 * 
	 * @param context
	 * @param catId
	 * @param fromOffset
	 * @param toOffset
	 * @param fetchNew
	 *            是否获取新的:true,只保存lasttooffset,false,只保存lastfromoffset(当0_0时两个都保存
	 *            )
	 */
	public GetArticleListOperate(Context context, String catId,
			String fromOffset, String toOffset, boolean fetchNew) {
		url = UrlMaker.getSoloArticleList(catId, fromOffset, toOffset);
		articleList = new ArticleList();
		isSolo = true;
		issueId = 0;
	}

	public ArticleList getArticleList() {
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
			for (int i = 0; i < length; i++) {
				obj = arr.optJSONObject(i);
				if (isNull(obj))
					continue;
				parseArtcle(obj, obj.optInt("id", -1));
			}
			// 批量添加数据库
			if (isSolo && ParseUtil.listNotNull(articleList.getList())) {
				for (ArticleColumnList column : articleList.getList()) {
					if (column.getId() != -1) {
						addSoloArticleListDb(column.getId(), column.getList());
					}
				}
			}
		}
	}

	/**
	 * 解析文章详情
	 * 
	 * @param arr
	 * @return
	 */
	private void parseArtcle(JSONObject jsonObject, int catId) {
		ArticleColumnList column = new ArticleColumnList();
		column.setId(catId);
		JSONArray artcleArr = jsonObject.optJSONArray("article");
		if (isNull(artcleArr)) {
			articleList.setHasData(false);
			return;
		}
		articleList.getList().add(column);
		int length = artcleArr.length();
		JSONObject obj;
		for (int i = 0; i < length; i++) {
			obj = artcleArr.optJSONObject(i);
			if (isNull(obj))
				continue;
			parseArticleItem(obj, catId, column, i == 0, i == length - 1);
		}
	}

	public void parseArticleItem(JSONObject obj, int catId,
			ArticleColumnList column, boolean isFirst, boolean isLast) {
		FavoriteItem detail = new FavoriteItem();
		detail.setId(obj.optInt("id", -1));
		detail.setIssueid(issueId);
		detail.setTitle(obj.optString("title", ""));
		detail.setLink(obj.optString("link", ""));
		detail.setPagenum(obj.optInt("pagenum", -1));
		detail.setUpdateTime(obj.optString("updateTime", ""));
		detail.setCatid(catId);
		detail.setDesc(obj.optString("desc", ""));
		detail.setUpdateTime(obj.optString("updateTime", ""));
		detail.setOffset(obj.optString("offset", ""));
		detail.setTag(obj.optString("tag", ""));
		JSONArray thumbArr = obj.optJSONArray("thumb");
		if (!isNull(thumbArr)) {
			for (int j = 0; j < thumbArr.length(); j++) {
				JSONObject thumbObj = thumbArr.optJSONObject(j);
				if (!isNull(thumbObj)) {
					Thumb thumb = new Thumb();
					thumb.setUrl(thumbObj.optString("url", ""));
					detail.getThumb().add(thumb);
				}
			}
		}
		detail.setProperty(parseProperty(obj.optJSONObject("property")));
		// TODO 添加整体广告
		articleList.getAllArticleList().add(detail);
		// TODO 添加单独栏目广告
		column.getList().add(detail);

		if (isSolo) {
			// TODO 独立栏目只有一个子集,所以不需要考虑是否被最外层的循环覆盖
			saveOffset(detail, isFirst, isLast);
			// 判断是否更新数据库
			detail.setJsonObject(obj.toString());
		}
	}

	/**
	 * 保存最新的offset
	 * 
	 * @param item
	 * @param isFirst
	 * @param isLast
	 */
	private void saveOffset(FavoriteItem item, boolean isFirst, boolean isLast) {
		if (isFirst)
			articleList.setToOffset(item.getOffset());
		else if (isLast)
			articleList.setFromOffset(item.getOffset());
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
		if (isSolo)
			return;
		String fileName = ConstData.getArticleListFileName();
		if (!CommonApplication.articleUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		if (isSolo)
			return null;
		return ConstData.getArticleListFileName();
	}

	public void addSoloArticleListDb(int catId, List<FavoriteItem> list) {

	}

}
