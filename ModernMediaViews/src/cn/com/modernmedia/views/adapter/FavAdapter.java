package cn.com.modernmedia.views.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cn.com.modernmedia.model.ArticleItem;
import cn.com.modernmedia.views.index.adapter.IndexViewHolder;
import cn.com.modernmedia.views.model.TemplateFav;

/**
 * 收藏adapter
 * 
 * @author user
 * 
 */
public class FavAdapter extends ArrayAdapter<ArticleItem> {
	private Context mContext;
	private TemplateFav template;
	private int selectPosition = 0;

	public FavAdapter(Context context, TemplateFav template) {
		super(context, 0);
		mContext = context;
		this.template = template;
	}

	public void setData(List<ArticleItem> list) {
		synchronized (list) {
			for (ArticleItem item : list) {
				add(item);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ArticleItem detail = getItem(position);
		IndexViewHolder holder = IndexViewHolder.get(mContext, convertView,
				template.getList().getData(), "");
		holder.setDataForFav(detail, position, this);
		return holder.getConvertView();
	}

	public int getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}

}
