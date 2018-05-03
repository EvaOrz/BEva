package cn.com.modernmedia.views.xmlparse;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;

import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.MyAnimate;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.head.IndexHeadCircularViewPager;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 为headview设置数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSetForHead extends BaseXMLDataSet {
	private int lineStartX, lineEndX, padding;

	public XMLDataSetForHead(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList) {
		super(context, map, clickViewList, ninePatchViewList);
		initPadding();
	}

	private void initPadding() {
		if (!map.containsKey(FunctionXML.FRAME))
			return;
		View view = map.get(FunctionXML.FRAME);
		padding = view.getPaddingLeft();
		padding += view.getPaddingRight();
	}

	/**
	 * 获取viewpager
	 * 
	 * @return
	 */
	public IndexHeadCircularViewPager getViewPager() {
		if (!ParseUtil.mapContainsKey(map, FunctionXMLHead.VIEW_PAGER))
			return null;
		View view = map.get(FunctionXMLHead.VIEW_PAGER);
		if (view instanceof IndexHeadCircularViewPager)
			return (IndexHeadCircularViewPager) view;
		return null;
	}


	public void showHead(){
		if (map.containsKey(FunctionXML.TITLE)){
			map.get(FunctionXML.TITLE).setVisibility(View.VISIBLE);
		}
		if (map.containsKey(FunctionXML.OUTLINE)){
			map.get(FunctionXML.OUTLINE).setVisibility(View.VISIBLE);
		}
		if (map.containsKey(FunctionXMLHead.DOT)){
			map.get(FunctionXMLHead.DOT).setVisibility(View.VISIBLE);
		}
		if (map.containsKey(FunctionXMLHead.ANIM)){
			map.get(FunctionXMLHead.ANIM).setVisibility(View.VISIBLE);
		}
	}

	public void unShowHead(){
		if (map.containsKey(FunctionXML.TITLE)){
			map.get(FunctionXML.TITLE).setVisibility(View.GONE);
		}
		if (map.containsKey(FunctionXML.OUTLINE)){
			map.get(FunctionXML.OUTLINE).setVisibility(View.GONE);
		}
		if (map.containsKey(FunctionXMLHead.DOT)){
			map.get(FunctionXMLHead.DOT).setVisibility(View.GONE);
		}
		if (map.containsKey(FunctionXMLHead.ANIM)){
			map.get(FunctionXMLHead.ANIM).setVisibility(View.GONE);
		}

	}
	/**
	 * 更新内容
	 * 
	 * @param item
	 */
	public void update(ArticleItem item) {
		title(item, null);
		desc(item, 0, null);
		outline(item, 0, null);
		tag(item, 0, null);
		date(item);
		fav(item);
		subTitle(item, 0, null);
		createUser(item, 0, null);
		modifyUser(item, 0, null);
		ninePatch();
		registerClick(item, ArticleType.Default);
	}

	/**
	 * 设置dot
	 */
	public void dot(List<ArticleItem> itemList, List<ImageView> dots) {
		if (!ParseUtil.mapContainsKey(map, FunctionXMLHead.DOT))
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
	 * 初始化动画
	 * 
	 * @param list
	 */
	public void initAnim(List<ArticleItem> list) {
		if (!ParseUtil.mapContainsKey(map, FunctionXMLHead.ANIM))
			return;
		View view = map.get(FunctionXMLHead.ANIM);
		if (list.size() == 1) {
			MyAnimate.startTranslateAnimation(view, 0, CommonApplication.width
					- padding);
		}
	}

	/**
	 * 执行动画
	 * 
	 * @param mList
	 * @param position
	 */
	public void anim(List<ArticleItem> mList, int position) {
		if (!ParseUtil.mapContainsKey(map, FunctionXMLHead.ANIM))
			return;
		View view = map.get(FunctionXMLHead.ANIM);
		if (!ParseUtil.listNotNull(mList))
			return;
		lineEndX = Math.round((CommonApplication.width - padding)
				* (position + 1) / mList.size());
		MyAnimate.startTranslateAnimation(view, lineStartX, lineEndX);
		lineStartX = lineEndX;
	}
}
