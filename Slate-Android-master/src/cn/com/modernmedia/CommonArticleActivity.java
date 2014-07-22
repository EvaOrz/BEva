package cn.com.modernmedia;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cn.com.modernmedia.api.OperateController;
import cn.com.modernmedia.common.ShareManager;
import cn.com.modernmedia.db.FavDb;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleList;
import cn.com.modernmedia.model.ArticleList.ArticleDetail;
import cn.com.modernmedia.model.Atlas;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.Issue;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.DataHelper;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.CommonAtlasView;
import cn.com.modernmedia.widget.CommonViewPager;

/**
 * common文章页
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CommonArticleActivity extends BaseActivity {
	private Context mContext;
	private OperateController controller;
	private boolean isIndex;
	private Issue issue;
	private int articleId;
	private int catId;
	private Handler handler = new Handler();
	private List<ArticleDetail> list;
	// private boolean hasFetchApi = false;
	private ViewPageAdapter adapter;
	private FavDb db;
	private ReadDb readDb;
	private long lastClickTime = 0;// 执行完动画才能返回
	private ShareManager shareManager;
	private String currentUrl = "";// 当前view的URL
	private List<String> loadOkUrl = new ArrayList<String>();
	private int needDeleteHidden = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		controller = new OperateController(this);
		initDataFromBundle();
		init();
		initProcess();
		initViewpager();
		if (isIndex)
			getArticleList();
		else
			getFavList();
	}

	private void initViewpager() {
		db = FavDb.getInstance(this);
		readDb = ReadDb.getInstance(this);
		shareManager = new ShareManager(this);
		lastClickTime = System.currentTimeMillis() / 1000;
		getViewPager().setOnPageChangeListener(new OnPageChangeListener() {
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
						adapter.destroyItem(getViewPager(), position,
								getViewPager().findViewWithTag(1));
						needDeleteHidden = -1;
						return;
					}
				}

				if (list.size() != 1) {
					if (position == 0) {
						getViewPager().setCurrentItem(list.size() - 2, false);// 其实是最后一个view
					} else if (position == list.size() - 1) {
						getViewPager().setCurrentItem(1, false);// 其实是第一个view
					}
				}
				String type = list.get(position).getProperty().getType();
				hideFont(type.equals("2"));
				changeFav(position);
				currentUrl = list.get(position).getLink();
				if (loadOkUrl.contains(currentUrl))
					readDb.addReadArticle(list.get(position).getArticleId());
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
			issue = (Issue) getIntent().getSerializableExtra("ISSUE");
			articleId = getIntent().getIntExtra("ARTICLE_ID", -1);
			catId = getIntent().getIntExtra("CAT_ID", -1);
			isIndex = getIntent().getBooleanExtra("IS_INDEX", true);
		}
	}

	/**
	 * 如果是从收藏页跳转过来的，显示收藏的列表文章
	 */
	private void getFavList() {
		disProcess();
		list = db.getAllFav().getList();
		getPosition(list, true);
	}

	/**
	 * 获取文章列表
	 */
	protected void getArticleList() {
		showLoading();
		controller.getArticleList(issue, new FetchEntryListener() {

			@Override
			public void setData(final Entry entry) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (entry != null && entry instanceof ArticleList) {
							list = ((ArticleList) entry).getAllArticleList();
							getPosition(list, true);
							DataHelper.setArticleUpdateTime(mContext,
									issue.getArticleUpdateTime());
							disProcess();
						} else {
							showError();
						}
					}
				});
			}
		});
	}

	private void getArticleById() {
		showLoading();
		controller.getArticleById(issue, catId + "", articleId + "",
				new FetchEntryListener() {

					@Override
					public void setData(final Entry entry) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								if (entry != null && entry instanceof Atlas) {
									Atlas atlas = (Atlas) entry;
									ArticleDetail detail = new ArticleDetail();
									detail.setArticleId(atlas.getId());
									detail.setCatId(catId);
									detail.setLink(atlas.getLink());
									// 把这个文章添在列表末尾
									list.add(detail);
									List<ArticleDetail> newList1 = new ArrayList<ArticleDetail>();
									newList1.add(detail);
									newList1.addAll(list);
									newList1.add(list.get(0));
									list.clear();
									list.addAll(newList1);
									newList1 = null;
									setDataForAdapter(list, list.size() - 2,
											false);
									checkHidden();
									disProcess();
								} else {
									showError();
								}
							}
						});
					}
				});
	}

	private void checkHidden() {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getArticleId() == articleId) {
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
	private void getPosition(List<ArticleDetail> list, boolean changeList) {
		if (list == null || list.isEmpty()) {
			return;
		}

		int length = list.size();
		if (length == 1) {
			setDataForAdapter(list, 1, true);
			return;
		}

		// 去掉滑动时需要隐藏的文章
		List<ArticleDetail> newList = new ArrayList<ArticleDetail>();
		for (int i = 0; i < length; i++) {
			ArticleDetail detail = list.get(i);
			if (detail.getProperty().getScrollHidden() == 0
					|| detail.getArticleId() == articleId) {
				newList.add(detail);
			}
		}
		list.clear();
		list.addAll(newList);
		newList = null;
		// ------------

		int pos = -1;
		length = list.size();
		for (int i = 0; i < length; i++) {
			if (list.get(i).getArticleId() == articleId) {
				// pos = i == 0 ? length - 2 : i;
				pos = i;
				break;
			}
		}

		if (pos == -1) {// 运营配错了
			getArticleById();
			return;
		}

		/**
		 * 创建一个新的list,循环滑动：头部添加一个和原尾部相同的view，尾部添加一个和原头部相同的view
		 * 当滑动到第一个的时候，其实显示的是本来的最后一个view，这时把显示位置移到最后第二个，即本来的最后一个view
		 * 同理，当滑到最后一个的时候，其实现实的是本来的第一个view，这时把位置移到第一个，即本来的第一个view
		 */
		if (changeList && list.size() != 1) {
			List<ArticleDetail> newList1 = new ArrayList<ArticleDetail>();
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

	private void setDataForAdapter(List<ArticleDetail> list, int position,
			boolean changeList) {
		if (list == null || list.isEmpty())
			return;
		adapter = new ViewPageAdapter();
		adapter.setData(list);
		getViewPager().setAdapter(adapter);
		getViewPager().setCurrentItem(position, false);
		changeFav(position);
	}

	/**
	 * 改变收藏图片
	 * 
	 * @param pos
	 */
	protected void changeFav(int pos) {
		if (list == null || list.size() <= pos)
			return;
		ArticleDetail detail = list.get(pos);
		if (db.containThisFav(detail)) {
			changeFavBtn(true);
		} else {
			changeFavBtn(false);
		}
		LogHelper.logAddFavoriteArticle(this, detail.getArticleId() + "",
				detail.getCatId() + "");
	}

	/**
	 * 添加收藏
	 */
	protected void addFav() {
		if (list == null)
			return;
		int position = getViewPager().getCurrentItem();
		if (list.size() > position) {
			ArticleDetail fav = list.get(position);
			if (db.containThisFav(fav)) {
				db.deleteFav(fav);
				Toast.makeText(this, R.string.delete_fav,
						ConstData.TOAST_LENGTH).show();
			} else {
				db.addFav(fav);
				Toast.makeText(this, R.string.add_fav, ConstData.TOAST_LENGTH)
						.show();
			}
		}
		changeFav(position);
		if (CommonApplication.favListener != null)// 刷新favView
			CommonApplication.favListener.refreshFav();
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

	protected void clickFont(boolean plus) {
		if (plus) {// 放大
			if (DataHelper.getFontSize(this) == 2) {
				return;
			}
		} else {
			if (DataHelper.getFontSize(this) == 1) {
				return;
			}
		}
		DataHelper.setFontSize(this, plus ? 2 : 1);
		loadViewAfterFont();
	}

	/**
	 * 改变行间距
	 * 
	 * @param plus
	 */
	protected void changeLineHeight(boolean plus) {
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
		loadViewAfterFont();
	}

	private void loadViewAfterFont() {
		if (list == null)
			return;
		int position = getViewPager().getCurrentItem();
		if (list.size() <= position) {
			return;
		}
		ArticleDetail detail = list.get(position);
		LogHelper.logChangeArticleFontSize(this, detail.getArticleId() + "",
				detail.getCatId() + "");
		setDataForAdapter(list, position, false);
	}

	/**
	 * 点击分享按钮
	 */
	protected void showShare() {
		int position = getViewPager().getCurrentItem();
		if (list != null && list.size() > position) {
			shareManager.showDialog(issue, list.get(position).getCatId() + "",
					list.get(position).getArticleId() + "");
		}
	}

	/**
	 * 从文章跳转到指定文章
	 */
	public void moveToArticle(int id) {
		int pos = 0;
		int length = list.size();
		for (; pos < length; pos++) {
			if (list.get(pos).getArticleId() == id) {
				break;
			}
		}
		changeFav(pos);
		getViewPager().setCurrentItem(pos, false);
	}

	private class ViewPageAdapter extends PagerAdapter {
		private List<ArticleDetail> list = new ArrayList<ArticleDetail>();

		public void setData(List<ArticleDetail> list) {
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
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ArticleDetail detail = list.get(position);
			View view = fetchView(detail);
			view.setTag(detail.getProperty().getScrollHidden());
			container.addView(view);
			return view;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			if (object instanceof CommonAtlasView) {
				getViewPager().setPager(getAtlasViewPager(object));
			} else {
				getViewPager().setPager(null);
			}
		}

	}

	/**
	 * 初始化资源
	 */
	protected abstract void init();

	/**
	 * 获取viewpager
	 * 
	 * @return
	 */
	protected abstract CommonViewPager getViewPager();

	/**
	 * 改变收藏图片
	 * 
	 * @param isFavEd
	 */
	protected abstract void changeFavBtn(boolean isFavEd);

	/**
	 * 是否隐藏字体按钮(图集隐藏)
	 * 
	 * @param hide
	 */
	protected abstract void hideFont(boolean hide);

	/**
	 * 获取文章view
	 * 
	 * @param detail
	 * @return
	 */
	protected abstract View fetchView(ArticleDetail detail);

	/**
	 * 获取图集文章view
	 * 
	 * @param object
	 */
	protected abstract AtlasViewPager getAtlasViewPager(Object object);

	public Issue getIssue() {
		return issue;
	}

	@Override
	public void reLoadData() {
		getArticleList();
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

	public String getCurrentUrl() {
		return currentUrl;
	}

}
