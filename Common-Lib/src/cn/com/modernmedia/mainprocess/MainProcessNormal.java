package cn.com.modernmedia.mainprocess;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.mainprocess.MainProcessManage.FetchDataCallBack;
import cn.com.modernmedia.mainprocess.MainProcessManage.ProcessType;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.util.ConstData.APP_TYPE;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.UpdateManager;
import cn.com.modernmedia.util.UpdateManager.CheckVersionListener;
import cn.com.modernmediaslate.model.Entry;

/**
 * 正常流程
 * 
 * @author user
 * 
 */
public class MainProcessNormal extends MainProcessSolo {
	public static final int DIALOG_ID = 0;
	private Context mContext;
	private boolean fromPreIssue = false;// 是否来自往期
	private boolean fromParse = false;// 是否来自推送文章返回;true:不提示最新一期，直接取，因为如果一开始不显示现在就看最新一期就进不了文章页

	public MainProcessNormal(Context context, FetchDataCallBack fetchCallBack) {
		super(context, fetchCallBack, ProcessType.Normal);
		mContext = context;
	}

	public void setFromPreIssue(boolean fromPreIssue) {
		this.fromPreIssue = fromPreIssue;
	}

	public void setFromParse(boolean fromParse) {
		this.fromParse = fromParse;
	}

	/**
	 * 比较版本
	 */
	public void checkVersion() {
		showLoad(true);
		if (CommonApplication.CHANNEL.equals("googleplay")) {
			getIssue("", false);
			return;
		}
		UpdateManager manager = new UpdateManager(mContext,
				new CheckVersionListener() {

					@Override
					public void checkEnd() {
						getIssue("", false);
					}
				});
		manager.checkVersion();
	}

	@Override
	protected void doAfterFetchIssue(Entry entry) {
		super.doAfterFetchIssue(entry);
		if (entry instanceof Issue) {
			if (fromPreIssue) {
				checkIssueId();
				return;
			}
			if (fromParse) {
				viewNow(DIALOG_ID);
				return;
			}
			// TODO 检测是否是新一期
			int oldIssueId = DataHelper.getIssueId(CommonApplication.mContext);
			if (oldIssueId != -1
					&& oldIssueId != CommonApplication.issue.getId()) {
				try {
					((CommonMainActivity) mContext).showDialog(DIALOG_ID);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				DataHelper
						.setIssueId(mContext, CommonApplication.issue.getId());
				if (hasAdv())
					getAdvList(false);
				else
					checkIssueId();
			}
		} else {
			showError();
		}
	}

	protected void doAfterFetchAdv(Entry entry) {
		super.doAfterFetchAdv(entry);
		if (!(entry instanceof AdvList)) {
			showError();
		}
	}

	@Override
	protected boolean doAfterFecthCatList(Entry entry) {
		if (!super.doAfterFecthCatList(entry)) {
			showError();
		}
		return true;
	}

	@Override
	protected void doAfterFetchSoloCatList(Entry entry) {
		super.doAfterFetchSoloCatList(entry);
		if (!(entry instanceof SoloColumn)) {
			showError();
		}
	}

	@Override
	protected void doAfterFetchIndex(Entry entry) {
		if (entry instanceof IndexArticle) {
			if (CommonApplication.issue != null) {
				DataHelper.setColumnUpdateTime(mContext,
						CommonApplication.issue.getColumnUpdateTime(),
						CommonApplication.issue.getId());
			}
			if (CommonApplication.appType != APP_TYPE.IWEEKLY) {
				// TODO iweekly还得走一个getLastestArticle接口
				fetchCallBack.afterFetch(mProcessType);
			}
			showLoad(false);
		} else {
			showError();
		}
		super.doAfterFetchIndex(entry);
	}

	@Override
	protected void doAfterFecthCatIndex(Entry entry) {
		if (entry instanceof CatIndexArticle) {
			if (CommonApplication.issue != null) {
				DataHelper.setColumnUpdateTime(mContext,
						CommonApplication.issue.getColumnUpdateTime(),
						CommonApplication.issue.getId());
			}
			if (CommonApplication.appType != APP_TYPE.IWEEKLY) {
				// TODO iweekly还得走一个getLastestArticle接口
				fetchCallBack.afterFetch(mProcessType);
			}
			showLoad(false);
		} else {
			showError();
		}
		super.doAfterFecthCatIndex(entry);
	}

	@Override
	protected void fetchSoloData(String from, String to) {
		if (from != null && !from.equals("0") && to != null && !to.equals("0")) {
			((CommonMainActivity) mContext).checkIndexLoading(1);
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (getArticleListFromDb()) {
						((CommonMainActivity) mContext).checkIndexLoading(0);
					} else {
						((CommonMainActivity) mContext).checkIndexLoading(2);
					}
				}
			});
		} else {
			getSoloArticleList(columnId, false, "0", "0", true);
		}
	}

	@Override
	protected void doAfterFetchData(CatIndexArticle catIndex, boolean fromDb,
			boolean newData) {
		showLoad(false);
		if (catIndex instanceof CatIndexArticle) {
			if (CommonApplication.appType != APP_TYPE.IWEEKLY) {
				// TODO iweekly还得走一个getLastestArticle接口
				fetchCallBack.afterFetch(mProcessType);
			}
		}
		super.doAfterFetchData(catIndex, fromDb, newData);
	}

	@Override
	protected void fecthDataError(boolean fromDb, boolean newData,
			boolean isPull) {
		super.fecthDataError(fromDb, newData, isPull);
		if (!isPull) {
			showError();
		}
	}

	@Override
	public void viewLater(int id) {
		getIssue(DataHelper.getIssueId(mContext) + "", false);
	}

	@Override
	public void viewNow(int id) {
		if (CommonApplication.issue != null)
			DataHelper.setIssueId(mContext, CommonApplication.issue.getId());
		if (hasAdv())
			getAdvList(false);
		else
			checkIssueId();
	}

}
