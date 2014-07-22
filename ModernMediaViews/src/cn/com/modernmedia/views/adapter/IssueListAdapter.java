package cn.com.modernmedia.views.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.com.modernmedia.CommonApplication;
import cn.com.modernmedia.breakpoint.DownloadPackageCallBack;
import cn.com.modernmedia.breakpoint.DownloadProcessView;
import cn.com.modernmedia.db.BreakPointDb;
import cn.com.modernmedia.model.IssueList;
import cn.com.modernmedia.model.IssueList.IssueListItem;
import cn.com.modernmedia.views.model.IssueListParm;

/**
 * 期刊列表adapter
 * 
 * @author ZhuQiao
 * 
 */
@SuppressLint("UseSparseArrays")
public class IssueListAdapter extends BaseAdapter {
	private Context mContext;
	protected List<IssueListItem> list = new ArrayList<IssueListItem>();
	private BreakPointDb db;
	private Map<Integer, ViewItem> sweepMap = new HashMap<Integer, ViewItem>();
	private int currentDownIssueId;
	private List<Integer> successIds = new ArrayList<Integer>();
	protected IssueListParm parm;

	public IssueListAdapter(Context context, IssueListParm parm) {
		mContext = context;
		db = BreakPointDb.getInstance(mContext);
		this.parm = parm;
		CommonApplication.downBack = new DownloadPackageCallBack() {

			@Override
			public void onSuccess(int issueId, String folderName) {
				addSuccess(issueId);
				notifyDataSetChanged();
			}

			@Override
			public void onProcess(int issueId, long complete, long total) {
				if (currentDownIssueId != issueId) {
					currentDownIssueId = issueId;
					notifyDataSetChanged();
				}
				removeSuccess(issueId);
				if (sweepMap.containsKey(issueId)) {
					float sweep = complete * 360 / total;
					ViewItem viewItem = sweepMap.get(issueId);
					viewItem.sweep = sweep;
					sweepMap.put(issueId, viewItem);
				}
			}

			@Override
			public void onFailed(int issueId) {
				currentDownIssueId = -1;
				notifyDataSetChanged();
			}

			@Override
			public void onPause(int issueId) {
			}
		};
	}

	public void setData(IssueList issueList) {
		list.addAll(issueList.getList());
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		return null;
	}

	/**
	 * 设置下载进度
	 * 
	 * @param view
	 * @param downLoad
	 * @param issueId
	 */
	public void setProcess(DownloadProcessView view, ImageView downLoad,
			int issueId) {
		if (sweepMap.containsKey(issueId)) {
			ViewItem viewItem = sweepMap.get(issueId);
			view.setSweepAngle(viewItem.sweep);
			if (viewItem.sweep >= 360) {
				addSuccess(issueId);
			}
		} else {
			// 添加package下载信息
			float sweep = 0;
			long complete = db.getComplete(issueId);
			long total = db.getTotal(issueId);
			if (total != 0 && complete != 0) {
				if (complete >= total) {
					sweep = 360;
					addSuccess(issueId);
					view.setSweepAngle(sweep);
				} else if (complete < total) {
					// sweep = complete * 360 / total;
				}
			}
			sweepMap.put(issueId, new ViewItem(sweep));
		}
		checkVisible(view, downLoad, issueId);
	}

	private void checkVisible(DownloadProcessView view, ImageView downLoad,
			int issueId) {
		if (successIds.contains(issueId)) {
			view.setVisibility(View.GONE);
			downLoad.setVisibility(View.GONE);
		} else if (currentDownIssueId == issueId) {
			view.setVisibility(View.VISIBLE);
			downLoad.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.GONE);
			downLoad.setVisibility(View.VISIBLE);
		}
	}

	private void addSuccess(int issueId) {
		if (!successIds.contains(issueId)) {
			successIds.add(issueId);
		}
	}

	@SuppressLint("UseValueOf")
	private void removeSuccess(int issueId) {
		if (successIds.contains(issueId)) {
			successIds.remove(new Integer(issueId));
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
		return list.size();
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
