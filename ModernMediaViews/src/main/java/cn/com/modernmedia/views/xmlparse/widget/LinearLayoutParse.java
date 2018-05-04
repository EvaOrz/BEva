package cn.com.modernmedia.views.xmlparse.widget;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 解析LinearLayout
 * 
 * @author zhuqiao
 * 
 */
public class LinearLayoutParse extends BaseViewParse {
	private int weightSum = 1;
	private boolean isVertical = false;
	private boolean isLinearLayoutParent = true;

	public LinearLayoutParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	/**
	 * 创建LinearLayout
	 * 
	 * @param parser
	 * @param parent
	 * @return
	 */
	public View createLinearLayout(XmlPullParser parser, View parent) {
		LinearLayout ll = new LinearLayout(mContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		if (parent == null) {
			int count = parser.getAttributeCount();
			if (count == -1)
				return ll;
			for (int i = 0; i < count; i++) {
				String atts = parser.getAttributeName(i);
				String value = parser.getAttributeValue(i);
				parseAtts(atts, value, ll);
			}

			lp = new LinearLayout.LayoutParams(xy[0], xy[1]);
			if (weight != -1)
				lp.weight = weight;
			if (weightSum != 1)
				ll.setWeightSum(weightSum);
			if (gravity != -1)
				ll.setGravity(gravity);
			if (layout_gravity != -1)
				lp.gravity = layout_gravity;
			ll.setOrientation(isVertical ? LinearLayout.VERTICAL
					: LinearLayout.HORIZONTAL);
			lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			ll.setPadding(padding[0], padding[1], padding[2], padding[3]);
			ll.setLayoutParams(lp);
		} else {
			isLinearLayoutParent = xmlParse
					.isLinearLayoutParent(lp, rp, parent);
			int count = parser.getAttributeCount();
			if (count == -1)
				return ll;
			for (int i = 0; i < count; i++) {
				String atts = parser.getAttributeName(i);
				String value = parser.getAttributeValue(i);
				parseAtts(atts, value, ll);
				if (!isLinearLayoutParent) {
					xmlParse.parseAlignAnchor(atts, value, rp);
					xmlParse.parseAlignParent(atts, value, rp);
				}
			}

			if (weightSum != 1)
				ll.setWeightSum(weightSum);
			if (gravity != -1)
				ll.setGravity(gravity);
			ll.setOrientation(isVertical ? LinearLayout.VERTICAL
					: LinearLayout.HORIZONTAL);
			ll.setPadding(padding[0], padding[1], padding[2], padding[3]);

			if (isLinearLayoutParent) {
				lp.width = xy[0];
				lp.height = xy[1];
				if (weight != -1)
					lp.weight = weight;
				if (layout_gravity != -1)
					lp.gravity = layout_gravity;
				lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
				ll.setLayoutParams(lp);
			} else {
				rp.width = xy[0];
				rp.height = xy[1];
				rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
				ll.setLayoutParams(rp);
			}
		}

		return ll;
	}

	/**
	 * 解析参数
	 * 
	 * @param atts
	 * @param value
	 * @param ll
	 */
	private void parseAtts(String atts, String value, LinearLayout ll) {
		super.parse(atts, value, ll);
		if (atts.equalsIgnoreCase("orientation")) {
			if (value.equalsIgnoreCase("vertical"))
				isVertical = true;
		} else if (atts.equalsIgnoreCase("weightSum"))
			weightSum = ParseUtil.stoi(value, 1);
	}
}
