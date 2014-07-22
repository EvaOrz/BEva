package cn.com.modernmedia.mainprocess;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.mainprocess.MainProcessManage.FetchDataCallBack;
import cn.com.modernmedia.mainprocess.MainProcessManage.ProcessType;
import cn.com.modernmedia.mainprocess.MainProcessSolo.FetchSoloListener;
import cn.com.modernmedia.model.AdvList;
import cn.com.modernmedia.model.AdvList.AdvItem;
import cn.com.modernmedia.model.AdvList.AdvSource;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.util.PrintHelper;
import cn.com.modernmediaslate.model.Entry;

/**
 * 主页流程基类
 * 
 * @author user
 * 
 */
public abstract class BaseMainProcess implements NewIssueDialogListener {
	private Context mContext;
	protected OperateController mController;
	protected FetchDataCallBack fetchCallBack;
	/**
	 * 1--getissue;2--getcatlist;3--getindex;4--getcatindex;5---solocatlist(
	 * 独立栏目列表);6--getSoloColumnIndex;7---advlist
	 */
	protected int errorType;
	public static boolean isFirstIn = true;// 显示切换动画
	public static boolean hasMoved = false;// 是否已经显示过入屏右移动画
	protected Handler handler = new Handler();

	protected Cat cat;
	protected int columnId;
	protected ProcessType mProcessType;

	public BaseMainProcess(Context context, FetchDataCallBack fetchCallBack,
			ProcessType processType) {
		mContext = context;
		this.fetchCallBack = fetchCallBack;
		mProcessType = processType;
		mController = OperateController.getInstance(mContext);
	}

