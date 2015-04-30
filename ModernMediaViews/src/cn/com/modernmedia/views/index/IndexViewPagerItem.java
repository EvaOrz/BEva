package cn.com.modernmedia.views.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.adapter.IndexViewPagerAdapter;
import cn.com.modernmedia.views.adapter.IndexViewPagerAdapter.CheckNavHideListener;
import cn.com.modernmedia.views.listener.LoadCallBack;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.solo.BaseChildCatHead;
import cn.com.modernmedia.views.solo.ChildCatHead;
import cn.com.modernmedia.views.util.DownloadTemplate;
import cn.com.modernmedia.views.util.DownloadTemplate.DownloadTemplateCallBack;
import cn.com.modernmedia.views.widget.VerticalGallery;
import cn.com.modernmedia.widget.RedProcess;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.TimeCollectUtil;

/**
 * 可滑动的首页view item
 * 
 * @author user
 * 
 */
public class IndexViewPagerItem implements Observer, CheckNavHideListener {
	protected Context mContext;
	private RelativeLayout view;
	private FrameLayout headFrame, frame;
	private View divider;
	private TagIndexListView indexListView;// 普通首页
	private VerticalGallery galleryView;// 图集

	protected TagInfo tagInfo;
	protected IndexViewPagerAdapter adapter;
	private View layout, loading, error;
	protected boolean hasSetData;
	protected TagInfoList childInfoList;
	private BaseChildCatHead childHead;
	private Template template;

	private int catHeadOffset;

	public IndexViewPagerItem(Context context, TagInfo tagInfo,
			IndexViewPagerAdapter adapter) {
		mContext = context;
		this.adapter = adapter;
		this.tagInfo = tagInfo;
		hasSetData = false;
		BaseChildCatHead.selectPosition = -1;
		init();
		if (SlateApplication.mConfig.getNav_hide() == 1)
			ViewsApplication.navObservable.addObserver(this);
	}

