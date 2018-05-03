package cn.com.modernmedia.views.xmlparse.widget;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.views.xmlparse.XMLParse;

/**
 * 解析Button
 * 
 * @author zhuqiao
 * 
 */
public class ButtonParse extends TextViewParse {

	public ButtonParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	/**
	 * 创建Button
	 * 
	 * @param parser
	 * @param parent
	 * @return
	 */
	public View createButton(XmlPullParser parser, View parent) {
		Button button = new Button(mContext);
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
			return button;
		for (int i = 0; i < count; i++) {
			String atts = parser.getAttributeName(i);
			String value = parser.getAttributeValue(i);
			parseAtts(atts, value, button);
			if (!isLinearLayoutParent) {
				xmlParse.parseAlignAnchor(atts, value, rp);
				xmlParse.parseAlignParent(atts, value, rp);
			}
		}
		if (gravity != -1)
			button.setGravity(gravity);
		button.setPadding(padding[0], padding[1], padding[2], padding[3]);

		if (isLinearLayoutParent) {
			lp.width = xy[0];
			lp.height = xy[1];
			if (weight != -1)
				lp.weight = weight;
			if (layout_gravity != -1)
				lp.gravity = layout_gravity;
			lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			button.setLayoutParams(lp);
		} else {
			rp.width = xy[0];
			rp.height = xy[1];
			rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			button.setLayoutParams(rp);
		}
		return button;
	}

	/**
	 * 解析参数
	 * 
	 * @param atts
	 * @param value
	 * @param button
	 */
	protected void parseAtts(String atts, String value, Button button) {
		super.parseAtts(atts, value, button);
	}

}
