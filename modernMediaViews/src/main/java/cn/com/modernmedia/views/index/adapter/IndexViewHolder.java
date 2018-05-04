package cn.com.modernmedia.views.index.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;

import java.util.List;

import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.adapter.FavAdapter;
import cn.com.modernmedia.views.column.ColumnAdapter;
import cn.com.modernmedia.views.xmlparse.XMLParse;

/**
 * 首页adapter的convertView存储
 * 
 * @author user
 * 
 */
public class IndexViewHolder {
	private View convertView;
	private XMLParse xmlParse;

	private IndexViewHolder(Context context, String xmlData, String host) {
		xmlParse = new XMLParse(context, null);
		convertView = xmlParse.inflate(xmlData, null, host);
		convertView.setTag(this);
	}

	public static IndexViewHolder get(Context context, View convertView,
			String xmlData, String host) {
		if (convertView == null || convertView.getTag() == null) {
			return new IndexViewHolder(context, xmlData, host);
		}
		return (IndexViewHolder) convertView.getTag();
	}

	/**
	 * 设置数据
	 * 
	 * @param adapter
	 */
	public void setData(ArticleItem item, int position,
			BaseIndexAdapter adapter, ArticleType articleType) {
		xmlParse.setData(item, position, adapter, articleType);
	}

	/**
	 * Grid类型列表设置数据
	 */
	public void setData(List<ArticleItem> list, int position, int oneLineCount,
			BaseIndexAdapter adapter, ArticleType articleType) {
		xmlParse.setData(list, position, oneLineCount, adapter, articleType);
	}

	/**
	 * 给栏目设置数据
	 * 
	 * @param tagInfo
	 */
	public void setDataForColumn(TagInfo tagInfo, int position,
			ColumnAdapter adapter) {
		xmlParse.setDataForColumn(tagInfo, position, adapter);
	}

	/**
	 * 给收藏设置数据
	 * 
	 * @param articleItem
	 */
	public void setDataForFav(ArticleItem articleItem, int position,
			FavAdapter adapter) {
		xmlParse.setDataForFavList(articleItem, position, adapter);
	}

	/**
	 * 获取convertView
	 * 
	 * @return
	 */
	public View getConvertView() {
		if (convertView != null)
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.FILL_PARENT,
					AbsListView.LayoutParams.WRAP_CONTENT));
		return convertView;
	}

	public XMLParse getXmlParse() {
		return xmlParse;
	}

}
