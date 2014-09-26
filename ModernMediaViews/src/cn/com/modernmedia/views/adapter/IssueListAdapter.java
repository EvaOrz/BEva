package cn.com.modernmedia.views.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.breakpoint.DownloadPackageCallBack;
import cn.com.modernmedia.breakpoint.DownloadProcessView;
import cn.com.modernmedia.db.BreakPointDb;
import cn.com.modernmedia.model.TagInfoList;
import cn.com.modernmedia.model.TagInfoList.TagInfo;
import cn.com.modernmedia.views.R;
import cn.com.modernmedia.views.index.IssueItemView;
import cn.com.modernmediaslate.adapter.ViewHolder;
import cn.com.modernmediaslate.unit.ParseUtil;

/**
 * 期刊列表adapter
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class IssueListAdapter extends BaseAdapter {
	protected Context mContext;
	protected List<TagInfo> list = new ArrayList<TagInfo>();
	private BreakPointDb db;
	private Map<String, ViewItem> sweepMap = new HashMap<String, ViewItem>();
	private String currentDownTag;
	private List<String> successTags = new ArrayList<String>();

	public IssueListAdapter(Context context) {
		mContext = context;
		db = BreakPointDb.getInstance(mContext);
		CommonApplication.downBack = new DownloadPackageCallBack() {

			@Override
			public void onSuccess(String tagName, String folderName) {
				addSuccess(tagName);
				notifyDataSetChanged();
			}

			@Override
			public void onProcess(String tagName, long complete, long total) {
				if (!TextUtils.equals(tagName, currentDownTag)) {
					currentDownTag = tagName;
					notifyDataSetChanged();
				}
				removeSuccess(tagName);
				if (sweepMap.containsKey(tagName)) {
					float sweep = complete * 360 / total;
					ViewItem viewItem = sweepMap.get(tagName);
					viewItem.sweep = sweep;
					sweepMap.put(tagName, viewItem);
				}
			}

			@Override
			public void onFailed(String tagName) {
				currentDownTag = "";
				notifyDataSetChanged();
			}

			@Override
			public void onPause(String tagName) {
			}
		};
	}

	public void setData(TagInfoList issueList) {
		list.addAll(issueList.getList());
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean convertViewIsNull = convertView == null;
		ViewHolder holder = ViewHolder.get(mContext, convertView,
				R.layout.issue_list_item_lohas);
		IssueItemView itemLeft = holder.getView(R.id.issuelist_rl_left);
		IssueItemView itemCenter = holder.getView(R.id.issuelist_rl_center);
		IssueItemView itemRight = holder.getView(R.id.issuelist_rl_right);

		if (convertViewIsNull) {
			itemLeft.setImgSize(CommonApplication.width);
			itemCenter.setImgSize(CommonApplication.width);
			itemRight.setImgSize(CommonApplication.width);
		}
		createView(new IssueItemView[] { itemLeft, itemCenter, itemRight },
				position);
		return holder.getConvertView();
	}

	private void createView(IssueItemView[] itemView, int position) {
		if (itemView == null || itemView.length < 3)
			return;
		itemView[0].setData(list.get(position * 3), this);
		if (list.size() > position * 3 + 1) {
			itemView[1].setVisibility(View.VISIBLE);
			itemView[1].setData(list.get(position * 3 + 1), this);
		} else {
			itemView[1].setVisibility(View.INVISIBLE);
		}
		if (list.size() > position * 3 + 2) {
			itemView[2].setVisibility(View.VISIBLE);
			itemView[2].setData(list.get(position * 3 + 2), this);
		} else {
			itemView[2].setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 设置下载进度
	 * 
	 * @param view
	 * @param downLoad
	 * @param issueId
	 */
	public void setProcess(DownloadProcessView view, ImageView downLoad,
			String tagName) {
		if (sweepMap.containsKey(tagName)) {
			ViewItem viewItem = sweepMap.get(tagName);
			view.setSweepAngle(viewItem.sweep);
			if (viewItem.sweep >= 360) {
				addSuccess(tagName);
			}
		} else {
			// 添加package下载信息
			float sweep = 0;
			long complete = db.getComplete(tagName);
			long total = db.getTotal(tagName);
			if (total != 0 && complete != 0) {
				if (complete >= total) {
					sweep = 360;
					addSuccess(tagName);
					view.setSweepAngle(sweep);
				} else if (complete < total) {
					// sweep = complete * 360 / total;
				}
			}
			sweepMap.put(tagName, new ViewItem(sweep));
		}
		checkVisible(view, downLoad, tagName);
	}

	private void checkVisible(DownloadProcessView view, ImageView downLoad,
			String tagName) {
		if (successTags.contains(tagName)) {
			view.setVisibility(View.GONE);
			downLoad.setVisibility(View.GONE);
		} else if (TextUtils.equals(tagName, currentDownTag)) {
			view.setVisibility(View.VISIBLE);
			downLoad.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.GONE);
			downLoad.setVisibility(View.VISIBLE);
		}
	}

	private void addSuccess(String tagName) {
		if (!successTags.contains(tagName)) {
			successTags.add(tagName);
		}
	}

	@SuppressLint("UseValueOf")
	private void removeSuccess(String tagName) {
		if (successTags.contains(tagName)) {
			successTags.remove(tagName);
			notifyDataSetChanged();
		}
	}

	private class ViewItem {
		float sweep;

		public ViewItem(float sweep) {
			this.sweep = sweep;
		}

	}

	@Override
	public int getCount() {
		return ParseUtil.listNotNull(list) ? (list.size() + 2) / 3 : 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}
