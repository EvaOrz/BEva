package cn.com.modernmedia.views.solo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.mainprocess.MainProcessSolo.FetchSoloListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.index.IndexHeadView;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.listener.ChildCatClickListener;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.PullToRefreshListView;
import cn.com.modernmedia.widget.PullToRefreshListView.OnRefreshListener;

/**
 * SOLO子栏目item
 * 
 * @author ZhuQiao
 * 
 */
public class SoloCatItem implements FetchSoloListener, ChildCatClickListener {
	private Context mContext;
	private View view;
	private PullToRefreshListView listView;
	private IndexHeadView headView;// 焦点图
	private BaseChildCatHead childHead;// 栏目导航栏
	private BaseIndexAdapter adapter;
	private SoloColumnChild mColumnChild;
	private CatIndexArticle mCatIndex;
	private List<ArticleItem> mSoloHeadIndex = new ArrayList<ArticleItem>();
	private IndexListParm parm;
	private boolean isRefresh;
	private boolean hasAddHead = false;// 当没有焦点图位置时使用
	private IndexViewPagerItem indexViewPagerItem;

	private List<View> selfScrollViews = new ArrayList<View>();

	public SoloCatItem(Context context, int parentId,
			IndexViewPagerItem indexViewPagerItem) {
		mContext = context;
		this.indexViewPagerItem = indexViewPagerItem;
		init(parentId);
	}

