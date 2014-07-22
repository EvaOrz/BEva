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
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.util.DataHelper;
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
	private NotifyAdapterListener listener;
	private NotifyAdapterListener adapterListener = new NotifyAdapterListener() {

		@Override
		public void notifyReaded() {
		}

		@Override
		public void nofitySelectItem(Object args) {
			try {
				int catId = (Integer) args;
				for (int i = 0; i < getCount(); i++) {
					if (getItem(i).getId() == catId) {
						selectPosition = i;
						break;
					}
				}
				notifyDataSetChanged();
				listener.notifyChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void notifyChanged() {
		}
	};

	// public interface NotifyAdapterDataChange {
	// public void dataChange();
	// }

	public ColumnListAdapter(Context context, NotifyAdapterListener listener) {
		super(context, 0);
		mContext = context;
		inflater = LayoutInflater.from(context);
		this.listener = listener;
		if (context instanceof MainActivity) {
			((MainActivity) context).addListener(adapterListener);
		}
	}

	public void setData(List<CatItem> list) {
		synchronized (list) {
			for (CatItem item : list) {
				add(item);
			}
		}
	}

	public void setSelectPosition(int catId) {
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
		holder.ll.setSelected(position == selectPosition);
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
		listener.notifyChanged();
		LogHelper.logOpenColumn(mContext, item.getId() + "");
		if (mContext instanceof MainActivity) {
			((MainActivity) mContext).setIndexTitle(item.getCname());
			((MainActivity) mContext).getScrollView().IndexClick();
			final String id = item.getId() + "";
			final String columnId = ((MainActivity) mContext).getColumnId();
			if (id.equals("0") && columnId.equals("-1"))
				return;
			if (columnId.equals(item.getId() + ""))
				return;
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (item.getHaveChildren() == 1) {
						if (DataHelper.childMap.containsKey(item.getParentId())) {
							((MainActivity) mContext).showChildCat(item
									.getParentId());
							((MainActivity) mContext).setColumnId(id);
							return;
						}
					}
					if (position == 0) {// 首页
						((MainActivity) mContext).getIndex();
					} else {// 栏目首页
						((MainActivity) mContext).getCatIndex(id);
					}
				}
			}, 200);
		}
	}
}
