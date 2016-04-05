package cn.com.modernmedia.businessweek;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmediaslate.model.Entry;

/**
 * 特刊tab gridview适配器
 * 
 * @author Eva.
 * 
 */
public class SpecialGridAdapter extends BaseAdapter {
	private Context context;
	private List<TagInfo> tags = new ArrayList<TagInfo>();
	private LayoutInflater mInflater;
	private ViewHolder viewHolder = null;

	public SpecialGridAdapter(Context context, List<TagInfo> strList) {
		this.context = context;
		this.tags = strList;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return tags.size();
	}

	@Override
	public Entry getItem(int position) {
		return tags.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		convertView = mInflater.inflate(R.layout.item_special, null);
		final TagInfo t = tags.get(pos);
		/**
		 * 控件初始化
		 */
		viewHolder = new ViewHolder();
		viewHolder.img = (ImageView) convertView
				.findViewById(R.id.special_item_cover);
		if (t.getColumnProperty().getBigPicture() != null
				&& t.getColumnProperty().getBigPicture().size() > 0) {
			viewHolder.img.setTag(t.getColumnProperty().getBigPicture().get(0));
			CommonApplication.finalBitmap.display(viewHolder.img,
					(String) viewHolder.img.getTag());
		}
		viewHolder.logo = (ImageView) convertView
				.findViewById(R.id.special_pay_logo);
		if (t.getIsPay() == 0)
			viewHolder.logo.setVisibility(View.GONE);
		else
			viewHolder.logo.setVisibility(View.VISIBLE);
		convertView.setTag(t);

		return convertView;
	}

	@SuppressLint("ViewHolder")
	// 通过ViewHolder显示项的内容
	static class ViewHolder {
		public ImageView img;
		public ImageView logo;

	}
}
