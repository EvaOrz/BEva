package cn.com.modernmedia.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.model.ColumnParm;
import cn.com.modernmedia.views.util.V;

/**
 * 商周类型栏目列表adapter
 * 
 * @author user
 * 
 */
public class BusColumnAdapter extends BaseColumnAdapter {

	public BusColumnAdapter(Context context, ColumnParm parm) {
		super(context, parm);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		final CatItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.bus_column_item);
		View contain = holder.getView(R.id.bus_column_contain);
		View row = holder.getView(R.id.bus_column_item_row);
		View margin = holder.getView(R.id.bus_cloumn_margin);
		ImageView color = holder.getView(R.id.bus_cloumn_item_color);
		TextView name = holder.getView(R.id.bus_column_item_name);
		ImageView rightArrow = holder.getView(R.id.bus_column_item_right_arrow);

		if (convertViewIsNull && parm.getItem_height() != 0) {
			row.getLayoutParams().height = parm.getItem_height()
					* CommonApplication.height / 1280;
		}

		if (parm.getShow_color() == 1) {
			color.setVisibility(View.VISIBLE);
			if (item.getColor() != 0)
				color.setBackgroundColor(item.getColor());
		} else {
			color.setVisibility(View.GONE);
		}

		name.setText(item.getCname());
		margin.setVisibility(parm.getShow_margin() == 1 ? View.VISIBLE
				: View.GONE);
		if (parm.getItem_margin_left() != 0) {
			name.setPadding(parm.getItem_margin_left(), 0, 0, 0);
		}
		if (parm.getName_size() != 0) {
			name.setTextSize(parm.getName_size());
		}
		margin.setBackgroundColor(Color.BLACK);
		V.setImage(row, parm.getRow()); // 带有居右箭头的背景图
		if (!TextUtils.isEmpty(parm.getRow_img())) { // 箭头图片
			rightArrow.setVisibility(View.VISIBLE);
			V.setImage(rightArrow, parm.getRow_img());
		}
		setBgIfSelected(contain, position == selectPosition);
		setNameIfSelect(name, position == selectPosition);
		if (position != selectPosition) {
			if (position == 0 && !TextUtils.isEmpty(parm.getFirst_item_bg())) {
				V.setImage(contain, parm.getFirst_item_bg());
			} else {
				V.setImage(contain, parm.getItem_bg());
			}
		}
		return holder.getConvertView();
	}
}
