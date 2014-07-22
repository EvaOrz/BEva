package cn.com.modernmedia.businessweek.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.adapter.MyPagerAdapter.OnItemClickListener;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.IndexAdapter;
import cn.com.modernmedia.businessweek.fragment.ChildCatFragment;
import cn.com.modernmedia.businessweek.fragment.IndexListFragment;
import cn.com.modernmedia.businessweek.solo.SoloCatFragment;
import cn.com.modernmedia.businessweek.unit.BusinessweekTools;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.model.IndexArticle.Today;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页
 * 
 * @author ZhuQiao
 * 
 */
public class IndexView extends BaseView implements FetchEntryListener {
	private static final String LIST_TAG = "list";
	private static final String CHILD_TAG = "child";
	private static final String SOLO_TAG = "solo";
	public static final String[] TAGS = { LIST_TAG, CHILD_TAG, SOLO_TAG };
	private String current_tag = "";
	private Context mContext;
	private Button column, fav;
	private TextView title;
	private ChildCatFragment childFragment;
	private IndexListFragment listFragment;
	private SoloCatFragment soloFragment;
	private LinearLayout cover;
	private boolean isIndex;
	private Entry entry;

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
		cover = (LinearLayout) findViewById(R.id.index_cover);
		cover.setBackgroundColor(Color.TRANSPARENT);
		cover.setBackgroundDrawable(null);

		listFragment = new IndexListFragment();
		childFragment = new ChildCatFragment();
		soloFragment = new SoloCatFragment();

		cover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContext instanceof MainActivity) {
					((MainActivity) mContext).getScrollView().IndexClick();
				}
			}
		});
		listFragment.setClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				IndexAdapter adapter = listFragment.getAdapter();
				if (adapter != null && adapter.getCount() > position) {
					ArticleItem item = adapter.getItem(position);
					BusinessweekTools.clickSlate(IndexView.this, mContext,
							item, ArticleType.Default);
					LogHelper.logOpenArticleFromColumnPage(mContext,
							item.getArticleId() + "", item.getCatId() + "");
					if (isIndex && entry instanceof IndexArticle) {
						findCatPosition(item.getCatId(), item.getArticleId(),
								(IndexArticle) entry);
					}
				}
			}
		});
	}

	private void findCatPosition(int catId, int articleId,
			IndexArticle indexArticle) {
		int section = -1, row = -1;
		List<Today> todatList = indexArticle.getTodayList();
		List<ArticleItem> itemList = null;
		for (int i = 0; i < todatList.size(); i++) {
			Today t = todatList.get(i);
			if (t.getTodayCatId() == catId) {
				section = i;
				itemList = t.getArticleItemList();
				break;
			}
		}
		if (ParseUtil.listNotNull(itemList)) {
			for (int i = 0; i < itemList.size(); i++) {
				if (itemList.get(i).getArticleId() == articleId) {
					row = i;
					break;
				}
			}
		}
		if (section != -1 && row != -1) {
			LogHelper.logAndroidTouchHighlightsTable(section, row);
		}
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
			this.entry = entry;
			current_tag = LIST_TAG;
			listFragment.setData(entry);
			showFragmentIfNeeded(listFragment, LIST_TAG, R.id.index_main, TAGS);
			isIndex = entry instanceof IndexArticle;
		}
	}

	public void setDataForChild(Issue issue, int parentId) {
		current_tag = CHILD_TAG;
		childFragment.setData(issue, parentId);
		showFragmentIfNeeded(childFragment, CHILD_TAG, R.id.index_main, TAGS);
	}

	public void setDataForSolo(int parentId) {
		current_tag = SOLO_TAG;
		soloFragment.setData(parentId);
		showFragmentIfNeeded(soloFragment, SOLO_TAG, R.id.index_main, TAGS);
	}

	@Override
	protected void reLoad() {
		if (mContext instanceof MainActivity) {
			((MainActivity) mContext).reLoadData();
		}
	}

	/**
	 * 是否启动自动切换
	 * 
	 * @param start
	 */
	public void setAuto(boolean start) {
		if (!current_tag.equals(LIST_TAG) || listFragment == null
				|| listFragment.getHeadView() == null)
			return;
		if (start) {
			listFragment.getHeadView().startRefresh();
		} else {
			listFragment.getHeadView().stopRefresh();
		}
	}

	public void reStorePullResfresh() {
		if (current_tag == SOLO_TAG && soloFragment != null) {
			soloFragment.reStoreRefresh();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (current_tag.equals(CHILD_TAG) && childFragment != null) {
			IndexHeadView headView = childFragment.getHeadView();
			if (headView != null && childFragment.getChildSize() > 1) {
				ViewPager scrollView = headView.getViewPager();
				if (scrollView != null) {
					Rect rect = new Rect();
					// 获取该gallery相对于全局的坐标点
					scrollView.getGlobalVisibleRect(rect);
					if (rect.contains((int) ev.getX(), (int) ev.getY())) {
						childFragment.setIntercept(true);
						return false;
					}
				}
			}
			childFragment.setIntercept(false);
		} else if (current_tag.equals(SOLO_TAG) && soloFragment != null) {
			IndexHeadView headView = soloFragment.getHeadView();
			if (headView != null && soloFragment.getChildSize() > 1) {
				ViewPager scrollView = headView.getViewPager();
				if (scrollView != null) {
					Rect rect = new Rect();
					// 获取该gallery相对于全局的坐标点
					scrollView.getGlobalVisibleRect(rect);
					if (rect.contains((int) ev.getX(), (int) ev.getY())) {
						soloFragment.setIntercept(true);
						return false;
					}
				}
			}
			soloFragment.setIntercept(false);
		}
		return super.onInterceptTouchEvent(ev);
	}
}
