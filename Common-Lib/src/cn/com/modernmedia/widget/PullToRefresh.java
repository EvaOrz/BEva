package cn.com.modernmedia.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.modernmedia.R;
import cn.com.modernmedia.util.DataHelper;

/**
 * 下拉刷新
 * 
 * @author ZhuQiao
 * 
 */
public class PullToRefresh {
	private final static int RELEASE_To_REFRESH = 0;// 松开刷新
	private final static int PULL_To_REFRESH = 1;// 下拉刷新
	private final static int REFRESHING = 2;// 正在刷新
	private final static int DONE = 3;// 默认状态

	private Context mContext;
	private PullToRefreshListView mListView;
	private int state, toState;
	private View mHeadView, contain;
	private TextView mRefreshText;
	private TextView mUpdateTimeText;
	private ProgressBar mProgressBar;
	private ImageView mRow;

	private int mHeadHeight;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private TranslateAnimation translateAnimation;

	private int startY;
	private boolean isRecored;
	private boolean isBack;// 在RELEASE_TO_LOAD和PULL_TO_LOAD中切换

	public PullToRefresh(Context context, PullToRefreshListView listView) {
		mContext = context;
		mListView = listView;
		init();
	}

	private void init() {
		mHeadView = LayoutInflater.from(mContext).inflate(
				R.layout.pull_to_refresh_header, null);

		contain = mHeadView.findViewById(R.id.pull_head_contain);
		mRow = (ImageView) mHeadView.findViewById(R.id.head_arrowImageView);
		mRow.setMinimumWidth(70);
		mRow.setMinimumHeight(50);
		mProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.head_progressBar);
		mRefreshText = (TextView) mHeadView
				.findViewById(R.id.head_tipsTextView);
		mUpdateTimeText = (TextView) mHeadView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(mHeadView);
		mHeadHeight = mHeadView.getMeasuredHeight();

		mHeadView.setPadding(0, -1 * mHeadHeight, 0, 0);
		mHeadView.invalidate();

		mListView.addHeaderView(mHeadView);

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

