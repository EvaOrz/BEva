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

	private float lastX;

	/**
	 * 判断viewpager是否被锁住滑动
	 * 
	 * @param event
	 * @return true viewpager无法滑动；false 可以
	 */
	/*
	 * public boolean checkLockScroll(MotionEvent event) { if (this == null ||
	 * pager.getTotalNum() == 0) return false; if (event.getAction() ==
	 * MotionEvent.ACTION_DOWN) { lastX = event.getX(); lockScroll = true; }
	 * else if (event.getAction() == MotionEvent.ACTION_MOVE) { if
	 * (pager.getCurrentItem() == 0 && lastX <= event.getX()) { lockScroll =
	 * false; } else if (pager.getCurrentItem() == pager.getTotalNum() - 1 &&
	 * lastX >= event.getX()) { lockScroll = false; } else { lockScroll = true;
	 * } lastX = event.getX(); } return lockScroll; }
	 */

}
