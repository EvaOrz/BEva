package cn.com.modernmedia.Fragment;

import cn.com.modernmedia.listener.FetchEntryListener;
import cn.com.modernmediaslate.fragment.SlateBaseFragment;
import cn.com.modernmediaslate.model.Entry;

public abstract class BaseFragment extends SlateBaseFragment implements FetchEntryListener {

    @Override
    public void setData(Entry entry) {

    }

}
