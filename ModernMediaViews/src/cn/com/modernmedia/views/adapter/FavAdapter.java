package cn.com.modernmedia.views.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.FavParm;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.model.Favorite.FavoriteItem;

/**
 * 收藏adapter
 * 
 * @author user
 * 
 */
public class FavAdapter extends ArrayAdapter<FavoriteItem> {
	private Context mContext;
	private FavParm parm;

	public FavAdapter(Context context, FavParm parm) {
		super(context, 0);
		mContext = context;
		this.parm = parm;
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
		boolean convertViewIsNull = convertView == null;
		final FavoriteItem detail = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.fav_item);
		TextView title = holder.getView(R.id.fav_item_name);
		LinearLayout margin = holder.getView(R.id.fav_item_margin);
		ImageView rightArrow = holder.getView(R.id.fav_item_right_arrow);
		if (convertViewIsNull) {
			margin.setBackgroundColor(Color.BLACK);
			if (!TextUtils.isEmpty(parm.getRow())) { // 带有箭头的背景图
				V.setImage(title, parm.getRow());
			}
			if (!TextUtils.isEmpty(parm.getRow_img())) { // 箭头图片
				rightArrow.setVisibility(View.VISIBLE);
				V.setImage(rightArrow, parm.getRow_img());
			}
			if (!TextUtils.isEmpty(parm.getFav_item_background())
					&& holder.getConvertView() != null) {
				V.setImage(holder.getConvertView(),
						parm.getFav_item_background());
			}
			if (parm.getMargin() == 0) {
				margin.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(parm.getText_color())) {
				title.setTextColor(Color.parseColor(parm.getText_color()));
			}
		}
		if (detail != null) {
			title.setText(detail.getTitle());
		}
		return holder.getConvertView();
	}

}
