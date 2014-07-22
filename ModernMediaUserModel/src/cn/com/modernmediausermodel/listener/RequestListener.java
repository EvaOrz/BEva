package cn.com.modernmediausermodel.listener;

import cn.com.modernmediaslate.model.Entry;


public interface RequestListener {
	public void onSuccess(Entry entry);

	public void onFailed(Entry error);
}
