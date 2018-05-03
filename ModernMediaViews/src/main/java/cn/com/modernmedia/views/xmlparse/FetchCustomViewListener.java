package cn.com.modernmedia.views.xmlparse;

import org.xmlpull.v1.XmlPullParser;

import android.view.View;

/**
 * 获取自定义view
 * 
 * @author zhuqiao
 * 
 */
public interface FetchCustomViewListener {

	public View fecthView(String name, XmlPullParser parser, View parent);
}
