package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import cn.com.modernmedia.model.ArticleItem;

/**
 * 自定义viewpager，拦截gallery滑动事件
 * @author ZhuQiao
 */
public class CommonViewPager extends CircularViewPager<ArticleItem> {
	private AtlasViewPager pager;// 图集
	private ArticleDetailItem articleDetailItem;// 文章
	private float lastX;
	private boolean isScroll;
	private GestureDetector gestureDetector = new GestureDetector(
			new OnGestureListener() {

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return false;
				}

				@Override
				public void onShowPress(MotionEvent e) {
				}

				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					isScroll = true;
					return false;
				}

				@Override
				public void onLongPress(MotionEvent e) {
				}

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					return false;
				}

				@Override
				public boolean onDown(MotionEvent e) {
					return false;
				}
			});

	public CommonViewPager(Context context) {
		super(context);
	}

	public CommonViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setPager(AtlasViewPager pager) {
		this.pager = pager;
		articleDetailItem = null;
	}

	public void setArticleDetailItem(ArticleDetailItem articleDetailItem) {
		this.articleDetailItem = articleDetailItem;
		pager = null;
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
		} else if (articleDetailItem != null
				&& articleDetailItem.getErrorType() == 2) {
			// TODO 当webview显示错误时,滑动会被process_layout的onClick拦截
			isScroll = false;
			gestureDetector.onTouchEvent(event);
			if (isScroll) {
				// TODO 当滑动时,拦截掉事件传递
				return true;
			}
		}
		return super.onInterceptTouchEvent(event);
	}

}