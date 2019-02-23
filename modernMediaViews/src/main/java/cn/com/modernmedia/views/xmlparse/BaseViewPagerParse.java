package cn.com.modernmedia.views.xmlparse;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.views.xmlparse.widget.BaseViewParse;

/**
 * 基础viewpager解析类
 * 
 * @author jiancong
 * 
 */
public abstract class BaseViewPagerParse extends BaseViewParse {

	public BaseViewPagerParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	/**
	 * 创建ViewPager
	 * 
	 * @param parser
	 * @param parent
	 * @return
	 */
	public View createViewPager(XmlPullParser parser, View parent) {
		ViewPager viewPager = getBaseViewPager();
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		boolean isLinearLayoutParent = xmlParse.isLinearLayoutParent(lp, rp,
				parent);

		int count = parser.getAttributeCount();
		if (count == -1)
			return viewPager;
		for (int i = 0; i < count; i++) {
			String atts = parser.getAttributeName(i);
			String value = parser.getAttributeValue(i);
			parse(atts, value, viewPager);
			if (!isLinearLayoutParent) {
				xmlParse.parseAlignAnchor(atts, value, rp);
				xmlParse.parseAlignParent(atts, value, rp);
			}
		}
		viewPager.setPadding(padding[0], padding[1], padding[2], padding[3]);

		if (isLinearLayoutParent) {
			lp.width = xy[0];
			lp.height = xy[1];
			if (weight != -1)
				lp.weight = weight;
			if (layout_gravity != -1)
				lp.gravity = layout_gravity;
			lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			viewPager.setLayoutParams(lp);
		} else {
			rp.width = xy[0];
			rp.height = xy[1];
			rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			viewPager.setLayoutParams(rp);
		}
		return viewPager;
	}

	protected abstract ViewPager getBaseViewPager();
}
