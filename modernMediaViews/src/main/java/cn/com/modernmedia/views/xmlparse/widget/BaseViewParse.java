package cn.com.modernmedia.views.xmlparse.widget;

import android.content.Context;
import android.view.View;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 解析view基类
 * 
 * @author zhuqiao
 * 
 */
public class BaseViewParse {
	protected Context mContext;
	protected XMLParse xmlParse;

	protected int weight = -1;// 权重
	protected int gravity = -1;// 重力
	protected int layout_gravity = -1;
	protected int padding[] = new int[4];// padding属性
	protected int margin[] = new int[4];// margin属性
	protected int[] xy = new int[2];// 控件大小

	public BaseViewParse(Context context, XMLParse xmlParse) {
		mContext = context;
		this.xmlParse = xmlParse;
	}

	protected void parse(String atts, String value, View view) {
		xmlParse.parseId(atts, value, view);
		xmlParse.parseWidthAndHeight(atts, value, xy);
		xmlParse.parsePadding(atts, value, padding);
		xmlParse.parseMargin(atts, value, margin);
		xmlParse.parseBackground(atts, value, view);
		xmlParse.parseVisibility(atts, value, view);
		xmlParse.parseFunction(atts, value, view);
		xmlParse.parseClick(atts, value, view);
		xmlParse.parseDataFormat(atts, value, view);
		xmlParse.parseDot(atts, value, view);
		xmlParse.parseMin(atts, value, view);
		if (atts.equalsIgnoreCase("gravity"))
			gravity = xmlParse.parseGravity(value);
		else if (atts.equalsIgnoreCase("layout_gravity"))
			layout_gravity = xmlParse.parseGravity(value);
		else if (atts.equalsIgnoreCase("layout_weight"))
			weight = ParseUtil.stoi(value, 1);
	}

}
