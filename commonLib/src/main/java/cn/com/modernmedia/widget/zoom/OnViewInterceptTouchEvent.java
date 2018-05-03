package cn.com.modernmedia.widget.zoom;

import android.view.MotionEvent;

/**
 * view拦截手势
 * 
 * @author user
 *
 */
public interface OnViewInterceptTouchEvent {

	/**
	 * 是否拦截手势(由于view没有Intercept事件，默认为它构造一个)
	 * 
	 * @eg 比如在viewPager中，itemView想拦截事件，但是监听到滑动手势，viewPager默认会拦截掉，导致不能实现该功能
	 * 
	 * @param event
	 * @return
	 */
	public boolean onInterceptTouchEvent(MotionEvent event);
}
