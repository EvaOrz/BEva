package cn.com.modernmediasolo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.model.SoloColumn.SoloColumnItem;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediasolo.api.SoloOperateController;
import cn.com.modernmediasolo.db.SoloArticleListDb;
import cn.com.modernmediasolo.db.SoloDb;

/**
 * 独立栏目页(独立栏目首页不考虑下拉刷新，因为刷新之前advlist已经获取到了，不能重排)
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonSoloActivity extends CommonMainActivity {
	private SoloOperateController mController;
	// protected SoloColumn soloColumn;
	/**
	 * 是否正在获取独立栏目信息，因为所有独立栏目列表都是同一个接口获取，所以当在读取接口的时候，在其他地方下拉获取数据时都不做处理
	 */
	private boolean isFetchingSoloIndex = false;

	private HashMap<String, FetchSoloListener> soloMap = new HashMap<String, FetchSoloListener>();

	public interface FetchSoloListener {
		/**
		 * 获得独立栏目数据
		 * 
		 * @param catIndex
		 * @param newData
		 *            是否是新数据
		 * @param fromNet
		 *            true:来自接口false:来自本地数据库
		 */
		public void fetchSoloData(CatIndexArticle catIndex, boolean newData,
				boolean fromNet);
	}

	private ArticleList articleList;// 用来保存offset
	private String currentCatId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mController = SoloOperateController.getInstance(this);
	}

	/**
	 * 获取独立栏目信息(暂时只有商周有)
	 */
	@Override
	protected void getSoloCatList() {
		super.getSoloCatList();
		SoloOperateController.getInstance(this).getSoloColumn(isUsingCache(),
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof SoloColumn) {
							// soloColumn = (SoloColumn) entry;
							SoloApplication.soloColumn = (SoloColumn) entry;
							checkFetchCatIndex();
						} else {
							// onRefreshComplete(isPull, false);
							// if (!isPull)
							// showError();
							// test
							checkFetchCatIndex();
						}
					}
				});
	}

	/**
	 * 获取独立栏目文章列表（先获取文章列表，再获取栏目）
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
	public void getSoloArticleList(final int catId, final boolean isPull,
			final String fromOffset, final String toOffset,
			final boolean newData) {
		if (TextUtils.isEmpty(fromOffset) || TextUtils.isEmpty(toOffset)) {
			fecthDataError(null, true, newData, isPull);
			showSoloIndexToast(true, newData);
			return;
		}
		mProcess.setErrorType(6);
		if (isFetchingSoloIndex)
			return;
		isFetchingSoloIndex = true;
		if (!isPull)
			checkIndexLoading(1);
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
			if (articleList.isHasData()) {
				this.articleList = articleList;
				getSoloColumnIndex(catId, isPull, fromOffset, toOffset, newData);
			} else {
				fecthDataError(null, fromDb, newData, isPull);
				showSoloIndexToast(fromDb, newData);
			}
		} else {
			// TODO 如果没有网络，那么取数据库数据
			if (!ModernMediaTools.checkNetWork(this)) {
				articleList = SoloArticleListDb.getInstance(this)
						.getArticleListByOffset(catId, fromOffset, toOffset,
								false, true);
				// TODO 如果从数据库获取数据成功,递归
				if (articleList instanceof ArticleList) {
					checkSoloArticleList(catId, articleList, isPull,
							fromOffset, toOffset, true, newData);
				} else {
					fecthDataError(null, fromDb, newData, isPull);
				}
			} else {
				// TODO 当有网络但获取数据失败，通知listview刷新状态至起始状态
				fecthDataError(null, fromDb, newData, isPull);
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
				mProcess.getCatPosition(String.valueOf(catId)),
				new FetchEntryListener() {

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
			if (catIndex.getHeadMap().containsKey(full)) {
				if (catIndex.getHeadMap().get(full) != null) {
					DataHelper.solo_head_count += catIndex.getHeadMap()
							.get(full).size();
				}
			}
			if (catIndex.getListMap().containsKey(full)) {
				if (catIndex.getListMap().get(full) != null) {
					DataHelper.solo_list_count += catIndex.getListMap()
							.get(full).size();
				}
			}
			listenerCallBack(catIndex, fromDb, newData);
			if (!isPull)
				checkIndexLoading(0);
			if (!catIndex.isHasData())
				showSoloIndexToast(fromDb, newData);
		} else {
			// TODO 如果没有网络，那么取数据库数据
			if (!ModernMediaTools.checkNetWork(this)) {
				catIndex = SoloDb.getInstance(this).getSoloIndexByOffset(catId,
						fromOffset, toOffset, true, getCatPosition());
				if (catIndex instanceof CatIndexArticle) {
					checkSoloColumnIndex(catId, catIndex, isPull, fromOffset,
							toOffset, true, newData);
				} else {
					fecthDataError(null, fromDb, newData, isPull);
				}
			} else {
				fecthDataError(null, fromDb, newData, isPull);
			}
		}
	}

	/** 保存最新的offset **/
	private void saveOffset(String mFromOffset, String mToOffset,
			boolean mFecthNew) {
		if (articleList != null) {
			if (mFromOffset.equals("0") && mToOffset.equals("0")) {
				DataHelper.toOffset.put(getColumnId(),
						articleList.getToOffset());
				DataHelper.fromOffset.put(getColumnId(),
						articleList.getFromOffset());
			} else {
				if (mFecthNew)
					DataHelper.toOffset.put(getColumnId(),
							articleList.getToOffset());
				else
					DataHelper.fromOffset.put(getColumnId(),
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
	private void fecthDataError(CatIndexArticle catIndex, boolean fromDb,
			boolean newData, boolean isPull) {
		isFetchingSoloIndex = false;
		if (isPull) {
			listenerCallBack(null, fromDb, newData);
		} else {
			checkIndexLoading(2);
		}
	}

	private void listenerCallBack(CatIndexArticle catIndex, boolean fromDb,
			boolean newData) {
		if (catIndex == null)
			for (String key : soloMap.keySet())
				soloMap.get(key).fetchSoloData(catIndex, newData, !fromDb);
		else if (!soloMap.isEmpty() && !catIndex.getListMap().isEmpty())
			for (String key : catIndex.getListMap().keySet()) {
				if (soloMap.containsKey(key))
					soloMap.get(key).fetchSoloData(catIndex, newData, !fromDb);
			}
	}

	/**
	 * 
	 * @param key
	 *            child cat name
	 * @param listener
	 */
	public void addSoloListener(String key, FetchSoloListener listener) {
		soloMap.put(key, listener);
		// TODO 等所有子view都继承了FetchSoloListener再获取数据
		if (soloMap.size() == getChild(ParseUtil.stoi(getColumnId(), -1))
				.size()) {
			String from = DataHelper.fromOffset.get(getColumnId());
			String to = DataHelper.toOffset.get(getColumnId());
			DataHelper.solo_head_count = 0;
			DataHelper.solo_list_count = 0;
			if (from != null && !from.equals("0") && to != null
					&& !to.equals("0")) {
				getArticleListFromDb();
			} else {
				getSoloArticleList(ParseUtil.stoi(getColumnId(), -1), false,
						"0", "0", true);
			}
		}
	}

	/**
	 * 独立栏目child
	 * 
	 * @param parentId
	 * @return
	 */
	public List<SoloColumnChild> getChild(int parentId) {
		SoloColumn soloColumn = SoloApplication.soloColumn;
		if (soloColumn == null || !ParseUtil.listNotNull(soloColumn.getList()))
			return new ArrayList<SoloColumnChild>();
		SoloColumnItem item = null;
		for (SoloColumnItem it : soloColumn.getList()) {
			if (it.getId() == parentId) {
				item = it;
				break;
			}
		}
		if (item != null) {
			return item.getList();
		}
		return new ArrayList<SoloColumnChild>();
	}

	private void showSoloIndexToast(boolean fromDb, boolean newData) {
		if (fromDb) {
			Toast.makeText(this, R.string.net_error, Toast.LENGTH_SHORT).show();
		} else {
			if (newData)
				Toast.makeText(this, R.string.is_lastest, Toast.LENGTH_SHORT)
						.show();
			else
				Toast.makeText(this, R.string.nomore_data, Toast.LENGTH_SHORT)
						.show();
		}
	}

	/**
	 * 显示独立栏目
	 * 
	 * @param parentId
	 */
	public void showSoloChildCat(int parentId) {
		setColumnId(parentId + "");
		clearMap();
		if (TextUtils.isEmpty(DataHelper.fromOffset.get(getColumnId()))
				&& TextUtils.isEmpty(DataHelper.toOffset.get(getColumnId()))) {
			// TODO 每次进应用重新计算fromOffset,toOffset,因为每次都从0-0开始
			DataHelper.fromOffset.put(getColumnId(), "0");
			DataHelper.toOffset.put(getColumnId(), "0");
		}
	}

	protected void clearMap() {
		soloMap.clear();
	}

	protected void setPulling(boolean isPull) {
	}

	@Override
	public void reLoadData() {
		if (mProcess.getErrorType() == 6) {
			getSoloArticleList(ParseUtil.stoi(getColumnId(), -1), false, "0",
					"0", true);
		}
		super.reLoadData();
	}

	/**
	 * 当已经进入过一次之后，下次进入先取缓存
	 */
	private void getArticleListFromDb() {
		checkIndexLoading(1);
		ArticleList articleList = SoloArticleListDb.getInstance(this)
				.getArticleListByOffset(ParseUtil.stoi(getColumnId(), -1),
						DataHelper.fromOffset.get(getColumnId()),
						DataHelper.toOffset.get(getColumnId()), true, false);
		if (articleList != null && articleList.isHasData()) {
			getSoloIndexFromDb();
		} else {
			checkIndexLoading(2);
		}
	}

	private void getSoloIndexFromDb() {
		CatIndexArticle catIndexArticle = SoloDb.getInstance(this)
				.getSoloIndexByOffset(ParseUtil.stoi(getColumnId(), -1),
						DataHelper.fromOffset.get(getColumnId()),
						DataHelper.toOffset.get(getColumnId()), false,
						getCatPosition());
		if (catIndexArticle != null) {
			listenerCallBack(catIndexArticle, true, false);
			checkIndexLoading(0);
		} else {
			checkIndexLoading(2);
		}
	}

	private int getCatPosition() {
		return mProcess.getCatPosition(currentCatId);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SoloApplication.exit();
	}

}
