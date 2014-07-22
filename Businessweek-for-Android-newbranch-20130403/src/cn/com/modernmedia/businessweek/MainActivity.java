package cn.com.modernmedia.businessweek;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.com.modernmedia.businessweek.widget.ColumnView;
import cn.com.modernmedia.businessweek.widget.FavView;
import cn.com.modernmedia.businessweek.widget.IndexView;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.SizeCallBackForButton;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediasolo.CommonSoloActivity;
import cn.com.modernmediausermodel.help.UserHelper;
import cn.com.modernmediausermodel.model.User;
import cn.com.modernmediausermodel.util.UserDataHelper;

/**
 * 首页
 * 
 * @author ZhuQiao
 * 
 */
public class MainActivity extends CommonSoloActivity {
	private IndexView indexView;
	private ColumnView columnView;// 栏目列表页
	private Button columnButton, favButton;
	private FavView favView;

	@Override
	protected void onResume() {
		super.onResume();
		if (indexView != null) {
			indexView.setAuto(true);
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
		favView = (FavView) findViewById(R.id.main_fav);
		indexView = new IndexView(this);
		columnButton = indexView.getColumn();
		favButton = indexView.getFav();

		View leftView = new View(this);// 为了显示左边页面，设置透明区
		leftView.setBackgroundColor(Color.TRANSPARENT);
		View rightView = new View(this);
		rightView.setBackgroundColor(Color.TRANSPARENT);// 为了显示右边页面，设置透明区
		final View[] children = new View[] { leftView, indexView, rightView };
		scrollView.initViews(children, new SizeCallBackForButton(columnButton),
				columnView, favView);
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
		autoLogin();
	}

	/**
	 * 自动登录
	 */
	private void autoLogin() {
		User user = UserDataHelper.getUserLoginInfo(this);
		if (user != null) {
			if (columnView != null)
				columnView.afterLogin(user.getUserName());
		}
	}

	@Override
	public void setDataForColumn(Entry entry) {
		columnView.setData(entry);
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

	public void gotoLoginActivity() {
		Intent intent = new Intent();
		if (UserDataHelper.getUserLoginInfo(this) != null) {
			intent.setClass(this, UserInfoActivity.class);
		} else {
			intent.setClass(this, LoginActivity.class);
		}
		startActivityForResult(intent, LOGIN_REQUEST_CODE);
		overridePendingTransition(R.anim.right_in, R.anim.zoom_out);
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

	@Override
	public void doAfterLogin() {
		User user = UserDataHelper.getUserLoginInfo(this);
		if (user == null) {
			if (columnView != null)
				columnView.afterLogin(getString(R.string.login));
			// 登出
			MyApplication.notifyFav();
		} else {
			// 登录
			if (columnView != null)
				columnView.afterLogin(user.getUserName());
			List<FavoriteItem> list = FavDb.getInstance(this)
					.getUserUnUpdateFav(user.getUid(), false);
			if (ParseUtil.listNotNull(list)) {
				UserHelper.updateFav(this);
			} else {
				UserHelper.getFav(this);
			}
		}
	}

	public void setColumnPosition(int position) {
		if (columnView != null)
			columnView.setAdapterPosition(position);
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
