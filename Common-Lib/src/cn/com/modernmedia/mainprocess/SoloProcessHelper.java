package cn.com.modernmedia.mainprocess;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.mainprocess.MainProcessSolo.FetchSoloListener;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.solo.db.SoloArticleListDb;
import cn.com.modernmedia.solo.db.SoloDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;

public abstract class SoloProcessHelper {
	private Context mContext;

	private OperateController mController;
	private ArticleList articleList;// 用来保存offset
	private String currentCatId;
	/**
	 * 是否正在获取独立栏目信息，防止同时获取多个独立栏目，导致显示出错
	 */
	private boolean isFetchingSoloIndex = false;

	protected boolean isClick;

	/**
	 * 保存当前独立栏目显示的子views
	 */
	private HashMap<String, FetchSoloListener> soloMap = new HashMap<String, FetchSoloListener>();

	public SoloProcessHelper(Context context) {
		mContext = context;
		mController = OperateController.getInstance(mContext);
		clearMap();
	}

	/**
	 * 获取独立栏目文章列表（先获取文章列表，再获取栏目）
	 * 
	 * @param catId
	 *            父栏目id
	 * @param isPull
	 *            是否下拉刷新or加载更多
	 * @param fromOffset
	 * 
	 * @param toOffset
	 * 
	 * @param newData
	 *            是否获取新数据
	 */
	public void getSoloArticleList(final int catId, final boolean isPull,
			final String fromOffset, final String toOffset,
			final boolean newData) {
		if (TextUtils.isEmpty(fromOffset) || TextUtils.isEmpty(toOffset)) {
			fecthDataError(true, newData, isPull);
			showSoloIndexToast(true, newData);
			return;
		}
		if (isFetchingSoloIndex)
			return;
		isFetchingSoloIndex = true;
		mController.getSoloArticleList(catId + "", fromOffset, toOffset,
				newData, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						checkSoloArticleList(catId, entry, isPull, fromOffset,
								toOffset, false, newData);
					}
				});
	}

	/** 获取独立栏目文章列表 **/
	private void checkSoloArticleList(int catId, Entry entry, boolean isPull,
			String fromOffset, String toOffset, boolean fromDb, boolean newData) {
		ArticleList articleList;
		if (entry instanceof ArticleList) {
			articleList = (ArticleList) entry;
			((CommonMainActivity) mContext).setSoloArticleList(articleList
					.getAllArticleList());
			if (articleList.isHasData()) {
				this.articleList = articleList;
				getSoloColumnIndex(catId, isPull, fromOffset, toOffset, newData);
			} else {
				fecthDataError(fromDb, newData, isPull);
				showSoloIndexToast(fromDb, newData);
			}
		} else {
			// TODO 如果没有网络，那么取数据库数据
			if (!ModernMediaTools.checkNetWork(mContext)) {
				articleList = SoloArticleListDb.getInstance(mContext)
						.getArticleListByOffset(catId, fromOffset, toOffset,
								false, true);
				// TODO 如果从数据库获取数据成功,递归
				if (articleList instanceof ArticleList) {
					checkSoloArticleList(catId, articleList, isPull,
							fromOffset, toOffset, true, newData);
				} else {
					fecthDataError(fromDb, newData, isPull);
				}
			} else {
				// TODO 当有网络但获取数据失败，通知listview刷新状态至起始状态
				fecthDataError(fromDb, newData, isPull);
			}
		}
	}

	/**
	 * 获取独立栏目index
	 * 
	 * @param catId
	 *            父栏目id
	 * @param isPull
	 *            是否下拉刷新
	 * @param fromOffset
	 * 
	 * @param toOffset
	 * 
	 * @param newData
	 *            是否获取新数据
	 */
	private void getSoloColumnIndex(final int catId, final boolean isPull,
			final String fromOffset, final String toOffset,
			final boolean newData) {
		currentCatId = String.valueOf(catId);
		mController.getSoloCatIndex(catId + "", fromOffset, toOffset, newData,
				getCatPosition(), new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						checkSoloColumnIndex(catId, entry, isPull, fromOffset,
								toOffset, false, newData);
					}
				});
	}

	/** 获取独立栏目index **/
	private void checkSoloColumnIndex(int catId, Entry entry, boolean isPull,
			String fromOffset, String toOffset, boolean fromDb, boolean newData) {
		isFetchingSoloIndex = false;
		CatIndexArticle catIndex;
		if (entry instanceof CatIndexArticle) {
			catIndex = (CatIndexArticle) entry;
			saveOffset(fromOffset, toOffset, newData);
			String full = catIndex.getFullKeyTag();
			if (catIndex.getSoloMap().containsKey(full)
					&& ParseUtil.mapContainsKey(
							catIndex.getSoloMap().get(full), 1)) {
				DataHelper.solo_head_count += catIndex.getSoloMap().get(full)
						.get(1).size();
			}
			if (catIndex.getSoloMap().containsKey(full)
					&& ParseUtil.mapContainsKey(
							catIndex.getSoloMap().get(full), 2)) {
				DataHelper.solo_list_count += catIndex.getSoloMap().get(full)
						.size();
			}
			doAfterFetchData(catIndex, fromDb, newData);
		} else {
			// TODO 如果没有网络，那么取数据库数据
			if (!ModernMediaTools.checkNetWork(mContext)) {
				catIndex = SoloDb.getInstance(mContext).getSoloIndexByOffset(
						catId, fromOffset, toOffset, false, true,
						getCatPosition());
				if (catIndex instanceof CatIndexArticle) {
					checkSoloColumnIndex(catId, catIndex, isPull, fromOffset,
							toOffset, true, newData);
				} else {
					fecthDataError(fromDb, newData, isPull);
				}
			} else {
				fecthDataError(fromDb, newData, isPull);
			}
		}
	}

	/**
	 * 显示独立栏目
	 * 
	 * @param parentId
	 */
	public void showSoloChildCat(int parentId, boolean fromClick) {
		isClick = fromClick;
		if (fromClick && !ConstData.isIndexPager()) {
			((CommonMainActivity) mContext).checkIndexLoading(1);
		}
		getProcess().setColumnId(parentId);
		clearMap();
		String cId = parentId + "";
		if (TextUtils.isEmpty(DataHelper.fromOffset.get(cId))
				&& TextUtils.isEmpty(DataHelper.toOffset.get(cId))) {
			// TODO 每次进应用重新计算fromOffset,toOffset,因为每次都从0-0开始
			DataHelper.fromOffset.put(cId, "0");
			DataHelper.toOffset.put(cId, "0");
		}
		showSoloChildCat(parentId);
	}

	public abstract void showSoloChildCat(int parentId);

	/**
	 * 添加子view接口，全部添加成功之后取数据
	 * 
	 * @param key
	 *            child cat name
	 * @param listener
	 */
	public void addSoloListener(String key, FetchSoloListener listener) {
		soloMap.put(key, listener);
		// TODO 等所有子view都继承了FetchSoloListener再获取数据
		if (soloMap.size() == ModernMediaTools.getSoloChild(
				getProcess().columnId).size()) {
			String from = DataHelper.fromOffset.get(getProcess().columnId + "");
			String to = DataHelper.toOffset.get(getProcess().columnId + "");
			DataHelper.solo_head_count = 0;
			DataHelper.solo_list_count = 0;
			fetchSoloData(from, to);
		}
	}

	public abstract void fetchSoloData(String from, String to);

	/**
	 * 获取独立栏目文章列表缓存
	 * 
	 * @use 1.首次取缓存时使用
	 * @use 2.当已经进入过一次之后，下次进入先取缓存
	 */
	protected boolean getArticleListFromDb() {
		ArticleList articleList = SoloArticleListDb.getInstance(mContext)
				.getArticleListByOffset(getProcess().columnId,
						DataHelper.fromOffset.get(getProcess().columnId + ""),
						DataHelper.toOffset.get(getProcess().columnId + ""),
						true, false);
		((CommonMainActivity) mContext).setSoloArticleList(articleList
				.getAllArticleList());
		if (articleList != null && articleList.isHasData()) {
			return getSoloIndexFromDb();
		}
		return false;
	}

	/**
	 * 获取独立栏目首页缓存
	 */
	protected boolean getSoloIndexFromDb() {
		CatIndexArticle catIndexArticle = SoloDb.getInstance(mContext)
				.getSoloIndexByOffset(getProcess().columnId,
						DataHelper.fromOffset.get(getProcess().columnId + ""),
						DataHelper.toOffset.get(getProcess().columnId + ""),
						true, false, getCatPosition());
		if (catIndexArticle != null) {
			doAfterFetchData(catIndexArticle, true, false);
			return true;
		}
		return false;
	}

	/**
	 * 拿完数据之后发给所有子页面
	 * 
	 * @param catIndex
	 * @param fromDb
	 * @param newData
	 */
	protected void doAfterFetchData(CatIndexArticle catIndex, boolean fromDb,
			boolean newData) {
		if (catIndex == null) {
			for (String key : soloMap.keySet())
				soloMap.get(key).fetchSoloData(catIndex, newData, !fromDb);
			return;
		} else if (!soloMap.isEmpty() && !catIndex.getSoloMap().isEmpty()) {
			for (String key : catIndex.getSoloMap().keySet()) {
				if (soloMap.containsKey(key))
					soloMap.get(key).fetchSoloData(catIndex, newData, !fromDb);
			}
		}
	}

	public void clearMap() {
		soloMap.clear();
	}

	private int getCatPosition() {
		return ModernMediaTools.getCatPosition(currentCatId, getProcess().cat);
	}

	/** 保存最新的offset **/
	private void saveOffset(String mFromOffset, String mToOffset,
			boolean mFecthNew) {
		if (articleList != null) {
			if (mFromOffset.equals("0") && mToOffset.equals("0")) {
				DataHelper.toOffset.put(getProcess().columnId + "",
						articleList.getToOffset());
				DataHelper.fromOffset.put(getProcess().columnId + "",
						articleList.getFromOffset());
			} else {
				if (mFecthNew)
					DataHelper.toOffset.put(getProcess().columnId + "",
							articleList.getToOffset());
				else
					DataHelper.fromOffset.put(getProcess().columnId + "",
							articleList.getFromOffset());
			}
		}
	}

	/**
	 * 获取数据失败
	 * 
	 * @param catIndex
	 * @param fromDb
	 * @param newData
	 * @param isPull
	 */
	protected void fecthDataError(boolean fromDb, boolean newData,
			boolean isPull) {
		isFetchingSoloIndex = false;
		if (isPull) {
			doAfterFetchData(null, fromDb, newData);
		}
	}

	/**
	 * 显示独立栏目错误信息
	 * 
	 * @param fromDb
	 * @param newData
	 */
	private void showSoloIndexToast(boolean fromDb, boolean newData) {
		if (fromDb) {
			ModernMediaTools.showToast(mContext, R.string.net_error);
		} else {
			if (newData)
				ModernMediaTools.showToast(mContext, R.string.is_lastest);
			else
				ModernMediaTools.showToast(mContext, R.string.nomore_data);
		}
	}

	private BaseMainProcess getProcess() {
		if (CommonApplication.manage == null)
			return new MainProcessNormal(mContext, null);
		return CommonApplication.manage.getProcess();
	}
}
