package cn.com.modernmedia.views.index.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.CommonArticleActivity.ArticleType;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.model.Template;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * grid形式的listview适配器
 * 
 * @author zhuqiao
 * 
 */
public class GridIndexAdapter extends BaseIndexAdapter {
	private String data = "";

	public GridIndexAdapter(Context context, Template template,
			ArticleType articleType) {
		super(context, template);
		this.articleType = articleType;
		for (int key : template.getList().getMap().keySet()) {
			data = template.getList().getMap().get(key);
			break;
		}
		if (TextUtils.isEmpty(data)) {
			data = template.getList().getDefault_data();
		}
	}

	@Override
	public void setData(List<ArticleItem> list) {
		mItemList.addAll(list);
		notifyDataSetChanged();
	}

	// /**
	// * 重写setData方法
	// */
	// @Override
	// public void setData(List<ArticleItem> list, boolean addToFirst) {
	// if (!ParseUtil.listNotNull(list))
	// return;
	// if (addToFirst) {
	// mItemList.addAll(0, list);
	// } else {
	// mItemList.addAll(list);
	// }
	// notifyDataSetChanged();
	// }

	@Override
	public int getCount() {
		return ParseUtil.listNotNull(mItemList) ? (mItemList.size()
				+ template.getList().getOne_line_count() - 1)
				/ template.getList().getOne_line_count() : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertViewIsNull = convertView == null;
		IndexViewHolder holder = IndexViewHolder.get(mContext, convertView,
				data, template.getHost());
		holder.setData(mItemList, position, template.getList()
				.getOne_line_count(), this, articleType);
		return holder.getConvertView();
	}

}
