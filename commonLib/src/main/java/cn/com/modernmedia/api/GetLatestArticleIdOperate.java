package cn.com.modernmedia.api;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.model.LastestArticleId;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取iweekly最新的文章id
 *
 * @author ZhuQiao
 *
 */
public class GetLatestArticleIdOperate extends BaseOperate {
	private static final String FILE_NAME = ConstData
			.getLastestArticleIdFileName();
	private String url;
	private ReadDb readDb;
	private LastestArticleId mLastestArticleId;
	private String mTagName;

	protected GetLatestArticleIdOperate(Context context, String tagName) {
		url = UrlMaker.getWeeklyLatestArticleId(tagName);
		readDb = ReadDb.getInstance(context);
		mLastestArticleId = new LastestArticleId();
		this.mTagName = tagName;
	}

	public LastestArticleId getmLastestArticleId() {
		return mLastestArticleId;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
		JSONArray array = jsonObject.optJSONArray("articletag");
		if (!isNull(array)) {
			int len = array.length();
			for (int i = 0; i < len; i++) {
				JSONObject articlesObj = array.optJSONObject(i);
				if (isNull(articlesObj))
					continue;
				String tagName = articlesObj.optString("tagname");
				JSONArray articles = articlesObj.optJSONArray("article");
				if (!isNull(articles)) {
					int count = articles.length();
					ArrayList<Integer> articleIdList = new ArrayList<Integer>();
					for (int j = 0; j < count; j++) {
						JSONObject articleObj = articles.optJSONObject(j);
						if (isNull(articleObj))
							continue;
						int articleId = articleObj.optInt("articleid");
						if (readDb.isReaded(articleId) == ReadDb.FLAG_ERROR) { // 未读
							articleIdList.add(articleId);
							if (!mLastestArticleId.getUnReadedId().contains(
									articleId))
								mLastestArticleId.getUnReadedId()
										.add(articleId);
						}
					}
					if (articleIdList.size() > 0) {
						map.put(tagName, articleIdList);
						mLastestArticleId.setUnReadedArticles(map);
					}

				}
			}
		}
	}

	@Override
	protected void saveData(String data) {
		FileManager.saveApiData(getDefaultFileName(), data);
	}

	@Override
	protected String getDefaultFileName() {
		String fileName = TextUtils.isEmpty(mTagName) ? FILE_NAME : FILE_NAME
				+ "_" + mTagName;
		return fileName;
	}
}
