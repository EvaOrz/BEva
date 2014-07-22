package cn.com.modernmedia.mainprocess;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.mainprocess.MainProcessManage.FetchDataCallBack;
import cn.com.modernmedia.mainprocess.MainProcessManage.ParseType;
import cn.com.modernmedia.mainprocess.MainProcessManage.ProcessType;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.UriParse;
import cn.com.modernmediaslate.model.Entry;

/**
 * 推送流程
 * 
 * @author user
 * 
 */
public class MainProcessParse extends MainProcessSolo {
	public static final int DIALOG_ID_ISSUE = 1;
	public static final int DIALOG_ID_ARTICLE = 2;

	private Context mContext;
	private int pushIssueId, pushCatId, pushArticleId;
	private boolean shouldGotoArticle = false;

	public MainProcessParse(Context context, FetchDataCallBack fetchCallBack) {
		super(context, fetchCallBack, ProcessType.Parse);
		mContext = context;
	}

	public void parsePushMsg(Intent intent) {
		try {
			String msg = intent.getExtras().getString("com.parse.Data");
			if (!TextUtils.isEmpty(msg)) {
				JSONObject json = new JSONObject(msg);
				String na = json.optString("na", "");// 新文章
				JSONObject newissue = json.optJSONObject("newissue");// 新一期
				if (TextUtils.isEmpty(na)
						&& (JSONObject.NULL.equals(newissue) || newissue == null)) {
					if (CommonApplication.issue != null) {
						// 不提示任何信息
						return;
					}
					fetchCallBack.afterParse(ParseType.LoadCache);
					return;
				}

				if (!TextUtils.isEmpty(na)) {
					// 进入文章 ==> 跳转至push文章页
					String[] arr = UriParse.parsePush(na);
					if (arr != null && arr.length == 3) {
						showArticle(ParseUtil.stoi(arr[0], -1),
								ParseUtil.stoi(arr[2], -1),
								ParseUtil.stoi(arr[1], -1));
					}
				} else {
					int newIssueId = newissue.optInt("issueid", -1);
					// 提示新一期
					showNewIssue(newIssueId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提示新一期
	 */
	private void showNewIssue(int newIssueId) {
		if (DataHelper.getIssueId(mContext) == -1) {
			// TODO 如果用户本地没有旧一期的id，那么直接给他获取最新一期
			fetchCallBack.afterParse(ParseType.LoadHttp);
		} else if (DataHelper.getIssueId(mContext) != newIssueId) {
			// TODO 缓存id!=推送id,提示新一期
			((CommonMainActivity) mContext).showDialog(DIALOG_ID_ISSUE);
		} else {
			// TODO 相同
			if (CommonApplication.issue == null) {
				fetchCallBack.afterParse(ParseType.LoadCache);
			}
		}
	}

	/**
	 * 显示文章
	 */
	private void showArticle(int id, int articleId, int catId) {
		pushIssueId = id;
		pushArticleId = articleId;
		pushCatId = catId;
		if (CommonApplication.issue == null) {
			// TODO 没打开过首页
			showMainProcess(1);
			getIssue("", false);
		} else {
			if (id == 0) {
				// TODO 独立栏目
				if (articleId != -1 && catId != -1) {
					gotoArticleActivity(articleId, catId, 0);
				}
			} else if (id != DataHelper.getIssueId(mContext)) {
				// TODO 有新一期
				((CommonMainActivity) mContext).showDialog(DIALOG_ID_ARTICLE);
			} else {
				// TODO 相同期
				gotoArticleActivity(articleId, catId, id);
			}
		}
	}

	@Override
	protected void doAfterFetchIssue(Entry entry) {
		super.doAfterFetchIssue(entry);
		if (entry instanceof Issue) {
			showMainProcess(0);
			if (shouldGotoArticle) {
				shouldGotoArticle = false;
				gotoArticleActivity(pushArticleId, pushCatId, pushIssueId);
			} else {
				showArticle(pushIssueId, pushArticleId, pushCatId);
			}
		} else {
			showMainProcess(2);
		}
	}

	/**
	 * 跳转至文章页
	 * 
	 * @param articleId
	 * @param catId
	 * @param issueId
	 */
	private void gotoArticleActivity(int articleId, int catId, int issueId) {
		((CommonMainActivity) mContext).setPushArticleId(articleId);
		TransferArticle transferArticle = new TransferArticle(articleId, catId,
				-1, issueId == 0 ? ArticleType.Solo : ArticleType.Default);
		((CommonMainActivity) mContext).gotoArticleActivity(transferArticle);
	}

	@Override
	public void viewLater(int id) {
		if (CommonApplication.issue == null || isFirstIn) {
			if (id == DIALOG_ID_ISSUE) {
				if (DataHelper.getIssueId(mContext) == -1) {
					fetchCallBack.afterParse(ParseType.LoadHttp);
				} else {
					fetchCallBack.afterParse(ParseType.LoadOnlyCache);
				}
			}
		}
		if (id == DIALOG_ID_ARTICLE && isFirstIn) {
			if (DataHelper.getIssueId(mContext) == -1) {
				fetchCallBack.afterParse(ParseType.LoadHttp);
			} else {
				fetchCallBack.afterParse(ParseType.LoadOnlyCache);
			}
		}
	}

	@Override
	public void viewNow(int id) {
		if (id == DIALOG_ID_ISSUE) {
			fetchCallBack.afterParse(ParseType.LoadHttp);
		} else if (id == DIALOG_ID_ARTICLE) {
			shouldGotoArticle = true;
			getIssue("", false);
		}
	}

	@Override
	protected void fetchSoloData(String from, String to) {
	}
}
