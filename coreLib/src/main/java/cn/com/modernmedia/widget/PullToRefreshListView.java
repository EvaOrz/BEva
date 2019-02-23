package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 下拉刷新listview
 * 
 * @author ZhuQiao
 * 
 */
public class PullToRefreshListView extends CheckScrollListview implements
		OnScrollListener {
	// 实际的padding的距离与界面上偏移距离的比例
	public final int RATIO = 2;

	private Context mContext;

	private int startY;
	private int firstItemIndex;

	private OnRefreshListener refreshListener;
	private PullToLoadMore mPullToLoadMore;
	private PullToRefresh mPullToRefresh;
	private boolean mScrollFooter;// 是否是执行footer
	private int itemCount;
	private boolean showPull = true;

	public PullToRefreshListView(Context context) {
		this(context, null);
		init(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		super.setOnScrollListener(this);
	}

	/**
	 * 是否开启下拉刷新和上拉加载
	 * 
	 * @param enableRefresh
	 *            是否开始下拉刷新
	 * @param enableLoad
	 *            是否开启上拉加载
	 */
	public void enableAutoFetch(boolean enableRefresh, boolean enableLoad) {
		if (enableRefresh) {
			mPullToRefresh = new PullToRefresh(mContext, this);
		}
		if (enableLoad) {
			mPullToLoadMore = new PullToLoadMore(mContext, this);
		}
	}

	public void setCatId(int catId) {
		if (mPullToRefresh != null)
			mPullToRefresh.setUpdateTime(catId);
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		firstItemIndex = firstVisibleItem;
		super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		super.onScrollStateChanged(view, scrollState);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (adjustAngle(event)) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mScrollFooter = false;
			startY = (int) event.getY();

			if (mPullToRefresh != null)
				mPullToRefresh.onTouchEvent(event);
			if (mPullToLoadMore != null)
				mPullToLoadMore.onTouchEvent(event);
			break;
		case MotionEvent.ACTION_UP:
			if (mScrollFooter && mPullToLoadMore != null) {
				mScrollFooter = false;
				mPullToLoadMore.onTouchEvent(event);
			}

			if (mPullToRefresh != null) {
				mPullToRefresh.onTouchEvent(event);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();

			if (mPullToLoadMore != null) {
				// 列表长度不满整个屏幕，既能看见firstitem,又能看见lastitem
				if (mPullToLoadMore.isInFooter() && firstItemIndex == 0) {
					if (startY > tempY
							&& (mPullToRefresh != null && mPullToRefresh
									.isStopMove())) {
						callFooterMove(event);
						break;
					}
				} else if (mPullToLoadMore.isInFooter()) {
					callFooterMove(event);
					break;
				}
			}
			callHeadMove(event);
			break;
		}
		return super.onTouchEvent(event);
	}

	private float angleX, angleY;// 比较Y轴滑动角度，如果低于10，则执行横屏滑动
	private double angle;
	private static final double MIN_ANGLE = 30;

	/**
	 * 滑动的角度是否小于最小值
	 * 
	 * @param ev
	 * @return true:不执行pull操作
	 */
	private boolean adjustAngle(MotionEvent ev) {
		float y = ev.getRawY();
		float x = ev.getRawX();
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			angleX = x;
			angleY = y;
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			angle = Math.atan(Math.abs(y - angleY) / Math.abs(x - angleX))
					/ Math.PI * 180;
			angleX = x;
			angleY = y;
			if (angle < MIN_ANGLE) {
				return true;
			}
		}
		return false;
	}

	private void callFooterMove(MotionEvent event) {
		if (mPullToLoadMore != null) {
			mScrollFooter = true;
			mPullToLoadMore.setValue();
			mPullToLoadMore.onTouchEvent(event);
		}
		if (itemCount == 0)
			if (mPullToRefresh != null && firstItemIndex == 0) {
				mPullToRefresh.reStoreStatus();
			}
	}

	private void callHeadMove(MotionEvent event) {
		if (mPullToRefresh != null) {
			mPullToRefresh.setValue();
			mPullToRefresh.onTouchEvent(event);
		}
		if (itemCount == 0)
			if (mPullToLoadMore != null && mPullToLoadMore.isInFooter()) {
				mPullToLoadMore.reStoreStatus();
			}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public OnRefreshListener getRefreshListener() {
		return refreshListener;
	}

	public interface OnRefreshListener {
		/**
		 * 执行下拉刷新
		 */
		public void onRefresh();

		/**
		 * 执行上推加载
		 */
		public void onLoad();

		/**
		 * 正在下拉刷新(status != done && status != refreshing)
		 */
		public void onPulling();

		/**
		 * 刷新完成
		 */
		public void onRefreshComplete();

		/**
		 * 状态回到done
		 */
		public void onRefreshDone();
	}

	public void onRefreshComplete(boolean shouldUpdateRefreshTime, int catId) {
		onRefreshComplete(shouldUpdateRefreshTime, catId, false);
	}

	/**
	 * 更新数据完成
	 * 
	 * @param shouldUpdateRefreshTime
	 *            是否需要更新刷新时间
	 * @param catId
	 *            需要更新时间的catid
	 * @param smoothscroll
	 *            是否smoothscroll
	 */
	public void onRefreshComplete(boolean shouldUpdateRefreshTime, int catId,
			boolean smoothscroll) {
		if (mPullToRefresh != null) {
			mPullToRefresh.onRefreshComplete(smoothscroll);
			if (refreshListener != null)
				refreshListener.onRefreshComplete();
			// if (shouldUpdateRefreshTime) {
			// DataHelper.setIndexUpdateTime(mContext,
			// ModernMediaTools.fetchTime(), catId);
			// mPullToRefresh.setUpdateTime(catId);
			// }
		}
	}

	public void onLoadComplete() {
		if (mPullToLoadMore != null)
			mPullToLoadMore.onRefreshComplete();
	}

	/**
	 * 当viewpager切换屏幕但headview的状态
	 */
	public void reStoreStatus() {
		if (mPullToRefresh != null)
			mPullToRefresh.reStoreStatus();
		if (mPullToLoadMore != null)
			mPullToLoadMore.reStoreStatus();
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public int getFirstItemIndex() {
		return firstItemIndex;
	}

	public void setHeadRes(boolean showTime, int color) {
		if (mPullToRefresh != null) {
			mPullToRefresh.showTime(showTime);
			mPullToRefresh.setContainBg(color);
		}
	}

	public boolean isHeadShow() {
		if (mPullToRefresh != null) {
			return mPullToRefresh.isHeadShow();
		}
		return false;
	}

	public boolean isShowPull() {
		return showPull;
	}

	public void setShowPull(boolean showPull) {
		this.showPull = showPull;
	}

}
