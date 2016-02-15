package cn.com.modernmedia.views.xmlparse.widget;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.views.widget.FullScreenVideoView;
import cn.com.modernmedia.views.xmlparse.XMLParse;

/**
 * 解析VideoView
 * 
 * @author zhuqiao
 *
 */
public class FullScreenVideoViewParse extends BaseViewParse {

	public FullScreenVideoViewParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	/**
	 * 创建View
	 * 
	 * @param parser
	 * @param parent
	 * @return
	 */
	public FullScreenVideoView createVideoView(XmlPullParser parser, View parent) {
		FullScreenVideoView view = new FullScreenVideoView(mContext);
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
			return view;
		for (int i = 0; i < count; i++) {
			String atts = parser.getAttributeName(i);
			String value = parser.getAttributeValue(i);
			parseAtts(atts, value, view);
			if (!isLinearLayoutParent) { 
				xmlParse.parseAlignAnchor(atts, value, rp);
				xmlParse.parseAlignParent(atts, value, rp);
			}
		}
		view.setPadding(padding[0], padding[1], padding[2], padding[3]);

		if (isLinearLayoutParent) {
			lp.width = xy[0];
			lp.height = xy[1];
			if (weight != -1)
				lp.weight = weight;
			if (layout_gravity != -1)
				lp.gravity = layout_gravity;
			lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			view.setLayoutParams(lp);
		} else {
			rp.width = xy[0];
			rp.height = xy[1];
			rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			view.setLayoutParams(rp);
		}
		return view;
	}

	/**
	 * 解析参数
	 * 
	 * @param atts
	 * @param value
	 * @param view
	 */
	private void parseAtts(String atts, String value, View view) {
		super.parse(atts, value, view);
	}
}
