package cn.com.modernmedia.businessweek;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.ScrollCallBackListener;
import cn.com.modernmedia.listener.SizeCallBackForButton;
import cn.com.modernmedia.model.AppValue;
import cn.com.modernmedia.model.SubscribeOrderList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagArticleListDb;
import cn.com.modernmedia.newtag.db.TagIndexDb;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.EnsubscriptHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.PageTransfer;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.column.NewColumnView;
import cn.com.modernmedia.views.column.book.BookActivity;
import cn.com.modernmedia.views.index.IndexView;
import cn.com.modernmedia.webridge.WBWebView;
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

/**
 * 首页
 * 
 * @author ZhuQiao
 * 
 */
public class MainActivity extends CommonMainActivity {
	public static final int SELECT_COLUMN_REQUEST_CODE = 200;
	public static final int SELECT_CHILD_COLUMN_LOGIN_REQUEST_CODE = 203;
	public static final int BOOK_ACTIVITY = 204;

	protected NewColumnView columnView;// 栏目列表侧滑栏
	private LinearLayout container;// 首页tab容器
	// 推荐tab
	protected IndexView indexView;
	// 直播tab
	private View liveTabView;
	private LinearLayout con;
	private WBWebView liveWeb;
	// 特刊tab
	private SpecialTabView specialTabView;
	// 个人中心tab
	private UserCenterTabView userTabView;

	private LifecycleObservable lifecycleObservable = new LifecycleObservable();

	AudioManager audioManager;
	private int volumeInApp;// 进入应用时的音量

