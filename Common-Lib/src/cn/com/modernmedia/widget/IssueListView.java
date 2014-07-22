package cn.com.modernmedia.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.R;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.IssueList;
import cn.com.modernmedia.model.IssueList.IssueListItem;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmediaslate.model.Entry;

/**
 * 往期列表
 * 
 * @author ZhuQiao
 * 
 */
public class IssueListView extends BaseView {
	private static final int LOADING = 1;// 加载中
	private static final int ERROR = 2;// 加载失败
	private static final int DONE = 3;// 还原状态

	private Context mContext;
	private int mPage = 1;
	private int totalCount;
	private OperateController mController;
	private ListView mListView;
	private View mFootView;
	private TextView foot_text;
	private ProgressBar mProgressBar;
	private List<IssueListItem> list = new ArrayList<IssueListItem>();
	private int status = DONE;
	private boolean isfirstIn = true;
	private LayoutInflater inflater;
	private FetchEntryListener mFetchEntryListener;

	public IssueListView(Context context) {
		this(context, null);
	}

	public IssueListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mController = OperateController.getInstance(mContext);
		init();
	}

	private void init() {
		inflater = LayoutInflater.from(mContext);
		this.addView(inflater.inflate(R.layout.issue_list_view, null));
		initProcess();
		mListView = (ListView) findViewById(R.id.issue_list);
		initFooter();
	}

	private void initFooter() {
		mFootView = inflater.inflate(R.layout.pull_to_load_footer, null);
		foot_text = (TextView) mFootView.findViewById(R.id.footer_text);
		foot_text.setText(R.string.pull_to_loading);
		mFootView.findViewById(R.id.footer_arrowImageView).setVisibility(
				View.GONE);
		mProgressBar = (ProgressBar) mFootView
				.findViewById(R.id.footer_progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		mListView.addFooterView(mFootView);
		mFootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (status == ERROR) {
					getIssueList();
					mProgressBar.setVisibility(View.VISIBLE);
					foot_text.setText(R.string.pull_to_loading);
				}
			}
		});
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mListView.getFooterViewsCount() == 0)
					return;
				if (scrollState == SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						if (list.size() < totalCount && status == DONE) {
							getIssueList();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	public void startFecth() {
		if (mPage == 1)
			getIssueList();
	}

	/**
	 * 获取往期列表
	 */
	private void getIssueList() {
		status = LOADING;
		if (isfirstIn)
			showLoading();
		mController.getIssueList(mPage, new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (entry instanceof IssueList) {
					IssueList issueList = (IssueList) entry;
					totalCount = issueList.getTotal();
					list.addAll(issueList.getList());
					mPage++;
					status = DONE;
					if (list.size() == totalCount) {
						mListView.removeFooterView(mFootView);
					}
					if (isfirstIn)
						disProcess();
					isfirstIn = false;
					if (mFetchEntryListener != null)
						mFetchEntryListener.setData(entry);
					if (ParseUtil.listNotNull(issueList.getList())) {
						for (IssueListItem item : issueList.getList()) {
							if (CommonApplication.issueIdList == null) {
								CommonApplication.issueIdList = new ArrayList<String>();
							}
							CommonApplication.issueIdList.add(String
									.valueOf(item.getId()));
						}
					}
				} else {
					status = ERROR;
					mProgressBar.setVisibility(View.GONE);
					foot_text.setText(R.string.click_to_load);
					if (isfirstIn)
						showError();
				}
			}
		});
	}

	public void setAdapter(BaseAdapter adapter) {
		mListView.setAdapter(adapter);
	}

	public void setEntryListener(FetchEntryListener listener) {
		mFetchEntryListener = listener;
	}

	public View getFootView() {
		return mFootView;
	}

	public ListView getListView() {
		return mListView;
	}

	@Override
	protected void reLoad() {
		getIssueList();
	}
}
