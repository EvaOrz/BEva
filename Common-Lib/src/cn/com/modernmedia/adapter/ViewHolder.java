package cn.com.modernmedia.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

/**
 * adapter的convertView存储
 * 
 * @author user
 * 
 */
public class ViewHolder {
	private View convertView;
	private SparseArray<View> views;

	private ViewHolder(Context context, int layoutId, int size) {
		views = new SparseArray<View>(size);
		convertView = LayoutInflater.from(context).inflate(layoutId, null);
		convertView.setTag(this);
	}

	public static ViewHolder get(Context context, View convertView, int layoutId) {
		return get(context, convertView, layoutId, 10);
	}

	public static ViewHolder get(Context context, View convertView,
			int layoutId, int size) {
		if (convertView == null || convertView.getTag() == null) {
			return new ViewHolder(context, layoutId, size);
		}
		return (ViewHolder) convertView.getTag();
	}

	/**
	 * 获取view
	 * 
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends View> T getView(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 获取convertView
	 * 
	 * @return
	 */
	public View getConvertView() {
		return convertView;
	}

}
