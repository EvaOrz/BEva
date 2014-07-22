package cn.com.modernmedia.views.solo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.SoloColumn.SoloColumnChild;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.index.IndexHeadView;
import cn.com.modernmedia.views.listener.ChildCatClickListener;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.PullToRefreshListView;
import cn.com.modernmediaslate.model.Entry;

/**
 * 子栏目itemview
 * 
 * @author user
 * 
 */
public class ChildCatItem extends RelativeLayout implements
		ChildCatClickListener {
	private Context mContext;
	private PullToRefreshListView listView;
	private IndexHeadView headView;// 焦点图
	private BaseChildCatHead childHead;// 栏目导航栏
	private BaseIndexAdapter adapter;
	private String columnId;
	private int childSize;
	private IndexListParm parm;
	private View layout, loading, error;

	public ChildCatItem(Context context, View defaultHeadView, int parentId) {
		super(context);
		mContext = context;
		init(defaultHeadView, parentId);
	}

	private void init(View defaultHeadView, int parentId) {
		parm = ParseProperties.getInstance(mContext).parseIndexList();
		addView(LayoutInflater.from(mContext)
				.inflate(R.layout.solo_index, null));
		layout = findViewById(R.id.solo_item_layout);
		loading = findViewById(R.id.solo_item_loading);
		error = findViewById(R.id.solo_item_error);
		listView = (PullToRefreshListView) findViewById(R.id.solo_cat_listview);
		listView.enableAutoFetch(false, false);
		listView.setParam(true, true, false);
		listView.setCheck_angle(true);
		headView = V.getIndexHeadView(mContext, parm);
		if (headView != null) {
			listView.addHeaderView(headView);
			listView.setScrollView(headView);
		}
		adapter = V.getIndexAdapter(mContext, parm);
		if (defaultHeadView != null)
			listView.addHeaderView(defaultHeadView);

		// 栏目导航栏
		if (ParseUtil.mapContainsKey(DataHelper.childMap, parentId)
				&& parm.getCat_list_hold() == 0) {
			if (parm.getCat_list_type().equals(V.BUSINESS)) {
				childHead = new BusChildCatHead(mContext, null);
			} else if (parm.getCat_list_type().equals(V.IWEEKLY)) {
				childHead = new WeeklyChildCatHead(mContext, null);
			}
		}
		if (childHead != null) {
			childHead.setChildValues(parentId);
			listView.addHeaderView(childHead.fetchView());
			listView.setScrollView(childHead.fetchView());
		}

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContext instanceof CommonMainActivity) {
					position = position - listView.getHeaderViewsCount();
					ArticleItem item = adapter.getItem(position);
					LogHelper.logOpenArticleFromColumnPage(mContext,
							item.getArticleId() + "", item.getCatId() + "");
					V.clickSlate(mContext, item, ArticleType.Default);
				}
			}
		});
		error.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reLoad();
			}
		});
	}

	private void getCatIndex(String columnId) {
		if (CommonApplication.manage == null)
			return;
		showLoading();
		OperateController.getInstance(mContext).getCartIndex(
				CommonApplication.issue, columnId,
				CommonApplication.manage.getCatPosition(columnId),
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

	protected void reLoad() {
		getCatIndex(columnId);
	}

	public void setData(String columnId) {
		this.columnId = columnId;
		getCatIndex(columnId);
	}

	private void setDataForList(CatIndexArticle catIndexArticle) {
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (TextUtils.isEmpty(parm.getHead_type())) {
			// TODO 没有焦点图
			for (int key : catIndexArticle.getMap().keySet()) {
				list.addAll(catIndexArticle.getMap().get(key));
			}
		} else {
			list = catIndexArticle.getMap().get(parm.getItem_position());
		}
		adapter.clear();
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
			if (catIndexArticle.hasData(parm.getHead_position())) {
				showHead(catIndexArticle);
				childSize = catIndexArticle.getMap()
						.get(parm.getHead_position()).size();
			} else {
				dissHead();
				childSize = 0;
			}
		} else {
			dissHead();
			childSize = 0;
		}
	}

	/**
	 * 显示焦点图
	 * 
	 * @param entry
	 */
	private void showHead(Entry entry) {
		if (headView != null) {
			headView.setPadding(0, 0, 0, 0);
			headView.invalidate();
			headView.setData(entry);
		}
	}

	/**
	 * 隐藏焦点图
	 */
	private void dissHead() {
		if (headView != null) {
			headView.setPadding(0, -parm.getHead_height()
					* CommonApplication.width / 720, 0, 0);
			headView.invalidate();
		}
	}

	public IndexHeadView getHeadView() {
		return headView;
	}

	public int getChildSize() {
		return childSize;
	}

	/**
	 * 显示loading图标
	 */
	public void showLoading() {
		layout.setVisibility(View.VISIBLE);
		loading.setVisibility(View.VISIBLE);
		error.setVisibility(View.GONE);
	}

	/**
	 * 显示错误提示
	 */
	public void showError() {
		layout.setVisibility(View.VISIBLE);
		loading.setVisibility(View.GONE);
		error.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏process_layout
	 */
	public void disProcess() {
		layout.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		error.setVisibility(View.GONE);
	}

	@Override
	public void onClick(int position, int parentId, SoloColumnChild soloChild) {
		if (childHead != null && parentId != -1) {
			childHead.setSelectedItemForChild(parentId);
		}
	}
}
