package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;

public abstract class CommonHeadView extends BaseView {

	public CommonHeadView(Context context) {
		this(context, null);
	}

	public CommonHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public abstract CircularViewPager getViewPager();
}
