package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.LoadIssueType;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.CheckPush;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.ViewPreviousIssue.FetchPreviousIssueCallBack;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmediaslate.model.Entry;

import com.parse.ParseAnalytics;

/**
 * 公共首页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonMainActivity extends BaseFragmentActivity {
	public static final int LOGIN_REQUEST_CODE = 101;// 从用户页面返回
	private Context mContext;
	private OperateController controller;// 接口控制器
	private long lastClickTime = 0;
	private Issue issue;
	private String columnId = "";
	private CheckPush checkPush;
	protected int pushArticleId = -1;
	protected int pushCatId = -1;
	private OnPullRefreshListener mRefreshListener;
	protected MainHorizontalScrollView scrollView;
	protected int currentViewIssueId = -1;// 当前查看的issueID
	protected LoadIssueType mLoadIssueType;
	// 如果点击往期进入文章页，进入之前改变currentIssueId,从文章页退出，还原为最新当前index的issueId;
	private boolean reSetIssueId = true;
	protected MainProcess mProcess;
	private boolean isUsingCache = true;
	// 如果缓存没有拿去成功，那么现实process
	private boolean useCacheSuccess = true;
	private List<NotifyAdapterListener> listenerList = new ArrayList<NotifyAdapterListener>();

	public interface OnPullRefreshListener {
		public void onRefresh(boolean success);
	}

	protected UseCacheCallBack cacheCallBack = new UseCacheCallBack() {

		@Override
		public void afterUse(boolean success) {
			ModernMediaTools.showLoading(mContext, false);
			if (isUsingCache) {
				isUsingCache = false;
				useCacheSuccess = success;
				if (ModernMediaTools.checkNetWork(mContext)) {
					firstUseCache(false);
					showFecthingToast(0);
				} else {
					if (!success) {// 如果没成功，那么现实error
						firstUseCache(false);
					}
					showToast(R.string.net_error);
				}
			}
		}
	};

	public UseCacheCallBack getCacheCallBack() {
		return cacheCallBack;
	}

	public interface UseCacheCallBack {
		/**
		 * 当第一次进应用时先取缓存再取服务器数据
		 * 
		 * @param success
		 *            是否成功拿取缓存
		 */
		public void afterUse(boolean success);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		checkPush = new CheckPush(this);
		mProcess = new MainProcess(this, cacheCallBack);
		init();
		ParseAnalytics.trackAppOpened(getIntent());
		controller = OperateController.getInstance(mContext);

		if (getIntent().getExtras() != null) {
			String from = getIntent().getExtras().getString(
					GenericConstant.FROM_ACTIVITY);
			if (!TextUtils.isEmpty(from)
					&& from.equals(GenericConstant.FROM_ACTIVITY_VALUE)) {
				// 从首页进来
				firstUseCache(true);
			} else {
				// 从push消息进来
				checkPush.parsePushMsg(getIntent(), issue);
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getExtras() != null) {
			if (checkPush == null)
				checkPush = new CheckPush(this);
			checkPush.parsePushMsg(intent, issue);
		}
	}

	public boolean isUsingCache() {
		return isUsingCache;
	}

	public void firstUseCache(boolean isFirst) {
		mProcess.firstUseCache(isFirst);
	}

	/**
	 * 下拉刷新获取issue
	 */
	public void pullToGetIssue() {
		if (mRefreshListener != null) {
			mLoadIssueType = LoadIssueType.PULL;
			mProcess.getIssue(true, false, null);
		}
	}

	/**
	 * 普通进入应用获取issue
	 * 
	 * @param fetchNew
	 *            true,查看最新一期；otherwise，查看上一期
	 */
	public void getIssue(boolean fetchNew) {
		mLoadIssueType = LoadIssueType.NORMAL;
		if (fetchNew) {
			currentViewIssueId = -1;
			mProcess.getIssue(true, isUsingCache, "");
		} else {
			currentViewIssueId = CommonApplication.oldIssueId;
			mProcess.getIssue(false, isUsingCache, CommonApplication.oldIssueId
					+ "");
		}
	}

	/**
	 * 查看往期
	 * 
	 * @param issueId
	 *            期id
	 */
	public void getPreIssue(int issueId, FetchPreviousIssueCallBack callBack) {
		mLoadIssueType = LoadIssueType.PRE;
		currentViewIssueId = issueId;
		mProcess.addPreCallback(issueId + "", callBack);
		mProcess.getIssue(false, false, issueId + "");
	}

	/**
	 * 判断获取栏目还是独立栏目
	 * 
	 * @param isPull
	 */
	protected void checkFetchCatIndex() {
		if (mLoadIssueType == LoadIssueType.PULL) {
			if (TextUtils.isEmpty(columnId) || columnId.compareTo("0") <= 0)
				mProcess.getIndex(false);
			else
				getCatIndex(columnId);
		} else
			mProcess.getIndex(false);
	}

	/**
	 * 获取独立栏目信息(暂时只有商周有)
	 */
	protected void getSoloCatList() {
		mProcess.setErrorType(5);
	}

	/**
	 * 从adapter中执行
	 * 
	 * @param type
	 */
	public void getIndex(LoadIssueType type, boolean showProcess) {
		mLoadIssueType = type;
		mProcess.getIndex(showProcess);
	}

	public void onRefreshComplete(boolean success) {
		if (mLoadIssueType == LoadIssueType.PULL && mRefreshListener != null) {
			mRefreshListener.onRefresh(success);
		}
	}

	public void pullToGetIndex(String columnId) {
		mLoadIssueType = LoadIssueType.PULL;
		getColumnIndex(columnId);
	}

	public void getCatIndex(String columnId) {
		mLoadIssueType = LoadIssueType.NORMAL;
		getColumnIndex(columnId);
	}

	/**
	 * 获取栏目首页
	 * 
	 * @param issue
	 * @param columnId
	 */
	private void getColumnIndex(String columnId) {
		mProcess.setErrorType(4);
		this.columnId = columnId;

		if (ConstData.getAppId() != 20) {
			checkIndexLoading(1);
		}
		controller.getCartIndex(issue, columnId,
				mProcess.getCatPosition(columnId), new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof CatIndexArticle) {
							setDataForIndex(entry);
							onRefreshComplete(true);
							if (ConstData.getAppId() != 20) {
								checkIndexLoading(0);
							}
						} else {
							if (isUsingCache) {
								cacheCallBack.afterUse(false);
								return;
							}

							onRefreshComplete(false);
							if (ConstData.getAppId() != 20) {
								checkIndexLoading(2);
							}
						}
					}
				});
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.new_issue);
		builder.setPositiveButton(R.string.vew_later,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonApplication.hasNewIssue = false;
						// 稍后查看
						if (id == 0) {
							getIssue(false);
						} else if (id == 1) {
							// push消息首页
							checkPush.viewNewIssue(false, issue);
						} else if (id == 2) {
							onRefreshComplete(true);
						}
					}
				}).setNegativeButton(R.string.view_now,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						currentViewIssueId = -1;
						CommonApplication.hasNewIssue = false;
						// 立即查看
						if (id == 0 || id == 2) {
							DataHelper.setIssueId(CommonApplication.mContext,
									issue.getId());
							CommonApplication.columnUpdateTimeSame = true;
							CommonApplication.issueIdSame = true;
							CommonApplication.articleUpdateTimeSame = true;
							mProcess.getCatList();
						} else if (id == 1) {
							checkPush.viewNewIssue(true, issue);
						}
					}
				});
		return builder.create();
	}

	/**
	 * 初始化资源
	 */
	protected abstract void init();

	/**
	 * 初始化栏目列表
	 * 
	 * @param entry
	 */
	protected abstract void setDataForColumn(Entry entry);

	/**
	 * 初始化index
	 * 
	 * @param entry
	 */
	protected abstract void setDataForIndex(Entry entry);

	public abstract MainHorizontalScrollView getScrollView();

	protected Class<?> getArticleActivity() {
		return null;
	}

	/**
	 * 如果继承的应用包含用户模块，需要重载此方法
	 * 
	 * @return
	 */
	protected String getUid() {
		return ConstData.UN_UPLOAD_UID;
	}

	/**
	 * indexview 显示loading
	 * 
	 * @param flag
	 *            0.不显示，1.显示loading，2.显示error
	 */
	public void checkIndexLoading(int flag) {
		if (mLoadIssueType != LoadIssueType.PULL && getIndexView() != null) {
			if (flag == 0)
				getIndexView().disProcess();
			else if (flag == 1)
				getIndexView().showLoading();
			else if (flag == 2)
				getIndexView().showError();
		}
	}

	protected abstract BaseView getIndexView();

	/**
	 * 设置首页title
	 * 
	 * @param name
	 */
	protected abstract void setIndexTitle(String name);

	protected void notifyRead() {
		for (NotifyAdapterListener listener : listenerList) {
			listener.notifyReaded();
		}
	};

	public void gotoArticleActivity(TransferArticle transferArticle) {
		if (transferArticle.getIssue() == null) {
			transferArticle.setIssue(issue);
		}
		PageTransfer.gotoArticleActivity(this, getArticleActivity(),
				transferArticle);
	}

	public void setRefreshListener(OnPullRefreshListener refreshListener) {
		mRefreshListener = refreshListener;
	}

	protected void doAfterLogin() {
	}

	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

	public void setPushArticleId(int pushArticleId) {
		this.pushArticleId = pushArticleId;
	}

	public void setPushCatId(int pushCatId) {
		this.pushCatId = pushCatId;
	}

	public void setReSetIssueId(boolean reSetIssueId) {
		this.reSetIssueId = reSetIssueId;
	}

	public void setCurrentViewIssueId(int currentViewIssueId) {
		this.currentViewIssueId = currentViewIssueId;
	}

	public boolean isUseCacheSuccess() {
		return useCacheSuccess;
	}

	/**
	 * 给scrollview设置需要拦截的子scrollview
	 * 
	 * @param flag
	 *            0.只执行子scrollview;1.当子scrollview滑到头时，执行父scrollview；2。
	 *            清空子scrollview
	 * @param view
	 */
	public void setScrollView(int flag, View view) {
		if (flag == 0) {
			scrollView.setNeedsScrollView(view);
		} else if (flag == 1 && view instanceof AtlasViewPager) {
			scrollView.setAtlasViewPager((AtlasViewPager) view);
		} else if (flag == 2) {
			scrollView.setNeedsScrollView(null);
		} else {
			scrollView.setAtlasViewPager(null);
		}
	}

	public void addListener(NotifyAdapterListener listener) {
		listenerList.add(listener);
	}

	/**
	 * 通知更新栏目列表选中状态״
	 * 
	 * @param catId
	 */
	public void notifyColumnAdapter(int catId) {
		for (NotifyAdapterListener listener : listenerList) {
			listener.nofitySelectItem(catId);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			long clickTime = System.currentTimeMillis() / 1000;
			if (clickTime - lastClickTime >= 3) {
				lastClickTime = clickTime;
				Toast.makeText(CommonMainActivity.this, R.string.exit_app,
						ConstData.TOAST_LENGTH).show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void reLoadData() {
		int flag = mProcess.reLoadData();
		switch (flag) {
		case 1:
			if (currentViewIssueId == -1) {
				getIssue(true);
			}
			break;
		case 4:
			getCatIndex(columnId);
			break;
		case 5:
			getSoloCatList();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CommonApplication.getImageDownloader().destroy();
		CommonApplication.exit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == PageTransfer.REQUEST_CODE) {
				notifyRead();
				if (issue != null && reSetIssueId) {
					reSetIssueId = false;
					CommonApplication.currentIssueId = issue.getId();
				}
				if (pushArticleId != -1) {
					pushArticleId = -1;
					pushCatId = -1;
					showFecthingToast(0);
					mProcess.getCatList();
				}
			} else if (requestCode == LOGIN_REQUEST_CODE) {
				doAfterLogin();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getColumnId() {
		return columnId;
	}

	public int getCatPosition(String catId) {
		return mProcess.getCatPosition(catId);
	}

	/**
	 * 获取网络时根据状态提示信息
	 * 
	 * @param flag
	 *            0:fetching;1.fecthOk;2.fetchError
	 */
	public void showFecthingToast(int flag) {
		if (isUsingCache || !ModernMediaTools.checkNetWork(mContext))
			// 如果正在使用缓存，不执行
			return;
		switch (flag) {
		case 0:
			showToast(R.string.fecthing_http);
			break;
		case 1:
			showToast(R.string.fecthing_ok);
			break;
		case 2:
			// 请求错误会统一提示
			break;
		default:
			break;
		}
	}
}
