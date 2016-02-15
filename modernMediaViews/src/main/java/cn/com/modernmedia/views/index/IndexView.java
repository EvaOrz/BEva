package cn.com.modernmedia.views.index;

import java.util.Observable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.column.book.TopMenuHorizontalScrollView;
import cn.com.modernmedia.views.index.head.BaseIndexHeadView;
import cn.com.modernmedia.views.model.TemplateIndexNavbar;
import cn.com.modernmedia.views.solo.BaseChildCatHead;
import cn.com.modernmedia.views.solo.BaseSoloIndexView;
import cn.com.modernmedia.views.solo.ChildIndexView;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.xmlparse.XMLDataSetForIndexNav;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页(栏目首页)
 * 
 * @author user
 * 
 */
public class IndexView extends BaseView implements FetchEntryListener {
	public static final int LIST = 1;// 普通列表
	public static final int CHILD = 3;// 子栏目
	public static final int ISSUE_LIST = 4;// 期刊列表

	public static int BAR_HEIGHT;
	public static int height;

	private Context mContext;
	private RelativeLayout navBar;
	private FrameLayout contain;
	private FrameLayout issueListLayout;
	private LinearLayout cover;
	private int currTag;
	private TopMenuHorizontalScrollView topMenu;

	// child view
	private TagIndexListView indexListView;
	private BaseSoloIndexView baseSoloIndexView;
	private IndexIssueListView issueListView;

	// 可滑动的栏目首页
	private IndexViewPager indexViewPager;
	// 首页导航栏模板数据
	private XMLDataSetForIndexNav dataSetForIndexNav;

	/**
	 * 通知列表滑动同步
	 * 
	 * @author zhuqiao
	 * 
	 */
	public static class NavObservable extends Observable {

		public void setData() {
			setChanged();
			notifyObservers();
		}

	}

	public IndexView(Context context) {
		this(context, null);
	}

