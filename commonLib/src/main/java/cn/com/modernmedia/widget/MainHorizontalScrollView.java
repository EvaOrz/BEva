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
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.ScrollStateListener;
import cn.com.modernmedia.listener.SizeCallBack;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmediaslate.unit.ParseUtil;

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
	public static int LEFT_ENLARGE_WIDTH = 0;// 扩展宽度
	public static int RIGHT_ENLARGE_WIDTH = 0;// 扩展宽度
	private int scrollWidth;// 需要滑动的宽度
	private View leftButton, rightButton;// 切换至左边view的button
	private int current;// 当前滑动的位置
	private int left_max_width, right_max_width, mid_right;// 显示左边最大滑动距离
	private List<View> needsScrollViewList = new ArrayList<View>();// 需要自己单独滑动的view(可能有多个)
	private boolean passToUp = false;// 传递给上层页面执行touch事件
	private ScrollCallBackListener listener;
	private static final long MIN_TIME = 500;
	private static final int CHECK_TIME = 100;
	private int lastPosition;
	private Runnable scrollTask;
	private VelocityTracker velocityTracker;// 判断滑动速率
	private static final int SNAP_VELOCITY = 2000;// 最低滑动速率
	private FecthViewSizeListener viewListener;
	private boolean intercept;// 拦截掉滑动

	/**
	 * 扩展过后左右view的宽度
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public interface FecthViewSizeListener {
		public void fetchViewWidth(int width);
	}

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
		clearNeedsScrollView();
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
	 * @param leftCallback
	 * @param leftView
	 */
	public void initViews(View[] children, SizeCallBack leftCallback,
			View leftView, View rightView) {
		this.leftView = leftView;
		this.rightView = rightView;
		ViewGroup parent = (ViewGroup) getChildAt(0);

		for (int i = 0; i < children.length; i++) {
			if (children[i].getVisibility() != View.GONE)
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
	public void setButtons(View leftButton, View rightButton) {
		this.leftButton = leftButton;
		this.rightButton = rightButton;
	}

	public void setListener(ScrollCallBackListener listener) {
		this.listener = listener;
	}

	public void addNeedsScrollView(View needsScrollView) {
		if (!needsScrollViewList.contains(needsScrollView))
			needsScrollViewList.add(needsScrollView);
	}

	public void clearNeedsScrollView() {
		needsScrollViewList.clear();
	}

	public void setPassToUp(boolean passToUp) {
		this.passToUp = passToUp;
	}

	public void setViewListener(FecthViewSizeListener viewListener) {
		this.viewListener = viewListener;
	}

	public void setIntercept(boolean intercept) {
		this.intercept = intercept;
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
		} else if (flag == 1) {
			leftOut = true;
			rightOut = false;
			scrollWidth = 0;
			scrollToDestination(0, smooth);
			LogHelper.logOpenColumnList(mContext);
			if (intercept && leftView != null
					&& leftView.getVisibility() != View.VISIBLE) {
				leftView.setVisibility(View.VISIBLE);
			}
			if (intercept && rightView != null
					&& rightView.getVisibility() != View.GONE) {
				rightView.setVisibility(View.GONE);
			}
		} else {
			leftOut = false;
			rightOut = true;
			scrollWidth = right_max_width;
			scrollToDestination(right_max_width, smooth);
			LogHelper.logOpenFavoriteArticleList(mContext);
			if (intercept && rightView != null
					&& rightView.getVisibility() != View.VISIBLE) {
				rightView.setVisibility(View.VISIBLE);
			}
			if (intercept && leftView != null
					&& leftView.getVisibility() != View.GONE) {
				leftView.setVisibility(View.GONE);
			}
		}
		if (listener != null)
			listener.showIndex(flag);
		moveOut = 0;
	}

	boolean isClick;

	private void scrollToDestination(int destination, boolean smooth) {
		isClick = true;
		if (smooth)
			this.smoothScrollTo(destination, 0);
		else
			this.scrollTo(destination, 0);
	}

	@Override
	public void scrollTo(int x, int y) {
		if (intercept) {
			if (isClick) {
				super.scrollTo(x, y);
				isClick = false;
			}
		} else {
			super.scrollTo(x, y);
		}
	}

	/**
	 * 监听滑动时的状态
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (intercept) {
			return;
		}
		super.onScrollChanged(l, t, oldl, oldt);
		current = l;
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
		} else {
			if (ConstData.getInitialAppId() == 102) {
				// 灵感有点问题
				if (current == 0) {
					viewSlide(0, false);
				}
			}
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
		if (intercept)
			return false;
		if (passToUp)
			return false;
		if (checkChilds(ev))
			return false;
		if (adjustAngle(ev)) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private boolean checkChilds(MotionEvent ev) {
		boolean inChild = false;
		if (!ParseUtil.listNotNull(needsScrollViewList))
			return inChild;
		for (View child : needsScrollViewList) {
			if (child == null)
				continue;
			if (child instanceof AtlasViewPager) {
				inChild = checkAtlasPager(ev, (AtlasViewPager) child);
			} else {
				inChild = checkNormalChild(ev, child);
			}
			if (inChild)
				break;
		}
		return inChild;
	}

	/**
	 * 判断普通child(只自己滑动)
	 * 
	 * @param ev
	 * @param child
	 * @return
	 */
	private boolean checkNormalChild(MotionEvent ev, View child) {
		Rect rect = new Rect();
		child.getGlobalVisibleRect(rect);
		if (rect.contains((int) ev.getX(), (int) ev.getY())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断文章图集样式的child(第2页至最后第2页)
	 * 
	 * @param ev
	 * @param child
	 * @return
	 */
	private boolean checkAtlasPager(MotionEvent ev, AtlasViewPager child) {
		Rect rect = new Rect();
		// 获取该gallery相对于全局的坐标点
		child.getGlobalVisibleRect(rect);
		if (rect.contains((int) ev.getX(), (int) ev.getY())) {
			return !lockScroll(child, ev);
		}
		return false;
	}

	/**
	 * 滑动的角度是否大于最小值
	 * 
	 * @param ev
	 * @return
	 */
	private boolean adjustAngle(MotionEvent ev) {
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
				return true;
			}
		}
		return false;
	}

	private float lastX;

	private boolean lockScroll(AtlasViewPager pager, MotionEvent event) {
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
		return true;
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
		if (intercept)
			return false;
		if (current == left_max_width) {
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
					&& x > rightButton.getMeasuredWidth() + RIGHT_ENLARGE_WIDTH) {
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
		try {
			return super.onTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 滑动时判断显示哪边view
	 */
	private void showView() {
		if (current < left_max_width) {
			if (leftView.getVisibility() != View.VISIBLE) {
				leftView.setVisibility(View.VISIBLE);
			}
			if (rightView != null && rightView.getVisibility() != View.GONE) {
				rightView.setVisibility(View.GONE);
			}
		} else if (current > left_max_width) {
			if (leftView.getVisibility() != View.GONE) {
				leftView.setVisibility(View.GONE);
			}
			if (rightView != null && rightView.getVisibility() != View.VISIBLE) {
				rightView.setVisibility(View.VISIBLE);
			}
		} else {
			if (leftView.getVisibility() != View.GONE) {
				leftView.setVisibility(View.GONE);
			}
			if (rightView != null && rightView.getVisibility() != View.GONE) {
				rightView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 初始化水平滑动的最大距离
	 */
	private void initWidgetWidth() {
		if (leftView.getVisibility() == View.GONE) {
			left_max_width = 0;
			scrollWidth = rightView.getMeasuredWidth()
					- rightButton.getMeasuredWidth() - LEFT_ENLARGE_WIDTH;
		} else if (left_max_width == 0) {
			scrollWidth = leftView.getMeasuredWidth()
					- leftButton.getMeasuredWidth() - LEFT_ENLARGE_WIDTH;
			left_max_width = scrollWidth;
		}
		if (right_max_width == 0) {
			if (rightButton.getVisibility() == View.VISIBLE)
				right_max_width = left_max_width + rightView.getMeasuredWidth()
						- rightButton.getMeasuredWidth() - RIGHT_ENLARGE_WIDTH;
			else
				// iweekly右边按钮可能是隐藏的
				right_max_width = left_max_width + leftView.getMeasuredWidth()
						- leftButton.getMeasuredWidth() - RIGHT_ENLARGE_WIDTH;
		}
		mid_right = right_max_width - scrollWidth / 2;
		leftView.getLayoutParams().width = scrollWidth;
		if (rightView != null)
			if (rightButton.getVisibility() == View.VISIBLE)
				rightView.setPadding(rightButton.getMeasuredWidth()
						+ RIGHT_ENLARGE_WIDTH, 0, 0, 0);
			else
				// iweekly右边按钮可能是隐藏的
				rightView.setPadding(leftButton.getMeasuredWidth()
						+ RIGHT_ENLARGE_WIDTH, 0, 0, 0);
		if (viewListener != null)
			viewListener.fetchViewWidth(scrollWidth);
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
			this.parent.removeViewsInLayout(0, children.length);
			int width = me.getMeasuredWidth();
			int height = me.getMeasuredHeight();

			// Add each view in turn, and apply the width and height returned by
			// the SizeCallback.
			int[] dims = new int[2];
			scrollToViewPos = 0;

			for (int i = 0; i < children.length; i++) {
				View child = children[i];
				if (child.getTag() == null)
					leftCallBack.onGlobalLayout(0);
				else
					leftCallBack.onGlobalLayout(ParseUtil.stoi(children[i]
							.getTag().toString()));
				leftCallBack.getViewSize(i, width, height, dims);
				if (children[i].getVisibility() != View.GONE) {
					children[i].setVisibility(View.VISIBLE);
					parent.addView(children[i], dims[0], dims[1]);
				}
				if (i == 0) {
					scrollToViewPos += dims[0];
				}
			}

			if (children[0].getVisibility() == View.GONE) {
				// 没有左边的
				scrollToViewPos = 0;
			}

			new Handler().post(new Runnable() {
				@Override
				public void run() {
					isClick = true;
					me.scrollTo(scrollToViewPos, 0);

					/* 视图不是中间视图 */
					me.setVisibility(View.VISIBLE);
					// leftView.setVisibility(View.VISIBLE);
				}
			});
			initWidgetWidth();
		}
	}
}