	/**
	 * 获取issue信息
	 * 
	 * @param issueId
	 *            ,不为空代表获取某一期
	 */
	public void getIssue(String issueId, boolean useCache) {
		// TODO 乐活默认issueid=0
		if (TextUtils.isEmpty(issueId) && ConstData.getInitialAppId() == 16) {
			issueId = "0";
		}
		final String id = issueId;
		errorType = 1;
		mController.getIssue(new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				doAfterFetchIssue(entry, id);
			}
		}, useCache, id);
	}

	/**
	 * getIssue获取之后的操作
	 * 
	 * @param entry
	 */
	protected void doAfterFetchIssue(Entry entry) {
		if (entry instanceof Issue) {
			Issue issue = (Issue) entry;
			CommonApplication.issue = issue;
			CommonApplication.currentIssueId = issue.getId();
		}
	}

	protected void doAfterFetchIssue(Entry entry, String issueId) {
		if (entry instanceof Issue) {
			// TODO 乐活默认issueid=0
			if (TextUtils.isEmpty(issueId) && ConstData.getInitialAppId() == 16) {
				((Issue) entry).setId(0);
			}
			// TODO 设置最新一期的id，广告使用
			if (TextUtils.isEmpty(issueId)) {
				CommonApplication.lastestIssueId = ((Issue) entry).getId();
			}
		}
		doAfterFetchIssue(entry);
	}

	/**
	 * 获取广告接口
	 */
	public void getAdvList(boolean useCache) {
		errorType = 7;
		mController.getAdvList(useCache, new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				doAfterFetchAdv(entry);
			}
		});
	}

	/**
	 * getAdvList获取之后的操作
	 * 
	 * @param entry
	 */
	protected void doAfterFetchAdv(Entry entry) {
		if (entry instanceof AdvList) {
			AdvList advList = (AdvList) entry;
			CommonApplication.advList = advList;
			downloadRuBan(advList);
			checkIssueId();
		}
	}

	/**
	 * 获取栏目列表
	 */
	public void getCatList() {
		errorType = 2;
		mController.getCatList(CommonApplication.issue,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						doAfterFecthCatList(entry);
					}
				});
	}

	/**
	 * getCatList获取之后的操作
	 * 
	 * @param entry
	 * @return
	 */
	protected boolean doAfterFecthCatList(Entry entry) {
		if (entry instanceof Cat) {
			cat = (Cat) entry;
			((CommonMainActivity) mContext).setDataForColumn(entry);
			if (CommonApplication.hasSolo) {
				getSoloCatList();
				return true;
			} else {
				return checkFecthIndex();
			}
		}
		return false;
	}

	/**
	 * 获取首页数据
	 */
	public void getIndex(boolean showLoading) {
		if (cat == null)
			return;
		errorType = 3;
		if (!isFirstIn && showLoading) {
			((CommonMainActivity) mContext).checkIndexLoading(1);
		}
		mController.getIndex(CommonApplication.issue, new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				doAfterFetchIndex(entry);
			}
		});
	}

	/**
	 * getIndex获取之后的操作
	 * 
	 * @param entry
	 */
	protected void doAfterFetchIndex(Entry entry) {
		if (entry instanceof IndexArticle) {
			if (ConstData.isIndexPager()) {
				// TODO首页滑屏
				((CommonMainActivity) mContext).showIndexPager();
			} else {
				((CommonMainActivity) mContext).setDataForIndex(entry);
			}
			moveToLeft();
			moveToIndex();
		}
	}

	/**
	 * 获取栏目首页数据
	 * 
	 * @param catId
	 */
	public void getCatIndex(int catId, boolean showLoading) {
		if (cat == null)
			return;
		errorType = 4;
		columnId = catId;
		if (!isFirstIn && showLoading) {
			((CommonMainActivity) mContext).checkIndexLoading(1);
		}
		mController.getCartIndex(CommonApplication.issue, catId + "",
				ModernMediaTools.getCatPosition(catId + "", cat),
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						doAfterFecthCatIndex(entry);
					}
				});
	}

	/**
	 * getCatIndex获取之后的操作
	 * 
	 * @param entry
	 */
	protected void doAfterFecthCatIndex(Entry entry) {
		if (entry instanceof CatIndexArticle) {
			if (ConstData.isIndexPager()) {
				// TODO首页滑屏
				((CommonMainActivity) mContext).showIndexPager();
			} else {
				((CommonMainActivity) mContext).setDataForIndex(entry);
			}
			moveToLeft();
			moveToIndex();
		}
	}

	/**
	 * 判断这个app是否含有广告
	 * 
	 * @return
	 */
	protected boolean hasAdv() {
		return ConstData.getInitialAppId() != 21
				&& ConstData.getInitialAppId() != 20;
	}

	/**
	 * 如果issueid=0,那么没有catList,直接获取solocatlist
	 */
	protected void checkIssueId() {
		if (CommonApplication.issue == null) {
			return;
		}
		if (CommonApplication.currentIssueId == 0) {
			CommonApplication.hasSolo = true;
			getSoloCatList();
		} else {
			getCatList();
		}
	}

	/**
	 * 判断栏目第一个是首页还是栏目首页
	 */
	public boolean checkFecthIndex() {
		if (cat == null)
			return false;
		List<CatItem> items = cat.getList();
		if (!ParseUtil.listNotNull(items)) {
			return false;
		}
		CatItem firstItem = items.get(0);
		int catId = ModernMediaTools.isSoloCat(firstItem.getLink());
		if (catId != -1) {
			// TODO 获取独立栏目
			showSoloChildCat(catId, false);
		} else if (firstItem.getId() == 0) {
			// TODO 获取首页
			getIndex(false);
		} else {
			// TODO 获取栏目首页
			getCatIndex(firstItem.getId(), false);
		}
		((CommonMainActivity) mContext).setIndexTitle(firstItem.getCname());
		return true;
	}

	/**
	 * 下载入版广告
	 * 
	 * @param advList
	 */
	protected void downloadRuBan(AdvList advList) {
		Map<Integer, List<AdvItem>> advMap = advList.getAdvMap();
		if (ParseUtil.mapContainsKey(advMap, AdvList.RU_BAN)) {
			if (ParseUtil.listNotNull(advMap.get(AdvList.RU_BAN))) {
				for (AdvItem item : advMap.get(AdvList.RU_BAN)) {
					for (AdvSource pic : item.getSourceList()) {
						PrintHelper.print("down_ruban===" + pic.getUrl());
						CommonApplication.finalBitmap.display(pic.getUrl());
					}
				}
			}
		}
	}

	public void moveToLeft() {
		isFirstIn = false;
		if (mProcessType == ProcessType.Cache || hasMoved) {
			return;
		}
		hasMoved = true;
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (((CommonMainActivity) mContext).getScrollView() != null)
					((CommonMainActivity) mContext).getScrollView()
							.clickButton(true);
				showMainProcess(0);
			}
		}, 500);
	}

	public void moveToIndex() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (((CommonMainActivity) mContext).getScrollView() != null)
					((CommonMainActivity) mContext).getScrollView()
							.IndexClick();
			}
		}, 1500);
	}

	protected abstract void getSoloCatList();

	public abstract void getSoloArticleList(int catId, boolean isPull,
			String fromOffset, String toOffset, boolean newData);

	public abstract void showSoloChildCat(int catId, boolean fromClick);

	public abstract void addSoloListener(String key, FetchSoloListener listener);

	protected void showMainProcess(int flag) {
		if (ConstData.getAppId() != 20) {
			ModernMediaTools.showLoadView(mContext, flag);
		}
	}

	protected void showLoad(boolean show) {
		if (show) {
			// TODO 如果缓存请求成功，那么显示dialog的loading
			if (!isFirstIn) {
				ModernMediaTools.showLoading(mContext, show);
			} else {
				showMainProcess(1);
			}
		} else {
			ModernMediaTools.showLoading(mContext, show);
			showMainProcess(0);
			((CommonMainActivity) mContext).checkIndexLoading(0);
		}
	}

	protected void showError() {
		showLoad(false);
		if (isFirstIn) {
			ModernMediaTools.showLoading(mContext, false);
			showMainProcess(2);
		} else {
			((CommonMainActivity) mContext).checkIndexLoading(2);
		}
	}

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	public Cat getCat() {
		return cat;
	}

	public ProcessType getProcessType() {
		return mProcessType;
	}

	/**
	 * 退出应用时还原变量
	 */
	public static void clear() {
		isFirstIn = true;
		hasMoved = false;
	}

}
