package cn.com.modernmedia.views.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.listener.FlowPositionChangedListener;
import cn.com.modernmedia.views.listener.GalleryOnTouchListener;
import cn.com.modernmedia.views.listener.GalleryScrollListener;
import cn.com.modernmedia.views.util.WeeklyLogEvent;

/**
 * 垂直画报
 * 
 * @author user
 * 
 */
public class VerticalFlowGallery extends ViewGroup implements
		FlowPositionChangedListener {
	private Context mContext;

	/** 图片宽、高 **/
	public static int childWidth, childHeight;
	/** 图片间隔 **/
	private int space;
	/** 每次整体移动距离 **/
	private int everyMove;

	private Scroller scroller;
	private GestureDetector gestureDetector;
	private int currentIndex;// 当前屏
	private boolean fling;// 是否是快速滑动
	private int current;

	/** 滑到状态接口 **/
	private GalleryScrollListener listener;
	/** 点击图片接口 **/
	private List<GalleryOnTouchListener> touchListeners = new ArrayList<GalleryOnTouchListener>();

	public VerticalFlowGallery(Context context) {
		this(context, null);
	}

	public VerticalFlowGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		int height = CommonApplication.height
				- mContext.getResources().getDimensionPixelSize(
						R.dimen.index_titlebar_height);
		childHeight = height * 7 / 10;
		childWidth = childHeight * 2 / 3;
		space = height * 3 / 85;
		everyMove = space + childHeight;

		scroller = new Scroller(mContext);
		gestureDetector = new GestureDetector(new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				if (getChildCount() > currentIndex
						&& touchListeners.size() > currentIndex) {
					touchListeners.get(currentIndex).onTouch(
							getChildAt(currentIndex));
				}
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				if ((distanceY > 0 && currentIndex < getChildCount() - 1)// 防止移动过最后一页
						|| (distanceY < 0 && getScrollY() > 0)) {// 防止向第一页之前移动
					scrollBy(0, (int) distanceY);
				}
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (Math.abs(velocityY) > ViewConfiguration.get(mContext)
						.getScaledMinimumFlingVelocity()) {// 判断是否达到最小轻松速度，取绝对值的
					if (velocityY > 0 && currentIndex > 0) {
						fling = true;
						scrollToScreen(currentIndex - 1, true);
					} else if (velocityY < 0
							&& currentIndex < getChildCount() - 1) {
						fling = true;
						scrollToScreen(currentIndex + 1, true);
					}
				}
				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
	}

	public void reSet() {
		touchListeners.clear();
		current = 0;
		currentIndex = 0;
		scrollTo(0, 0);
	}

	public void addListener(GalleryOnTouchListener listener) {
		touchListeners.add(listener);
	}

	public void setListener(GalleryScrollListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setVisibility(View.VISIBLE);
			child.measure(r - l, b - t);
			int top = space * (i + 1) + childHeight * i;
			int left = (r - l - childWidth) / 2;
			child.layout(left, top, left + childWidth, top + childHeight);
		}
	}

	// 主要功能是计算拖动的位移量、更新背景、设置要显示的屏幕
	@Override
	public void computeScroll() {
		if ((!scroller.isFinished()) && (scroller.computeScrollOffset())) {
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = scroller.getCurrX();
			int y = scroller.getCurrY();

			if ((oldX != x) || (oldY != y)) {
				scrollTo(x, y);
			}
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (!fling) {
				computeScrollToTarget();
			}
			fling = false;
		}
		return true;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		current = t;
		notifyScrollChange(true);
	}

	/**
	 * 滑动中,改变view大小
	 */
	private void notifyScrollChange(boolean isScroll) {
		if (listener == null)
			return;
		if (isScroll)
			listener.scrolling(current, 0);
		else
			listener.scrollEnd(currentIndex);
	}

	private void scrollToScreen(int targetIndex, boolean smoothScroll) {
		if (targetIndex < 0) {
			targetIndex = 0;
		}
		if (targetIndex > getChildCount() - 1) {
			targetIndex = getChildCount() - 1;
		}
		int destinationY = targetIndex * everyMove - getScrollY();
		currentIndex = targetIndex;
		if (smoothScroll) {
			int duration = Math.abs(destinationY);
			scroller.startScroll(0, getScrollY(), 0, destinationY, duration);
		} else {
			scroller.startScroll(0, getScrollY(), 0, destinationY);
		}
		postInvalidate();
		notifyScrollChange(false);
	}

	private void computeScrollToTarget() {
		int targetScreen = (current + (everyMove / 2)) / everyMove;
		scrollToScreen(targetScreen, true);
		WeeklyLogEvent.logAndroidShowCoverflow();
	}

	@Override
	public void setCurrentPosition(int position) {
		scrollToScreen(position, false);
	}

}
