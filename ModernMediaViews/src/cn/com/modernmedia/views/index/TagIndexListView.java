package cn.com.modernmedia.views.index;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.TagDataHelper;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.index.adapter.BaseIndexAdapter;
import cn.com.modernmedia.views.index.head.BaseIndexHeadView;
import cn.com.modernmedia.views.listener.LoadCallBack;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.solo.BaseChildCatHead;
import cn.com.modernmedia.views.solo.ChildCatHead;
import cn.com.modernmedia.views.util.DownloadTemplate;
import cn.com.modernmedia.views.util.DownloadTemplate.DownloadTemplateCallBack;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.widget.HideNavListView;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.CheckFooterListView.FooterCallBack;
import cn.com.modernmedia.widget.PullToRefreshListView.OnRefreshListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.Tools;

public class TagIndexListView implements LoadCallBack, OnScrollListener {
	private Context mContext;
	private HideNavListView listView;
	private BaseIndexAdapter adapter;// 列表适配器
	private BaseIndexHeadView headView;// 焦点图
	private Template template;
	private String tagName = "";
	private String top = "";
	private TagInfo tagInfo;
	private List<ArticleItem> headList = new ArrayList<ArticleItem>();
	private boolean hasInitTemplate = false;

	private IndexViewPagerItem indexViewPagerItem;

	private List<View> selfScrollViews = new ArrayList<View>();
	private TagArticleList _articleList = new TagArticleList();

	private View defaultView;
	private int defaultY, currY = -1;

	public TagIndexListView(Context context) {
		this(context, null);
	}

	public TagIndexListView(Context context,
			IndexViewPagerItem indexViewPagerItem) {
		mContext = context;
		this.indexViewPagerItem = indexViewPagerItem;
		init();
	}

