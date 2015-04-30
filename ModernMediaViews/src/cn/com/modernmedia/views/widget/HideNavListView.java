package cn.com.modernmedia.views.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.com.modernmedia.views.index.IndexView;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmediaslate.SlateApplication;

/**
 * 隐藏导航栏listview
 * 
 * @author zhuqiao
 *
 */
public class HideNavListView extends CheckFooterListView {

	public HideNavListView(Context context) {
		this(context, null);
	}

	public HideNavListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (SlateApplication.mConfig.getNav_hide() == 0) {
			setShowPull(true);
		} else {
			setShowPull(IndexView.height == IndexView.BAR_HEIGHT);
		}
		return super.onTouchEvent(event);
	}

}
