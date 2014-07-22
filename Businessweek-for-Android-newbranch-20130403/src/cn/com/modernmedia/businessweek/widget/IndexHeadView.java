package cn.com.modernmedia.businessweek.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.VideoPlayerActivity;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.businessweek.adapter.PageGalleryAdapter;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.listener.SlateListener;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.CatIndexArticle;
import cn.com.modernmedia.model.Entry;
import cn.com.modernmedia.model.IndexArticle;
import cn.com.modernmedia.util.LogHelper;
import cn.com.modernmedia.widget.BaseView;
import cn.com.modernmedia.widget.PageGallery;

/**
 * 首页滚屏view(作为首页listview的headview)
 * 
 * @author ZhuQiao
 * 
 */
public class IndexHeadView extends BaseView implements FetchEntryListener {
	private Context mContext;
	private PageGallery gallery;
	private PageGalleryAdapter galleryAdapter;
	private LinearLayout dotLl;
	private TextView title;
	private List<ImageView> dots = new ArrayList<ImageView>();
	private SlateListener listener = new SlateListener() {

		@Override
		public void linkNull(ArticleItem item) {
			if (item.getAdv().getAdvProperty().getIsadv() == 0) {
				((MainActivity) mContext).gotoArticleActivity(
						item.getArticleId(), true);
			}
		}

		@Override
		public void httpLink(ArticleItem item, Intent intent) {
			if (item.getAdv().getAdvProperty().getIsadv() == 1)
				((MainActivity) mContext).startActivity(intent);
		}

		@Override
		public void articleLink(ArticleItem item, int articleId) {
			((MainActivity) mContext).gotoArticleActivity(articleId, true);
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

	public IndexHeadView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.page_gallery, null));
		gallery = (PageGallery) findViewById(R.id.index_gallery);
		dotLl = (LinearLayout) findViewById(R.id.index_gallery_dot);
		title = (TextView) findViewById(R.id.index_head_title);
		setListener(listener);
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
		if (list == null || list.isEmpty())
			return;
		galleryAdapter = new PageGalleryAdapter(mContext);
		gallery.setAdapter(galleryAdapter);
		galleryAdapter.setArticlelist(list);
		initDot(list);
		// 改变title的值
		// title.setText(list.get(0).getTitle());
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContext instanceof MainActivity) {
					ArticleItem item = list.get(position % list.size());
					LogHelper.logOpenArticleFromColumnPage(mContext,
							item.getArticleId() + "", item.getCatId() + "");
					clickSlate(item);
				}
			}
		});
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					final int position, long id) {
				postDelayed(new Runnable() {

					@Override
					public void run() {
						int index = position % list.size();
						title.setText(list.get(index).getTitle());
						for (int i = 0; i < dots.size(); i++) {
							if (i == index) {
								dots.get(i).setBackgroundResource(
										R.drawable.dot_active);
							} else {
								dots.get(i).setBackgroundResource(
										R.drawable.dot);
							}
						}
					}

				}, 200);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	public Gallery getGallery() {
		return gallery;
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
		for (int i = 0; i < itemList.size(); i++) {
			iv = new ImageView(mContext);
			if (i == 0) {
				iv.setBackgroundResource(R.drawable.dot_active);
			} else {
				iv.setBackgroundResource(R.drawable.dot);
			}
			dotLl.addView(iv, lp);
			dots.add(iv);
		}
	}

	@Override
	protected void reLoad() {
	}
}
