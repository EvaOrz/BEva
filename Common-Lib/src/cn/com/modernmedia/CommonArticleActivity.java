package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.db.NewFavDb;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.BindFavToUserListener;
import cn.com.modernmedia.listener.CallWebStatusChangeListener;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.HideTitleBarListener;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.newtag.db.TagInfoListDb;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.GenericConstant;
import cn.com.modernmedia.util.LastIssueAticleActivityHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.PageTransfer.TransferArticle;
import cn.com.modernmedia.widget.ArticleDetailItem;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmedia.widget.CommonViewPager;
import cn.com.modernmediaslate.api.SlateBaseOperate.FetchApiType;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * common文章页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonArticleActivity extends BaseActivity {
	private Context mContext;
	protected TransferArticle transferArticle;
	protected List<ArticleItem> list = new ArrayList<ArticleItem>();
	protected CommonViewPager viewPager;
	private ViewPageAdapter adapter;
	private NewFavDb db;
	private ReadDb readDb;
	private long lastClickTime = 0;// 执行完动画才能返回
	private int currArticleId;// 当前文章id
	private List<Integer> loadOkIds = new ArrayList<Integer>();
	private int needDeleteHidden = -1;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, CallWebStatusChangeListener> listenerMap = new HashMap<Integer, CallWebStatusChangeListener>();
	protected int currentPosition = -1;
	private BindFavToUserListener bindFavToUserListener;
	private boolean isHide = false;
	protected View currView;
	private boolean singleArticle;// 只有一篇文章
	private Handler handler = new Handler();

	private HideTitleBarListener hideListener = new HideTitleBarListener() {

		@Override
		public void hide() {
			if (isHide) {
				isHide = false;
			} else {
				isHide = true;
			}
		}
	};

	public enum ArticleType {
		Default, Fav, Last/** 往期 **/
		;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
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
		if (transferArticle == null)
			return;
		init();
		initProcess();
		initViewpager();
		fetchData(true);
	}

	protected void init() {
		viewPager = (CommonViewPager) findViewById(R.id.default_article_viewpager);
		viewPager.setOffscreenPageLimit(1);// 限制预加载，只加载下一页
	}

	protected void addRule() {
		LayoutParams lp = (LayoutParams) viewPager.getLayoutParams();
		lp.topMargin = getResources().getDimensionPixelSize(
				R.dimen.article_bar_height);
	}

	/**
	 * 获取文章数据
	 */
	private void fetchData(boolean delay) {
		if (transferArticle.getArticleType() == ArticleType.Fav) { // 收藏
			getFavList();
		} else if (transferArticle.getArticleType() == ArticleType.Last) { // 往期
			LastIssueAticleActivityHelper lastHelper = new LastIssueAticleActivityHelper(
					this, transferArticle);
			lastHelper.doGetLastIssueAricles();
		} else {
			showLoading();
			if (delay) {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						getArticleList();
					}
				}, 1000);
			} else {
				getArticleList();
			}
		}
	}

	/**
	 * 从服务器获取文章列表
	 */
	private void getArticleList() {
		showLoading();
		TagInfo tagInfo;
		if (TextUtils.isEmpty(transferArticle.getParent())) {
			tagInfo = TagInfoListDb.getInstance(this).getTagInfoByName(
					transferArticle.getTagName(), "", true);
		} else {
			tagInfo = TagInfoListDb.getInstance(this).getTagInfoByName(
					transferArticle.getTagName(), transferArticle.getParent(),
					false);
		}
		if (TextUtils.isEmpty(tagInfo.getTagName())) {
			// TODO 请求tag信息
			getTagInfo();
		} else {
			tagInfo = tagInfo.getMergeParentTagInfo(this);
			getTagArticleList(tagInfo, true);
		}
	}

	/**
	 * 获取文章信息
	 * 
	 * @param tagInfo
	 */
	private void getTagArticleList(TagInfo tagInfo, boolean useCache) {
		// TODO 因为在请求index时候已经请求了articlelist，所有每次都从数据库取，如果数据库没有，那么从服务器上获取
		if (useCache) {
			OperateController.getInstance(this).getTagArticles(tagInfo, "", "",
					null, FetchApiType.USE_CACHE_FIRST,
					new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							afterFecthArticleList(entry);
						}
					});
		} else {
			OperateController.getInstance(this).getTagArticles(tagInfo, "", "",
					null, new FetchEntryListener() {

						@Override
						public void setData(Entry entry) {
							afterFecthArticleList(entry);
						}
					});
		}
	}

	public void afterFecthArticleList(Entry entry) {
		if (entry instanceof TagArticleList) {
			checkShouldInsertSubscribe((TagArticleList) entry);
			disProcess();
		} else {
			showError();
		}
	}

	/**
	 * 获取tag信息(tag只能是子栏目，如果是父栏目，那么必须有自己的文章列表)
	 * 
	 * @param tagName
	 */
	private void getTagInfo() {
		OperateController.getInstance(this).getTagInfo(
				transferArticle.getTagName(), FetchApiType.USE_CACHE_FIRST,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagInfoList) {
							TagInfoList list = (TagInfoList) entry;
							if (ParseUtil.listNotNull(list.getList())) {
								getTagArticleList(list.getList().get(0), false);
							} else {
								showError();
							}
						} else {
							showError();
						}
					}
				});
	}

	/**
	 * 获取解压的zip数据存放的目录名
	 */
	public String getLocalArticlesFolder() {
		return transferArticle.getFloderName();
	}

	/**
	 * 获取文章
	 * 
	 * @param articleId
	 */
	private void getArticle(int articleId, final boolean resetAdapter) {
		showLoading();
		OperateController.getInstance(this).getArticleDetails(articleId,
				new FetchEntryListener() {

					@Override
					public void setData(Entry entry) {
						if (entry instanceof TagArticleList) {
							TagArticleList tagArticleList = (TagArticleList) entry;
							if (ParseUtil.listNotNull(tagArticleList
									.getArticleList())) {
								// 把这个文章添在列表末尾
								ArticleItem item = tagArticleList
										.getArticleList().get(0);
								list.addAll(checkMultiplePage(item));
								final int pos = list.size()
										- item.getPageUrlList().size();
								if (resetAdapter) {
									setDataForAdapter(list, pos);
								} else {
									viewPager.setCurrentItem(pos, false);
								}
							}
							disProcess();
						} else {
							showError();
						}
					}
				});
	}

	/**
	 * 判断是否需要插入订阅文章
	 * 
	 * @param dArticleList
	 */
	protected void checkShouldInsertSubscribe(TagArticleList dArticleList) {
	}

	/**
	 * 如果是从收藏页跳转过来的，显示收藏的列表文章
	 */
	private void getFavList() {
		disProcess();
		list = db.getUserFav(transferArticle.getUid());
		getPosition();
	}

	private void initViewpager() {
		db = NewFavDb.getInstance(this);
		readDb = ReadDb.getInstance(this);
		lastClickTime = System.currentTimeMillis() / 1000;
		viewPager.setListener(new NotifyArticleDesListener() {

			@Override
			public void updatePage(int state) {
			}

			@Override
			public void updateDes(int position) {
				if (list.size() <= position)
					return;

				if (needDeleteHidden != -1) {
					if (Math.abs(position - needDeleteHidden) > 1) {
						list.remove(list.get(needDeleteHidden));
						adapter.destroyItem(viewPager, position,
								viewPager.findViewWithTag(1));
						needDeleteHidden = -1;
						return;
					}
				}

				ArticleItem item = list.get(position);
				int type = item.getProperty().getType();
				hideFont(type == 2);
				hideIfAdv(item.isAdv());
				changeFav(position);
				changedNavBar(position);
				currArticleId = item.getArticleId();
				if (loadOkIds.contains(currArticleId)) {
					readDb.addReadArticle(item.getArticleId());
					LogHelper.logAndroidShowArticle(mContext,
							transferArticle.getTagName(), item.getArticleId()
									+ "");
				}
			}
		});
	}

	public void addLoadOkIds(int articleId) {
		loadOkIds.add(articleId);
	}

	/**
	 * 初始化从上一个页面传递过来的数据
	 */
	private void initDataFromBundle() {
		if (getIntent() != null && getIntent().getExtras() != null) {
			transferArticle = (TransferArticle) getIntent().getExtras().get(
					GenericConstant.TRANSFE_RARTICLE);
		}
	}

	private void checkHidden() {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getArticleId() == transferArticle.getArtcleId()) {
				// 当前文章为需要隐藏的文章
				if (list.get(i).getProperty().getScrollHidden() == 1) {
					needDeleteHidden = i;
				}
				break;
			}
		}
	}

	/**
	 * 判断是否有多个page
	 * 
	 * @param detail
	 * @return
	 */
	private List<ArticleItem> checkMultiplePage(ArticleItem detail) {
		List<ArticleItem> _list = new ArrayList<ArticleItem>();
		if (detail.getPageUrlList().size() > 1
				&& detail.getProperty().getType() == 1) {
			// pagelist多页
			ArticleItem _temp = new ArticleItem();
			_temp.getPageUrlList().clear();
			_temp.getPageUrlList().addAll(detail.getPageUrlList());
			for (PhonePageList pageList : _temp.getPageUrlList()) {
				ArticleItem item = detail.copy();
				item.getPageUrlList().clear();
				item.getPageUrlList().add(pageList);
				_list.add(item);
			}
		} else {
			_list.add(detail);
		}
		return _list;
	}

	/**
	 * 获取当前文章所在索引
	 * 
	 * @return
	 */
	protected void getPosition() {
		if (!ParseUtil.listNotNull(list)) {
			return;
		}

		int length = list.size();
		if (length == 1) {
			setDataForAdapter(list, 0);
			return;
		}

		List<ArticleItem> newList = new ArrayList<ArticleItem>();
		for (int i = 0; i < length; i++) {
			ArticleItem detail = list.get(i);
			// 去掉滑动时需要隐藏的文章
			if (detail.getProperty().getScrollHidden() == 1
					&& detail.getArticleId() != transferArticle.getArtcleId())
				continue;
			newList.addAll(checkMultiplePage(detail));
		}
		list.clear();
		list.addAll(newList);
		newList = null;
		// ------------

		int pos = -1;
		if (transferArticle.getArtcleId() == -1
				&& transferArticle.getAdvId() == -1) {
			pos = 0;
		} else {
			length = list.size();
			for (int i = 0; i < length; i++) {
				if (transferArticle.getAdvId() > 0) {
					// TODO 跳转至某个广告
					if (list.get(i).getAdvId() == transferArticle.getAdvId()) {
						pos = i;
						break;
					}
				} else if (list.get(i).getArticleId() == transferArticle
						.getArtcleId()) {
					pos = i;
					break;
				}
			}
		}

		if (pos == -1 && !singleArticle) {
			// NOTE 找不到这篇文章
			singleArticle = true;
			getArticle(transferArticle.getArtcleId(), true);
			return;
		}

		pos = pos == -1 ? 0 : pos;
		checkHidden();
		setDataForAdapter(list, pos);
	}

	private void setDataForAdapter(List<ArticleItem> list, int position) {
		if (ParseUtil.listNotNull(list)) {
			adapter = new ViewPageAdapter(mContext, list);
			viewPager.setDataForPager(list, position, adapter);
			changeFav(position);
		}
	}

	/**
	 * 改变收藏图片
	 * 
	 * @param pos
	 */
	protected void changeFav(int pos) {
		ArticleItem detail = getArticleByPosition(pos);
		if (detail != null) {
			if (db.containThisFav(detail.getArticleId(),
					transferArticle.getUid())) {
				changeFavBtn(true);
			} else {
				changeFavBtn(false);
			}
		}
	}

	/**
	 * 切换view
	 */
	protected void changedNavBar(int pos) {
	}

	/**
	 * 添加收藏
	 */
	public void addFav() {
		if (!ParseUtil.listNotNull(list))
			return;
		int position = viewPager.getCurrentItem();
		String uid = transferArticle.getUid();
		if (list.size() > position) {
			ArticleItem fav = list.get(position);
			ModernMediaTools.addFav(this, fav, uid, bindFavToUserListener);
		}
		changeFav(position);
	}

	/**
	 * 点击字体按钮
	 */
	public void clickFont() {
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
	public void clickFont(boolean plus) {
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
	public void changeLineHeight(boolean plus) {
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
		ArticleItem detail = list.get(position);
		LogHelper.logChangeArticleFontSize(this, detail.getArticleId() + "",
				transferArticle.getTagName());
		setDataForAdapter(list, position);
	}

	/**
	 * 从文章跳转到指定文章
	 */
	public void moveToArticle(TransferArticle _tr) {
		if (_tr.getTagName().equals(transferArticle.getTagName())) {
			int pos = -1;
			int length = list.size();
			int aId = _tr.getArtcleId();
			for (int i = 0; i < length; i++) {
				if (list.get(i).getArticleId() == aId) {
					pos = i;
					break;
				}
			}
			if (pos != -1) {
				changeFav(pos);
				viewPager.setCurrentItem(pos, false);
			} else {
				getArticle(aId, false);
			}
		} else {
			transferArticle = _tr;
			fetchData(false);
		}
	}

	/**
	 * 从文章跳转到指定广告
	 */
	public void moveToAdv(int id) {
		int pos = -1;
		int length = list.size();
		for (int i = 0; i < length; i++) {
			if (list.get(i).getAdvId() == id) {
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
	 * 发送send_action到登陆页
	 * 
	 * @param item
	 */
	protected void checkLogin(ArticleItem item, Class<?> loginCls) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setClass(this, loginCls);
		// 将articleid作为添加到发送的数据中
		String prefix = "{:" + item.getArticleId() + ":}";
		intent.putExtra(Intent.EXTRA_TEXT, prefix + item.getDesc());
		startActivity(intent);
		overridePendingTransition(R.anim.activity_open_enter,
				R.anim.activity_open_exit);
	}

	private class ViewPageAdapter extends MyPagerAdapter<ArticleItem> {

		public ViewPageAdapter(Context context, List<ArticleItem> list) {
			super(context, list);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			if (listenerMap.containsKey(position)
					&& position != currentPosition) {
				listenerMap.remove(position);
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ArticleItem detail = list.get(position);
			View view = fetchView(detail);
			view.setTag(detail.getProperty().getScrollHidden());
			container.addView(view);
			if (view instanceof CallWebStatusChangeListener) {
				listenerMap.put(position, (CallWebStatusChangeListener) view);
			}
			return view;
		}

		@Override
		public View fetchView(ArticleItem t) {
			return CommonArticleActivity.this.fetchView(t);
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int loopPosition,
				int realPosition, Object object) {
			if (currentPosition == realPosition)
				return;
			changePage(currView, false);

			if (object instanceof CommonAtlasView) {
				viewPager.setPager(getAtlasViewPager(object));
				currView = (View) object;
			} else if (object instanceof ArticleDetailItem) {
				currView = (View) object;
				viewPager.setArticleDetailItem((ArticleDetailItem) object);
				changePage(currView, true);
			} else {
				currView = null;
				viewPager.setPager(null);
				viewPager.setArticleDetailItem(null);
			}
			currentPosition = realPosition;
		}

		private void changePage(View view, boolean push) {
			if (!(view instanceof ArticleDetailItem))
				return;
			ArticleDetailItem detailView = (ArticleDetailItem) view;
			if (detailView == null
					|| detailView.getDetail() == null
					|| detailView.getWebView() == null
					|| !loadOkIds.contains(detailView.getDetail()
							.getArticleId())) {
				return;
			}
			if (push) {
				detailView.getWebView().push();
			} else {
				detailView.getWebView().pop();
			}
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
	protected abstract View fetchView(ArticleItem detail);

	/**
	 * 获取图集文章view
	 * 
	 * @param object
	 */
	protected abstract AtlasViewPager getAtlasViewPager(Object object);

	protected ArticleItem getArticleByPosition(int position) {
		if (position == -1)
			return null;
		if (list != null && list.size() > position) {
			return list.get(position);
		}
		return null;
	}

	protected ArticleItem getCurrentArticleDetail() {
		return getArticleByPosition(currentPosition);
	}

	public int getCurrArticleId() {
		return currArticleId;
	}

	public void setBindFavToUserListener(
			BindFavToUserListener bindFavToUserListener) {
		this.bindFavToUserListener = bindFavToUserListener;
	}

	public HideTitleBarListener getHideListener() {
		return hideListener;
	}

	public View getCurrView() {
		return currView;
	}

	@Override
	public void reLoadData() {
		singleArticle = false;
		fetchData(false);
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
		loadOkIds.clear();
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

	/**
	 * 返回
	 */
	public void back() {
		if (checkTime()) {
			finishAndAnim();
		}
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
