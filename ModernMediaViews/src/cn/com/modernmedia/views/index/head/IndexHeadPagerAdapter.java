package cn.com.modernmedia.views.index.head;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.util.ConstData;
import cn.com.modernmedia.util.WeeklyLogEvent;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.xmlparse.XMLParse;

/**
 * 首页焦点图适配器
 * 
 * @author zhuqiao
 * 
 */
public class IndexHeadPagerAdapter extends MyPagerAdapter<ArticleItem> {
	private Template template;
	private ArticleType articleType;

	public IndexHeadPagerAdapter(Context context, List<ArticleItem> list,
			Template template, ArticleType articleType) {
		super(context, list);
		this.template = template;
		this.articleType = articleType;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		if (ConstData.getAppId() == 20)
			WeeklyLogEvent.logAndroidColumnHeadviewShowCount();

		int newPosition = position % list.size();
		View view = fetchView(list.get(newPosition), newPosition);
		container.addView(view);
		return view;
	}

	public View fetchView(ArticleItem item, int position) {
		if (template == null)
			return new View(mContext);
		XMLParse parse = new XMLParse(mContext, null);
		View view = parse.inflate(template.getHeadItem().getData(), null,
				template.getHost());
		parse.setDataForHeadItem(item, position, articleType);
		return view;
	}

}
