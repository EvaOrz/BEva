package cn.com.modernmedia.views.index;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.IssueList;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.adapter.IssueListAdapter;
import cn.com.modernmedia.views.adapter.LohasIssueListAdapter;
import cn.com.modernmedia.views.model.IndexNavParm;
import cn.com.modernmedia.views.model.IndexNavParm.IndexNavTitleParm;
import cn.com.modernmedia.views.model.IssueListParm;
import cn.com.modernmedia.views.solo.BaseChildCatHead;
import cn.com.modernmedia.views.solo.BaseSoloIndexView;
import cn.com.modernmedia.views.solo.ChildIndexView;
import cn.com.modernmedia.views.solo.SoloIndexView;
import cn.com.modernmedia.views.util.ParseProperties;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.IssueListView;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页(栏目首页)
 * 
 * @author user
 * 
 */
public class IndexView extends BaseView implements FetchEntryListener {
	public static final int LIST = 1;// 普通列表
	public static final int SOLO = 2;// 独立栏目
	public static final int CHILD = 3;// 子栏目

	private Context mContext;
	private View navBar;
	private ImageView column, fav, navShadow, titleImage;
	private TextView title;
	private FrameLayout contain;
	private LinearLayout cover;
	private int currTag;

	// child view
	private IndexListView indexListView;
	private BaseSoloIndexView baseSoloIndexView;
	private IssueListView issueListView;
	private IssueListAdapter issueListAdapter;

	// 可滑动的栏目首页
	private IndexViewPager indexViewPager;

	public IndexView(Context context) {
		this(context, null);
	}

	public IndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		addView(LayoutInflater.from(mContext)
				.inflate(R.layout.index_view, null), new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		initProcess();
		navBar = findViewById(R.id.index_titleBar);
		navBar.getLayoutParams().height = CommonApplication.width * 88 / 640;
		column = (ImageView) findViewById(R.id.index_titleBar_column);
		fav = (ImageView) findViewById(R.id.index_titleBar_fav);
		title = (TextView) findViewById(R.id.index_titleBar_title);
		navShadow = (ImageView) findViewById(R.id.index_bar_divider);
		titleImage = (ImageView) findViewById(R.id.index_titleBar_title_img);
		contain = (FrameLayout) findViewById(R.id.index_contain);
		issueListView = (IssueListView) findViewById(R.id.index_issue_list_view);
		indexViewPager = (IndexViewPager) findViewById(R.id.index_pager);
		cover = (LinearLayout) findViewById(R.id.index_cover);
		cover.setBackgroundColor(Color.TRANSPARENT);
		cover.setBackgroundDrawable(null);

		initRes();
		initIssueListView();
	}

	/**
	 * 初始化导航栏资源
	 */
	private void initRes() {
		IndexNavParm parm = ParseProperties.getInstance(mContext)
				.parseIndexNav();
		V.setImage(column, parm.getNav_column());
		V.setImage(fav, parm.getNav_fav());
		V.setImage(navShadow, parm.getNav_shadow());
		V.setImage(navBar, parm.getNav_bg());

		int rule = parm.getType().equals(V.IWEEKLY) ? RelativeLayout.CENTER_HORIZONTAL
				: RelativeLayout.CENTER_IN_PARENT;
		((LayoutParams) title.getLayoutParams()).addRule(rule);
		((LayoutParams) titleImage.getLayoutParams()).addRule(rule);

		if (parm.getShow_title() == 0) {
			title.setVisibility(View.GONE);
		}
		IndexNavTitleParm titleParm = parm.getTitleParm();
		if (titleParm.getTitle_top_padding() != 0) {
			((LayoutParams) title.getLayoutParams()).topMargin = titleParm
					.getTitle_top_padding() * CommonApplication.height / 1280;
		}
		if (titleParm.getTitle_textsize() != 0) {
			title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
					titleParm.getTitle_textsize());
		}
		if (!TextUtils.isEmpty(titleParm.getTitle_color())) {
			title.setTextColor(Color.parseColor(titleParm.getTitle_color()));
		}

		if (parm.getTitleParm().getShow_shadow() == 1) {
			// TODO 显示阴影
			title.setShadowLayer(1, 0, -0.5f, Color.BLACK);
		}

