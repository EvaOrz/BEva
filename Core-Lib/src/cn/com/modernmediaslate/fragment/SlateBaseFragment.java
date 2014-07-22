package cn.com.modernmediaslate.fragment;

import android.support.v4.app.Fragment;

public abstract class SlateBaseFragment extends Fragment {

	public abstract void refresh();

	public abstract void showView(boolean show);
}
