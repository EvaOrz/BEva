package cn.com.modernmedia.businessweek.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.modernmedia.businessweek.MainActivity;
import cn.com.modernmedia.businessweek.R;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.util.LogHelper;

/**
 * 栏目列表适配器
 * 
 * @author ZhuQiao
 * 
 */
public class ColumnListAdapter extends ArrayAdapter<CatItem> {
	private Context mContext;
	private LayoutInflater inflater;
	private int selectPosition = 0;
	private NotifyAdapterDataChange listener;

	public interface NotifyAdapterDataChange {
		public void dataChange();
	}

	public ColumnListAdapter(Context context, NotifyAdapterDataChange listener) {
		super(context, 0);
		mContext = context;
		inflater = LayoutInflater.from(context);
		this.listener = listener;
	}

	public void setData(List<CatItem> list) {
		synchronized (list) {
			for (CatItem item : list) {
				add(item);
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final CatItem item = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.column_item, null);
			holder = new ViewHolder();
			holder.button = (Button) convertView
					.findViewById(R.id.cloumn_item_color);
			holder.name = (TextView) convertView
					.findViewById(R.id.cloumn_item_name);
			holder.margin = (LinearLayout) convertView
					.findViewById(R.id.cloumn_margin);
			holder.margin.setBackgroundColor(Color.BLACK);
			holder.ll = (LinearLayout) convertView
					.findViewById(R.id.column_item_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (item != null) {
			if (item.getColor() != 0)
				holder.button.setBackgroundColor(item.getColor());
			holder.name.setText(item.getCname());
		}
		if (position == selectPosition) {
			holder.ll.setBackgroundResource(R.drawable.left_cell_tapped);
		} else {
			holder.ll.setBackgroundResource(R.drawable.column_selector);
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (item != null)
					click(item, position);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		Button button;
		TextView name;
		LinearLayout margin, ll;
	}

	private void click(final CatItem item, final int position) {
		selectPosition = position;
		notifyDataSetChanged();
		listener.dataChange();
		LogHelper.logOpenColumn(mContext, item.getId() + "");
		if (mContext instanceof MainActivity) {
			((MainActivity) mContext).setIndexTitle(item.getCname());
			((MainActivity) mContext).getScrollView().IndexClick();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (position == 0) {// 首页
						((MainActivity) mContext).getIndex();
					} else {// 栏目首页
						((MainActivity) mContext).getCatIndex(item.getId() + "");
					}
				}
			}, 100);
		}
	}
}
