package cn.com.modernmedia.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * 
 * listview��headview�ǿ������һ����ģ������һ���ʱ��������listview��������»���
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

	public void setScrollView(View scrollView) {
		this.scrollView = scrollView;
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
