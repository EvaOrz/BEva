package cn.com.modernmedia.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.widget.MyCircularViewPager;
import cn.com.modernmedia.widget.TouchLoadingImage;
import cn.com.modernmedia.widget.zoom.CSTImageView;
import cn.com.modernmediaslate.unit.ParseUtil;

public class MyCircularPagerAdapter extends MyPagerAdapter<ArticleItem> {
	private Context mContext;
	private String tag = "";
	private int width = 0, height = 0;

	private MyCircularViewPager mViewPager;

	public MyCircularPagerAdapter(Context context, List<ArticleItem> list,
			String tag, int width, int height) {
		super(context, list);
		mContext = context;
		this.tag = tag;
		this.width = width;
		this.height = height;
	}

	public void setViewPager(MyCircularViewPager vp) {
		mViewPager = vp;
	}

	@Override
	public View fetchView(ArticleItem item) {
		TouchLoadingImage loadingImage = new TouchLoadingImage(mContext, CSTImageView.CustomScaleType.INSIDE.getNativeInt(), width, height);
		loadingImage.setTag(tag);
		if (item != null) {
			if (ParseUtil.listNotNull(item.getPicList())) {
				loadingImage.setUrl(item.getPicList().get(0).getUrl());
			}
		}
		return loadingImage;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

}
