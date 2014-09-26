package cn.com.modernmedia.views.index.head;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.adapter.MyPagerAdapter;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.widget.CircularViewPager;

/**
 * 首页焦点图viewpager
 * 
 * @author zhuqiao
 * 
 */
public class IndexHeadCircularViewPager extends CircularViewPager<ArticleItem> {
	private Template template;
	private ArticleType articleType;

	public IndexHeadCircularViewPager(Context context) {
		this(context, null);
	}

	public IndexHeadCircularViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public void setArticleType(ArticleType articleType) {
		this.articleType = articleType;
	}

	@Override
	public MyPagerAdapter<ArticleItem> fetchAdapter(Context context,
			List<ArticleItem> list) {
		return new IndexHeadPagerAdapter(context, list, template, articleType);
	}

}
