package cn.com.modernmedia.Fragment;

import android.support.v4.app.Fragment;
import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmedia.model.Entry;

public abstract class BaseFragment extends Fragment implements
		FetchEntryListener {

	@Override
	public void setData(Entry entry) {

	}

	public abstract void refresh();

	// public void showView(boolean show) {
	// if (this.getView() != null) {
	// this.getView().setVisibility(show ? View.VISIBLE : View.GONE);
	// }
	// }
	
	public abstract void showView(boolean show);
}
