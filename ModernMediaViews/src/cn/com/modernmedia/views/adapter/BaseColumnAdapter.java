package cn.com.modernmedia.views.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.Cat.CatItem;
import cn.com.modernmedia.model.SoloColumn.SoloColumnItem;
import cn.com.modernmedia.views.model.ColumnParm;
import cn.com.modernmedia.views.util.V;

/**
 * 栏目列表基础adapter
 * 
 * @author user
 * 
 */
public class BaseColumnAdapter extends ArrayAdapter<CatItem> {
	protected Context mContext;
	protected LayoutInflater inflater;
	protected int selectPosition = 0;
	protected ColumnParm parm;
	private NotifyAdapterListener adapterListener = new NotifyAdapterListener() {

		@Override
		public void notifyReaded() {
		}

		@Override
		public void nofitySelectItem(Object args) {
			try {
				selectPosition = -1;
				int catId = (Integer) args;
				for (int i = 0; i < getCount(); i++) {
					if (getItem(i).getId() == catId) {
						selectPosition = i;
						break;
					}
				}
				notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void notifyChanged() {
		}
	};

	public BaseColumnAdapter(Context context, ColumnParm parm) {
		super(context, 0);
		mContext = context;
		this.parm = parm;
		inflater = LayoutInflater.from(context);
		if (context instanceof CommonMainActivity) {
			((CommonMainActivity) context).addListener(adapterListener);
		}
	}

	public void setData(List<CatItem> list) {
		synchronized (list) {
			for (CatItem item : list) {
				add(item);
			}
		}
	}

	public void setData(List<SoloColumnItem> list, boolean isSolo) {
		synchronized (list) {
			for (CatItem item : list) {
				add(item);
			}
		}
	}

	public int getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}

	@Override
	public void clear() {
		super.clear();
		selectPosition = 0;
	}

	/**
	 * 设置背景
	 * 
	 * @param view
	 * @param isSelect
	 */
	protected void setBgIfSelected(View view, boolean isSelect) {
		if (parm == null)
			return;
		if (isSelect) {
			V.setImage(view, parm.getItem_bg_select());
		} else {
			V.setImage(view, parm.getItem_bg());
		}
	}

	/**
	 * 设置title颜色
	 * 
	 * @param textView
	 * @param isSelect
	 */
	protected void setNameIfSelect(TextView name, boolean isSelect) {
		if (isSelect) {
			String select = parm.getName_color_select();
			if (select.startsWith("#")) {
				name.setTextColor(Color.parseColor(select));
			} else {
				name.setTextColor(Color.WHITE);
			}
		} else {
			String unSelect = parm.getName_color();
			if (unSelect.startsWith("#")) {
				name.setTextColor(Color.parseColor(unSelect));
			} else {
				name.setTextColor(Color.WHITE);
			}
		}
	}
}
