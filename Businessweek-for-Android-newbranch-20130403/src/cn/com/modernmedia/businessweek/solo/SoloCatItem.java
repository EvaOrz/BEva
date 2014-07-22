package cn.com.modernmedia.businessweek.solo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.unit.BusinessweekTools;
import cn.com.modernmedia.businessweek.widget.IndexHeadView;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.PullToRefreshListView;
import cn.com.modernmedia.widget.PullToRefreshListView.OnRefreshListener;
import cn.com.modernmediasolo.CommonSoloActivity.FetchSoloListener;

/**
 * SOLO子栏目item
 * 
 * @author ZhuQiao
 * 
 */
public class SoloCatItem extends BaseView implements FetchSoloListener {
	private Context mContext;
	private PullToRefreshListView listView;
	private IndexHeadView headView;
	private SoloIndexAdapter adapter;
	private int childSize;
	private SoloColumnChild mColumnChild;
	private CatIndexArticle mCatIndex;
	private List<ArticleItem> mSoloHeadIndex = new ArrayList<ArticleItem>();

	public SoloCatItem(Context context) {
		this(context, null);
	}

	public SoloCatItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		mSoloHeadIndex.clear();
		this.addView(LayoutInflater.from(mContext).inflate(R.layout.child_item,
				null));
		initProcess();
		listView = (PullToRefreshListView) findViewById(R.id.child_cat_listview);
		listView.setParam(true, true, true);
		listView.enableAutoFetch(true, true);
		listView.setCatId(ParseUtil.stoi(((MainActivity) mContext)
				.getColumnId()));
		headView = new IndexHeadView(mContext);
		listView.setScrollView(headView.getViewPager());
		adapter = new SoloIndexAdapter(mContext);
		listView.addHeaderView(headView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContext instanceof MainActivity) {
					position = position - listView.getHeaderViewsCount();// 因为有headview，所有要-
					if (adapter.getCount() > position) {
						ArticleItem item = adapter.getItem(position);
						LogHelper.logOpenArticleFromColumnPage(mContext,
								item.getArticleId() + "", item.getCatId() + "");
						BusinessweekTools.clickSlate(SoloCatItem.this,
								mContext, item, ArticleType.Solo);
					}
				}
			}
		});
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (mContext instanceof MainActivity) {
					if (mCatIndex != null) {
						((MainActivity) mContext).getSoloArticleList(mCatIndex
								.getId(), true, DataHelper.toOffset
								.get(((MainActivity) mContext).getColumnId()),
								"0", true);
					}
				}
			}

			@Override
			public void onLoad() {
				if (mContext instanceof MainActivity) {
					if (mCatIndex != null) {
						((MainActivity) mContext).getSoloArticleList(mCatIndex
								.getId(), true, "0", DataHelper.fromOffset
								.get(((MainActivity) mContext).getColumnId()),
								false);
					}
				}
			}

			@Override
			public void onPulling() {
				if (mContext instanceof MainActivity) {
					((MainActivity) mContext).setPulling(true);
				}
			}

			@Override
			public void onRefreshComplete() {
				if (mContext instanceof MainActivity) {
					((MainActivity) mContext).setPulling(false);
				}
			}

			@Override
			public void onRefreshDone() {
				if (mContext instanceof MainActivity) {
					((MainActivity) mContext).setPulling(false);
				}
			}
		});
	}

	@Override
	protected void reLoad() {
	}

	public void setData(SoloColumnChild soloColumnChild) {
		mColumnChild = soloColumnChild;
		adapter.clearData();
		if (mContext instanceof MainActivity)
			((MainActivity) mContext).addSoloListener(mColumnChild.getName(),
					this);
	}

	private void setDataForList(CatIndexArticle catIndex, boolean newData) {
		if (mColumnChild == null)
			return;
		mCatIndex = catIndex;
		List<ArticleItem> soloHeadIndex = mCatIndex.getHeadMap().get(
				mColumnChild.getName());
		List<ArticleItem> soloListIndex = mCatIndex.getListMap().get(
				mColumnChild.getName());

		// if (newData) {
		// adapter.clear();
		// mSoloHeadIndex.addAll(0, soloHeadIndex);
		// } else
		// mSoloHeadIndex.addAll(soloHeadIndex);

		// TODO 独立栏目接口每次都会返回焦点图，所以每次获得数据后更新焦点图，而不是添加
		mSoloHeadIndex.clear();
		mSoloHeadIndex = soloHeadIndex;

		adapter.setData(soloListIndex, newData);
		if (ParseUtil.listNotNull(mSoloHeadIndex)) {
			headView.setPadding(0, 0, 0, 0);
			headView.invalidate();
			headView.setSoloData(mSoloHeadIndex);
			childSize = mSoloHeadIndex.size();
		} else {
			if (!ParseUtil.listNotNull(headView.getDataList())) {
				headView.setPadding(0, -MyApplication.width / 2, 0, 0);
				headView.invalidate();
			}
		}
		listView.setItemCount(adapter.getCount());
	}

	public IndexHeadView getHeadView() {
		return headView;
	}

	public int getChildSize() {
		return childSize;
	}

	@Override
	public void fetchSoloData(CatIndexArticle catIndex, boolean newData,
			boolean fromNet) {
		if (catIndex == null)
			listView.reStoreStatus();
		if (newData) {
			listView.onRefreshComplete(fromNet,
					ParseUtil.stoi(((MainActivity) mContext).getColumnId()),
					mCatIndex != null);
		} else {
			listView.onLoadComplete();
		}
		if (catIndex != null)
			setDataForList(catIndex, newData);
	}

	public void reStoreRefresh() {
		listView.reStoreStatus();
	}
}
