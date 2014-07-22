package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.model.Atlas.AtlasPicture;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * Í¼¼¯½âÎö
 * 
 * @author ZhuQiao
 * 
 */
public class GetArticleOperate extends BaseOperate {
	private String url = "";
	private Atlas atlas;
	private Issue issue;
	private String columnId = "";
	private String articleId = "";

	protected GetArticleOperate(Issue issue, String columnId, String articleId) {
		atlas = new Atlas();
		this.issue = issue;
		this.columnId = columnId;
		this.articleId = articleId;
		url = UrlMaker.getArticleById(issue.getId() + "", columnId, articleId,
				issue.getArticleUpdateTime() + "");
	}

	protected Atlas getAtlas() {
		return atlas;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		atlas.setId(jsonObject.optInt("id", -1));
		atlas.setDesc(jsonObject.optString("desc", ""));
		atlas.setLink(jsonObject.optString("link", ""));
		atlas.setWeburl(jsonObject.optString("weburl", ""));
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
			picture.setUrl(obj.optString("url", ""));
			picture.setDesc(obj.optString("desc", ""));
			picture.setTitle(obj.optString("title", ""));
			atlas.getList().add(picture);
		}
	}

	@Override
	protected void saveData(String data) {
		if (issue == null)
			return;
		String fileName = ConstData.getArticleFileName(issue.getId() + "",
				columnId, articleId);
		if (!CommonApplication.articleUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		if (issue == null)
			return "";
		return ConstData.getArticleFileName(issue.getId() + "", columnId,
				articleId);
	}

}
