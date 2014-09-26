package cn.com.modernmedia.views.index.head;

import java.util.List;

import android.content.Context;
import android.widget.ImageView;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmedia.views.xmlparse.XMLDataSetForHead;
import cn.com.modernmedia.views.xmlparse.XMLParse;

/**
 * 首页焦点图
 * 
 * @author zhuqiao
 * 
 */
public class IndexViewHead extends BaseIndexHeadView {
	private XMLParse parse;
	private XMLDataSetForHead dataSet;

	public IndexViewHead(Context context, Template template) {
		super(context, template);
	}

	@Override
	protected void init() {
		parse = new XMLParse(mContext, null);
		addView(parse.inflate(template.getHead().getData(), null,
				template.getHost()));
		dataSet = parse.getDataSetForHead();
		viewPager = dataSet.getViewPager();
	}

	/**
	 * 更新title
	 * 
	 * @param item
	 */
	@Override
	protected void updateTitle(ArticleItem item) {
		dataSet.update(item);
	}

	/**
	 * 初始化dotll
	 * 
	 * @param itemList
	 * @param dots
	 */
	@Override
	protected void initDot(List<ArticleItem> itemList, List<ImageView> dots) {
		dataSet.dot(itemList, dots);
	}

	@Override
	protected void setDataToGallery(List<ArticleItem> list) {
		super.setDataToGallery(list);
		dataSet.initAnim(list);
	}

	@Override
	public void updateDes(int position) {
		super.updateDes(position);
		dataSet.anim(mList, position);
	}

}
