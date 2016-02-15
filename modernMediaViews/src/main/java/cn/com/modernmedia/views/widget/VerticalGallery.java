package cn.com.modernmedia.views.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.db.ReadDb;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagArticleList;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.WeeklyLogEvent;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.ViewsMainActivity;
import cn.com.modernmedia.views.index.IndexView;
import cn.com.modernmedia.views.listener.FlowPositionChangedListener;
import cn.com.modernmedia.views.listener.NotifyLastestChangeListener;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.widget.VerticalViewPager.OnPageChangeListener;
import cn.com.modernmedia.views.widget.VerticalViewPager.ScrollListener;
import cn.com.modernmedia.widget.RedProcess;
import cn.com.modernmediaslate.SlateApplication;
import cn.com.modernmediaslate.listener.ImageDownloadStateListener;
import cn.com.modernmediaslate.model.Entry;
import cn.com.modernmediaslate.unit.ParseUtil;

public class VerticalGallery {
	private Context mContext;
	private View view, frame;
	private VerticalViewPager viewPager;
	private VerticalGalleryAdapter adapter;
	private List<ArticleItem> list = new ArrayList<ArticleItem>();
	private int currY = -1;
	private TagArticleList articleList;

	private int width, height, space;

	private boolean stopCheckNav = false;

	public VerticalGallery(Context context) {
		mContext = context;
		init();
		ViewsApplication.positionChangedListener = new FlowPositionChangedListener() {

			@Override
			public void setCurrentPosition(int position) {
				if (viewPager != null) {
					viewPager.setCurrentItem(position, false);
					setSelection(IndexView.height);
				}
			}
		};
	}

	@SuppressLint("InflateParams")
	private void init() {
		height = CommonApplication.height - IndexView.BAR_HEIGHT;
		height = height * 7 / 10;
		width = height * 2 / 3;
		space = height * 3 / 85;

		view = LayoutInflater.from(mContext).inflate(R.layout.vertical_gallery,
				null);
		frame = view.findViewById(R.id.gallery_view_pager_frame);
		viewPager = (VerticalViewPager) view
				.findViewById(R.id.gallery_view_pager);
		viewPager.getLayoutParams().width = width;
		viewPager.getLayoutParams().height = height + space;
		((LayoutParams) viewPager.getLayoutParams()).topMargin = space
				+ IndexView.BAR_HEIGHT;
		viewPager.setInit(true);
		adapter = new VerticalGalleryAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setPageMargin(space);
		viewPager.setOffscreenPageLimit(3);

		frame.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return viewPager.dispatchTouchEvent(event);
			}
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				frame.invalidate();
				viewPager.setInit(false);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		viewPager.setScrollListener(new ScrollListener() {

			@Override
			public void scroll(int y) {
				if (stopCheckNav)
					return;
				((ViewsMainActivity) mContext).callNavPadding(-y);
				if (currY != -1)
					currY = IndexView.height;
			}
		});
	}

	public void setData(Entry entry, Template template) {
		list.clear();
		if (entry instanceof TagArticleList) {
			articleList = (TagArticleList) entry;
			for (int i : template.getList().getPosition()) {
				if (ParseUtil.mapContainsKey(articleList.getMap(), i))
					list.addAll(articleList.getMap().get(i));
			}
			adapter.notifyDataSetChanged();
			setSelection(IndexView.height);
		}
	}

	public View getView() {
		return view;
	}

	public VerticalViewPager getViewPager() {
		return viewPager;
	}

	public void setSelection(int height) {
		if (SlateApplication.mConfig.getNav_hide() == 0 || currY == height)
			return;
		currY = height;
		viewPager.setCurrentItem(0, false);
		if (((ViewsMainActivity) mContext).getNavBarStatus()) {
			viewPager.scrollTo(0, IndexView.BAR_HEIGHT - IndexView.height);
		} else {
			viewPager.scrollTo(0, IndexView.BAR_HEIGHT);
		}
	}

	public void setStopCheckNav(boolean stopCheckNav) {
		this.stopCheckNav = stopCheckNav;
	}

	@SuppressLint("InflateParams")
	private class VerticalGalleryAdapter extends VerticalPagerAdapter {
		private int mChildCount = 0;

		@Override
		public void notifyDataSetChanged() {
			mChildCount = getCount();
			super.notifyDataSetChanged();
		}

		@Override
		public int getItemPosition(Object object) {
			if (mChildCount > 0) {
				mChildCount--;
				return POSITION_NONE;
			}
			return super.getItemPosition(object);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final ArticleItem item = list.get(position);
			View view = fetchView(item);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((ViewsMainActivity) mContext).gotoGallertDetailActivity(
							list, item, position);
					if (ConstData.getAppId() != 20) {
						ReadDb.getInstance(mContext).addReadArticle(
								item.getArticleId());
					} else if (ViewsApplication.lastestArticleId != null) {
						if (ViewsApplication.lastestArticleId.getUnReadedId()
								.contains(item.getArticleId())) {
							ReadDb.getInstance(mContext).addReadArticle(
									item.getArticleId());
						}
					}
					WeeklyLogEvent.logAndroidEnterCoverflow(mContext);
				}
			});
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		private View fetchView(final ArticleItem item) {
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.vertical_gallery_item, null);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.vertical_gallery_img);
			final View newImg = view
					.findViewById(R.id.vertical_gallery_new_img);
			TextView title = (TextView) view
					.findViewById(R.id.vertical_gallery_title);
			TextView desc = (TextView) view
					.findViewById(R.id.vertical_gallery_desc);
			final RedProcess process = (RedProcess) view
					.findViewById(R.id.vertical_gallery_process);

			boolean isRead = V.articleIsReaded(item);
			newImg.setVisibility(isRead ? View.GONE : View.VISIBLE);
			process.setVisibility(View.VISIBLE);
			process.start();
			imageView.setImageBitmap(null);
			String url = "";
			if (ParseUtil.listNotNull(item.getPicList())) {
				url = item.getPicList().get(0).getUrl();
			} else if (ParseUtil.listNotNull(item.getThumbList())) {
				url = item.getThumbList().get(0).getUrl();
			}
			CommonApplication.finalBitmap.display(imageView, url,
					new ImageDownloadStateListener() {

						@Override
						public void loading() {
							process.setVisibility(View.VISIBLE);
							process.start();
						}

						@Override
						public void loadOk(Bitmap bitmap,
								NinePatchDrawable drawable) {
							process.stop();
							process.setVisibility(View.GONE);
						}

						@Override
						public void loadError() {
							process.setVisibility(View.VISIBLE);
							process.start();
						}
					});
			title.setText(item.getTitle());
			desc.setText(item.getDesc());
			if (!isRead) {
				ViewsApplication.addListener(url,
						new NotifyLastestChangeListener() {

							@Override
							public void changeCount() {
								if (ViewsApplication.readedArticles
										.contains(Integer.valueOf(item
												.getArticleId()))) {
									newImg.setVisibility(View.GONE);
								}
							}
						});
			}

			return view;
		}

	}
}
