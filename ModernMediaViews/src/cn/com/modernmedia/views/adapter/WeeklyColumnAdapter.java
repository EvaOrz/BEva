package cn.com.modernmedia.views.adapter;

import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.ViewsApplication;
import cn.com.modernmedia.views.listener.NotifyLastestChangeListener;
import cn.com.modernmedia.views.model.ColumnParm;
import cn.com.modernmedia.views.util.V;

/**
 * iweekly类型栏目列表adapter
 * 
 * @author user
 * 
 */
public class WeeklyColumnAdapter extends BaseColumnAdapter {

	public WeeklyColumnAdapter(Context context, ColumnParm parm) {
		super(context, parm);
		ViewsApplication.addListener("WeeklyColumnAdapter",
				new NotifyLastestChangeListener() {

					@Override
					public void changeCount() {
						notifyDataSetChanged();
					}
				});
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		final CatItem item = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.weekly_column_item);
		View contain = holder.getView(R.id.column_left_item_contain);
		ImageView icon = holder.getView(R.id.column_left_item_img);
		ImageView dot = holder.getView(R.id.column_left_item_new);
		TextView title = holder.getView(R.id.column_left_item_title);
		ImageView row = holder.getView(R.id.column_left_item_row);
		ImageView divider = holder.getView(R.id.column_left_item_divider);

		if (convertViewIsNull) {
			V.setImage(row, parm.getRow());
			V.setImage(divider, parm.getDivider());
		}

		// TODO iweekly栏目页没有选中状态
		setBgIfSelected(contain, false);
		setNameIfSelect(title, false);

		if (item != null) {
			title.setText(item.getCname());
			if (position > 0) {
				setTileNum(item, dot);
			} else {
				dot.setVisibility(View.GONE);
			}
			V.setImageForWeeklyCat(item, icon, true);
		}
		return holder.getConvertView();
	}

	public void setTileNum(CatItem item, View newDot) {
		if (ViewsApplication.lastestArticleId == null)
			return;
		HashMap<Integer, HashMap<Integer, String>> map = ViewsApplication.lastestArticleId
				.getUnReadedEntryIds();
		int catId = item.getId();
		if (map.containsKey(catId)) {
			if (map.get(catId).size() == 0) {
				newDot.setVisibility(View.GONE);
			} else {
				if (newDot.getVisibility() != View.VISIBLE)
					newDot.setVisibility(View.VISIBLE);
			}
		} else {
			newDot.setVisibility(View.GONE);
		}
	};

}
