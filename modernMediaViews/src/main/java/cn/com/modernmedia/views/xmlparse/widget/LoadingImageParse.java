package cn.com.modernmedia.views.xmlparse.widget;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.LoadingImage;

/**
 * 封装LoadingImage
 * 
 * @author zhuqiao
 * 
 */
public class LoadingImageParse extends BaseViewParse {

	public LoadingImageParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	/**
	 * 创建LoadingImage
	 * 
	 * @param parser
	 * @param parent
	 * @return
	 */
	public View createLoadingImage(XmlPullParser parser,
			View parent) {
		LoadingImage loadingImage = new LoadingImage(mContext, -1, -1);
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
			return loadingImage;
		for (int i = 0; i < count; i++) {
			String atts = parser.getAttributeName(i);
			String value = parser.getAttributeValue(i);
			parse(atts, value, loadingImage);
			if (!isLinearLayoutParent) {
				xmlParse.parseAlignAnchor(atts, value, rp);
				xmlParse.parseAlignParent(atts, value, rp);
			}
			if (atts.equalsIgnoreCase("scaleType")) {
				loadingImage.setTag(value);
			}
		}
		loadingImage.setPadding(padding[0], padding[1], padding[2], padding[3]);

		if (isLinearLayoutParent) {
			lp.width = xy[0];
			lp.height = xy[1];
			if (weight != -1)
				lp.weight = weight;
			if (layout_gravity != -1)
				lp.gravity = layout_gravity;
			lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			loadingImage.setLayoutParams(lp);
		} else {
			rp.width = xy[0];
			rp.height = xy[1];
			rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			loadingImage.setLayoutParams(rp);
		}
		return loadingImage;
	}
}
