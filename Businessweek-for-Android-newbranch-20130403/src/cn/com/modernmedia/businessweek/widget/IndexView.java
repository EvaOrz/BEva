package cn.com.modernmedia.businessweek.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.modernmedia.VideoPlayerActivity;
import cn.com.modernmedia.api.ImageDownloader;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.IndexAdapter;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.SlateListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.IndexArticle.Today;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.widget.BaseView;

/**
 * 首页
 * 
 * @author ZhuQiao
 * 
 */
public class IndexView extends BaseView implements FetchEntryListener {
	private Context mContext;
	private Button column, fav;
	private TextView title;
	private ListView listView;
	private IndexAdapter adapter;
	private IndexHeadView headView;
	private LinearLayout cover;
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
					true);
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
			((MainActivity) mContext).gotoArticleActivity(articleId, true);
		}
	};

	public IndexView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public IndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext)
				.inflate(R.layout.index, null));
		initProcess();
		column = (Button) findViewById(R.id.index_titleBar_column);
		fav = (Button) findViewById(R.id.index_titleBar_fav);
		title = (TextView) findViewById(R.id.index_titleBar_title);
		listView = (ListView) findViewById(R.id.index_listview);
		cover = (LinearLayout) findViewById(R.id.index_cover);
		cover.setBackgroundColor(Color.TRANSPARENT);
		cover.setBackgroundDrawable(null);
		headView = new IndexHeadView(mContext);
		adapter = new IndexAdapter(mContext);
		cover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContext instanceof MainActivity) {
					((MainActivity) mContext).getScrollView().IndexClick();
				}
			}
		});
		setListener(listener);
		// 把gallery作为listview的head，这样可以在滑动的时候一起滑
		listView.setFooterDividersEnabled(true);
		listView.setHeaderDividersEnabled(true);
		listView.addHeaderView(headView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 因为有headview，所有要-1
				if (mContext instanceof MainActivity && position > 0) {
					ArticleItem item = adapter.getItem(position - 1);
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
				if (scrollState == SCROLL_STATE_FLING
						|| scrollState == SCROLL_STATE_TOUCH_SCROLL) {
					ImageDownloader.getInstance().lock();
				} else if (scrollState == SCROLL_STATE_IDLE) {
					ImageDownloader.getInstance().unlock();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	public Gallery getGallery() {
		return headView.getGallery();
	}

	public Button getColumn() {
		return column;
	}

	public Button getFav() {
		return fav;
	}

	/**
	 * 设置首页标题
	 * 
	 * @param name
	 */
	public void setTitle(String name) {
		title.setText(name);
	}

	/**
	 * 如果显示的是index,就把cover隐藏掉
	 * 
	 * @param showIndex
	 */
	public void showCover(boolean showIndex) {
		if (showIndex) {
			cover.setVisibility(View.GONE);
		} else {
			cover.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 如果cover显示，当action_down在首页上的时候，ontouch被传递给最顶层，即cover,
	 * scrollview的ontouch事件被拦截了， 返回false则交由scrollview处理
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (cover.getVisibility() == View.VISIBLE) {
			return false;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void setData(Entry entry) {
		if (entry != null) {
			if (entry instanceof IndexArticle) {
				IndexArticle indexArticle = (IndexArticle) entry;
				setValuesForWidget(indexArticle);
				headView.setData(indexArticle);
			} else if (entry instanceof CatIndexArticle) {
				CatIndexArticle catIndexArticle = (CatIndexArticle) entry;
				setValuesForWidget(catIndexArticle);
				headView.setData(catIndexArticle);
			}
		}
	}

	/**
	 * 首页
	 * 
	 * @param indexArticle
	 */
	private void setValuesForWidget(IndexArticle indexArticle) {
		List<Today> todayList = indexArticle.getTodayList();
		if (todayList == null || todayList.isEmpty())
			return;
		adapter.clear();
		for (Today today : todayList) {
			if (today == null)
				continue;
			List<ArticleItem> itemList = today.getArticleItemList();
			if (itemList == null || itemList.isEmpty())
				continue;
			adapter.setData(itemList);
		}
		listView.setSelection(0);
	}

	/**
	 * 栏目首页
	 * 
	 * @param catIndexArticle
	 */
	private void setValuesForWidget(CatIndexArticle catIndexArticle) {
		List<ArticleItem> list = catIndexArticle.getArticleItemList();
		if (list == null || list.isEmpty())
			return;
		adapter.clear();
		adapter.setData(list);
		listView.setSelection(0);
	}

	@Override
	protected void reLoad() {
		if (mContext instanceof MainActivity) {
			((MainActivity) mContext).reLoadData();
		}
	}

	/**
	 * 刷新已阅读文章
	 */
	public void notifyAdapter() {
		adapter.notifyDataSetChanged();
	}

}
