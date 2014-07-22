package cn.com.modernmedia.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainAddFavActivity;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.SizeCallBackForButton;
import cn.com.modernmedia.model.Cat;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.views.column.ColumnView;
import cn.com.modernmedia.views.index.IndexView;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmedia.widget.MainHorizontalScrollView.FecthViewSizeListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediausermodel.help.BindFavToUserImplement;
import cn.com.modernmediausermodel.util.UserTools;

/**
 * 封装了view的首页
 * 
 * @author user
 * 
 */
public abstract class ViewsMainActivity extends CommonMainAddFavActivity {
	protected IndexView indexView;
	protected ColumnView columnView;// 栏目列表页
	private View columnButton, favButton;
	private FrameLayout userView;
	// 还得考虑只有独立栏目
	private Cat cat;

	@Override
	protected void onResume() {
		super.onResume();
		if (indexView != null) {
			indexView.setAuto(true);
		}
		ViewsApplication.readedArticles = ReadDb.getInstance(this)
				.getAllReadArticle();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (indexView != null) {
			indexView.setAuto(false);
		}
	}

	@Override
	protected void init() {
		setContentView(R.layout.main_activity);
		initProcess();
		userView = (FrameLayout) findViewById(R.id.main_fav);
		userView.addView(fetchRightView());
		columnView = (ColumnView) findViewById(R.id.main_column);
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);
		indexView = new IndexView(this);
		columnButton = indexView.getColumn();
		favButton = indexView.getFav();

		View leftView = LayoutInflater.from(this).inflate(R.layout.scroll_left,
				null);
		leftView.setTag(MainHorizontalScrollView.LEFT_ENLARGE_WIDTH);
		View rightView = LayoutInflater.from(this).inflate(
				R.layout.scroll_right, null);
		rightView.setTag(MainHorizontalScrollView.RIGHT_ENLARGE_WIDTH);
		final View[] children = new View[] { leftView, indexView, rightView };
		scrollView.initViews(children, new SizeCallBackForButton(columnButton),
				columnView, userView);
		scrollView.setButtons(columnButton, favButton);
		scrollView.setListener(new ScrollCallBackListener() {

			@Override
			public void showIndex(int index) {
				indexView.showCover(index == 0);
				indexView.reStorePullResfresh();
				showView(index);
			}
		});
		columnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scrollView.clickButton(true);
			}
		});
		favButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scrollView.clickButton(false);
			}
		});
		scrollView.setViewListener(new FecthViewSizeListener() {

			@Override
			public void fetchViewWidth(int width) {
				if (columnView != null) {
					columnView.setViewWidth(width);
				}
				setViewWidth(width);
			}
		});
		setBindFavToUserListener(new BindFavToUserImplement(this));
	}

	/**
	 * 右边栏view
	 * 
	 * @return
	 */
	protected abstract View fetchRightView();

	@Override
	public void setDataForColumn(Entry entry) {
		columnView.setData(entry);
		if (entry instanceof Cat)
			cat = (Cat) entry;
	}

	@Override
	public MainHorizontalScrollView getScrollView() {
		return scrollView;
	}

	@Override
	public BaseView getIndexView() {
		return indexView;
	}

	/**
	 * 当前请求的item的title(子栏目取tagname,else取cname)
	 * 
	 * @param item
	 */
	public void setTitle(CatItem item) {
		if (CommonApplication.manage != null) {
			CommonApplication.manage.getProcess().setColumnId(item.getId());
		}
		String title = "";
		if (item.getParentId() == -1) {
			// TODO 父栏目
			title = item.getCname();
		} else if (DataHelper.columnTitleMap.containsKey(item.getParentId())) {
			title = DataHelper.columnTitleMap.get(item.getParentId()) + " · "
					+ item.getTagname();
		}
		setIndexTitle(title);
	}

	@Override
	public void setIndexTitle(String name) {
		indexView.setTitle(name);
	}

	@Override
	public void setDataForIndex(Entry entry) {
		if (!ConstData.isIndexPager())
			indexView.setData(entry);
	}

	/**
	 * 显示独立栏目
	 */
	@Override
	public void showSoloChildCat(int parentId) {
		if (CommonApplication.manage != null)
			CommonApplication.manage.getProcess().setColumnId(parentId);
		if (!ConstData.isIndexPager())
			indexView.setDataForSolo(parentId);
	}

	/**
	 * 显示子栏目
	 * 
	 * @param parentId
	 */
	public void showChildCat(int parentId) {
		if (CommonApplication.manage != null)
			CommonApplication.manage.getProcess().setColumnId(parentId);
		if (!ConstData.isIndexPager())
			indexView.setDataForChild(parentId);
	}

	@Override
	public void showIndexPager() {
		indexView.setDataForIndexPager();
	}

	/**
	 * 首页滑屏，直接定位到具体的栏目
	 * 
	 * @param item
	 */
	public void clickItemIfPager(int id) {
		indexView.checkPositionIfPager(id);
	}

	@Override
	public String getUid() {
		return UserTools.getUid(this);
	}

	public void setShadowAlpha(int alpha) {
		indexView.setShadowAlpha(alpha);
	}

	/**
	 * 显示第几屏
	 * 
	 * @param index
	 */
	protected void showView(int index) {
	}

	/**
	 * 设置左右两屏宽度
	 * 
	 * @param width
	 */
	protected void setViewWidth(int width) {
	}

	public Cat getCat() {
		return cat;
	}

	/**
	 * 艺术新闻有个默认headview
	 * 
	 * @return
	 */
	public View getDefaultHeadView() {
		return null;
	}

	/**
	 * 跳转至画报详情
	 */
	public void gotoGallertDetailActivity(CatIndexArticle catIndexArticle,
			int position) {
	}

	@Override
	public String[] getFragmentTags() {
		return null;
	}

	@Override
	protected void exitApp() {
		super.exitApp();
		ViewsApplication.exit();
	}

}
