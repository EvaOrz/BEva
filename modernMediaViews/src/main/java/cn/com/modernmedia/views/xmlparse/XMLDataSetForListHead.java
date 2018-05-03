package cn.com.modernmedia.views.xmlparse;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 设置列表headview数据
 * 
 * @author zhuqiao
 * 
 */
public class XMLDataSetForListHead extends BaseXMLDataSet {

	public XMLDataSetForListHead(Context context, HashMap<String, View> map,
			List<View> clickViewList, List<View> ninePatchViewList) {
		super(context, map, clickViewList, ninePatchViewList);
	}

	/**
	 * 设置数据
	 */
	public void setData(TagInfo tagInfo) {
		if (tagInfo == null)
			return;
		ArticleItem item = new ArticleItem();
		String name = "";
		if (tagInfo.getTagLevel() == 1)
			name = tagInfo.getColumnProperty().getCname();
		else
			name = tagInfo.getColumnProperty().getCname();
		item.setTitle(name);
		title(item, null);
		icon(tagInfo);
		ninePatch();
	}

	/**
	 * 栏目icon
	 */
	private void icon(TagInfo tagInfo) {
		if (!ParseUtil.mapContainsKey(map, FunctionXML.IMAGE))
			return;
		View view = map.get(FunctionXML.IMAGE);
		if (!(view instanceof ImageView))
			return;
		ImageView imageView = (ImageView) view;
		V.setImageForWeeklyCat(mContext, tagInfo, imageView);
	}
}
