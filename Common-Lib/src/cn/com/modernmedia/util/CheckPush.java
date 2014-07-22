package cn.com.modernmedia.util;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;

/**
 * 分析push信息
 * 
 * @author ZhuQiao
 * 
 */
public class CheckPush {
	private Context mContext;

	public CheckPush(Context context) {
		mContext = context;
	}

	public void parsePushMsg(Intent intent, Issue issue) {
		try {
			String msg = intent.getExtras().getString("com.parse.Data");
			if (!TextUtils.isEmpty(msg)) {
				JSONObject json = new JSONObject(msg);
				String na = json.optString("na", "");// 新文章
				JSONObject newissue = json.optJSONObject("newissue");// 新一期
				if (TextUtils.isEmpty(na)
						&& (JSONObject.NULL.equals(newissue) || newissue == null)) {
					if (issue != null) {
						// 不提示任何信息
						return;
					}
					if (isInstanceofMain())
						((CommonMainActivity) mContext).firstUseCache(true);
					return;
				}

				if (!TextUtils.isEmpty(na)) {
					// 进入文章 ==> 跳转至push文章页
					String[] arr = UriParse.parsePush(na);
					if (arr != null && arr.length == 3) {
						checkPushIssue(issue, ParseUtil.stoi(arr[0], -1),
								ParseUtil.stoi(arr[2], -1),
								ParseUtil.stoi(arr[1], -1));
					}
				} else {
					int newIssueId = newissue.optInt("issueid", -1);
					// 提示新一期
					checkPushIssue(issue, newIssueId, -1, -1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 比较本地锁存的issue和push消息里的issue
	 */
	private void checkPushIssue(Issue issue, int id, int articleId, int catId) {
		if (isInstanceofMain()) {
			if (((CommonMainActivity) mContext).getScrollView() != null)
				((CommonMainActivity) mContext).getScrollView().IndexClick();
		}
		int pushArticleId = -1, pushCatId = -1;
		// (如果用户本地没有旧一期的id，那么直接给他获取最新一期)
		if (DataHelper.getIssueId(mContext) == -1) {
			CommonApplication.isFetchPush = true;
			getIssue(true);
		} else if (id == 0 && articleId != -1 && catId != -1) {
			// 独立栏目
			pushArticleId = articleId;
			pushCatId = catId;
			setPushIds(pushArticleId, pushCatId);
			gotoArticleActivity(pushArticleId, pushCatId, 0);
		} else if (id != DataHelper.getIssueId(mContext)) {
			// (如果用户本地的缓存id与服务器推送消息的不一致，那么提示他是否查看最新一期)
			pushArticleId = articleId;
			pushCatId = catId;
			setPushIds(pushArticleId, pushCatId);
			if (isInstanceofMain())
				try {
					((CommonMainActivity) mContext).showDialog(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
		} else {
			// (如果用户本地的缓存id与服务器推送消息的一致，那么获取相关推送数据)
			pushArticleId = articleId;
			pushCatId = catId;
			setPushIds(pushArticleId, pushCatId);
			// 如果该文章在当前所看的这期中，直接调至文章页
			if (issue == null) {
				getIssue(true);
			} else {
				if (articleId != -1)
					gotoArticleActivity(pushArticleId, pushCatId, id);
			}
		}
	}

	/**
	 * 是否查看最新一期
	 * 
	 * @param viewNew
	 */
	public void viewNewIssue(boolean viewNew, Issue issue) {
		if (viewNew) {
			// 推送，有新的一期
			CommonApplication.isFetchPush = true;
			getIssue(true);
		} else {
			// 稍后查看
			// push消息首页
			if (issue == null) {
				CommonApplication.oldIssueId = DataHelper.getIssueId(mContext);
				getIssue(false);
			}
		}
	}

	private void getIssue(boolean fetchNew) {
		if (isInstanceofMain())
			((CommonMainActivity) mContext).getIssue(fetchNew);
	}

	private void gotoArticleActivity(int artcleId, int catId, int issueId) {
		if (isInstanceofMain()) {
			TransferArticle transferArticle = new TransferArticle(artcleId,
					catId, -1, issueId == 0 ? ArticleType.Solo
							: ArticleType.Default);
			((CommonMainActivity) mContext)
					.gotoArticleActivity(transferArticle);
		}
	}

	private void setPushIds(int pushArticleId, int pushCatId) {
		if (isInstanceofMain()) {
			((CommonMainActivity) mContext).setPushArticleId(pushArticleId);
			((CommonMainActivity) mContext).setPushCatId(pushCatId);
		}
	}

	private boolean isInstanceofMain() {
		return mContext instanceof CommonMainActivity;
	}

}
