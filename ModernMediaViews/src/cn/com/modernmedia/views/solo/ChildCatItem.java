package cn.com.modernmedia.views.solo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.api.GetTagArticlesOperate;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.util.TagDataHelper;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.index.head.BaseIndexHeadView;
import cn.com.modernmedia.views.listener.ChildCatClickListener;
import cn.com.modernmedia.views.listener.LoadCallBack;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.util.DownloadTemplate;
import cn.com.modernmedia.views.util.DownloadTemplate.DownloadTemplateCallBack;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.CheckFooterListView;
import cn.com.modernmedia.widget.CheckFooterListView.FooterCallBack;
import cn.com.modernmedia.widget.PullToRefreshListView.OnRefreshListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 子栏目itemview
 * 
 * @author user
 * 
 */
public class ChildCatItem extends RelativeLayout implements
		ChildCatClickListener, LoadCallBack {
	private Context mContext;
	private CheckFooterListView listView;
	private BaseIndexHeadView headView;// 焦点图
	private BaseChildCatHead childHead;// 栏目导航栏
	private BaseIndexAdapter adapter;
	private int childSize;
	private Template template;
	private View layout, loading, error;

	private TagInfo tagInfo;
	private TagInfoList childInfoList;
	private ChildIndexView childIndexView;

	// 焦点图数据，累加
	private List<ArticleItem> headList = new ArrayList<ArticleItem>();

	private String tagName = "";
	private String top = "";
	private TagArticleList _articleList = new TagArticleList();

	public ChildCatItem(Context context, View defaultHeadView, TagInfo tagInfo,
			TagInfoList childInfoList, ChildIndexView childIndexView) {
		super(context);
		mContext = context;
		this.tagInfo = tagInfo;
		this.childInfoList = childInfoList;
		this.childIndexView = childIndexView;
		init(defaultHeadView);
		getArticleList(false, false);
	}

	@SuppressLint("InflateParams")
	private void init(View defaultHeadView) {
		addView(LayoutInflater.from(mContext)
				.inflate(R.layout.solo_index, null));
		layout = findViewById(R.id.solo_item_layout);
		loading = findViewById(R.id.solo_item_loading);
		error = findViewById(R.id.solo_item_error);
		listView = (CheckFooterListView) findViewById(R.id.solo_cat_listview);
		listView.enableAutoFetch(true, false);
		listView.setParam(true, true, false);
		listView.setCheck_angle(true);

		if (defaultHeadView != null)
			listView.addHeaderView(defaultHeadView);

		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefreshDone() {
			}

			@Override
			public void onRefreshComplete() {
			}

			@Override
			public void onRefresh() {
				if (!TextUtils.isEmpty(tagName))
					refresh();
			}

			@Override
			public void onPulling() {
			}

			@Override
			public void onLoad() {
			}
		});
		listView.setCallBack(new FooterCallBack() {

			@Override
			public void onLoad() {
				if (!TextUtils.isEmpty(top)) {
					getArticleList(false, true);
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

	/**
	 * 初始化配置信息
	 */
	private void initProperties() {
		// 列表headview
		if (!TextUtils.isEmpty(template.getListHead().getData())) {
			XMLParse parse = new XMLParse(mContext, null);
			View listHead = parse.inflate(template.getListHead().getData(),
					null, template.getHost());
			listHead.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT,
					AbsListView.LayoutParams.WRAP_CONTENT));
			parse.setDataForListHead(tagInfo);
			listView.addHeaderView(listHead);
		}
		// 栏目导航栏
		if (template.getCatHead().getCat_list_hold() == 0) {
			childHead = new ChildCatHead(mContext, null, template);
		}
		if (childHead != null) {
			childHead.setChildValues(childInfoList, tagInfo.getTagName());
			listView.addHeaderView(childHead.fetchView());
			listView.setScrollView(childHead.fetchView());
		}
		// 栏目导航栏
		if (childIndexView != null) {
			childIndexView.initProperties(template, true);
		}
		// 焦点图
		headView = V.getIndexHeadView(mContext, template);
		if (headView != null) {
			listView.addHeaderView(headView);
			listView.setScrollView(headView);
		}

		adapter = V.getIndexAdapter(mContext, template, ArticleType.Default);
		listView.setAdapter(adapter);
	}

	/**
	 * 先请求文章列表(与栏目首页联动);文章列表的数据库缓存会在接口里实现
	 */
	private void getArticleList(final boolean refresh, final boolean loadMore) {
		if (!refresh && !loadMore)
			showLoading();
		String _top = loadMore ? top : "";
		OperateController.getInstance(mContext).getTagArticles(tagInfo, _top,
				"", null, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							if (ParseUtil.listNotNull(articleList
									.getArticleList())) {
								getCatIndex(refresh, loadMore);
							} else {
								listView.loadOk(false);
								onLoaded(false);
							}
							return;
						}
						if (!refresh && !loadMore)
							showError();
					}
				});
	}

	/**
	 * 获取栏目首页
	 * 
	 * @param columnId
	 */
	private void getCatIndex(final boolean refresh, final boolean loadMore) {
		String _top = loadMore ? top : "";
		TagArticleList t = !refresh
				&& TextUtils.equals(_articleList.getViewbygroup(),
						TagArticleList.BY_INPUTTIME) ? _articleList : null;
		OperateController.getInstance(mContext).getTagIndex(tagInfo, _top, "",
				t, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							getTemplate((TagArticleList) entry, loadMore);
							if (loadMore) {
								onLoaded(true);
							} else if (refresh) {
								onRefreshed(true);
							}
						} else {
							if (loadMore) {
								onLoaded(false);
							} else if (refresh) {
								onRefreshed(false);
							} else {
								showError();
							}
						}
					}
				});
	}

	/**
	 * 获取模板
	 */
	private void getTemplate(final TagArticleList articleList,
			final boolean loadMore) {
		if (template != null) {
			disProcess();
			checkShouldInsertSubscribe(articleList, loadMore);
			return;
		}
		new DownloadTemplate(mContext, articleList,
				new DownloadTemplateCallBack() {

					@Override
					public void afterDownload(Template _template) {
						disProcess();
						template = _template;
						initProperties();
						checkShouldInsertSubscribe(articleList, loadMore);
					}
				}).getTemplate();
	}

	/**
	 * 判断是否需要插入订阅文章
	 * 
	 * @param dArticleList
	 * @param loadMore
	 */
	private void checkShouldInsertSubscribe(TagArticleList dArticleList,
			boolean loadMore) {
		if (!loadMore
				&& V.checkShouldInsertSubscribeArticle(mContext,
						tagInfo.getTagName())) {
			// TODO 如果是iweekly新闻栏目，那么获取所有订阅栏目的前5篇文章
			Entry subscribeTopEntry = TagIndexDb.getInstance(mContext)
					.getEntry(
							new GetTagArticlesOperate(tagInfo, "", "5", null),
							"", "", false, TagIndexDb.SUBSCRIBE_TOP_ARTICLE);
			if (subscribeTopEntry instanceof TagArticleList
					&& ParseUtil
							.listNotNull(((TagArticleList) subscribeTopEntry)
									.getArticleList())) {
				reBuildArticleList(dArticleList,
						(TagArticleList) subscribeTopEntry, loadMore);
			} else {
				getSubscribeTopArticle(dArticleList, loadMore);
			}
		} else {
			reBuildArticleList(dArticleList, null, loadMore);
		}
	}

	/**
	 * 获取所有订阅栏目前5篇文章
	 * 
	 * @param articleList
	 */
	private void getSubscribeTopArticle(final TagArticleList dArticleList,
			final boolean loadMore) {
		String mergeNames = AppValue.ensubscriptColumnList
				.getSubscriptTagMergeName();
		if (TextUtils.isEmpty(mergeNames)) {
			reBuildArticleList(dArticleList, null, loadMore);
			return;
		} else {
			OperateController.getInstance(mContext).getTagIndex(tagInfo, "",
					"5", null, new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							if (entry instanceof TagArticleList) {
								reBuildArticleList(dArticleList,
										(TagArticleList) entry, loadMore);
							} else {
								showError();
							}
						}
					});
		}
	}

	/**
	 * 重新组装文章列表
	 * 
	 * @param dArticleList
	 *            初始的列表
	 * @param sArticleList
	 *            订阅top5列表
	 */
	private void reBuildArticleList(TagArticleList dArticleList,
			TagArticleList sArticleList, boolean loadMore) {
		dArticleList.insertSubscribeArticle(mContext, sArticleList, true);
		setDataForList(dArticleList, loadMore);
	}

	private void setDataForList(TagArticleList articleList, boolean loadMore) {
		_articleList.addTagArticleList(articleList);
		tagName = articleList.getTagName();
		top = articleList.getEndOffset();
		if (!TextUtils.isEmpty(top)) {
			listView.showFooter();
		} else {
			listView.loadOk(false);
		}
		List<ArticleItem> list = new ArrayList<ArticleItem>();
		if (!ParseUtil.listNotNull(template.getHead().getPosition())) {
			// TODO 没有焦点图
			for (int key : articleList.getMap().keySet()) {
				list.addAll(articleList.getMap().get(key));
			}
		} else {
			for (int i : template.getList().getPosition()) {
				if (ParseUtil.mapContainsKey(articleList.getMap(), i))
					list.addAll(articleList.getMap().get(i));
			}
		}
		if (!loadMore) {
			adapter.clear();
			headList.clear();
		}
		if (ParseUtil.listNotNull(list)) {
			adapter.setData(list);
		}
		if (ParseUtil.listNotNull(template.getHead().getPosition())) {
			for (int position : template.getHead().getPosition()) {
				if (articleList.hasData(position)) {
					headList.addAll(articleList.getMap().get(position));
				}
				showHead();
			}
		}
		if (headList.size() > 0) {
			showHead();
			childSize = headList.size();
		} else {
			dissHead();
			childSize = 0;
		}
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (listView.isShowLastItem()) {
					if (listView.getFooterViewsCount() > 0) {
						listView.onLoad();
					}
				}
			}
		}, 100);
	}

	/**
	 * 显示焦点图
	 * 
	 * @param entry
	 */
	private void showHead() {
		if (headView != null) {
			headView.setPadding(0, 0, 0, 0);
			headView.invalidate();
			headView.setData(headList);
		}
	}

	/**
	 * 隐藏焦点图
	 */
	private void dissHead() {
		if (headView != null) {
			headView.dismissHead();
		}
	}

	public BaseIndexHeadView getHeadView() {
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
	public void onClick(int position, TagInfo info) {
		if (childHead != null) {
			childHead.setSelectedItemForChild(info.getTagName());
		}
	}

	/**
	 * 刷新
	 */
	private void refresh() {
		// TODO 清除缓存更新时间
		TagDataHelper.setCatIndexUpdateTime(mContext, tagName, "");
		TagDataHelper.setCatArticleUpdateTime(mContext, tagName, "");
		getArticleList(true, false);
	}

	@Override
	public void onRefreshed(boolean success) {
		listView.onRefreshComplete(false, 0);
	}

	@Override
	public void onLoaded(boolean success) {
		if (success) {
			listView.loadOk(true);
		} else {
			listView.onLoadError();
		}
	}

	protected void reLoad() {
		getArticleList(false, false);
	}
}
