package cn.com.modernmedia.adapter;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;

/**
 * 需要判断滑动状态的adapter(在滑动停止时渲染文字、图片等元素)
 * 
 * @author ZhuQiao
 * 
 */
public class CheckScrollAdapter<T> extends ArrayAdapter<T> implements
		OnScrollListener {
	protected boolean isScroll = false;

	public CheckScrollAdapter(Context context) {
		super(context, 0);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			isScroll = false;
			this.notifyDataSetChanged();
		} else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
			isScroll = false;
		} else if (scrollState == SCROLL_STATE_FLING) {
			isScroll = true;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

}
