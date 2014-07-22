package cn.com.modernmedia.businessweek.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.unit.BusinessweekTools;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.CircularViewPager;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页滚屏view(作为首页listview的headview)
 * 
 * @author ZhuQiao
 * 
 */
public class IndexHeadView extends BaseView implements FetchEntryListener {
	private static final int CHANGE = 1;
	private static final int CHANGE_DELAY = 5000;
	private Context mContext;
	private CircularViewPager viewPager;
	private LinearLayout dotLl;
	private TextView title;
	private List<ImageView> dots = new ArrayList<ImageView>();
	private List<ArticleItem> mList = new ArrayList<ArticleItem>();
	private boolean isSetting = true;
	private boolean isAuto;
	private ArticleType articleType = ArticleType.Default;

	private NotifyArticleDesListener pagerListener = new NotifyArticleDesListener() {

		@Override
		public void updatePage(int state) {
			isSetting = state == ViewPager.SCROLL_STATE_SETTLING;// 加载完毕
//			startChange();
		}

		@Override
		public void updateDes(int position) {
			if (mList != null && mList.size() > position && mList.size() > 1) {
				ArticleItem item = mList.get(position);
				title.setText(item.getTitle());
				if (position == 0) {
					position = dots.size() - 1;
				} else if (position == mList.size() - 1) {
					position = 0;
				} else {
					position--;
				}
				for (int i = 0; i < dots.size(); i++) {
					if (i == position) {
						dots.get(i)
								.setBackgroundResource(R.drawable.dot_active);
					} else {
						dots.get(i).setBackgroundResource(R.drawable.dot);
					}
				}
				LogHelper.lodAndroidShowHeadline(position);
			}

		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CHANGE) {
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
				startChange();
			}
		}

	};

	public IndexHeadView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.page_gallery, null));
		viewPager = (CircularViewPager) findViewById(R.id.index_gallery);
		viewPager.getLayoutParams().height = MyApplication.width / 2;
		viewPager.setPlaceholderRes(R.drawable.placeholder);
		dotLl = (LinearLayout) findViewById(R.id.index_gallery_dot);
		title = (TextView) findViewById(R.id.index_head_title);
		viewPager.setListener(pagerListener);
	}

	@Override
	public void setData(Entry entry) {
		if (entry != null) {
			articleType = ArticleType.Default;
			if (entry instanceof IndexArticle) {
				setDataToGallery(((IndexArticle) entry).getTitleArticleList());
			} else if (entry instanceof CatIndexArticle) {
				setDataToGallery(((CatIndexArticle) entry)
						.getTitleActicleList());
			}
		}
	}

	/**
	 * 独立栏目
	 * 
	 * @param soloHeadIndex
	 */
	public void setSoloData(List<ArticleItem> soloHeadIndex) {
		articleType = ArticleType.Solo;
		setDataToGallery(soloHeadIndex);
	}

	/**
	 * 获取当前list;当是独立栏目时，如果从服务器上返会的list。size为0，而当前headview不为空，那么不删除headview
	 * 
	 * @return
	 */
	public List<ArticleItem> getDataList() {
		return mList;
	}

	/**
	 * 
	 * @param list
	 * @param isSolo
	 *            true:更新完的数据，追加在开始添加；false:覆盖本来数据
	 */
	private void setDataToGallery(final List<ArticleItem> list) {
		if (list == null)
			return;
		mList.clear();
		mList.addAll(list);
		if (!ParseUtil.listNotNull(mList))
			return;
		if (mContext instanceof MainActivity) {
			if (mList.size() == 1) {
				((MainActivity) mContext).setScrollView(2, null);
			} else {
				((MainActivity) mContext).setScrollView(0, viewPager);
				startRefresh();
			}
		}
		title.setText(mList.get(0).getTitle());
		viewPager.setDataForPager(mList);
		initDot(mList);

		PagerAdapter adapter = viewPager.getAdapter();
		if (adapter != null && adapter instanceof MyPagerAdapter) {
			((MyPagerAdapter) adapter)
					.setOnItemClickListener(new MyPagerAdapter.OnItemClickListener() {

						@Override
						public void onItemClick(View v, int position) {
							if (mContext instanceof MainActivity) {
								ArticleItem item = mList.get(position
										% mList.size());
								LogHelper.logOpenArticleFromColumnPage(
										mContext, item.getArticleId() + "",
										item.getCatId() + "");
								if (ParseUtil.listNotNull(dots)) {
									if (position == 0) {
										position = dots.size() - 1;
									} else if (position == mList.size() - 1) {
										position = 0;
									} else {
										position--;
									}
									LogHelper.logAndroidTouchHeadline(position);
								}
								BusinessweekTools.clickSlate(
										IndexHeadView.this, mContext, item,
										articleType);
							}
						}
					});
		}
	}

	public ViewPager getViewPager() {
		return viewPager;
	}

	/**
	 * 初始化dot
	 * 
	 * @param itemList
	 */
	private void initDot(final List<ArticleItem> itemList) {
		dotLl.removeAllViews();
		dots.clear();
		if (itemList.size() == 1)
			return;
		ImageView iv;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = 5;
		for (int i = 1; i < itemList.size() - 1; i++) {
			iv = new ImageView(mContext);
			if (i == 1) {
				iv.setBackgroundResource(R.drawable.dot_active);
			} else {
				iv.setBackgroundResource(R.drawable.dot);
			}
			dotLl.addView(iv, lp);
			dots.add(iv);
		}
	}

	private void startChange() {
		if (mList == null || mList.size() < 2 || !isSetting || !isAuto) {
			return;
		}
		Message msg = handler.obtainMessage();
		if (handler.hasMessages(CHANGE)) {
			handler.removeMessages(CHANGE);
		}
		msg.what = 1;
		handler.sendMessageDelayed(msg, CHANGE_DELAY);
	}

	/**
	 * 开始自动切换
	 */
	public void startRefresh() {
		isAuto = true;
		isSetting = true;
		startChange();
	}

	/**
	 * 停止后台自动切换
	 */
	public void stopRefresh() {
		if (handler.hasMessages(1)) {
			handler.removeMessages(1);
		}
		isAuto = false;
	}

	@Override
	protected void reLoad() {
	}
}
