package cn.com.modernmedia.mainprocess;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cn.com.modernmedia.mainprocess.MainProcessManage.FetchDataCallBack;
import cn.com.modernmedia.mainprocess.MainProcessManage.ProcessType;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmediaslate.model.Entry;

/**
 * 往期流程
 * 
 * @author user
 * 
 */
public class MainProcessPreIssue extends MainProcessSolo {
	protected Map<String, FetchPreviousIssueCallBack> preMap = new HashMap<String, FetchPreviousIssueCallBack>();
	private PreIssusType preIssusType;

	public enum PreIssusType {
		REFRESH_INDEX/** 获取往期issue后刷新页面，显示往期的内容 **/
		, GO_TO_ARTICLE/** 获取往期issue后不更新首页，只跳至文章页 **/
		, Zip_GO_TO_ARTICLE/** 获取往期issue后不更新首页，下载zip包,跳至文章页 **/
		;
	}

	public interface FetchPreviousIssueCallBack {
		public void onSuccess(Issue issue);

		public void onFailed();
	}

	public MainProcessPreIssue(Context context, FetchDataCallBack fetchCallBack) {
		super(context, fetchCallBack, ProcessType.Last);
	}

	/**
	 * 查看往期
	 * 
	 * @param issueId
	 * @param callBack
	 */
	public void getPreIssue(int issueId, FetchPreviousIssueCallBack callBack,
			PreIssusType preIssusType) {
		this.preIssusType = preIssusType;
		addPreCallback(issueId + "", callBack);
		getIssue(issueId + "", false);
	}

	@Override
	protected void doAfterFetchIssue(Entry entry, String issueId) {
		FetchPreviousIssueCallBack callBack = getCallBack(issueId);
		if (entry instanceof Issue) {
			if (callBack != null) {
				if (preIssusType == PreIssusType.REFRESH_INDEX) {
					// TODO 刷新首页直接走normal的流程
				} else {
					callBack.onSuccess((Issue) entry);
				}
				addPreCallback(issueId, null);
			}
		} else {
			if (callBack != null) {
				callBack.onFailed();
				addPreCallback(issueId, null);
			}
		}
	}

	private FetchPreviousIssueCallBack getCallBack(String issueId) {
		return preMap.containsKey(issueId) ? preMap.get(issueId) : null;
	}

	private void addPreCallback(String issueId,
			FetchPreviousIssueCallBack callBack) {
		if (callBack != null)
			preMap.put(issueId, callBack);
		else {
			if (preMap.containsKey(issueId))
				preMap.remove(issueId);
		}
	}

	@Override
	public void viewLater(int id) {
	}

	@Override
	public void viewNow(int id) {
	}

	@Override
	protected void fetchSoloData(String from, String to) {
	}

}
