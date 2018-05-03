package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ListAdapter;

/**
 * 需要判断滑动状态的adapter(在滑动停止时渲染文字、图片等元素)
 * 
 * @author ZhuQiao
 * 
 */
public class CheckScrollListview extends HeadPagerListView implements
		OnScrollListener {
	private Adapter mAdapter;
	private OnScrollListener checkScrollView;

	public CheckScrollListview(Context context) {
		super(context);
		setOnScrollListener(this);
	}

	public CheckScrollListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnScrollListener(this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mAdapter instanceof OnScrollListener) {
			((OnScrollListener) mAdapter).onScrollStateChanged(view,
					scrollState);
		}
		if (checkScrollView != null) {
			checkScrollView.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mAdapter instanceof OnScrollListener) {
			((OnScrollListener) mAdapter).onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
		if (checkScrollView != null) {
			checkScrollView.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mAdapter = adapter;
		super.setAdapter(adapter);
	}

	public void setCheckScrollView(OnScrollListener checkScrollView) {
		this.checkScrollView = checkScrollView;
	}
}
