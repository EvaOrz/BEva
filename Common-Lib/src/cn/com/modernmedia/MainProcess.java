package cn.com.modernmedia;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity.UseCacheCallBack;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.LoadIssueType;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmedia.util.UpdateManager;
import cn.com.modernmedia.util.UpdateManager.CheckVersionListener;
import cn.com.modernmedia.util.ViewPreviousIssue.FetchPreviousIssueCallBack;
import cn.com.modernmediaslate.model.Entry;

public class MainProcess {
	private Context mContext;
	private OperateController mController;
	private Issue issue;
	private Cat cat;
	private boolean isFirstIn = true;// 显示切换动画
	private Handler handler = new Handler();
	private UseCacheCallBack mCacheCallBack;

	/**
	 * 1--getissue;2--getcatlist;3--getindex;4--getcatindex;5---solocatlist(
	 * 独立栏目列表);6--getSoloColumnIndex;7---advlist
	 */
	private int errorType;
	@SuppressLint("UseSparseArrays")
	protected Map<String, FetchPreviousIssueCallBack> preMap = new HashMap<String, FetchPreviousIssueCallBack>();

	public MainProcess(Context context, UseCacheCallBack cacheCallBack) {
		mContext = context;
		mCacheCallBack = cacheCallBack;
		mController = OperateController.getInstance(mContext);
		preMap.clear();
	}

	/**
	 * 是否首先使用缓存
	 * 
	 * @param isFirst
	 *            true:是；otherwise：否
	 */
	public void firstUseCache(boolean isFirst) {
		CommonApplication.oldIssueId = DataHelper.getIssueId(mContext);
		if (CommonApplication.oldIssueId == -1 && isFirst) {
			mCacheCallBack.afterUse(false);
			return;
		}
		// 从首页进来
		if (CommonApplication.CHANNEL.equals("googleplay") || isFirst) {
			ModernMediaTools.showLoading(mContext, true);
			((CommonMainActivity) mContext).getIssue(!isFirst);
		} else {
			checkVersion();
		}
	}

	/**
	 * 比较版本
	 */
	private void checkVersion() {
		showMainProcess(1);
		UpdateManager manager = new UpdateManager(mContext,
				new CheckVersionListener() {

					@Override
					public void checkEnd() {
						((CommonMainActivity) mContext).getIssue(true);
					}
				});
		manager.checkVersion();
	}