	public IndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	@SuppressLint("InflateParams")
	private void init() {
		BAR_HEIGHT = mContext.getResources().getDimensionPixelSize(
				R.dimen.index_titlebar_height);
		addView(LayoutInflater.from(mContext)
				.inflate(R.layout.index_view, null), new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		initProcess();
		navBar = (RelativeLayout) findViewById(R.id.index_titleBar);
		contain = (FrameLayout) findViewById(R.id.index_contain);
		issueListLayout = (FrameLayout) findViewById(R.id.index_issuelist);
		indexViewPager = (IndexViewPager) findViewById(R.id.index_pager);
		cover = (LinearLayout) findViewById(R.id.index_cover);
		cover.setBackgroundColor(Color.TRANSPARENT);
		cover.setBackgroundDrawable(null);
		// top menu
		topMenu = (TopMenuHorizontalScrollView) findViewById(R.id.book_menu);

		initRes();
		if (SlateApplication.mConfig.getNav_hide() == 0
				&& SlateApplication.mConfig.getAlign_bar() == 0) {
			((LayoutParams) indexViewPager.getLayoutParams()).topMargin = BAR_HEIGHT;
		} else {
			callNavPadding(0);
		}
	}

	/**
	 * top_menu setData
	 * 
	 * @param list
	 */
	public void setTopMenuData(TagInfoList list) {
		topMenu.setData(list.getList());
	}

	/**
	 * 初始化导航栏资源
	 */
	private void initRes() {
		TemplateIndexNavbar template = ParseProperties.getInstance(mContext)
				.parseIndexNav();
		XMLParse xmlParse = new XMLParse(mContext, null);
		View view = xmlParse.inflate(template.getData(), null, "");
		navBar.addView(view);
		dataSetForIndexNav = xmlParse.getDataSetForIndexNav();
		dataSetForIndexNav.setData();
	}

	public View getColumn() {
		return dataSetForIndexNav.getColumn();
	}

	public View getFav() {
		return dataSetForIndexNav.getFav();
	}

	public View getNav() {
		return dataSetForIndexNav.getNavBar();
	}

	/**
	 * 设置首页标题
	 * 
	 * @param name
	 */
	public void setTitle(String name) {
		dataSetForIndexNav.setTitle(name);

	}

	public void setTopMenuSelect(TagInfo tag) {
		topMenu.setSelectedItemForChild(tag.getTagName());
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

	@Override
	public void setData(Entry entry) {
		if (entry == null)
			return;
		((CommonMainActivity) mContext).clearScrollView();
		// TODO 直接初始化，防止headview添加多次
		indexListView = new TagIndexListView(mContext);
		contain.removeAllViews();
		contain.addView(indexListView.fetchView());
		issueListLayout.setVisibility(View.GONE);
		if (entry instanceof TagArticleList)
			indexListView.setData((TagArticleList) entry, null);
		currTag = LIST;

		baseSoloIndexView = null;
		issueListView = null;
	}

	/**
	 * 设置子栏目catid
	 * 
	 * @param childInfoList
	 */
	public void setDataForChild(TagInfoList childInfoList) {
		((CommonMainActivity) mContext).clearScrollView();
		contain.removeAllViews();
		baseSoloIndexView = new ChildIndexView(mContext);
		baseSoloIndexView.setData(childInfoList);
		contain.addView(baseSoloIndexView.fetchView());
		currTag = CHILD;
		issueListLayout.setVisibility(View.GONE);
		indexListView = null;
		issueListView = null;
	}

	/**
	 * 设置期刊列表
	 */
	public void setDataForIssueList() {
		indexViewPager.setVisibility(View.GONE);
		contain.setVisibility(View.GONE);
		issueListLayout.setVisibility(View.VISIBLE);
		issueListView = new IndexIssueListView(mContext);
		issueListLayout.addView(issueListView.fetchView());
		issueListView.setData(null);
		currTag = ISSUE_LIST;

		baseSoloIndexView = null;
		indexListView = null;
	}

	/**
	 * 显示首页滑屏view
	 */
	public void setDataForIndexPager() {
		issueListLayout.setVisibility(View.GONE);
		indexViewPager.setVisibility(View.VISIBLE);
		indexViewPager.setCatList();
	}

	/**
	 * 定位首页滑屏
	 * 
	 * @param tagName
	 * @param isUri
	 *            是否来自uri;如果是，当找不到的时候需要添加这个栏目，否则，显示第一个栏目
	 */
	public void checkPositionIfPager(String tagName, boolean isUri) {
		if (indexViewPager != null) {
			issueListLayout.setVisibility(View.GONE);
			indexViewPager.setVisibility(View.VISIBLE);
			indexViewPager.checkPosition(tagName, isUri);
		}
	}

	public void stopAuto() {
		if (indexListView != null
				&& indexListView.getHeadView() != null) {
			indexListView.getHeadView().stopRefresh();
		}
	}

	public void reStorePullResfresh() {
		// if (currTag == SOLO && baseSoloIndexView instanceof SoloIndexView) {
		// ((SoloIndexView) baseSoloIndexView).reStoreRefresh();
		// }
	}

	/**
	 * 设置导航栏分割线的透明度(艺术新闻效果)
	 * 
	 * @param alpha
	 */
	public void setShadowAlpha(int alpha) {
		// navShadow.setAlpha(alpha);
		dataSetForIndexNav.setAlpha(alpha);
	}

	@Override
	protected void reLoad() {
		if (currTag == ISSUE_LIST && issueListView != null) {
			issueListView.setData(null);
		} else if (mContext instanceof CommonMainActivity) {
			((CommonMainActivity) mContext).reLoadData();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (cover.getVisibility() == View.VISIBLE) {
			((CommonMainActivity) mContext).getScrollView().IndexClick();
			return true;
		}
		if (checkHead(ev, baseSoloIndexView)) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private boolean checkHead(MotionEvent ev, BaseSoloIndexView soloIndexView) {
		if (soloIndexView == null) {
			return false;
		}
		BaseIndexHeadView headView = soloIndexView.getHeadView();
		if (headView != null && soloIndexView.getChildSize() > 1) {
			Rect rect = new Rect();
			headView.getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY())) {
				soloIndexView.setIntercept(true);
				return true;
			}
		}
		BaseChildCatHead catHead = soloIndexView.getCatHead();
		if (catHead != null && catHead.fetchView() != null) {
			Rect rect = new Rect();
			catHead.fetchView().getGlobalVisibleRect(rect);
			if (rect.contains((int) ev.getX(), (int) ev.getY())) {
				soloIndexView.setIntercept(true);
				return true;
			}
		}
		baseSoloIndexView.setIntercept(false);
		return false;
	}

	public void callNavPadding(int padding) {
		dataSetForIndexNav.callNavPadding(padding);
	}

	public boolean isNavShow() {
		return dataSetForIndexNav.isNavShow();
	}

	/**
	 * 栏目内容是网页时，需判断是否是起始栏目内容，若不是，点击返回键时则返回到上一级内容，直至起始栏目内容
	 * 
	 * @return
	 */
	public boolean doGoBack() {
		return indexViewPager.getVisibility() == View.VISIBLE
				&& indexViewPager.doGoBack();
	}
}
