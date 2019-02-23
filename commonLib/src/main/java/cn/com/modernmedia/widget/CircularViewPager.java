package cn.com.modernmedia.widget;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.listener.NotifyArticleDesListener;
import cn.com.modernmediaslate.unit.ImageScaleType;

/**
 * 循环viewpager
 * 
 * @author ZhuQiao
 * 
 */
public class CircularViewPager<T> extends LoopViewPager {
	private Context mContext;
	private NotifyArticleDesListener listener;
	private List<T> list;
	/**
	 * 占位图资源
	 */
	private int placeholderRes = -1;
	private String scaleType = ImageScaleType.FIT_XY;

	public CircularViewPager(Context context) {
		this(context, null);
	}

	public CircularViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		this.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (getAdapter() instanceof LoopPagerAdapter) {
					LoopPagerAdapter infAdapter = (LoopPagerAdapter) getAdapter();
					position = position % infAdapter.getRealCount();
				}
				if (list == null || list.size() <= position)
					return;
				if (listener != null)
					listener.updateDes(position);
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state != ViewPager.SCROLL_STATE_DRAGGING) {
				}
				if (listener != null)
					listener.updatePage(state);
			}
		});
	}

	/**
	 * 设置占位图
	 * 
	 * @param placeholderRes
	 */
	public void setPlaceholderRes(int placeholderRes) {
		this.placeholderRes = placeholderRes;
	}

	/**
	 * 设置图片scaletype,默认fit_xy
	 * 
	 * @param scaleType
	 */
	public void setScaleType(String scaleType) {
		if (TextUtils.isEmpty(scaleType))
			scaleType = ImageScaleType.FIT_XY;
		this.scaleType = scaleType;
	}

	/**
	 * 给viewpager设置，并且自动设置默认pageradapter
	 * 
	 * @param list
	 */
	public void setDataForPager(List<T> list) {
		setDataForPager(list, list.size());
	}

	public void setDataForPager(List<T> list, int position) {
		setDataForPager(list, position, fetchAdapter(mContext, list));
	}

	public void setDataForPager(List<T> list, int position,
			MyPagerAdapter<T> adapter) {
		LoopPagerAdapter _adapter = new LoopPagerAdapter(adapter);
		this.list = list;
		this.setAdapter(_adapter);
		this.setCurrentItem(position, false);
		if (position == 0 && listener != null) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// 等待view渲染
					listener.updateDes(0);
				}
			}, 100);
		}
	}

	public void setListener(NotifyArticleDesListener listener) {
		this.listener = listener;
	}

	public MyPagerAdapter<T> fetchAdapter(Context context, List<T> list) {
		return new MyPagerAdapter<T>(context, list, placeholderRes, scaleType);
	}
}
