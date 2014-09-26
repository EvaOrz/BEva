package cn.com.modernmedia.views.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.modernmedia.adapter.CheckScrollAdapter;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.util.V;
import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 选择子栏目adapter
 * 
 * @author zhuqiao
 * 
 */
public class SelectChildColumnAdapter extends CheckScrollAdapter<TagInfo> {
	private Context mContext;

	public SelectChildColumnAdapter(Context context) {
		super(context);
		mContext = context;
	}

	public void setData(List<TagInfo> list) {
		if (!ParseUtil.listNotNull(list))
			return;
		synchronized (list) {
			for (TagInfo item : list) {
				if (item.getEnablesubscribe() == 1
						&& item.getColumnProperty().getNoColumn() == 0
						&& item.getIsFix() == 0)
					add(item);
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final TagInfo info = getItem(position);
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.select_child_column_list_item);
		ImageView imageView = holder
				.getView(R.id.select_child_column_list_item_img);
		TextView name = holder.getView(R.id.select_child_column_list_item_name);
		ImageView select = holder
				.getView(R.id.select_child_column_list_item_check);

		V.downCatPic(mContext, info, imageView, true);
		name.setText(info.getColumnProperty().getCname());
		select.setImageResource(info.getHasSubscribe() == 1 ? R.drawable.subscribe_checked
				: R.drawable.subscribe_check);
		holder.getConvertView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickSelect(info);
			}
		});
		return holder.getConvertView();
	}

	/**
	 * 切换选中状态
	 * 
	 * @param info
	 */
	private void clickSelect(TagInfo info) {
		if (info.getIsFix() == 1) {
			// TODO 不可被取消(其实都不会显示出来,可以不用判断)
			return;
		}
		if (info.getHasSubscribe() == 1) {
			info.setHasSubscribe(0);
		} else {
			info.setHasSubscribe(1);
		}
		notifyDataSetChanged();
	}
}
