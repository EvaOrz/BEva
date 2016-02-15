package cn.com.modernmedia.views.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.adapter.WeeklyCircularPagerAdapter;
import cn.com.modernmedia.views.index.head.IndexHeadCircularViewPager;

/**
 * iweekly类型焦点图viewpager
 * 
 * @author user
 * 
 */
public class WeeklyCircularViewPager extends IndexHeadCircularViewPager {
	private int width = 0;
	private int height = 0;

	public WeeklyCircularViewPager(Context context) {
		this(context, null);
	}

	public WeeklyCircularViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public MyPagerAdapter<ArticleItem> fetchAdapter(Context context,
			List<ArticleItem> list) {
		String tag = this.getTag() == null ? "" : this.getTag().toString();
		return new WeeklyCircularPagerAdapter(context, list, tag, width, height);
	}
}
