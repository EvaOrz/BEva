package cn.com.modernmedia.views.xmlparse.article;

import android.content.Context;
import android.support.v4.view.ViewPager;
import cn.com.modernmedia.views.xmlparse.BaseViewPagerParse;
import cn.com.modernmedia.views.xmlparse.XMLParse;
import cn.com.modernmedia.widget.AtlasViewPager;

/**
 * 图集ViewPager解析类
 * 
 * @author jiancong
 * 
 */
public class AtlasViewPagerParse extends BaseViewPagerParse {

	public AtlasViewPagerParse(Context context, XMLParse xmlParse) {
		super(context, xmlParse);
	}

	@Override
	protected ViewPager getBaseViewPager() {
		return new AtlasViewPager(mContext);
	}

}
