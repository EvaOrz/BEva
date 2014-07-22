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
import android.widget.HorizontalScrollView;
import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.ScrollStateListener;
import cn.com.modernmedia.listener.SizeCallBack;
import cn.com.modernmedia.util.LogHelper;

/**
 * ��ҳ������
 * 
 * @author ZhuQiao
 * 
 */
public class MainHorizontalScrollView extends HorizontalScrollView {
	private MainHorizontalScrollView me;
	private Context mContext;
	private View leftView, rightView;// ��Ļ���view
	private boolean leftOut, rightOut;// �Ƿ���ʾ������ߵ�view
	/**
	 * �ƶ�����ʾ��view;0.�ޡ�1.��2.��
	 */
	private int moveOut = 0;
	public static int ENLARGE_WIDTH = 0;// ��չ���
	private int scrollWidth;// ��Ҫ�����Ŀ��
	private Button leftButton, rightButton;// �л������view��button
	private int current;// ��ǰ������λ��
	private int left_max_width, right_max_width, mid_right;// ��ʾ�����󻬶�����
	private View needsScrollView;// ��Ҫ�Լ�����������view
	private AtlasViewPager atlasViewPager;// ��Ҫ�Լ�������view(��2ҳ������2ҳ)
	private ScrollCallBackListener listener;
	private static final long MIN_TIME = 500;
	private static final int CHECK_TIME = 100;
	private int lastPosition;
	private Runnable scrollTask;
	private VelocityTracker velocityTracker;// �жϻ�������
	private static final int SNAP_VELOCITY = 2000;// ��ͻ�������
	private FecthViewSizeListener viewListener;

	/**
	 * ��չ��������view�Ŀ��
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
		// �����ޱ�Եɫ
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
	 * ��ʼ����ҳ��Ϣ
	 * 
	 * @param children
	 *            �������������������������ɼ���Ϊ�˻�����������ʾ��Ŀ�б���ղ�
	 * @param sizeCallBack
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
	 * ���ð�ť
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

	public void setNeedsScrollView(View needsScrollView) {
		this.needsScrollView = needsScrollView;
	}

	public void setAtlasViewPager(AtlasViewPager atlasViewPager) {
		this.atlasViewPager = atlasViewPager;
	}

	public void setViewListener(FecthViewSizeListener viewListener) {
		this.viewListener = viewListener;
	}

	/**
	 * ��ҳtitlebar�ϰ�ť���
	 * 
	 * @param left
	 */
	public void clickButton(boolean left) {
		if (left) {
			// TODO �����smooth�����а�ɫ��Ӱ?!
			viewSlide(leftOut ? 0 : 1, false);
		} else {
			viewSlide(rightOut ? 0 : 2, false);
		}
	}

	/**
	 * ������view
	 * 
	 * @param flag
	 *            0:��ʾindex;1:��ʾleft;2:��ʾright
	 */
	private void viewSlide(int flag, boolean smooth) {
		if (flag == 0) {
			leftOut = false;
			rightOut = false;
			scrollWidth = left_max_width;
			scrollToDestination(left_max_width, smooth);
			if (listener != null)
				listener.isShowIndex(true);
		} else if (flag == 1) {
			leftOut = true;
			rightOut = false;
			scrollWidth = 0;
			scrollToDestination(0, smooth);
			if (listener != null)
				listener.isShowIndex(false);
			LogHelper.logOpenColumnList(mContext);
		} else {
			leftOut = false;
			rightOut = true;
			scrollWidth = right_max_width;
			scrollToDestination(right_max_width, smooth);
			if (listener != null)
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
	 * ��������ʱ��״̬
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		current = l;
		checkCurrent(false, true);
		showView();
	}

	/**
	 * �жϵ�ǰλ��
	 * 
	 * @param scroll
	 *            �Ƿ���Ҫ����
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
	 * ���ƽ������м�ҳʱ�Ļ�����������Ȼ�����ʾ��view���ֿ����������򻬣�����ʾ��ҳ����ʾ��view��
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
	 * �����ƶ�
	 */
	private void quicklyMove() {
		if (leftOut) {// ��������ҳ��
			if (moveDir == 1) {
				viewSlide(0, false);
			}
		} else if (rightOut) {// ��������ҳ��
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
			} else if (moveOut == 1) {// �������м䣬���ƶ���ʾ ����ҳ��
				if (moveDir == 1) {
					viewSlide(0, false);
				} else if (moveDir == 2) {
					viewSlide(1, false);
				}
			} else {// �������м䣬���ƶ���ʾ ����ҳ��
				if (moveDir == 1) {
					viewSlide(2, false);
				} else if (moveDir == 2) {
					viewSlide(0, false);
				}
			}
		}
	}

