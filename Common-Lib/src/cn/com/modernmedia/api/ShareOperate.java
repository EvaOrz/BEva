package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.model.Share;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 
 * @author ZhuQiao
 * 
 */
public class ShareOperate extends BaseOperate {
	private String url = "";
	private Issue issue;
	private String columnId = "";
	private String articleId = "";
	private String shareType = "";
	private Share share;

	protected ShareOperate(Issue issue, String columnId, String articleId,
			String shareType) {
		this.issue = issue;
		this.columnId = columnId;
		this.articleId = articleId;
		this.shareType = shareType;
		url = UrlMaker.share(issue.getId() + "", columnId, articleId,
				issue.getArticleUpdateTime() + "", shareType);
		share = new Share();
	}

	protected Share getShare() {
		return share;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		share.setId(jsonObject.optInt("id", -1));
		share.setTitle(jsonObject.optString("title", ""));
		share.setContent(jsonObject.optString("content", ""));
		share.setWeburl(jsonObject.optString("weburl", ""));
		parsePicture(jsonObject.optJSONArray("picture"));
	}

	/**
	 * 解析图片
	 * 
	 * @param arr
	 */
	private void parsePicture(JSONArray arr) {
		if (isNull(arr))
			return;
		JSONObject object;
		for (int i = 0; i < arr.length(); i++) {
			object = arr.optJSONObject(i);
			if (isNull(object))
				continue;
			share.getPicList().add(object.optString("url", ""));
		}
	}

	@Override
	protected void saveData(String data) {
		if (issue == null)
			return;
		String fileName = ConstData.getShareFileName(issue.getId() + "",
				columnId, articleId, shareType);
		if (!CommonApplication.articleUpdateTimeSame
				|| !FileManager.containFile(fileName)) {
			FileManager.saveApiData(fileName, data);
		}
	}

	@Override
	protected String getDefaultFileName() {
		if (issue == null)
			return "";
		return ConstData.getShareFileName(issue.getId() + "", columnId,
				articleId, shareType);
	}

}
