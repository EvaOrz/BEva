package cn.com.modernmedia.businessweek.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.VideoPlayerActivity;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.MyApplication;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.listener.SlateListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.widget.BaseView;

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
	private MyCircularViewPager viewPager;
	private LinearLayout dotLl;
	private TextView title;
	private List<ImageView> dots = new ArrayList<ImageView>();
	private List<ArticleItem> list;
	private boolean isSetting = true;
	private boolean isAuto;
	private SlateListener listener = new SlateListener() {

		@Override
		public void linkNull(ArticleItem item) {
			if (item.getAdv().getAdvProperty().getIsadv() == 0) {
				((MainActivity) mContext).gotoArticleActivity(
						item.getArticleId(), item.getCatId(), true);
			}
		}

		@Override
		public void httpLink(ArticleItem item, Intent intent) {
			if (item.getAdv().getAdvProperty().getIsadv() == 1)
				((MainActivity) mContext).startActivity(intent);
		}

		@Override
		public void articleLink(ArticleItem item, int articleId) {
			((MainActivity) mContext).gotoArticleActivity(articleId,
					item.getCatId(), true);
		}

		@Override
		public void video(ArticleItem item, String path) {
			if (item.getAdv().getAdvProperty().getIsadv() == 1
					&& path.toLowerCase().endsWith(".mp4")) {
				Intent intent = new Intent(mContext, VideoPlayerActivity.class);
				intent.putExtra("vpath", path);
				mContext.startActivity(intent);
			}
		}

		@Override
		public void column(String columnId) {
		}

		@Override
		public void image(String url) {
		}

		@Override
		public void gallery(String url) {
		}
	};

	private NotifyArticleDesListener pagerListener = new NotifyArticleDesListener() {

		@Override
		public void updatePage(int state) {
			isSetting = state == ViewPager.SCROLL_STATE_SETTLING;// 加载完毕
			startChange();
		}

		@Override
		public void updateDes(int position) {
			if (list != null && list.size() > position && list.size() > 1) {
				title.setText(list.get(position).getTitle());
				if (position == 0) {
					position = dots.size() - 1;
				} else if (position == list.size() - 1) {
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
		viewPager = (MyCircularViewPager) findViewById(R.id.index_gallery);
		viewPager.getLayoutParams().height = MyApplication.width / 2;
		dotLl = (LinearLayout) findViewById(R.id.index_gallery_dot);
		title = (TextView) findViewById(R.id.index_head_title);
		setListener(listener);
		viewPager.setListener(pagerListener);
	}

	@Override
	public void setData(Entry entry) {
		if (entry != null) {
			if (entry instanceof IndexArticle) {
				setDataToGallery(((IndexArticle) entry).getTitleArticleList());
			} else if (entry instanceof CatIndexArticle) {
				setDataToGallery(((CatIndexArticle) entry)
						.getTitleActicleList());
			}
		}
	}

	private void setDataToGallery(final List<ArticleItem> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		this.list = list;
		if (mContext instanceof MainActivity) {
			if (list.size() == 1) {
				((MainActivity) mContext).setScrollView(2, null);
			} else {
				((MainActivity) mContext).setScrollView(0, viewPager);
				startRefresh();
			}
		}
		title.setText(list.get(0).getTitle());
		viewPager.setDataForPager(list);
		initDot(list);

		PagerAdapter adapter = viewPager.getAdapter();
		if (adapter != null && adapter instanceof MyPagerAdapter) {
			((MyPagerAdapter) adapter)
					.setOnItemClickListener(new MyPagerAdapter.OnItemClickListener() {

						@Override
						public void onItemClick(View v, int position) {
							if (mContext instanceof MainActivity) {
								ArticleItem item = list.get(position
										% list.size());
								LogHelper.logOpenArticleFromColumnPage(
										mContext, item.getArticleId() + "",
										item.getCatId() + "");
								clickSlate(item);
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
		if (list == null || list.size() < 2 || !isSetting || !isAuto) {
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
