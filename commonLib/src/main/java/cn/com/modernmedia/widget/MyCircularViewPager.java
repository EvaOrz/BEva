package cn.com.modernmedia.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.com.modernmedia.adapter.MyCircularPagerAdapter;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.widget.zoom.OnViewInterceptTouchEvent;

/**
 * 文章页大图gallery viewpager
 * 
 * 要求可缩放
 * 
 * @author Eva.
 * 
 */
public class MyCircularViewPager extends CircularViewPager<ArticleItem> {
	private int width = 0;
	private int height = 0;
	private OnViewInterceptTouchEvent onViewIntercept;

	public MyCircularViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public OnViewInterceptTouchEvent getOnViewIntercept() {
		return onViewIntercept;
	}

	public void setOnViewIntercept(OnViewInterceptTouchEvent onViewIntercept) {
		this.onViewIntercept = onViewIntercept;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (onViewIntercept != null
				&& onViewIntercept.onInterceptTouchEvent(event)) {
			return false;
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public MyPagerAdapter<ArticleItem> fetchAdapter(Context context,
			List<ArticleItem> list) {
		String tag = this.getTag() == null ? "" : this.getTag().toString();
		MyCircularPagerAdapter adapter = new MyCircularPagerAdapter(context,
				list, tag, width, height);
		adapter.setViewPager(this);
		return adapter;
	}

}
