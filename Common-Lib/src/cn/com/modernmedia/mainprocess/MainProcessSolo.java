package cn.com.modernmedia.mainprocess;

import android.content.Context;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.mainprocess.MainProcessManage.FetchDataCallBack;
import cn.com.modernmedia.mainprocess.MainProcessManage.ProcessType;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.SoloColumn;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;

/**
 * 独立栏目显示、接口帮助类
 * 
 * @author user
 * 
 */
public abstract class MainProcessSolo extends BaseMainProcess {
	private Context mContext;
	private SoloProcessHelper helper;

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

	public MainProcessSolo(Context context, FetchDataCallBack fetchCallBack,
			ProcessType processType) {
		super(context, fetchCallBack, processType);
		mContext = context;
		helper = new SoloProcessHelper(mContext) {

			@Override
			public void fetchSoloData(String from, String to) {
				MainProcessSolo.this.fetchSoloData(from, to);
			}

			@Override
			public void showSoloChildCat(int parentId) {
				if (ConstData.isIndexPager()) {
					// TODO首页滑屏
					((CommonMainActivity) mContext).showIndexPager();
				} else {
					((CommonMainActivity) mContext).showSoloChildCat(parentId);
				}
			}

			@Override
			protected void doAfterFetchData(CatIndexArticle catIndex,
					boolean fromDb, boolean newData) {
				super.doAfterFetchData(catIndex, fromDb, newData);
				MainProcessSolo.this
						.doAfterFetchData(catIndex, fromDb, newData);
			}

		};
	}

	/**
	 * 获取独立栏目列表
	 */
	@Override
	protected void getSoloCatList() {
		errorType = 5;
		mController.getSoloColumn(mProcessType == ProcessType.Cache,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						doAfterFetchSoloCatList(entry);
					}
				});
	}

	/**
	 * getSoloCatList获取之后的操作
	 * 
	 * @param entry
	 */
	protected void doAfterFetchSoloCatList(Entry entry) {
		if (entry instanceof SoloColumn) {
			SoloColumn soloColumn = (SoloColumn) entry;
			CommonApplication.soloColumn = soloColumn;
			if (CommonApplication.issue != null
					&& CommonApplication.issue.getId() == 0
					&& ParseUtil.listNotNull(soloColumn.getList())) {
				((CommonMainActivity) mContext).setDataForColumn(soloColumn);
				showSoloChildCat(soloColumn.getList().get(0).getId(), false);
			} else {
				if (!checkFecthIndex()) {
					// TODO 如果缓存数据出错，去服务器上取，避免永远进不了应用
					if (mProcessType == ProcessType.Cache) {
						fetchCallBack.afterFetch(mProcessType);
					}
				}
			}
		}
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
	@Override
	public void getSoloArticleList(final int catId, final boolean isPull,
			final String fromOffset, final String toOffset,
			final boolean newData) {
		helper.getSoloArticleList(catId, isPull, fromOffset, toOffset, newData);
	}

	/**
	 * 显示独立栏目
	 * 
	 * @param parentId
	 */
	@Override
	public void showSoloChildCat(int parentId, boolean fromClick) {
		showLoad(false);
		helper.showSoloChildCat(parentId, fromClick);
	}

	/**
	 * 添加子view接口，全部添加成功之后取数据
	 * 
	 * @param key
	 *            child cat name
	 * @param listener
	 */
	@Override
	public void addSoloListener(String key, FetchSoloListener listener) {
		helper.addSoloListener(key, listener);
	}

	/**
	 * 当添加全部view之后获取独立栏目数据
	 */
	protected abstract void fetchSoloData(String from, String to);

	/**
	 * 获取独立栏目文章列表缓存
	 * 
	 * @use 1.首次取缓存时使用
	 * @use 2.当已经进入过一次之后，下次进入先取缓存
	 */
	protected boolean getArticleListFromDb() {
		return helper.getArticleListFromDb();
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
		showLoad(false);
		moveToLeft();
		moveToIndex();
	}

	protected void fecthDataError(boolean fromDb, boolean newData,
			boolean isPull) {
		showLoad(false);
		helper.fecthDataError(fromDb, newData, isPull);
	}
}
