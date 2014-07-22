package cn.com.modernmedia.api;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.listener.DataCallBack;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Issue;

/**
 * 接口控制
 * 
 * @author ZhuQiao
 * 
 */
public class OperateController {
	private Context mContext;

	public OperateController(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 获取最新一期杂志信息
	 * 
	 * @param fetchNew
	 *            是否查看的是新一期
	 */
	public void getIssue(final FetchEntryListener listener, boolean fetchNew) {
		String id = fetchNew ? "" : CommonApplication.oldIssueId + "";
		final GetIssueOperate operate = new GetIssueOperate(0, id);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				listener.setData(success ? operate.getIssue() : null);
			}
		});
	}

	/**
	 * 获取最新一期的栏目列表
	 * 
	 * @param Issue
	 * 
	 */
	public void getCatList(Issue issue, final FetchEntryListener listener) {
		if (issue == null) {
			listener.setData(null);
			return;
		}
		final GetCatListOperate operate = new GetCatListOperate(issue.getId());
		operate.asyncRequest(mContext, CommonApplication.columnUpdateTimeSame,
				new DataCallBack() {

					@Override
					public void callback(boolean success) {
						listener.setData(success ? operate.getCat() : null);
					}
				});
	}

	/**
	 * 获取首页数据
	 * 
	 * @param Issue
	 */
	public void getIndex(Issue issue, final FetchEntryListener listener) {
		if (issue == null) {
			listener.setData(null);
			return;
		}
		final GetIndexOperate operate = new GetIndexOperate(issue.getId() + "",
				issue.getColumnUpdateTime() + "");
		operate.asyncRequest(mContext, CommonApplication.columnUpdateTimeSame,
				new DataCallBack() {

					@Override
					public void callback(final boolean success) {
						listener.setData(success ? operate.getIndexArticle()
								: null);
					}
				});
	}

	/**
	 * 获取除首页以外的栏目首页
	 * 
	 * @param issue
	 * @param columnId
	 * @param listener
	 */
	public void getCartIndex(Issue issue, String columnId,
			final FetchEntryListener listener) {
		if (issue == null || TextUtils.isEmpty(columnId)) {
			listener.setData(null);
			return;
		}
		final GetCatIndexOperate operate = new GetCatIndexOperate(issue.getId()
				+ "", issue.getColumnUpdateTime() + "", columnId);
		operate.asyncRequest(mContext, CommonApplication.columnUpdateTimeSame,
				new DataCallBack() {

					@Override
					public void callback(boolean success) {
						listener.setData(success ? operate.getCatIndexArticle()
								: null);
					}
				});
	}

	/**
	 * 获取文章列表
	 * 
	 * @param issue
	 * @param listener
	 */
	public void getArticleList(Issue issue, final FetchEntryListener listener) {
		if (issue == null) {
			listener.setData(null);
			return;
		}
		final GetArticleListOperate operate = new GetArticleListOperate(
				issue.getId() + "", issue.getArticleUpdateTime() + "");
		operate.asyncRequest(mContext, CommonApplication.articleUpdateTimeSame,
				new DataCallBack() {

					@Override
					public void callback(boolean success) {
						listener.setData(success ? operate.getArticleList()
								: null);
					}
				});
	}

	/**
	 * 获取图集文章
	 * 
	 * @param issue
	 * @param columnId
	 * @param articleId
	 * @param listener
	 */
	public void getArticleById(Issue issue, String columnId, String articleId,
			final FetchEntryListener listener) {
		if (issue == null || TextUtils.isEmpty(columnId)
				|| TextUtils.isEmpty(articleId)) {
			listener.setData(null);
			return;
		}
		final GetArticleOperate operate = new GetArticleOperate(issue,
				columnId, articleId);
		operate.asyncRequest(mContext, true, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				listener.setData(success ? operate.getAtlas() : null);
			}
		});
	}

	/**
	 * 获取统计装机量是否成功
	 * 
	 * @param listener
	 */
	public void getDown(final FetchEntryListener listener) {
		final DownOperate operate = new DownOperate(mContext);
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				listener.setData(success ? operate.getDown() : null);
			}
		});
	}

	/**
	 * 判断版本号
	 * 
	 * @param listener
	 */
	public void checkVersion(final FetchEntryListener listener) {
		final CheckVersionOperate operate = new CheckVersionOperate();
		operate.asyncRequest(mContext, false, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				listener.setData(success ? operate.getVersion() : null);
			}
		});
	}

	/**
	 * 分享
	 * 
	 * @param issue
	 * @param columnId
	 * @param articleId
	 * @param shareType
	 * @param listener
	 */
	public void share(Issue issue, String columnId, String articleId,
			String shareType, final FetchEntryListener listener) {
		final ShareOperate operate = new ShareOperate(issue, columnId,
				articleId, shareType);
		operate.asyncRequest(mContext, true, new DataCallBack() {

			@Override
			public void callback(boolean success) {
				listener.setData(success ? operate.getShare() : null);
			}
		});
	}
}
