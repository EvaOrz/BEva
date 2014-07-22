package cn.com.modernmedia.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.ScrollStateListener;
import cn.com.modernmedia.listener.SizeCallBack;
import cn.com.modernmedia.util.LogHelper;

/**
 * 主页横向划屏
 * 
 * @author ZhuQiao
 * 
 */
public class MainHorizontalScrollView extends HorizontalScrollView {
	private MainHorizontalScrollView me;
	private Context mContext;
	private View leftView, rightView;// 屏幕左边view
	private boolean leftOut, rightOut;// 是否显示的是左边的view
	/**
	 * 移动中显示的view;0.无。1.左。2.右
	 */
	private int moveOut = 0;
	public static final int ENLARGE_WIDTH = 0;// 扩展宽度
	private int scrollWidth;// 需要滑动的宽度
	private Button leftButton, rightButton;// 切换至左边view的button
	private int current;// 当前滑动的位置
	// private int moveCurrent;// 滑动中的当前位置（如果速率太快，可能会直接从左边滑到右边，这个时候做判断）
	private int left_max_width, right_max_width, mid_right;// 显示左边最大滑动距离
	private Gallery gallery;// 横向滑动中的gallery需要单独处理事件
	private ScrollCallBackListener listener;
	private static final long MIN_TIME = 500;
	private static final int CHECK_TIME = 100;
	private int lastPosition;
	private Runnable scrollTask;
	private VelocityTracker velocityTracker;// 判断滑动速率
	private static final int SNAP_VELOCITY = 2000;// 最低滑动速率

	private ScrollStateListener stateListener = new ScrollStateListener() {

		@Override
		public void onStop() {
			checkCurrent(true, false);
		}
	};

