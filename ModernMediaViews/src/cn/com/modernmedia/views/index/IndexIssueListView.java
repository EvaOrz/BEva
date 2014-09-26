package cn.com.modernmedia.views.index;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.IssueListAdapter;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 期刊列表
 * 
 * @author jiancong
 * 
 */
public class IndexIssueListView implements FetchEntryListener {
	private static final int LOADING = 1;// 加载中
	private static final int ERROR = 2;// 加载失败
	private static final int DONE = 3;// 还原状态

	private Context mContext;
	private OperateController mController;
	private ListView mListView;
	private View mFootView;
	private TextView foot_text;
	private ProgressBar mProgressBar;
	private TagInfoList mTagInfoList = new TagInfoList();
	private int status = DONE;
	private LayoutInflater inflater;
	private IssueListAdapter mAdapter;
	// private IssueListParm mParm;
	private View view;

	public IndexIssueListView(Context context) {
		mContext = context;
		init();
	}

	private void init() {
		mController = OperateController.getInstance(mContext);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.index_issue_list_view, null);
		mListView = (ListView) view.findViewById(R.id.issue_list);
		initFooter();
		mAdapter = new IssueListAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mListView.getFooterViewsCount() == 0)
					return;
				if (scrollState == SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						// if (mTagInfoList.getList().size() <
						// mIssueList.getTotal()
						// && status == DONE) {
						// getIssueList(true);
						// }
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void initFooter() {
		// mParm = ParseProperties.getInstance(mContext).parseIssueList();
		mFootView = inflater.inflate(R.layout.pull_to_load_footer, null);
		foot_text = (TextView) mFootView.findViewById(R.id.footer_text);
		foot_text.setText(R.string.pull_to_loading);
		mFootView.findViewById(R.id.footer_arrowImageView).setVisibility(
				View.GONE);
		mProgressBar = (ProgressBar) mFootView
				.findViewById(R.id.footer_progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		// mListView.addFooterView(mFootView);
		mFootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (status == ERROR) {
					getIssueList(true, "0");
					mProgressBar.setVisibility(View.VISIBLE);
					foot_text.setText(R.string.pull_to_loading);
				}
			}
		});
		// 目前不支持分页
		mFootView.setVisibility(View.GONE);
	}

	@Override
	public void setData(Entry entry) {
		getIssueList(false, "0");
	}

	/**
	 * 获取列表View
	 * 
	 * @return
	 */
	public View fetchView() {
		return view;
	}

	// /**
	// * 清除首页scrollview设置需要拦截的子scrollview
	// */
	// public void removeScroll() {
	// if (mContext instanceof CommonMainActivity) {
	// ((CommonMainActivity) mContext).setScrollView(3, null);
	// }
	// }

	/**
	 * 获取往期列表
	 * 
	 * @param is
	 */
	public void getIssueList(final boolean isGetMore, String top) {
		status = LOADING;
		if (!isGetMore && mContext instanceof CommonMainActivity)
			((CommonMainActivity) mContext).checkIndexLoading(1);
		mController.getLastIssueList("", new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				doAfterGetData(entry, isGetMore);
			}
		});
	}

	private void doAfterGetData(Entry entry, boolean isGetMore) {
		if (entry instanceof TagInfoList) {
			TagInfoList issueList = (TagInfoList) entry;
			// mIssueList.setTotal(issueList.getTotal());
			mTagInfoList.getList().addAll(issueList.getList());
			status = DONE;
			// 所有数据加载完成,移除底部view
			// if (mTagInfoList.getList().size() == mIssueList.getTotal()) {
			// mListView.removeFooterView(mFootView);
			// }
			if (!isGetMore)
				((CommonMainActivity) mContext).checkIndexLoading(0);
			if (ParseUtil.listNotNull(issueList.getList())) {
				if (mAdapter != null) {
					mAdapter.setData(issueList);
				}
			}
		} else if (isGetMore) {
			status = ERROR;
			mProgressBar.setVisibility(View.GONE);
			foot_text.setText(R.string.click_to_load);
		} else {
			status = ERROR;
			((CommonMainActivity) mContext).checkIndexLoading(2);
		}
	}
}
