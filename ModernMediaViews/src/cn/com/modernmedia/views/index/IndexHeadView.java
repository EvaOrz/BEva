package cn.com.modernmedia.views.index;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.util.AdvTools;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.util.ModernMediaTools;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.model.IndexListParm;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.util.WeeklyLogEvent;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.CircularViewPager;
import cn.com.modernmediaslate.model.Entry;

/**
 * 首页焦点图
 * 
 * @author user
 * 
 */
public abstract class IndexHeadView extends BaseView implements
		FetchEntryListener, NotifyArticleDesListener {
	private static final int CHANGE = 1;
	private static final int CHANGE_DELAY = 5000;

	protected Context mContext;
	protected CircularViewPager<ArticleItem> viewPager;
	protected LinearLayout dotLl;
	protected TextView title;
	private List<ImageView> dots = new ArrayList<ImageView>();
	protected List<ArticleItem> mList = new ArrayList<ArticleItem>();
	private boolean isSetting = true;
	private boolean isAuto;
	private ArticleType articleType = ArticleType.Default;
	protected IndexListParm parm;
	private boolean shouldScroll;// 如果只有一张图，那么不监听滑动

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CHANGE) {
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
				startChange();
			}
		}

	};

	public IndexHeadView(Context context, IndexListParm parm) {
		super(context);
		mContext = context;
		this.parm = parm;
		init();
		initProperties();
	}

	/**
	 * 初始化资源
	 */
	protected abstract void init();

	private void initProperties() {
		if (parm == null)
			return;
		viewPager.getLayoutParams().height = parm.getHead_height()
				* CommonApplication.width / 720;
		viewPager.setPlaceholderRes(V.getId(parm.getHead_placeholder()));
		viewPager.setListener(this);
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
					V.setImage(dots.get(i), parm.getHead_dot_active());
				} else {
					V.setImage(dots.get(i), parm.getHead_dot());
				}
			}
			LogHelper.lodAndroidShowHeadline(position);
			AdvTools.requestImpression(item);
		}
	}

	@Override
	public void updatePage(int state) {
		isSetting = state == ViewPager.SCROLL_STATE_SETTLING;// 加载完毕
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

	@Override
	public void setData(Entry entry) {
		if (entry != null) {
			articleType = ArticleType.Default;
			if (entry instanceof IndexArticle) {
				IndexArticle indexArticle = (IndexArticle) entry;
				if (indexArticle.hasData(parm.getHead_position()))
					setDataToGallery(indexArticle.getMap().get(
							parm.getHead_position()));
			} else if (entry instanceof CatIndexArticle) {
				CatIndexArticle indexArticle = (CatIndexArticle) entry;
				if (indexArticle.hasData(parm.getHead_position()))
					setDataToGallery(indexArticle.getMap().get(
							parm.getHead_position()));
			}
		}
	}

	/**
	 * 
	 * @param list
	 * @param isSolo
	 *            true:更新完的数据，追加在开始添加；false:覆盖本来数据
	 */
	protected void setDataToGallery(final List<ArticleItem> list) {
		if (list == null)
			return;
		mList.clear();
		mList.addAll(list);
		if (!ParseUtil.listNotNull(mList))
			return;
		if (mContext instanceof CommonMainActivity) {
			if (mList.size() == 1) {
				shouldScroll = false;
			} else {
				((CommonMainActivity) mContext).setScrollView(this);
				shouldScroll = true;
				startRefresh();
			}
		}
		title.setText(mList.get(0).getTitle());
		// addOutline(list.get(0).getOutline(), parm.getItem_outline_img());
		viewPager.setDataForPager(mList);
		initDot(mList);

		PagerAdapter adapter = viewPager.getAdapter();
		if (adapter != null && adapter instanceof MyPagerAdapter) {
			((MyPagerAdapter<?>) adapter)
					.setOnItemClickListener(new MyPagerAdapter.OnItemClickListener() {

						@Override
						public void onItemClick(View v, int position) {
							ArticleItem item = mList.get(position
									% mList.size());
							LogHelper.logOpenArticleFromColumnPage(mContext,
									item.getArticleId() + "", item.getCatId()
											+ "");
							if (ParseUtil.listNotNull(dots)) {
								if (position == 0) {
									position = dots.size() - 1;
								} else if (position == mList.size() - 1) {
									position = 0;
								} else {
									position--;
								}
								LogHelper.logAndroidTouchHeadline(position);
								AdvTools.requestClick(item);
								if (ConstData.getInitialAppId() == 20) {
									WeeklyLogEvent
											.logAndroidColumnHeadviewClickCount();
								}
							}
							V.clickSlate(mContext, item, articleType);
						}
					});
		}
	}

	protected void addOutline(String outline, String outlineImg) {
	}

	/**
	 * 分享
	 */
	protected void doShare() {
		if (mContext instanceof ViewsMainActivity) {
			int position = viewPager.getCurrentItem();
			if (mList != null && mList.size() > position) {
				ModernMediaTools.shareArticleItem(mContext,
						mList.get(position), CommonApplication.issue);
			}
		}
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
		if (parm.getHead_dot_type() == 1) {
			int dot_size = mContext.getResources().getDimensionPixelOffset(
					R.dimen.atlas_dot_size);
			lp = new LinearLayout.LayoutParams(dot_size, dot_size);
		}

		lp.leftMargin = 5;
		for (int i = 1; i < itemList.size() - 1; i++) {
			iv = new ImageView(mContext);
			if (i == 1) {
				V.setImage(iv, parm.getHead_dot_active());
			} else {
				V.setImage(iv, parm.getHead_dot());
			}
			dotLl.addView(iv, lp);
			dots.add(iv);
		}
	}

	private void startChange() {
		if (mList == null || mList.size() < 2 || !isSetting || !isAuto
				|| parm.getHead_type().equals(V.IWEEKLY)) {
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

	public View getViewPager() {
		return viewPager;
	}

	public boolean isShouldScroll() {
		return shouldScroll;
	}

	@Override
	protected void reLoad() {
	}
}