	public MainHorizontalScrollView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public MainHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		// 设置无边缘色
		this.setHorizontalFadingEdgeEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		this.setFillViewport(true);
		this.setVisibility(View.INVISIBLE);
		this.setFadingEdgeLength(0);
		me = this;
		leftOut = false;
		rightOut = false;
		scrollTask = new Runnable() {

			@Override
			public void run() {
				int newPosition = getScrollX();
				if (newPosition == lastPosition) {
					if (stateListener != null)
						stateListener.onStop();
				} else
					startScrollerTask();
			}
		};
	}

	private void startScrollerTask() {
		lastPosition = getScrollX();
		postDelayed(scrollTask, CHECK_TIME);
	}

	/**
	 * 初始化首页信息
	 * 
	 * @param children
	 *            左中右三屏，但左右两屏不可见，为了滑动并且能显示栏目列表和收藏
	 * @param sizeCallBack
	 * @param leftView
	 */
	public void initViews(View[] children, SizeCallBack leftCallback,
			View leftView, View rightView) {
		this.leftView = leftView;
		this.rightView = rightView;
		ViewGroup parent = (ViewGroup) getChildAt(0);

		for (int i = 0; i < children.length; i++) {
			children[i].setVisibility(View.INVISIBLE);
			parent.addView(children[i]);
		}
		OnGlobalLayoutListener listener = new MenuOnGlobalLayoutListener(
				parent, children, leftCallback);
		getViewTreeObserver().addOnGlobalLayoutListener(listener);
	}

	/**
	 * 设置按钮
	 * 
	 * @param leftButton
	 */
	public void setButtons(Button leftButton, Button rightButton) {
		this.leftButton = leftButton;
		this.rightButton = rightButton;
	}

	public void setListener(ScrollCallBackListener listener) {
		this.listener = listener;
	}

	/**
	 * 设置gallery
	 * 
	 * @param gallery
	 */
	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	/**
	 * 首页titlebar上按钮点击
	 * 
	 * @param left
	 */
	public void clickButton(boolean left) {
		if (left) {
			// TODO 如果是smooth，会有白色阴影?!
			viewSlide(leftOut ? 0 : 1, false);
		} else {
			viewSlide(rightOut ? 0 : 2, false);
		}
	}

	/**
	 * 滑动出view
	 * 
	 * @param flag
	 *            0:显示index;1:显示left;2:显示right
	 */
	private void viewSlide(int flag, boolean smooth) {
		if (flag == 0) {
			leftOut = false;
			rightOut = false;
			scrollWidth = left_max_width;
			scrollToDestination(left_max_width, smooth);
			listener.isShowIndex(true);
		} else if (flag == 1) {
			leftOut = true;
			rightOut = false;
			scrollWidth = 0;
			scrollToDestination(0, smooth);
			listener.isShowIndex(false);
			LogHelper.logOpenColumnList(mContext);
		} else {
			leftOut = false;
			rightOut = true;
			scrollWidth = right_max_width;
			scrollToDestination(right_max_width, smooth);
			listener.isShowIndex(false);
			LogHelper.logOpenFavoriteArticleList();
		}
		moveOut = 0;
	}

	private void scrollToDestination(int destination, boolean smooth) {
		if (smooth)
			this.smoothScrollTo(destination, 0);
		else
			this.scrollTo(destination, 0);
	}

	/**
	 * 监听滑动时的状态
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		current = l;
		// 判断瞬间位移来确定速度
		// if (Math.abs(moveCurrent - current) < 100 || moveCurrent == 0) {
		// moveCurrent = current;
		// }
		checkCurrent(false, true);
		showView();
	}

	/**
	 * 判断当前位置
	 * 
	 * @param scroll
	 *            是否需要滑动
	 */
	private void checkCurrent(boolean scroll, boolean smooth) {
		if (current <= left_max_width) {
			if (current < left_max_width / 2) {
				scrollWidth = 0;
			} else {
				scrollWidth = left_max_width;
			}
		} else {
			if (current < mid_right) {
				scrollWidth = left_max_width;
			} else {
				scrollWidth = right_max_width;
			}
		}
		if (scroll) {
			if (scrollWidth == 0)
				viewSlide(1, smooth);
			else if (scrollWidth == left_max_width)
				viewSlide(0, smooth);
			else
				viewSlide(2, smooth);
		}
	}

	/**
	 * 控制焦点在中间页时的滑屏（如果首先滑动显示左view，又快速往反方向滑，不显示首页而显示右view）
	 */
	private void checkShowView() {
		if (!leftOut && !rightOut) {
			if (current > left_max_width) {
				moveOut = 2;
			} else if (current < left_max_width) {
				moveOut = 1;
			} else {
				moveOut = 0;
			}
		}
	}

	/**
	 * 快速移动
	 */
	private void quicklyMove() {
		if (leftOut) {// 焦点在左页面
			if (moveDir == 1) {
				viewSlide(0, false);
			}
		} else if (rightOut) {// 焦点在右页面
			if (moveDir == 2) {
				viewSlide(0, false);
			}
		} else {
			if (moveOut == 0) {
				if (moveDir == 1) {
					viewSlide(2, false);
				} else if (moveDir == 2) {
					viewSlide(1, false);
				}
			} else if (moveOut == 1) {// 焦点在中间，但移动显示 了左页面
				if (moveDir == 1) {
					viewSlide(0, false);
				} else if (moveDir == 2) {
					viewSlide(1, false);
				}
			} else {// 焦点在中间，但移动显示 了右页面
				if (moveDir == 1) {
					viewSlide(2, false);
				} else if (moveDir == 2) {
					viewSlide(0, false);
				}
			}
		}
	}

	/**
	 * 从父控件往子控件传递
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (gallery != null && gallery instanceof PageGallery) {
			if (((PageGallery) gallery).isScroll()) {
				((PageGallery) gallery).setScroll(false);
				return false;
			}
		}
		float y = ev.getRawY();
		float x = ev.getRawX();
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// TODO 滑动时监听不到action_down
			startX = x;
			angleX = x;
			angleY = y;
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			angle = Math.atan(Math.abs(y - angleY) / Math.abs(x - angleX))
					/ Math.PI * 180;
			angleX = x;
			angleY = y;
			if (angle > MIN_ANGLE) {
				return false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	private long time = 0;
	private float startX;
	private float angleX, angleY;// 比较Y轴滑动角度，如果低于10，则执行横屏滑动
	private double angle;
	private static final double MIN_ANGLE = 30;
	/**
	 * 滑动方向。0.默认，1.左(startX > x)。2.右(startX < x)
	 */
	private int moveDir = 0;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (current == left_max_width && gallery != null) {
			Rect rect = new Rect();
			// 获取该gallery相对于全局的坐标点
			gallery.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY())) {
				return gallery.dispatchTouchEvent(ev);
			}
		}

		// TODO 注册velocityTracker
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(ev);

		float x = ev.getRawX();
		// 当左边页面完全显示并且点击范围在左边页面，那么事件传递给左边页面
		if (current <= left_max_width) {
			if (current == 0 && x < left_max_width) {
				return false;
			}
		} else {
			// 当右边页面完全显示并且点击范围在右边页面，那么事件传递给右边页面
			if (current == right_max_width
					&& x > rightButton.getMeasuredWidth() + ENLARGE_WIDTH) {
				return false;
			}
		}
		// TODO
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			time = System.currentTimeMillis();
			startScrollerTask();
			startX = x;
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (startX < x) {
				moveDir = 2;
			} else if (startX > x) {
				moveDir = 1;
			}
			startX = x;
			checkShowView();
		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			final VelocityTracker tempVelocityTracker = velocityTracker;
			tempVelocityTracker.computeCurrentVelocity(1000);
			boolean sudu = Math.abs((int) tempVelocityTracker.getXVelocity()) > SNAP_VELOCITY;
			if (velocityTracker != null) {
				velocityTracker.recycle();
				velocityTracker = null;
			}
			if (sudu) {
				quicklyMove();
				moveDir = 0;
				return true;
			}

			if (System.currentTimeMillis() - time < MIN_TIME) {// 如果点击速度快，显示首页
				IndexClick();
			} else {
				checkCurrent(true, true);
			}
			time = 0;
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 滑动时判断显示哪边view
	 */
	private void showView() {
		if (current < left_max_width) {
			if (leftView.getVisibility() != View.VISIBLE) {
				leftView.setVisibility(View.VISIBLE);
			}
			if (rightView.getVisibility() != View.GONE) {
				rightView.setVisibility(View.GONE);
			}
		} else if (current > left_max_width) {
			if (leftView.getVisibility() != View.GONE) {
				leftView.setVisibility(View.GONE);
			}
			if (rightView.getVisibility() != View.VISIBLE) {
				rightView.setVisibility(View.VISIBLE);
			}
		} else {
			if (leftView.getVisibility() != View.GONE) {
				leftView.setVisibility(View.GONE);
			}
			if (rightView.getVisibility() != View.GONE) {
				rightView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 初始化水平滑动的最大距离
	 */
	private void initWidgetWidth() {
		if (left_max_width == 0) {
			left_max_width = leftView.getMeasuredWidth()
					- leftButton.getMeasuredWidth() - ENLARGE_WIDTH;
		}
		if (right_max_width == 0) {
			right_max_width = left_max_width + leftView.getMeasuredWidth()
					- rightButton.getWidth() - ENLARGE_WIDTH;
		}
		mid_right = right_max_width - left_max_width / 2;
		leftView.getLayoutParams().width = left_max_width;
		rightView.setPadding(rightButton.getMeasuredWidth() + ENLARGE_WIDTH, 0,
				0, 0);
		scrollWidth = left_max_width;
	}

	/**
	 * scrollView显示首页
	 * 
	 */
	public void IndexClick() {
		viewSlide(0, true);
	}

	/**
	 * 在onCreate函数中，View还未被展开，是无法获得控件的大小，所以设置一个监听，在view显示的时候滑动到相应的位置
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public class MenuOnGlobalLayoutListener implements OnGlobalLayoutListener {
		private ViewGroup parent;
		private View[] children;
		private SizeCallBack leftCallBack;
		private int scrollToViewPos;// 滑动到的位置

		public MenuOnGlobalLayoutListener(ViewGroup parent, View[] children,
				SizeCallBack leftCallBack) {
			this.parent = parent;
			this.children = children;
			this.leftCallBack = leftCallBack;
		}

		@Override
		public void onGlobalLayout() {
			// The listener will remove itself as a layout listener to the
			// HorizontalScrollView
			me.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			// Allow the SizeCallback to 'see' the Views before we remove them
			// and re-add them.
			// This lets the SizeCallback prepare View sizes, ahead of calls to
			// SizeCallback.getViewSize().
			leftCallBack.onGlobalLayout();
			this.parent.removeViewsInLayout(0, children.length);
			int width = me.getMeasuredWidth();
			int height = me.getMeasuredHeight();

			// Add each view in turn, and apply the width and height returned by
			// the SizeCallback.
			int[] dims = new int[2];
			scrollToViewPos = 0;

			for (int i = 0; i < children.length; i++) {
				leftCallBack.getViewSize(i, width, height, dims);
				children[i].setVisibility(View.VISIBLE);

				parent.addView(children[i], dims[0], dims[1]);
				if (i == 0) {
					scrollToViewPos += dims[0];
				}
			}
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					me.scrollBy(scrollToViewPos, 0);

					/* 视图不是中间视图 */
					me.setVisibility(View.VISIBLE);
					// leftView.setVisibility(View.VISIBLE);
				}
			});
			initWidgetWidth();
		}
	}
}