	/**
	 * �Ӹ��ؼ����ӿؼ�����
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (needsScrollView != null) {
			Rect rect = new Rect();
			// ��ȡ��gallery�����ȫ�ֵ������
			needsScrollView.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY())) {
				return false;
			}
		}
		if (atlasViewPager != null) {
			Rect rect = new Rect();
			// ��ȡ��gallery�����ȫ�ֵ������
			atlasViewPager.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY())) {
				if (!lockScroll(atlasViewPager, ev))
					return false;
			}
		}
		if (adjustAngle(ev)) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * �����ĽǶ��Ƿ������Сֵ
	 * 
	 * @param ev
	 * @return
	 */
	private boolean adjustAngle(MotionEvent ev) {
		float y = ev.getRawY();
		float x = ev.getRawX();
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// TODO ����ʱ��������action_down
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
	private float angleX, angleY;// �Ƚ�Y�Ử���Ƕȣ��������10����ִ�к�������
	private double angle;
	private static final double MIN_ANGLE = 30;
	/**
	 * ��������0.Ĭ�ϣ�1.��(startX > x)��2.��(startX < x)
	 */
	private int moveDir = 0;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (current == left_max_width) {
		}

		// TODO ע��velocityTracker
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(ev);

		float x = ev.getRawX();
		// �����ҳ����ȫ��ʾ���ҵ����Χ�����ҳ�棬��ô�¼����ݸ����ҳ��
		if (current <= left_max_width) {
			if (current == 0 && x < left_max_width) {
				return false;
			}
		} else {
			// ���ұ�ҳ����ȫ��ʾ���ҵ����Χ���ұ�ҳ�棬��ô�¼����ݸ��ұ�ҳ��
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

			if (System.currentTimeMillis() - time < MIN_TIME) {// �������ٶȿ죬��ʾ��ҳ
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
	 * ����ʱ�ж���ʾ�ı�view
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
	 * ��ʼ��ˮƽ������������
	 */
	private void initWidgetWidth() {
		if (left_max_width == 0) {
			left_max_width = leftView.getMeasuredWidth()
					- leftButton.getMeasuredWidth() - ENLARGE_WIDTH;
		}
		if (right_max_width == 0) {
			if (rightButton.getVisibility() == View.VISIBLE)
				right_max_width = left_max_width + leftView.getMeasuredWidth()
						- rightButton.getMeasuredWidth() - ENLARGE_WIDTH;
			else
				// iweekly�ұ߰�ť���������ص�
				right_max_width = left_max_width + leftView.getMeasuredWidth()
						- leftButton.getMeasuredWidth() - ENLARGE_WIDTH;
		}
		mid_right = right_max_width - left_max_width / 2;
		leftView.getLayoutParams().width = left_max_width;
		if (rightView != null)
			if (rightButton.getVisibility() == View.VISIBLE)
				rightView.setPadding(rightButton.getMeasuredWidth()
						+ ENLARGE_WIDTH, 0, 0, 0);
			else
				// iweekly�ұ߰�ť���������ص�
				rightView.setPadding(leftButton.getMeasuredWidth()
						+ ENLARGE_WIDTH, 0, 0, 0);
		scrollWidth = left_max_width;
		if (viewListener != null)
			viewListener.fetchViewWidth(left_max_width);
	}

	/**
	 * scrollView��ʾ��ҳ
	 * 
	 */
	public void IndexClick() {
		viewSlide(0, true);
	}

	/**
	 * ��onCreate�����У�View��δ��չ�������޷���ÿؼ��Ĵ�С����������һ����������view��ʾ��ʱ�򻬶�����Ӧ��λ��
	 * 
	 * @author ZhuQiao
	 * 
	 */
	public class MenuOnGlobalLayoutListener implements OnGlobalLayoutListener {
		private ViewGroup parent;
		private View[] children;
		private SizeCallBack leftCallBack;
		private int scrollToViewPos;// ��������λ��

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
				if (children[i].getVisibility() != View.GONE)
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

					/* ��ͼ�����м���ͼ */
					me.setVisibility(View.VISIBLE);
					// leftView.setVisibility(View.VISIBLE);
				}
			});
			initWidgetWidth();
		}
	}
}
