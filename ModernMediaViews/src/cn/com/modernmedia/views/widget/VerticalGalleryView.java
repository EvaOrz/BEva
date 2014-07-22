package cn.com.modernmedia.views.widget;

import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.index.IndexViewPagerItem;
import cn.com.modernmedia.views.listener.GalleryOnTouchListener;
import cn.com.modernmedia.views.listener.GalleryScrollListener;
import cn.com.modernmedia.views.util.WeeklyLogEvent;
import cn.com.modernmediaslate.model.Entry;

/**
 * 画报
 * 
 * @author ZhuQiao
 * 
 */
public class VerticalGalleryView implements FetchEntryListener,
		GalleryScrollListener {
	private static final int SIZE = 5;
	private Context mContext;
	private VerticalFlowGallery gallery;
	private List<ArticleItem> list;
	private Stack<GalleryScrollListener> stack = new Stack<GalleryScrollListener>();
	private Handler handler = new Handler();
	private IndexViewPagerItem indexViewPagerItem;

	public VerticalGalleryView(Context context,
			IndexViewPagerItem indexViewPagerItem) {
		this.mContext = context;
		this.indexViewPagerItem = indexViewPagerItem;
		init();
	}

	private void init() {
		gallery = new VerticalFlowGallery(mContext);
		gallery.setListener(this);
		ViewsApplication.positionChangedListener = gallery;
	}

	public void onDestroy() {
		ViewsApplication.positionChangedListener = null;
		recycle();
	}

	@Override
	public void scrolling(int currentX, int position) {
	}

	@Override
	public void scrollEnd(int currentIndex) {
		if (!ParseUtil.listNotNull(list))
			return;
		notifyView(currentIndex);
		notifyView(currentIndex - 1);
		notifyView(currentIndex + 1);
	}

	/**
	 * 通知当前view前一个和后一个加载图片
	 * 
	 * @param currentIndex
	 */
	private void notifyView(int index) {
		if (index >= 0 && stack.size() > index) {
			stack.get(index).scrollEnd(index);
		}
	}

	@Override
	public void destoryItem(int position) {
		if (position >= 0 && stack.size() > position)
			stack.get(position).destoryItem(position);
	}

	@Override
	public void setData(Entry entry) {
		if (entry == null)
			return;
		if (indexViewPagerItem != null)
			indexViewPagerItem.showLoading();
		gallery.reSet();
		stack.clear();
		recycle();
		if (entry instanceof CatIndexArticle) {
			if (entry instanceof CatIndexArticle) {
				final CatIndexArticle catIndexArticle = (CatIndexArticle) entry;
				handler.post(new Runnable() {

					@Override
					public void run() {
						addImage(catIndexArticle);
					}
				});
			}
		}
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (indexViewPagerItem != null)
					indexViewPagerItem.disProcess();
			}
		}, 1500);
	}

	/**
	 * 第一次进入画报时调用，只加载5张
	 * 
	 * @param list
	 */
	private void addImage(final CatIndexArticle catIndexArticle) {
		if (!ParseUtil.mapContainsKey(catIndexArticle.getMap(), 2))
			return;
		List<ArticleItem> articleItemList = catIndexArticle.getMap().get(2);
		if (!ParseUtil.listNotNull(articleItemList))
			return;
		list = articleItemList;
		for (int i = 0; i < list.size(); i++) {
			final ArticleItem item = list.get(i);
			final VerticalGalleryItem galleryItem = new VerticalGalleryItem(
					mContext);
			galleryItem.setData(item, i <= SIZE, checkIfReaded(item));
			stack.add(galleryItem);
			final int position = i;
			gallery.addListener(new GalleryOnTouchListener() {

				@Override
				public void onTouch(View view) {
					if (mContext instanceof ViewsMainActivity) {
						((ViewsMainActivity) mContext)
								.gotoGallertDetailActivity(catIndexArticle,
										position);
						if (ViewsApplication.lastestArticleId != null) {
							if (ViewsApplication.lastestArticleId
									.getUnReadedId().containsKey(
											item.getArticleId())) {
								ReadDb.getInstance(mContext).addReadArticle(
										item.getArticleId());
							}
						}
						WeeklyLogEvent.logAndroidEnterCoverflow();
					}
				}
			});
			gallery.addView(galleryItem);// 加载最后
		}
	}

	private void recycle() {
		gallery.removeAllViews();
		new Thread() {

			@Override
			public void run() {
				super.run();
				for (int i = 0; i < stack.size(); i++) {
					destoryItem(i);
				}
			}

		}.start();
		CommonApplication.callGc();
	}

	public VerticalFlowGallery getGallery() {
		return gallery;
	}

	/**
	 * 检查是否已读文章
	 * 
	 * @param item
	 * @return
	 */
	private boolean checkIfReaded(ArticleItem item) {
		if (ViewsApplication.lastestArticleId != null) {
			return ViewsApplication.lastestArticleId.getUnReadedId()
					.containsKey(item.getArticleId());
		}
		return false;
	}
}
