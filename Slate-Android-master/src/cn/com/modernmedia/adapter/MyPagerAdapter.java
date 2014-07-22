package cn.com.modernmedia.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleItem;

public abstract class MyPagerAdapter extends PagerAdapter {
	private List<ArticleItem> list = new ArrayList<ArticleItem>();
	private OnItemClickListener onItemClickListener;

	public static interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}

	public MyPagerAdapter(Context context, List<ArticleItem> list) {
		this.list = list;
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
				if(onItemClickListener!=null)
					onItemClickListener.onItemClick(view, position);
			}
		});
		container.addView(view);
		return view;
	}

	public abstract View fetchView(ArticleItem item);
}
