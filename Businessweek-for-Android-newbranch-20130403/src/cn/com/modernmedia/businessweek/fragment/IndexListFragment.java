package cn.com.modernmedia.businessweek.fragment;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.modernmedia.Fragment.BaseFragment;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.adapter.IndexAdapter;
import cn.com.modernmedia.businessweek.widget.IndexHeadView;
import cn.com.modernmedia.listener.ListScrollStateListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.IndexArticle.Today;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.widget.HeadPagerListView;

/**
 * 首页列表
 * 
 * @author ZhuQiao
 * 
 */
public class IndexListFragment extends BaseFragment {
	private Context mContext;
	private HeadPagerListView listView;
	private IndexAdapter adapter;
	private IndexHeadView headView;
	private ListScrollStateListener scrollListener;
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
		listView = new HeadPagerListView(mContext);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setDivider(null);
		listView.setFadingEdgeLength(0);
		listView.setFooterDividersEnabled(true);
		listView.setHeaderDividersEnabled(true);
		headView = new IndexHeadView(mContext);
		// listView.addHeaderView(headView);
		// listView.setAdapter(adapter);
		adapter = new IndexAdapter(mContext);
		scrollListener = adapter.getListener();
		listView.setScrollView(headView.getViewPager());
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollListener == null)
					return;
				if (scrollState == SCROLL_STATE_FLING
						|| scrollState == SCROLL_STATE_TOUCH_SCROLL) {
					scrollListener.scrolling();
				} else if (scrollState == SCROLL_STATE_IDLE) {
					scrollListener.scrollIdle();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContext instanceof MainActivity) {
					if (listView.getHeaderViewsCount() > 0) {
						// 因为有headview，所有要-1
						position = position - 1;
					}
					if (clickListener != null) {
						clickListener.onItemClick(view, position);
					}
				}
			}
		});
		refresh();
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
			((MainActivity) mContext).setScrollView(3, null);
			if (entry instanceof IndexArticle) {
				IndexArticle indexArticle = (IndexArticle) entry;
				if (indexArticle.getTitleArticleList() == null
						|| indexArticle.getTitleArticleList().isEmpty()) {
					listView.removeHeaderView(headView);
				} else {
					if (listView.getHeaderViewsCount() == 0) {
						// 把adapter置为null.不然会报Cannot add header view to list --
						// setAdapter has already been called.错误
						listView.setAdapter(null);
						listView.addHeaderView(headView);
					}
					headView.setData(indexArticle);
				}
				setValuesForWidget(indexArticle);
				LogHelper.logAndroidShowHighlights();
			} else if (entry instanceof CatIndexArticle) {
				CatIndexArticle catIndexArticle = (CatIndexArticle) entry;
				if (catIndexArticle.getTitleActicleList() == null
						|| catIndexArticle.getTitleActicleList().isEmpty()) {
					listView.removeHeaderView(headView);
				} else {
					if (listView.getHeaderViewsCount() == 0) {
						listView.setAdapter(null);
						listView.addHeaderView(headView);
					}
					headView.setData(catIndexArticle);
				}
				setValuesForWidget(catIndexArticle);
				LogHelper.logAndroidShowColumn(mContext,
						catIndexArticle.getId() + "");
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
		// TODO 先添加adapter，因为添加head的时候被置为null了，没有adapter就不显示headview
		listView.setAdapter(adapter);
		if (todayList == null || todayList.isEmpty())
			return;
		for (Today today : todayList) {
			if (today == null)
				continue;
			List<ArticleItem> itemList = today.getArticleItemList();
			if (itemList == null || itemList.isEmpty())
				continue;
			adapter.setData(itemList);
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
		listView.setAdapter(adapter);
		if (list == null || list.isEmpty())
			return;
		adapter.setData(list);
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

}
