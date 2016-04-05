package cn.com.modernmedia.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.SizeCallBackForButton;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagArticleListDb;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.EnsubscriptHelper;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.views.column.NewColumnView;
import cn.com.modernmedia.views.column.book.BookActivity;
import cn.com.modernmedia.views.index.IndexView;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.MainHorizontalScrollView;
import cn.com.modernmedia.widget.MainHorizontalScrollView.FecthViewSizeListener;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;
import cn.com.modernmediaslate.unit.SlateDataHelper;
import cn.com.modernmediaslate.unit.Tools;
import cn.com.modernmediausermodel.api.UserOperateController;
import cn.com.modernmediausermodel.listener.UserFetchEntryListener;
import cn.com.modernmediausermodel.util.UserCentManager;
import cn.com.modernmediausermodel.widget.UserCenterView;

/**
 * 封装了view的首页
 * 
 * @author user
 * 
 */
public abstract class ViewsMainActivity extends CommonMainActivity {
	// bac
	public static final int SELECT_COLUMN_REQUEST_CODE = 200;
	public static final int SELECT_CHILD_COLUMN_REQUEST_CODE = 201;
	public static final int SELECT_COLUMN_LOGIN_REQUEST_CODE = 202;
	public static final int SELECT_CHILD_COLUMN_LOGIN_REQUEST_CODE = 203;

	public static final int BOOK_ACTIVITY = 204;

	protected IndexView indexView;
	protected NewColumnView columnView;// 栏目列表页
	private View columnButton, favButton;
	private FrameLayout userView;
	public LinearLayout musicMenuContainer;// 音乐播放悬浮窗容器
	protected UserCenterView userCenterView; // 用户中心页，默认右边页
	private int currentPage = 0; // 当期处于第几屏

	private LifecycleObservable lifecycleObservable = new LifecycleObservable();

	AudioManager audioManager;
	private int volumeInApp;// 进入应用时的音量

	public enum LifeCycle {
		RESUME, PAUSE, STOP;
	}

	public class LifecycleObservable extends Observable {

