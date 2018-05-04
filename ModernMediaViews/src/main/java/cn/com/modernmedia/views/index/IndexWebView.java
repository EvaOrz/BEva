package cn.com.modernmedia.views.index;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebSettings;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.modernmedia.listener.WebProcessListener;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.CommonWebView;
import cn.com.modernmedia.widget.CommonWebView.WebViewLoadListener;
import cn.com.modernmediaslate.unit.Tools;

/**
 * 栏目内容是网页形式
 * 
 * @author jiancong
 * 
 */
public class IndexWebView extends BaseView {
	private View mHeadView;
	private CommonWebView mWebView;
	private TextView mRefreshText;
	private ProgressBar mProgressBar;
	private int headHeight = 0;

	public IndexWebView(Context context) {
		this(context, null);
	}

	public IndexWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.index_web_view, this);
		init();
	}

	private void init() {
		mHeadView = findViewById(R.id.index_web_refresh_layout);
		mRefreshText = (TextView) mHeadView
				.findViewById(R.id.head_tipsTextView);
		mProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.head_progressBar);
		mWebView = (CommonWebView) findViewById(R.id.index_web_webview);
		mWebView.setShowColumn(true);
		setCacheMode();
		mWebView.setLoadListener(new WebViewLoadListener() {

			@Override
			public void loadComplete(boolean success) {
				if (mHeadView != null) {
					mProgressBar.setVisibility(View.GONE);
					mHeadView.setPadding(0, -1 * headHeight, 0, 0);
				}
			}
		});
	}

	private void setCacheMode() {
		if (Tools.checkNetWork(getContext())) {
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			mWebView.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
	}

	/**
	 * 设置要加载网页的url
	 * 
	 * @param url
	 */
	public void setData(String url) {
		mWebView.loadUrl(url);
	}

	/**
	 * 返回事件处理
	 * 
	 * @return
	 */
	public boolean doGoBack() {
		return mWebView.doGoBack();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mHeadView != null && headHeight == 0) {
			measureChild(mHeadView, widthMeasureSpec, heightMeasureSpec);
			headHeight = mHeadView.getMeasuredHeight();
			mHeadView.setPadding(0, -1 * headHeight, 0, 0);
		}
	}

	private final int touchSlop = ViewConfiguration.getTouchSlop();
	private final int minVelocity = ViewConfiguration.getMinimumFlingVelocity();
	private float startX = 0, startY = 0;
	private boolean isPull = false;
	private VelocityTracker velocityTracker;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev);
		if (isPull && action == MotionEvent.ACTION_MOVE)
			return true;
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(ev);
		float x = ev.getX();
		float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startX = x;
			startY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = y - startY;
			float dx = x - startX;
			velocityTracker.computeCurrentVelocity(1000);
			if (isUnderView(x, y)
					&& mWebView.getScrollY() == 0
					&& ((dy > touchSlop && Math.abs(dx) < touchSlop) || velocityTracker
							.getYVelocity() > minVelocity)) {
				isPull = true;
			}
			break;
		default:
			if (velocityTracker != null) {
				velocityTracker.recycle();
				velocityTracker = null;
			}
			isPull = false;
			break;
		}
		return isPull;
	}

	private boolean isUnderView(float x, float y) {
		return x > mWebView.getLeft() && x < mWebView.getRight()
				&& y > mWebView.getTop() && y < mWebView.getBottom();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);
		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startX = x;
			startY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = y - startY;
			if (isPull) {
				int padding = (int) (dy / 2 - 1 * headHeight);
				mHeadView.setPadding(0, padding, 0, 0);
				if (mWebView.getTop() > headHeight) {
					mRefreshText
							.setText(R.string.pull_to_refresh_release_label);
				} else {
					mRefreshText.setText(R.string.pull_to_refresh_pull_label);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			isPull = false;
			if (mWebView.getTop() >= headHeight) {
				mHeadView.setPadding(0, 0, 0, 0);
				mProgressBar.setVisibility(View.VISIBLE);
				mRefreshText.setText(R.string.pull_to_refresh_refreshing_label);
				reLoad();// 重新加载
			} else {
				mHeadView.setPadding(0, -1 * headHeight, 0, 0);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			isPull = false;
			break;
		default:
			break;
		}
		if (velocityTracker != null) {
			velocityTracker.recycle();
			velocityTracker = null;
		}
		return isPull;
	}

	@Override
	protected void reLoad() {
		setCacheMode();
		mWebView.reload();

	}

	public void setProcessListener(WebProcessListener processListener) {
		mWebView.setListener(processListener);
	}

}
