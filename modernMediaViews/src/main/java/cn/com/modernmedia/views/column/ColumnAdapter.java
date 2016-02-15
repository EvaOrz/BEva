package cn.com.modernmedia.views.column;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.CommonMainActivity;
import cn.com.modernmedia.listener.NotifyAdapterListener;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.index.adapter.IndexViewHolder;
import cn.com.modernmedia.views.model.TemplateColumn;

/**
 * 栏目适配器
 * 
 * @author zhuqiao
 * 
 */
public class ColumnAdapter extends ArrayAdapter<TagInfo> {
	private Context mContext;
	private TemplateColumn template;

	private int selectPosition = 0;
	private int thisAppItemCount = -1;// 当前应用的栏目数量

	private NotifyAdapterListener adapterListener = new NotifyAdapterListener() {

		@Override
		public void notifyReaded() {
		}

		@Override
		public void nofitySelectItem(Object args) {
			try {
				selectPosition = -1;
				String parentTag = (String) args;
				for (int i = 0; i < getCount(); i++) {
					if (getItem(i).getTagName().equals(parentTag)) {
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

	public ColumnAdapter(Context context, TemplateColumn template) {
		super(context, 0);
		mContext = context;
		this.template = template;
		if (context instanceof CommonMainActivity) {
			((CommonMainActivity) context).addListener(adapterListener);
		}
	}

	public void setData(List<TagInfo> list) {
		synchronized (list) {
			for (TagInfo item : list) {
				add(item);
			}
		}
	}

	public int getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(int selectPosition) {
		if (CommonApplication.mConfig.getHas_single_view() != 1
				|| selectPosition != 0) {
			this.selectPosition = selectPosition;
		}
	}

	public int getThisAppItemCount() {
		return thisAppItemCount;
	}

	public void setThisAppItemCount(int thisAppItemCount) {
		this.thisAppItemCount = thisAppItemCount;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TagInfo item = getItem(position);
		IndexViewHolder holder = IndexViewHolder.get(mContext, convertView,
				template.getList().getData(), "");
		holder.setDataForColumn(item, position, this);
		return holder.getConvertView();
	}

}