	private void init(int parentId) {
		parm = ParseProperties.getInstance(mContext).parseIndexList();
		mSoloHeadIndex.clear();
		view = LayoutInflater.from(mContext).inflate(R.layout.solo_index, null);
		listView = (PullToRefreshListView) view
				.findViewById(R.id.solo_cat_listview);
		listView.setParam(true, true, true);
		listView.enableAutoFetch(true, true);
		listView.setCheck_angle(true);
		listView.setCatId(ParseUtil.stoi(((CommonMainActivity) mContext)
				.getColumnId()));
		if (((ViewsMainActivity) mContext).getDefaultHeadView() != null)
			listView.addHeaderView(((ViewsMainActivity) mContext)
					.getDefaultHeadView());
		// 栏目导航栏
		if (ParseUtil.listNotNull(ModernMediaTools.getSoloChild(parentId))
				&& parm.getCat_list_hold() == 0) {
			if (parm.getCat_list_type().equals(V.BUSINESS)) {
				childHead = new BusChildCatHead(mContext, indexViewPagerItem);
			} else if (parm.getCat_list_type().equals(V.IWEEKLY)) {
				childHead = new WeeklyChildCatHead(mContext, indexViewPagerItem);
			}
		}
		if (childHead != null) {
			childHead.setSoloValues(parentId);
			listView.addHeaderView(childHead.fetchView());
			listView.setScrollView(childHead.fetchView());
			selfScrollViews.add(childHead.fetchView());
		}
		// 焦点图
		headView = V.getIndexHeadView(mContext, parm);
		// 列表adapter
		adapter = V.getIndexAdapter(mContext, parm);
		if (headView != null) {
			listView.setScrollView(headView);
			listView.addHeaderView(headView);
			selfScrollViews.add(headView);
		}
		adapter.setSoloAdapter(true);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContext instanceof CommonMainActivity) {
					position = position - listView.getHeaderViewsCount();// 因为有headview，所有要-
					if (adapter.getCount() > position) {
						ArticleItem item = adapter.getItem(position);
						LogHelper.logOpenArticleFromColumnPage(mContext,
								item.getArticleId() + "", item.getCatId() + "");
						V.clickSlate(mContext, item, ArticleType.Solo);
						AdvTools.requestClick(item);
					}
				}
			}
		});
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (mContext instanceof CommonMainActivity) {
					if (mCatIndex != null) {
						isRefresh = true;
						getSoloArticleList(
								DataHelper.toOffset.get(mCatIndex.getId() + ""),
								"0", true);
					}
				}
			}

			@Override
			public void onLoad() {
				if (mContext instanceof CommonMainActivity) {
					if (mCatIndex != null) {
						getSoloArticleList(
								"0",
								DataHelper.fromOffset.get(mCatIndex.getId()
										+ ""), false);
					}
				}
			}

			@Override
			public void onPulling() {
			}

			@Override
			public void onRefreshComplete() {
			}

			@Override
			public void onRefreshDone() {
			}
		});
	}

	/**
	 * 如果indexViewPagerItem!=null，说明使用的不是process里的solohelper;
	 * 需要使用indexViewPagerItem里的solohelper;
	 * 
	 * @param soloColumnChild
	 * @param indexViewPagerItem
	 */
	public void setData(SoloColumnChild soloColumnChild) {
		mColumnChild = soloColumnChild;
		adapter.clearData();
		if (mContext instanceof CommonMainActivity) {
			if (indexViewPagerItem != null
					&& indexViewPagerItem.getHelper() != null) {
				indexViewPagerItem.getHelper().addSoloListener(
						mColumnChild.getName(), this);
			} else if (CommonApplication.manage != null) {
				CommonApplication.manage.getProcess().addSoloListener(
						mColumnChild.getName(), this);
			}
		}
	}

	private void setDataForList(CatIndexArticle catIndex, boolean newData) {
		if (mColumnChild == null)
			return;
		mCatIndex = catIndex;
		String name = mColumnChild.getName();
		int headPosition = parm.getHead_position();
		int itemPosition = parm.getItem_position();

		//
		List<ArticleItem> soloHeadIndex = new ArrayList<ArticleItem>();
		if (ParseUtil.mapContainsKey(mCatIndex.getSoloMap(), name)) {
			if (ParseUtil.mapContainsKey(mCatIndex.getSoloMap().get(name),
					headPosition)) {
				soloHeadIndex = mCatIndex.getSoloMap().get(name)
						.get(headPosition);
			}
		}
		List<ArticleItem> soloListIndex = new ArrayList<ArticleItem>();
		if (ParseUtil.mapContainsKey(mCatIndex.getSoloMap(), name)) {
			if (ParseUtil.mapContainsKey(mCatIndex.getSoloMap().get(name),
					itemPosition)) {
				soloListIndex = mCatIndex.getSoloMap().get(name)
						.get(itemPosition);
			}
		}

		// TODO 独立栏目接口每次都会返回焦点图，所以每次获得数据后更新焦点图，而不是添加
		mSoloHeadIndex.clear();
		mSoloHeadIndex = soloHeadIndex;

		if (TextUtils.isEmpty(parm.getHead_type()) && !hasAddHead) {
			// TODO 没有焦点图,需要把焦点图数据添加到列表里显示
			soloListIndex.addAll(0, mSoloHeadIndex);
			hasAddHead = true;
		}

		adapter.setData(soloListIndex, newData);
		if (ParseUtil.listNotNull(mSoloHeadIndex)) {
			showHead();
		} else {
			if (headView != null
					&& !ParseUtil.listNotNull(headView.getDataList())) {
				dissHead();
			}
		}
		listView.setItemCount(adapter.getCount());
	}

	/**
	 * 显示焦点图
	 * 
	 * @param entry
	 */
	private void showHead() {
		if (headView != null) {
			headView.setPadding(0, 0, 0, 0);
			headView.invalidate();
			headView.setSoloData(mSoloHeadIndex);
			if (!headView.isShouldScroll()
					&& selfScrollViews.contains(headView)) {
				selfScrollViews.remove(headView);
			}
		}
	}

	/**
	 * 隐藏焦点图
	 */
	private void dissHead() {
		if (headView != null) {
			headView.setPadding(0, -parm.getHead_height()
					* CommonApplication.width / 720, 0, 0);
			headView.invalidate();
		}
	}

	public IndexHeadView getHeadView() {
		return headView;
	}

	public int getChildSize() {
		if (mSoloHeadIndex == null)
			return 0;
		return mSoloHeadIndex.size();
	}

	@Override
	public void fetchSoloData(CatIndexArticle catIndex, boolean newData,
			boolean fromNet) {
		if (catIndex == null) {
			listView.reStoreStatus();
		}
		if (newData) {
			if (isRefresh)
				listView.onRefreshComplete(fromNet, mCatIndex.getId(),
						mCatIndex != null);
			else
				listView.reStoreStatus();
			isRefresh = false;
		} else {
			listView.onLoadComplete();
		}
		if (catIndex != null)
			setDataForList(catIndex, newData);
	}

	/**
	 * 刷新、加载更多独立栏目
	 * 
	 * @param fromOffset
	 * @param toOffset
	 * @param newData
	 */
	private void getSoloArticleList(String fromOffset, String toOffset,
			boolean newData) {
		if (mCatIndex == null)
			return;
		if (mContext instanceof CommonMainActivity) {
			if (indexViewPagerItem != null
					&& indexViewPagerItem.getHelper() != null) {
				indexViewPagerItem.getHelper().getSoloArticleList(
						mCatIndex.getId(), true, fromOffset, toOffset, newData);
			} else {
				CommonApplication.manage.getProcess().getSoloArticleList(
						mCatIndex.getId(), true, fromOffset, toOffset, newData);
			}
		}
	}

	public void reStoreRefresh() {
		listView.reStoreStatus();
	}

	public View fetchView() {
		return view;
	}

	public List<View> getSelfScrollViews() {
		return selfScrollViews;
	}

	@Override
	public void onClick(int position, int parentId, SoloColumnChild soloChild) {
		if (childHead != null && soloChild != null) {
			childHead.setSelectedItemForSolo(soloChild);
		}
	}

}
