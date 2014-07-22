package cn.com.modernmedia.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.com.modernmedia.listener.NotifyArticleDesListener;

/**
 * 图集
 * 
 * @author ZhuQiao
 * 
 */
public class AtlasViewPager extends ViewPager {
	private int totalNum = 0;// view总数
	private NotifyArticleDesListener listener;
	private boolean intercept = false;

	public AtlasViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnPageChangeListener(new OnPageChangeListener() {

			/**
			 * 当一个页面即将被加载时，调用此方法 Position index of the new selected page
			 */
			@Override
			public void onPageSelected(int position) {
				if (listener != null)
					listener.updateDes(position);
			}

			/**
			 * 当在一个页面滚动时，调用此方法postion:要滑向页面索引
			 */
			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
			}

			/**
			 * 状态有三个0空闲，1是增在滑行中，2目标加载完毕
			 */
			@Override
			public void onPageScrollStateChanged(int state) {
				if (listener != null)
					listener.updatePage(state);
			}
		});
	}

	public void setValue(int totalNum) {
		this.totalNum = totalNum;
	}

	public void setListener(NotifyArticleDesListener listener) {
		this.listener = listener;
		listener.updateDes(0);
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setIntercept(boolean intercept) {
		this.intercept = intercept;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (intercept) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

}
