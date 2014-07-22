package cn.com.modernmedia.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import cn.com.modernmedia.R;

/**
 * 上拉加载更多
 * 
 * @author user
 * 
 */
public class CheckFooterListView extends PullToRefreshListView {
	private static final int LOADING = 1;// 加载中
	private static final int ERROR = 2;// 加载失败
	private static final int DONE = 3;// 还原状态

	private Context mContext;
	private View footerView;
	private TextView footText;
	private View progress;
	private FooterCallBack callBack;
	private int status = DONE;
	private int mFooterHeight;

	public static interface FooterCallBack {
		public void onLoad();
	}

	public CheckFooterListView(Context context) {
		this(context, null);
	}

	public CheckFooterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		footerView = LayoutInflater.from(mContext).inflate(
				R.layout.user_list_footer, null);
		mFooterHeight = mContext.getResources().getDimensionPixelSize(
				R.dimen.footer_height);

		footText = (TextView) footerView.findViewById(R.id.user_footer_text);
		progress = footerView.findViewById(R.id.user_footer_progressBar);

		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (status == ERROR) {
					onLoad();
				}
			}
		});
		addFooter();
		dismissFooter();
	}

	public void addFooter() {
		if (getFooterViewsCount() == 0)
			addFooterView(footerView);
	}

	public void removeFooter() {
		if (getFooterViewsCount() > 0)
			removeFooterView(footerView);
	}

	/**
	 * 隐藏footer
	 */
	public void dismissFooter() {
		footerView.setPadding(0, -mFooterHeight, 0, 0);
	}

	/**
	 * 隐藏footer
	 */
	public void showFooter() {
		footerView.setPadding(0, 0, 0, 0);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		super.onScrollStateChanged(view, scrollState);
		if (getFooterViewsCount() == 0)
			return;
		if (scrollState == SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() == view.getCount() - 1) {
				onLoad();
			}
		}
	}

	/**
	 * 加载
	 */
	private void onLoad() {
		if (callBack != null) {
			progress.setVisibility(View.VISIBLE);
			footText.setText(R.string.pull_to_loading);
			callBack.onLoad();
			status = LOADING;
		}
	}

	/**
	 * 加载失败
	 */
	public void onLoadError() {
		status = ERROR;
		progress.setVisibility(View.INVISIBLE);
		footText.setText(R.string.click_to_load);
	}

	/**
	 * 加载成功
	 * 
	 * @param hasMore
	 *            是否还有
	 */
	public void loadOk(boolean hasMore) {
		status = DONE;
		if (!hasMore) {
			removeFooter();
		}
	}

	public void setCallBack(FooterCallBack callBack) {
		this.callBack = callBack;
	}

}