	// 底部导航
	private RadioGroup radioGroup;
	private RadioButton radioButton_news;// 首页 新闻推荐

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
			Log.e("登录状态变化", "登录状态变化");
			String uid = Tools.getUid(this);
			Log.e("获取用户订阅列表", uid + " ** " + SlateApplication.UN_UPLOAD_UID);
			if (TextUtils.isEmpty(uid)
					|| TextUtils.equals(uid, SlateApplication.UN_UPLOAD_UID)) {
				radioButton_news.setChecked(true);
				refreshSubscript("", -1);
			} else
				getUserSubscript("", -1);
			userTabView.reLoad();
			SlateApplication.loginStatusChange = false;
		}
		// 用户中心页数据变化时，刷新页面？？
		if (userTabView != null
				&& radioGroup.getCheckedRadioButtonId() == R.id.bottom_nav_mine) {
			userTabView.reLoad();
		}

		lifecycleObservable.setData(LifeCycle.RESUME);
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
		EnsubscriptHelper.addEnsubscriptColumn(this);//

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

	@Override
	public BaseView getIndexView() {
		return indexView;
	}

	@Override
	public void setIndexTitle(String name) {
		indexView.setTitle(name);
		String newName = name.replaceAll("、", "");
		// 设置底部导航文字
		radioButton_news.setText(newName);
	}

	@Override
	public void showIndexPager() {
		ViewsApplication.autoScrollObservable.clearAll();
		indexView.setDataForIndexPager();
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

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == PageTransfer.REQUEST_CODE
					&& CommonApplication.mConfig.getHas_coin() == 1) {
				/**
				 * Boom: 频繁请求文章获取金币规则-- 添加金币
				 */
				// UserCentManager.getInstance(this)
				// .addArticleCoinCent(null, true);
			} else if (requestCode == BOOK_ACTIVITY) {
				refreshSubscript("", -1);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
	 * 给scrollview设置正在下拉刷新
	 * 
	 * @param isPulling
	 */
	public void setPulling(boolean isPulling) {
		scrollView.setPassToUp(isPulling);
	}

	@Override
	public String getActivityName() {
		return MainActivity.class.getName();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (indexView.doGoBack()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	protected void init() {
		setContentView(R.layout.activity_businessweek_main);
		container = (LinearLayout) findViewById(R.id.main_container);
		columnView = (NewColumnView) findViewById(R.id.main_column);
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);
		indexView = new IndexView(this);
		liveTabView = LayoutInflater.from(this).inflate(
				R.layout.main_live_view, null);
		liveTabView.setLayoutParams(new LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
		con = (LinearLayout) liveTabView.findViewById(R.id.live_webView);
		liveWeb = new WBWebView(this);
		con.addView(liveWeb, new LayoutParams(
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT));

		specialTabView = new SpecialTabView(this);
		userTabView = new UserCenterTabView(this);

		View leftView = LayoutInflater.from(this).inflate(R.layout.scroll_left,
				null);
		leftView.setTag(MainHorizontalScrollView.LEFT_ENLARGE_WIDTH);
		View rightView = LayoutInflater.from(this).inflate(
				R.layout.scroll_right, null);
		rightView.setTag(MainHorizontalScrollView.RIGHT_ENLARGE_WIDTH);
		final View[] children = new View[] { leftView, indexView, rightView };
		scrollView.initViews(
				children,
				new SizeCallBackForButton(indexView
						.getTopMenuColumnViewButton()), columnView, new View(
						this));
		scrollView
				.setIntercept(CommonApplication.mConfig.getIs_index_pager() == 1);
		scrollView.setListener(new ScrollCallBackListener() {

			@Override
			public void showIndex(int index) {
				indexView.showCover(index == 0);
				indexView.reStorePullResfresh();
			}
		});
		scrollView.setButtons(indexView.getTopMenuColumnViewButton(),
				indexView.getTopMenuAddViewButton());
		scrollView.setViewListener(new FecthViewSizeListener() {

			@Override
			public void fetchViewWidth(int width) {
				if (columnView != null) {
					columnView.setViewWidth(width);
				}
			}
		});
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO 跑马灯链接
				liveWeb.loadUrl(liveUrl);
				UserCentManager.getInstance(MainActivity.this)
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

		// 底部导航
		radioGroup = (RadioGroup) findViewById(R.id.main_bottom_nav)
				.findViewById(R.id.bottom_nav_rg);
		radioButton_news = (RadioButton) findViewById(R.id.main_bottom_nav)
				.findViewById(R.id.bottom_nav_news);

		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == R.id.bottom_nav_news) {
							changeTab(0);
							LogHelper.checkBottomNavNews(MainActivity.this);

						} else if (checkedId == R.id.bottom_nav_live) {
							changeTab(1);
							LogHelper.checkBottomNavLive(MainActivity.this);

						} else if (checkedId == R.id.bottom_nav_special) {
							changeTab(2);
							LogHelper.checkBottomNavSpecial(MainActivity.this);

						} else if (checkedId == R.id.bottom_nav_mine) {
							changeTab(3);
							LogHelper.checkBottomNavMine(MainActivity.this);
						}
					}
				});
	}

	public void changeTab(int position) {
		container.removeAllViews();
		switch (position) {
		case 0:
			container.addView(scrollView);
			break;
		case 1:
			container.addView(liveTabView);
			liveWeb.loadUrl(liveUrl);
			Log.e("直播html", liveUrl);
			break;
		case 2:
			container.addView(specialTabView);
			break;
		case 3:
			container.addView(userTabView);
			break;
		}
	}

	@Override
	public void gotoMarquee() {
		super.gotoMarquee();
		radioGroup.check(R.id.bottom_nav_live);
	}

	@Override
	protected void setDataForColumn() {
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

	@Override
	public MainHorizontalScrollView getScrollView() {
		return scrollView;
	}

	@Override
	protected void exitApp() {
		super.exitApp();
		ViewsApplication.exit();
		if (audioManager != null) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					volumeInApp, 0);
		}
	}

	@Override
	public String[] getFragmentTags() {
		return null;
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

	/**
	 * 跳转至选择栏目页
	 */
	public void gotoSelectColumnActivity() {
		Intent intent = new Intent(this, BookActivity.class);
		startActivityForResult(intent, BOOK_ACTIVITY);
	}
}
