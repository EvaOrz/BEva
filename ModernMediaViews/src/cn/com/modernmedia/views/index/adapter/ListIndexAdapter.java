package cn.com.modernmedia.views.index.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 普通列表形式的listview适配器
 * 
 * @author zhuqiao
 * 
 */
public class ListIndexAdapter extends BaseIndexAdapter {

	public ListIndexAdapter(Context context, Template template,
			ArticleType articleType) {
		super(context, template);
		this.articleType = articleType;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getPosition().getStyle();
	}

	@Override
	public int getViewTypeCount() {
		int size = template.getList().getMap().size();
		return size == 0 ? 1 : size;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertViewIsNull = convertView == null;
		final ArticleItem item = getItem(position);
		int type = getItemViewType(position);
		IndexViewHolder holder = IndexViewHolder.get(mContext, convertView,
				getData(type), template.getHost());
		holder.setData(item, position, this, articleType);
		return holder.getConvertView();
	}

	/**
	 * 获取type对应的layout
	 * 
	 * @param type
	 * @return
	 */
	private String getData(int type) {
		String data = "";
		if (ParseUtil.mapContainsKey(template.getList().getMap(), type)) {
			data = template.getList().getMap().get(type);
		}
		if (TextUtils.isEmpty(data)) {
			if (type == 1 || type == 2 || type == 3) {
				data = template.getList().getDefault_data();
			}
		}
		return data;
	}
}