		public void setData(LifeCycle lifeCycle) {
			setChanged();
			notifyObservers(lifeCycle);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewsApplication.readedArticles = ReadDb.getInstance(this)
				.getAllReadArticle();
		lifecycleObservable.deleteObservers();

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		volumeInApp = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (SlateApplication.loginStatusChange) {
			String uid = Tools.getUid(this);
			if (TextUtils.isEmpty(uid)
					|| TextUtils.equals(uid, SlateApplication.UN_UPLOAD_UID))
				refreshSubscript("", -1);
			else {
				getUserSubscript("", -1);
				Log.e("获取用户订阅列表", Tools.getUid(this));
			}

			SlateApplication.loginStatusChange = false;
		}
		// 用户中心页数据变化时，刷新页面
		if (userCenterView != null && currentPage == 2) {
			userCenterView.reLoad();
		}

		lifecycleObservable.setData(LifeCycle.RESUME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (indexView != null) {
			indexView.stopAuto();
		}
		lifecycleObservable.setData(LifeCycle.PAUSE);
	}

	public LifecycleObservable getLifecycleObservable() {
		return lifecycleObservable;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void init() {
		setContentView(R.layout.main_activity);
		initProcess();
		userView = (FrameLayout) findViewById(R.id.main_fav);
		userView.addView(fetchRightView());
		/** 电台悬浮窗容器 **/
		musicMenuContainer = (LinearLayout) findViewById(R.id.music_menu_container);

		columnView = (NewColumnView) findViewById(R.id.main_column);
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
		scrollView
				.setIntercept(CommonApplication.mConfig.getIs_index_pager() == 1);
		scrollView.setListener(new ScrollCallBackListener() {

			@Override
			public void showIndex(int index) {
				indexView.showCover(index == 0);
				indexView.reStorePullResfresh();
				showView(index);
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
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				UserCentManager.getInstance(ViewsMainActivity.this)
						.addLoginCoinCent();
			}
		}, 3000);
		if (SlateApplication.mConfig.getHas_coin() == 0) {
			UserOperateController.getInstance(this).getActionRules(
					new UserFetchEntryListener() {

						@Override
						public void setData(Entry entry) {
						}
					});
		}
	}

	@Override
	public void setDataForColumn() {
		TagInfoList columnTags = new TagInfoList();
		List<TagInfo> list = new ArrayList<TagInfo>();
		for (int i = 0; i < AppValue.ensubscriptColumnList.getList().size(); i++) {
			if (AppValue.ensubscriptColumnList.getList().get(i).getIsFix() == 1) {
				list.add(AppValue.ensubscriptColumnList.getList().get(i));
			}
		}
		columnTags.setList(list);
		columnView.setData(columnTags);
		// 塞top_menu数据
		indexView.setTopMenuData(AppValue.bookColumnList);
	}

	/**
	 * 右边栏view，默认用用户中心页
	 * 
	 * @return
	 */
	protected View fetchRightView() {
		userCenterView = new UserCenterView(this);
		return userCenterView;
	}

	@Override
	public void showIndexPager() {
		ViewsApplication.autoScrollObservable.clearAll();
		indexView.setDataForIndexPager();
	}

	@Override
	public MainHorizontalScrollView getScrollView() {
		return scrollView;
	}

	@Override
	public BaseView getIndexView() {
		return indexView;
	}

	@Override
	public void setIndexTitle(String name) {
		indexView.setTitle(name);
	}

	@Override
	public void notifyColumnAdapter(String tagName) {
		super.notifyColumnAdapter(tagName);
		if (ViewsApplication.mConfig.getIs_navbar_bg_change() == 1
				&& indexView.getNav() != null
				&& ParseUtil.mapContainsKey(DataHelper.columnColorMap, tagName)) {
			indexView.getNav().setBackgroundColor(
					DataHelper.columnColorMap.get(tagName));
		}
	}

	/**
	 * 显示期刊列表
	 */
	public void showIssueListView() {
		indexView.setDataForIssueList();
	}

	/**
	 * 首页滑屏，直接定位到具体的栏目
	 * 
	 * @param tagName
	 * @param isUri
	 *            是否来自uri;如果是，当找不到的时候需要添加这个栏目，否则，显示第一个栏目
	 */
	@Override
	public void clickItemIfPager(String tagName, boolean isUri) {
		indexView.checkPositionIfPager(tagName, isUri);
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
		currentPage = index;
		if (index == 2 && userCenterView != null) {
			userCenterView.reLoad();
		}
	}

	/**
	 * 设置左右两屏宽度
	 * 
	 * @param width
	 */
	protected void setViewWidth(int width) {
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
	public void gotoGallertDetailActivity(List<ArticleItem> articleList,
			ArticleItem item, int position) {
		if (ConstData.getAppId() == 20) {
		} else {
			V.clickSlate(this, item, ArticleType.Default);
		}
	}

	/**
	 * 跳转至选择栏目页 bac: SelectChildColumnActivity
	 */
	public void gotoSelectColumnActivity() {
		Intent intent = new Intent(this, BookActivity.class);
		startActivityForResult(intent, BOOK_ACTIVITY);
	}

	/**
	 * 从子栏目head点击进入 bac: SelectChildColumnActivity
	 * 
	 * @param tagInfo
	 */
	public void gotoSelectChildColumnActvity(String tagName) {
		Intent intent = new Intent(this, BookActivity.class);
		startActivityForResult(intent, BOOK_ACTIVITY);
	}

	@Override
	protected void notifyRead() {
		ViewsApplication.readedArticles = ReadDb.getInstance(this)
				.getAllReadArticle();
		List<Integer> list = new ArrayList<Integer>();
		if (ViewsApplication.lastestArticleId != null) {
			for (Integer id : ViewsApplication.lastestArticleId.getUnReadedId()) {
				if (ViewsApplication.readedArticles.contains(id)) {
					list.add(id);
				}
			}
		}
		if (!list.isEmpty()) {
			for (Integer id : list) {
				Map<String, ArrayList<Integer>> map = ViewsApplication.lastestArticleId
						.getUnReadedArticles();
				for (String key : map.keySet()) {
					ArrayList<Integer> articleIds = map.get(key);
					if (ParseUtil.listNotNull(articleIds)) {
						if (articleIds.contains(id))
							articleIds.remove(id);
					}
				}
				if (ViewsApplication.lastestArticleId != null)
					ViewsApplication.lastestArticleId.getUnReadedId()
							.remove(id);
			}
			ViewsApplication.notifyLastest();
		}
		super.notifyRead();
	}

	public void getUserSubscript(final String currTag, final int code) {
		showLoadingDialog(true);
		OperateController.getInstance(this).getSubscribeOrderList(
				Tools.getUid(this), SlateDataHelper.getToken(this),
				FetchApiType.USE_HTTP_FIRST, new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						showLoadingDialog(false);
						if (entry instanceof SubscribeOrderList) {
							refreshSubscript(currTag, code);
						}
					}
				});
	}

	/**
	 * 刷新订阅列表
	 * 
	 * @param currTag
	 *            父栏目tagname
	 */
	private void refreshSubscript(String currTag, int code) {
		TagIndexDb.getInstance(this).clearSubscribeTopArticle();
		TagArticleListDb.getInstance(this).clearSubscribeTopArticle();
		EnsubscriptHelper.addEnsubscriptColumn(this);
		setDataForColumn();
		if (ViewsApplication.columnChangedListener != null)
			ViewsApplication.columnChangedListener.changed();

		if (SlateApplication.mConfig.getIs_index_pager() == 1) {
			showIndexPager();
			if (!TextUtils.isEmpty(currTag)) {
				clickItemIfPager(currTag, false);
			}
		}

	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == PageTransfer.REQUEST_CODE
					&& CommonApplication.mConfig.getHas_coin() == 1) {
				UserCentManager.getInstance(this)
						.addArticleCoinCent(null, true);
			} else if (requestCode == BOOK_ACTIVITY) {
				refreshSubscript("", -1);
			}
			// else if (requestCode == SELECT_COLUMN_REQUEST_CODE) {
			// refreshSubscript("", -1);
			// } else if (requestCode == SELECT_CHILD_COLUMN_REQUEST_CODE) {
			// String subscriptTag = "";
			// if (data != null && data.getExtras() != null)
			// subscriptTag = data.getExtras().getString("PARENT", "");
			// refreshSubscript(subscriptTag, -1);
			// }
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void callNavPadding(int padding) {
		if (indexView != null)
			indexView.callNavPadding(padding);
	}

	public boolean getNavBarStatus() {
		if (indexView != null) {
			return indexView.isNavShow();
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (indexView.doGoBack()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