		if (!TextUtils.isEmpty(parm.getNav_title_img())) {
			titleImage.setVisibility(View.VISIBLE);
			V.setImage(titleImage, parm.getNav_title_img());
			if (parm.getNav_title_img_top_padding() != 0) {
				((LayoutParams) titleImage.getLayoutParams()).topMargin = parm
						.getNav_title_img_top_padding()
						* CommonApplication.height / 1280;
			}
		}
	}

	public View getColumn() {
		return column;
	}

	public View getFav() {
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

	@Override
	public void setData(Entry entry) {
		if (entry == null)
			return;
		((CommonMainActivity) mContext).clearScrollView();
		if (indexListView == null) {
			indexListView = new IndexListView(mContext);
			contain.removeAllViews();
			contain.addView(indexListView.fetchView());
		}
		indexListView.setData(entry);
		currTag = LIST;

		baseSoloIndexView = null;
		showIssueListView(false);
	}

	/**
	 * 设置子栏目catid
	 * 
	 * @param parentId
	 */
	public void setDataForChild(int parentId) {
		((CommonMainActivity) mContext).clearScrollView();
		contain.removeAllViews();
		baseSoloIndexView = new ChildIndexView(mContext);
		baseSoloIndexView.setData(parentId);
		contain.addView(baseSoloIndexView.fetchView());
		currTag = CHILD;

		indexListView = null;
		showIssueListView(false);
	}

	/**
	 * 设置独立栏目catid
	 * 
	 * @param parentId
	 */
	public void setDataForSolo(int parentId) {
		((CommonMainActivity) mContext).clearScrollView();
		contain.removeAllViews();
		baseSoloIndexView = new SoloIndexView(mContext);
		baseSoloIndexView.setData(parentId);
		contain.addView(baseSoloIndexView.fetchView());
		currTag = SOLO;

		indexListView = null;
		showIssueListView(false);
	}

	/**
	 * 显示首页滑屏view
	 */
	public void setDataForIndexPager() {
		indexViewPager.setVisibility(View.VISIBLE);
		Entry entry = ((ViewsMainActivity) mContext).getCat();
		if (entry == null)
			entry = CommonApplication.soloColumn;
		indexViewPager.setCatList(entry);
	}

	/**
	 * 定位首页滑屏
	 * 
	 * @param id
	 */
	public void checkPositionIfPager(int id) {
		if (indexViewPager != null) {
			indexViewPager.checkPosition(id);
		}
	}

	/**
	 * 是否启动自动切换
	 * 
	 * @param start
	 */
	public void setAuto(boolean start) {
		if (currTag == LIST && indexListView != null
				&& indexListView.getHeadView() != null) {
			if (start)
				indexListView.getHeadView().startRefresh();
			else
				indexListView.getHeadView().stopRefresh();
		}
	}

	public void reStorePullResfresh() {
		if (currTag == SOLO && baseSoloIndexView instanceof SoloIndexView) {
			((SoloIndexView) baseSoloIndexView).reStoreRefresh();
		}
	}

	/**
	 * 设置导航栏分割线的透明度(艺术新闻效果)
	 * 
	 * @param alpha
	 */
	public void setShadowAlpha(int alpha) {
		navShadow.setAlpha(alpha);
	}

	@Override
	protected void reLoad() {
		if (issueListView.getVisibility() == View.VISIBLE) {
			issueListView.startFecth();
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
		IndexHeadView headView = soloIndexView.getHeadView();
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

	/**
	 * 首页是否显示为期刊列表
	 * 
	 * @param isShow
	 */
	public void showIssueListView(boolean isShow) {
		if (isShow) {
			issueListView.setVisibility(View.VISIBLE);
			contain.setVisibility(View.GONE);
			issueListView.startFecth();
		} else {
			issueListView.setVisibility(View.GONE);
			contain.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化期刊列表
	 * 
	 */
	private void initIssueListView() {
		IssueListParm parm = ParseProperties.getInstance(mContext)
				.parseIssueList();
		View footerView = issueListView.getFootView();
		View layout = footerView.findViewById(R.id.footer_contain);
		// 添加背景颜色
		V.setImage(layout, parm.getFooter_bg());
		// 字体颜色
		if (!TextUtils.isEmpty(parm.getFooter_text_color())) {
			((TextView) footerView.findViewById(R.id.footer_text))
					.setTextColor(Color.parseColor(parm.getFooter_text_color()));
		}
		if (V.ILOHAS.equals(parm.getType())) {
			issueListAdapter = new LohasIssueListAdapter(mContext, parm);
		} else {
			issueListAdapter = new IssueListAdapter(mContext, parm);
		}
		issueListView.setAdapter(issueListAdapter);
		issueListView.setEntryListener(new FetchEntryListener() {

			@Override
			public void setData(Entry entry) {
				if (issueListAdapter != null && entry != null
						&& entry instanceof IssueList)
					issueListAdapter.setData((IssueList) entry);
			}
		});
	}

	/**
	 * 获取当前期刊列表的view
	 * 
	 * @return
	 */
	public IssueListView getIssueListView() {
		return issueListView;
	}
}
