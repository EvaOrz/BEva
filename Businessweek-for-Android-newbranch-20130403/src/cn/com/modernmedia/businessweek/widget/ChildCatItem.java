package cn.com.modernmedia.businessweek.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.modernmedia.VideoPlayerActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.IndexAdapter;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.ListScrollStateListener;
import cn.com.modernmedia.listener.SlateListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.HeadPagerListView;

/**
 * 子栏目item
 * 
 * @author ZhuQiao
 * 
 */
public class ChildCatItem extends BaseView {
	private Context mContext;
	private HeadPagerListView listView;
	private IndexHeadView headView;
	private IndexAdapter adapter;
	private ListScrollStateListener scrollListener;
	private OperateController controller;
	private Issue issue;
	private String columnId;
	private int childSize;
	private Handler handler = new Handler();

	private SlateListener listener = new SlateListener() {

		@Override
		public void video(ArticleItem item, String path) {
			if (item.getAdv().getAdvProperty().getIsadv() == 1
					&& path.toLowerCase().endsWith(".mp4")) {
				Intent intent = new Intent(mContext, VideoPlayerActivity.class);
				intent.putExtra("vpath", path);
				mContext.startActivity(intent);
			}
		}

		@Override
		public void linkNull(ArticleItem item) {
			((MainActivity) mContext).gotoArticleActivity(item.getArticleId(),
					item.getCatId(), true);
		}

		@Override
		public void image(String url) {
		}

		@Override
		public void httpLink(ArticleItem item, Intent intent) {
			if (item.getAdv().getAdvProperty().getIsadv() == 1)
				((MainActivity) mContext).startActivity(intent);
		}

		@Override
		public void gallery(String url) {
		}

		@Override
		public void column(String columnId) {
		}

		@Override
		public void articleLink(ArticleItem item, int articleId) {
			((MainActivity) mContext).gotoArticleActivity(articleId,
					item.getCatId(), true);
		}
	};

	public ChildCatItem(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ChildCatItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		controller = new OperateController(mContext);
		this.addView(LayoutInflater.from(mContext).inflate(R.layout.child_item,
				null));
		initProcess();
		listView = (HeadPagerListView) findViewById(R.id.child_cat_listview);
		listView.setFooterDividersEnabled(true);
		listView.setHeaderDividersEnabled(true);
		headView = new IndexHeadView(mContext);
		listView.addHeaderView(headView);
		listView.setScrollView(headView.getViewPager());
		adapter = new IndexAdapter(mContext);
		scrollListener = adapter.getListener();
		listView.setAdapter(adapter);
		setListener(listener);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContext instanceof MainActivity) {
					if (listView.getHeaderViewsCount() > 0) {
						position = position - 1;// 因为有headview，所有要-1
					}
					ArticleItem item = adapter.getItem(position);
					LogHelper.logOpenArticleFromColumnPage(mContext,
							item.getArticleId() + "", item.getCatId() + "");
					int type = item.getAdv().getAdvProperty().getIsadv();
					if (type != 1) {// 不为广告
						clickSlate(item);
					}
				}
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollListener == null)
					return;
				if (scrollState == SCROLL_STATE_FLING
						|| scrollState == SCROLL_STATE_TOUCH_SCROLL) {
					scrollListener.scrolling();
				} else if (scrollState == SCROLL_STATE_IDLE) {
					scrollListener.scrollIdle();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void getCatIndex(Issue issue, String columnId) {
		showLoading();
		controller.getCartIndex(issue, columnId, new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (entry != null && entry instanceof CatIndexArticle) {
							setDataForList((CatIndexArticle) entry);
							disProcess();
						} else {
							showError();
						}
					}
				});
			}
		});
	}

	@Override
	protected void reLoad() {
		getCatIndex(issue, columnId);
	}

	public void setData(Issue issue, String columnId) {
		this.issue = issue;
		this.columnId = columnId;
		getCatIndex(issue, columnId);
	}

	private void setDataForList(CatIndexArticle catIndexArticle) {
		List<ArticleItem> list = catIndexArticle.getArticleItemList();
		if (list == null || list.isEmpty())
			return;
		adapter.clear();
		adapter.setData(list);
		List<ArticleItem> titleList = catIndexArticle.getTitleActicleList();
		if (titleList != null && !titleList.isEmpty()) {
			headView.setData(catIndexArticle);
			childSize = titleList.size();
		} else {
			listView.removeHeaderView(headView);
			childSize = 0;
		}
	}

	public IndexHeadView getHeadView() {
		return headView;
	}

	public int getChildSize() {
		return childSize;
	}

}
