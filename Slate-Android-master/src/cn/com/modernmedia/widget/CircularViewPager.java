package cn.com.modernmedia.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmedia.model.ArticleItem;

/**
 * 循环viewpager
 * 
 * @author ZhuQiao
 * 
 */
public abstract class CircularViewPager extends ViewPager {
	private CircularViewPager me;
	private MyPagerAdapter adapter;
	private Context mContext;
	private NotifyArticleDesListener listener;
	private List<ArticleItem> list;
	private int dirPosition = 0;

	public CircularViewPager(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public CircularViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		me = this;
		this.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (list == null || list.size() <= position)
					return;
				dirPosition = position;
				if (listener != null)
					listener.updateDes(position);
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state != ViewPager.SCROLL_STATE_DRAGGING) {
					if (list.size() != 1) {
						if (dirPosition == 0) {
							me.setCurrentItem(list.size() - 2, false);// 其实是最后一个view
						} else if (dirPosition == list.size() - 1) {
							me.setCurrentItem(1, false);// 其实是第一个view
						}
					}
				}
				if (listener != null)
					listener.updatePage(state);
			}
		});
	}

	public void setDataForPager(List<ArticleItem> list) {
		if (list.size() > 1) {
			List<ArticleItem> newList = new ArrayList<ArticleItem>();
			newList.add(list.get(list.size() - 1));
			newList.addAll(list);
			newList.add(list.get(0));
			list.clear();
			list.addAll(newList);
			newList = null;
		}
		this.list = list;
		adapter = fetchAdapter(mContext, list);
		this.setAdapter(adapter);
		if (list.size() > 1)
			this.setCurrentItem(1, false);
	}

	public void setListener(NotifyArticleDesListener listener) {
		this.listener = listener;
		listener.updateDes(0);
	}

	public void clickItem() {
	}

	public abstract MyPagerAdapter fetchAdapter(Context context,
			List<ArticleItem> list);
}