		translateAnimation = new TranslateAnimation(0, 0, 0, -mHeadHeight);
		translateAnimation.setInterpolator(new LinearInterpolator());
		translateAnimation.setDuration(300);
		translateAnimation.setFillAfter(true);
		translateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mListView.clearAnimation();
				mHeadView.setPadding(0, -1 * mHeadHeight, 0, 0);
			}
		});

		state = DONE;
		toState = DONE;
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
			if (mListView.getFirstItemIndex() == 0 && !isRecored) {
				isRecored = true;
				startY = (int) event.getY();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (state != REFRESHING) {
				if (state == DONE) {
				}
				if (state == PULL_To_REFRESH) {
					state = DONE;
					changeHeaderViewByState();
				}
				if (state == RELEASE_To_REFRESH) {
					state = REFRESHING;
					changeHeaderViewByState();
					onRefresh();
				}
			}
			isRecored = false;
			isBack = false;
			break;
		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			if (!isRecored && mListView.getFirstItemIndex() == 0) {
				isRecored = true;
				startY = tempY;
			}

			// 防止左右滑动时处理上下移动
			if (state == DONE && Math.abs(startY - tempY) < 10)
				break;

			if (state != REFRESHING && isRecored) {
				// 处理快速滑动刷新问题
				if (mListView.getFirstVisiblePosition() != 0) {
					break;
				}
				// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
				// 可以松手去刷新了
				if (state == RELEASE_To_REFRESH) {
					mListView.setSelection(0);

					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((tempY - startY) / mListView.RATIO < mHeadHeight)
							&& (tempY - startY) > 0) {
						state = PULL_To_REFRESH;
						changeHeaderViewByState();

					}
					// 一下子推到顶了
					else if (tempY - startY <= 0) {
						state = DONE;
						changeHeaderViewByState();

					}
					// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
					else {
						// 不用进行特别的操作，只用更新paddingTop的值就行了
					}
				}
				// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
				if (state == PULL_To_REFRESH) {
					mListView.setSelection(0);

					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((tempY - startY) / mListView.RATIO >= mHeadHeight) {
						state = RELEASE_To_REFRESH;
						isBack = true;
						changeHeaderViewByState();

					}
					// 上推到顶了
					else if (tempY - startY <= 0) {
						state = DONE;
						changeHeaderViewByState();

					}
				}

				// done状态下
				if (state == DONE) {
					if (tempY - startY > 0) {
						state = PULL_To_REFRESH;
						changeHeaderViewByState();
					}
				}

				// 更新headView的paddingTop
				if (state == RELEASE_To_REFRESH || state == PULL_To_REFRESH) {
					mHeadView.setPadding(0, (tempY - startY) / mListView.RATIO
							- mHeadHeight, 0, 0);
				}

			}
			break;
		}
	}

	private void changeHeaderViewByState() {
		changeHeaderViewByState(false);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState(boolean smoothScroll) {
		switch (state) {
		case RELEASE_To_REFRESH:
			mRow.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			mRefreshText.setVisibility(View.VISIBLE);

			mRow.clearAnimation();
			mRow.startAnimation(animation);

			mRefreshText.setText(R.string.pull_to_refresh_release_label);
			onPulling();
			break;
		case PULL_To_REFRESH:
			mProgressBar.setVisibility(View.GONE);
			mRefreshText.setVisibility(View.VISIBLE);
			mRow.clearAnimation();
			mRow.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				mRow.clearAnimation();
				mRow.startAnimation(reverseAnimation);
			}
			mRefreshText.setText(R.string.pull_to_refresh_pull_label);
			onPulling();
			break;
		case REFRESHING:
			mHeadView.setPadding(0, 0, 0, 0);

			mProgressBar.setVisibility(View.VISIBLE);
			mRow.clearAnimation();
			mRow.setVisibility(View.GONE);
			mRefreshText.setText(R.string.pull_to_refresh_refreshing_label);
			toState = REFRESHING;
			break;
		case DONE:
			if (smoothScroll) {
				// mListView.clearAnimation();
				mListView.startAnimation(translateAnimation);
			} else
				mHeadView.setPadding(0, -1 * mHeadHeight, 0, 0);

			mProgressBar.setVisibility(View.GONE);
			mRow.clearAnimation();
			// arrowImageView.setImageResource(R.drawable.refresh_down);
			mRefreshText.setText(R.string.pull_to_refresh_pull_label);
			toState = DONE;
			if (mListView.getRefreshListener() != null) {
				mListView.getRefreshListener().onRefreshDone();
			}
			break;
		}
	}

	private void onRefresh() {
		if (mListView.getRefreshListener() != null) {
			mListView.getRefreshListener().onRefresh();
		}
	}

	private void onPulling() {
		if (state == toState)
			return;
		toState = state;
		if (mListView.getRefreshListener() != null) {
			mListView.getRefreshListener().onPulling();
		}
	}

	public void onRefreshComplete(boolean smooth) {
		state = DONE;
		changeHeaderViewByState(smooth);
		// TODO show success status toast
	}

	/**
	 * 当viewpager切换屏幕但headview的状态
	 */
	public void reStoreStatus() {
		if (state != REFRESHING) {
			onRefreshComplete(false);
		}
	}

	public boolean isStopMove() {
		return state == DONE || state == REFRESHING;
	}

	public void setUpdateTime(int catId) {
		String time = "";
		time = DataHelper.getIndexUpdateTime(mContext, catId);
		if (!TextUtils.isEmpty(time))
			mUpdateTimeText.setText(String.format(
					mContext.getString(R.string.pull_to_refresh_update_time),
					time));
		else
			mUpdateTimeText.setText("");
	}

	/**
	 * 是否显示时间
	 * 
	 * @param show
	 */
	public void showTime(boolean show) {
		mUpdateTimeText.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	/**
	 * 设置背景
	 * 
	 * @param res
	 */
	public void setContainBg(int res) {
		contain.setBackgroundColor(res);
	}
}
