package cn.com.modernmedia.views.xmlparse.article;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.ArticleItem.PhonePageList;
import cn.com.modernmedia.util.MyAnimate;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.BaseXMLDataSet;
import cn.com.modernmedia.views.xmlparse.FunctionXML;
import cn.com.modernmedia.views.xmlparse.FunctionXMLHead;
import cn.com.modernmedia.widget.AtlasViewPager;
import cn.com.modernmedia.widget.LoadingImage;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 给图集设置数据
 * 
 * @author jiancong
 * 
 */
public class XMLDataSetForAtlas extends BaseXMLDataSet {
	private int lineStartX, lineEndX;

	public XMLDataSetForAtlas(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList) {
		super(context, map, clickViewList, ninePatchViewList);
	}

	/**
	 * 设置内容
	 * 
	 * @param item
	 */
	public void setData(PhonePageList item, ArticleItem articleItem) {
		title(item.getTitle());
		desc(item.getDesc());
		date(articleItem);
	}

	/**
	 * 图集adapter设置数据
	 * 
	 * @param item
	 */
	public void setPagerItemData(PhonePageList item) {
		image(item.getUrl());
	}

	/**
	 * 图片
	 * 
	 */
	private void image(String url) {
		if (!ParseUtil.mapContainsKey(map, FunctionXML.IMAGE))
			return;
		View view = map.get(FunctionXML.IMAGE);
		if (view instanceof ImageView) {
			img(url, (ImageView) view);
		} else if (view instanceof LoadingImage) {
			img(url, (LoadingImage) view);
		}
	}

	private void img(String url, ImageView imageView) {
		if (imageView.getTag(R.id.img_placeholder) instanceof String) {
			imageView.setScaleType(ScaleType.CENTER);
			V.setImage(imageView, imageView.getTag(R.id.img_placeholder)
					.toString());
		}
		CommonApplication.finalBitmap.display(imageView, url);
	}

	private void img(String url, LoadingImage loadingImage) {
		loadingImage.setUrl(url);
	}

	private void title(String text) {
		if (!map.containsKey(FunctionXML.TITLE))
			return;
		View view = map.get(FunctionXML.TITLE);
		if (!(view instanceof TextView))
			return;
		TextView title = (TextView) view;
		title.setText(text);
	}

	private void desc(String text) {
		if (!map.containsKey(FunctionXML.DESC))
			return;
		View view = map.get(FunctionXML.DESC);
		if (!(view instanceof TextView))
			return;
		TextView desc = (TextView) view;
		desc.setText(text);
	}

	/**
	 * 设置dot
	 */
	public void dot(List<PhonePageList> itemList, List<ImageView> dots) {
		if (!map.containsKey(FunctionXMLHead.DOT))
			return;
		View view = map.get(FunctionXMLHead.DOT);
		if (!(view instanceof LinearLayout))
			return;
		LinearLayout ll = (LinearLayout) view;
		ll.removeAllViews();
		ImageView iv;
		int dot_size;
		if (ll.getTag(R.id.dot_size) instanceof Integer) {
			dot_size = (Integer) ll.getTag(R.id.dot_size);
		} else {
			dot_size = mContext.getResources().getDimensionPixelOffset(
					R.dimen.atlas_dot_size);
		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dot_size,
				dot_size);

		lp.leftMargin = 5;
		for (int i = 0; i < itemList.size(); i++) {
			iv = new ImageView(mContext);
			if (i == 0) {
				iv.setImageResource(R.drawable.dot_active);
			} else {
				iv.setImageResource(R.drawable.dot);
			}
			ll.addView(iv, lp);
			dots.add(iv);
		}
	}

	/**
	 * 获取图集viewPager
	 * 
	 * @return
	 */
	public AtlasViewPager getViewPager() {
		if (!map.containsKey(FunctionXMLAtlas.ATLAS_VIEW_PAGER))
			return new AtlasViewPager(mContext);
		View view = map.get(FunctionXMLAtlas.ATLAS_VIEW_PAGER);
		return (view instanceof AtlasViewPager) ? (AtlasViewPager) view
				: new AtlasViewPager(mContext);
	}

	/**
	 * 初始化动画
	 * 
	 * @param list
	 */
	public void initAnim(List<PhonePageList> list) {
		if (!ParseUtil.mapContainsKey(map, FunctionXMLHead.ANIM))
			return;
		View view = map.get(FunctionXMLHead.ANIM);
		if (list.size() == 1) {
			MyAnimate.startTranslateAnimation(view, 0, CommonApplication.width);
		}
	}

	/**
	 * 执行动画
	 * 
	 * @param mList
	 * @param position
	 */
	public void anim(List<PhonePageList> mList, int position) {
		if (!ParseUtil.mapContainsKey(map, FunctionXMLHead.ANIM))
			return;
		View view = map.get(FunctionXMLHead.ANIM);
		if (!ParseUtil.listNotNull(mList))
			return;
		lineEndX = Math.round(CommonApplication.width * (position + 1)
				/ mList.size());
		MyAnimate.startTranslateAnimation(view, lineStartX, lineEndX);
		lineStartX = lineEndX;
	}
}
