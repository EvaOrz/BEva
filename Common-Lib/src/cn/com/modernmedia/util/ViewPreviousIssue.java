package cn.com.modernmedia.util;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.model.Issue;

/**
 * 查看往期
 * 
 * @author ZhuQiao
 * 
 */
public class ViewPreviousIssue {
	/**
	 * 获取往期issue后刷新页面，显示往期的内容
	 */
	public static final int REFRESH_INDEX = 1;
	/**
	 * 获取往期issue后不更新首页，只跳至文章页
	 */
	public static final int GO_TO_ARTICLE = 2;

	private Context mContext;

	public interface FetchPreviousIssueCallBack {
		public void onSuccess(Issue issue);

		public void onFailed();
	}

	public ViewPreviousIssue(Context context) {
		mContext = context;
	}

	/**
	 * 获取往期issue
	 * 
	 * @param issueId
	 *            期id
	 * @param style
	 *            获取完往期后需要的操作{@link #REFRESH_INDEX},{@link #GO_TO_ARTICLE}
	 */
	public void getIssue(int issueId, int style,
			FetchPreviousIssueCallBack callBack) {
		if (isInstanceofMain()) {
			CommonApplication.currentIssueId = issueId;
			((CommonMainActivity) mContext).getPreIssue(issueId, callBack);
		}
	}

	private boolean isInstanceofMain() {
		return mContext instanceof CommonMainActivity;
	}

}
