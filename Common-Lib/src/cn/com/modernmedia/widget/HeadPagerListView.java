package cn.com.modernmedia.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * 
 * listview的headview是可以左右滑动的，当左右滑动时，拦截了listview本身的上下滑动
 * 
 * @author ZhuQiao
 * 
 */
public class HeadPagerListView extends ListView {
	private View scrollView;

	public HeadPagerListView(Context context) {
		super(context);
	}

	public HeadPagerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public View getScrollView() {
		return scrollView;
	}

	public void setScrollView(View scrollView) {
		this.scrollView = scrollView;
	}

	/**
	 * 给listview设置参数
	 * 
	 * @param dividerNull
	 *            是否设置divider
	 * @param footerDividersEnabled 是否给listview设置footerview divider
	 * @param headerDividersEnabled 是否给listview设置headview divider
	 */
	public void setParam(boolean dividerNull, boolean footerDividersEnabled,
			boolean headerDividersEnabled) {
		if (dividerNull) {
			setDivider(null);
		}
		setCacheColorHint(Color.TRANSPARENT);
		setDivider(null);
		setFadingEdgeLength(0);
		setFooterDividersEnabled(footerDividersEnabled);
		setHeaderDividersEnabled(headerDividersEnabled);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (scrollView != null) {
			Rect rect = new Rect();
			scrollView.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY())) {
				return false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}
}
