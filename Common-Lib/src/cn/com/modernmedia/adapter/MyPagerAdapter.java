package cn.com.modernmedia.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ParseUtil;

/**
 * 循环viewpager的适配器
 * 
 * @author user
 * 
 * @param <T>
 */
public class MyPagerAdapter<T> extends PagerAdapter {
	private List<T> list = new ArrayList<T>();
	private OnItemClickListener onItemClickListener;
	private Context mContext;
	/**
	 * 占位图资源
	 */
	private int placeholderRes;

	public static interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}

	public MyPagerAdapter(Context context, List<T> list) {
		this(context, list, -1);
	}

	public MyPagerAdapter(Context context, List<T> list, int placeholderRes) {
		mContext = context;
		this.list = list;
		this.placeholderRes = placeholderRes;
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
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
		CommonApplication.callGc();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		final View view = fetchView(list.get(position));
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onItemClickListener != null)
					onItemClickListener.onItemClick(view, position);
			}
		});
		container.addView(view);
		return view;
	}

	/**
	 * 获取view.默认只有只有imageview，如果是特殊view,请重载此方法
	 * 
	 * @param item
	 * @return
	 */
	public View fetchView(T t) {
		if (!(t instanceof ArticleItem))
			return new View(mContext);
		ArticleItem item = (ArticleItem) t;
		String url = "";
		if (item != null) {
			if (item.getAdvSource() != null) {
				// 广告
				url = item.getAdvSource().getUrl();
			}
			if (TextUtils.isEmpty(url)) {
				List<String> list = item.getPicList();
				if (ParseUtil.listNotNull(list)) {
					url = list.get(0);
				}
			}
		}

		ImageView view = new ImageView(mContext);
		view.setLayoutParams(new Gallery.LayoutParams(
				android.widget.Gallery.LayoutParams.FILL_PARENT,
				android.widget.Gallery.LayoutParams.FILL_PARENT));
		if (placeholderRes > 0)
			view.setImageResource(placeholderRes);
		view.setScaleType(ScaleType.CENTER);
		CommonApplication.finalBitmap.display(view, url);
		return view;
	};
}
