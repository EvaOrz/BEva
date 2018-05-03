package cn.com.modernmedia.views.xmlparse.widget;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.views.xmlparse.XMLParse;

/**
 * 解析RelativeLayout
 * 
 * @author zhuqiao
 * 
 */
public class RelativeLayoutParse extends BaseViewParse {
	private boolean isLinearLayoutParent = true;

	public RelativeLayoutParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	/**
	 * 创建RelativeLayout
	 * 
	 * @param parser
	 * @param parent
	 * @return
	 */
	public View createRelativeLayout(XmlPullParser parser, View parent) {
		RelativeLayout rl = new RelativeLayout(mContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		if (parent == null) {
			int count = parser.getAttributeCount();
			if (count == -1)
				return rl;
			for (int i = 0; i < count; i++) {
				String atts = parser.getAttributeName(i);
				String value = parser.getAttributeValue(i);
				parseAtts(atts, value, rl);
			}
			if (gravity != -1)
				rl.setGravity(gravity);

			rp = new RelativeLayout.LayoutParams(xy[0], xy[1]);
			rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			rl.setPadding(padding[0], padding[1], padding[2], padding[3]);
			rl.setLayoutParams(rp);
		} else {
			isLinearLayoutParent = xmlParse
					.isLinearLayoutParent(lp, rp, parent);
			int count = parser.getAttributeCount();
			if (count == -1)
				return rl;
			for (int i = 0; i < count; i++) {
				String atts = parser.getAttributeName(i);
				String value = parser.getAttributeValue(i);
				parseAtts(atts, value, rl);
				if (!isLinearLayoutParent) {
					xmlParse.parseAlignAnchor(atts, value, rp);
					xmlParse.parseAlignParent(atts, value, rp);
				}
			}
			if (gravity != -1)
				rl.setGravity(gravity);
			rl.setPadding(padding[0], padding[1], padding[2], padding[3]);
			if (isLinearLayoutParent) {
				lp.width = xy[0];
				lp.height = xy[1];
				if (weight != -1)
					lp.weight = weight;
				if (layout_gravity != -1)
					lp.gravity = layout_gravity;
				lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
				rl.setLayoutParams(lp);
			} else {
				rp.width = xy[0];
				rp.height = xy[1];
				rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
				rl.setLayoutParams(rp);
			}
		}
		return rl;
	}

	/**
	 * 解析参数
	 * 
	 * @param atts
	 * @param value
	 * @param rl
	 */
	private void parseAtts(String atts, String value, RelativeLayout rl) {
		super.parse(atts, value, rl);
	}
}