	@SuppressLint("InflateParams")
	private void init() {
		if (tagInfo == null)
			return;
		view = (RelativeLayout) LayoutInflater.from(mContext).inflate(
				R.layout.index_view_pager_item, null);
		view.setTag(this);
		if (ConstData.getAppId() == 20) {
			layout = view.findViewById(R.id.pager_item_layout_weekly);
			loading = view.findViewById(R.id.pager_item_loading_weekly);
			layout.setVisibility(View.VISIBLE);
		} else {
			layout = view.findViewById(R.id.pager_item_layout);
			loading = view.findViewById(R.id.pager_item_loading);
			error = view.findViewById(R.id.pager_item_error);
		}
		headFrame = (FrameLayout) view.findViewById(R.id.pager_item_head_frame);
		frame = (FrameLayout) view.findViewById(R.id.pager_item_frame);
		divider = view.findViewById(R.id.pager_item_head_frame_divider);
		if (error != null)
			error.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					fetchData("", false, false, null, null);
				}
			});
		showLoading();
	}

	/**
	 * 获取数据
	 */
	public void fetchData(String top, boolean isRefresh, boolean isLoadMore,
			LoadCallBack callBack, TagArticleList tagArticleList) {
		if (hasSetData)
			return;
		TimeCollectUtil.getInstance().savePageTime(
				TimeCollectUtil.EVENT_OPEN_COLUMN + tagInfo.getTagName(), true);
		if (tagInfo.showChildren()) {
			getChildren();
		} else {
			getArticleList(tagInfo, top, isRefresh, isLoadMore, callBack,
					tagArticleList);
		}
	}

	/**
	 * 刷新
	 */
	public void refresh(LoadCallBack callBack) {
		hasSetData = false;
		fetchData("", true, false, callBack, null);
	}

	/**
	 * 加载更多
	 * 
	 * @param callBack
	 * @param top
	 */
	public void loadMore(LoadCallBack callBack, String top,
			TagArticleList articleList) {
		hasSetData = false;
		fetchData(top, false, true, callBack, articleList);
	}

	/**
	 * 获取指定的子栏目数据
	 * 
	 */
	public void fecthSpecificChildData(int position, int catHeadOffset) {
		hasSetData = false;
		this.catHeadOffset = catHeadOffset;
		if (childInfoList.getList().size() > position) {
			TimeCollectUtil.getInstance().savePageTime(
					TimeCollectUtil.EVENT_OPEN_COLUMN + tagInfo.getTagName(),
					true);
			getCatIndex(childInfoList.getList().get(position));
		}
	}

	public void setHeadFrame(Template template) {
		if (childInfoList == null
				|| template.getCatHead().getCat_list_hold() == 0)
			return;
		if (childHead != null)
			return;
		headFrame.removeAllViews();
		divider.setVisibility(View.GONE);
		if (!checkShowCatHead()) {
			return;
		}
		// TODO 子栏目导航栏固定在顶部
		childHead = new ChildCatHead(mContext, this, template);
		childHead.setChildValues(childInfoList, tagInfo.getTagName());
		headFrame.addView(childHead.fetchView(), new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT));
		String bg = template.getCatHead().getColor();
		if (TextUtils.isEmpty(bg) || bg.equalsIgnoreCase("#FFFFFFFF")) {
			divider.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 判断是否需要显示cathead
	 * 
	 * @return
	 */
	protected boolean checkShowCatHead() {
		if (tagInfo.getTagLevel() == 2) {
			TagInfo parentTag = TagInfoListDb.getInstance(mContext)
					.getTagInfoByName(tagInfo.getParent(), "", true);
			return parentTag.showChildren();
		}
		return tagInfo.showChildren();
	}

	/**
	 * 获取子栏目
	 */
	protected void getChildren() {
		childInfoList = AppValue.ensubscriptColumnList
				.getChildHasSubscriptTagInfoList(tagInfo.getTagName());
		if (ParseUtil.listNotNull(childInfoList.getList())) {
			getCatIndex(childInfoList.getList().get(0));
		}
	}

	protected void getCatIndex(TagInfo tagInfo) {
		getArticleList(tagInfo, "", false, false, null, null);
	}

	/**
	 * 获取api请求时的tagname
	 * 
	 * @return
	 */
	protected String getApiTagName() {
		String tagName = tagInfo.getMergeName(true);
		return TextUtils.isEmpty(tagName) ? tagInfo.getTagName() : tagName;
	}

	/**
	 * 先请求文章列表(与栏目首页联动);文章列表的数据库缓存会在接口里实现
	 * 
	 * @param tagInfo
	 * @param top
	 * @param isRefresh
	 * @param isLoadMore
	 * @param callBack
	 * @param tagArticleList
	 */
	protected void getArticleList(final TagInfo tagInfo, final String top,
			final boolean isRefresh, final boolean isLoadMore,
			final LoadCallBack callBack, final TagArticleList tagArticleList) {
		if (!isRefresh && !isLoadMore)
			showLoading();
		OperateController.getInstance(mContext).getTagArticles(tagInfo, top,
				"", null, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList articleList = (TagArticleList) entry;
							if (ParseUtil.listNotNull(articleList
									.getArticleList())) {
								getCatIndex(tagInfo, top, isRefresh,
										isLoadMore, callBack, tagArticleList);
							} else {
								if (isLoadMore && indexListView != null
										&& indexListView.getListView() != null) {
									indexListView.getListView().loadOk(false);
								}
							}
							return;
						}
						if (!isRefresh && !isLoadMore)
							showError();
					}
				});
	}

	/**
	 * 获取栏目首页
	 * 
	 * @param isRefresh
	 *            是否刷新
	 * @param isLoadMore
	 *            是否加载更多
	 */
	private void getCatIndex(TagInfo tagInfo, String top,
			final boolean isRefresh, final boolean isLoadMore,
			final LoadCallBack callBack, TagArticleList tagArticleList) {
		this.tagInfo = tagInfo;
		OperateController.getInstance(mContext).getTagIndex(tagInfo, top, "",
				tagArticleList, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						boolean success = false;
						if (entry instanceof TagArticleList) {
							success = true;
							getTemplate((TagArticleList) entry, isLoadMore);
						} else {
							if (!isRefresh && !isLoadMore)
								showError();
						}
						loadCallBack(callBack, isRefresh, isLoadMore, success);
					}
				});
	}

	/**
	 * 通知加载完成
	 * 
	 * @param callBack
	 * @param success
	 */
	private void loadCallBack(LoadCallBack callBack, boolean isRefresh,
			boolean isLoadMore, boolean success) {
		if (callBack == null)
			return;
		if (isRefresh)
			callBack.onRefreshed(success);
		else if (isLoadMore)
			callBack.onLoaded(success);
	}

	/**
	 * 设置列表/图集数据
	 * 
	 * @param entry
	 */
	private void setValueForIndex(TagArticleList articleList, boolean loadMore) {
		disProcess();
		if (template.getList().getIs_gallery() == 1) {
			// TODO iweekly图集
			setHeadFrame(template);
			frame.removeAllViews();
			view.setBackgroundColor(Color.BLACK);
			galleryView = new VerticalGallery(mContext);
			frame.addView(galleryView.getView());
			galleryView.setData(articleList, template);
			TimeCollectUtil.getInstance().savePageTime(
					TimeCollectUtil.EVENT_OPEN_COLUMN + tagInfo.getTagName(),
					false);
		} else {
			// TODO 列表
			if (indexListView != null && loadMore) {
				indexListView.setData(articleList, childInfoList, true,
						template);
			} else {
				frame.removeAllViews();
				indexListView = new TagIndexListView(mContext, this);
				frame.addView(indexListView.fetchView());
				indexListView.setData(articleList, childInfoList, loadMore,
						template);
			}
		}
		hasSetData = true;
	}

	/**
	 * 获取模板
	 * 
	 * @param entry
	 * @param loadMore
	 */
	private void getTemplate(final TagArticleList articleList,
			final boolean loadMore) {
		if (template != null) {
			setValueForIndex(articleList, loadMore);
			return;
		}
		new DownloadTemplate(mContext, articleList,
				new DownloadTemplateCallBack() {

					@Override
					public void afterDownload(Template _template) {
						template = _template;
						setValueForIndex(articleList, loadMore);
					}
				}).getTemplate();
	}

	public List<View> getHeadView() {
		List<View> list = new ArrayList<View>();
		list.add(headFrame);
		if (indexListView != null) {
			list.addAll(indexListView.getSelfScrollViews());
		} else if (galleryView != null) {
			list.add(galleryView.getViewPager());
		}
		return list;
	}

	/**
	 * 显示loading图标
	 */
	public void showLoading() {
		layout.setVisibility(View.VISIBLE);
		loading.setVisibility(View.VISIBLE);
		if (loading instanceof RedProcess)
			((RedProcess) loading).start();
		if (error != null)
			error.setVisibility(View.GONE);
	}

	/**
	 * 显示错误提示
	 */
	public void showError() {
		layout.setVisibility(View.VISIBLE);
		if (error != null) {
			loading.setVisibility(View.GONE);
			error.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏process_layout
	 */
	public void disProcess() {
		layout.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		if (loading instanceof RedProcess)
			((RedProcess) loading).stop();
		if (error != null)
			error.setVisibility(View.GONE);
	}

	public TagInfo getTagInfo() {
		return tagInfo;
	}

	public void destory() {
		hasSetData = false;
		if (indexListView != null && indexListView.getHeadView() != null) {
			indexListView.getHeadView().stopRefresh();
		}
		if (SlateApplication.mConfig.getNav_hide() == 1)
			ViewsApplication.navObservable.deleteObserver(this);
	}

	public View fetchView() {
		return view;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (indexListView != null) {
			indexListView.setSelection(-1);
		} else if (galleryView != null) {
			galleryView.setSelection(IndexView.height);
		}
	}

	public int getCatHeadOffset() {
		return catHeadOffset;
	}

	@Override
	public void onResume() {
		if (indexListView != null) {
			indexListView.setStopCheckNav(false);
		} else if (galleryView != null) {
			galleryView.setStopCheckNav(false);
		}
	}

	@Override
	public void onPause() {
		if (indexListView != null) {
			indexListView.setStopCheckNav(true);
		} else if (galleryView != null) {
			galleryView.setStopCheckNav(true);
		}
	}

}
