package cn.com.modernmedia.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.modernmediaslate.R;

/**
 * 上推加载
 * 
 * @author ZhuQiao
 * 
 */
public class PullToLoadMore {
	private static final int RELEASE_TO_LOAD = 0;// 可以松开加载
	private static final int PULL_TO_LOAD = 1;// 可以上拉加载
	private static final int LOADING = 2;// 正在加载
	private static final int DONE = 3;// 默认状态

	private Context mContext;
	private PullToRefreshListView mListView;
	private int state;
	private View mFooterView;
	private TextView mLoadText;
	private ProgressBar mProgressBar;
	private ImageView mRow;

	private int mFooterHeight;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	private boolean isRecored;
	private boolean isBack;// 在RELEASE_TO_LOAD和PULL_TO_LOAD中切换

	public PullToLoadMore(Context context, PullToRefreshListView listView) {
		mContext = context;
		mListView = listView;
		init();
	}

	private void init() {
		mFooterView = LayoutInflater.from(mContext).inflate(
				R.layout.pull_to_load_footer, null);
		mLoadText = (TextView) mFooterView.findViewById(R.id.footer_text);
		mProgressBar = (ProgressBar) mFooterView
				.findViewById(R.id.footer_progressBar);
		mRow = (ImageView) mFooterView.findViewById(R.id.footer_arrowImageView);
		mRow.setMinimumWidth(70);
		mRow.setMinimumHeight(50);

		measureView(mFooterView);
		mFooterHeight = mFooterView.getMeasuredHeight();

		mFooterView.setPadding(0, 0, 0, -mFooterHeight);
		mFooterView.invalidate();

		mListView.addFooterView(mFooterView);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(150);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(150);
		reverseAnimation.setFillAfter(true);

		state = DONE;
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setValue() {
	}

	public void onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isInFooter() && !isRecored) {
				isRecored = true;
				startY = (int) event.getY();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			if (!isRecored && isInFooter()) {
				isRecored = true;
				startY = tempY;
			}

			// 防止左右滑动时处理上下移动
			if (state == DONE && Math.abs(startY - tempY) < 10)
				break;

			if (state != LOADING && isRecored) {
				// 处理快速滑动刷新问题
				if (isQuicklyScroll()) {
					break;
				}
				// 可以松手去加载了
				if (state == RELEASE_TO_LOAD) {
					mListView.setSelection(mListView.getLastVisiblePosition());

					// 往下拉了，拉到足够掩盖footer的程蝶衣，但是还没有拉到全部掩盖的地步
					if (((startY - tempY) / mListView.RATIO < mFooterHeight)
							&& (startY - tempY) > 0) {
						state = PULL_TO_LOAD;
						changeFooterViewByState();
					} else if (startY - tempY <= 0) {
						// 往下拉的时候完全覆盖住footerview
						state = DONE;
						changeFooterViewByState();
					}
				}

				// 还没有到达显示松开加载的时候
				if (state == PULL_TO_LOAD) {
					mListView.setSelection(mListView.getLastVisiblePosition());

					// 上推到可以进入RELEASE_TO_LOAD的状态
					if ((startY - tempY) / mListView.RATIO >= mFooterHeight) {
						state = RELEASE_TO_LOAD;
						isBack = true;
						changeFooterViewByState();
					} else if (startY - tempY <= 0) {
						// 往下拉的时候完全覆盖住footerview
						state = DONE;
						changeFooterViewByState();
					}
				}

				// done状态下
				if (state == DONE) {
					if (startY - tempY > 0) {
						state = PULL_TO_LOAD;
						changeFooterViewByState();
					}
				}

				// 更新footerView的size
				if (state == PULL_TO_LOAD || state == RELEASE_TO_LOAD) {
					mFooterView.setPadding(0, 0, 0, -1 * mFooterHeight
							+ (startY - tempY) / mListView.RATIO);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (state != LOADING) {
				if (state == PULL_TO_LOAD) {
					state = DONE;
					changeFooterViewByState();
				}
				if (state == RELEASE_TO_LOAD) {
					state = LOADING;
					changeFooterViewByState();
					onLoadMore();
				}
			}
			isRecored = false;
			isBack = false;
			break;
		}
	}

	/**
	 * 当状态改变时候，调用该方法，以更新界面
	 */
	private void changeFooterViewByState() {
		switch (state) {
		case RELEASE_TO_LOAD:
			mProgressBar.setVisibility(View.GONE);
			mRow.setVisibility(View.VISIBLE);

			mRow.clearAnimation();
			mRow.startAnimation(animation);

			mLoadText.setText(R.string.pull_to_release);
			break;
		case PULL_TO_LOAD:
			mProgressBar.setVisibility(View.GONE);
			mRow.setVisibility(View.VISIBLE);

			// 是由RELEASE_To_LOAD状态转变来的
			if (isBack) {
				isBack = false;
				mRow.clearAnimation();
				mRow.startAnimation(reverseAnimation);
			}
			mLoadText.setText(R.string.pull_to_loadmore);
			break;
		case LOADING:
			mFooterView.setPadding(0, 0, 0, 0);
			mProgressBar.setVisibility(View.VISIBLE);
			mRow.clearAnimation();
			mRow.setVisibility(View.GONE);

			mLoadText.setText(R.string.pull_to_loading);
			break;
		case DONE:
			mFooterView.setPadding(0, 0, 0, -mFooterHeight);
			mProgressBar.setVisibility(View.GONE);
			mRow.clearAnimation();

			mLoadText.setText(R.string.pull_to_loadmore);

			break;
		default:
			break;
		}
	}

	private void onLoadMore() {
		if (mListView.getRefreshListener() != null) {
			mListView.getRefreshListener().onLoad();
		}
	}

	/**
	 * 最后一个可见的item是否是最后一个footerview
	 * 
	 * @return
	 */
	public boolean isInFooter() {
		return mListView.getLastVisiblePosition() == getTotalCount() - 1;
	}

	private boolean isQuicklyScroll() {
		return mListView.getLastVisiblePosition() != getTotalCount() - 1;
	}

	private int getTotalCount() {
		// int itemCount = mListView.getAdapter() == null ? 0 : mListView
		// .getAdapter().getCount();
		return mListView.getHeaderViewsCount() + mListView.getItemCount()
				+ mListView.getFooterViewsCount();
	}

	public void onRefreshComplete() {
		state = DONE;
		changeFooterViewByState();
		// TODO show success status toast
	}

	/**
	 * 当viewpager切换屏幕但headview的状态
	 */
	public void reStoreStatus() {
		if (state != LOADING) {
			onRefreshComplete();
		}
	}
}
