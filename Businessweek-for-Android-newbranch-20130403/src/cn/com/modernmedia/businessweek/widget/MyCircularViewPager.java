package cn.com.modernmedia.businessweek.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.businessweek.adapter.MyCircularPagerAdapter;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.widget.CircularViewPager;

/**
 * Ñ­»·viewpager
 * 
 * @author ZhuQiao
 * 
 */
public class MyCircularViewPager extends CircularViewPager {

	public MyCircularViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public MyPagerAdapter fetchAdapter(Context context, List<ArticleItem> list) {
		return new MyCircularPagerAdapter(context, list);
	}

}