	/**
	 * 获取最新一期杂志信息 查看上一期
	 * 
	 * @param fetchNew
	 *            是否查看新一期(第一次进应用默认true)
	 * @param useCache
	 */
	public synchronized void getIssue(final boolean fetchNew,
			final boolean useCache, final String issueId) {
		errorType = 1;
		showMainProcess(1);
		mController.getIssue(new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				doAfterFetchIssue(entry, fetchNew, useCache, issueId);
			}
		}, useCache, issueId);
	}

	private void doAfterFetchIssue(Entry entry, boolean fetchNew,
			boolean useCache, String callIssueId) {
		if (entry instanceof Issue) {
			// TODO 如果是查看往期，就不保存issueId
			if (getCallBack(callIssueId) != null) {
				getCallBack(callIssueId).onSuccess((Issue) entry);
				addPreCallback(callIssueId, null);
				return;
			}
			issue = (Issue) entry;
			((CommonMainActivity) mContext).setIssue(issue);
			if (fetchNew)
				CommonApplication.lastestIssueId = issue.getId();
			if (getPushArticleId() != -1) {
				// 调至至文章页
				if (fetchNew) {
					TransferArticle transferArticle = new TransferArticle(
							issue, getPushArticleId(), getPushCatId(),
							ArticleType.Default, "", "");
					((CommonMainActivity) mContext)
							.gotoArticleActivity(transferArticle);
					DataHelper.setIssueId(mContext, issue.getId());
				}
			} else if (CommonApplication.isFetchPush) {
				DataHelper.setIssueId(mContext, issue.getId());
				CommonApplication.isFetchPush = false;
				CommonApplication.hasNewIssue = false;
				((CommonMainActivity) mContext).setCurrentViewIssueId(-1);
				getAdvList();
			} else {
				if (CommonApplication.hasNewIssue) {
					if (mContext != null) {
						if (getIssueType() == LoadIssueType.NORMAL)
							((CommonMainActivity) mContext).showDialog(0);
						else if (getIssueType() == LoadIssueType.PULL)
							((CommonMainActivity) mContext).showDialog(2);
					}
				} else {
					if (getIssueType() == LoadIssueType.PULL) {
						if (!CommonApplication.columnUpdateTimeSame) {
							getAdvList();
						} else {
							((CommonMainActivity) mContext)
									.onRefreshComplete(true);
						}
					} else {
						getAdvList();
					}
				}
			}
		} else {
			if (getCallBack(callIssueId) != null) {
				getCallBack(callIssueId).onFailed();
				addPreCallback(callIssueId, null);
				return;
			}
			fecthError();
		}
	}

	/**
	 * 获取广告资源
	 */
	public void getAdvList() {
		if (!hasAdv()) {
			getCatList();
			return;
		}
		errorType = 7;
		mController.getAdvList(((CommonMainActivity) mContext).isUsingCache(),
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof AdvList) {
							AdvList advList = (AdvList) entry;
							CommonApplication.advList = advList;
							// 下载进版广告
							downloadRuBan(advList);
							getCatList();
						} else {
							fecthError();
						}
					}
				});
	}

	/**
	 * 获取最新一期的栏目列表
	 * 
	 * @param isPull
	 *            是否下拉刷新之后发现column数据有变更
	 * 
	 */
	public void getCatList() {
		errorType = 2;
		mController.getCatList(issue, new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				if (entry instanceof Cat) {
					cat = (Cat) entry;
					((CommonMainActivity) mContext).setDataForColumn(entry);
					if (CommonApplication.hasSolo
							&& getIssueType() != LoadIssueType.PULL) {
						((CommonMainActivity) mContext).getSoloCatList();
					} else if (ConstData.getAppId() == 20) {
						((CommonMainActivity) mContext).getCatIndex(cat
								.getList().get(0).getId()
								+ "");
					} else {
						((CommonMainActivity) mContext).checkFetchCatIndex();
					}
				} else {
					fecthError();
				}
			}
		});
	}

	/**
	 * 获取首页数据
	 * 
	 */
	public void getIndex(final boolean showProcess) {
		if (cat == null)
			return;
		errorType = 3;
		if (!isFirstIn && showProcess)
			((CommonMainActivity) mContext).checkIndexLoading(1);
		((CommonMainActivity) mContext).setColumnId("-1");
		mController.getIndex(issue, new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				if (entry instanceof IndexArticle) {
					((CommonMainActivity) mContext).setDataForIndex(entry);
					if (ParseUtil.listNotNull(cat.getList())) {
						((CommonMainActivity) mContext).setIndexTitle(cat
								.getList().get(0).getCname());
					}
					DataHelper.setColumnUpdateTime(mContext,
							issue.getColumnUpdateTime(), issue.getId());
					if (!isFirstIn || showProcess) {
						((CommonMainActivity) mContext).checkIndexLoading(0);
						showMainProcess(0);
					} else if (getIssueType() == LoadIssueType.NORMAL) {
						moveToLeft();
						moveToIndex();
					}
					if (!showProcess)
						((CommonMainActivity) mContext).showFecthingToast(1);
					((CommonMainActivity) mContext).onRefreshComplete(true);
					mCacheCallBack.afterUse(true);
				} else {
					fecthError();
					if (showProcess) {
						((CommonMainActivity) mContext).checkIndexLoading(2);
					}
				}
			}
		});
	}

	/**
	 * 优家效果有点卡，延迟100毫秒执行
	 */
	private void moveToLeft() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (((CommonMainActivity) mContext).getScrollView() != null)
					((CommonMainActivity) mContext).getScrollView()
							.clickButton(true);
				showMainProcess(0);
			}
		}, 100);
	}

	private void moveToIndex() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (((CommonMainActivity) mContext).getScrollView() != null)
					((CommonMainActivity) mContext).getScrollView()
							.IndexClick();
				isFirstIn = false;
			}
		}, 1000);
	}

	private LoadIssueType getIssueType() {
		return ((CommonMainActivity) mContext).mLoadIssueType;
	}

	private FetchPreviousIssueCallBack getCallBack(String issueId) {
		return preMap.containsKey(issueId) ? preMap.get(issueId) : null;
	}

	public void addPreCallback(String issueId,
			FetchPreviousIssueCallBack callBack) {
		if (callBack != null)
			preMap.put(issueId, callBack);
		else {
			if (preMap.containsKey(issueId))
				preMap.remove(issueId);
		}
	}

	private int getPushArticleId() {
		return ((CommonMainActivity) mContext).pushArticleId;
	}

	private int getPushCatId() {
		return ((CommonMainActivity) mContext).pushCatId;
	}

	public boolean isFirstIn() {
		return isFirstIn;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	private void fecthError() {
		if (((CommonMainActivity) mContext).isUsingCache()) {
			mCacheCallBack.afterUse(false);
			return;
		}
		((CommonMainActivity) mContext).onRefreshComplete(false);
		if (getIssueType() != LoadIssueType.PULL)
			showMainProcess(2);
	}

	private boolean useCacheSuccess() {
		return ((CommonMainActivity) mContext).isUseCacheSuccess();
	}

	private void showMainProcess(int flag) {
		if (!((CommonMainActivity) mContext).isUsingCache()
				&& !useCacheSuccess() && ConstData.getAppId() != 20) {
			ModernMediaTools.showLoadView(mContext, flag);
		}
	}

	public int getErrorType() {
		return errorType;
	}

	/**
	 * 获取当前cat所在catList的位置
	 * 
	 * @return
	 */
	public int getCatPosition(String catId) {
		return ModernMediaTools.getCatPosition(catId, cat);
	}

	/**
	 * 下载入版广告
	 * 
	 * @param advList
	 */
	private void downloadRuBan(AdvList advList) {
		Map<Integer, List<AdvItem>> advMap = advList.getAdvMap();
		if (!advMap.isEmpty() && advMap.containsKey(AdvList.RU_BAN)) {
			if (ParseUtil.listNotNull(advMap.get(AdvList.RU_BAN))) {
				for (AdvItem item : advMap.get(AdvList.RU_BAN)) {
					for (AdvSource pic : item.getSourceList()) {
						PrintHelper.print("down_ruban===" + pic.getUrl());
						CommonApplication.getImageDownloader().download(
								pic.getUrl());
					}
				}
			}
		}
	}

	private boolean hasAdv() {
		return ConstData.getInitialAppId() != 21
				&& ConstData.getInitialAppId() != 20;
	}

	public int reLoadData() {
		if (isFirstIn())
			showMainProcess(1);
		else
			((CommonMainActivity) mContext).checkIndexLoading(1);
		switch (errorType) {
		case 1:
			return 1;
		case 2:
			getCatList();
			break;
		case 3:
			getIndex(true);
			break;
		case 4:
			return 4;
		case 5:
			return 5;
		case 6:
			return 6;
		case 7:
			getAdvList();
			break;
		default:
			break;
		}
		return -1;
	}

}
