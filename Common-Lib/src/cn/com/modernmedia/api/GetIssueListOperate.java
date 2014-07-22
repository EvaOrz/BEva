package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import cn.com.modernmedia.model.IssueList;
import cn.com.modernmedia.model.IssueList.IssueListItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.FileManager;

/**
 * 获取往期列表
 * 
 * @author ZhuQiao
 * 
 */
public class GetIssueListOperate extends BaseOperate {
	private int page;
	private IssueList issueList;

	protected GetIssueListOperate(Context context, int page) {
		this.page = page;
		issueList = new IssueList();
	}

	protected IssueList getIssueList() {
		return issueList;
	}

	@Override
	protected String getUrl() {
		return UrlMaker.getIssueList(page);
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		issueList.setTotal(jsonObject.optInt("total", 0));
		issueList.setNumperpage(jsonObject.optInt("numperpage", 0));
		JSONArray arr = jsonObject.optJSONArray("issue");
		if (!isNull(arr))
			parseIssue(arr);
	}

	/**
	 * 解析issue数组
	 * 
	 * @param arr
	 */
	private void parseIssue(JSONArray arr) {
		int length = arr.length();
		IssueListItem item;
		JSONObject obj;
		for (int i = 0; i < length; i++) {
			obj = arr.optJSONObject(i);
			if (isNull(obj))
				continue;
			item = new IssueListItem();
			item.setId(obj.optInt("id", 0));
			item.setTitle(obj.optString("title", ""));
			item.setStartTime(obj.optString("startTime", ""));
			item.setEndTime(obj.optString("endTime", ""));
			JSONArray picArr = obj.optJSONArray("issuepic");
			if (!isNull(picArr)) {
				for (int j = 0; j < picArr.length(); j++) {
					JSONObject picObj = picArr.optJSONObject(j);
					if (!isNull(picObj)) {
						item.getIssuepicList().add(picObj.optString("url", ""));
					}
				}
			}
			issueList.getList().add(item);
		}
	}

	@Override
	protected void saveData(String data) {
		FileManager.saveApiData(ConstData.getIssuelistFileName(page), data);
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getIssuelistFileName(page);
	}

}
