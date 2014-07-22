package cn.com.modernmedia.businessweek.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.IndexAdapter;
import cn.com.modernmedia.businessweek.unit.BusinessweekTools;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.PullToRefreshListView;
import cn.com.modernmediaslate.model.Entry;

/**
 * 子栏目item
 * 
 * @author ZhuQiao
 * 
 */
public class ChildCatItem extends BaseView {
	private Context mContext;
	private PullToRefreshListView listView;
	private IndexHeadView headView;
	private IndexAdapter adapter;
	private Issue issue;
	private String columnId;
	private int childSize;

	public ChildCatItem(Context context) {
		this(context, null);
	}

	public ChildCatItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(R.layout.child_item,
				null));
		initProcess();
		listView = (PullToRefreshListView) findViewById(R.id.child_cat_listview);
		listView.enableAutoFetch(false, false);
		listView.setParam(true, true, false);
		headView = new IndexHeadView(mContext);
		listView.addHeaderView(headView);
		listView.setScrollView(headView.getViewPager());
		adapter = new IndexAdapter(mContext);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContext instanceof MainActivity) {
					position = position - listView.getHeaderViewsCount();
					ArticleItem item = adapter.getItem(position);
					LogHelper.logOpenArticleFromColumnPage(mContext,
							item.getArticleId() + "", item.getCatId() + "");
					BusinessweekTools.clickSlate(ChildCatItem.this, mContext,
							item, ArticleType.Default);
				}
			}
		});
	}

	private void getCatIndex(Issue issue, String columnId) {
		showLoading();
		OperateController.getInstance(mContext).getCartIndex(issue, columnId,
				((MainActivity) mContext).getCatPosition(columnId),
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof CatIndexArticle) {
							setDataForList((CatIndexArticle) entry);
							disProcess();
						} else {
							showError();
						}
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
		adapter.clear();
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
			List<ArticleItem> titleList = catIndexArticle.getTitleActicleList();
			if (ParseUtil.listNotNull(titleList)) {
				headView.setPadding(0, 0, 0, 0);
				headView.invalidate();
				headView.setData(catIndexArticle);
				childSize = titleList.size();
			} else {
				headView.setPadding(0, -CommonApplication.width / 2, 0, 0);
				headView.invalidate();
				childSize = 0;
			}
		} else {
			headView.setPadding(0, -CommonApplication.width / 2, 0, 0);
			headView.invalidate();
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
