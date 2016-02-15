package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取往期文章
 * 
 * @author jiancong
 * 
 */
public class GetLastIssueArticlesOperate extends GetTagArticlesOperate {
	private String lastIssueTag = "";

	public GetLastIssueArticlesOperate(String lastIssueTag, String catTags,
			String top, String publishTime) {
		this.tagName = catTags;
		this.top = top;
		isCatIndex = false;
		this.url = UrlMaker.getLastArticlesByTag(tagName, top, publishTime);
		this.articleList = new TagArticleList();
		this.lastIssueTag = lastIssueTag;
		cacheIsDb = false;
	}

	public GetLastIssueArticlesOperate(String tagName) {
		this.articleList = new TagArticleList();
		this.lastIssueTag = tagName;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		JSONArray articletag = jsonObject.optJSONArray("articletag");
		if (isNull(articletag))
			return;
		parseArr(articletag);
	}

	@Override
	protected void saveData(String data) {
		FileManager.saveApiData(getDefaultFileName(), data);

	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getLastArticlesFileName(lastIssueTag);
	}

}