	private void init() {
		Rect frame = new Rect();
		((Activity) mContext).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		defaultY = frame.top;
		listView = new HideNavListView(mContext);
		if (SlateApplication.mConfig.getNav_hide() == 1) {
			addDefaultHead();
		}
		listView.setParam(true, true, true);
		listView.enableAutoFetch(true, false);
		listView.setCheck_angle(true);
		listView.setCheckScrollView(this);
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
				if (!TextUtils.isEmpty(top) && adapter != null) {
					load();
				}
			}
		});
	}

	private void addDefaultHead() {
		defaultView = ((ViewsMainActivity) mContext).getDefaultHeadView();
		if (defaultView != null)
			listView.addHeaderView(defaultView);
	}

	/**
	 * 初始化配置信息
	 */
	private void initProperties(String tagName, TagInfoList childList) {
		if (hasInitTemplate) {
			return;
		}
		hasInitTemplate = true;
		if (SlateApplication.mConfig.getNav_hide() == 0) {
			addDefaultHead();
		}
		// 列表headview
		if (!TextUtils.isEmpty(template.getListHead().getData())) {
			XMLParse parse = new XMLParse(mContext, null);
			View listHead = parse.inflate(template.getListHead().getData(),
					null, template.getHost());
			listHead.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT,
					AbsListView.LayoutParams.WRAP_CONTENT));
			parse.setDataForListHead(AppValue.currColumn);
			listView.addHeaderView(listHead);
		}
		BaseChildCatHead childHead = null;
		// 栏目导航栏
		if (childList != null && ParseUtil.listNotNull(childList.getList())
				&& template.getCatHead().getCat_list_hold() == 0) {
			childHead = new ChildCatHead(mContext, indexViewPagerItem, template);
		}
		// 栏目导航栏
		if (indexViewPagerItem != null) {
			indexViewPagerItem.setHeadFrame(template);
		}
		if (childHead != null) {
			childHead.setChildValues(childList, tagName);
			listView.addHeaderView(childHead.fetchView());
			listView.setScrollView(childHead.fetchView());
			selfScrollViews.add(childHead.fetchView());
		}
		// 焦点图
		headView = V.getIndexHeadView(mContext, template);
		if (headView != null) {
			listView.addHeaderView(headView);
			listView.setScrollView(headView);
			selfScrollViews.add(headView);
		}
		// list adpter
		adapter = V.getIndexAdapter(mContext, template, ArticleType.Default);
		listView.setAdapter(adapter);
	}

	public void setData(TagArticleList articleList, TagInfoList childList) {
		setData(articleList, childList, false, null);
	}

	/**
	 * 设置数据,首先获取模板
	 * 
	 * @param articleList
	 * @param childList
	 */
	public void setData(final TagArticleList articleList,
			final TagInfoList childList, final boolean loadMore, Template temp) {
		template = temp;
		tagName = articleList.getTagName();
		if (template != null) {
			disPro();
			initProperties(articleList.getTagName(), childList);
			checkShouldInsertSubscribe(articleList, loadMore);
			return;
		}
		new DownloadTemplate(mContext, articleList,
				new DownloadTemplateCallBack() {

					@Override
					public void afterDownload(Template _template) {
						disPro();
						template = _template;
						initProperties(articleList.getTagName(), childList);
						checkShouldInsertSubscribe(articleList, loadMore);
					}
				}).getTemplate();
	}

	private void disPro() {
		if (indexViewPagerItem != null)
			indexViewPagerItem.disProcess();
		ModernMediaTools.dismissLoad(mContext);
	}

	/**
	 * 判断是否需要插入订阅文章
	 * 
	 * @param dArticleList
	 * @param loadMore
	 */
	private void checkShouldInsertSubscribe(TagArticleList dArticleList,
			boolean loadMore) {
		tagInfo = TagInfoListDb.getInstance(mContext).getTagInfoByName(tagName,
				"", true);
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
				getSubscribeTopArticle(dArticleList, loadMore, tagInfo);
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
			final boolean loadMore, final TagInfo tagInfo) {
		String mergeNames = AppValue.ensubscriptColumnList
				.getSubscriptTagMergeName();
		if (TextUtils.isEmpty(mergeNames)) {
			reBuildArticleList(dArticleList, null, loadMore);
			return;
		} else {
			if (indexViewPagerItem != null) {
				indexViewPagerItem.showLoading();
			} else {
				Tools.showLoading(mContext, true);
			}
			OperateController.getInstance(mContext).getTagIndex(tagInfo, "",
					"5", null, new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							Tools.showLoading(mContext, false);
							if (entry instanceof TagArticleList) {
								if (indexViewPagerItem != null) {
									indexViewPagerItem.disProcess();
								}
								reBuildArticleList(dArticleList,
										(TagArticleList) entry, loadMore);
							} else {
								if (indexViewPagerItem != null) {
									indexViewPagerItem.showError();
								}
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
		TagArticleList result = dArticleList.copy();
		result.insertSubscribeArticle(mContext, sArticleList, true);
		setDataToIndex(result, loadMore);
	}

	/**
	 * 设置列表数据
	 * 
	 * @param articleList
	 */
	private void setDataToIndex(TagArticleList articleList, boolean loadMore) {
		checkHasMoreData(articleList);
		_articleList.addTagArticleList(articleList);
		if (TextUtils.equals(articleList.getViewbygroup(),
				TagArticleList.BY_CATNAME)) {
			// 按栏目排序没有加载更多
			listView.loadOk(false);
		}
		top = articleList.getEndOffset();
		if (!TextUtils.isEmpty(top)) {
			listView.showFooter();
		} else {
			listView.loadOk(false);
		}
		if (!loadMore) {
			headList.clear();
		}

		// TODO 焦点图
		if (!ParseUtil.listNotNull(template.getHead().getPosition())) {
			dissHead();
		} else {
			for (int position : template.getHead().getPosition()) {
				if (articleList.hasData(position)) {
					headList.addAll(articleList.getMap().get(position));
				}
			}
			if (headList.size() > 0) {
				showHead();
			} else {
				dissHead();
			}
		}
		// TODO 列表
		setValueForCatIndex(articleList, loadMore);
		LogHelper.logAndroidShowColumn(mContext, articleList.getTagName());
		impressAdv(articleList.getImpressionUrlList());
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
			headView.setData(headList, tagInfo);
			if (!headView.isShouldScroll()
					&& selfScrollViews.contains(headView)) {
				selfScrollViews.remove(headView);
			}
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

	/**
	 * 栏目首页赋值
	 * 
	 * @param indexArticle
	 */
	private void setValueForCatIndex(TagArticleList articleList,
			boolean loadMore) {
		if (loadMore && articleList.getMap().isEmpty()) {
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
			if (ParseUtil.listNotNull(list)) {
				adapter.setData(list);
				setSelection(-1);
			}
		} else {
			adapter.setData(list);
		}
	}

	public void setSelection(int height) {
		if (ViewsApplication.mConfig.getNav_hide() == 0) {
			listView.setSelection(0);
			return;
		}
		if (height == -1)
			height = IndexView.height;
		height = height < 2 ? 0 : height;
		if (currY == height)
			return;
		currY = height;
		// TODO 有默认的下拉刷新headview
		listView.setSelectionFromTop(2, height);
	}

	/**
	 * 统计广告
	 * 
	 * @param impressionUrlList
	 */
	private void impressAdv(List<String> impressionUrlList) {
		if (ParseUtil.listNotNull(impressionUrlList)) {
			for (String str : impressionUrlList) {
				AdvTools.requestImpression(str);
			}
		}
	}

	/**
	 * 下拉刷新，获取当前taginfo的更新时间是否变化
	 */
	private void getTagInfo() {
		OperateController.getInstance(mContext).getTagInfo(tagName, false,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagInfoList) {
							TagInfoList list = (TagInfoList) entry;
							if (ParseUtil.listNotNull(list.getList())) {
								checkUpdatetimeIsChanged(list.getList().get(0));
							} else {
								onRefreshed(false);
							}
						} else {
							onRefreshed(false);
						}
					}
				});
	}

	/**
	 * 判断更新时间是否改变,如果改变了，那么执行刷新
	 * 
	 * @param tagInfo
	 */
	private void checkUpdatetimeIsChanged(TagInfo tagInfo) {
		String columnUpdateTime = tagInfo.getColoumnupdatetime();
		String articleUpdateTime = tagInfo.getArticleupdatetime();
		String columnUpdateTimeCache = TagDataHelper.getCatIndexUpdateTime(
				mContext, tagName);
		String articleUpdateTimeCache = TagDataHelper.getCatArticleUpdateTime(
				mContext, tagName);
		if (TextUtils.equals(columnUpdateTimeCache, columnUpdateTime)
				&& TextUtils.equals(articleUpdateTimeCache, articleUpdateTime)) {
			// NOTE 未改变
			onRefreshed(true);
		} else {
			AppValue.updateTagInfoUpdateTime(tagInfo);
			if (indexViewPagerItem != null
					&& indexViewPagerItem.getTagInfo() != null) {
				indexViewPagerItem.getTagInfo().setColoumnupdatetime(
						articleUpdateTime);
				indexViewPagerItem.getTagInfo().setArticleupdatetime(
						articleUpdateTime);
			}
			if (indexViewPagerItem != null) {
				indexViewPagerItem.refresh(this);
			} else if (AppValue.mainProcess != null) {
				AppValue.mainProcess.getArticleList(
						AppValue.mainProcess.getCurrTagInfo(), null);
			}
		}
	}

	/**
	 * 刷新
	 */
	private void refresh() {
		getTagInfo();
	}

	/**
	 * 加载更多
	 */
	private void load() {
		if (indexViewPagerItem != null) {
			if (TextUtils.equals(_articleList.getViewbygroup(),
					TagArticleList.BY_INPUTTIME))
				indexViewPagerItem.loadMore(this, top, _articleList);
			else
				indexViewPagerItem.loadMore(this, top, null);
		} else if (AppValue.mainProcess != null) {
			loadMoreArticleList(AppValue.mainProcess.getCurrTagInfo());
		}
	}

	/**
	 * 先请求文章列表(与栏目首页联动);文章列表的数据库缓存会在接口里实现
	 * 
	 * @param tagInfo
	 */
	public void loadMoreArticleList(final TagInfo tagInfo) {
		if (tagInfo == null)
			return;
		OperateController.getInstance(mContext).getTagArticles(tagInfo, top,
				"", null, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							if (ParseUtil.listNotNull(articleList
									.getArticleList())) {
								loadMoreCatIndex(tagInfo);
							} else {
								listView.loadOk(false);
							}
							return;
						} else {
							onLoaded(false);
						}
					}
				});
	}

	private void loadMoreCatIndex(TagInfo catTagInfo) {
		if (catTagInfo == null)
			return;
		TagArticleList t = TextUtils.equals(_articleList.getViewbygroup(),
				TagArticleList.BY_INPUTTIME) ? _articleList : null;
		OperateController.getInstance(mContext).getTagIndex(catTagInfo, top,
				"", t, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							checkShouldInsertSubscribe(articleList, true);
						} else {
							onLoaded(false);
						}
					}
				});
	}

	private boolean checkHasMoreData(TagArticleList articleList) {
		if (!articleList.getMap().isEmpty()) {
			boolean hasData = false;
			for (int key : articleList.getMap().keySet()) {
				if (articleList.hasData(key)) {
					hasData = true;
					break;
				}
			}
			if (!hasData) {
				listView.loadOk(false);
			} else {
				onLoaded(true);
			}
			return hasData;
		}
		return false;
	}

	public BaseIndexHeadView getHeadView() {
		return headView;
	}

	public List<View> getSelfScrollViews() {
		return selfScrollViews;
	}

	public View fetchView() {
		return listView;
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (listView.isHeadShow()
				|| SlateApplication.mConfig.getNav_hide() == 0) {
			return;
		}
		// TODO 有默认的下拉刷新headview
		if (firstVisibleItem > 1) {
			((ViewsMainActivity) mContext)
					.callNavPadding(-IndexView.BAR_HEIGHT);
		} else {
			((ViewsMainActivity) mContext)
					.callNavPadding(getCurrY() - defaultY);
		}
	}

	private int getCurrY() {
		if (defaultView == null)
			return 0;
		int[] coor = new int[2];
		defaultView.getLocationOnScreen(coor);
		return coor[1];
	}

	public HideNavListView getListView() {
		return listView;
	}

}
