package cn.com.modernmedia.api;

import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;

/**
 * 封装getIssue接口返回数据
 * 
 * @author ZhuQiao
 * 
 */
public class GetIssueOperate extends BaseOperate {
	private String url;
	private Issue issue;
	private String issueId;
	private boolean hasNewIssue;

	protected GetIssueOperate(int page, String issueId) {
		url = UrlMaker.getIssueUrl(page, issueId);
		issue = new Issue();
		this.issueId = issueId;
		hasNewIssue = false;
		DataHelper.clear();
		DataHelper.clearSoloMap();
	}

	protected Issue getIssue() {
		return issue;
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected void handler(JSONObject jsonObject) {
		issue.setId(jsonObject.optInt("id", -1));
		issue.setCoverpic(jsonObject.optString("coverpic", ""));
		issue.setColumnUpdateTime(jsonObject.optLong("columnUpdateTime", -1));
		issue.setArticleUpdateTime(jsonObject.optLong("articleUpdateTime", -1));
		issue.setFullPackage(jsonObject.optString("fullpackage", ""));
		// CommonApplication.currentIssue = issue;
		CommonApplication.currentIssueId = issue.getId();
		compareData(issue);
	}

	/**
	 * 比较服务器返回的数据以及本地缓存的数据
	 * 
	 * @param issue
	 */
	private void compareData(Issue issue) {
		if (!TextUtils.isEmpty(issueId)) {
			// TODO 如果是获取特定一期，比较这一期的updatetime
		} else {
			// 比较获取的期id与上一次保存的是否相同
			int oldIssueId = DataHelper.getIssueId(CommonApplication.mContext);
			if (oldIssueId == issue.getId()) {
				CommonApplication.issueIdSame = true;
			} else {
				if (oldIssueId != -1) {
					// 以前保存过一期，但有了新的一期
					hasNewIssue = true;
				}
				updateIssue(issue);
				updateColumn(issue);
				updateArticle(issue);
				return;
			}
		}

		long oldColumnTime = DataHelper.getColumnUpdateTime(
				CommonApplication.mContext, issue.getId());
		if (issue.getColumnUpdateTime() == oldColumnTime) {
			CommonApplication.columnUpdateTimeSame = true;
		} else {
			updateColumn(issue);
			updateArticle(issue);
			return;
		}
		// 比较文章更新时间
		long oldArticleTime = DataHelper.getArticleUpdateTime(
				CommonApplication.mContext, issue.getId());
		if (issue.getArticleUpdateTime() == oldArticleTime) {
			CommonApplication.articleUpdateTimeSame = true;
		} else {
			updateArticle(issue);
		}
	}

	/**
	 * 更新issue病删除catList本地文件，需要重新获取
	 * 
	 * @param issue
	 */
	private void updateIssue(Issue issue) {
		CommonApplication.issueIdSame = false;
		if (!hasNewIssue)
			DataHelper.setIssueId(CommonApplication.mContext, issue.getId());
	}

	/**
	 * 删除index和catindex文件，需要重新获取
	 * 
	 * @param issue
	 */
	private void updateColumn(Issue issue) {
		CommonApplication.columnUpdateTimeSame = false;
		if (!hasNewIssue)
			FileManager.deleteCatIndexFile();
		// TODO 当获取index成功后更新
		// if (!MyApplication.hasNewIssue)
		// DataHelper.setColumnUpdateTime(MyApplication.mContext,
		// issue.getColumnUpdateTime());
	}

	/**
	 * 删除文章列表文件，需要重新获取
	 * 
	 * @param issue
	 */
	private void updateArticle(Issue issue) {
		CommonApplication.articleUpdateTimeSame = false;
		if (!hasNewIssue) {
			FileManager.deleteArticleFile();
			FileManager.deleteShareFile();
		}

		// DataHelper.setArticleUpdateTime(MyApplication.mContext,
		// issue.getArticleUpdateTime());
	}

	@Override
	protected void saveData(String data) {
		// 每次都保存最新d
		FileManager.saveApiData(ConstData.getIssueFileName(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getIssueFileName();
	}
}
