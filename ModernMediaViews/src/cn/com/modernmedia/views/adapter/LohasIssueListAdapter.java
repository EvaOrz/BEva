package cn.com.modernmedia.views.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.adapter.ViewHolder;
import cn.com.modernmedia.util.ParseUtil;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.IssueItemView;
import cn.com.modernmedia.views.model.IssueListParm;
import cn.com.modernmedia.views.util.V;

/**
 * 往期adapter
 * 
 * @author ZhuQiao
 * 
 */
public class LohasIssueListAdapter extends IssueListAdapter {
	private Context mContext;

	public LohasIssueListAdapter(Context context, IssueListParm parm) {
		super(context, parm);
		mContext = context;
	}

	private void createView(IssueItemView[] itemView, int position) {
		if (itemView == null || itemView.length < 3)
			return;
		itemView[0].setData(list.get(position * 3), parm, this);
		if (list.size() > position * 3 + 1) {
			itemView[1].setVisibility(View.VISIBLE);
			itemView[1].setData(list.get(position * 3 + 1), parm, this);
		} else {
			itemView[1].setVisibility(View.INVISIBLE);
		}
		if (list.size() > position * 3 + 2) {
			itemView[2].setVisibility(View.VISIBLE);
			itemView[2].setData(list.get(position * 3 + 2), parm, this);
		} else {
			itemView[2].setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.issue_list_item_lohas);
		IssueItemView itemLeft = holder.getView(R.id.issuelist_rl_left);
		IssueItemView itemCenter = holder.getView(R.id.issuelist_rl_center);
		IssueItemView itemRight = holder.getView(R.id.issuelist_rl_right);
		View layout = holder.getView(R.id.issuelist_item_layout);

		if (convertViewIsNull) {
			itemLeft.setImgSize(CommonApplication.width);
			itemCenter.setImgSize(CommonApplication.width);
			itemRight.setImgSize(CommonApplication.width);
			// 添加背景颜色
			V.setImage(layout, parm.getIssue_bg());
		}
		createView(new IssueItemView[] { itemLeft, itemCenter, itemRight },
				position);
		return holder.getConvertView();
	}

	@Override
	public int getCount() {
		return ParseUtil.listNotNull(list) ? (list.size() + 2) / 3 : 0;
	}

}
