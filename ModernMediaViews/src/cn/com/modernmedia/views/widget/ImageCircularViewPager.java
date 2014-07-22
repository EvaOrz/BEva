package cn.com.modernmedia.views.widget;

import android.content.Context;
import android.util.AttributeSet;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.widget.CircularViewPager;

public class ImageCircularViewPager extends CircularViewPager<ArticleItem> {

	public ImageCircularViewPager(Context context) {
		this(context, null);
	}

	public ImageCircularViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
