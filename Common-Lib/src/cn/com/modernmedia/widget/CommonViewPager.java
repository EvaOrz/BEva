package cn.com.modernmedia.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义viewpager，拦截gallery滑动事件
 * 
 * @author ZhuQiao
 * 
 */
public class CommonViewPager extends ViewPager {
	private AtlasViewPager pager;
	private float lastX;

	public CommonViewPager(Context context) {
		super(context);
	}

	public CommonViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setPager(AtlasViewPager pager) {
		this.pager = pager;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (pager != null && pager.getTotalNum() > 0) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				lastX = event.getX();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (pager.getCurrentItem() == 0 && lastX <= event.getX()) {
					// TODO
				} else if (pager.getCurrentItem() == pager.getTotalNum() - 1
						&& lastX >= event.getX()) {
					// TODO
				} else {
					return false;
				}
			}
		}
		return super.onInterceptTouchEvent(event);
	}

}
