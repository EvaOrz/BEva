package cn.com.modernmedia.api;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.Adv;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.FileManager;

/**
 * ��װgetIssue�ӿڷ�������
 * 
 * @author ZhuQiao
 * 
 */
public class GetIssueOperate extends BaseOperate {
	private String url;
	private Issue issue;

	protected GetIssueOperate(int page, String issueId) {
		url = UrlMaker.getIssueUrl(page, issueId);
		issue = new Issue();
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
		parseIssueAdv(jsonObject);
		CommonApplication.currentIssue = issue;
		compareData(issue);
	}

	/**
	 * �������
	 * 
	 * @param obj
	 */
	private void parseIssueAdv(JSONObject obj) {
		JSONArray appadv = obj.optJSONArray("appadv");
		if (isNull(appadv)) {
			// ���û��appadv�����appadv���
			setAd(null, true);
			return;
		}
		JSONObject advappObj = appadv.optJSONObject(0);
		if (isNull(advappObj)) {
			// ���û��advappObj�����appadv���
			setAd(null, true);
			return;
		}
		Adv adv = issue.getAdv();
		adv.getColumnAdv().setId(advappObj.optInt("id", 0));
		adv.getColumnAdv().setStartTime(advappObj.optString("starttime", ""));
		adv.getColumnAdv().setEndTime(advappObj.optString("endtime", ""));
		JSONArray picture_h = advappObj.optJSONArray("picture_h");
		if (!isNull(picture_h)) {
			JSONObject pictureObj = picture_h.optJSONObject(0);
			if (!isNull(pictureObj))
				adv.getColumnAdv().setUrl(pictureObj.optString("url", ""));
		}
		setAd(adv, false);
	}

	private void setAd(Adv adv, boolean clear) {
		if (getmContext() != null) {
			if (clear || adv == null) {
				DataHelper.setStartTime(getmContext(), "");
				DataHelper.setEndTime(getmContext(), "");
				DataHelper.setAdvPic(getmContext(), "");
			} else {
				DataHelper.setStartTime(getmContext(), adv.getColumnAdv()
						.getStartTime());
				DataHelper.setEndTime(getmContext(), adv.getColumnAdv()
						.getEndTime());
				DataHelper
						.setAdvPic(getmContext(), adv.getColumnAdv().getUrl());
			}
		}
	}

	/**
	 * �ȽϷ��������ص������Լ����ػ��������
	 * 
	 * @param issue
	 */
	private void compareData(Issue issue) {
		// �Ƚϻ�ȡ����id����һ�α�����Ƿ���ͬ
		int oldIssueId = DataHelper.getIssueId(CommonApplication.mContext);
		if (oldIssueId == issue.getId()) {
			CommonApplication.issueIdSame = true;
		} else {
			if (oldIssueId != -1 || CommonApplication.isFetchPush) {
				CommonApplication.oldIssueId = oldIssueId;
				// ��ǰ�����һ�ڣ��������µ�һ��
				CommonApplication.hasNewIssue = true;
			}
			updateIssue(issue);
			updateColumn(issue);
			updateArticle(issue);
			return;
		}

		long oldColumnTime = DataHelper
				.getColumnUpdateTime(CommonApplication.mContext);
		if (issue.getColumnUpdateTime() == oldColumnTime) {
			CommonApplication.columnUpdateTimeSame = true;
		} else {
			updateColumn(issue);
			updateArticle(issue);
			return;
		}
		// �Ƚ����¸���ʱ��
		long oldArticleTime = DataHelper
				.getArticleUpdateTime(CommonApplication.mContext);
		if (issue.getArticleUpdateTime() == oldArticleTime) {
			CommonApplication.articleUpdateTimeSame = true;
		} else {
			updateArticle(issue);
		}
	}

	/**
	 * ����issue��ɾ��catList�����ļ�����Ҫ���»�ȡ
	 * 
	 * @param issue
	 */
	private void updateIssue(Issue issue) {
		CommonApplication.issueIdSame = false;
		if (!CommonApplication.hasNewIssue)
			DataHelper.setIssueId(CommonApplication.mContext, issue.getId());
	}

	/**
	 * ɾ��index��catindex�ļ�����Ҫ���»�ȡ
	 * 
	 * @param issue
	 */
	private void updateColumn(Issue issue) {
		CommonApplication.columnUpdateTimeSame = false;
		if (!CommonApplication.hasNewIssue)
			FileManager.deleteCatIndexFile();
		// TODO ����ȡindex�ɹ������
		// if (!MyApplication.hasNewIssue)
		// DataHelper.setColumnUpdateTime(MyApplication.mContext,
		// issue.getColumnUpdateTime());
	}

	/**
	 * ɾ�������б��ļ�����Ҫ���»�ȡ
	 * 
	 * @param issue
	 */
	private void updateArticle(Issue issue) {
		CommonApplication.articleUpdateTimeSame = false;
		if (!CommonApplication.hasNewIssue) {
			FileManager.deleteArticleFile();
			FileManager.deleteShareFile();
		}

		// DataHelper.setArticleUpdateTime(MyApplication.mContext,
		// issue.getArticleUpdateTime());
	}

	@Override
	protected void saveData(String data) {
		// ÿ�ζ���������d
		FileManager.saveApiData(ConstData.getIssueFileName(), data);
	}

	@Override
	protected String getDefaultFileName() {
		return ConstData.getIssueFileName();
	}
}
