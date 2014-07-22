package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.BindFavToUserListener;
import cn.com.modernmedia.listener.CallWebStatusChangeListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.HideTitleBarListener;
import cn.com.modernmedia.mainprocess.MainProcessPreIssue;
import cn.com.modernmedia.mainprocess.MainProcessPreIssue.FetchPreviousIssueCallBack;
import cn.com.modernmedia.mainprocess.MainProcessPreIssue.PreIssusType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmedia.widget.CommonViewPager;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * common文章页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonArticleActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private OperateController controller;
	protected Issue issue;
	public TransferArticle mBundle;
	protected List<FavoriteItem> list = new ArrayList<FavoriteItem>();
	protected CommonViewPager viewPager;
	private ViewPageAdapter adapter;
	private FavDb db;
	private ReadDb readDb;
	private long lastClickTime = 0;// 执行完动画才能返回
	private String currentUrl = "";// 当前view的URL
	private List<String> loadOkUrl = new ArrayList<String>();
	private int needDeleteHidden = -1;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, CallWebStatusChangeListener> listenerMap = new HashMap<Integer, CallWebStatusChangeListener>();
	protected int currentPosition;
	private BindFavToUserListener bindFavToUserListener;
	protected Button backBtn, favBtn, fontBtn, shareBtn;
	private boolean isHide = false;
	private ArticleDetailItem currentDetailItem;
	private HideTitleBarListener hideListener = new HideTitleBarListener() {

		@Override
		public void hide() {
			if (isHide) {
				isHide = false;
				// getWindow().clearFlags(
				// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			} else {
				isHide = true;
				// getWindow().addFlags(
				// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			}
		}
	};

	public enum ArticleType {
		Default, Solo, Fav, Last/** 往期 **/
		;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		controller = OperateController.getInstance(mContext);
		list.clear();
		initDataFromBundle();
	}

	/**
	 * 设置页面
	 * 
	 * @param layoutResID
	 *            页面layout_id,可传-1，代表使用默认页面
	 */
	@Override
	public void setContentView(int layoutResID) {
		if (layoutResID == -1) {
			layoutResID = R.layout.default_article_activity;
		}
		super.setContentView(layoutResID);
		init();
		initProcess();
		initViewpager();
		fetchData();
	}

	protected void init() {
		backBtn = (Button) findViewById(R.id.default_article_back_btn);
		favBtn = (Button) findViewById(R.id.default_article_fav_btn);
		fontBtn = (Button) findViewById(R.id.default_article_font_btn);
		shareBtn = (Button) findViewById(R.id.default_article_share_btn);
		viewPager = (CommonViewPager) findViewById(R.id.default_article_viewpager);
		viewPager.setOffscreenPageLimit(1);// 限制预加载，只加载下一页

		backBtn.setOnClickListener(this);
		favBtn.setOnClickListener(this);
		fontBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
	}

	protected void addRule() {
		LayoutParams lp = (LayoutParams) viewPager.getLayoutParams();
		// lp.addRule(RelativeLayout.BELOW, R.id.default_article_toolbar);
		lp.topMargin = getResources().getDimensionPixelSize(
				R.dimen.article_bar_height);
	}

	private void fetchData() {
		if (mBundle == null)
			return;
		switch (mBundle.getArticleType()) {
		case Default:
			getArticleList();
			break;
		case Fav:
			getFavList();
			break;
		case Solo:
			getSoloArticleList(mBundle.getCatId(), mBundle.getArtcleId());
			break;
		case Last:
			// 图片link进入往期时（如slate://article/393...）先取得期信息，然后取文章列表
			if (CommonApplication.lastIssue == null
					|| (CommonApplication.lastIssue.getId() != CommonApplication.currentIssueId)) {
				getPreIssue();
			} else {
				issue = CommonApplication.lastIssue;
				getArticleList();
			}
			break;
		default:
			break;
		}
	}

	private void initViewpager() {
		db = FavDb.getInstance(this);
		readDb = ReadDb.getInstance(this);
		lastClickTime = System.currentTimeMillis() / 1000;
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			/**
			 * 当一个页面即将被加载时，调用此方法 Position index of the new selected page
			 */
			@Override
			public void onPageSelected(int position) {
				if (list.size() <= position)
					return;

				if (needDeleteHidden != -1) {
					if (Math.abs(position - needDeleteHidden) > 1) {
						// pos = i == 0 ? length - 2 : i;
						if (needDeleteHidden == list.size() - 2
								|| needDeleteHidden == 0) {
							list.remove(list.size() - 2);
							list.remove(0);
						} else if (needDeleteHidden == 1
								|| needDeleteHidden == list.size() - 1) {
							list.remove(list.size() - 1);
							list.remove(1);
						} else {
							list.remove(list.get(needDeleteHidden));
						}
						adapter.destroyItem(viewPager, position,
								viewPager.findViewWithTag(1));
						needDeleteHidden = -1;
						return;
					}
				}

				if (list.size() != 1) {
					if (position == 0) {
						viewPager.setCurrentItem(list.size() - 2, false);// 其实是最后一个view
					} else if (position == list.size() - 1) {
						viewPager.setCurrentItem(1, false);// 其实是第一个view
					}
				}
				FavoriteItem item = list.get(position);
				int type = item.getProperty().getType();
				hideFont(type == 2);
				hideIfAdv(item.isAdv());
				changeFav(position);
				currentUrl = item.getLink();
				if (loadOkUrl.contains(currentUrl)) {
					readDb.addReadArticle(item.getId());
					LogHelper.logAndroidShowArticle(mContext, item.getCatid()
							+ "", item.getId() + "");
				}
			}

			/**
			 * 当在一个页面滚动时，调用此方法postion:要滑向页面索引
			 */
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			/**
			 * 状态有三个0空闲，1是增在滑行中，2目标加载完毕
			 */
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	public void addLoadOkUrl(String url) {
		loadOkUrl.add(url);
	}

	/**
	 * 初始化从上一个页面传递过来的数据
	 */
	private void initDataFromBundle() {
		if (getIntent() != null && getIntent().getExtras() != null) {
			mBundle = (TransferArticle) getIntent().getSerializableExtra(
					GenericConstant.TRANSFE_RARTICLE);
			issue = CommonApplication.issue;
		}
	}

	/**
	 * 如果是从收藏页跳转过来的，显示收藏的列表文章
	 */
	private void getFavList() {
		disProcess();
		list = db.getUserFav(mBundle.getUid(), true);
		getPosition(list, true);
	}

	/**
	 * 获取往期信息及文章列表
	 */
	private void getPreIssue() {
		showLoading();
		MainProcessPreIssue preIssue = new MainProcessPreIssue(mContext, null);
		preIssue.getPreIssue(CommonApplication.currentIssueId,
				new FetchPreviousIssueCallBack() {

					@Override
					public void onSuccess(Issue resultIssue) {
						CommonApplication.lastIssue = resultIssue;
						issue = resultIssue;
						getArticleList();
					}

					@Override
					public void onFailed() {
						disProcess();
					}
				}, PreIssusType.GO_TO_ARTICLE);
	}

	/**
	 * 获取文章列表
	 */
	protected void getArticleList() {
		showLoading();
		controller.getArticleList(issue, mBundle.getArticleType(),
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						if (entry instanceof ArticleList) {
							list = ((ArticleList) entry).getAllArticleList();
							getPosition(list, true);
							if (mBundle.getArticleType() != ArticleType.Last)
								DataHelper.setArticleUpdateTime(mContext,
										issue.getArticleUpdateTime(),
										issue.getId());
							disProcess();
						} else {
							showError();
						}
					}
				});
	}

	/**
	 * 获取独立栏目文章列表
	 * 
	 * @param catId
	 */
	protected void getSoloArticleList(int catId, int articleId) {
	}

	private void getArticleById() {
		if (issue == null)
			return;
		showLoading();
		FavoriteItem item = new FavoriteItem();
		item.setId(mBundle.getArtcleId());
		item.setCatid(mBundle.getCatId());
		item.setIssueid(issue.getId());
		item.setUpdateTime(issue.getArticleUpdateTime() + "");
		controller.getArticleById(item, new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				afterGetArticleById(entry);
			}
		});
	}

	protected void getSoloArticleById(Issue issue, String catId,
			String articleId) {
	}

	protected void afterGetArticleById(Entry entry) {
		if (entry instanceof Atlas) {
			Atlas atlas = (Atlas) entry;
			// 把这个文章添在列表末尾
			list.add(atlas);
			List<FavoriteItem> newList1 = new ArrayList<FavoriteItem>();
			newList1.add(atlas);
			newList1.addAll(list);
			newList1.add(list.get(0));
			list.clear();
			list.addAll(newList1);
			newList1 = null;
			setDataForAdapter(list, list.size() - 2, false);
			checkHidden();
			disProcess();
		} else {
			showError();
		}
	}

	private void checkHidden() {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == mBundle.getArtcleId()) {
				// 当前文章为需要隐藏的文章
				if (list.get(i).getProperty().getScrollHidden() == 1) {
					needDeleteHidden = i;
				}
				break;
			}
		}
	}

	/**
	 * 获取当前文章所在索引
	 * 
	 * @param changeList
	 *            是否需要更改list(如果是从别的页面进来的，需要添加头尾；如果是改变整体的字体，那么不需要)
	 * @return
	 */
	protected void getPosition(List<FavoriteItem> list, boolean changeList) {
		if (!ParseUtil.listNotNull(list)) {
			return;
		}

		int length = list.size();
		if (length == 1) {
			setDataForAdapter(list, 0, true);
			return;
		}

		// 去掉滑动时需要隐藏的文章
		List<FavoriteItem> newList = new ArrayList<FavoriteItem>();
		for (int i = 0; i < length; i++) {
			FavoriteItem detail = list.get(i);
			if (detail.getProperty().getScrollHidden() == 0
					|| detail.getId() == mBundle.getArtcleId()) {
				newList.add(detail);
			}
		}
		list.clear();
		list.addAll(newList);
		newList = null;
		// ------------

		int pos = -1;
		if (mBundle.getArtcleId() == -1) {
			pos = 0;
		} else {
			length = list.size();
			for (int i = 0; i < length; i++) {
				if (list.get(i).getId() == mBundle.getArtcleId()) {
					pos = i;
					break;
				}
			}

			if (pos == -1) {// 运营配错了
				if (mBundle.getArticleType() == ArticleType.Default)
					getArticleById();
				else
					getSoloArticleById(issue, mBundle.getCatId() + "",
							mBundle.getArtcleId() + "");
				return;
			}
		}

		/**
		 * 创建一个新的list,循环滑动：头部添加一个和原尾部相同的view，尾部添加一个和原头部相同的view
		 * 当滑动到第一个的时候，其实显示的是本来的最后一个view，这时把显示位置移到最后第二个，即本来的最后一个view
		 * 同理，当滑到最后一个的时候，其实现实的是本来的第一个view，这时把位置移到第一个，即本来的第一个view
		 */
		if (changeList && list.size() != 1) {
			List<FavoriteItem> newList1 = new ArrayList<FavoriteItem>();
			newList1.add(list.get(list.size() - 1));
			newList1.addAll(list);
			newList1.add(list.get(0));
			list.clear();
			list.addAll(newList1);
			newList1 = null;
			if (pos == 0) {
				pos = 1;
			} else if (pos == length - 1) {
				pos = list.size() - 2;
			} else {
				pos++;
			}
			checkHidden();
		}

		setDataForAdapter(list, pos, true);
	}

	private void setDataForAdapter(List<FavoriteItem> list, int position,
			boolean changeList) {
		if (ParseUtil.listNotNull(list)) {
			adapter = new ViewPageAdapter();
			adapter.setData(list);
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(position, false);
			changeFav(position);
		}
	}

	/**
	 * 改变收藏图片
	 * 
	 * @param pos
	 */
	protected void changeFav(int pos) {
		FavoriteItem detail = getArticleByPosition(pos);
		if (detail != null) {
			if (db.containThisFav(detail.getId(), mBundle.getUid())) {
				changeFavBtn(true);
			} else {
				changeFavBtn(false);
			}
		}
	}

	/**
	 * 添加收藏
	 */
	protected void addFav() {
		if (!ParseUtil.listNotNull(list))
			return;
		int position = viewPager.getCurrentItem();
		String uid = mBundle.getUid();
		if (list.size() > position) {
			FavoriteItem fav = list.get(position);
			ModernMediaTools.addFav(this, fav, uid, bindFavToUserListener);
		}
		changeFav(position);
	}

	/**
	 * 点击字体按钮
	 */
	protected void clickFont() {
		if (DataHelper.getFontSize(this) == 1) {
			DataHelper.setFontSize(this, 2);
		} else {
			DataHelper.setFontSize(this, 1);
		}
		loadViewAfterFont();
	}

	/**
	 * iweekly使用
	 * 
	 * @param plus
	 */
	protected void clickFont(boolean plus) {
		int now = DataHelper.getFontSize(this);
		if (plus) {// 放大
			if (now == 5) {
				return;
			}
			now++;
		} else {
			if (now == 1) {
				return;
			}
			now--;
		}
		DataHelper.setFontSize(this, now);
		// loadViewAfterFont();
		changeFont();
	}

	private void changeFont() {
		if (!listenerMap.isEmpty())
			for (int position : listenerMap.keySet()) {
				listenerMap.get(position).changeFontSize();
			}
	}

	/**
	 * 改变行间距,iweekly使用
	 * 
	 * @param plus
	 */
	protected void changeLineHeight(boolean plus) {
		if (!ParseUtil.listNotNull(list))
			return;
		int now = DataHelper.getLineHeight(this);
		if (plus) {// 放大
			if (now == 5) {
				return;
			}
			now++;
		} else {
			if (now == 1) {
				return;
			}
			now--;
		}
		DataHelper.setLineHeight(this, now);
		// loadViewAfterFont();
		changeLineHeight();
	}

	private void changeLineHeight() {
		if (!listenerMap.isEmpty())
			for (int position : listenerMap.keySet()) {
				listenerMap.get(position).changeLineHeight();
			}
	}

	private void loadViewAfterFont() {
		if (!ParseUtil.listNotNull(list))
			return;
		int position = viewPager.getCurrentItem();
		if (list.size() <= position) {
			return;
		}
		FavoriteItem detail = list.get(position);
		LogHelper.logChangeArticleFontSize(this, detail.getId() + "",
				detail.getCatid() + "");
		setDataForAdapter(list, position, false);
	}

	/**
	 * 点击分享按钮
	 */
	protected void showShare() {
		int position = viewPager.getCurrentItem();
		FavoriteItem item = getArticleByPosition(position);
		ModernMediaTools.shareFavoriteItem(this, item, issue);
	}

	/**
	 * 从文章跳转到指定文章
	 */
	public void moveToArticle(int id) {
		int pos = -1;
		int length = list.size();
		for (int i = 0; i < length; i++) {
			if (list.get(i).getId() == id) {
				pos = i;
				break;
			}
		}
		if (pos != -1) {
			changeFav(pos);
			viewPager.setCurrentItem(pos, false);
		}
	}

	/**
	 * 从文章跳转到指定广告
	 */
	public void moveToAdv(int id) {
		int pos = -1;
		int length = list.size();
		for (int i = 0; i < length; i++) {
			if (list.get(i).getId() == id) {
				pos = i;
				break;
			}
		}
		if (pos != -1) {
			changeFav(pos);
			viewPager.setCurrentItem(pos, false);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.default_article_back_btn) {
			if (checkTime())
				finishAndAnim();
		} else if (id == R.id.default_article_fav_btn) {
			addFav();
		} else if (id == R.id.default_article_font_btn) {
			clickFont();
		} else if (id == R.id.default_article_share_btn) {
			showShare();
		}
	}

	/**
	 * 发送send_action到登陆页
	 * 
	 * @param item
	 */
	protected void checkLogin(ArticleItem item, Class<?> loginCls) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setClass(this, loginCls);
		intent.putExtra(Intent.EXTRA_TEXT, item.getDesc());
		startActivity(intent);
		overridePendingTransition(R.anim.activity_open_enter,
				R.anim.activity_open_exit);
	}

	private class ViewPageAdapter extends PagerAdapter {
		private List<FavoriteItem> list = new ArrayList<FavoriteItem>();

		public void setData(List<FavoriteItem> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			if (listenerMap.containsKey(position))
				listenerMap.remove(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			FavoriteItem detail = list.get(position);
			View view = fetchView(detail);
			view.setTag(detail.getProperty().getScrollHidden());
			container.addView(view);
			if (view instanceof CallWebStatusChangeListener) {
				listenerMap.put(position, (CallWebStatusChangeListener) view);
			}
			return view;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			if (object instanceof CommonAtlasView) {
				viewPager.setPager(getAtlasViewPager(object));
			} else if (object instanceof ArticleDetailItem) {
				currentDetailItem = (ArticleDetailItem) object;
				viewPager.setArticleDetailItem(currentDetailItem);
			} else {
				viewPager.setPager(null);
				viewPager.setArticleDetailItem(null);
			}
			currentPosition = position;
		}

	}

	/**
	 * 改变收藏图片
	 * 
	 * @param isFavEd
	 */
	protected abstract void changeFavBtn(boolean isFavEd);

	/**
	 * 是否隐藏字体按钮(图集)
	 * 
	 * @param hide
	 */
	protected abstract void hideFont(boolean hide);

	/**
	 * 是否隐藏titleBar按钮(广告文章隐藏)
	 * 
	 * @param hide
	 */
	protected abstract void hideIfAdv(boolean hide);

	/**
	 * 获取文章view
	 * 
	 * @param detail
	 * @return
	 */
	protected abstract View fetchView(FavoriteItem detail);

	/**
	 * 获取图集文章view
	 * 
	 * @param object
	 */
	protected abstract AtlasViewPager getAtlasViewPager(Object object);

	private FavoriteItem getArticleByPosition(int position) {
		if (list != null && list.size() > position) {
			return list.get(position);
		}
		return null;
	}

	protected FavoriteItem getCurrentArticleDetail() {
		return getArticleByPosition(currentPosition);
	}

	public String getCurrentUrl() {
		return currentUrl;
	}

	public void setBindFavToUserListener(
			BindFavToUserListener bindFavToUserListener) {
		this.bindFavToUserListener = bindFavToUserListener;
	}

	public TransferArticle getBundle() {
		return mBundle;
	}

	public HideTitleBarListener getHideListener() {
		return hideListener;
	}

	public ArticleDetailItem getCurrentDetailItem() {
		return currentDetailItem;
	}

	@Override
	public void reLoadData() {
		fetchData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (!checkTime()) {
				return true;
			}
			finishAndAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void finishAndAnim() {
		loadOkUrl.clear();
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.right_out);
	}

	/**
	 * 等待动画结束
	 * 
	 * @return
	 */
	protected boolean checkTime() {
		long clickTime = System.currentTimeMillis() / 1000;
		if (clickTime - lastClickTime >= 1) {
			lastClickTime = clickTime;
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!listenerMap.isEmpty()) {
			listenerMap.clear();
		}
		list.clear();
	}

}
