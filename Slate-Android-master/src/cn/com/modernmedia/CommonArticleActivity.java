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
 * common����ҳ
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
	private long lastClickTime = 0;// ִ���궯�����ܷ���
	private ShareManager shareManager;
	private String currentUrl = "";// ��ǰview��URL
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
			 * ��һ��ҳ�漴��������ʱ�����ô˷��� Position index of the new selected page
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
						getViewPager().setCurrentItem(list.size() - 2, false);// ��ʵ�����һ��view
					} else if (position == list.size() - 1) {
						getViewPager().setCurrentItem(1, false);// ��ʵ�ǵ�һ��view
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
			 * ����һ��ҳ�����ʱ�����ô˷���postion:Ҫ����ҳ������
			 */
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			/**
			 * ״̬������0���У�1�����ڻ����У�2Ŀ��������
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
	 * ��ʼ������һ��ҳ�洫�ݹ���������
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
	 * ����Ǵ��ղ�ҳ��ת�����ģ���ʾ�ղص��б�����
	 */
	private void getFavList() {
		disProcess();
		list = db.getAllFav().getList();
		getPosition(list, true);
	}

	/**
	 * ��ȡ�����б�
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
									// ��������������б�ĩβ
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
				// ��ǰ����Ϊ��Ҫ���ص�����
				if (list.get(i).getProperty().getScrollHidden() == 1) {
					needDeleteHidden = i;
				}
				break;
			}
		}
	}

	/**
	 * ��ȡ��ǰ������������
	 * 
	 * @param changeList
	 *            �Ƿ���Ҫ����list(����Ǵӱ��ҳ������ģ���Ҫ���ͷβ������Ǹı���������壬��ô����Ҫ)
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

		// ȥ������ʱ��Ҫ���ص�����
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

		if (pos == -1) {// ��Ӫ�����
			getArticleById();
			return;
		}

		/**
		 * ����һ���µ�list,ѭ��������ͷ�����һ����ԭβ����ͬ��view��β�����һ����ԭͷ����ͬ��view
		 * ����������һ����ʱ����ʵ��ʾ���Ǳ��������һ��view����ʱ����ʾλ���Ƶ����ڶ����������������һ��view
		 * ͬ�����������һ����ʱ����ʵ��ʵ���Ǳ����ĵ�һ��view����ʱ��λ���Ƶ���һ�����������ĵ�һ��view
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
	 * �ı��ղ�ͼƬ
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
	 * ����ղ�
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
		if (CommonApplication.favListener != null)// ˢ��favView
			CommonApplication.favListener.refreshFav();
	}

	/**
	 * ������尴ť
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
		if (plus) {// �Ŵ�
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
	 * �ı��м��
	 * 
	 * @param plus
	 */
	protected void changeLineHeight(boolean plus) {
		int now = DataHelper.getLineHeight(this);
		if (plus) {// �Ŵ�
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
	 * �������ť
	 */
	protected void showShare() {
		int position = getViewPager().getCurrentItem();
		if (list != null && list.size() > position) {
			shareManager.showDialog(issue, list.get(position).getCatId() + "",
					list.get(position).getArticleId() + "");
		}
	}

	/**
	 * ��������ת��ָ������
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
	 * ��ʼ����Դ
	 */
	protected abstract void init();

	/**
	 * ��ȡviewpager
	 * 
	 * @return
	 */
	protected abstract CommonViewPager getViewPager();

	/**
	 * �ı��ղ�ͼƬ
	 * 
	 * @param isFavEd
	 */
	protected abstract void changeFavBtn(boolean isFavEd);

	/**
	 * �Ƿ��������尴ť(ͼ������)
	 * 
	 * @param hide
	 */
	protected abstract void hideFont(boolean hide);

	/**
	 * ��ȡ����view
	 * 
	 * @param detail
	 * @return
	 */
	protected abstract View fetchView(ArticleDetail detail);

	/**
	 * ��ȡͼ������view
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
	 * �ȴ���������
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
