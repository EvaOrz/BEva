package cn.com.modernmedia.businessweek;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.modernmedia.businessweek.widget.ColumnView;
import cn.com.modernmedia.businessweek.widget.IndexView;
import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.SizeCallBackForButton;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediasolo.CommonSoloActivity;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;
import cn.com.modernmediausermodel.widget.UserCenterView;

/**
 * 首页
 * 
 * @author ZhuQiao
 * 
 */
public class MainActivity extends CommonSoloActivity {
	private IndexView indexView;
	private ColumnView columnView;// 栏目列表页
	private View columnButton, favButton;
	private UserCenterView userCenterView;

	@Override
	protected void onResume() {
		super.onResume();
		if (indexView != null) {
			indexView.setAuto(true);
		}
		// 用户中心页数据变化时，刷新页面
		if (userCenterView != null) {
			userCenterView.reLoad();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (indexView != null) {
			indexView.setAuto(false);
		}
	}

	@Override
	public void init() {
		clearMap();
		setContentView(R.layout.main);
		initProcess();
		columnView = (ColumnView) findViewById(R.id.main_column);
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);
		userCenterView = (UserCenterView) findViewById(R.id.main_fav);
		indexView = new IndexView(this);
		columnButton = indexView.getColumn();
		favButton = indexView.getFav();

		View leftView = LayoutInflater.from(this).inflate(R.layout.scroll_left,
				null);
		View rightView = LayoutInflater.from(this).inflate(
				R.layout.scroll_right, null);
		final View[] children = new View[] { leftView, indexView, rightView };
		scrollView.initViews(children, new SizeCallBackForButton(columnButton),
				columnView, userCenterView);
		scrollView.setButtons(columnButton, favButton);
		scrollView.setListener(new ScrollCallBackListener() {

			@Override
			public void showIndex(int index) {
				indexView.showCover(index == 0);
				indexView.reStorePullResfresh();
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
	}

	@Override
	public void setDataForColumn(Entry entry) {
		columnView.setData(entry);
		userCenterView.setIssue(getIssue());
	}

	@Override
	public void setDataForIndex(Entry entry) {
		indexView.setData(entry);
	}

	@Override
	public MainHorizontalScrollView getScrollView() {
		return scrollView;
	}

	@Override
	protected BaseView getIndexView() {
		return indexView;
	}

	@Override
	protected Class<?> getArticleActivity() {
		return ArticleActivity.class;
	}

	@Override
	protected String getUid() {
		User user = UserDataHelper.getUserLoginInfo(this);
		return user == null ? ConstData.UN_UPLOAD_UID : user.getUid();
	}

	@Override
	public void setIndexTitle(String name) {
		indexView.setTitle(name);
	}

	/**
	 * 显示独立栏目
	 * 
	 * @param parentId
	 */
	@Override
	public void showSoloChildCat(int parentId) {
		super.showSoloChildCat(parentId);
		indexView.setDataForSolo(parentId);
	}

	/**
	 * 显示子栏目
	 * 
	 * @param parentId
	 */
	public void showChildCat(int parentId) {
		setColumnId(parentId + "");
		indexView.setDataForChild(getIssue(), parentId);
	}

	/**
	 * 给scrollview设置正在下拉刷新
	 * 
	 * @param isPulling
	 */
	public void setPulling(boolean isPulling) {
		scrollView.setPassToUp(isPulling);
	}

	public void setColumnPosition(int position) {
		if (columnView != null)
			columnView.setAdapterPosition(position);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.exit();
	}

	@Override
	public String[] getFragmentTags() {
		return IndexView.TAGS;
	}

	@Override
	public String getActivityName() {
		return MainActivity.class.getName();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}
