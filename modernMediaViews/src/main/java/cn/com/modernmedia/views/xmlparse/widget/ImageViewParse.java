package cn.com.modernmedia.views.xmlparse.widget;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmediaslate.unit.ImageScaleType;

/**
 * 解析ImageView
 * 
 * @author zhuqiao
 * 
 */
public class ImageViewParse extends BaseViewParse {

	public ImageViewParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	/**
	 * 创建ImageView
	 * 
	 * @param parser
	 * @param parent
	 * @return
	 */
	public View createImageView(XmlPullParser parser, View parent) {
		ImageView imageView = new ImageView(mContext);
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
			return imageView;
		for (int i = 0; i < count; i++) {
			String atts = parser.getAttributeName(i);
			String value = parser.getAttributeValue(i);
			parseAtts(atts, value, imageView);
			if (!isLinearLayoutParent) {
				xmlParse.parseAlignAnchor(atts, value, rp);
				xmlParse.parseAlignParent(atts, value, rp);
			}
		}
		imageView.setPadding(padding[0], padding[1], padding[2], padding[3]);

		if (isLinearLayoutParent) {
			lp.width = xy[0];
			lp.height = xy[1];
			if (weight != -1)
				lp.weight = weight;
			if (layout_gravity != -1)
				lp.gravity = layout_gravity;
			lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			imageView.setLayoutParams(lp);
		} else {
			rp.width = xy[0];
			rp.height = xy[1];
			rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			imageView.setLayoutParams(rp);
		}
		return imageView;
	}

	/**
	 * 解析参数
	 * 
	 * @param atts
	 * @param value
	 * @param imageView
	 */
	private void parseAtts(String atts, String value, ImageView imageView) {
		super.parse(atts, value, imageView);
		parseImage(atts, value, imageView);
	}

	/**
	 * 解析ImageView特有属性
	 * 
	 * @param atts
	 * @param value
	 * @param imageView
	 */
	private void parseImage(String atts, String value, ImageView imageView) {
		if (atts.equalsIgnoreCase("scaleType")) {
			ImageScaleType.setScaleType(imageView, value);
			imageView.setTag(R.id.scale_type, value);
		} else if (atts.equals("use")) {
			imageView.setTag(R.id.img_use, value);
		} else if (atts.equalsIgnoreCase("maxWidth")) {
			imageView.setMaxWidth(xmlParse.getExpressionResult(value));
		} else if (atts.equalsIgnoreCase("maxHeight")) {
			imageView.setMaxHeight(xmlParse.getExpressionResult(value));
		}
	}
}
