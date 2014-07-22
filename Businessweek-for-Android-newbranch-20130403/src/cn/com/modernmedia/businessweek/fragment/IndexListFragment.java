package cn.com.modernmedia.businessweek.fragment;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.modernmedia.CommonMainActivity.OnPullRefreshListener;
import cn.com.modernmedia.Fragment.BaseFragment;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.adapter.IndexAdapter;
import cn.com.modernmedia.businessweek.widget.IndexHeadView;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.IndexArticle.Today;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.PullToRefreshListView;
import cn.com.modernmedia.widget.PullToRefreshListView.OnRefreshListener;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页Fragment
 * 
 * @author ZhuQiao
 * 
 */
public class IndexListFragment extends BaseFragment implements
		OnPullRefreshListener {
	private Context mContext;
	private PullToRefreshListView listView;
	private IndexAdapter adapter;
	private IndexHeadView headView;
	private cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener clickListener;
	private Entry entry;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listView = new PullToRefreshListView(mContext);
		listView.setParam(true, true, true);
		listView.enableAutoFetch(false, false);
		headView = new IndexHeadView(mContext);
		adapter = new IndexAdapter(mContext);
		listView.addHeaderView(headView);
		listView.setAdapter(adapter);
		listView.setScrollView(headView.getViewPager());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContext instanceof MainActivity) {
					// 如果有headview
					position = position - listView.getHeaderViewsCount();
					if (clickListener != null) {
						clickListener.onItemClick(view, position);
					}
				}
			}
		});
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				((MainActivity) mContext).pullToGetIssue();
			}

			@Override
			public void onLoad() {
			}

			@Override
			public void onPulling() {
				((MainActivity) mContext).setPulling(true);
			}

			@Override
			public void onRefreshComplete() {
				((MainActivity) mContext).setPulling(false);
			}

			@Override
			public void onRefreshDone() {
				((MainActivity) mContext).setPulling(false);
			}
		});
		refresh();
		if (mContext instanceof MainActivity) {
			((MainActivity) mContext).setRefreshListener(this);
		}
		return listView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void setData(Entry entry) {
		this.entry = entry;
	}

	@Override
	public void refresh() {
		if (entry != null && mContext instanceof MainActivity
				&& listView != null) {
			listView.onRefreshComplete(false, -1);
			((MainActivity) mContext).setScrollView(3, null);
			if (entry instanceof IndexArticle) {
				((MainActivity) mContext).setColumnPosition(0);
				IndexArticle indexArticle = (IndexArticle) entry;
				if (ParseUtil.listNotNull(indexArticle.getTitleArticleList())) {
					headView.setPadding(0, 0, 0, 0);
					headView.invalidate();
					headView.setData(indexArticle);
				} else {
					headView.setPadding(0, -MyApplication.width / 2, 0, 0);
					headView.invalidate();
				}
				setValuesForWidget(indexArticle);
				LogHelper.logAndroidShowHighlights();
				listView.setCatId(0);
			} else if (entry instanceof CatIndexArticle) {
				CatIndexArticle catIndexArticle = (CatIndexArticle) entry;
				if (ParseUtil
						.listNotNull(catIndexArticle.getTitleActicleList())) {
					headView.setPadding(0, 0, 0, 0);
					headView.invalidate();
					headView.setData(catIndexArticle);
				} else {
					headView.setPadding(0, -MyApplication.width / 2, 0, 0);
					headView.invalidate();
				}
				setValuesForWidget(catIndexArticle);
				LogHelper.logAndroidShowColumn(mContext,
						catIndexArticle.getId() + "");
				listView.setCatId(catIndexArticle.getId());
			}
		}
	}

	/**
	 * 首页
	 * 
	 * @param indexArticle
	 */
	private void setValuesForWidget(IndexArticle indexArticle) {
		List<Today> todayList = indexArticle.getTodayList();
		adapter.clear();
		if (ParseUtil.listNotNull(todayList)) {
			for (Today today : todayList) {
				if (today == null)
					continue;
				List<ArticleItem> itemList = today.getArticleItemList();
				if (ParseUtil.listNotNull(itemList)) {
					adapter.setData(itemList);
				}
			}
			listView.setSelection(0);
		}
	}

	/**
	 * 栏目首页
	 * 
	 * @param catIndexArticle
	 */
	private void setValuesForWidget(CatIndexArticle catIndexArticle) {
		List<ArticleItem> list = catIndexArticle.getArticleItemList();
		adapter.clear();
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
			listView.setSelection(0);
		}
	}

	public void setClickListener(
			cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener clickListener) {
		this.clickListener = clickListener;
	}

	public IndexAdapter getAdapter() {
		return adapter;
	}

	public IndexHeadView getHeadView() {
		return headView;
	}

	@Override
	public void showView(boolean show) {
		if (listView != null)
			listView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onRefresh(boolean success) {
		if (success) {
			int catId = -1;
			if (entry instanceof IndexArticle)
				catId = 0;
			else if (entry instanceof CatIndexArticle)
				catId = ((CatIndexArticle) entry).getId();
			listView.onRefreshComplete(catId != -1, catId, true);
		} else
			listView.onRefreshComplete(false, -1, true);
	}

}
