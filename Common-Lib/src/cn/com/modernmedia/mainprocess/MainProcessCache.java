package cn.com.modernmedia.mainprocess;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.mainprocess.MainProcessManage.FetchDataCallBack;
import cn.com.modernmedia.mainprocess.MainProcessManage.ProcessType;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.util.ConstData.APP_TYPE;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmediaslate.model.Entry;

/**
 * 使用缓存流程
 * 
 * @author user
 * 
 */
public class MainProcessCache extends MainProcessSolo {
	private Context mContext;

	public MainProcessCache(Context context, FetchDataCallBack fetchCallBack) {
		super(context, fetchCallBack, ProcessType.Cache);
		mContext = context;
	}

	/**
	 * 获取本地缓存issue
	 */
	public void getIssueById() {
		int issueId = DataHelper.getIssueId(mContext);
		if (issueId == -1) {
			fetchCallBack.afterFetch(ProcessType.Cache);
		} else {
			showMainProcess(1);
			getIssue(issueId + "", true);
		}
	}

	@Override
	protected void doAfterFetchIssue(Entry entry) {
		super.doAfterFetchIssue(entry);
		if (entry instanceof Issue) {
			if (hasAdv())
				getAdvList(true);
			else
				checkIssueId();
		} else {
			fetchCallBack.afterFetch(mProcessType);
		}
	}

	@Override
	protected void doAfterFetchAdv(Entry entry) {
		super.doAfterFetchAdv(entry);
		if (!(entry instanceof AdvList)) {
			fetchCallBack.afterFetch(mProcessType);
		}
	}

	@Override
	protected boolean doAfterFecthCatList(Entry entry) {
		if (!super.doAfterFecthCatList(entry)) {
			fetchCallBack.afterFetch(mProcessType);
		}
		return true;
	}

	@Override
	protected void doAfterFetchIndex(Entry entry) {
		super.doAfterFetchIndex(entry);
		if (CommonApplication.appType != APP_TYPE.IWEEKLY) {
			// TODO iweekly还得走一个getLastestArticle接口
			fetchCallBack.afterFetch(mProcessType);
		}
	}

	@Override
	protected void doAfterFetchSoloCatList(Entry entry) {
		super.doAfterFetchSoloCatList(entry);
		if (!(entry instanceof SoloColumn)) {
			fetchCallBack.afterFetch(mProcessType);
		}
	}

	@Override
	protected void doAfterFecthCatIndex(Entry entry) {
		super.doAfterFecthCatIndex(entry);
		if (CommonApplication.appType != APP_TYPE.IWEEKLY) {
			// TODO iweekly还得走一个getLastestArticle接口
			fetchCallBack.afterFetch(mProcessType);
		}
	}

	@Override
	protected void fetchSoloData(String from, String to) {
		if (!getArticleListFromDb())
			if (CommonApplication.appType != APP_TYPE.IWEEKLY) {
				// TODO iweekly还得走一个getLastestArticle接口
				fetchCallBack.afterFetch(mProcessType);
			}
	}

	@Override
	protected void doAfterFetchData(CatIndexArticle catIndex, boolean fromDb,
			boolean newData) {
		super.doAfterFetchData(catIndex, fromDb, newData);
		if (CommonApplication.appType != APP_TYPE.IWEEKLY) {
			// TODO iweekly还得走一个getLastestArticle接口
			fetchCallBack.afterFetch(mProcessType);
		}
	}

	@Override
	public void viewLater(int id) {
	}

	@Override
	public void viewNow(int id) {
	}

}
