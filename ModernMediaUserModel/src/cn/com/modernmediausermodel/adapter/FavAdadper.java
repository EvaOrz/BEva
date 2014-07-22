package cn.com.modernmediausermodel.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;
import cn.com.modernmediausermodel.R;

/**
 * 收藏列表适配器
 * 
 * @author ZhuQiao
 * 
 */
public class FavAdadper extends ArrayAdapter<FavoriteItem> {
	private Context mContext;

	public FavAdadper(Context context) {
		super(context, 0);
		mContext = context;
	}

	public void setData(List<FavoriteItem> list) {
		synchronized (list) {
			for (FavoriteItem item : list) {
				add(item);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final FavoriteItem detail = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.favorites_item);
		TextView title = holder.getView(R.id.favorites_item_name);
		if (detail != null) {
			title.setText(detail.getTitle());
		}
		return holder.getConvertView();
	}

}
