package cn.com.modernmedia.views.xmlparse.widget;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 解析TextView
 * 
 * @author zhuqiao
 * 
 */
public class TextViewParse extends BaseViewParse {

	public TextViewParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	/**
	 * 创建TextView
	 * 
	 * @param parser
	 * @param parent
	 * @return
	 */
	public View createTextView(XmlPullParser parser, View parent) {
		TextView textView = new TextView(mContext);
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
			return textView;
		for (int i = 0; i < count; i++) {
			String atts = parser.getAttributeName(i);
			String value = parser.getAttributeValue(i);
			parseAtts(atts, value, textView);
			if (!isLinearLayoutParent) {
				xmlParse.parseAlignAnchor(atts, value, rp);
				xmlParse.parseAlignParent(atts, value, rp);
			}
		}
		if (gravity != -1)
			textView.setGravity(gravity);
		textView.setPadding(padding[0], padding[1], padding[2], padding[3]);

		if (isLinearLayoutParent) {
			lp.width = xy[0];
			lp.height = xy[1];
			if (weight != -1)
				lp.weight = weight;
			if (layout_gravity != -1)
				lp.gravity = layout_gravity;
			lp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			textView.setLayoutParams(lp);
		} else {
			rp.width = xy[0];
			rp.height = xy[1];
			rp.setMargins(margin[0], margin[1], margin[2], margin[3]);
			textView.setLayoutParams(rp);
		}
		return textView;
	}

	/**
	 * 解析参数
	 * 
	 * @param atts
	 * @param value
	 * @param textView
	 */
	protected void parseAtts(String atts, String value, TextView textView) {
		super.parse(atts, value, textView);
		parseText(atts, value, textView);
	}

	/**
	 * 解析TextView特有属性
	 * 
	 * @param atts
	 * @param value
	 * @param textView
	 */
	private void parseText(String atts, String value, TextView textView) {
		if (atts.equalsIgnoreCase("text")) {
			if (value.startsWith("@string/")) {
				String[] arr = value.split("@string/");
				if (arr.length == 2)
					V.setTextById(textView, arr[1]);
			} else
				textView.setText(value);
		} else if (atts.equalsIgnoreCase("textSize")) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
					xmlParse.getDpValue(value));
		} else if (atts.equalsIgnoreCase("textColor")) {
			if (value.startsWith("#"))
				textView.setTextColor(Color.parseColor(value));
		} else if (atts.equalsIgnoreCase("title_default_color")) {
			textView.setTag(R.id.title_default_color, value);
		} else if (atts.equalsIgnoreCase("title_readed_color")) {
			textView.setTag(R.id.title_readed_color, value);
		} else if (atts.equalsIgnoreCase("maxLines")) {
			textView.setMaxLines(ParseUtil.stoi(value, 1));
		} else if (atts.equalsIgnoreCase("lines")) {
			textView.setLines(ParseUtil.stoi(value, 1));
		} else if (atts.equalsIgnoreCase("singleLine")) {
			textView.setSingleLine(true);
		} else if (atts.equalsIgnoreCase("minLines")) {
			textView.setMinLines(ParseUtil.stoi(value, 1));
		} else if (atts.equalsIgnoreCase("minWidth")) {
			textView.setMinWidth(xmlParse.getExpressionResult(value));
		} else if (atts.equalsIgnoreCase("minHeight")) {
			textView.setMinHeight(xmlParse.getExpressionResult(value));
		} else if (atts.equalsIgnoreCase("maxLines")) {
			textView.setMaxLines(ParseUtil.stoi(value, 1));
		} else if (atts.equalsIgnoreCase("maxWidth")) {
			textView.setMaxWidth(xmlParse.getExpressionResult(value));
		} else if (atts.equalsIgnoreCase("maxHeight")) {
			textView.setMaxHeight(xmlParse.getExpressionResult(value));
		} else if (atts.equalsIgnoreCase("ellipsize")) {
			if (value.equals("start")) {
				textView.setEllipsize(TruncateAt.START);
			} else if (value.equals("middle")) {
				textView.setEllipsize(TruncateAt.MIDDLE);
			} else if (value.equals("end")) {
				textView.setEllipsize(TruncateAt.END);
			} else if (value.equals("marquee")) {
				textView.setEllipsize(TruncateAt.MARQUEE);
			}
		} else if (atts.equalsIgnoreCase("descCheckScroll")) {
			textView.setTag(R.id.desc_check_scroll, true);
		} else if (atts.equalsIgnoreCase("shadow")) {
			if (!TextUtils.isEmpty(value)) {
				String[] shadowArr = value.split(",");
				if (shadowArr != null && shadowArr.length == 4) {
					float radius = ParseUtil.stof(shadowArr[0]);
					float dx = ParseUtil.stof(shadowArr[1]);
					float dy = ParseUtil.stof(shadowArr[2]);
					int color = Color.BLACK;
					if (shadowArr[3].startsWith("#"))
						color = Color.parseColor(shadowArr[3]);
					textView.setShadowLayer(radius, dx, dy, color);
				}
			}
		} else {
			xmlParse.parseSelectColor(atts, value, textView);
		}
	}
}
